package com.strictlock.app.ui.theme

import androidx.compose.ui.graphics.Color

// Regain Core Palette
val RegainDarkBg = Color(0xFF090C0A)            // Deep Obsidian Background
val RegainSurface = Color(0xFF131714)           // Elevated Surface Container
val RegainSurfaceVariant = Color(0xFF1A211D)    // Higher Elevation Surface
val RegainCardBorder = Color(0xFF26332A)        // Subtle card borders

// Accents & Signals
val RegainMint = Color(0xFF22C55E)              // Signature Emerald Mint (Focus, Saved Time)
val RegainMintLight = Color(0xFF4ADE80)         // Bright mint highlight
val RegainMintDark = Color(0xFF15803D)          // Deep mint outline
val RegainAmber = Color(0xFFF59E0B)             // Casual Mode, Cooldown warnings
val RegainCrimson = Color(0xFFEF4444)           // Strict Mode, Blocked status

// Text Colors
val RegainTextPrimary = Color(0xFFF9FAFB)       // Crisp Stark White
val RegainTextSecondary = Color(0xFF9CA3AF)     // Medium Gray
val RegainTextMuted = Color(0xFF64748B)         // Slate Muted
val RegainDivider = Color(0xFF1E2621)

// Backward-compat aliases for existing references
val DarkBackground = RegainDarkBg
val DarkSurface = RegainSurface
val DarkBorder = RegainCardBorder
val StarkWhite = RegainTextPrimary
val TextPrimary = RegainTextPrimary
val TextMuted = RegainTextMuted
val StrictCrimson = RegainCrimson
val CasualAmber = RegainAmber
val AccentVolt = RegainMintLight
