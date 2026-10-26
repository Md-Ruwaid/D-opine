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
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.ui.theme.*
import com.strictlock.app.ui.viewmodel.MainViewModel
import com.strictlock.app.ui.viewmodel.PermissionsData

private data class CalibrationStep(
    val stepIndex: Int,
    val title: String,
    val subtitle: String,
    val plainDescription: String,
    val icon: ImageVector,
    val isGranted: Boolean,
    val oemNote: String? = null,
    val onGrant: () -> Unit
)

/**
 * Startup Permissions Gate utilizing the Zeigarnik Effect
 * Presents permissions as "System Shield Calibration" with plain language and privacy guarantees.
 */
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
        CalibrationStep(
            stepIndex = 1,
            title = "Usage Access",
            subtitle = "Calibrate screen-time measurement",
            plainDescription = "StrictLock measures your active app usage locally on your device to enforce limits when your daily budget is reached.",
            icon = Icons.Outlined.QueryStats,
            isGranted = permissions.hasUsageAccess,
            onGrant = { viewModel.openUsageAccessSettings(context) }
        ),
        CalibrationStep(
            stepIndex = 2,
            title = "Display Over Other Apps",
            subtitle = "Enable mindful breathing & lockout overlays",
            plainDescription = "Allows StrictLock to display the full-screen 5s mindful pause and lockout screens over restricted apps.",
            icon = Icons.Outlined.Layers,
            isGranted = permissions.hasOverlayPermission,
            onGrant = { viewModel.openOverlaySettings(context) }
        ),
        CalibrationStep(
            stepIndex = 3,
            title = "Accessibility Service",
            subtitle = "Instant foreground & doomscroll shield",
            plainDescription = "Used to detect when apps switch instantly, dismiss YouTube Shorts and Instagram Reels doomscroll feeds, and protect settings.",
            icon = Icons.Outlined.Widgets,
            isGranted = permissions.hasAccessibilityService,
            oemNote = "OnePlus / Oppo / Android 13+: If grayed out, open App Info -> tap 3 dots in top right -> Allow restricted settings.",
            onGrant = { viewModel.openAccessibilitySettings(context) }
        ),
        CalibrationStep(
            stepIndex = 4,
            title = "Battery Unrestricted",
            subtitle = "Background guard without silent termination",
            plainDescription = "Exempts StrictLock from aggressive OEM task-killers so your screen time tracking and strict limits are never killed in the background.",
            icon = Icons.Outlined.BatteryAlert,
            isGranted = permissions.hasBatteryUnrestricted,
            onGrant = { viewModel.openBatteryOptimizationSettings(context) }
        ),
        CalibrationStep(
            stepIndex = 5,
            title = "Notifications",
            subtitle = "Foreground status & 10m/2m alerts",
            plainDescription = "StrictLock keeps a silent background shield status and sends polite alerts at 10m and 2m before limits run out.",
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

    var currentStepIndex by remember {
        val firstUnfinished = steps.indexOfFirst { !it.isGranted }
        mutableIntStateOf(if (firstUnfinished != -1) firstUnfinished else 0)
    }

    // Auto-advance on grant
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
    val progressPercent = (grantedCount * 100) / totalSteps
    val progress = (grantedCount.toFloat() / totalSteps).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
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
                        .background(InteractiveSurface)
                        .border(1.dp, SubtleHairline, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = ParasympatheticSage,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "System Shield Calibration",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AlabasterText
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ZEIGARNIK PROGRESS BAR (Innately motivates completion of partial progress)
            Row(
                modifier = Modifier.fillMaxWidth(0.92f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (permissions.requiredGranted) "All Shields Calibrated" else "Calibration Step ${currentStepIndex + 1} of $totalSteps",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ParasympatheticSageLight
                )
                Text(
                    text = "$grantedCount of $totalSteps Calibrated ($progressPercent%)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (permissions.requiredGranted) ParasympatheticSageLight else MindfulAmber,
                    style = TabularNumberStyle
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (permissions.requiredGranted) ParasympatheticSage else MindfulAmber,
                trackColor = InteractiveSurface,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CALIBRATION POPUP CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (permissions.requiredGranted) ParasympatheticSage.copy(alpha = 0.5f) else SubtleHairline
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp)
                ) {
                    if (permissions.requiredGranted) {
                        // CELEBRATION READY STATE
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(ParasympatheticSage.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = ParasympatheticSage,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "Shields Fully Calibrated",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlabasterText
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "All essential Android subsystems have been granted. Your digital wellness boundaries are ready to activate.",
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                textAlign = TextAlign.Center,
                                color = MutedSlateText
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Privacy Guarantee Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(InteractiveSurface)
                                    .border(1.dp, SubtleHairline, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = ParasympatheticSage,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "100% Offline • Zero Data Leaves Device",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ParasympatheticSageLight
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = onContinue,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ParasympatheticSage,
                                    contentColor = DeepObsidian
                                )
                            ) {
                                Text(
                                    text = "ENTER STRICTLOCK →",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }
                    } else {
                        // INDIVIDUAL CALIBRATION STEP
                        val step = steps[currentStepIndex]
                        AnimatedContent(
                            targetState = step,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "calibration_step"
                        ) { targetStep ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(62.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (targetStep.isGranted) ParasympatheticSage.copy(alpha = 0.16f)
                                            else MindfulAmber.copy(alpha = 0.14f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = targetStep.icon,
                                        contentDescription = null,
                                        tint = if (targetStep.isGranted) ParasympatheticSage else MindfulAmber,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = targetStep.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlabasterText
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = targetStep.subtitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (targetStep.isGranted) ParasympatheticSageLight else MindfulAmber
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = targetStep.plainDescription,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    textAlign = TextAlign.Center,
                                    color = MutedSlateText
                                )

                                // Privacy badge
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(InteractiveSurface)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "100% Offline • Zero Data Leaves Device",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ParasympatheticSageLight
                                    )
                                }

                                if (targetStep.oemNote != null && !targetStep.isGranted) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(InteractiveSurface)
                                            .border(1.dp, SubtleHairline, RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = targetStep.oemNote,
                                            fontSize = 11.sp,
                                            lineHeight = 15.sp,
                                            color = MutedSlateText
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                // 56dp CTA in natural thumb arc
                                Button(
                                    onClick = targetStep.onGrant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (targetStep.isGranted) InteractiveSurface else ParasympatheticSage,
                                        contentColor = if (targetStep.isGranted) ParasympatheticSageLight else DeepObsidian
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
                                                tint = ParasympatheticSage,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = "Calibrated ✓",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            Text(
                                                text = "Calibrate ${targetStep.title}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Wizard Navigation Controls
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = { if (currentStepIndex > 0) currentStepIndex-- },
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
                                            Text("Previous", fontSize = 12.sp, color = if (currentStepIndex > 0) AlabasterText else DimSlateText)
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
                                                            s.isGranted -> ParasympatheticSage
                                                            idx == currentStepIndex -> AlabasterText
                                                            else -> DimSlateText
                                                        }
                                                    )
                                                    .clickable { currentStepIndex = idx }
                                            )
                                        }
                                    }

                                    TextButton(
                                        onClick = { if (currentStepIndex < totalSteps - 1) currentStepIndex++ },
                                        enabled = currentStepIndex < totalSteps - 1
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text("Next", fontSize = 12.sp, color = if (currentStepIndex < totalSteps - 1) AlabasterText else DimSlateText)
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

            // Optional Device Admin prompt below popup
            if (permissions.requiredGranted && !permissions.hasDeviceAdmin) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElevatedSlate)
                        .border(1.dp, SubtleHairline, RoundedCornerShape(14.dp))
                        .clickable { viewModel.openDeviceAdminSettings(context) }
                        .padding(14.dp),
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
                            tint = AuthorityIndigo,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Device Admin (Optional)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlabasterText
                            )
                            Text(
                                text = "Arm uninstallation defense when strict mode is active.",
                                fontSize = 11.sp,
                                color = MutedSlateText
                            )
                        }
                    }
                    Text(
                        text = "Enable",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuthorityIndigo
                    )
                }
            }
        }
    }
}
