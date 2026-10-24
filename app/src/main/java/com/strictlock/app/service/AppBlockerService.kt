package com.strictlock.app.service

import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.strictlock.app.data.db.StrictLockDatabase
import com.strictlock.app.data.repository.RuleRepository
import com.strictlock.app.engine.NotificationHelper
import com.strictlock.app.engine.TamperDetector
import com.strictlock.app.engine.UsageCalculator
import com.strictlock.app.engine.WhitelistManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppBlockerService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var monitorJob: Job? = null

    private lateinit var repository: RuleRepository
    private lateinit var usageCalculator: UsageCalculator
    private lateinit var tamperDetector: TamperDetector
    private lateinit var whitelistManager: WhitelistManager
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var overlayManager: OverlayManager
    private var usageStatsManager: UsageStatsManager? = null

    companion object {
        @Volatile
        var instance: AppBlockerService? = null

        @Volatile
        var latestForegroundPackage: String? = null
        @Volatile
        var latestForegroundTimestamp: Long = 0L

        fun updateForegroundPackage(pkg: String?) {
            latestForegroundPackage = pkg
            latestForegroundTimestamp = System.currentTimeMillis()
        }

        fun start(context: Context) {
            val intent = Intent(context, AppBlockerService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    fun checkImmediately() {
        serviceScope.launch {
            try {
                checkForegroundAppAndEnforceRules()
            } catch (_: Exception) {}
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val db = StrictLockDatabase.getInstance(applicationContext)
        repository = RuleRepository(db.ruleDao())
        usageCalculator = UsageCalculator(applicationContext)
        tamperDetector = TamperDetector(applicationContext)
        whitelistManager = WhitelistManager(applicationContext)
        notificationHelper = NotificationHelper(applicationContext)
        overlayManager = OverlayManager(applicationContext)
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager

        startForeground(
            NotificationHelper.SERVICE_NOTIFICATION_ID,
            notificationHelper.buildForegroundNotification()
        )

        startMonitoringLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Sticky so Android restarts service if killed
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        monitorJob?.cancel()
        serviceScope.cancel()
        overlayManager.dismissOverlay()
    }

    private fun startMonitoringLoop() {
        monitorJob?.cancel()
        monitorJob = serviceScope.launch {
            while (isActive) {
                try {
                    checkForegroundAppAndEnforceRules()
                } catch (_: Exception) {}
                delay(1000L) // Checks about once per second as requested
            }
        }
    }

    private suspend fun checkForegroundAppAndEnforceRules() {
        // 1. Detect foreground package (Accessibility service + UsageStats fallback)
        val fgPackage = detectForegroundPackage() ?: return

        // 2. Never block whitelisted packages
        if (whitelistManager.isWhitelisted(fgPackage)) return

        // 3. Check for clock tampering
        val isTampered = tamperDetector.checkTampering()

        // 4. Check active rules for this package
        val rule = repository.getActiveRuleForPackage(fgPackage) ?: return

        // If rule has expired, unrestricted
        if (rule.isExpired()) return

        // If tampering detected and rule is strict, block immediately
        if (isTampered && rule.isStrict) {
            overlayManager.showTimesUpOverlay(rule.appName, rule.packageName, isTamperBlock = true)
            return
        }

        // 5. Calculate today's usage from system UsageStatsManager
        val usageMs = usageCalculator.calculateTodayUsageMs(rule)
        val usageMinutes = usageMs / (1000 * 60)
        val limitMinutes = rule.dailyLimitMinutes
        val remainingMinutes = limitMinutes - usageMinutes

        // 6. Check limit breach
        if (usageMinutes >= limitMinutes) {
            overlayManager.showTimesUpOverlay(rule.appName, rule.packageName, isTamperBlock = false)
            return
        }

        // 7. Send warnings at 10m and 2m left
        notificationHelper.checkAndSendLimitWarning(
            packageName = rule.packageName,
            appName = rule.appName,
            remainingMinutes = remainingMinutes
        )
    }

    private fun detectForegroundPackage(): String? {
        // First priority: instant report from AccessibilityService (valid if received within last 3 seconds)
        val cached = latestForegroundPackage
        val cachedTime = latestForegroundTimestamp
        val now = System.currentTimeMillis()
        if (!cached.isNullOrBlank() && (now - cachedTime) < 3000L) {
            return cached
        }

        // Second priority: query UsageEvents
        val usm = usageStatsManager ?: return null
        val events = try {
            usm.queryEvents(now - 10_000L, now)
        } catch (_: Exception) {
            return null
        }

        var foregroundApp: String? = null
        val event = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                foregroundApp = event.packageName
            }
        }
        return foregroundApp
    }
}
