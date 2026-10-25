package com.strictlock.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.ui.theme.RegainCardBorder
import com.strictlock.app.ui.theme.RegainDarkBg
import com.strictlock.app.ui.theme.RegainMint
import com.strictlock.app.ui.theme.RegainMintLight
import com.strictlock.app.ui.theme.RegainSurface
import com.strictlock.app.ui.theme.RegainSurfaceVariant
import com.strictlock.app.ui.theme.RegainTextMuted
import com.strictlock.app.ui.theme.RegainTextPrimary
import com.strictlock.app.ui.theme.RegainTextSecondary
import com.strictlock.app.ui.viewmodel.MainViewModel

@Composable
fun FocusScreen(viewModel: MainViewModel) {
    val remainingSecs by viewModel.focusSecondsRemaining.collectAsState()
    val totalSecs by viewModel.focusSessionTotalSeconds.collectAsState()
    val isRunning by viewModel.isFocusTimerRunning.collectAsState()
    val settings by viewModel.regainSettings.collectAsState()

    val minutes = remainingSecs / 60
    val seconds = remainingSecs % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val progress = (remainingSecs.toFloat() / totalSecs.coerceAtLeast(1)).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "focus_progress")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RegainDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title Header
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Focus Session",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RegainTextPrimary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Deep work timer with strict anti-quit protection",
                fontSize = 13.sp,
                color = RegainTextMuted
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Big Circular Timer Dial
        Box(
            modifier = Modifier.size(260.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background track
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.fillMaxSize(),
                color = RegainSurfaceVariant,
                strokeWidth = 14.dp,
                trackColor = RegainSurfaceVariant
            )

            // Dynamic progress
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxSize(),
                color = RegainMint,
                strokeWidth = 14.dp,
                strokeCap = StrokeCap.Round,
                trackColor = androidx.compose.ui.graphics.Color.Transparent
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeFormatted,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = RegainTextPrimary,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isRunning) "FOCUSING" else "PAUSED",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRunning) RegainMintLight else RegainTextMuted,
                    letterSpacing = 2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Focus Duration Slider (1 min to 1 Day / 1440m)
        val currentFocusMins = (totalSecs / 60).coerceIn(1, 1440)
        val durationLabel = when {
            currentFocusMins == 1440 -> "1 Day (24 hrs)"
            currentFocusMins < 60 -> "$currentFocusMins min"
            currentFocusMins % 60 == 0 -> "${currentFocusMins / 60} hrs"
            else -> "${currentFocusMins / 60}h ${currentFocusMins % 60}m"
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SESSION LENGTH",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = RegainMintLight,
                letterSpacing = 1.sp
            )
            Text(
                text = durationLabel,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RegainMint
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Slider(
            value = currentFocusMins.toFloat(),
            onValueChange = { newVal ->
                if (!isRunning) {
                    viewModel.setFocusSessionMinutes(newVal.toInt())
                }
            },
            enabled = !isRunning,
            valueRange = 1f..1440f,
            colors = SliderDefaults.colors(
                thumbColor = RegainMint,
                activeTrackColor = RegainMint,
                inactiveTrackColor = RegainSurfaceVariant
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "1 min", fontSize = 11.sp, color = RegainTextMuted)
            Text(text = "1 Day (1440m)", fontSize = 11.sp, color = RegainTextMuted)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Preset Chips for Focus Session
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(1, 15, 25, 45, 60, 120, 240, 1440).forEach { mins ->
                val isSelected = currentFocusMins == mins
                val chipText = when (mins) {
                    1440 -> "1d"
                    in 60..1439 -> "${mins / 60}h"
                    else -> "${mins}m"
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) RegainMint.copy(alpha = 0.2f) else RegainSurface)
                        .border(
                            1.dp,
                            if (isSelected) RegainMint else RegainCardBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable(enabled = !isRunning) {
                            viewModel.setFocusSessionMinutes(mins)
                        }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chipText,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) RegainMintLight else RegainTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Play / Pause / Reset Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset Button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(RegainSurface)
                    .border(1.dp, RegainCardBorder, CircleShape)
                    .clickable { viewModel.resetFocusSession() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = RegainTextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Start / Pause Main Action
            Button(
                onClick = {
                    if (isRunning) viewModel.pauseFocusSession()
                    else viewModel.startFocusSession()
                },
                modifier = Modifier
                    .height(58.dp)
                    .width(180.dp),
                shape = RoundedCornerShape(29.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RegainMint,
                    contentColor = RegainDarkBg
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = if (isRunning) "PAUSE" else "START FOCUS",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Strict Focus Mode Protection Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = RegainSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, RegainCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(RegainSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = RegainMint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Strict Focus Mode",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RegainTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You asked me to be strict with you. You cannot quit until the timer ends.",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = RegainTextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = settings.focusTimerStrictEnabled,
                    onCheckedChange = { viewModel.toggleFocusStrict() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = RegainMint,
                        checkedTrackColor = RegainMint.copy(alpha = 0.25f),
                        uncheckedThumbColor = RegainTextMuted,
                        uncheckedTrackColor = RegainSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
