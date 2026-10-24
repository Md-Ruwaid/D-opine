package com.strictlock.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RegainSettings(
    val blockShortsEnabled: Boolean = true,
    val mindfulPauseEnabled: Boolean = true,
    val uninstallProtectionEnabled: Boolean = true,
    val splitScreenProtectionEnabled: Boolean = true,
    val floatingWindowProtectionEnabled: Boolean = true,
    val noGiveUpStrictLimitEnabled: Boolean = true,
    val focusTimerStrictEnabled: Boolean = true,
    val focusSessionMinutes: Int = 25
)

class RegainPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("regain_app_preferences", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<RegainSettings> = _settingsFlow.asStateFlow()

    companion object {
        @Volatile
        private var instance: RegainPreferences? = null

        fun getInstance(context: Context): RegainPreferences {
            return instance ?: synchronized(this) {
                instance ?: RegainPreferences(context.applicationContext).also { instance = it }
            }
        }

        private const val KEY_BLOCK_SHORTS = "key_block_shorts"
        private const val KEY_MINDFUL_PAUSE = "key_mindful_pause"
        private const val KEY_UNINSTALL_PROTECTION = "key_uninstall_protection"
        private const val KEY_SPLIT_SCREEN_PROTECTION = "key_split_screen_protection"
        private const val KEY_FLOATING_WINDOW_PROTECTION = "key_floating_window_protection"
        private const val KEY_NO_GIVE_UP = "key_no_give_up"
        private const val KEY_FOCUS_STRICT = "key_focus_strict"
        private const val KEY_FOCUS_SESSION_MINUTES = "key_focus_session_minutes"
    }

    private fun loadSettings(): RegainSettings {
        return RegainSettings(
            blockShortsEnabled = prefs.getBoolean(KEY_BLOCK_SHORTS, true),
            mindfulPauseEnabled = prefs.getBoolean(KEY_MINDFUL_PAUSE, true),
            uninstallProtectionEnabled = prefs.getBoolean(KEY_UNINSTALL_PROTECTION, true),
            splitScreenProtectionEnabled = prefs.getBoolean(KEY_SPLIT_SCREEN_PROTECTION, true),
            floatingWindowProtectionEnabled = prefs.getBoolean(KEY_FLOATING_WINDOW_PROTECTION, true),
            noGiveUpStrictLimitEnabled = prefs.getBoolean(KEY_NO_GIVE_UP, true),
            focusTimerStrictEnabled = prefs.getBoolean(KEY_FOCUS_STRICT, true),
            focusSessionMinutes = prefs.getInt(KEY_FOCUS_SESSION_MINUTES, 25)
        )
    }

    fun updateBlockShorts(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BLOCK_SHORTS, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(blockShortsEnabled = enabled)
    }

    fun updateMindfulPause(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MINDFUL_PAUSE, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(mindfulPauseEnabled = enabled)
    }

    fun updateUninstallProtection(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_UNINSTALL_PROTECTION, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(uninstallProtectionEnabled = enabled)
    }

    fun updateSplitScreenProtection(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SPLIT_SCREEN_PROTECTION, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(splitScreenProtectionEnabled = enabled)
    }

    fun updateFloatingWindowProtection(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FLOATING_WINDOW_PROTECTION, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(floatingWindowProtectionEnabled = enabled)
    }

    fun updateNoGiveUp(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NO_GIVE_UP, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(noGiveUpStrictLimitEnabled = enabled)
    }

    fun updateFocusStrict(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FOCUS_STRICT, enabled).apply()
        _settingsFlow.value = _settingsFlow.value.copy(focusTimerStrictEnabled = enabled)
    }

    fun updateFocusMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_FOCUS_SESSION_MINUTES, minutes).apply()
        _settingsFlow.value = _settingsFlow.value.copy(focusSessionMinutes = minutes)
    }
}
