package com.strictlock.app.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// NEURO-ERGONOMIC & CIRCADIAN PALETTE (Low-Cortisol, Anti-Halation Dark)
// =========================================================================

// Primary Surfaces & Backgrounds
val DeepObsidian = Color(0xFF0B0F17)            // #0B0F17: Primary background (prevents halation vs #000000, saves OLED power)
val ElevatedSlate = Color(0xFF131A26)           // #131A26: Surface Level 1 (Default cards and navigation bar)
val InteractiveSurface = Color(0xFF1C2536)      // #1C2536: Surface Level 2 (Active/pressed cards, dialogs, bottom sheets)
val SubtleHairline = Color(0xFF263248)          // #263248: Surface border (1.dp Gestalt boundary without heavy drop-shadows)

// Scientific Text Contrast Hierarchy (Prevents astigmatic retinal glare)
val AlabasterText = Color(0xFFF1F5F9)           // #F1F5F9: Text Primary (87% luminance ratio, crisp without harshness)
val MutedSlateText = Color(0xFF94A3B8)          // #94A3B8: Text Secondary (Supporting labels, timestamps, package IDs)
val DimSlateText = Color(0xFF475569)            // #475569: Text Disabled / Dim (Axis lines, inactive status markers)

// Semantic Accents (Autonomic & Cognitive State Mapping)
val ParasympatheticSage = Color(0xFF4EA88B)      // #4EA88B: Biophilic sage teal (Lowers heart rate; Focus, Breathing, Time Saved)
val ParasympatheticSageLight = Color(0xFF6BBF9E) // #6BBF9E: Soft sage tint for badge text & high-contrast highlights
val ParasympatheticSageGlow = Color(0x334EA88B)  // 20% alpha sage for ambient breathing halo

val AuthorityIndigo = Color(0xFF6366F1)          // #6366F1: Deep cognitive authority (Active shields, multi-day locks, system integrity)
val AuthorityIndigoDark = Color(0xFF4F46E5)      // #4F46E5: Dark indigo for secondary accents & borders
val AuthorityIndigoGlow = Color(0x336366F1)      // 20% alpha indigo for lock status rings

val MindfulAmber = Color(0xFFF59E0B)             // #F59E0B: Deliberate caution (Casual Mode 10-min cooldowns, approaching limits)
val MindfulAmberLight = Color(0xFFFBBF24)        // #FBBF24: Amber highlight
val MindfulAmberGlow = Color(0x33F59E0B)         // 20% alpha amber for cooldown timer containers

val ControlledCrimson = Color(0xFFE11D48)        // #E11D48: Desaturated crimson (Strict lockouts, anti-tamper, No Give Up)
val ControlledCrimsonLight = Color(0xFFFB7185)   // #FB7185: Crimson highlight
val ControlledCrimsonGlow = Color(0x33E11D48)    // 20% alpha crimson for emergency alerts

// =========================================================================
// BACKWARD-COMPATIBILITY ALIASES (Ensures zero breakage across existing code)
// =========================================================================
val RegainDarkBg = DeepObsidian
val RegainSurface = ElevatedSlate
val RegainSurfaceVariant = InteractiveSurface
val RegainCardBorder = SubtleHairline

val RegainMint = ParasympatheticSage
val RegainMintLight = ParasympatheticSageLight
val RegainAmber = MindfulAmber
val RegainCrimson = ControlledCrimson

val RegainTextPrimary = AlabasterText
val RegainTextSecondary = MutedSlateText
val RegainTextMuted = DimSlateText
