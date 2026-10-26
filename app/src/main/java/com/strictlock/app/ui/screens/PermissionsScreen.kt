package com.strictlock.app.ui.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.BatteryAlert
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.ui.theme.*
import com.strictlock.app.ui.viewmodel.MainViewModel

@Composable
fun PermissionsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val permissions by viewModel.permissions.collectAsState()
    val showEmergencyDialog by viewModel.showEmergencyDialog.collectAsState()
    val emergencyTapCount by viewModel.emergencyTapCount.collectAsState()

    var isOemGuideExpanded by remember { mutableStateOf(false) }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.refreshPermissions()
    }

    val totalShields = 6
    var calibratedShields = 0
    if (permissions.hasUsageAccess) calibratedShields++
    if (permissions.hasOverlayPermission) calibratedShields++
    if (permissions.hasAccessibilityService) calibratedShields++
    if (permissions.hasDeviceAdmin) calibratedShields++
    if (permissions.hasBatteryUnrestricted) calibratedShields++
    if (permissions.hasNotificationPermission) calibratedShields++

    val calibrationPercent = (calibratedShields * 100) / totalShields

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Shields & Calibration",
                fontSize = 28.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.SemiBold,
                color = AlabasterText,
                letterSpacing = (-0.25).sp
            )
            Text(
                text = "Essential Android subsystem accesses & privacy boundaries",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MutedSlateText
            )
        }

        // =========================================================================
        // ZEIGARNIK EFFECT PROGRESS CARD: "System Shield Calibration"
        // =========================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (permissions.requiredGranted) ParasympatheticSage.copy(alpha = 0.5f) else MindfulAmber.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
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
                                        if (permissions.requiredGranted) ParasympatheticSage.copy(alpha = 0.16f)
                                        else MindfulAmber.copy(alpha = 0.16f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (permissions.requiredGranted) Icons.Default.Shield else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (permissions.requiredGranted) ParasympatheticSage else MindfulAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = if (permissions.requiredGranted) "Shields Operational" else "Shield Calibration Needed",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (permissions.requiredGranted) ParasympatheticSageLight else MindfulAmber
                                )
                                Text(
                                    text = "$calibratedShields of $totalShields Shields Calibrated ($calibrationPercent%)",
                                    fontSize = 12.sp,
                                    color = MutedSlateText,
                                    style = TabularNumberStyle
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { calibratedShields.toFloat() / totalShields },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (permissions.requiredGranted) ParasympatheticSage else MindfulAmber,
                        trackColor = InteractiveSurface,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Privacy Guarantee Micro-Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(InteractiveSurface)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = ParasympatheticSage,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = "100% Offline • Zero Data Leaves Device",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ParasympatheticSageLight
                            )
                        }
                    }
                }
            }
        }

        // =========================================================================
        // OEM ACCORDION CARD (OnePlus / Oppo / Xiaomi / Android 13–16 Guide)
        // =========================================================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isOemGuideExpanded = !isOemGuideExpanded },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
                border = androidx.compose.foundation.BorderStroke(1.dp, SubtleHairline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MindfulAmber.copy(alpha = 0.16f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "!",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MindfulAmber
                                )
                            }
                            Column {
                                Text(
                                    text = "OnePlus / Android 14–16 Helper",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlabasterText
                                )
                                Text(
                                    text = "Troubleshoot grayed-out \"Restricted Setting\"",
                                    fontSize = 11.sp,
                                    color = MutedSlateText
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isOemGuideExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MutedSlateText
                        )
                    }

                    AnimatedVisibility(visible = isOemGuideExpanded) {
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            listOf(
                                "1" to "Open StrictLock App Info (long press app icon > App Info).",
                                "2" to "Tap the 3 dots in the top-right corner.",
                                "3" to "Select \"Allow restricted settings\" and verify PIN / Fingerprint.",
                                "4" to "Return to StrictLock and toggle Accessibility Service."
                            ).forEach { (stepNum, text) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(22.dp)
                                            .clip(CircleShape)
                                            .background(InteractiveSurface)
                                            .border(1.dp, SubtleHairline, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stepNum,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ParasympatheticSageLight,
                                            style = TabularNumberStyle
                                        )
                                    }
                                    Text(
                                        text = text,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp,
                                        color = MutedSlateText
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // =========================================================================
        // PERMISSION CALIBRATION CARDS
        // =========================================================================
        item {
            PermissionCalibrationCard(
                title = "1. Usage Access",
                plainDescription = "Measures active foreground application duration locally on device.",
                granted = permissions.hasUsageAccess,
                icon = Icons.Outlined.QueryStats,
                onClick = { viewModel.openUsageAccessSettings(context) }
            )
        }

        item {
            PermissionCalibrationCard(
                title = "2. Display Over Other Apps",
                plainDescription = "Enables full-screen 5s mindful pause and lockout screens over blocked apps.",
                granted = permissions.hasOverlayPermission,
                icon = Icons.Outlined.Layers,
                onClick = { viewModel.openOverlaySettings(context) }
            )
        }

        item {
            PermissionCalibrationCard(
                title = "3. Accessibility Service",
                plainDescription = "Instant foreground window detection, Reels/Shorts dismissal, and settings defense.",
                granted = permissions.hasAccessibilityService,
                icon = Icons.Outlined.Widgets,
                onClick = { viewModel.openAccessibilitySettings(context) }
            )
        }

        item {
            PermissionCalibrationCard(
                title = "4. Device Admin (Optional)",
                plainDescription = "Optional anti-tamper policy. Prevents uninstallation when Strict Mode is armed.",
                granted = permissions.hasDeviceAdmin,
                icon = Icons.Outlined.Security,
                onClick = { viewModel.openDeviceAdminSettings(context) }
            )
        }

        item {
            PermissionCalibrationCard(
                title = "5. Battery Unrestricted",
                plainDescription = "Exempts StrictLock from aggressive OEM task-killers so guards survive.",
                granted = permissions.hasBatteryUnrestricted,
                icon = Icons.Outlined.BatteryAlert,
                onClick = { viewModel.openBatteryOptimizationSettings(context) }
            )
        }

        item {
            PermissionCalibrationCard(
                title = "6. Notifications",
                plainDescription = "Displays continuous foreground status and sends polite 10m / 2m remaining alerts.",
                granted = permissions.hasNotificationPermission,
                icon = Icons.Outlined.Notifications,
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        viewModel.openNotificationSettings(context)
                    }
                }
            )
        }

        // =========================================================================
        // EMERGENCY EXIT SECRET TAP AREA
        // =========================================================================
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "StrictLock v1.0.1 (Regain Architecture)",
                    fontSize = 12.sp,
                    color = DimSlateText,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            viewModel.handleEmergencyTap()
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Secret tap progress dots feedback
                if (emergencyTapCount > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in 1..7) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (i <= emergencyTapCount) ControlledCrimson else InteractiveSurface
                                    )
                            )
                        }
                    }
                }

                Text(
                    text = "Emergency override: Tap version label 7 times",
                    fontSize = 10.sp,
                    color = DimSlateText.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Emergency Reset Confirmation Dialog
    if (showEmergencyDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissEmergencyDialog() },
            containerColor = ElevatedSlate,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = ControlledCrimson,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Emergency Override Triggered",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ControlledCrimson
                    )
                }
            },
            text = {
                Text(
                    text = "Are you sure you want to terminate ALL active rules immediately? This bypasses all strict and casual limits and restores unrestricted access.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MutedSlateText
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmEmergencyExit() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ControlledCrimson,
                        contentColor = AlabasterText
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("TERMINATE ALL", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissEmergencyDialog() }) {
                    Text("Cancel", color = MutedSlateText)
                }
            }
        )
    }
}

@Composable
private fun PermissionCalibrationCard(
    title: String,
    plainDescription: String,
    granted: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
        border = androidx.compose.foundation.BorderStroke(1.dp, SubtleHairline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (granted) ParasympatheticSage.copy(alpha = 0.12f)
                        else InteractiveSurface
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (granted) ParasympatheticSage else DimSlateText,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AlabasterText
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = plainDescription,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = MutedSlateText
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (granted) ParasympatheticSage.copy(alpha = 0.16f)
                        else MindfulAmber.copy(alpha = 0.16f)
                    )
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (granted) "Active" else "Calibrate",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (granted) ParasympatheticSageLight else MindfulAmber,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
