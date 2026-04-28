# DB Workflow

To let `auth-service` update the Oracle schema from JPA entities, run it with:

```powershell
$env:SPRING_PROFILES_ACTIVE="docker"
$env:SPRING_DATASOURCE_URL="jdbc:oracle:thin:@//localhost:1521/XEPDB1"
$env:SPRING_DATASOURCE_USERNAME="APPUSER"
$env:SPRING_DATASOURCE_PASSWORD="appuser"
$env:SPRING_DATASOURCE_DRIVER_CLASS_NAME="oracle.jdbc.OracleDriver"
$env:SPRING_JPA_DATABASE_PLATFORM="org.hibernate.dialect.OracleDialect"
$env:SPRING_JPA_HIBERNATE_DDL_AUTO="update"
mvn -f BE/pom.xml -pl auth-service spring-boot:run
```

That makes Hibernate create or update tables in the Oracle schema used by `APPUSER`.

After the schema is updated, regenerate the dump tracked in Git with:

```powershell
powershell -ExecutionPolicy Bypass -File .\DB\export-db2.ps1
```

Default export settings for the old `APPUSER` workflow:

- Oracle user: `APPUSER`
- Connect string: `localhost:1521/XEPDB1`
- Dump file: `DB/DB2.DMP`
- Log file: `DB/export_pfe.log`

Current Docker import settings:

- Source dump schema: `MOHAMED`
- Docker target schema: `APPUSER`
- Docker import remap: `REMAP_SCHEMA=MOHAMED:APPUSER`

To refresh Docker from your local `MOHAMED` schema, export the dump from the
local database where your rows exist:

```powershell
expdp MOHAMED/chouikh@XE DIRECTORY=MON_DIR DUMPFILE=DB2.DMP LOGFILE=export_pfe.log SCHEMAS=MOHAMED
```

Then copy/replace that dump as `DB/DB2.DMP` in this project.

## App runtime database

The Dockerized backend services are configured to connect directly to the local
Oracle XE schema used in SQL Developer / Object Browser:

- JDBC URL: `jdbc:oracle:thin:@//host.docker.internal:1521/XE`
- Username: `MOHAMED`
- Password: `chouikh`

From a container, verify the app can see the same `APP_AUTH` rows with:

```powershell
$sql = "SELECT COUNT(*) AS APP_AUTH_COUNT FROM APP_AUTH;`nSELECT EMAIL, ROLE FROM APP_AUTH ORDER BY ID_AUTH;`nEXIT;`n"
$sql | docker exec -i auth-service sqlplus -s MOHAMED/chouikh@//host.docker.internal:1521/XE
```

Verify the login API with:

```powershell
Invoke-WebRequest -UseBasicParsing -Method Post `
  -Uri http://localhost:4200/api/auth/login `
  -ContentType 'application/json' `
  -Body '{"username":"098765","password":"1234"}'
```

The bundled Docker Oracle database is now optional and only starts when the
`docker-db` profile is enabled:

```powershell
docker compose --profile docker-db up -d oracle-db
```

## Docker import behavior

`docker-compose.yml` mounts `DB/DB2.DMP` into the Oracle container and runs
`DB/init-db2-import.sh` on every Oracle startup. The script stores the checksum
of the last successfully imported dump in the Oracle Docker volume. If
`DB/DB2.DMP` changes, Oracle imports it again before `auth-service` and
`user-service` are allowed to start.

After changing `DB/DB2.DMP`, run:

```powershell
docker compose up --build
```

If Oracle is already running, restart it so the startup import hook runs:

```powershell
docker compose restart oracle-db
docker compose up --build
```

To force a completely fresh database volume, use:

```powershell
docker compose down -v
docker compose up --build
```

If needed, you can override the defaults:

```powershell
powershell -ExecutionPolicy Bypass -File .\DB\export-db2.ps1 `
  -OracleUser APPUSER `
  -OraclePassword appuser `
  -ConnectString localhost:1521/XEPDB1 `
  -DirectoryName MON_EXPORT `
  -DumpFile DB2.DMP `
  -LogFile export_pfe.log `
  -Schema APPUSER
```
