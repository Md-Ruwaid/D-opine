package com.strictlock.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.ui.theme.*
import com.strictlock.app.ui.viewmodel.MainViewModel

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.strictlock.app.ui.haptics.NeuroHapticEngine

@Composable
fun FocusScreen(viewModel: MainViewModel) {
    val remainingSecs by viewModel.focusSecondsRemaining.collectAsState()
    val totalSecs by viewModel.focusSessionTotalSeconds.collectAsState()
    val isRunning by viewModel.isFocusTimerRunning.collectAsState()
    val settings by viewModel.regainSettings.collectAsState()
    val context = LocalContext.current

    val minutes = remainingSecs / 60
    val seconds = remainingSecs % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val progress = (remainingSecs.toFloat() / totalSecs.coerceAtLeast(1)).coerceIn(0f, 1f)
    // 1. Liquid Arc Sweep (iosFluidSpring for critically damped smooth motion)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = MotionPhysics.iosFluidSpring,
        label = "focus_dial_progress"
    )

    // Visual De-Cluttering: When actively focusing, fade out secondary controls to 0.15f
    val secondaryAlpha by animateFloatAsState(
        targetValue = if (isRunning) 0.15f else 1.0f,
        animationSpec = MotionPhysics.iosFluidSpring,
        label = "secondary_alpha"
    )

    // 2. Ambient Focus Pulse: Soft breathing outer halo ring on a 4-second sine cycle
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_focus_halo")
    val haloStrokeWidth by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_stroke"
    )
    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Screen Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(secondaryAlpha)
        ) {
            Text(
                text = "Deep Focus",
                fontSize = 28.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.SemiBold,
                color = AlabasterText,
                letterSpacing = (-0.25).sp
            )
            Text(
                text = "Flow-state induction with strict anti-quit friction",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MutedSlateText
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // =========================================================================
        // TACTILE CIRCULAR DIAL (Dual-Layer Canvas Ring in Optical Center + Ambient Halo)
        // =========================================================================
        Box(
            modifier = Modifier.size(260.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidthPx = 12.dp.toPx()
                val diameter = size.minDimension - strokeWidthPx
                val topLeft = Offset(strokeWidthPx / 2f, strokeWidthPx / 2f)
                val arcSize = Size(diameter, diameter)

                // Layer 0: Ambient Halo Ring (Only when running)
                if (isRunning) {
                    drawArc(
                        color = ParasympatheticSage.copy(alpha = haloAlpha),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = haloStrokeWidth.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Layer 1: Background track (InteractiveSurface)
                drawArc(
                    color = InteractiveSurface,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidthPx)
                )

                // Layer 2: Liquid progress arc (ParasympatheticSage with StrokeCap.Round)
                drawArc(
                    color = ParasympatheticSage,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }

            // Tabular Central Countdown Timer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeFormatted,
                    fontSize = 48.sp,
                    lineHeight = 56.sp,
                    fontWeight = FontWeight.Light,
                    color = AlabasterText,
                    letterSpacing = (-0.5).sp,
                    style = TabularNumberStyle
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isRunning) "DEEP FOCUS • STRICT ANTI-QUIT" else "PAUSED • TAP TO START",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRunning) ParasympatheticSageLight else MutedSlateText,
                    letterSpacing = 1.2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // =========================================================================
        // BOTTOM THUMB ZONE (Habit Triggers & Duration Controls: Bottom 25%)
        // =========================================================================
        val currentFocusMins = (totalSecs / 60).coerceIn(1, 1440)
        val durationLabel = when {
            currentFocusMins == 1440 -> "1 Day (24 hrs)"
            currentFocusMins < 60 -> "$currentFocusMins min"
            currentFocusMins % 60 == 0 -> "${currentFocusMins / 60} hrs"
            else -> "${currentFocusMins / 60}h ${currentFocusMins % 60}m"
        }

        // Tactile Pill Preset Chips
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(secondaryAlpha)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SESSION DURATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MutedSlateText,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = durationLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParasympatheticSageLight,
                    style = TabularNumberStyle
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scientific Cycle Presets: 25m Pomodoro, 45m Deep Work, 90m Ultradian, 120m Block
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair(25, "25m Pomodoro"),
                    Pair(45, "45m Deep Work"),
                    Pair(90, "90m Ultradian"),
                    Pair(120, "2h Flow")
                ).forEach { (mins, label) ->
                    val isSelected = currentFocusMins == mins
                    val chipBg by androidx.compose.animation.animateColorAsState(
                        targetValue = if (isSelected) ParasympatheticSage.copy(alpha = 0.20f) else ElevatedSlate,
                        animationSpec = androidx.compose.animation.core.tween(200),
                        label = "chipBg"
                    )
                    val chipBorder by androidx.compose.animation.animateColorAsState(
                        targetValue = if (isSelected) ParasympatheticSage else SubtleHairline,
                        animationSpec = androidx.compose.animation.core.tween(200),
                        label = "chipBorder"
                    )
                    val chipScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.03f else 1.0f,
                        animationSpec = MotionPhysics.iosSnappySpring,
                        label = "chipScale"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                scaleX = chipScale
                                scaleY = chipScale
                            }
                            .clip(RoundedCornerShape(10.dp))
                            .background(chipBg)
                            .border(1.dp, chipBorder, RoundedCornerShape(10.dp))
                            .iosTactilePress(
                                enabled = !isRunning,
                                scaleDown = 0.94f,
                                onPressHaptic = {
                                    NeuroHapticEngine.lightTick(context)
                                }
                            )
                            .clickable(enabled = !isRunning) {
                                NeuroHapticEngine.lightTick(context)
                                viewModel.setFocusSessionMinutes(mins)
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) ParasympatheticSageLight else MutedSlateText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Smooth Slider for fine adjustment
            Slider(
                value = currentFocusMins.toFloat(),
                onValueChange = { newVal ->
                    if (!isRunning) {
                        viewModel.setFocusSessionMinutes(newVal.toInt())
                    }
                },
                enabled = !isRunning,
                valueRange = 1f..240f,
                colors = SliderDefaults.colors(
                    thumbColor = ParasympatheticSage,
                    activeTrackColor = ParasympatheticSage,
                    inactiveTrackColor = InteractiveSurface
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // PRIMARY ACTION BUTTON (Fitts's Law: 56.dp height in natural thumb zone)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset Button
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElevatedSlate)
                    .border(1.dp, SubtleHairline, RoundedCornerShape(16.dp))
                    .iosTactilePress(
                        scaleDown = 0.90f,
                        onPressHaptic = {
                            NeuroHapticEngine.lightTick(context)
                        }
                    )
                    .clickable {
                        NeuroHapticEngine.lightTick(context)
                        viewModel.resetFocusSession()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset timer",
                    tint = MutedSlateText,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Primary Start / Pause CTA (56dp min height)
            Button(
                onClick = {
                    if (isRunning) {
                        NeuroHapticEngine.lightTick(context)
                        viewModel.pauseFocusSession()
                    } else {
                        if (settings.focusTimerStrictEnabled) {
                            NeuroHapticEngine.heavyVaultLock(context)
                        } else {
                            NeuroHapticEngine.mediumImpact(context)
                        }
                        viewModel.startFocusSession()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .iosTactilePress(scaleDown = 0.96f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) InteractiveSurface else ParasympatheticSage,
                    contentColor = if (isRunning) AlabasterText else DeepObsidian
                ),
                border = if (isRunning) androidx.compose.foundation.BorderStroke(1.dp, SubtleHairline) else null
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isRunning) "PAUSE SESSION" else "BEGIN FOCUS SESSION",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Strict Anti-Quit Lock Policy Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(secondaryAlpha),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
            border = androidx.compose.foundation.BorderStroke(1.dp, SubtleHairline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(InteractiveSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = AuthorityIndigo,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Strict Anti-Quit Shield",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlabasterText
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Prevents quitting during an active deep work interval.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MutedSlateText
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = settings.focusTimerStrictEnabled,
                    onCheckedChange = { viewModel.toggleFocusStrict() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AuthorityIndigo,
                        checkedTrackColor = AuthorityIndigo.copy(alpha = 0.25f),
                        uncheckedThumbColor = DimSlateText,
                        uncheckedTrackColor = InteractiveSurface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
