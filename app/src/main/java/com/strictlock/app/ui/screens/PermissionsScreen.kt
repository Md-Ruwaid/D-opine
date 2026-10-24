package com.strictlock.app.ui.screens

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.service.StrictLockDeviceAdminReceiver
import com.strictlock.app.ui.theme.RegainAmber
import com.strictlock.app.ui.theme.RegainCardBorder
import com.strictlock.app.ui.theme.RegainCrimson
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
fun PermissionsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val permissions by viewModel.permissions.collectAsState()
    val showEmergencyDialog by viewModel.showEmergencyDialog.collectAsState()

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        viewModel.refreshPermissions()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RegainDarkBg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Permissions & Shields",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RegainTextPrimary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Essential Android subsystem accesses required by Regain",
                fontSize = 13.sp,
                color = RegainTextMuted
            )
        }

        // Status Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = RegainSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (permissions.requiredGranted) RegainMint else RegainAmber
                )
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
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (permissions.requiredGranted) RegainMint.copy(alpha = 0.15f)
                                else RegainAmber.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (permissions.requiredGranted) Icons.Default.Shield else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (permissions.requiredGranted) RegainMint else RegainAmber,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (permissions.requiredGranted) "Essential Shields Active" else "Permissions Setup Needed",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (permissions.requiredGranted) RegainMintLight else RegainAmber
                        )
                        Text(
                            text = if (permissions.requiredGranted)
                                "Background monitoring, anti-tamper, and overlay ready."
                            else
                                "Grant missing accesses below to ensure rock-solid enforcement.",
                            fontSize = 12.sp,
                            color = RegainTextSecondary
                        )
                    }
                }
            }
        }

        // OnePlus Android 16 Notice
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RegainSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, RegainCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ONEPLUS OXYGENOS (ANDROID 16) NOTICE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RegainAmber,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "If Accessibility is greyed out as \"Restricted Setting\": Open App Info for StrictLock -> tap 3 dots (top-right) -> tap \"Allow restricted settings\" -> enter PIN, then enable Accessibility.",
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = RegainTextSecondary
                    )
                }
            }
        }

        // Permission Items
        item {
            PermissionCard(
                title = "1. Usage Access",
                description = "Required to measure exact foreground screen time per application.",
                granted = permissions.hasUsageAccess,
                icon = Icons.Outlined.QueryStats,
                onClick = { viewModel.openUsageAccessSettings(context) }
            )
        }

        item {
            PermissionCard(
                title = "2. Display Over Other Apps",
                description = "Enables full-screen mindful breathing and time's up overlays over blocked apps.",
                granted = permissions.hasOverlayPermission,
                icon = Icons.Outlined.Layers,
                onClick = { viewModel.openOverlaySettings(context) }
            )
        }

        item {
            PermissionCard(
                title = "3. Accessibility Service",
                description = "Instant foreground window detection, Reels/Shorts closing, and Settings tamper shield.",
                granted = permissions.hasAccessibilityService,
                icon = Icons.Outlined.Widgets,
                onClick = { viewModel.openAccessibilitySettings(context) }
            )
        }

        item {
            PermissionCard(
                title = "4. Device Admin (Optional)",
                description = "Optional anti-uninstall shield. Not required to run StrictLock.",
                granted = permissions.hasDeviceAdmin,
                icon = Icons.Outlined.Security,
                onClick = { viewModel.openDeviceAdminSettings(context) }
            )
        }

        item {
            PermissionCard(
                title = "5. Battery Unrestricted",
                description = "Exempts Regain from OnePlus background task killers and Android Doze.",
                granted = permissions.hasBatteryUnrestricted,
                icon = Icons.Outlined.BatteryAlert,
                onClick = { viewModel.openBatteryOptimizationSettings(context) }
            )
        }

        item {
            PermissionCard(
                title = "6. Notifications",
                description = "Displays continuous foreground status and sends 10m / 2m remaining alerts.",
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

        // Emergency Exit Secret Tap Area
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Regain v71.3 (StrictLock Engine)",
                    fontSize = 12.sp,
                    color = RegainTextMuted,
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
                Text(
                    text = "Emergency exit: Tap version label 7 times",
                    fontSize = 10.sp,
                    color = RegainTextMuted.copy(alpha = 0.5f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Emergency Reset Dialog
    if (showEmergencyDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissEmergencyDialog() },
            containerColor = RegainSurface,
            title = {
                Text(
                    text = "Emergency Exit Triggered",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = RegainCrimson
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to terminate ALL active rules immediately? This bypasses all strict and casual limits and restores unrestricted access.",
                    fontSize = 13.sp,
                    color = RegainTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmEmergencyExit() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RegainCrimson,
                        contentColor = RegainTextPrimary
                    )
                ) {
                    Text("TERMINATE ALL")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissEmergencyDialog() }) {
                    Text("Cancel", color = RegainTextMuted)
                }
            }
        )
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    granted: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
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
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (granted) RegainMint else RegainTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RegainTextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = RegainTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (granted) RegainMint.copy(alpha = 0.15f) else RegainAmber.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (granted) "Active" else "Grant",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (granted) RegainMintLight else RegainAmber
                )
            }
        }
    }
}
