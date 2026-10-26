package com.strictlock.app.ui.viewmodel

import android.app.AppOpsManager
import android.app.Application
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.Process
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.strictlock.app.data.db.StrictLockDatabase
import com.strictlock.app.data.model.RuleEntity
import com.strictlock.app.data.model.RuleMode
import com.strictlock.app.data.repository.AppInfo
import com.strictlock.app.data.repository.InstalledAppsRepository
import com.strictlock.app.data.repository.RegainPreferences
import com.strictlock.app.data.repository.RegainSettings
import com.strictlock.app.data.repository.RuleRepository
import com.strictlock.app.data.repository.UnlockResult
import com.strictlock.app.engine.AppUsageStat
import com.strictlock.app.engine.HourlyUsage
import com.strictlock.app.engine.TamperDetector
import com.strictlock.app.engine.UsageCalculator
import com.strictlock.app.service.AppBlockerService
import com.strictlock.app.service.StrictLockAccessibilityService
import com.strictlock.app.service.StrictLockDeviceAdminReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ActiveRuleItem(
    val rule: RuleEntity,
    val usedMinutesToday: Long,
    val limitMinutes: Long,
    val daysLeft: Int,
    val icon: Drawable? = null,
    val isBlocked: Boolean = false,
    val casualRemainingMs: Long? = null
)

data class PermissionsData(
    val hasUsageAccess: Boolean = false,
    val hasOverlayPermission: Boolean = false,
    val hasAccessibilityService: Boolean = false,
    val hasDeviceAdmin: Boolean = false,
    val hasBatteryUnrestricted: Boolean = false,
    val hasNotificationPermission: Boolean = false
) {
    // The 5 essential subsystem accesses required before entering the app
    // Device Admin is OPTIONAL per user requirement
    val requiredGranted: Boolean
        get() = hasUsageAccess && hasOverlayPermission && hasAccessibilityService &&
                hasBatteryUnrestricted && hasNotificationPermission

    val allGranted: Boolean
        get() = requiredGranted && hasDeviceAdmin

    val remainingRequiredCount: Int
        get() {
            var count = 0
            if (!hasUsageAccess) count++
            if (!hasOverlayPermission) count++
            if (!hasAccessibilityService) count++
            if (!hasBatteryUnrestricted) count++
            if (!hasNotificationPermission) count++
            return count
        }
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = StrictLockDatabase.getInstance(application)
    private val ruleRepository = RuleRepository(db.ruleDao())
    private val appsRepository = InstalledAppsRepository(application)
    private val usageCalculator = UsageCalculator(application)
    private val tamperDetector = TamperDetector(application)
    private val regainPreferences = RegainPreferences.getInstance(application)

    // Regain Settings
    val regainSettings: StateFlow<RegainSettings> = regainPreferences.settingsFlow

    // Apps & Permissions
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps

    private val _permissions = MutableStateFlow(PermissionsData())
    val permissions: StateFlow<PermissionsData> = _permissions

    // Regain Dashboard Metrics
    private val _totalScreenTimeTodayMs = MutableStateFlow(0L)
    val totalScreenTimeTodayMs: StateFlow<Long> = _totalScreenTimeTodayMs.asStateFlow()

    private val _timeSavedTodayMs = MutableStateFlow(0L)
    val timeSavedTodayMs: StateFlow<Long> = _timeSavedTodayMs.asStateFlow()

    private val _devicePickups = MutableStateFlow(0)
    val devicePickups: StateFlow<Int> = _devicePickups.asStateFlow()

    private val _topApps = MutableStateFlow<List<AppUsageStat>>(emptyList())
    val topApps: StateFlow<List<AppUsageStat>> = _topApps.asStateFlow()

    private val _hourlyUsage = MutableStateFlow<List<HourlyUsage>>(emptyList())
    val hourlyUsage: StateFlow<List<HourlyUsage>> = _hourlyUsage.asStateFlow()

    // Focus Timer State
    private val _focusSecondsRemaining = MutableStateFlow(25 * 60)
    val focusSecondsRemaining: StateFlow<Int> = _focusSecondsRemaining.asStateFlow()

    private val _isFocusTimerRunning = MutableStateFlow(false)
    val isFocusTimerRunning: StateFlow<Boolean> = _isFocusTimerRunning.asStateFlow()

    private val _focusSessionTotalSeconds = MutableStateFlow(25 * 60)
    val focusSessionTotalSeconds: StateFlow<Int> = _focusSessionTotalSeconds.asStateFlow()

    private var focusTimerJob: Job? = null

    // Tamper Status
    private val _isTampered = MutableStateFlow(false)
    val isTampered: StateFlow<Boolean> = _isTampered.asStateFlow()

    // Emergency Reset State
    private val _emergencyTapCount = MutableStateFlow(0)
    val emergencyTapCount: StateFlow<Int> = _emergencyTapCount

    private val _showEmergencyDialog = MutableStateFlow(false)
    val showEmergencyDialog: StateFlow<Boolean> = _showEmergencyDialog

    private val tickerFlow = MutableStateFlow(System.currentTimeMillis())

    init {
        loadInstalledApps()
        refreshPermissions()
        refreshDashboardMetrics()

        // Adaptive ticker: 1-second ticks only during casual cooldowns, 10s otherwise to prevent UI lag
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                tickerFlow.value = System.currentTimeMillis()
                val hasActiveCooldown = ruleRepository.getActiveRules().any { it.isCasual && it.cooldownStartTime != null }
                delay(if (hasActiveCooldown) 1000L else 10_000L)
            }
        }

        // Periodic dashboard metrics refresher (every 15s)
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                refreshDashboardMetrics()
                _isTampered.value = tamperDetector.checkTampering()
                delay(15_000L)
            }
        }
    }

    fun refreshDashboardMetrics() {
        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            _totalScreenTimeTodayMs.value = usageCalculator.getTotalScreenTimeToday(now)
            _timeSavedTodayMs.value = usageCalculator.getEstimatedTimeSavedToday(now)
            _devicePickups.value = usageCalculator.getDeviceUnlocksToday(now)
            _topApps.value = usageCalculator.getTopAppUsagesToday(6, now)
            _hourlyUsage.value = usageCalculator.getHourlyUsageToday(now)
        }
    }

    // Regain Settings Toggles
    fun toggleBlockShorts() {
        regainPreferences.updateBlockShorts(!regainSettings.value.blockShortsEnabled)
    }

    fun toggleMindfulPause() {
        regainPreferences.updateMindfulPause(!regainSettings.value.mindfulPauseEnabled)
    }

    fun toggleUninstallProtection(context: Context? = null) {
        val current = regainSettings.value.uninstallProtectionEnabled
        val newTarget = !current
        if (!newTarget) {
            // User wants to disable Strict Mode: check if any apps are currently under a strict rule
            val hasStrictRules = activeRules.value.any { it.rule.isStrict }
            if (hasStrictRules) {
                // Cannot disable while rules are active
                return
            }
            regainPreferences.updateUninstallProtection(false)
            deactivateDeviceAdmin()
        } else {
            regainPreferences.updateUninstallProtection(true)
            if (context != null && !permissions.value.hasDeviceAdmin) {
                openDeviceAdminSettings(context)
            }
        }
    }

    fun deactivateDeviceAdmin() {
        try {
            val context = getApplication<Application>()
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
            val componentName = ComponentName(context, StrictLockDeviceAdminReceiver::class.java)
            if (dpm != null && dpm.isAdminActive(componentName)) {
                dpm.removeActiveAdmin(componentName)
            }
            refreshPermissions()
        } catch (_: Exception) {}
    }

    fun triggerAppUninstall(context: Context) {
        deactivateDeviceAdmin()
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            openApplicationDetailsSettings(context)
        }
    }

    fun toggleSplitScreenProtection() {
        regainPreferences.updateSplitScreenProtection(!regainSettings.value.splitScreenProtectionEnabled)
    }

    fun toggleFloatingWindowProtection() {
        regainPreferences.updateFloatingWindowProtection(!regainSettings.value.floatingWindowProtectionEnabled)
    }

    fun toggleNoGiveUp() {
        regainPreferences.updateNoGiveUp(!regainSettings.value.noGiveUpStrictLimitEnabled)
    }

    fun toggleFocusStrict() {
        regainPreferences.updateFocusStrict(!regainSettings.value.focusTimerStrictEnabled)
    }

    // Regain Focus Timer Controls (Allows 1 min up to 1440 min / 1 Day)
    fun setFocusSessionMinutes(minutes: Int) {
        if (_isFocusTimerRunning.value) return
        val clamped = minutes.coerceIn(1, 1440)
        val totalSecs = clamped * 60
        _focusSessionTotalSeconds.value = totalSecs
        _focusSecondsRemaining.value = totalSecs
        regainPreferences.updateFocusMinutes(clamped)
    }

    fun startFocusSession() {
        if (_isFocusTimerRunning.value) return
        _isFocusTimerRunning.value = true
        focusTimerJob?.cancel()
        focusTimerJob = viewModelScope.launch(Dispatchers.Default) {
            while (_isFocusTimerRunning.value && _focusSecondsRemaining.value > 0) {
                delay(1000L)
                _focusSecondsRemaining.value -= 1
            }
            if (_focusSecondsRemaining.value <= 0) {
                _isFocusTimerRunning.value = false
                _focusSecondsRemaining.value = _focusSessionTotalSeconds.value
            }
        }
    }

    fun pauseFocusSession() {
        _isFocusTimerRunning.value = false
        focusTimerJob?.cancel()
    }

    fun resetFocusSession() {
        _isFocusTimerRunning.value = false
        focusTimerJob?.cancel()
        _focusSecondsRemaining.value = _focusSessionTotalSeconds.value
    }

    // Combined Active Rules for StrictLock Dedicated Tab
    val activeRules: StateFlow<List<ActiveRuleItem>> = combine(
        ruleRepository.allActiveRules,
        tickerFlow
    ) { rules, now ->
        rules.map { rule ->
            val usedMs = usageCalculator.calculateTodayUsageMs(rule, now)
            val usedMins = usedMs / (1000 * 60)
            val daysLeft = rule.getRemainingCalendarDays(now)
            val isBlocked = usedMins >= rule.dailyLimitMinutes

            val cooldown = rule.cooldownStartTime
            val casualRemainingMs = if (rule.isCasual && cooldown != null) {
                val elapsed = now - cooldown
                val totalWait = 10 * 60 * 1000L
                if (elapsed < totalWait) totalWait - elapsed else 0L
            } else null

            ActiveRuleItem(
                rule = rule,
                usedMinutesToday = usedMins,
                limitMinutes = rule.dailyLimitMinutes,
                daysLeft = daysLeft,
                icon = null,
                isBlocked = isBlocked,
                casualRemainingMs = casualRemainingMs
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val hasStrictRules: StateFlow<Boolean> = activeRules.map { rules ->
        rules.any { it.rule.isStrict }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val strictAppsCount: StateFlow<Int> = activeRules.map { rules ->
        rules.count { it.rule.isStrict }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val isStrictModeLocked: StateFlow<Boolean> = combine(
        hasStrictRules,
        regainSettings
    ) { strictActive, settings ->
        strictActive || settings.uninstallProtectionEnabled
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private fun loadInstalledApps() {
        viewModelScope.launch {
            _installedApps.value = appsRepository.getInstalledLaunchableApps()
        }
    }

    fun refreshPermissions() {
        val context = getApplication<Application>()
        _permissions.value = PermissionsData(
            hasUsageAccess = checkUsageAccess(context),
            hasOverlayPermission = Settings.canDrawOverlays(context),
            hasAccessibilityService = isAccessibilityServiceEnabled(context),
            hasDeviceAdmin = isDeviceAdminActive(context),
            hasBatteryUnrestricted = isBatteryOptimizationsIgnored(context),
            hasNotificationPermission = checkNotificationPermission(context)
        )
    }

    fun createRule(
        packageName: String,
        appName: String,
        dailyLimitMinutes: Long,
        calendarDays: Int,
        mode: RuleMode
    ) {
        createRules(
            apps = listOf(AppInfo(packageName, appName, null)),
            dailyLimitMinutes = dailyLimitMinutes,
            calendarDays = calendarDays,
            mode = mode
        )
    }

    fun createRules(
        apps: List<AppInfo>,
        dailyLimitMinutes: Long,
        calendarDays: Int,
        mode: RuleMode
    ) {
        viewModelScope.launch {
            if (mode == RuleMode.STRICT) {
                regainPreferences.updateUninstallProtection(true)
            }
            for (app in apps) {
                ruleRepository.createRule(
                    packageName = app.packageName,
                    appName = app.appName,
                    limitMinutes = dailyLimitMinutes,
                    totalDays = calendarDays,
                    mode = mode
                )
            }
            AppBlockerService.instance?.checkImmediately()
            refreshDashboardMetrics()
        }
    }


    fun startCasualCooldown(ruleId: Long) {
        viewModelScope.launch {
            ruleRepository.startCasualCooldown(ruleId)
        }
    }

    suspend fun verifyAndUnlockCasualRule(ruleId: Long, inputCode: String): UnlockResult {
        val result = ruleRepository.verifyAndUnlockCasual(ruleId, inputCode)
        if (result is UnlockResult.Success) {
            AppBlockerService.instance?.checkImmediately()
            refreshDashboardMetrics()
        }
        return result
    }

    fun handleEmergencyTap() {
        _emergencyTapCount.value += 1
        if (_emergencyTapCount.value >= 7) {
            _emergencyTapCount.value = 0
            _showEmergencyDialog.value = true
        }
    }

    fun dismissEmergencyDialog() {
        _showEmergencyDialog.value = false
    }

    fun confirmEmergencyExit() {
        _showEmergencyDialog.value = false
        viewModelScope.launch {
            ruleRepository.emergencyTerminateAllRules()
            tamperDetector.clearTamperState()
            AppBlockerService.latestForegroundPackage = null
            refreshDashboardMetrics()
        }
    }

    fun openUsageAccessSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            openApplicationDetailsSettings(context)
        }
    }

    fun openOverlaySettings(context: Context) {
        try {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            openApplicationDetailsSettings(context)
        }
    }

    fun openAccessibilitySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            openApplicationDetailsSettings(context)
        }
    }

    fun openDeviceAdminSettings(context: Context) {
        try {
            val componentName = ComponentName(context, StrictLockDeviceAdminReceiver::class.java)
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName)
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "StrictLock requires Device Admin permission to enforce strict rules and prevent uninstallation."
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            openApplicationDetailsSettings(context)
        }
    }

    fun openBatteryOptimizationSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val fallbackIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (_: Exception) {
                openApplicationDetailsSettings(context)
            }
        }
    }

    fun openNotificationSettings(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } else {
                openApplicationDetailsSettings(context)
            }
        } catch (_: Exception) {
            openApplicationDetailsSettings(context)
        }
    }

    fun openApplicationDetailsSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    private fun checkUsageAccess(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager ?: return false
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val expectedComponentName = ComponentName(context, StrictLockAccessibilityService::class.java).flattenToString()
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.contains(expectedComponentName)
    }

    private fun isDeviceAdminActive(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager ?: return false
        val adminComponent = ComponentName(context, StrictLockDeviceAdminReceiver::class.java)
        return dpm.isAdminActive(adminComponent)
    }

    private fun isBatteryOptimizationsIgnored(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager ?: return false
        return pm.isIgnoringBatteryOptimizations(context.packageName)
    }

    private fun checkNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}
