package com.strictlock.app.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
    tableName = "rules",
    indices = [Index(value = ["packageName"], unique = false)]
)
data class RuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val packageName: String,
    val appName: String,
    val dailyLimitMinutes: Long,
    val mode: String, // "STRICT" or "CASUAL"
    val startEpochMs: Long,
    val endEpochMs: Long,
    val totalDays: Int,
    val isActive: Boolean = true,
    val casualUnlockRequestedAt: Long? = null,
    val casualUnlockVerificationCode: String? = null
) {
    @get:androidx.room.Ignore
    val isStrict: Boolean get() = mode == RuleMode.STRICT.name
    @get:androidx.room.Ignore
    val isCasual: Boolean get() = mode == RuleMode.CASUAL.name

    @get:androidx.room.Ignore
    val cooldownStartTime: Long? get() = casualUnlockRequestedAt
    @get:androidx.room.Ignore
    val unlockCode: String? get() = casualUnlockVerificationCode

    /**
     * Checks if this rule is still within its active date range.
     * The rule expires when the last calendar day ends (endEpochMs).
     */
    fun isExpired(now: Long = System.currentTimeMillis()): Boolean {
        return now > endEpochMs
    }

    fun getRemainingCalendarDays(now: Long = System.currentTimeMillis()): Int {
        if (now > endEpochMs) return 0
        val startOfToday = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val diffMs = endEpochMs - startOfToday
        if (diffMs <= 0) return 0
        return (diffMs / (24 * 60 * 60 * 1000L)).toInt() + 1
    }
}
