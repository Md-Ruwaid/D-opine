package com.strictlock.app.ui.screens

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RegainDarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                // App Brand Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(RegainSurfaceVariant)
                        .border(1.dp, RegainCardBorder, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = RegainMint,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome to StrictLock",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RegainTextPrimary,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "StrictLock requires 5 essential subsystem permissions to track usage, block doomscrolling, and enforce mindful pauses.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = RegainTextSecondary
                )
            }

            // Progress Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = RegainSurface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (permissions.requiredGranted) RegainMint else RegainAmber
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (permissions.requiredGranted) RegainMint.copy(alpha = 0.15f)
                                    else RegainAmber.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (permissions.requiredGranted) Icons.Default.Check else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (permissions.requiredGranted) RegainMint else RegainAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (permissions.requiredGranted) "Ready to Enter" else "${permissions.remainingRequiredCount} Required Permission(s) Remaining",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (permissions.requiredGranted) RegainMintLight else RegainAmber
                            )
                            Text(
                                text = if (permissions.requiredGranted) "All required permissions granted." else "Tap each card below to grant required access.",
                                fontSize = 12.sp,
                                color = RegainTextMuted
                            )
                        }
                    }
                }
            }

            // 1. Usage Access (REQUIRED)
            item {
                GatePermissionItem(
                    title = "1. Usage Access",
                    description = "Required to calculate foreground app screen time and enforce daily limits.",
                    granted = permissions.hasUsageAccess,
                    isRequired = true,
                    icon = Icons.Outlined.QueryStats,
                    onClick = { viewModel.openUsageAccessSettings(context) }
                )
            }

            // 2. Display Over Apps (REQUIRED)
            item {
                GatePermissionItem(
                    title = "2. Display Over Other Apps",
                    description = "Required for mindful breathing pause overlays and blocking screens.",
                    granted = permissions.hasOverlayPermission,
                    isRequired = true,
                    icon = Icons.Outlined.Layers,
                    onClick = { viewModel.openOverlaySettings(context) }
                )
            }

            // 3. Accessibility Service (REQUIRED)
            item {
                GatePermissionItem(
                    title = "3. Accessibility Service",
                    description = "Required to block YouTube Shorts & Instagram Reels doomscrolling.",
                    granted = permissions.hasAccessibilityService,
                    isRequired = true,
                    icon = Icons.Outlined.Widgets,
                    note = "OnePlus/Android 16: If restricted, tap 3 dots in App Info -> Allow restricted settings.",
                    onClick = { viewModel.openAccessibilitySettings(context) }
                )
            }

            // 4. Battery Optimization (REQUIRED)
            item {
                GatePermissionItem(
                    title = "4. Ignore Battery Optimizations",
                    description = "Exempts StrictLock from aggressive OnePlus task killers so locks stay active.",
                    granted = permissions.hasBatteryUnrestricted,
                    isRequired = true,
                    icon = Icons.Outlined.BatteryAlert,
                    onClick = { viewModel.openBatteryOptimizationSettings(context) }
                )
            }

            // 5. Notifications (REQUIRED)
            item {
                GatePermissionItem(
                    title = "5. Notification Permission",
                    description = "Keeps foreground service active and alerts when screen time limit is nearing.",
                    granted = permissions.hasNotificationPermission,
                    isRequired = true,
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

            // 6. Device Administrator (OPTIONAL - NOT REQUIRED)
            item {
                GatePermissionItem(
                    title = "6. Device Admin",
                    description = "Advanced anti-uninstall shield. Optional: can be enabled now or skipped.",
                    granted = permissions.hasDeviceAdmin,
                    isRequired = false,
                    icon = Icons.Outlined.Security,
                    onClick = { viewModel.openDeviceAdminSettings(context) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Sticky Bottom Enter Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(RegainDarkBg)
                .border(1.dp, RegainCardBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(16.dp)
        ) {
            Button(
                onClick = onContinue,
                enabled = permissions.requiredGranted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RegainMint,
                    disabledContainerColor = RegainSurfaceVariant
                )
            ) {
                Text(
                    text = if (permissions.requiredGranted) "Enter StrictLock →" else "Grant ${permissions.remainingRequiredCount} Required Permission(s)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (permissions.requiredGranted) Color.Black else RegainTextMuted
                )
            }
        }
    }
}

@Composable
private fun GatePermissionItem(
    title: String,
    description: String,
    granted: Boolean,
    isRequired: Boolean,
    icon: ImageVector,
    note: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RegainSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (granted) RegainMint.copy(alpha = 0.4f) else RegainCardBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(RegainSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (granted) RegainMint else RegainTextMuted,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RegainTextPrimary
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (isRequired) RegainAmber.copy(alpha = 0.15f)
                                    else RegainSurfaceVariant
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isRequired) "REQUIRED" else "OPTIONAL",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isRequired) RegainAmber else RegainTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = description,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = RegainTextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (granted) RegainMint.copy(alpha = 0.15f)
                            else if (isRequired) RegainAmber.copy(alpha = 0.15f)
                            else RegainSurfaceVariant
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (granted) "Active ✓" else if (isRequired) "Grant" else "Optional",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (granted) RegainMintLight else if (isRequired) RegainAmber else RegainTextMuted
                    )
                }
            }

            if (!granted && note != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = note,
                    fontSize = 11.sp,
                    color = RegainAmber,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
