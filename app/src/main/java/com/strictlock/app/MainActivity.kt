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
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.graphicsLayer
import com.strictlock.app.service.AppBlockerService
import com.strictlock.app.ui.screens.BlocksScreen
import com.strictlock.app.ui.screens.FocusScreen
import com.strictlock.app.ui.screens.HomeScreen
import com.strictlock.app.ui.screens.PermissionsScreen
import com.strictlock.app.ui.screens.StrictLockScreen
import com.strictlock.app.ui.theme.*
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
    var hasEnteredApp by remember { androidx.compose.runtime.mutableStateOf(false) }

    // Intercept with permission popups at the very start of the app
    // Admin is NOT necessary, but other 5 permissions ARE required before entering
    if (!permissions.requiredGranted || !hasEnteredApp) {
        com.strictlock.app.ui.screens.StartupPermissionsGate(
            viewModel = viewModel,
            permissions = permissions,
            onContinue = { hasEnteredApp = true }
        )
    } else {
        Scaffold(
            containerColor = RegainDarkBg,
            bottomBar = {
                RegainBottomNavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    hasPermissionWarning = !permissions.requiredGranted
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(RegainDarkBg)
            ) {
                androidx.compose.animation.AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        val isForward = targetState > initialState
                        val slideOffset = { size: Int -> if (isForward) size / 14 else -size / 14 }
                        val exitSlideOffset = { size: Int -> if (isForward) -size / 14 else size / 14 }

                        (androidx.compose.animation.slideInHorizontally(
                            animationSpec = MotionPhysics.iosFluidIntOffsetSpring,
                            initialOffsetX = slideOffset
                        ) + androidx.compose.animation.fadeIn(
                            animationSpec = androidx.compose.animation.core.tween(220)
                        ) + androidx.compose.animation.scaleIn(
                            initialScale = 0.98f,
                            animationSpec = MotionPhysics.iosFluidSpring
                        )).togetherWith(
                            androidx.compose.animation.slideOutHorizontally(
                                animationSpec = MotionPhysics.iosFluidIntOffsetSpring,
                                targetOffsetX = exitSlideOffset
                            ) + androidx.compose.animation.fadeOut(
                                animationSpec = androidx.compose.animation.core.tween(160)
                            ) + androidx.compose.animation.scaleOut(
                                targetScale = 0.98f,
                                animationSpec = MotionPhysics.iosFluidSpring
                            )
                        )
                    },
                    label = "MainScreenDepthTransition"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> HomeScreen(viewModel)
                        1 -> BlocksScreen(viewModel)
                        2 -> FocusScreen(viewModel)
                        3 -> StrictLockScreen(viewModel)
                        4 -> PermissionsScreen(viewModel)
                    }
                }
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
    val context = androidx.compose.ui.platform.LocalContext.current
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
            .background(DeepObsidian)
            .border(
                width = 1.dp,
                color = SubtleHairline,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(ElevatedSlate)
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = selectedTab == index

                val iconScale by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1f,
                    animationSpec = MotionPhysics.iosSnappySpring,
                    label = "tabIconScale"
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .iosTactilePress(
                            scaleDown = 0.92f,
                            onPressHaptic = {
                                com.strictlock.app.ui.haptics.NeuroHapticEngine.lightTick(context)
                            }
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (selectedTab != index) {
                                com.strictlock.app.ui.haptics.NeuroHapticEngine.lightTick(context)
                                onTabSelected(index)
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(contentAlignment = Alignment.TopEnd) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .graphicsLayer {
                                    scaleX = iconScale
                                    scaleY = iconScale
                                }
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected && item.isCustomTab) AuthorityIndigo.copy(alpha = 0.22f)
                                    else if (isSelected) ParasympatheticSage.copy(alpha = 0.18f)
                                    else androidx.compose.ui.graphics.Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                                tint = if (isSelected && item.isCustomTab) AuthorityIndigo
                                else if (isSelected) ParasympatheticSageLight
                                else MutedSlateText,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (item.hasWarning) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(MindfulAmber)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected && item.isCustomTab) AuthorityIndigo
                        else if (isSelected) ParasympatheticSageLight
                        else MutedSlateText
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
