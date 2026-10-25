package com.strictlock.app.engine

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemClock

class TamperDetector(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("strictlock_tamper_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LAST_WALL_TIME = "last_wall_time"
        private const val KEY_LAST_ELAPSED_TIME = "last_elapsed_time"
        private const val KEY_IS_TAMPERED = "is_tampered"

        // Allow up to 10 minutes backward jump tolerance for network NTP adjustments
        private const val BACKWARD_JUMP_TOLERANCE_MS = 10 * 60 * 1000L
        // Allow up to 4 hours forward skew tolerance before flagging manual jump to next day
        private const val FORWARD_TAMPER_JUMP_MS = 4 * 60 * 60 * 1000L
    }

    /**
     * Checks whether clock tampering is detected.
     * Compares wall-clock time vs SystemClock.elapsedRealtime().
     */
    @Synchronized
    fun checkTampering(): Boolean {
        if (prefs.getBoolean(KEY_IS_TAMPERED, false)) {
            return true
        }

        val nowWall = System.currentTimeMillis()
        val nowElapsed = SystemClock.elapsedRealtime()

        val lastWall = prefs.getLong(KEY_LAST_WALL_TIME, 0L)
        val lastElapsed = prefs.getLong(KEY_LAST_ELAPSED_TIME, 0L)

        if (lastWall == 0L || lastElapsed == 0L) {
            // First time initialization
            recordTimeSample(nowWall, nowElapsed)
            return false
        }

        // 1. Clock jumped backwards by more than 10 minutes
        if (nowWall < (lastWall - BACKWARD_JUMP_TOLERANCE_MS)) {
            setTampered(true)
            return true
        }

        // 2. Clock jumped forward artificially (e.g. user manually changed date forward by hours while phone was awake)
        if (nowElapsed >= lastElapsed) {
            val deltaElapsed = nowElapsed - lastElapsed
            val deltaWall = nowWall - lastWall

            // If wall clock moved forward by > 4 hours while hardware elapsed time moved by < 10 minutes
            if (deltaWall > FORWARD_TAMPER_JUMP_MS && deltaElapsed < (10 * 60 * 1000L)) {
                setTampered(true)
                return true
            }
        } else {
            // Device was rebooted (nowElapsed < lastElapsed)
            // Wall clock must not be older than lastWall by more than backward tolerance
            if (nowWall < (lastWall - BACKWARD_JUMP_TOLERANCE_MS)) {
                setTampered(true)
                return true
            }
        }

        // Update recorded time
        recordTimeSample(nowWall, nowElapsed)
        return false
    }

    @Synchronized
    fun isTampered(): Boolean {
        return prefs.getBoolean(KEY_IS_TAMPERED, false)
    }

    @Synchronized
    fun setTampered(tampered: Boolean) {
        prefs.edit().putBoolean(KEY_IS_TAMPERED, tampered).apply()
    }

    @Synchronized
    fun resetTamper() {
        val nowWall = System.currentTimeMillis()
        val nowElapsed = SystemClock.elapsedRealtime()
        prefs.edit()
            .putBoolean(KEY_IS_TAMPERED, false)
            .putLong(KEY_LAST_WALL_TIME, nowWall)
            .putLong(KEY_LAST_ELAPSED_TIME, nowElapsed)
            .apply()
    }

    @Synchronized
    fun clearTamperState() = resetTamper()

    private fun recordTimeSample(wallTime: Long, elapsedTime: Long) {
        prefs.edit()
            .putLong(KEY_LAST_WALL_TIME, wallTime)
            .putLong(KEY_LAST_ELAPSED_TIME, elapsedTime)
            .apply()
    }
}
