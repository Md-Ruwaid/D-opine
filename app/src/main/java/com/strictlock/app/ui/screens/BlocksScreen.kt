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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.strictlock.app.ui.haptics.NeuroHapticEngine
import kotlinx.coroutines.launch
import com.strictlock.app.ui.theme.*
import com.strictlock.app.ui.viewmodel.MainViewModel

@Composable
fun BlocksScreen(viewModel: MainViewModel) {
    val settings by viewModel.regainSettings.collectAsState()
    val isStrictModeLocked by viewModel.isStrictModeLocked.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section Header
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Distraction Shields",
                fontSize = 28.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.SemiBold,
                color = AlabasterText,
                letterSpacing = (-0.25).sp
            )
            Text(
                text = "Tactile defense barriers & autonomic friction",
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MutedSlateText
            )
        }

        // =========================================================================
        // CATEGORY 1: SHORT-FORM DOOMSCROLL SHIELDS
        // =========================================================================
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ControlledCrimson)
                )
                Text(
                    text = "SHORT-FORM DOOMSCROLL SHIELDS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AlabasterText,
                    letterSpacing = 0.8.sp
                )
            }
        }

        item {
            ArmoredVaultCard(
                icon = Icons.Default.Videocam,
                iconTint = ControlledCrimson,
                title = "Block Reels & Shorts",
                description = "Scrapes accessibility hierarchy to immediately dismiss Instagram Reels & YouTube Shorts doomscrolling feeds.",
                checked = settings.blockShortsEnabled,
                onCheckedChange = { viewModel.toggleBlockShorts() },
                isStrictLocked = false
            )
        }

        // =========================================================================
        // CATEGORY 2: MINDFUL FRICTION & WINDOW GUARDS
        // =========================================================================
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ParasympatheticSage)
                )
                Text(
                    text = "MINDFUL FRICTION & WINDOW GUARDS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AlabasterText,
                    letterSpacing = 0.8.sp
                )
            }
        }

        item {
            ArmoredVaultCard(
                icon = Icons.Default.Air,
                iconTint = ParasympatheticSage,
                title = "Mindful Pause & Breathing",
                description = "Interposes an expanding 5-second vagus nerve breathing circle before launch to interrupt unconscious app opening.",
                checked = settings.mindfulPauseEnabled,
                onCheckedChange = { viewModel.toggleMindfulPause() },
                isStrictLocked = false
            )
        }

        item {
            ArmoredVaultCard(
                icon = Icons.Default.Lock,
                iconTint = AuthorityIndigo,
                title = "Uninstall Protection",
                description = "Uses Device Administrator & Accessibility to immediately close system settings pages for Uninstall, Force Stop, and Clear Data.",
                checked = settings.uninstallProtectionEnabled,
                onCheckedChange = { viewModel.toggleUninstallProtection() },
                isStrictLocked = isStrictModeLocked,
                strictLockExplanation = "Protected by active strict rule"
            )
        }

        item {
            ArmoredVaultCard(
                icon = Icons.Default.Splitscreen,
                iconTint = MindfulAmber,
                title = "Split-Screen Protection",
                description = "Blocks split-screen multitasking attempts used to sneak access to restricted applications.",
                checked = settings.splitScreenProtectionEnabled,
                onCheckedChange = { viewModel.toggleSplitScreenProtection() },
                isStrictLocked = false
            )
        }

        item {
            ArmoredVaultCard(
                icon = Icons.Default.PictureInPicture,
                iconTint = MindfulAmber,
                title = "Floating Window / PiP Shield",
                description = "Prevents restricted applications from running in picture-in-picture frames or floating bubbles.",
                checked = settings.floatingWindowProtectionEnabled,
                onCheckedChange = { viewModel.toggleFloatingWindowProtection() },
                isStrictLocked = false
            )
        }

        item {
            ArmoredVaultCard(
                icon = Icons.Default.HourglassBottom,
                iconTint = ControlledCrimson,
                title = "No 'Just 5 Minutes' Policy",
                description = "Completely eliminates post-limit extension prompts. When your daily operating budget expires, apps remain locked until midnight.",
                checked = settings.noGiveUpStrictLimitEnabled,
                onCheckedChange = { viewModel.toggleNoGiveUp() },
                isStrictLocked = isStrictModeLocked,
                strictLockExplanation = "Enforced in strict mode"
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Armored Vault Card UI Component
 * Tactile metaphor: Gestalt border, soft 8% alpha tinted container, clear status micro-badge (ARMED vs STANDBY),
 * mechanical shield arming spring, and tactile Padlock Pill Badge with shake rejection when locked by Strict Mode.
 */
@Composable
private fun ArmoredVaultCard(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isStrictLocked: Boolean = false,
    strictLockExplanation: String? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val shakeOffset = remember { Animatable(0f) }
    val badgeScale = remember { Animatable(1f) }

    val targetBorderColor = when {
        isStrictLocked -> AuthorityIndigo.copy(alpha = 0.5f)
        checked -> iconTint.copy(alpha = 0.4f)
        else -> SubtleHairline
    }
    val animatedBorderColor by animateColorAsState(
        targetValue = targetBorderColor,
        animationSpec = tween(250),
        label = "vaultBorderColor"
    )

    val targetContainerColor = if (checked) ElevatedSlate else ElevatedSlate.copy(alpha = 0.7f)
    val animatedContainerColor by animateColorAsState(
        targetValue = targetContainerColor,
        animationSpec = tween(250),
        label = "vaultContainerColor"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (checked) 1.0f else 0.90f,
        animationSpec = MotionPhysics.iosVaultSpring,
        label = "vaultIconScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationX = shakeOffset.value
            }
            .then(
                if (isStrictLocked) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        NeuroHapticEngine.warningReject(context)
                        scope.launch {
                            badgeScale.animateTo(1.12f, tween(80))
                            badgeScale.animateTo(1.0f, MotionPhysics.iosSnappySpring)
                        }
                        scope.launch {
                            shakeOffset.snapTo(0f)
                            shakeOffset.animateTo(
                                targetValue = 0f,
                                animationSpec = keyframes {
                                    durationMillis = 320
                                    0f at 0
                                    -10f at 50
                                    10f at 100
                                    -7f at 160
                                    7f at 220
                                    -3f at 270
                                    0f at 320
                                }
                            )
                        }
                    }
                } else {
                    Modifier.iosTactilePress()
                }
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = animatedContainerColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, animatedBorderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tactile 8% alpha tinted icon container with Spring Scale
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .graphicsLayer {
                            scaleX = iconScale
                            scaleY = iconScale
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (checked) iconTint.copy(alpha = 0.12f)
                            else InteractiveSurface
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (checked) iconTint else DimSlateText,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlabasterText
                        )

                        // Status Micro-badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (checked) iconTint.copy(alpha = 0.12f)
                                    else InteractiveSurface
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (checked) "ARMED" else "STANDBY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (checked) iconTint else DimSlateText,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = description,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MutedSlateText
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Control: Either tactile Padlock Pill Badge or Standard Switch
                if (isStrictLocked) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = badgeScale.value
                                scaleY = badgeScale.value
                            }
                            .clip(RoundedCornerShape(10.dp))
                            .background(AuthorityIndigo.copy(alpha = 0.15f))
                            .border(1.dp, AuthorityIndigo.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Strictly Locked",
                                tint = AuthorityIndigo,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "LOCKED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AuthorityIndigo,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                } else {
                    Switch(
                        checked = checked,
                        onCheckedChange = { newChecked ->
                            NeuroHapticEngine.mediumImpact(context)
                            onCheckedChange(newChecked)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = iconTint,
                            checkedTrackColor = iconTint.copy(alpha = 0.22f),
                            uncheckedThumbColor = DimSlateText,
                            uncheckedTrackColor = InteractiveSurface
                        )
                    )
                }
            }

            if (isStrictLocked && strictLockExplanation != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = AuthorityIndigo,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = strictLockExplanation,
                        fontSize = 11.sp,
                        color = AuthorityIndigo
                    )
                }
            }
        }
    }
}
