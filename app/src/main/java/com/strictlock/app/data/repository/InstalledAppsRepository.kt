package com.strictlock.app.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InstalledAppsRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private val whitelistManager = com.strictlock.app.engine.WhitelistManager(context)

    suspend fun getInstalledLaunchableApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(intent, 0)
        }

        val apps = mutableListOf<AppInfo>()
        val seenPackages = mutableSetOf<String>()

        for (resolveInfo in resolveInfos) {
            val pkg = resolveInfo.activityInfo.packageName
            if (pkg == context.packageName) continue // Never include StrictLock
            if (whitelistManager.isWhitelisted(pkg)) continue // Never include whitelisted system apps
            if (seenPackages.contains(pkg)) continue
            seenPackages.add(pkg)

            val appName = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)
            val isSystem = (resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            apps.add(
                AppInfo(
                    packageName = pkg,
                    appName = appName,
                    icon = icon,
                    isSystemApp = isSystem
                )
            )
        }

        apps.sortedBy { it.appName.lowercase() }
    }

    fun getAppInfo(packageName: String): AppInfo? {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val label = packageManager.getApplicationLabel(appInfo).toString()
            val icon = packageManager.getApplicationIcon(appInfo)
            val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            AppInfo(packageName, label, icon, isSystem)
        } catch (_: Exception) {
            null
        }
    }
}
