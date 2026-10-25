# StrictLock ADB Reset and Reinstall Script
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"

if (-not (Test-Path $adb)) {
    Write-Error "ADB not found at $adb"
    exit 1
}

Write-Host ">>> Checking for connected Android device..." -ForegroundColor Cyan
$devices = & $adb devices
Write-Host $devices

$deviceLine = ($devices -split "`n" | Where-Object { $_ -match "\bdevice\b" -and $_ -notmatch "List of" })

if (-not $deviceLine) {
    Write-Host "`n[!] No authorized device detected yet!" -ForegroundColor Yellow
    Write-Host "Please make sure:" -ForegroundColor Yellow
    Write-Host "  1. Developer Options is enabled (Settings > About Phone > Tap 'Build Number' 7 times)"
    Write-Host "  2. 'USB Debugging' is switched ON in Developer Options"
    Write-Host "  3. Unlock phone and tap 'Allow' on 'Allow USB debugging?' prompt"
    Write-Host "  4. (For OnePlus/Oppo) Enable 'Disable Permission Monitoring' at bottom of Developer Options"
    exit 2
}

Write-Host "`n>>> Found authorized device! Proceeding with full reset of StrictLock..." -ForegroundColor Green
$pkg = "com.strictlock.app"

Write-Host "1. Force stopping $pkg..." -ForegroundColor Gray
& $adb shell am force-stop $pkg

Write-Host "2. Deactivating Device Administrator..." -ForegroundColor Gray
& $adb shell dpm remove-active-admin $pkg/.service.StrictLockDeviceAdminReceiver 2>&1
& $adb shell dpm remove-active-admin "$pkg/com.strictlock.app.service.StrictLockDeviceAdminReceiver" 2>&1

Write-Host "3. Resetting accessibility and overlay services..." -ForegroundColor Gray
& $adb shell settings put secure enabled_accessibility_services "" 2>&1

Write-Host "4. Clearing app data and cache..." -ForegroundColor Gray
& $adb shell pm clear $pkg 2>&1

Write-Host "5. Resetting all permissions..." -ForegroundColor Gray
& $adb shell pm reset-permissions $pkg 2>&1

Write-Host "6. Uninstalling old package..." -ForegroundColor Gray
& $adb uninstall $pkg 2>&1

Write-Host "7. Installing fresh StrictLock APK..." -ForegroundColor Green
$apkPath = "$PSScriptRoot\StrictLock-Regain-v1.0.1.apk"
if (-not (Test-Path $apkPath)) {
    $apkPath = "$PSScriptRoot\app\build\outputs\apk\debug\app-debug.apk"
}

if (Test-Path $apkPath) {
    Write-Host "Installing: $apkPath" -ForegroundColor Cyan
    & $adb install -r -d "$apkPath"
    
    Write-Host "8. Pre-granting necessary permissions for smooth setup..." -ForegroundColor Cyan
    & $adb shell pm grant $pkg android.permission.POST_NOTIFICATIONS 2>&1
    & $adb shell appops set $pkg GET_USAGE_STATS allow 2>&1
    & $adb shell appops set $pkg SYSTEM_ALERT_WINDOW allow 2>&1
    & $adb shell dumpsys deviceidle whitelist +$pkg 2>&1
    
    Write-Host "`n[SUCCESS] StrictLock permissions reset and app installed successfully!" -ForegroundColor Green
} else {
    Write-Host "`n[!] No APK found to install. Build the project first using Android Studio or Gradle." -ForegroundColor Yellow
}
