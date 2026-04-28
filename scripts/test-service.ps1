param(
    [Parameter(Mandatory = $false)]
    [ValidateSet(
        "oracle-db",
        "eureka-server",
        "config-server",
        "auth-service",
        "user-service",
        "notification-service",
        "api-gateway",
        "frontend",
        "all"
    )]
    [string]$Service = "all",

    [switch]$Build,
    [switch]$KeepRunning
)

$ErrorActionPreference = "Stop"

$serviceDefinitions = @{
    "oracle-db" = @{
        Dependencies = @()
        Kind = "tcp"
        Url = $null
        Host = "localhost"
        Port = 1521
    }
    "eureka-server" = @{
        Dependencies = @()
        Kind = "http"
        Url = "http://localhost:8761"
        Port = 8761
    }
    "config-server" = @{
        Dependencies = @("eureka-server")
        Kind = "tcp"
        Url = $null
        Host = "localhost"
        Port = 8888
    }
    "auth-service" = @{
        Dependencies = @("eureka-server", "config-server")
        Kind = "http"
        Url = "http://localhost:8082/auth/users"
        Port = 8082
    }
    "user-service" = @{
        Dependencies = @("eureka-server", "config-server")
        Kind = "http"
        Url = "http://localhost:8083/users"
        Port = 8083
    }
    "notification-service" = @{
        Dependencies = @("eureka-server")
        Kind = "http"
        Url = "http://localhost:8084/notifications"
        Port = 8084
    }
    "api-gateway" = @{
        Dependencies = @("eureka-server", "config-server", "auth-service", "user-service")
        Kind = "http"
        Url = "http://localhost:8081/auth/users"
        Port = 8081
    }
    "frontend" = @{
        Dependencies = @("eureka-server", "config-server", "auth-service", "user-service", "api-gateway")
        Kind = "http"
        Url = "http://localhost:4200"
        Port = 4200
    }
}

function Get-OrderedServices {
    param([string]$Target)

    if ($Target -eq "all") {
        return @(
            "eureka-server",
            "config-server",
            "auth-service",
            "user-service",
            "notification-service",
            "api-gateway",
            "frontend"
        )
    }

    $ordered = New-Object System.Collections.Generic.List[string]
    $seen = New-Object System.Collections.Generic.HashSet[string]

    function Add-Service([string]$Name) {
        if (-not $seen.Add($Name)) {
            return
        }

        foreach ($dependency in $serviceDefinitions[$Name].Dependencies) {
            Add-Service $dependency
        }

        $ordered.Add($Name)
    }

    Add-Service $Target
    return $ordered
}

function Start-ComposeServices {
    param([string[]]$Names)

    $args = @("compose", "up", "-d")
    if ($Build) {
        $args += "--build"
    }
    $args += $Names

    Write-Host ""
    Write-Host "Starting services: $($Names -join ', ')"
    & docker @args
    if ($LASTEXITCODE -ne 0) {
        throw "docker compose up failed."
    }
}

function Wait-ForHttp {
    param(
        [string]$Url,
        [int]$TimeoutSeconds = 180
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        try {
            $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 10
            if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 500) {
                return $response
            }
        } catch {
            Start-Sleep -Seconds 3
        }
    }

    throw "Timed out waiting for HTTP endpoint: $Url"
}

function Wait-ForTcp {
    param(
        [string]$Host,
        [int]$Port,
        [int]$TimeoutSeconds = 180
    )

    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        $client = $null
        try {
            $client = New-Object System.Net.Sockets.TcpClient
            $async = $client.BeginConnect($Host, $Port, $null, $null)
            if ($async.AsyncWaitHandle.WaitOne(3000, $false) -and $client.Connected) {
                $client.EndConnect($async)
                return
            }
        } catch {
        } finally {
            if ($client) {
                $client.Dispose()
            }
        }

        Start-Sleep -Seconds 3
    }

    throw "Timed out waiting for TCP port $Host`:$Port"
}

function Test-Service {
    param([string]$Name)

    $definition = $serviceDefinitions[$Name]

    Write-Host ""
    Write-Host "Testing $Name..."

    if ($definition.Kind -eq "http") {
        $response = Wait-ForHttp -Url $definition.Url
        Write-Host "PASS  $Name -> $($definition.Url) [$($response.StatusCode)]"
        return
    }

    Wait-ForTcp -Host $definition.Host -Port $definition.Port
    Write-Host "PASS  $Name -> TCP $($definition.Host):$($definition.Port)"
}

$servicesToStart = Get-OrderedServices -Target $Service

try {
    Start-ComposeServices -Names $servicesToStart

    if ($Service -eq "all") {
        foreach ($name in @(
            "eureka-server",
            "config-server",
            "auth-service",
            "user-service",
            "notification-service",
            "api-gateway",
            "frontend"
        )) {
            Test-Service -Name $name
        }
    } else {
        Test-Service -Name $Service
    }

    Write-Host ""
    Write-Host "All requested checks passed."
} finally {
    if (-not $KeepRunning) {
        Write-Host ""
        Write-Host "Stopping compose stack..."
        & docker compose down
    }
}
