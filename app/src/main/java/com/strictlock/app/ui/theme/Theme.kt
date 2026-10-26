package com.strictlock.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Low-Cortisol, Anti-Halation Dark Color Scheme
private val NeuroErgonomicColorScheme = darkColorScheme(
    primary = ParasympatheticSage,
    onPrimary = DeepObsidian,
    primaryContainer = InteractiveSurface,
    onPrimaryContainer = ParasympatheticSageLight,
    secondary = AuthorityIndigo,
    onSecondary = AlabasterText,
    secondaryContainer = InteractiveSurface,
    onSecondaryContainer = AuthorityIndigo,
    tertiary = MindfulAmber,
    onTertiary = DeepObsidian,
    background = DeepObsidian,
    onBackground = AlabasterText,
    surface = ElevatedSlate,
    onSurface = AlabasterText,
    surfaceVariant = InteractiveSurface,
    onSurfaceVariant = MutedSlateText,
    outline = SubtleHairline,
    error = ControlledCrimson,
    onError = AlabasterText
)

@Composable
fun StrictLockTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = NeuroErgonomicColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            @Suppress("DEPRECATION")
            window.statusBarColor = DeepObsidian.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = DeepObsidian.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = StrictLockTypography,
        content = content
    )
}
