package com.strictlock.app.engine

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Telephony
import android.telecom.TelecomManager

class WhitelistManager(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private val telecomManager =
        context.getSystemService(Context.TELECOM_SERVICE) as? TelecomManager

    /**
     * Checks whether an app is protected under the Whitelist and must never be blocked.
     * Core Rule Requirement:
     * "Never block: the phone/dialer app, the messaging app, Settings, or StrictLock itself."
     */
    fun isWhitelisted(packageName: String?): Boolean {
        if (packageName.isNullOrBlank()) return true

        // 1. StrictLock app itself
        if (packageName == context.packageName) return true

        // 2. Settings packages
        if (isSettingsPackage(packageName)) return true

        // 3. Default Phone / Dialer app
        if (isDialerPackage(packageName)) return true

        // 4. Default Messaging / SMS app
        if (isMessagingPackage(packageName)) return true

        // 5. System launchers, keyboards, and Android SystemUI
        if (isSystemCriticalPackage(packageName)) return true

        return false
    }

    private fun isSettingsPackage(packageName: String): Boolean {
        if (packageName == "com.android.settings" ||
            packageName == "com.android.settings.intelligence" ||
            packageName.endsWith(".settings")
        ) {
            return true
        }
        return false
    }

    private fun isDialerPackage(packageName: String): Boolean {
        // Query default dialer from TelecomManager
        val defaultDialer = try {
            telecomManager?.defaultDialerPackage
        } catch (_: Exception) {
            null
        }
        if (packageName == defaultDialer) return true

        // Check if package handles ACTION_DIAL
        val dialIntent = Intent(Intent.ACTION_DIAL)
        val resolveInfo = packageManager.resolveActivity(dialIntent, 0)
        if (resolveInfo?.activityInfo?.packageName == packageName) return true

        return packageName.contains("dialer", ignoreCase = true)
    }

    private fun isMessagingPackage(packageName: String): Boolean {
        // Query default SMS from Telephony
        val defaultSms = try {
            Telephony.Sms.getDefaultSmsPackage(context)
        } catch (_: Exception) {
            null
        }
        if (packageName == defaultSms) return true

        // Check if package handles smsto
        val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"))
        val resolveInfo = packageManager.resolveActivity(smsIntent, 0)
        if (resolveInfo?.activityInfo?.packageName == packageName) return true

        return packageName.contains("messaging", ignoreCase = true) ||
                packageName.contains("mms", ignoreCase = true)
    }

    private fun isSystemCriticalPackage(packageName: String): Boolean {
        // Android Core & System UI
        if (packageName == "android" ||
            packageName == "com.android.systemui" ||
            packageName == "com.google.android.gms" ||
            packageName == "com.google.android.gsf"
        ) return true

        // Check against all registered launchers
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val homeActivities = try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    homeIntent,
                    PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.queryIntentActivities(homeIntent, 0)
            }
        } catch (_: Exception) {
            emptyList()
        }
        for (info in homeActivities) {
            if (info.activityInfo?.packageName == packageName) return true
        }

        // Generic fallback for OEM launchers (Realme, Oppo, ColorOS, OnePlus, Xiaomi, Samsung, Pixel, etc.)
        if (packageName.contains("launcher", ignoreCase = true) ||
            packageName.contains("quickstep", ignoreCase = true) ||
            packageName == "com.oppo.launcher" ||
            packageName == "com.coloros.launcher" ||
            packageName == "com.oneplus.launcher" ||
            packageName == "com.android.launcher"
        ) {
            return true
        }

        return false
    }
}
