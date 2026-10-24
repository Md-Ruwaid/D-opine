package com.strictlock.app.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strictlock.app.ui.components.AppIconImage
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
import kotlinx.coroutines.delay

class OverlayActivity : ComponentActivity() {

    companion object {
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_IS_STRICT_BLOCK = "extra_is_strict_block"
        const val EXTRA_IS_TAMPER_BLOCK = "extra_is_tamper_block"

        fun show(
            context: Context,
            appName: String,
            packageName: String,
            isStrictBlock: Boolean,
            isTamperBlock: Boolean = false
        ) {
            val intent = Intent(context, OverlayActivity::class.java).apply {
                putExtra(EXTRA_APP_NAME, appName)
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                putExtra(EXTRA_IS_STRICT_BLOCK, isStrictBlock)
                putExtra(EXTRA_IS_TAMPER_BLOCK, isTamperBlock)
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                )
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: "This App"
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        val isStrictBlock = intent.getBooleanExtra(EXTRA_IS_STRICT_BLOCK, true)
        val isTamperBlock = intent.getBooleanExtra(EXTRA_IS_TAMPER_BLOCK, false)

        setContent {
            MindfulOverlayContent(
                appName = appName,
                packageName = packageName,
                isStrictBlock = isStrictBlock,
                isTamperBlock = isTamperBlock,
                onExitToHome = { exitToHome() },
                onProceed = { finish() }
            )
        }
    }

    private fun exitToHome() {
        AppBlockerService.latestForegroundPackage = null
        val handled = StrictLockAccessibilityService.goHome()
        if (!handled) {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            try {
                startActivity(homeIntent)
            } catch (_: Exception) {}
        }
        finish()
    }
}

@Composable
fun MindfulOverlayContent(
    appName: String,
    packageName: String,
    isStrictBlock: Boolean,
    isTamperBlock: Boolean,
    onExitToHome: () -> Unit,
    onProceed: () -> Unit
) {
    var countdown by remember { mutableIntStateOf(5) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown -= 1
        }
    }

    // Breathing Animation
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.78f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RegainDarkBg)
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // App Icon
            if (packageName.isNotBlank()) {
                AppIconImage(
                    packageName = packageName,
                    appName = appName,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = appName,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RegainTextPrimary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated Breathing / Pulsing Circle
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .scale(scale),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .background(
                            if (isStrictBlock) RegainCrimson.copy(alpha = 0.12f)
                            else RegainMint.copy(alpha = 0.12f)
                        )
                )

                // Middle ring
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            if (isStrictBlock) RegainCrimson.copy(alpha = 0.25f)
                            else RegainMint.copy(alpha = 0.22f)
                        )
                )

                // Core Circle
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = if (isStrictBlock) listOf(RegainCrimson, Color(0xFF991B1B))
                                else listOf(RegainMintLight, RegainMint)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isStrictBlock) Icons.Default.Lock else Icons.Default.Spa,
                        contentDescription = null,
                        tint = if (isStrictBlock) RegainTextPrimary else RegainDarkBg,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            if (isTamperBlock) {
                Text(
                    text = "Clock Tampering Detected",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = RegainCrimson
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "System time was skewed against hardware elapsed time. Apps remain locked under Strict Protection.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    color = RegainTextSecondary
                )
            } else if (isStrictBlock) {
                Text(
                    text = "Daily Limit Reached",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = RegainCrimson
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Strict Mode Protection is active.\nNo extensions or bypasses allowed today.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    color = RegainTextSecondary
                )
            } else {
                Text(
                    text = "Take a Mindful Pause",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = RegainMintLight
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (countdown > 0)
                        "Inhale... Exhale... Wait $countdown seconds to reflect on your intention."
                    else
                        "Do you really need to open $appName right now?",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    color = RegainTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Primary Exit Action
            Button(
                onClick = onExitToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isStrictBlock) RegainCrimson else RegainMint,
                    contentColor = if (isStrictBlock) RegainTextPrimary else RegainDarkBg
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "EXIT TO HOME SCREEN",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Optional Proceed for Mindful Pause (only when not strict blocked)
            if (!isStrictBlock && !isTamperBlock) {
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onProceed,
                    enabled = countdown <= 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (countdown <= 0) RegainCardBorder else Color.Transparent
                    )
                ) {
                    Text(
                        text = if (countdown > 0) "WAIT ($countdown)" else "CONTINUE TO APP",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (countdown <= 0) RegainTextSecondary else RegainTextMuted
                    )
                }
            }
        }
    }
}
