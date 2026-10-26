package com.strictlock.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// =========================================================================
// SCIENTIFIC TYPOGRAPHY SCALE (Perfect Fourth / Major Third Modular Scale)
// Optimized for 32cm mobile viewing distance with zero jitter tabular numerals
// =========================================================================

val StrictLockTypography = Typography(
    // Hero Focus Timer / Mindful Breathing Counter
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = (-0.5).sp,
        fontFeatureSettings = "tnum"
    ),

    // Screen Headers & Primary Daily Screen Time Stat
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.25).sp,
        fontFeatureSettings = "tnum"
    ),

    // Section Headers, Card Titles & Prominent Statuses
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),

    // Primary App Names & Rule Descriptions (1.5x lineHeight for saccadic comfort)
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),

    // Secondary Subtitles, Metric Footnotes & Supporting Copy
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),

    // Badges, Axis Labels, Status Pills (Uppercase Micro-Labels)
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        fontFeatureSettings = "tnum"
    )
)

// Helper TextStyle for tabular numbers with monospaced digit widths
val TabularNumberStyle = TextStyle(
    fontFeatureSettings = "tnum"
)
