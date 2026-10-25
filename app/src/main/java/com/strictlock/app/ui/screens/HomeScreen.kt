package com.strictlock.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.automirrored.filled.TrendingUp
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
import androidx.compose.ui.platform.LocalContext
import com.strictlock.app.ui.theme.RegainCrimson
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.ui.components.AppIconImage
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

    if (showCannotDisableDialog) {
        AlertDialog(
            onDismissRequest = { showCannotDisableDialog = false },
            title = {
                Text(
                    text = "Strict Mode Protected",
                    fontWeight = FontWeight.Bold,
                    color = RegainTextPrimary
                )
            },
            text = {
                Text(
                    text = "You currently have $strictAppsCount app(s) locked under strict rules. In accordance with StrictLock, strict mode and uninstall protection cannot be deactivated until rules expire or through Emergency Reset.",
                    fontSize = 13.sp,
                    color = RegainTextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = { showCannotDisableDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RegainMint, contentColor = RegainDarkBg)
                ) {
                    Text("Understood")
                }
            },
            containerColor = RegainSurface
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RegainDarkBg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            // Regain Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "StrictLock",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RegainTextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Mindful Screen Time & Hard Lock Engine",
                        fontSize = 13.sp,
                        color = RegainTextMuted
                    )
                }
            }
        }

        // STRICT MODE FEATURE AT THE VERY FIRST OF THE PAGE
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = RegainSurface),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isStrictModeLocked) RegainCrimson.copy(alpha = 0.5f) else RegainMint.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Header Row with Status Badge
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isStrictModeLocked) RegainCrimson.copy(alpha = 0.15f)
                                        else RegainMint.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isStrictModeLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                    contentDescription = null,
                                    tint = if (isStrictModeLocked) RegainCrimson else RegainMint,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "STRICT MODE",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    color = RegainTextPrimary
                                )
                                Text(
                                    text = if (isStrictModeLocked) "Anti-Uninstall Enforced" else "Safe to Uninstall",
                                    fontSize = 11.sp,
                                    color = if (isStrictModeLocked) RegainCrimson else RegainMintLight
                                )
                            }
                        }

                        // Status Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isStrictModeLocked) RegainCrimson.copy(alpha = 0.15f)
                                    else RegainMint.copy(alpha = 0.15f)
                                )
                                .border(
                                    1.dp,
                                    if (isStrictModeLocked) RegainCrimson.copy(alpha = 0.4f)
                                    else RegainMint.copy(alpha = 0.4f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isStrictModeLocked) Icons.Default.Lock else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (isStrictModeLocked) RegainCrimson else RegainMint,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (isStrictModeLocked) "LOCKED" else "UNLOCKED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isStrictModeLocked) RegainCrimson else RegainMintLight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description text
                    Text(
                        text = if (isStrictModeLocked) {
                            if (strictAppsCount > 0)
                                "$strictAppsCount app(s) are actively locked under strict plan. Anti-uninstall and Device Admin protections are strictly engaged."
                            else
                                "Strict Mode Plan is active. Uninstallation and settings tampering are blocked until turned off."
                        } else {
                            "No apps are currently under a strict plan. Device Admin and anti-uninstall protections are released; StrictLock can be uninstalled freely."
                        },
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = RegainTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Toggle & Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Strict Mode Protection",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = RegainTextPrimary
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
                                checkedThumbColor = RegainMint,
                                checkedTrackColor = RegainSurfaceVariant,
                                uncheckedThumbColor = RegainTextMuted,
                                uncheckedTrackColor = RegainSurfaceVariant
                            )
                        )
                    }

                    // If safe to uninstall, provide direct button
                    if (!isStrictModeLocked) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.triggerAppUninstall(context) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RegainSurfaceVariant,
                                contentColor = RegainCrimson
                            )
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

        // Hero Screen Time Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = RegainSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, RegainCardBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RegainSurface)
                        .padding(24.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = RegainMint,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "SCREEN TIME TODAY",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RegainMintLight,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${totalHours}h ${totalMins}m",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                color = RegainTextPrimary,
                                letterSpacing = (-1).sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Compared to an unconstrained 4h baseline.",
                            fontSize = 13.sp,
                            color = RegainTextSecondary
                        )
                    }
                }
            }
        }

        // 2-Column Metrics (Time Saved & Pickups)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Time Saved Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = RegainSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RegainCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = RegainMint,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Time Saved",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RegainTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "+${savedHours}h ${savedMins}m",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = RegainMintLight
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Earned back today",
                            fontSize = 11.sp,
                            color = RegainTextMuted
                        )
                    }
                }

                // Pickups Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = RegainSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RegainCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = RegainAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Pickups",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = RegainTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$pickups",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = RegainTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Device unlocks today",
                            fontSize = 11.sp,
                            color = RegainTextMuted
                        )
                    }
                }
            }
        }

        // Daily Activity Bar Graph Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = RegainSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, RegainCardBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "HOURLY INTENSITY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RegainTextMuted,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val maxMins = hourlyUsage.maxOfOrNull { it.minutesUsed }?.coerceAtLeast(10) ?: 60

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        hourlyUsage.forEach { item ->
                            val heightFraction = (item.minutesUsed.toFloat() / maxMins).coerceIn(0.1f, 1f)
                            val animatedHeight by animateFloatAsState(
                                targetValue = heightFraction,
                                animationSpec = tween(durationMillis = 600)
                            )

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height((70 * animatedHeight).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            if (animatedHeight > 0.5f) RegainMint else RegainSurfaceVariant
                                        )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.hourLabel,
                                    fontSize = 10.sp,
                                    color = RegainTextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Top Distracting Apps Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Used Apps",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = RegainTextPrimary
                )
                Text(
                    text = "${topApps.size} tracked",
                    fontSize = 12.sp,
                    color = RegainTextMuted
                )
            }
        }

        // Top Apps List
        if (topApps.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(RegainSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Grant Usage Access in Permissions tab to load detailed app breakdowns.",
                        fontSize = 13.sp,
                        color = RegainTextMuted,
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
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RegainSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RegainCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconImage(
                            packageName = app.packageName,
                            appName = app.appName,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = app.appName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RegainTextPrimary
                                )
                                Text(
                                    text = timeString,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = RegainMintLight
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { app.percentageOfTotal },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = RegainMint,
                                trackColor = RegainSurfaceVariant
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
