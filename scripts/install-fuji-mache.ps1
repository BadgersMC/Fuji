# Bootstrap the independent Fuji Mache 26.4 Snapshot 8 artifact.
# Run this script manually from the experimental Fuji branch with Java 25 and Git.
[CmdletBinding()]
param(
    [string] $MacheDirectory = ''
)
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
if ([string]::IsNullOrWhiteSpace($MacheDirectory)) {
    $MacheDirectory = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..\Fuji-Mache'))
}

$repo = 'https://github.com/BadgersMC/Fuji-Mache.git'
$pinnedCommit = '8ad2b02dd5f0d4e698182383ef5f227e0f9747f2'
$version = '26.4-snapshot-8+build.local-SNAPSHOT'

if (-not (Test-Path -LiteralPath $MacheDirectory)) {
    & git clone --depth 1 $repo $MacheDirectory
    if ($LASTEXITCODE -ne 0) { throw 'Unable to clone Fuji-Mache' }
}
if (-not (Test-Path -LiteralPath (Join-Path $MacheDirectory 'gradlew.bat'))) {
    throw "Fuji-Mache Gradle wrapper is missing at $MacheDirectory"
}
$actualCommit = (& git -C $MacheDirectory rev-parse HEAD).Trim()
if ($LASTEXITCODE -ne 0 -or $actualCommit -ne $pinnedCommit) {
    throw "Fuji-Mache HEAD must equal $pinnedCommit (found $actualCommit). Preserve unrelated changes before switching revisions."
}
$modified = (& git -C $MacheDirectory status --porcelain)
if ($LASTEXITCODE -ne 0 -or $modified) {
    throw "Fuji-Mache has uncommitted source changes; refusing to build a non-reproducible artifact."
}
Push-Location $MacheDirectory
try {
    & '.\gradlew.bat' ':versions:v26_4-snapshot-8:publishToMavenLocal' --stacktrace --no-daemon
    if ($LASTEXITCODE -ne 0) { throw 'Fuji-Mache Maven-local publication failed' }
} finally {
    Pop-Location
}
$maven = Join-Path $env:USERPROFILE ('.m2\repository\io\papermc\mache\' + $version)
$pom = Join-Path $maven ('mache-' + $version + '.pom')
if (-not (Test-Path -LiteralPath $pom)) {
    throw "Expected Maven POM not found at $pom. Check generated artifact coordinates."
}
Write-Host "Fuji-Mache $version is installed at $maven"
