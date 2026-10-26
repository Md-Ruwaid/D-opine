package com.strictlock.app.ui.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * NeuroHapticEngine
 *
 * Somatosensory Haptic Feedback Engine for StrictLock (Regain Edition).
 * Simulates Apple Taptic Engine (UIImpactFeedbackGenerator & UINotificationFeedbackGenerator)
 * across Android API 26 to API 35+, targeting Meissner corpuscles and Pacinian corpuscles
 * to provide neuro-ergonomically distinct tactile cues without causing tactile fatigue.
 */
object NeuroHapticEngine {

    private fun getVibrator(context: Context): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 1. Light Tick (Meissner Corpuscle Stimulation)
     * Equivalent to Apple UIImpactFeedbackGenerator(.light).
     * Used for: Bottom navigation tab switches, duration chips, slider step ticks, press-down events.
     */
    fun lightTick(context: Context) {
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(8, 60))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(8)
            }
        } catch (_: Exception) {
            // Silently swallow device-specific vibration exceptions
        }
    }

    /**
     * 2. Medium Impact (Tactile Detent)
     * Equivalent to Apple UIImpactFeedbackGenerator(.medium).
     * Used for: Toggling a Block Shield ON/OFF, selecting/deselecting an app, focus timer setup.
     */
    fun mediumImpact(context: Context) {
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(14, 140))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(14)
            }
        } catch (_: Exception) {
        }
    }

    /**
     * 3. Heavy Vault Lock (Mechanical Deadbolt)
     * Equivalent to Apple UIImpactFeedbackGenerator(.heavy) + Double Thud.
     * Used for: Activating STRICT mode, locking in a multi-day rule, OverlayActivity lock engagement.
     */
    fun heavyVaultLock(context: Context) {
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Crisp initial catch (18ms @ 180) -> 45ms silence -> 28ms deadbolt impact @ 255
                val timings = longArrayOf(0, 18, 45, 28)
                val amplitudes = intArrayOf(0, 180, 0, 255)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 18, 45, 28), -1)
            }
        } catch (_: Exception) {
        }
    }

    /**
     * 4. Warning Reject (Anti-Tamper / Locked Toggle Shake)
     * Equivalent to Apple UINotificationFeedbackGenerator(.error).
     * Used for: Attempting to disable a locked toggle while Strict Mode is active, or invalid code input.
     */
    fun warningReject(context: Context) {
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Rapid double-stutter rejection waveform
                val timings = longArrayOf(0, 15, 40, 15, 40, 20)
                val amplitudes = intArrayOf(0, 200, 0, 200, 0, 140)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 15, 40, 15, 40, 20), -1)
            }
        } catch (_: Exception) {
        }
    }

    /**
     * 5. Escalating Override Tap (7-Tap Emergency Override)
     * Gradually escalates amplitude from tap 1 to tap 7 to provide tactile resistance.
     */
    fun escalatingTap(context: Context, tapCount: Int) {
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) return

        try {
            val clampedTap = tapCount.coerceIn(1, 7)
            val amplitude = (60 + (clampedTap * 25)).coerceIn(60, 255)
            val duration = (10 + (clampedTap * 3)).toLong()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        } catch (_: Exception) {
        }
    }

    /**
     * 6. Parasympathetic Breathing Pulse (Vagus Nerve Pacemaker)
     * Soft, low-amplitude pulse triggered at Inhale / Hold inflection points in OverlayActivity.
     */
    fun breathingPulse(context: Context, phase: String = "Inhale") {
        val vibrator = getVibrator(context) ?: return
        if (!vibrator.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val amplitude = if (phase == "Hold") 45 else 35
                vibrator.vibrate(VibrationEffect.createOneShot(25, amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20)
            }
        } catch (_: Exception) {
        }
    }
}
