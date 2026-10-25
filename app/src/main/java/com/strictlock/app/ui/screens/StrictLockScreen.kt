package com.strictlock.app.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.data.model.RuleMode
import com.strictlock.app.data.repository.AppInfo
import com.strictlock.app.data.repository.UnlockResult
import com.strictlock.app.ui.components.AppIconImage
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
import com.strictlock.app.ui.viewmodel.ActiveRuleItem
import com.strictlock.app.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrictLockScreen(viewModel: MainViewModel) {
    val activeRules by viewModel.activeRules.collectAsState()
    val isTampered by viewModel.isTampered.collectAsState()
    val installedApps by viewModel.installedApps.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var showAddRuleSheet by remember { mutableStateOf(false) }
    var casualUnlockTargetRule by remember { mutableStateOf<ActiveRuleItem?>(null) }
    var casualCodeInput by remember { mutableStateOf("") }
    var casualErrorMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(RegainDarkBg)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
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
                            text = "Custom Multi-Day Rules & Hard Engine",
                            fontSize = 13.sp,
                            color = RegainTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(RegainMint.copy(alpha = 0.15f))
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
                                tint = RegainMint,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "New Rule",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RegainMintLight
                            )
                        }
                    }
                }
            }

            // Clock Tamper Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = RegainSurface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isTampered) RegainCrimson else RegainCardBorder
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
                                .clip(CircleShape)
                                .background(if (isTampered) RegainCrimson.copy(alpha = 0.2f) else RegainMint.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isTampered) Icons.Default.Warning else Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (isTampered) RegainCrimson else RegainMint,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isTampered) "Clock Tampering Flagged" else "Clock Anti-Tamper Shield",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTampered) RegainCrimson else RegainTextPrimary
                            )
                            Text(
                                text = if (isTampered)
                                    "Wall clock skewed against hardware elapsed realtime. Strict rules locked."
                                else
                                    "Hardware elapsed time verified against wall clock.",
                                fontSize = 12.sp,
                                color = RegainTextSecondary
                            )
                        }
                    }
                }
            }

            // Rules List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE RULES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RegainTextMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${activeRules.size} active",
                        fontSize = 12.sp,
                        color = RegainTextMuted
                    )
                }
            }

            if (activeRules.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(RegainSurface)
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = RegainTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No StrictLock Rules Active",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RegainTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap 'New Rule' above to create a STRICT or CASUAL multi-day app limit.",
                                fontSize = 13.sp,
                                color = RegainTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(activeRules) { item ->
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

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Modal Sheet for Adding New Rule
    if (showAddRuleSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddRuleSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = RegainSurface
        ) {
            AddRuleSheetContent(
                installedApps = installedApps,
                onDismiss = { showAddRuleSheet = false },
                onConfirm = { pkg, appName, limitMins, days, mode ->
                    viewModel.createRule(pkg, appName, limitMins, days, mode)
                    showAddRuleSheet = false
                }
            )
        }
    }

    // Casual Rule Unlock Dialog
    if (casualUnlockTargetRule != null) {
        val target = casualUnlockTargetRule!!
        val remainingMs = target.casualRemainingMs ?: 0L
        val waitMinutes = remainingMs / (60 * 1000)
        val waitSeconds = (remainingMs % (60 * 1000)) / 1000
        val isCooldownFinished = remainingMs <= 0L

        AlertDialog(
            onDismissRequest = { casualUnlockTargetRule = null },
            containerColor = RegainSurface,
            title = {
                Text(
                    text = "Unlock Casual Rule: ${target.rule.appName}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = RegainTextPrimary
                )
            },
            text = {
                Column {
                    if (!isCooldownFinished) {
                        Text(
                            text = "A mandatory 10-minute cooldown is active to break impulsive urges.",
                            fontSize = 13.sp,
                            color = RegainTextSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(RegainAmber.copy(alpha = 0.15f))
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = String.format("Wait time remaining: %02d:%02d", waitMinutes, waitSeconds),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RegainAmber,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else {
                        Text(
                            text = "Cooldown complete! Type the verification code below to permanently delete this rule:",
                            fontSize = 13.sp,
                            color = RegainTextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(RegainSurfaceVariant)
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = target.rule.unlockCode ?: "UNLOCK",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = RegainMintLight,
                                letterSpacing = 3.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = casualCodeInput,
                            onValueChange = { casualCodeInput = it },
                            placeholder = { Text("Type exact verification code") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RegainMint,
                                unfocusedBorderColor = RegainCardBorder,
                                focusedTextColor = RegainTextPrimary
                            )
                        )

                        if (casualErrorMessage != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = casualErrorMessage!!,
                                fontSize = 12.sp,
                                color = RegainCrimson
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
                                    casualUnlockTargetRule = null
                                } else if (result is UnlockResult.CodeMismatch || result is UnlockResult.IncorrectCode) {
                                    casualErrorMessage = "Code does not match. Try again."
                                } else if (result is UnlockResult.TimeNotElapsed) {
                                    casualErrorMessage = "10 minutes have not elapsed yet."
                                } else {
                                    casualErrorMessage = "Unlock failed. Please try again."
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RegainMint, contentColor = RegainDarkBg)
                    ) {
                        Text("Verify & Unlock")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { casualUnlockTargetRule = null }) {
                    Text("Cancel", color = RegainTextMuted)
                }
            }
        )
    }
}

@Composable
private fun RuleItemCard(
    item: ActiveRuleItem,
    onUnlockCasualClick: () -> Unit
) {
    val rule = item.rule
    val isStrict = rule.isStrict
    val progress = (item.usedMinutesToday.toFloat() / item.limitMinutes.coerceAtLeast(1L)).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = RegainSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isBlocked) RegainCrimson else RegainCardBorder
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
                        .size(44.dp)
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
                            color = RegainTextPrimary
                        )

                        // Mode Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isStrict) RegainCrimson.copy(alpha = 0.2f) else RegainAmber.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isStrict) "STRICT" else "CASUAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isStrict) RegainCrimson else RegainAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = RegainTextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "${item.daysLeft} calendar days remaining",
                            fontSize = 12.sp,
                            color = RegainTextSecondary
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
                    text = "Usage Today: ${item.usedMinutesToday}m / ${item.limitMinutes}m",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (item.isBlocked) RegainCrimson else RegainTextPrimary
                )

                if (item.isBlocked) {
                    Text(
                        text = "BLOCKED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RegainCrimson
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
                color = if (item.isBlocked) RegainCrimson else RegainMint,
                trackColor = RegainSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer action or permanent strict status
            if (isStrict) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = RegainTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Strict Mode: Non-editable & cannot be uninstalled.",
                        fontSize = 11.sp,
                        color = RegainTextMuted
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Casual: Requires 10m wait + code.",
                        fontSize = 11.sp,
                        color = RegainTextMuted
                    )

                    Button(
                        onClick = onUnlockCasualClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RegainSurfaceVariant,
                            contentColor = RegainAmber
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

@Composable
private fun AddRuleSheetContent(
    installedApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onConfirm: (pkg: String, name: String, limitMins: Long, days: Int, mode: RuleMode) -> Unit
) {
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }
    var hours by remember { mutableIntStateOf(0) }
    var minutes by remember { mutableIntStateOf(30) }
    var days by remember { mutableIntStateOf(14) }
    var selectedMode by remember { mutableStateOf(RuleMode.STRICT) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) installedApps
        else installedApps.filter { it.appName.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Create Rule",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RegainTextPrimary
        )
        Text(
            text = "Enforce hard daily budgets for calendar days",
            fontSize = 13.sp,
            color = RegainTextMuted
        )

        Spacer(modifier = Modifier.height(18.dp))

        if (selectedApp == null) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search installed apps") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RegainMint,
                    unfocusedBorderColor = RegainCardBorder,
                    focusedTextColor = RegainTextPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.height(260.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredApps) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(RegainSurfaceVariant)
                            .clickable { selectedApp = app }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconImage(
                            packageName = app.packageName,
                            appName = app.appName,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = app.appName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RegainTextPrimary
                        )
                    }
                }
            }
        } else {
            // Selected App Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(RegainSurfaceVariant)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIconImage(
                    packageName = selectedApp!!.packageName,
                    appName = selectedApp!!.appName,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedApp!!.appName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RegainTextPrimary
                    )
                    Text(
                        text = selectedApp!!.packageName,
                        fontSize = 11.sp,
                        color = RegainTextMuted
                    )
                }
                TextButton(onClick = { selectedApp = null }) {
                    Text("Change", color = RegainMintLight)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Daily Limit Selection with Slider (1 min to 1 Day / 1440m)
            var totalLimitMinsFloat by remember {
                val initMins = (hours * 60 + minutes).coerceIn(1, 1440)
                mutableFloatStateOf(initMins.toFloat())
            }
            val currentMinsInt = totalLimitMinsFloat.toInt().coerceIn(1, 1440)
            val budgetLabel = when {
                currentMinsInt == 1440 -> "1 Day (24 hrs)"
                currentMinsInt < 60 -> "$currentMinsInt min"
                currentMinsInt % 60 == 0 -> "${currentMinsInt / 60} hrs"
                else -> "${currentMinsInt / 60}h ${currentMinsInt % 60}m"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAILY BUDGET",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RegainMintLight,
                    letterSpacing = 1.sp
                )
                Text(
                    text = budgetLabel,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RegainMint
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = totalLimitMinsFloat,
                onValueChange = { newVal ->
                    totalLimitMinsFloat = newVal
                    val intMins = newVal.toInt().coerceIn(1, 1440)
                    hours = intMins / 60
                    minutes = intMins % 60
                },
                valueRange = 1f..1440f,
                colors = SliderDefaults.colors(
                    thumbColor = RegainMint,
                    activeTrackColor = RegainMint,
                    inactiveTrackColor = RegainSurfaceVariant
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "1 min", fontSize = 11.sp, color = RegainTextMuted)
                Text(text = "1 Day (1440m)", fontSize = 11.sp, color = RegainTextMuted)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Preset Chips for Daily Budget
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(1, 5, 15, 30, 60, 120, 240, 1440).forEach { mins ->
                    val isSelected = currentMinsInt == mins
                    val chipText = when (mins) {
                        1440 -> "1d"
                        in 60..1439 -> "${mins / 60}h"
                        else -> "${mins}m"
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) RegainMint.copy(alpha = 0.2f) else RegainSurfaceVariant)
                            .border(1.dp, if (isSelected) RegainMint else RegainCardBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                totalLimitMinsFloat = mins.toFloat()
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
                            color = if (isSelected) RegainMintLight else RegainTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Calendar Days Selection with Slider (1 to 365 days)
            var daysFloat by remember { mutableFloatStateOf(days.toFloat().coerceIn(1f, 365f)) }
            val currentDaysInt = daysFloat.toInt().coerceIn(1, 365)
            val durationLabel = when (currentDaysInt) {
                1 -> "1 Calendar Day"
                365 -> "1 Year (365 Days)"
                else -> "$currentDaysInt Calendar Days"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DURATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = RegainMintLight,
                    letterSpacing = 1.sp
                )
                Text(
                    text = durationLabel,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = RegainMintLight
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Slider(
                value = daysFloat,
                onValueChange = { newVal ->
                    daysFloat = newVal
                    days = newVal.toInt().coerceIn(1, 365)
                },
                valueRange = 1f..365f,
                colors = SliderDefaults.colors(
                    thumbColor = RegainMint,
                    activeTrackColor = RegainMint,
                    inactiveTrackColor = RegainSurfaceVariant
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "1 Day", fontSize = 11.sp, color = RegainTextMuted)
                Text(text = "365 Days (1 Yr)", fontSize = 11.sp, color = RegainTextMuted)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(1, 7, 14, 30, 90, 180, 365).forEach { d ->
                    val isSelected = currentDaysInt == d
                    val chipText = if (d == 365) "1y" else "${d}d"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) RegainMint.copy(alpha = 0.2f) else RegainSurfaceVariant)
                            .border(1.dp, if (isSelected) RegainMint else RegainCardBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                daysFloat = d.toFloat()
                                days = d
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chipText,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) RegainMintLight else RegainTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Mode Selection
            Text(
                text = "ENFORCEMENT MODE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = RegainMintLight,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // STRICT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selectedMode == RuleMode.STRICT) RegainCrimson.copy(alpha = 0.15f) else RegainSurfaceVariant)
                        .border(
                            1.dp,
                            if (selectedMode == RuleMode.STRICT) RegainCrimson else RegainCardBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedMode = RuleMode.STRICT }
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "STRICT",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RegainCrimson
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Cannot be deleted. Device Admin & Settings block active.",
                            fontSize = 11.sp,
                            color = RegainTextSecondary
                        )
                    }
                }

                // CASUAL
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (selectedMode == RuleMode.CASUAL) RegainAmber.copy(alpha = 0.15f) else RegainSurfaceVariant)
                        .border(
                            1.dp,
                            if (selectedMode == RuleMode.CASUAL) RegainAmber else RegainCardBorder,
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedMode = RuleMode.CASUAL }
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = "CASUAL",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RegainAmber
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Requires 10-minute wait + long verification code to unlock.",
                            fontSize = 11.sp,
                            color = RegainTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val totalLimitMinutes = (hours * 60L) + minutes
                    if (totalLimitMinutes > 0 && selectedApp != null) {
                        onConfirm(
                            selectedApp!!.packageName,
                            selectedApp!!.appName,
                            totalLimitMinutes,
                            days,
                            selectedMode
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RegainMint,
                    contentColor = RegainDarkBg
                )
            ) {
                Text(
                    text = "LOCK RULE",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
