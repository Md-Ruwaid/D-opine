package com.strictlock.app.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.strictlock.app.ui.haptics.NeuroHapticEngine

/**
 * Apple-Grade Hooke's Law Spring Physics & Kinetic Motion Tokens.
 * Optimized for natural somatic responsiveness, zero layout jitter, and high frame rates (60–120Hz).
 */
object MotionPhysics {

    /**
     * iOS Snappy Spring:
     * Damping: 0.82 (subtle organic settle bounce without being cartoonish)
     * Stiffness: 420.0 (fast, tactile response)
     * Use for: Buttons, interactive cards, chip selections, toggles.
     */
    val iosSnappySpring: SpringSpec<Float> = spring(
        dampingRatio = 0.82f,
        stiffness = 420f
    )

    /**
     * iOS Fluid Spring (Critically Damped):
     * Damping: 1.0 (zero overshoot, natural deceleration)
     * Stiffness: 280.0 (smooth, continuous velocity matching)
     * Use for: Screen transitions, progress bars, sheet slides, layout size changes.
     */
    val iosFluidSpring: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = 280f
    )

    /**
     * iOS Vault Spring (High-Tension Mechanical Snap):
     * Damping: 0.68 (firm mechanical lock-in sensation)
     * Stiffness: 500.0 (immediate high-energy response)
     * Use for: Locking shields, Strict Mode activation, deadbolt confirmations.
     */
    val iosVaultSpring: SpringSpec<Float> = spring(
        dampingRatio = 0.68f,
        stiffness = 500f
    )

    /**
     * Specialized Spring Specs for Dp and IntOffset
     */
    val iosSnappyDpSpring: SpringSpec<Dp> = spring(
        dampingRatio = 0.82f,
        stiffness = 420f
    )

    val iosFluidIntOffsetSpring: SpringSpec<IntOffset> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = 280f
    )
}

/**
 * Reusable Tactile Squish Modifier (Modifier.iosTactilePress).
 *
 * Simulates Apple-style physical button depression.
 * - Collects pressed state from [interactionSource].
 * - Animates scale between 1.0f and [scaleDown] (default 0.96f) using [MotionPhysics.iosSnappySpring].
 * - Executes strictly inside [graphicsLayer] so that ZERO Compose layout re-measurements occur.
 * - Triggers [onPressHaptic] (defaults to [NeuroHapticEngine.lightTick]) immediately on press down.
 */
fun Modifier.iosTactilePress(
    enabled: Boolean = true,
    scaleDown: Float = 0.96f,
    interactionSource: MutableInteractionSource? = null,
    onPressHaptic: (() -> Unit)? = null
): Modifier = composed {
    if (!enabled) return@composed this

    val context = LocalContext.current
    val internalInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by internalInteractionSource.collectIsPressedAsState()

    // Trigger crisp haptic tick immediately upon pressing down
    LaunchedEffect(isPressed) {
        if (isPressed) {
            if (onPressHaptic != null) {
                onPressHaptic()
            } else {
                NeuroHapticEngine.lightTick(context)
            }
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = MotionPhysics.iosSnappySpring,
        label = "iosTactilePressScale"
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
