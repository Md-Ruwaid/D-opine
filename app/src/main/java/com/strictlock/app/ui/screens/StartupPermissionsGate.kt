package com.strictlock.app.ui.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.BatteryAlert
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.ui.theme.RegainAmber
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
import com.strictlock.app.ui.viewmodel.PermissionsData

private data class PromptStep(
    val stepIndex: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val isGranted: Boolean,
    val note: String? = null,
    val onGrant: () -> Unit
)

@Composable
fun StartupPermissionsGate(
    viewModel: MainViewModel,
    permissions: PermissionsData,
    onContinue: () -> Unit
) {
    val context = LocalContext.current

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.refreshPermissions()
    }

    val steps = listOf(
        PromptStep(
            stepIndex = 1,
            title = "Usage Access",
            subtitle = "Track screen time & enforce limits",
            description = "StrictLock requires Usage Access to accurately track which apps you use and enforce your daily budgets when limits are reached.",
            icon = Icons.Outlined.QueryStats,
            isGranted = permissions.hasUsageAccess,
            onGrant = { viewModel.openUsageAccessSettings(context) }
        ),
        PromptStep(
            stepIndex = 2,
            title = "Display Over Other Apps",
            subtitle = "Mindful pause & lock overlays",
            description = "Allows StrictLock to present mindful breathing pauses and lock screens directly over restricted apps when time is up.",
            icon = Icons.Outlined.Layers,
            isGranted = permissions.hasOverlayPermission,
            onGrant = { viewModel.openOverlaySettings(context) }
        ),
        PromptStep(
            stepIndex = 3,
            title = "Accessibility Service",
            subtitle = "Instant foreground & doomscroll protection",
            description = "Used to detect foreground switches instantaneously and block YouTube Shorts & Instagram Reels doomscrolling.",
            icon = Icons.Outlined.Widgets,
            isGranted = permissions.hasAccessibilityService,
            note = "OnePlus/Android 14+: If grayed out, open App Info -> tap 3 dots in top right -> Allow restricted settings.",
            onGrant = { viewModel.openAccessibilitySettings(context) }
        ),
        PromptStep(
            stepIndex = 4,
            title = "Battery Optimization",
            subtitle = "Background survival without killing",
            description = "Exempts StrictLock from aggressive manufacturer battery savers so your locks and screen time tracking don't silently stop.",
            icon = Icons.Outlined.BatteryAlert,
            isGranted = permissions.hasBatteryUnrestricted,
            onGrant = { viewModel.openBatteryOptimizationSettings(context) }
        ),
        PromptStep(
            stepIndex = 5,
            title = "Notifications",
            subtitle = "Foreground service & limit warnings",
            description = "StrictLock sends friendly warnings at 10m and 2m before your app limits expire so you are never abruptly cut off.",
            icon = Icons.Outlined.Notifications,
            isGranted = permissions.hasNotificationPermission,
            onGrant = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    viewModel.openNotificationSettings(context)
                }
            }
        )
    )

    // Current popup index
    var currentStepIndex by remember {
        // Start at first ungranted step
        val firstUnfinished = steps.indexOfFirst { !it.isGranted }
        mutableIntStateOf(if (firstUnfinished != -1) firstUnfinished else 0)
    }

    // Automatically advance if current step gets granted
    LaunchedEffect(permissions) {
        if (steps[currentStepIndex].isGranted && !permissions.requiredGranted) {
            val nextUnfinished = steps.indexOfFirst { !it.isGranted }
            if (nextUnfinished != -1) {
                currentStepIndex = nextUnfinished
            }
        }
    }

    val totalSteps = steps.size
    val grantedCount = steps.count { it.isGranted }
    val progress = (grantedCount.toFloat() / totalSteps).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RegainDarkBg)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(RegainSurfaceVariant)
                        .border(1.dp, RegainCardBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = RegainMint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "StrictLock Setup",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RegainTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Progress Bar & Indicators
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (permissions.requiredGranted) "Ready to Enter" else "Step ${currentStepIndex + 1} of $totalSteps",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RegainMintLight
                )
                Text(
                    text = "$grantedCount / $totalSteps Granted",
                    fontSize = 12.sp,
                    color = RegainTextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (permissions.requiredGranted) RegainMint else RegainAmber,
                trackColor = RegainSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // MAIN PROMPT WINDOW POPUP CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = RegainSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (permissions.requiredGranted) RegainMint.copy(alpha = 0.5f) else RegainCardBorder
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    if (permissions.requiredGranted) {
                        // CELEBRATION READY POPUP
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(RegainMint.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = RegainMint,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Setup Complete!",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = RegainTextPrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "All 5 essential subsystem accesses have been successfully configured. StrictLock is ready to protect your screen time.",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                textAlign = TextAlign.Center,
                                color = RegainTextSecondary
                            )

                            Spacer(modifier = Modifier.height(26.dp))

                            Button(
                                onClick = onContinue,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RegainMint,
                                    contentColor = RegainDarkBg
                                )
                            ) {
                                Text(
                                    text = "Enter StrictLock →",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    } else {
                        // INDIVIDUAL PERMISSION PROMPT POPUP
                        val step = steps[currentStepIndex]
                        AnimatedContent(
                            targetState = step,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "prompt_step"
                        ) { targetStep ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Glowing Permission Icon
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (targetStep.isGranted) RegainMint.copy(alpha = 0.15f)
                                            else RegainAmber.copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = targetStep.icon,
                                        contentDescription = null,
                                        tint = if (targetStep.isGranted) RegainMint else RegainAmber,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Title & Subtitle
                                Text(
                                    text = targetStep.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = RegainTextPrimary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = targetStep.subtitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (targetStep.isGranted) RegainMintLight else RegainAmber
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Description
                                Text(
                                    text = targetStep.description,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    textAlign = TextAlign.Center,
                                    color = RegainTextSecondary
                                )

                                if (targetStep.note != null && !targetStep.isGranted) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(RegainSurfaceVariant)
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = targetStep.note,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp,
                                            color = RegainTextMuted
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Primary Grant Action Button
                                Button(
                                    onClick = targetStep.onGrant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (targetStep.isGranted) RegainSurfaceVariant else RegainMint,
                                        contentColor = if (targetStep.isGranted) RegainMintLight else RegainDarkBg
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (targetStep.isGranted) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = RegainMint,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = "Granted ✓",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            Text(
                                                text = "Grant ${targetStep.title}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Wizard Navigation Controls
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = {
                                            if (currentStepIndex > 0) currentStepIndex--
                                        },
                                        enabled = currentStepIndex > 0
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text("Previous", fontSize = 12.sp)
                                        }
                                    }

                                    // Dots indicator
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        steps.forEachIndexed { idx, s ->
                                            Box(
                                                modifier = Modifier
                                                    .size(if (idx == currentStepIndex) 8.dp else 6.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when {
                                                            s.isGranted -> RegainMint
                                                            idx == currentStepIndex -> RegainMintLight
                                                            else -> RegainCardBorder
                                                        }
                                                    )
                                                    .clickable { currentStepIndex = idx }
                                            )
                                        }
                                    }

                                    TextButton(
                                        onClick = {
                                            if (currentStepIndex < totalSteps - 1) currentStepIndex++
                                        },
                                        enabled = currentStepIndex < totalSteps - 1
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text("Next", fontSize = 12.sp)
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Optional Device Admin card reminder below popup
            if (permissions.requiredGranted && !permissions.hasDeviceAdmin) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(RegainSurfaceVariant)
                        .clickable { viewModel.openDeviceAdminSettings(context) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = null,
                            tint = RegainAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Device Admin (Optional)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RegainTextPrimary
                            )
                            Text(
                                text = "Engages uninstallation prevention when strict mode is active.",
                                fontSize = 11.sp,
                                color = RegainTextMuted
                            )
                        }
                    }
                    Text(
                        text = "Enable",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RegainMintLight
                    )
                }
            }
        }
    }
}
