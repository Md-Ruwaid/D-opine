package com.strictlock.app.service

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.strictlock.app.data.db.StrictLockDatabase
import com.strictlock.app.data.repository.RegainPreferences
import com.strictlock.app.data.repository.RuleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class StrictLockAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var repository: RuleRepository
    private lateinit var regainPreferences: RegainPreferences
    private val mainHandler = Handler(Looper.getMainLooper())
    private var lastToastTime = 0L
    private var lastReelsBlockTime = 0L

    companion object {
        @Volatile
        var instance: StrictLockAccessibilityService? = null

        fun goHome(): Boolean {
            return instance?.performGlobalAction(GLOBAL_ACTION_HOME) ?: false
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        val db = StrictLockDatabase.getInstance(applicationContext)
        repository = RuleRepository(db.ruleDao())
        regainPreferences = RegainPreferences.getInstance(applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val packageName = event.packageName?.toString() ?: return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            AppBlockerService.updateForegroundPackage(packageName)
            AppBlockerService.instance?.checkImmediately()
        }

        // 1. Regain Strict Mode Settings Protection
        if (isSettingsPackage(packageName)) {
            checkAndPreventSettingsTampering(event)
        }

        // 2. Regain Shorts & Reels Protection
        if (regainPreferences.settingsFlow.value.blockShortsEnabled) {
            checkAndBlockShortsOrReels(packageName)
        }
    }

    override fun onInterrupt() {
        // Required callback
    }

    private fun isSettingsPackage(packageName: String): Boolean {
        return packageName == "com.android.settings" ||
                packageName == "com.android.settings.intelligence" ||
                packageName == "com.google.android.permissioncontroller" ||
                packageName.contains("settings", ignoreCase = true) ||
                packageName.contains("packageinstaller", ignoreCase = true)
    }

    private fun checkAndPreventSettingsTampering(event: AccessibilityEvent) {
        serviceScope.launch {
            val hasStrictRules = repository.hasActiveStrictRules()
            val uninstallProtection = regainPreferences.settingsFlow.value.uninstallProtectionEnabled

            if (!hasStrictRules && !uninstallProtection) return@launch

            val rootNode = rootInActiveWindow ?: return@launch

            // Check if StrictLock or its package is on screen
            val mentionsStrictLock = scanTreeForText(rootNode, "StrictLock", "com.strictlock.app", "regain")
            if (!mentionsStrictLock) return@launch

            // CRITICAL: NEVER block installation or update screens!
            val isInstallOrUpdate = scanTreeForText(rootNode, "Do you want to install", "Do you want to update", "Install", "Update")
            if (isInstallOrUpdate) return@launch

            // Check if user is on uninstallation, force-stop, or data deletion confirmation prompts
            val isUninstallPrompt = scanTreeForText(rootNode, "Do you want to uninstall", "Uninstall this app", "uninstall")
            val isForceStopPrompt = scanTreeForText(rootNode, "If you force stop an app", "Force stop?", "force stop")
            val isClearDataPrompt = scanTreeForText(rootNode, "Delete app data?", "Clear all data", "clear data")
            val isDeactivateAdmin = scanTreeForText(rootNode, "Deactivate this device admin", "Deactivate and uninstall", "deactivate")

            if (isUninstallPrompt || isForceStopPrompt || isClearDataPrompt || isDeactivateAdmin) {
                blockSettingsAccess()
            }
        }
    }

    private fun checkAndBlockShortsOrReels(packageName: String) {
        val now = System.currentTimeMillis()
        if (now - lastReelsBlockTime < 1500L) return

        if (packageName == "com.google.android.youtube" || packageName == "com.instagram.android") {
            val rootNode = rootInActiveWindow ?: return
            val isShorts = scanTreeForReelsOrShorts(rootNode, packageName)
            if (isShorts) {
                lastReelsBlockTime = now
                performGlobalAction(GLOBAL_ACTION_BACK)
                mainHandler.post {
                    Toast.makeText(
                        applicationContext,
                        "Shorts & Reels blocked by Regain",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun scanTreeForReelsOrShorts(node: AccessibilityNodeInfo?, packageName: String): Boolean {
        if (node == null) return false

        val viewId = node.viewIdResourceName?.lowercase() ?: ""
        val text = node.text?.toString()?.lowercase() ?: ""
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""

        if (packageName == "com.google.android.youtube") {
            if (viewId.contains("shorts_container") || viewId.contains("reel_player_overlay") ||
                desc.contains("shorts") && node.isSelected) {
                return true
            }
        } else if (packageName == "com.instagram.android") {
            if (viewId.contains("clips_video_container") || viewId.contains("layout_comment_thread_edittext") ||
                desc.contains("reels") && node.isSelected) {
                return true
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (scanTreeForReelsOrShorts(child, packageName)) {
                return true
            }
        }
        return false
    }

    private fun scanTreeForText(node: AccessibilityNodeInfo?, vararg keywords: String): Boolean {
        if (node == null) return false

        val text = node.text?.toString()?.lowercase() ?: ""
        val desc = node.contentDescription?.toString()?.lowercase() ?: ""

        for (kw in keywords) {
            val lowerKw = kw.lowercase()
            if (text.contains(lowerKw) || desc.contains(lowerKw)) {
                return true
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (scanTreeForText(child, *keywords)) {
                return true
            }
        }
        return false
    }

    private fun blockSettingsAccess() {
        val handled = performGlobalAction(GLOBAL_ACTION_BACK)
        if (!handled) {
            performGlobalAction(GLOBAL_ACTION_HOME)
        }

        val now = System.currentTimeMillis()
        if (now - lastToastTime > 3000L) {
            lastToastTime = now
            mainHandler.post {
                Toast.makeText(
                    applicationContext,
                    "StrictLock: Deactivation blocked by Strict Mode Protection",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
