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
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(RegainDarkBg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            // Regain Header with Protection Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Regain",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RegainTextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Mindful Screen Time & Focus",
                        fontSize = 13.sp,
                        color = RegainTextMuted
                    )
                }

                // Status chip
                val isStrictActive = regainSettings.uninstallProtectionEnabled
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isStrictActive) RegainSurfaceVariant else RegainAmber.copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            if (isStrictActive) RegainMint.copy(alpha = 0.5f) else RegainAmber.copy(alpha = 0.5f),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isStrictActive) RegainMint else RegainAmber)
                        )
                        Text(
                            text = if (isStrictActive) "Strict Mode Active" else "Shields Partial",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isStrictActive) RegainMintLight else RegainAmber
                        )
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
                                imageVector = Icons.Default.TrendingUp,
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
