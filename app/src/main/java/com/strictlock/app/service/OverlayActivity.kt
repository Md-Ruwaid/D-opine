package com.strictlock.app.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.strictlock.app.ui.components.AppIconImage
import com.strictlock.app.ui.haptics.NeuroHapticEngine
import com.strictlock.app.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar

class OverlayActivity : ComponentActivity() {

    companion object {
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_IS_STRICT_BLOCK = "extra_is_strict_block"
        const val EXTRA_IS_TAMPER_BLOCK = "extra_is_tamper_block"

        fun show(
            context: Context,
            appName: String,
            packageName: String,
            isStrictBlock: Boolean,
            isTamperBlock: Boolean = false
        ) {
            val intent = Intent(context, OverlayActivity::class.java).apply {
                putExtra(EXTRA_APP_NAME, appName)
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                putExtra(EXTRA_IS_STRICT_BLOCK, isStrictBlock)
                putExtra(EXTRA_IS_TAMPER_BLOCK, isTamperBlock)
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: "This Application"
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        val isStrictBlock = intent.getBooleanExtra(EXTRA_IS_STRICT_BLOCK, true)
        val isTamperBlock = intent.getBooleanExtra(EXTRA_IS_TAMPER_BLOCK, false)

        setContent {
            StrictLockTheme {
                MindfulOverlayContent(
                    appName = appName,
                    packageName = packageName,
                    isStrictBlock = isStrictBlock,
                    isTamperBlock = isTamperBlock,
                    onExitToHome = { exitToHome() },
                    onProceed = { finish() }
                )
            }
        }
    }

    private fun exitToHome() {
        AppBlockerService.latestForegroundPackage = null
        val handled = StrictLockAccessibilityService.goHome()
        if (!handled) {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            try {
                startActivity(homeIntent)
            } catch (_: Exception) {}
        }
        finish()
    }
}

/**
 * Pattern Interrupt & Vagus Nerve Reset Overlay
 * Features concentric expanding rings in optical center (~42% from top),
 * calm non-judgmental reframing typography, tabular time until midnight, and a 56dp thumb CTA.
 */
@Composable
fun MindfulOverlayContent(
    appName: String,
    packageName: String,
    isStrictBlock: Boolean,
    isTamperBlock: Boolean,
    onExitToHome: () -> Unit,
    onProceed: () -> Unit
) {
    if (isStrictBlock || isTamperBlock) {
        BackHandler {
            onExitToHome()
        }
    }

    var countdown by remember { mutableIntStateOf(5) }
    var secondsRemaining by remember {
        val now = Calendar.getInstance()
        val midnight = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val diffSecs = ((midnight.timeInMillis - now.timeInMillis) / 1000L).coerceAtLeast(0L)
        mutableLongStateOf(diffSecs)
    }

    // Mindful countdown timer
    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown -= 1
        }
    }

    // Midnight countdown ticker
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            val now = Calendar.getInstance()
            val midnight = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            secondsRemaining = ((midnight.timeInMillis - now.timeInMillis) / 1000L).coerceAtLeast(0L)
        }
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (isStrictBlock || isTamperBlock) {
            NeuroHapticEngine.heavyVaultLock(context)
        }
    }

    val hrs = secondsRemaining / 3600
    val mins = (secondsRemaining % 3600) / 60
    val secs = secondsRemaining % 60
    val formattedTimeRemaining = String.format("%02dh %02dm %02ds until reset", hrs, mins, secs)

    // Bio-Rhythmic Vagus Nerve Synchronizer: Apple Watch Mindfulness Breathe Easing
    val infiniteTransition = rememberInfiniteTransition(label = "apple_mindfulness_breathe")
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe_scale"
    )

    val breatheRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 45f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe_rotation"
    )

    // Breathing phase detection & somatosensory haptic pulses
    val currentPhase = when {
        breatheScale < 0.60f -> "Inhale"
        breatheScale > 0.90f -> "Hold"
        else -> "Exhale"
    }

    val breathingPhaseText = when (currentPhase) {
        "Inhale" -> "Inhale slowly..."
        "Hold" -> "Hold..."
        else -> "Release the urge..."
    }

    var lastHapticPhase by remember { mutableStateOf("") }
    LaunchedEffect(currentPhase) {
        if (currentPhase != lastHapticPhase && (currentPhase == "Inhale" || currentPhase == "Hold")) {
            lastHapticPhase = currentPhase
            NeuroHapticEngine.breathingPulse(context, currentPhase)
        }
    }

    val primaryAccent = if (isTamperBlock || isStrictBlock) ControlledCrimson else ParasympatheticSage

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Time Remaining Until Midnight Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(ElevatedSlate)
                    .border(1.dp, SubtleHairline, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = primaryAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = formattedTimeRemaining,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlabasterText,
                        style = TabularNumberStyle,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // App Icon
            if (packageName.isNotBlank()) {
                AppIconImage(
                    packageName = packageName,
                    appName = appName,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Text(
                text = appName,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AlabasterText
            )

            // Push breathing orb to Optical Center (~42% from top of screen)
            Spacer(modifier = Modifier.weight(0.4f))

            // =========================================================================
            // APPLE WATCH MINDFULNESS STYLE OVERLAPPING TRANSLUCENT PETALS (Canvas)
            // =========================================================================
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = breatheRotation
                        }
                ) {
                    val centerOffset = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
                    val baseRadius = (size.minDimension / 4.2f) * breatheScale

                    // Draw 6 concentric overlapping translucent petals (Apple Watch style)
                    for (i in 0 until 6) {
                        val angleRad = Math.toRadians((i * 60.0)).toFloat()
                        val petalCenter = androidx.compose.ui.geometry.Offset(
                            x = centerOffset.x + (baseRadius * 0.7f * kotlin.math.cos(angleRad)),
                            y = centerOffset.y + (baseRadius * 0.7f * kotlin.math.sin(angleRad))
                        )
                        drawCircle(
                            color = primaryAccent.copy(alpha = 0.18f),
                            radius = baseRadius,
                            center = petalCenter
                        )
                    }

                    // Central core aura
                    drawCircle(
                        color = primaryAccent.copy(alpha = 0.35f),
                        radius = baseRadius * 0.85f,
                        center = centerOffset
                    )
                }

                // Inner core icon badge
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(primaryAccent.copy(alpha = 0.35f))
                        .border(1.5.dp, primaryAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isStrictBlock || isTamperBlock) Icons.Default.Lock else Icons.Default.Spa,
                        contentDescription = null,
                        tint = AlabasterText,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cognitive Reframing Copy
            if (isTamperBlock) {
                Text(
                    text = "Clock Tampering Intercepted",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ControlledCrimson
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "System time was skewed against monotonic hardware elapsed time. Apps remain locked under Strict Protection.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    color = MutedSlateText,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            } else if (isStrictBlock) {
                Text(
                    text = "Daily Allowance Reached",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ControlledCrimson
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Strict Mode Protection is active. Honor your commitment to yourself and return your attention to what matters.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    color = MutedSlateText,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            } else {
                androidx.compose.animation.AnimatedContent(
                    targetState = breathingPhaseText,
                    transitionSpec = {
                        androidx.compose.animation.fadeIn(androidx.compose.animation.core.tween(400))
                            .togetherWith(androidx.compose.animation.fadeOut(androidx.compose.animation.core.tween(400)))
                    },
                    label = "breathingPhaseTransition"
                ) { phaseText ->
                    Text(
                        text = phaseText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ParasympatheticSageLight
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (countdown > 0)
                        "Inhale... exhale... Wait $countdown seconds to check in with your intention."
                    else
                        "Do you truly need to enter $appName right now?",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center,
                    color = MutedSlateText,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
            }

            Spacer(modifier = Modifier.weight(0.6f))

            // =========================================================================
            // BOTTOM THUMB ZONE (Primary Return Home CTA: 56.dp height)
            // =========================================================================
            Button(
                onClick = onExitToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .iosTactilePress(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryAccent,
                    contentColor = DeepObsidian
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "RETURN TO HOME SCREEN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            // Optional Proceed for Mindful Pause (only when not strictly blocked)
            if (!isStrictBlock && !isTamperBlock) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onProceed,
                    enabled = countdown <= 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .iosTactilePress(enabled = countdown <= 0),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (countdown <= 0) SubtleHairline else Color.Transparent
                    )
                ) {
                    Text(
                        text = if (countdown > 0) "WAIT ($countdown)" else "CONTINUE WITH INTENTION",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (countdown <= 0) AlabasterText else DimSlateText,
                        style = TabularNumberStyle
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
