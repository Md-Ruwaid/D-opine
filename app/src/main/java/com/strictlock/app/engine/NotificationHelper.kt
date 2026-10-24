package com.strictlock.app.engine

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import com.strictlock.app.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val prefs: SharedPreferences =
        context.getSharedPreferences("strictlock_notif_prefs", Context.MODE_PRIVATE)

    companion object {
        const val CHANNEL_SERVICE_ID = "strictlock_service_channel"
        const val CHANNEL_REMINDERS_ID = "strictlock_reminders_channel"
        const val SERVICE_NOTIFICATION_ID = 1001

        private const val NOTIF_ID_10M_BASE = 2000
        private const val NOTIF_ID_2M_BASE = 3000
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Foreground service channel
            val serviceChannel = NotificationChannel(
                CHANNEL_SERVICE_ID,
                "StrictLock Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors app usage and enforces time limits"
                setShowBadge(false)
            }

            // Reminders channel
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS_ID,
                "App Limit Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when an app has 10 or 2 minutes remaining"
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager.createNotificationChannel(serviceChannel)
            notificationManager.createNotificationChannel(remindersChannel)
        }
    }

    fun buildForegroundNotification(): Notification {
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_SERVICE_ID)
            .setContentTitle("StrictLock Active")
            .setContentText("Monitoring daily app usage limits")
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    fun checkAndSendLimitWarning(
        packageName: String,
        appName: String,
        remainingMinutes: Long
    ) {
        val todayKey = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())

        if (remainingMinutes in 3..10) {
            val key = "${packageName}_10m_$todayKey"
            if (!prefs.getBoolean(key, false)) {
                sendNotification(
                    id = NOTIF_ID_10M_BASE + (packageName.hashCode() and 0x7FFF),
                    title = "Time Warning: $appName",
                    message = "You have 10 minutes left for $appName today.",
                    priority = NotificationCompat.PRIORITY_DEFAULT
                )
                prefs.edit().putBoolean(key, true).apply()
            }
        } else if (remainingMinutes in 1..2) {
            val key = "${packageName}_2m_$todayKey"
            if (!prefs.getBoolean(key, false)) {
                sendNotification(
                    id = NOTIF_ID_2M_BASE + (packageName.hashCode() and 0x7FFF),
                    title = "Time Almost Up: $appName",
                    message = "Only 2 minutes remaining for $appName today!",
                    priority = NotificationCompat.PRIORITY_HIGH
                )
                prefs.edit().putBoolean(key, true).apply()
            }
        }
    }

    private fun sendNotification(id: Int, title: String, message: String, priority: Int) {
        val launchIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(priority)
            .build()

        try {
            notificationManager.notify(id, notification)
        } catch (_: SecurityException) {
            // Notification permission might not yet be granted
        }
    }
}
