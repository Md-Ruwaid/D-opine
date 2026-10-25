package com.strictlock.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val RegainDarkColorScheme = darkColorScheme(
    primary = RegainMint,
    onPrimary = RegainDarkBg,
    primaryContainer = RegainSurfaceVariant,
    onPrimaryContainer = RegainMintLight,
    secondary = RegainAmber,
    onSecondary = RegainDarkBg,
    background = RegainDarkBg,
    onBackground = RegainTextPrimary,
    surface = RegainSurface,
    onSurface = RegainTextPrimary,
    surfaceVariant = RegainSurfaceVariant,
    onSurfaceVariant = RegainTextSecondary,
    error = RegainCrimson,
    onError = RegainTextPrimary
)

@Composable
fun StrictLockTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = RegainDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            @Suppress("DEPRECATION")
            window.statusBarColor = RegainDarkBg.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = RegainDarkBg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
