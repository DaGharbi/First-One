param(
    [string]$OracleUser = "APPUSER",
    [string]$OraclePassword = "appuser",
    [string]$ConnectString = "localhost:1521/XEPDB1",
    [string]$DirectoryName = "MON_EXPORT",
    [string]$DumpFile = "DB2.DMP",
    [string]$LogFile = "export_pfe.log",
    [string]$Schema = "APPUSER"
)

$ErrorActionPreference = "Stop"

$dbDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$dumpPath = Join-Path $dbDir $DumpFile
$logPath = Join-Path $dbDir $LogFile

Write-Host "Exporting Oracle schema '$Schema' to '$dumpPath'..."
Write-Host "Connection: $ConnectString"

Push-Location $dbDir
try {
    expdp "$OracleUser/$OraclePassword@$ConnectString" `
        DIRECTORY=$DirectoryName `
        DUMPFILE=$DumpFile `
        LOGFILE=$LogFile `
        SCHEMAS=$Schema `
        EXCLUDE=STATISTICS
}
finally {
    Pop-Location
}

if (-not (Test-Path $dumpPath)) {
    throw "Export finished without producing $dumpPath"
}

Write-Host "Dump updated:"
Get-Item $dumpPath | Select-Object FullName, Length, LastWriteTime
Write-Host "Log updated:"
Get-Item $logPath | Select-Object FullName, Length, LastWriteTime
