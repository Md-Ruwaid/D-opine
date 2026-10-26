package com.strictlock.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.data.model.RuleMode
import com.strictlock.app.data.repository.AppInfo
import com.strictlock.app.data.repository.UnlockResult
import com.strictlock.app.ui.components.AppIconImage
import com.strictlock.app.ui.theme.*
import com.strictlock.app.ui.viewmodel.ActiveRuleItem
import com.strictlock.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import com.strictlock.app.ui.haptics.NeuroHapticEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrictLockScreen(viewModel: MainViewModel) {
    val activeRules by viewModel.activeRules.collectAsState()
    val isTampered by viewModel.isTampered.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val emergencyTapCount by viewModel.emergencyTapCount.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var showAddRuleSheet by remember { mutableStateOf(false) }
    var casualUnlockTargetRule by remember { mutableStateOf<ActiveRuleItem?>(null) }
    var casualCodeInput by remember { mutableStateOf("") }
    var casualErrorMessage by remember { mutableStateOf<String?>(null) }
    val challengeShakeOffset = remember { Animatable(0f) }

    // 7-Tap Emergency Override Escalating Somatosensory Feedback
    LaunchedEffect(emergencyTapCount) {
        if (emergencyTapCount > 0) {
            NeuroHapticEngine.escalatingTap(context, emergencyTapCount)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepObsidian)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "StrictLock Engine",
                            fontSize = 28.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AlabasterText,
                            letterSpacing = (-0.25).sp
                        )
                        Text(
                            text = "Multi-day commitment contracts & anti-tamper shields",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MutedSlateText
                        )
                    }

                    // Create Rule Button (Bottom/Top accessible)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ParasympatheticSage.copy(alpha = 0.16f))
                            .border(1.dp, ParasympatheticSage.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable { showAddRuleSheet = true }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = ParasympatheticSage,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "New Lock",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ParasympatheticSageLight
                            )
                        }
                    }
                }
            }

            // Top Deliberate Zone: Hardware Clock Anti-Tamper Shield Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isTampered) ControlledCrimson else SubtleHairline
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isTampered) ControlledCrimson.copy(alpha = 0.18f)
                                    else AuthorityIndigo.copy(alpha = 0.16f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isTampered) Icons.Default.Warning else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (isTampered) ControlledCrimson else AuthorityIndigo,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isTampered) "Clock Tampering Flagged" else "Clock Anti-Tamper Armed",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTampered) ControlledCrimson else AlabasterText
                            )
                            Text(
                                text = if (isTampered)
                                    "Wall clock skewed against hardware elapsed realtime. Strict rules locked."
                                else
                                    "Monotonic hardware timer calibrated against wall clock.",
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = MutedSlateText
                            )
                        }
                    }
                }
            }

            // Active Rules Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE COMMITMENT CONTRACTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedSlateText,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "${activeRules.size} active",
                        fontSize = 12.sp,
                        color = MutedSlateText,
                        style = TabularNumberStyle
                    )
                }
            }

            // Empty State
            if (activeRules.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(ElevatedSlate)
                            .border(1.dp, SubtleHairline, RoundedCornerShape(20.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = DimSlateText,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No StrictLock Rules Active",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlabasterText
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap 'New Lock' to create a STRICT or CASUAL multi-day app limit.",
                                fontSize = 13.sp,
                                color = MutedSlateText,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(activeRules, key = { it.rule.id }) { item ->
                    RuleItemCard(
                        item = item,
                        onUnlockCasualClick = {
                            casualUnlockTargetRule = item
                            casualCodeInput = ""
                            casualErrorMessage = null
                            if (item.rule.cooldownStartTime == null) {
                                viewModel.startCasualCooldown(item.rule.id)
                            }
                        }
                    )
                }
            }

            // High-Consequence Top/Bottom Emergency Exit Feedback
            item {
                Spacer(modifier = Modifier.height(10.dp))
                if (emergencyTapCount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Emergency sequence: ",
                            fontSize = 11.sp,
                            color = MutedSlateText
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (i in 1..7) {
                                val isFilled = i <= emergencyTapCount
                                val dotScale by animateFloatAsState(
                                    targetValue = if (isFilled) 1.25f else 1.0f,
                                    animationSpec = MotionPhysics.iosSnappySpring,
                                    label = "dotScale_$i"
                                )
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .graphicsLayer {
                                            scaleX = dotScale
                                            scaleY = dotScale
                                        }
                                        .clip(CircleShape)
                                        .background(
                                            if (isFilled) ControlledCrimson else InteractiveSurface
                                        )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Modal Sheet for Creating Rule (3-Step Commitment Contract)
    if (showAddRuleSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddRuleSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ElevatedSlate
        ) {
            AddRuleSheetContent(
                installedApps = installedApps,
                onDismiss = { showAddRuleSheet = false },
                onConfirm = { apps, limitMins, days, mode ->
                    if (mode == RuleMode.STRICT) {
                        NeuroHapticEngine.heavyVaultLock(context)
                    } else {
                        NeuroHapticEngine.mediumImpact(context)
                    }
                    viewModel.createRules(apps, limitMins, days, mode)
                    showAddRuleSheet = false
                }
            )
        }
    }

    // Casual Rule Unlock Dialog with 10-Minute Cooldown & Verification Challenge
    if (casualUnlockTargetRule != null) {
        val target = casualUnlockTargetRule!!
        val remainingMs = target.casualRemainingMs ?: 0L
        val waitMinutes = remainingMs / (60 * 1000)
        val waitSeconds = (remainingMs % (60 * 1000)) / 1000
        val isCooldownFinished = remainingMs <= 0L

        AlertDialog(
            onDismissRequest = { casualUnlockTargetRule = null },
            containerColor = ElevatedSlate,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = MindfulAmber,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Unlock: ${target.rule.appName}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlabasterText
                    )
                }
            },
            text = {
                Column {
                    if (!isCooldownFinished) {
                        Text(
                            text = "A mandatory 10-minute cooldown is active to break impulsive dopamine loops. Take a breath and reflect.",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MutedSlateText
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MindfulAmber.copy(alpha = 0.12f))
                                .border(1.dp, MindfulAmber.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = String.format("Wait time remaining: %02d:%02d", waitMinutes, waitSeconds),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MindfulAmber,
                                fontFamily = FontFamily.Monospace,
                                style = TabularNumberStyle
                            )
                        }
                    } else {
                        Text(
                            text = "Cooldown complete. Type the verification challenge below to confirm rule deletion:",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MutedSlateText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(InteractiveSurface)
                                .border(1.dp, SubtleHairline, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = target.rule.unlockCode ?: "UNLOCK",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = ParasympatheticSageLight,
                                letterSpacing = 3.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = casualCodeInput,
                            onValueChange = { casualCodeInput = it },
                            placeholder = { Text("Type exact verification code", color = DimSlateText) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    translationX = challengeShakeOffset.value
                                },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ParasympatheticSage,
                                unfocusedBorderColor = SubtleHairline,
                                focusedTextColor = AlabasterText,
                                unfocusedTextColor = AlabasterText,
                                focusedContainerColor = InteractiveSurface,
                                unfocusedContainerColor = InteractiveSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        if (casualErrorMessage != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = casualErrorMessage!!,
                                fontSize = 12.sp,
                                color = ControlledCrimson
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (isCooldownFinished) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val result = viewModel.verifyAndUnlockCasualRule(target.rule.id, casualCodeInput.trim())
                                if (result is UnlockResult.Success) {
                                    NeuroHapticEngine.mediumImpact(context)
                                    casualUnlockTargetRule = null
                                } else {
                                    NeuroHapticEngine.warningReject(context)
                                    challengeShakeOffset.snapTo(0f)
                                    challengeShakeOffset.animateTo(
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
                                    if (result is UnlockResult.CodeMismatch || result is UnlockResult.IncorrectCode) {
                                        casualErrorMessage = "Code does not match. Try again."
                                    } else if (result is UnlockResult.TimeNotElapsed) {
                                        casualErrorMessage = "10 minutes have not elapsed yet."
                                    } else {
                                        casualErrorMessage = "Unlock failed. Please try again."
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ParasympatheticSage,
                            contentColor = DeepObsidian
                        ),
                        modifier = Modifier.iosTactilePress(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Verify & Unlock", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { casualUnlockTargetRule = null }) {
                    Text("Cancel", color = MutedSlateText)
                }
            }
        )
    }
}

/**
 * Rule Item Card with Miller's law data point limit (Icon + Title, Mode Badge, Usage Bar)
 */
@Composable
private fun RuleItemCard(
    item: ActiveRuleItem,
    onUnlockCasualClick: () -> Unit
) {
    val rule = item.rule
    val isStrict = rule.isStrict
    val progress = if (item.limitMinutes == 0L) 1f else (item.usedMinutesToday.toFloat() / item.limitMinutes.coerceAtLeast(1L)).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ElevatedSlate),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isBlocked) ControlledCrimson.copy(alpha = 0.5f)
            else if (isStrict) AuthorityIndigo.copy(alpha = 0.35f)
            else SubtleHairline
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconImage(
                    packageName = rule.packageName,
                    appName = rule.appName,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = rule.appName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlabasterText
                        )

                        // Mode Micro-Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isStrict) AuthorityIndigo.copy(alpha = 0.18f)
                                    else MindfulAmber.copy(alpha = 0.16f)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isStrict) "STRICT" else "CASUAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isStrict) AuthorityIndigo else MindfulAmber,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MutedSlateText,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${item.daysLeft} calendar days remaining",
                            fontSize = 12.sp,
                            color = MutedSlateText,
                            style = TabularNumberStyle
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Usage vs Limit Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (item.limitMinutes == 0L) "Instant Lock: Blocked on launch" else "Used: ${item.usedMinutesToday}m / ${item.limitMinutes}m allowance",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.isBlocked) ControlledCrimson else AlabasterText,
                    style = TabularNumberStyle
                )

                if (item.isBlocked) {
                    Text(
                        text = "BLOCKED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ControlledCrimson,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (item.isBlocked) ControlledCrimson else ParasympatheticSage,
                trackColor = InteractiveSurface,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Action or Status
            if (isStrict) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = DimSlateText,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Strict Mode: Immutable contract until end date.",
                        fontSize = 11.sp,
                        color = DimSlateText
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Casual: 10m wait + verification code.",
                        fontSize = 11.sp,
                        color = DimSlateText
                    )

                    Button(
                        onClick = onUnlockCasualClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = InteractiveSurface,
                            contentColor = MindfulAmber
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (item.casualRemainingMs != null) "In Cooldown" else "Unlock",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 3-Step Commitment Contract Builder in Bottom Sheet
 */
@Composable
private fun AddRuleSheetContent(
    installedApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onConfirm: (apps: List<AppInfo>, limitMins: Long, days: Int, mode: RuleMode) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    var selectedApps by remember { mutableStateOf<Set<AppInfo>>(emptySet()) }
    var searchQuery by remember { mutableStateOf("") }

    var hours by remember { mutableIntStateOf(0) }
    var minutes by remember { mutableIntStateOf(30) }
    var days by remember { mutableIntStateOf(14) }
    var selectedMode by remember { mutableStateOf(RuleMode.STRICT) }

    val filteredApps = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) installedApps
        else installedApps.filter {
            it.appName.contains(searchQuery, ignoreCase = true) ||
                    it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    if (currentStep == 1) {
        // =========================================================================
        // CONTRACT STEP 1: TARGET APP SELECTION
        // =========================================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "1. Select Apps to Restrict",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AlabasterText
                    )
                    Text(
                        text = "Choose applications to include in this lock",
                        fontSize = 12.sp,
                        color = MutedSlateText
                    )
                }
                if (selectedApps.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ParasympatheticSage.copy(alpha = 0.16f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${selectedApps.size} selected",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ParasympatheticSageLight,
                            style = TabularNumberStyle
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search installed applications...", color = DimSlateText) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = MutedSlateText)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MutedSlateText)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ParasympatheticSage,
                    unfocusedBorderColor = SubtleHairline,
                    focusedTextColor = AlabasterText,
                    unfocusedTextColor = AlabasterText,
                    focusedContainerColor = InteractiveSurface,
                    unfocusedContainerColor = InteractiveSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredApps.size} apps available",
                    fontSize = 12.sp,
                    color = MutedSlateText,
                    style = TabularNumberStyle
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (selectedApps.isNotEmpty()) {
                        TextButton(
                            onClick = { selectedApps = emptySet() },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Clear All", fontSize = 12.sp, color = MutedSlateText)
                        }
                    }

                    val currentFiltered = filteredApps.toSet()
                    val allFilteredSelected = currentFiltered.isNotEmpty() && selectedApps.containsAll(currentFiltered)

                    TextButton(
                        onClick = {
                            selectedApps = if (allFilteredSelected) {
                                selectedApps - currentFiltered
                            } else {
                                selectedApps + currentFiltered
                            }
                        },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (allFilteredSelected) "Deselect Filtered" else "Select All Filtered",
                            fontSize = 12.sp,
                            color = ParasympatheticSageLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    val isSelected = selectedApps.contains(app)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) ParasympatheticSage.copy(alpha = 0.14f) else InteractiveSurface)
                            .border(
                                1.dp,
                                if (isSelected) ParasympatheticSage else SubtleHairline,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                selectedApps = if (isSelected) selectedApps - app else selectedApps + app
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                selectedApps = if (checked) selectedApps + app else selectedApps - app
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = ParasympatheticSage,
                                uncheckedColor = DimSlateText,
                                checkmarkColor = DeepObsidian
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        AppIconImage(
                            packageName = app.packageName,
                            appName = app.appName,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = app.appName,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlabasterText
                            )
                            Text(
                                text = app.packageName,
                                fontSize = 11.sp,
                                color = MutedSlateText,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Selected",
                                tint = ParasympatheticSage,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sticky 56dp CTA in thumb zone
            Button(
                onClick = { currentStep = 2 },
                enabled = selectedApps.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ParasympatheticSage,
                    contentColor = DeepObsidian,
                    disabledContainerColor = InteractiveSurface,
                    disabledContentColor = DimSlateText
                )
            ) {
                Text(
                    text = if (selectedApps.isEmpty())
                        "SELECT APPS TO CONTINUE"
                    else
                        "CONFIRM SELECTION (${selectedApps.size} ${if (selectedApps.size == 1) "APP" else "APPS"}) →",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    } else {
        // =========================================================================
        // CONTRACT STEP 2 & 3: ALLOWANCE, DURATION & COMMITMENT ARCHETYPES
        // =========================================================================
        val totalMinutes = hours * 60 + minutes

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentStep = 1 }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AlabasterText
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "2. Commitment Contract",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AlabasterText
                    )
                    Text(
                        text = "Daily allowance, duration & enforcement mode",
                        fontSize = 12.sp,
                        color = MutedSlateText
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(ParasympatheticSage.copy(alpha = 0.16f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${selectedApps.size} apps",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ParasympatheticSageLight,
                        style = TabularNumberStyle
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selected Apps Horizontal Avatar Preview
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedApps.toList()) { app ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(InteractiveSurface)
                            .border(1.dp, SubtleHairline, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconImage(
                            packageName = app.packageName,
                            appName = app.appName,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = app.appName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = AlabasterText
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = MutedSlateText,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable {
                                    selectedApps = selectedApps - app
                                    if (selectedApps.isEmpty()) currentStep = 1
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable Configuration Body
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section: Daily Operating Time
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DAILY OPERATING ALLOWANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedSlateText,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = if (totalMinutes == 0) "0 min (Instant Lock)" else if (hours > 0) "${hours}h ${minutes}m / day" else "${minutes}m / day",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (totalMinutes == 0) ControlledCrimson else ParasympatheticSageLight,
                            style = TabularNumberStyle
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Allowance Banner Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (totalMinutes == 0) ControlledCrimson.copy(alpha = 0.12f)
                                else ParasympatheticSage.copy(alpha = 0.12f)
                            )
                            .border(
                                1.dp,
                                if (totalMinutes == 0) ControlledCrimson else ParasympatheticSage,
                                RoundedCornerShape(14.dp)
                            )
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (totalMinutes == 0) Icons.Default.Lock else Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = if (totalMinutes == 0) ControlledCrimson else ParasympatheticSage,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (totalMinutes == 0)
                                        "Instant Lock (0m Operating Allowance)"
                                    else
                                        "${if (hours > 0) "${hours}h " else ""}${minutes}m Daily Allowance",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (totalMinutes == 0) ControlledCrimson else ParasympatheticSageLight
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (totalMinutes == 0)
                                        "Apps will be blocked immediately upon opening. Zero daily screen time."
                                    else
                                        "Apps operate freely until the daily limit expires. Resets at midnight.",
                                    fontSize = 11.sp,
                                    color = MutedSlateText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Steppers Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Hours
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(InteractiveSurface)
                                .border(1.dp, SubtleHairline, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("HOURS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DimSlateText)
                                    Text("${hours}h", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AlabasterText, style = TabularNumberStyle)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    IconButton(
                                        onClick = { if (hours > 0) hours -= 1 },
                                        enabled = hours > 0,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = if (hours > 0) AlabasterText else DimSlateText)
                                    }
                                    IconButton(
                                        onClick = { if (hours < 23) hours += 1 },
                                        enabled = hours < 23,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = if (hours < 23) AlabasterText else DimSlateText)
                                    }
                                }
                            }
                        }

                        // Minutes
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(InteractiveSurface)
                                .border(1.dp, SubtleHairline, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("MINUTES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DimSlateText)
                                    Text("${minutes}m", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AlabasterText, style = TabularNumberStyle)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    IconButton(
                                        onClick = {
                                            if (minutes >= 5) minutes -= 5
                                            else if (hours > 0) { hours -= 1; minutes = 55 }
                                            else { minutes = 0 }
                                        },
                                        enabled = hours > 0 || minutes > 0,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = if (hours > 0 || minutes > 0) AlabasterText else DimSlateText)
                                    }
                                    IconButton(
                                        onClick = {
                                            if (minutes + 5 <= 55) minutes += 5
                                            else if (hours < 23) { hours += 1; minutes = 0 }
                                        },
                                        enabled = hours < 23 || minutes < 55,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = if (hours < 23 || minutes < 55) AlabasterText else DimSlateText)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Slider(
                        value = totalMinutes.toFloat(),
                        onValueChange = { newVal ->
                            val m = newVal.toInt().coerceIn(0, 1440)
                            hours = m / 60
                            minutes = m % 60
                        },
                        valueRange = 0f..1440f,
                        colors = SliderDefaults.colors(
                            thumbColor = if (totalMinutes == 0) ControlledCrimson else ParasympatheticSage,
                            activeTrackColor = if (totalMinutes == 0) ControlledCrimson else ParasympatheticSage,
                            inactiveTrackColor = InteractiveSurface
                        )
                    )

                    // Quick Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0, 15, 30, 45, 60, 120, 240, 1440).forEach { mins ->
                            val isSelected = totalMinutes == mins
                            val chipText = when (mins) {
                                0 -> "0m"
                                1440 -> "24h"
                                in 60..1439 -> "${mins / 60}h"
                                else -> "${mins}m"
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) {
                                            if (mins == 0) ControlledCrimson.copy(alpha = 0.2f) else ParasympatheticSage.copy(alpha = 0.2f)
                                        } else InteractiveSurface
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) (if (mins == 0) ControlledCrimson else ParasympatheticSage) else SubtleHairline,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        hours = mins / 60
                                        minutes = mins % 60
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = chipText,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) {
                                        if (mins == 0) ControlledCrimson else ParasympatheticSageLight
                                    } else MutedSlateText
                                )
                            }
                        }
                    }
                }

                // Section: Lock Duration (Calendar Days)
                val durationLabel = when (days) {
                    1 -> "1 Calendar Day"
                    365 -> "365 Days (1 Year)"
                    else -> "$days Calendar Days"
                }

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONTRACT DURATION (CALENDAR DAYS)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MutedSlateText,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = durationLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ParasympatheticSageLight,
                            style = TabularNumberStyle
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = days.toFloat(),
                        onValueChange = { newVal ->
                            days = newVal.toInt().coerceIn(1, 365)
                        },
                        valueRange = 1f..365f,
                        colors = SliderDefaults.colors(
                            thumbColor = ParasympatheticSage,
                            activeTrackColor = ParasympatheticSage,
                            inactiveTrackColor = InteractiveSurface
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1, 3, 7, 14, 30, 90, 180, 365).forEach { d ->
                            val isSelected = days == d
                            val chipText = if (d == 365) "1y" else "${d}d"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ParasympatheticSage.copy(alpha = 0.2f) else InteractiveSurface)
                                    .border(1.dp, if (isSelected) ParasympatheticSage else SubtleHairline, RoundedCornerShape(8.dp))
                                    .clickable { days = d }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = chipText,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) ParasympatheticSageLight else MutedSlateText
                                )
                            }
                        }
                    }
                }

                // Section: Commitment Archetype Cards (STRICT vs CASUAL)
                Column {
                    Text(
                        text = "3. COMMITMENT LEVEL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MutedSlateText,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // STRICT ARCHETYPE
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (selectedMode == RuleMode.STRICT) AuthorityIndigo.copy(alpha = 0.16f)
                                    else InteractiveSurface
                                )
                                .border(
                                    1.dp,
                                    if (selectedMode == RuleMode.STRICT) AuthorityIndigo else SubtleHairline,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedMode = RuleMode.STRICT }
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = AuthorityIndigo,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "STRICT",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AuthorityIndigo
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Unbreakable contract. Device Admin & anti-uninstall armed until end date.",
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = MutedSlateText
                                )
                            }
                        }

                        // CASUAL ARCHETYPE
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (selectedMode == RuleMode.CASUAL) MindfulAmber.copy(alpha = 0.14f)
                                    else InteractiveSurface
                                )
                                .border(
                                    1.dp,
                                    if (selectedMode == RuleMode.CASUAL) MindfulAmber else SubtleHairline,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { selectedMode = RuleMode.CASUAL }
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LockOpen,
                                        contentDescription = null,
                                        tint = MindfulAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "CASUAL",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MindfulAmber
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Intentional friction. 10-minute wait + verification challenge to delete.",
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = MutedSlateText
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sticky Confirm 56dp CTA in natural thumb zone
            Button(
                onClick = {
                    val finalLimitMins = (hours * 60L + minutes)
                    if (selectedApps.isNotEmpty()) {
                        onConfirm(
                            selectedApps.toList(),
                            finalLimitMins,
                            days,
                            selectedMode
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedMode == RuleMode.STRICT) AuthorityIndigo else MindfulAmber,
                    contentColor = if (selectedMode == RuleMode.STRICT) AlabasterText else DeepObsidian
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "SEAL COMMITMENT (${selectedApps.size} APPS • $days DAYS)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Text(
                        text = if (totalMinutes == 0) "Instant Lock (0m allowance)" else "${if (hours > 0) "${hours}h " else ""}${minutes}m daily operating allowance",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}
