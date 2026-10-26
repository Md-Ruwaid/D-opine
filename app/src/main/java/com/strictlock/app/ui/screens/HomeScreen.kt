package com.strictlock.app.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.ui.components.AppIconImage
import com.strictlock.app.ui.theme.*
import com.strictlock.app.ui.viewmodel.MainViewModel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle

@Composable
fun OdometerText(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = text,
        transitionSpec = {
            (slideInVertically(animationSpec = MotionPhysics.iosFluidIntOffsetSpring) { -it } + fadeIn())
                .togetherWith(slideOutVertically(animationSpec = MotionPhysics.iosFluidIntOffsetSpring) { it } + fadeOut())
        },
        label = "odometer_anim",
        modifier = modifier
    ) { animatedText ->
        Text(
            text = animatedText,
            style = style,
            color = color
        )
    }
}

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val totalTimeMs by viewModel.totalScreenTimeTodayMs.collectAsState()
    val savedTimeMs by viewModel.timeSavedTodayMs.collectAsState()
    val pickups by viewModel.devicePickups.collectAsState()
    val topApps by viewModel.topApps.collectAsState()
    val hourlyUsage by viewModel.hourlyUsage.collectAsState()
    val regainSettings by viewModel.regainSettings.collectAsState()

    val totalHours = totalTimeMs / (3600 * 1000)
    val totalMins = (totalTimeMs % (3600 * 1000)) / (60 * 1000)

    val savedHours = savedTimeMs / (3600 * 1000)
    val savedMins = (savedTimeMs % (3600 * 1000)) / (60 * 1000)

    val strictAppsCount by viewModel.strictAppsCount.collectAsState()
    val hasStrictRules by viewModel.hasStrictRules.collectAsState()
    val isStrictModeLocked by viewModel.isStrictModeLocked.collectAsState()
    val context = LocalContext.current

    var showCannotDisableDialog by remember { mutableStateOf(false) }

    // Staggered Entrance Animations (45ms cadence per section)
    var isLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isLoaded = true
    }

    val s0Alpha by animateFloatAsState(targetValue = if (isLoaded) 1f else 0f, animationSpec = tween(320, 0, FastOutSlowInEasing), label = "s0A")
    val s0Y by animateFloatAsState(targetValue = if (isLoaded) 0f else 24f, animationSpec = MotionPhysics.iosFluidSpring, label = "s0Y")

    val s1Alpha by animateFloatAsState(targetValue = if (isLoaded) 1f else 0f, animationSpec = tween(320, 45, FastOutSlowInEasing), label = "s1A")
    val s1Y by animateFloatAsState(targetValue = if (isLoaded) 0f else 24f, animationSpec = MotionPhysics.iosFluidSpring, label = "s1Y")

    val s2Alpha by animateFloatAsState(targetValue = if (isLoaded) 1f else 0f, animationSpec = tween(320, 90, FastOutSlowInEasing), label = "s2A")
    val s2Y by animateFloatAsState(targetValue = if (isLoaded) 0f else 24f, animationSpec = MotionPhysics.iosFluidSpring, label = "s2Y")

    val s3Alpha by animateFloatAsState(targetValue = if (isLoaded) 1f else 0f, animationSpec = tween(320, 135, FastOutSlowInEasing), label = "s3A")
    val s3Y by animateFloatAsState(targetValue = if (isLoaded) 0f else 24f, animationSpec = MotionPhysics.iosFluidSpring, label = "s3Y")

    val s4Alpha by animateFloatAsState(targetValue = if (isLoaded) 1f else 0f, animationSpec = tween(320, 180, FastOutSlowInEasing), label = "s4A")
    val s4Y by animateFloatAsState(targetValue = if (isLoaded) 0f else 24f, animationSpec = MotionPhysics.iosFluidSpring, label = "s4Y")

    // Subtle pulsing dot animation for the top integrity status badge
    val infiniteTransition = rememberInfiniteTransition(label = "integrity_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    if (showCannotDisableDialog) {
        AlertDialog(
            onDismissRequest = { showCannotDisableDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = AuthorityIndigo,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Strict Mode Protected",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlabasterText
                    )
                }
            },
            text = {
                Text(
                    text = "You currently have $strictAppsCount app(s) locked under strict rules. To protect your commitment against impulse, Strict Mode and uninstall protection cannot be deactivated until rules expire or through Emergency Reset.",
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MutedSlateText
                )
            },
            confirmButton = {
                Button(
                    onClick = { showCannotDisableDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ParasympatheticSage,
                        contentColor = DeepObsidian
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Understood", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = InteractiveSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "StrictLock",
                        fontSize = 28.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AlabasterText,
                        letterSpacing = (-0.25).sp
                    )
                    Text(
                        text = "Neuro-Ergonomic Habit Architecture",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MutedSlateText
                    )
                }
            }
        }

        // =========================================================================
        // 1. TOP INTEGRITY BANNER (High-Effort / Deliberate Zone: Top 25%)
        // High-consequence controls placed at the top introduce physical friction
        // =========================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = s0Alpha
                        translationY = s0Y
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isStrictModeLocked) AuthorityIndigo.copy(alpha = 0.5f) else SubtleHairline
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    // Header Row with Pulsing Integrity Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isStrictModeLocked) AuthorityIndigo.copy(alpha = 0.16f)
                                        else ParasympatheticSage.copy(alpha = 0.16f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isStrictModeLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = if (isStrictModeLocked) AuthorityIndigo else ParasympatheticSage,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "STRICT SHIELD INTEGRITY",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = AlabasterText
                                )
                                Text(
                                    text = if (isStrictModeLocked) "Anti-Uninstall Armed" else "Safe to Reconfigure / Uninstall",
                                    fontSize = 11.sp,
                                    color = if (isStrictModeLocked) AuthorityIndigo else ParasympatheticSageLight
                                )
                            }
                        }

                        // Pulsing Status Micro-Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isStrictModeLocked) AuthorityIndigo.copy(alpha = 0.14f)
                                    else ParasympatheticSage.copy(alpha = 0.14f)
                                )
                                .border(
                                    1.dp,
                                    if (isStrictModeLocked) AuthorityIndigo.copy(alpha = 0.35f)
                                    else ParasympatheticSage.copy(alpha = 0.35f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .scale(pulseScale)
                                        .clip(CircleShape)
                                        .background(if (isStrictModeLocked) AuthorityIndigo else ParasympatheticSage)
                                )
                                Text(
                                    text = if (isStrictModeLocked) "ARMED" else "UNLOCKED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isStrictModeLocked) AuthorityIndigo else ParasympatheticSageLight,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isStrictModeLocked) {
                            if (strictAppsCount > 0)
                                "$strictAppsCount app(s) actively locked under strict policy. Settings tampering and uninstallation are intercepted."
                            else
                                "Strict Mode is armed. Device Administrator defends active rules against bypasses."
                        } else {
                            "No apps currently under strict multi-day rules. StrictLock can be modified or safely uninstalled."
                        },
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MutedSlateText
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Switch Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Strict Mode Defense",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AlabasterText
                        )

                        Switch(
                            checked = isStrictModeLocked,
                            onCheckedChange = { targetOn ->
                                if (!targetOn && strictAppsCount > 0) {
                                    showCannotDisableDialog = true
                                } else {
                                    viewModel.toggleUninstallProtection(context)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AuthorityIndigo,
                                checkedTrackColor = AuthorityIndigo.copy(alpha = 0.25f),
                                uncheckedThumbColor = DimSlateText,
                                uncheckedTrackColor = InteractiveSurface
                            )
                        )
                    }

                    // Deliberate high-friction action when safe
                    if (!isStrictModeLocked) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { viewModel.triggerAppUninstall(context) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = InteractiveSurface,
                                contentColor = ControlledCrimson
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SubtleHairline)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Uninstall StrictLock App",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 2. HERO DUAL-METRIC CARD (Von Restorff Isolation: Elevates Time Regained)
        // =========================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = s1Alpha
                        translationY = s1Y
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
                border = androidx.compose.foundation.BorderStroke(1.dp, SubtleHairline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Dual Metric Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Left: Screen Time Used Today
                        Column(modifier = Modifier.weight(1.1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = MutedSlateText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "SCREEN TIME",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MutedSlateText,
                                    letterSpacing = 0.8.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            OdometerText(
                                text = "${totalHours}h ${totalMins}m",
                                style = TabularNumberStyle.copy(
                                    fontSize = 32.sp,
                                    lineHeight = 38.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = AlabasterText
                            )
                            Text(
                                text = "Today's total usage",
                                fontSize = 12.sp,
                                color = DimSlateText
                            )
                        }

                        // Right: Time Regained (Parasympathetic Sage Highlight)
                        Column(
                            modifier = Modifier.weight(0.9f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = ParasympatheticSage,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "TIME REGAINED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ParasympatheticSageLight,
                                    letterSpacing = 0.8.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            OdometerText(
                                text = "+${savedHours}h ${savedMins}m",
                                style = TabularNumberStyle.copy(
                                    fontSize = 28.sp,
                                    lineHeight = 36.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ParasympatheticSage
                            )
                            Text(
                                text = "vs 3.5h unguided baseline",
                                fontSize = 11.sp,
                                color = DimSlateText
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Mindful Balance Ratio Progress Bar
                    val baselineMs = (3.5 * 3600 * 1000).toFloat()
                    val ratio = (totalTimeMs.toFloat() / baselineMs).coerceIn(0f, 1f)

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Circadian Budget Pace",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MutedSlateText
                            )
                            Text(
                                text = "${(ratio * 100).toInt()}% of healthy cap",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (ratio > 0.85f) MindfulAmber else ParasympatheticSageLight,
                                style = TabularNumberStyle
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (ratio > 0.85f) MindfulAmber else ParasympatheticSage,
                            trackColor = InteractiveSurface,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        // =========================================================================
        // 3. SECONDARY STAT PILLS (Pickups & Health Factor)
        // =========================================================================
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = s2Alpha
                        translationY = s2Y
                    },
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Device Pickups Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SubtleHairline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(InteractiveSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = MindfulAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            OdometerText(
                                text = "$pickups",
                                style = TabularNumberStyle.copy(
                                    fontSize = 20.sp,
                                    lineHeight = 24.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = AlabasterText
                            )
                            Text(
                                text = "Device Pickups",
                                fontSize = 11.sp,
                                color = MutedSlateText
                            )
                        }
                    }
                }

                // Autonomic Balance Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SubtleHairline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(InteractiveSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = AuthorityIndigo,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (strictAppsCount > 0) "$strictAppsCount Active" else "Guarded",
                                fontSize = 16.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlabasterText
                            )
                            Text(
                                text = "Shield Defense",
                                fontSize = 11.sp,
                                color = MutedSlateText
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 4. HOURLY USAGE INTENSITY (Visual Scanning Zone: Middle 50%)
        // =========================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = s3Alpha
                        translationY = s3Y
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
                border = androidx.compose.foundation.BorderStroke(1.dp, SubtleHairline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HOURLY USAGE INTENSITY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedSlateText,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "Circadian distribution",
                            fontSize = 11.sp,
                            color = DimSlateText
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val maxMins = hourlyUsage.maxOfOrNull { it.minutesUsed }?.coerceAtLeast(10) ?: 60

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        hourlyUsage.forEach { item ->
                            val heightFraction = (item.minutesUsed.toFloat() / maxMins).coerceIn(0.08f, 1f)
                            val animatedHeight by animateFloatAsState(
                                targetValue = if (isLoaded) heightFraction else 0f,
                                animationSpec = MotionPhysics.iosFluidSpring,
                                label = "bar_height"
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(16.dp)
                                        .height((64 * animatedHeight).dp)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(
                                            if (animatedHeight > 0.65f) AuthorityIndigo
                                            else if (animatedHeight > 0.35f) ParasympatheticSage
                                            else SubtleHairline
                                        )
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.hourLabel,
                                    fontSize = 11.sp,
                                    color = MutedSlateText,
                                    fontWeight = FontWeight.Medium,
                                    style = TabularNumberStyle
                                )
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 5. TOP USED APPS BREAKDOWN (Miller's Law <= 3 data points per row)
        // =========================================================================
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Used Applications",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AlabasterText
                )
                Text(
                    text = "${topApps.size} monitored",
                    fontSize = 12.sp,
                    color = MutedSlateText
                )
            }
        }

        if (topApps.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ElevatedSlate)
                        .border(1.dp, SubtleHairline, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Grant Usage Access in Shields tab to load screen time breakdowns.",
                        fontSize = 13.sp,
                        color = MutedSlateText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            items(topApps) { app ->
                val appMins = app.usageTimeMs / (60 * 1000)
                val hours = appMins / 60
                val mins = appMins % 60
                val timeString = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = s4Alpha
                            translationY = s4Y
                        }
                        .iosTactilePress(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SubtleHairline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconImage(
                            packageName = app.packageName,
                            appName = app.appName,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = app.appName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AlabasterText,
                                    maxLines = 1
                                )
                                Text(
                                    text = timeString,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ParasympatheticSageLight,
                                    style = TabularNumberStyle
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { app.percentageOfTotal },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = ParasympatheticSage,
                                trackColor = InteractiveSurface,
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
