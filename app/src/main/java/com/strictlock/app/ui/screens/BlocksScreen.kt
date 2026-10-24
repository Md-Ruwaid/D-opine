package com.strictlock.app.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.ui.components.AppIconImage
import com.strictlock.app.ui.theme.RegainAmber
import com.strictlock.app.ui.theme.RegainCardBorder
import com.strictlock.app.ui.theme.RegainCrimson
import com.strictlock.app.ui.theme.RegainDarkBg
import com.strictlock.app.ui.theme.RegainMint
import com.strictlock.app.ui.theme.RegainSurface
import com.strictlock.app.ui.theme.RegainSurfaceVariant
import com.strictlock.app.ui.theme.RegainTextMuted
import com.strictlock.app.ui.theme.RegainTextPrimary
import com.strictlock.app.ui.theme.RegainTextSecondary
import com.strictlock.app.ui.viewmodel.MainViewModel

@Composable
fun BlocksScreen(viewModel: MainViewModel) {
    val settings by viewModel.regainSettings.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()

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
                text = "Distraction Blocks",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RegainTextPrimary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = "Mindful friction and strict mode anti-tamper shields",
                fontSize = 13.sp,
                color = RegainTextMuted
            )
        }

        // Section: In-App Smart Shields
        item {
            Text(
                text = "IN-APP SHIELDS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = RegainTextMuted,
                letterSpacing = 1.sp
            )
        }

        item {
            BlockSettingCard(
                icon = Icons.Default.Videocam,
                iconTint = RegainCrimson,
                title = "Block Shorts & Reels",
                description = "Scrapes screen to immediately close Instagram Reels and YouTube Shorts doomscrolling feeds.",
                checked = settings.blockShortsEnabled,
                onCheckedChange = { viewModel.toggleBlockShorts() }
            )
        }

        item {
            BlockSettingCard(
                icon = Icons.Default.Air,
                iconTint = RegainMint,
                title = "Mindful Pause & Breathing",
                description = "Shows an expanding breathing circle (5s) before opening distracting apps to break automatic habits.",
                checked = settings.mindfulPauseEnabled,
                onCheckedChange = { viewModel.toggleMindfulPause() }
            )
        }

        // Section: Strict Mode Protections (Regain's Core Anti-Tamper)
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = RegainMint,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "REGAIN STRICT MODE PROTECTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RegainMint,
                    letterSpacing = 1.sp
                )
            }
        }

        item {
            BlockSettingCard(
                icon = Icons.Default.Lock,
                iconTint = RegainMint,
                title = "Uninstall Protection",
                description = "Uses Device Admin & Accessibility to immediately close Android Settings screens for Uninstall, Force Stop, and Clear Data.",
                checked = settings.uninstallProtectionEnabled,
                onCheckedChange = { viewModel.toggleUninstallProtection() }
            )
        }

        item {
            BlockSettingCard(
                icon = Icons.Default.Splitscreen,
                iconTint = RegainAmber,
                title = "Split-Screen Protection",
                description = "Closes split-screen multitasking attempts used to sneak access to blocked apps.",
                checked = settings.splitScreenProtectionEnabled,
                onCheckedChange = { viewModel.toggleSplitScreenProtection() }
            )
        }

        item {
            BlockSettingCard(
                icon = Icons.Default.PictureInPicture,
                iconTint = RegainAmber,
                title = "Floating Window Protection",
                description = "Blocks picture-in-picture and floating bubbles for restricted apps.",
                checked = settings.floatingWindowProtectionEnabled,
                onCheckedChange = { viewModel.toggleFloatingWindowProtection() }
            )
        }

        item {
            BlockSettingCard(
                icon = Icons.Default.HourglassBottom,
                iconTint = RegainCrimson,
                title = "No 'Just 5 Minutes' Bypasses",
                description = "Completely disables extension buttons when time is up. Once your daily limit is hit, the app remains blocked until midnight.",
                checked = settings.noGiveUpStrictLimitEnabled,
                onCheckedChange = { viewModel.toggleNoGiveUp() }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BlockSettingCard(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = RegainTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = RegainTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = RegainMint,
                    checkedTrackColor = RegainMint.copy(alpha = 0.25f),
                    uncheckedThumbColor = RegainTextMuted,
                    uncheckedTrackColor = RegainSurfaceVariant
                )
            )
        }
    }
}
