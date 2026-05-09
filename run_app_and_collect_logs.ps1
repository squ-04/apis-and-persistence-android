# Script to launch the app and collect filtered logs (Room/SQLite/Runtime/Exception)
# Usage: Open PowerShell in project root and run:
#    powershell -ExecutionPolicy Bypass -File .\run_app_and_collect_logs.ps1

# Try to read sdk.dir from local.properties
$localProps = Join-Path $PSScriptRoot "local.properties"
if (-Not (Test-Path $localProps)) {
    Write-Error "local.properties not found in $PSScriptRoot. Please run this script from the project root."
    exit 1
}

$sdkDir = ""
Get-Content $localProps | ForEach-Object {
    if ($_ -match "^\s*sdk\.dir\s*=\s*(.+)\s*$") {
        $sdkDir = $matches[1].Trim()
    }
}

if ([string]::IsNullOrWhiteSpace($sdkDir)) {
    Write-Error "sdk.dir not found in local.properties. Please ensure local.properties contains sdk.dir=..."
    exit 1
}

# Normalize path separators and unescape properties-style escapes (e.g. C\:\\...)
# local.properties may contain escaped backslashes and escaped colon (e.g. C\:\\Users...)
$sdkDir = $sdkDir.Trim()
# Unescape escaped colon (\:) produced by properties file serialization
$sdkDir = $sdkDir -replace '\\:', ':'
# Replace doubled backslashes with single backslash
$sdkDir = $sdkDir -replace '\\\\', '\\'
# If there are remaining escaped backslashes like '\\', collapse them
$sdkDir = $sdkDir -replace '\\', '\\'
$adb = Join-Path $sdkDir "platform-tools\adb.exe"

if (-Not (Test-Path $adb)) {
    Write-Error "adb not found at $adb. Please ensure Android SDK platform-tools are installed."
    exit 1
}

Write-Output "Using adb: $adb"

# Clear logcat
& $adb logcat -c

# Start activity
Write-Output "Starting MainActivity..."
& $adb shell am start -n com.uniquindio.thecatapp/.MainActivity

# Wait a bit for app to start and any crash to occur
Start-Sleep -Seconds 5

# Collect filtered logs and open in notepad
$filter = "Room|SQLite|AndroidRuntime|FATAL|Exception"
& $adb logcat -d | Select-String -Pattern $filter -SimpleMatch | Out-File -Encoding UTF8 crash_filtered.txt

Write-Output "Filtered log saved to crash_filtered.txt"
notepad crash_filtered.txt

# Also print last 200 lines to console for quick inspection
Write-Output "--- Last 200 filtered lines ---"
Get-Content crash_filtered.txt | Select-Object -Last 200 | ForEach-Object { Write-Output $_ }

Write-Output "Done."

