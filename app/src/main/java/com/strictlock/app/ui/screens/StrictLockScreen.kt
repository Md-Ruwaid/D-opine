package com.strictlock.app.ui.screens

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
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
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
                onConfirm = { apps, limitMins, days, mode ->
                    viewModel.createRules(apps, limitMins, days, mode)
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
    val progress = if (item.limitMinutes == 0L) 1f else (item.usedMinutesToday.toFloat() / item.limitMinutes.coerceAtLeast(1L)).coerceIn(0f, 1f)

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
                    text = if (item.limitMinutes == 0L) "Instant Lock: Blocked Immediately" else "Usage Today: ${item.usedMinutesToday}m / ${item.limitMinutes}m",
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Step 1 Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Select Apps to Lock",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RegainTextPrimary
                    )
                    Text(
                        text = "Choose apps you want to restrict",
                        fontSize = 12.sp,
                        color = RegainTextMuted
                    )
                }
                if (selectedApps.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(RegainMint.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${selectedApps.size} selected",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RegainMintLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search installed apps...", color = RegainTextMuted) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = RegainTextMuted)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = RegainTextMuted)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RegainMint,
                    unfocusedBorderColor = RegainCardBorder,
                    focusedTextColor = RegainTextPrimary,
                    unfocusedTextColor = RegainTextPrimary,
                    focusedContainerColor = RegainSurfaceVariant,
                    unfocusedContainerColor = RegainSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Select / Clear Filtered Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredApps.size} apps available",
                    fontSize = 12.sp,
                    color = RegainTextMuted
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (selectedApps.isNotEmpty()) {
                        TextButton(
                            onClick = { selectedApps = emptySet() },
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Clear All", fontSize = 12.sp, color = RegainTextMuted)
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
                            color = RegainMintLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Apps List taking remaining height
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
                            .background(if (isSelected) RegainMint.copy(alpha = 0.14f) else RegainSurfaceVariant)
                            .border(
                                1.dp,
                                if (isSelected) RegainMint else RegainCardBorder,
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
                                checkedColor = RegainMint,
                                uncheckedColor = RegainTextMuted,
                                checkmarkColor = RegainDarkBg
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
                                color = RegainTextPrimary
                            )
                            Text(
                                text = app.packageName,
                                fontSize = 11.sp,
                                color = RegainTextMuted,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Selected",
                                tint = RegainMint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // STICKY CONFIRM SELECTION BUTTON AT BOTTOM
            Button(
                onClick = { currentStep = 2 },
                enabled = selectedApps.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RegainMint,
                    contentColor = RegainDarkBg,
                    disabledContainerColor = RegainSurfaceVariant,
                    disabledContentColor = RegainTextMuted
                )
            ) {
                Text(
                    text = if (selectedApps.isEmpty())
                        "SELECT APPS TO CONTINUE"
                    else
                        "CONFIRM SELECTION (${selectedApps.size} ${if (selectedApps.size == 1) "APP" else "APPS"}) →",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    } else {
        // Step 2: Configure Lock Rules
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
                        tint = RegainMintLight
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lock Settings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = RegainTextPrimary
                    )
                    Text(
                        text = "Enforce hard budgets & anti-tamper protections",
                        fontSize = 12.sp,
                        color = RegainTextMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(RegainMint.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${selectedApps.size} apps",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RegainMintLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selected Apps Horizontal Preview
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedApps.toList()) { app ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(RegainSurfaceVariant)
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
                            color = RegainTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove",
                            tint = RegainTextMuted,
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
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Section 1: Daily Operating Time (Timer)
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DAILY OPERATING TIME (TIMER)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RegainMintLight,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (totalMinutes == 0) "0 min (Instant Block)" else if (hours > 0) "${hours}h ${minutes}m / day" else "${minutes}m / day",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (totalMinutes == 0) RegainCrimson else RegainMint
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Hero Banner Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (totalMinutes == 0) RegainCrimson.copy(alpha = 0.14f) else RegainMint.copy(alpha = 0.14f))
                            .border(
                                1.dp,
                                if (totalMinutes == 0) RegainCrimson else RegainMint,
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
                                tint = if (totalMinutes == 0) RegainCrimson else RegainMint,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = if (totalMinutes == 0)
                                        "Instant Lock (0m Operating Time Allowed)"
                                    else
                                        "${if (hours > 0) "${hours} Hour${if (hours > 1) "s" else ""} " else ""}${minutes} Minute${if (minutes != 1) "s" else ""} Daily Allowance",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (totalMinutes == 0) RegainCrimson else RegainMintLight
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (totalMinutes == 0)
                                        "Apps are blocked immediately whenever opened. 0 operating minutes."
                                    else
                                        "You can operate these apps up to this duration each day. Limits reset at midnight.",
                                    fontSize = 11.sp,
                                    color = RegainTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Hours & Minutes Steppers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Hours Stepper
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RegainSurfaceVariant)
                                .border(1.dp, RegainCardBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("HOURS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RegainTextMuted)
                                    Text("${hours}h", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = RegainTextPrimary)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    IconButton(
                                        onClick = { if (hours > 0) hours -= 1 },
                                        enabled = hours > 0,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease Hour", tint = if (hours > 0) RegainMintLight else RegainTextMuted)
                                    }
                                    IconButton(
                                        onClick = { if (hours < 23) hours += 1 },
                                        enabled = hours < 23,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase Hour", tint = if (hours < 23) RegainMintLight else RegainTextMuted)
                                    }
                                }
                            }
                        }

                        // Minutes Stepper
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RegainSurfaceVariant)
                                .border(1.dp, RegainCardBorder, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("MINUTES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RegainTextMuted)
                                    Text("${minutes}m", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = RegainTextPrimary)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                    IconButton(
                                        onClick = {
                                            if (minutes >= 5) minutes -= 5
                                            else if (hours > 0) {
                                                hours -= 1
                                                minutes = 55
                                            } else {
                                                minutes = 0
                                            }
                                        },
                                        enabled = hours > 0 || minutes > 0,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease Minute", tint = if (hours > 0 || minutes > 0) RegainMintLight else RegainTextMuted)
                                    }
                                    IconButton(
                                        onClick = {
                                            if (minutes + 5 <= 55) minutes += 5
                                            else if (hours < 23) {
                                                hours += 1
                                                minutes = 0
                                            }
                                        },
                                        enabled = hours < 23 || minutes < 55,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase Minute", tint = if (hours < 23 || minutes < 55) RegainMintLight else RegainTextMuted)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Smooth Slider
                    Slider(
                        value = totalMinutes.toFloat(),
                        onValueChange = { newVal ->
                            val m = newVal.toInt().coerceIn(0, 1440)
                            hours = m / 60
                            minutes = m % 60
                        },
                        valueRange = 0f..1440f,
                        colors = SliderDefaults.colors(
                            thumbColor = if (totalMinutes == 0) RegainCrimson else RegainMint,
                            activeTrackColor = if (totalMinutes == 0) RegainCrimson else RegainMint,
                            inactiveTrackColor = RegainSurfaceVariant
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "0m (Instant Block)", fontSize = 11.sp, color = if (totalMinutes == 0) RegainCrimson else RegainTextMuted)
                        Text(text = "24h (1440m)", fontSize = 11.sp, color = RegainTextMuted)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Preset Chips
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
                                            if (mins == 0) RegainCrimson.copy(alpha = 0.25f) else RegainMint.copy(alpha = 0.25f)
                                        } else RegainSurfaceVariant
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) (if (mins == 0) RegainCrimson else RegainMint) else RegainCardBorder,
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
                                        if (mins == 0) RegainCrimson else RegainMintLight
                                    } else RegainTextSecondary
                                )
                            }
                        }
                    }
                }

                // Section 2: Duration Slider (1 to 365 Days)
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
                            text = "LOCK DURATION (CALENDAR DAYS)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RegainMintLight,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = durationLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RegainMintLight
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Stepper Box for Days
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(RegainSurfaceVariant)
                            .border(1.dp, RegainCardBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ACTIVE DURATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = RegainTextMuted)
                                Text(durationLabel, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = RegainTextPrimary)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = { if (days > 1) days -= 1 },
                                    enabled = days > 1,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease Day", tint = if (days > 1) RegainMintLight else RegainTextMuted)
                                }
                                IconButton(
                                    onClick = { if (days < 365) days += 1 },
                                    enabled = days < 365,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase Day", tint = if (days < 365) RegainMintLight else RegainTextMuted)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = days.toFloat(),
                        onValueChange = { newVal ->
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
                        listOf(1, 3, 7, 14, 30, 90, 180, 365).forEach { d ->
                            val isSelected = days == d
                            val chipText = if (d == 365) "1y" else "${d}d"
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) RegainMint.copy(alpha = 0.25f) else RegainSurfaceVariant)
                                    .border(1.dp, if (isSelected) RegainMint else RegainCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { days = d }
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
                }

                // Section 3: Enforcement Mode
                Column {
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
                                    text = "Requires 10-minute wait + verification code to unlock.",
                                    fontSize = 11.sp,
                                    color = RegainTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // STICKY CONFIRM & LOCK BUTTON
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
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RegainMint,
                    contentColor = RegainDarkBg
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
                            text = "LOCK ${selectedApps.size} APP${if (selectedApps.size > 1) "S" else ""} FOR $days DAYS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = if (totalMinutes == 0) "Instant Lock (0m daily allowance)" else "${if (hours > 0) "${hours}h " else ""}${minutes}m daily operating allowance",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RegainDarkBg.copy(alpha = 0.85f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

