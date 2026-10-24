package com.strictlock.app.service

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings

class OverlayManager(private val context: Context) {

    private val mainHandler = Handler(Looper.getMainLooper())

    fun showTimesUpOverlay(appName: String, packageName: String = "", isTamperBlock: Boolean = false) {
        mainHandler.post {
            try {
                OverlayActivity.show(
                    context = context,
                    appName = appName,
                    packageName = packageName,
                    isStrictBlock = true,
                    isTamperBlock = isTamperBlock
                )
            } catch (_: Exception) {
                fallbackSendHome()
            }
        }
    }

    fun showMindfulPauseOverlay(appName: String, packageName: String) {
        mainHandler.post {
            try {
                OverlayActivity.show(
                    context = context,
                    appName = appName,
                    packageName = packageName,
                    isStrictBlock = false,
                    isTamperBlock = false
                )
            } catch (_: Exception) {}
        }
    }

    fun dismissOverlay() {
        // OverlayActivity manages its own lifecycle and dismiss actions
    }

    private fun fallbackSendHome() {
        AppBlockerService.latestForegroundPackage = null
        val handledByAccessibility = StrictLockAccessibilityService.goHome()
        if (!handledByAccessibility) {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            try {
                context.startActivity(homeIntent)
            } catch (_: Exception) {}
        }
    }
}
