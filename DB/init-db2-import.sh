#!/bin/bash
set -e

DPDUMP_DIR=/u01/app/oracle/admin/XE/dpdump
DUMP_FILE="${DPDUMP_DIR}/DB2.DMP"
STATE_DIR=/tmp
READY_FILE="${STATE_DIR}/.db2-dump-ready"
HASH_FILE="${STATE_DIR}/.db2-dump.sha256"
CURRENT_HASH_FILE="${STATE_DIR}/.db2-dump.current.sha256"

rm -f "${READY_FILE}"

if [ ! -f "${DUMP_FILE}" ]; then
  echo "DB2.DMP not found, skipping Oracle Data Pump import."
  touch "${READY_FILE}"
  exit 0
fi

sha256sum "${DUMP_FILE}" | awk '{print $1}' > "${CURRENT_HASH_FILE}"

if [ -f "${HASH_FILE}" ] && [ "$(cat "${CURRENT_HASH_FILE}")" = "$(cat "${HASH_FILE}")" ]; then
  echo "DB2.DMP has not changed since the last successful import, skipping Data Pump import."
  touch "${READY_FILE}"
  exit 0
fi

echo "DB2.DMP changed or was not imported before. Importing into XE with Data Pump..."
sqlplus -s system/oracle <<'SQL'
-- Create MOHAMED user and grant privileges
BEGIN
  EXECUTE IMMEDIATE 'CREATE USER MOHAMED IDENTIFIED BY chouikh';
  EXECUTE IMMEDIATE 'GRANT DBA, CONNECT, RESOURCE TO MOHAMED';
EXCEPTION 
  WHEN OTHERS THEN
    NULL; -- Ignore if user exists
END;
/
CREATE OR REPLACE DIRECTORY APP_DUMP_DIR AS '/u01/app/oracle/admin/XE/dpdump';

BEGIN
  FOR sequence_record IN (
    SELECT sequence_name
    FROM dba_sequences
    WHERE sequence_owner = 'MOHAMED'
  ) LOOP
    EXECUTE IMMEDIATE 'DROP SEQUENCE MOHAMED."' || sequence_record.sequence_name || '"';
  END LOOP;
END;
/
EXIT
SQL

# impdp may return non-zero even on partial success (e.g. trigger warnings)
set +e
impdp system/"${ORACLE_PASSWORD}"@XE \
  DIRECTORY=APP_DUMP_DIR \
  DUMPFILE=DB2.DMP \
  LOGFILE=DB2_import.log \
  SCHEMAS=MOHAMED \
  EXCLUDE=STATISTICS,USER \
  TABLE_EXISTS_ACTION=REPLACE
IMPDP_RC=$?
set -e

if [ $IMPDP_RC -eq 0 ]; then
  echo "SUCCESS: impdp completed without errors."
elif [ $IMPDP_RC -eq 1 ] || [ $IMPDP_RC -eq 5 ]; then
  echo "WARNING: impdp completed with warnings/errors (exit code $IMPDP_RC). Continuing..."
else
  echo "ERROR: impdp failed with exit code $IMPDP_RC"
  exit 1
fi

mv "${CURRENT_HASH_FILE}" "${HASH_FILE}"
touch "${READY_FILE}"
echo "DB2 import completed successfully."
