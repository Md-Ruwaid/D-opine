package com.strictlock.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.service.AppBlockerService
import com.strictlock.app.ui.screens.BlocksScreen
import com.strictlock.app.ui.screens.FocusScreen
import com.strictlock.app.ui.screens.HomeScreen
import com.strictlock.app.ui.screens.PermissionsScreen
import com.strictlock.app.ui.screens.StrictLockScreen
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
import com.strictlock.app.ui.theme.StrictLockTheme
import com.strictlock.app.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            StrictLockTheme {
                MainContent(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshPermissions()
        viewModel.refreshDashboardMetrics()
        AppBlockerService.start(this)
    }
}

@Composable
fun MainContent(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val permissions by viewModel.permissions.collectAsState()

    Scaffold(
        containerColor = RegainDarkBg,
        bottomBar = {
            RegainBottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                hasPermissionWarning = !permissions.allGranted
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(RegainDarkBg)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(viewModel)
                1 -> BlocksScreen(viewModel)
                2 -> FocusScreen(viewModel)
                3 -> StrictLockScreen(viewModel)
                4 -> PermissionsScreen(viewModel)
            }
        }
    }
}

@Composable
fun RegainBottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    hasPermissionWarning: Boolean
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Blocks", Icons.Filled.Block, Icons.Outlined.Block),
        BottomNavItem("Focus", Icons.Filled.Timer, Icons.Outlined.Timer),
        BottomNavItem("StrictLock", Icons.Filled.Lock, Icons.Outlined.Lock, isCustomTab = true),
        BottomNavItem("Shields", Icons.Filled.Security, Icons.Outlined.Security, hasWarning = hasPermissionWarning)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(RegainDarkBg)
            .border(
                width = 1.dp,
                color = RegainCardBorder,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(RegainSurface)
            .padding(vertical = 8.dp, horizontal = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedTab == index

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(index) }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected && item.isCustomTab) RegainCrimson.copy(alpha = 0.18f)
                                    else if (isSelected) RegainMint.copy(alpha = 0.15f)
                                    else androidx.compose.ui.graphics.Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = if (isSelected && item.isCustomTab) RegainCrimson
                                else if (isSelected) RegainMintLight
                                else RegainTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (item.hasWarning) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(RegainAmber)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected && item.isCustomTab) RegainCrimson
                        else if (isSelected) RegainMintLight
                        else RegainTextMuted
                    )
                }
            }
        }
    }
}

private data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val isCustomTab: Boolean = false,
    val hasWarning: Boolean = false
)
