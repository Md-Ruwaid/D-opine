# StrictLock (Regain Edition) 🛡️🧘

A privacy-first, offline Android screen-time limiter and mindful app blocker built with **Kotlin** and **Jetpack Compose (Material 3)**.

StrictLock recreates the core digital mindfulness and anti-distraction architecture of Regain—including **mindful breathing pauses**, **Reels/Shorts doomscroll blockers**, and **deep focus timers**—combined with an unbreakable **StrictLock multi-day rule engine** backed by Device Administration and Accessibility shields.

---

## 📱 Features

### 1. Regain Digital Mindfulness & Distraction Shields
- **Mindful Breathing Interventions**: Pulsating 3.2-second breathing pause (`OverlayActivity`) triggering on launch of designated distracting apps.
- **Doomscroll Killers**: Real-time view-hierarchy scanning (`StrictLockAccessibilityService`) that detects and auto-dismisses:
  - Instagram Reels (`clips_video_container`, `reels_tab`)
  - YouTube Shorts (`shorts_container`, `shorts_player_view`)
- **Deep Focus Pomodoro Timer**: Circular animated timer (15, 25, 45, 60 minutes) with strict anti-quit mode.
- **Regain Obsidian Dashboard**: Dark theme stats tracking total screen time, estimated minutes saved, device unlocks, and hourly usage bar charts.

### 2. StrictLock Rule Engine
- **Multi-Day Rules**: Set usage limits over a span of 1 to 30 calendar days.
- **STRICT Mode**: Completely unbreakable. Zero bypasses, zero extensions, and zero overrides until the time limit resets.
- **CASUAL Mode**: Requires an intentional **10-minute cooldown timer** followed by a random 6-character alphanumeric challenge before unlocking.
- **Anti-Tamper Protections**:
  - Device Administrator shield preventing uninstallation without deactivation.
  - Settings page interception preventing force-stopping or clearing data.
  - Split-screen & floating window blocking.
  - Clock anti-tamper detection preventing time manipulation.
- **Emergency Safety Valve**: 7-tap confirmation reset inside the app for true emergencies.

---

## 🌐 Official Download Portal & Website

A dedicated landing page is built directly into the repository root:
- **Phone Frame Live Simulator**: Interactive previews of Home, Mindful Pause, Distraction Shields, Focus Timer, and StrictLock screens.
- **Live Breathing Exercise**: Interactive 4-3-4 mindful pause simulator in the browser.
- **Direct APK Downloads**: Serves `StrictLock-Regain-v1.0.1.apk` with proper Android package MIME types.
- **Local Network QR Code**: Scan directly from phone cameras to download over local Wi-Fi.
- **OxygenOS (Android 16) Guide**: Step-by-step instructions for Android 13–16 restricted accessibility settings.

To run the download site locally:
```bash
node server.js
```
Then visit `http://localhost:3000` (or your local IP on phone).

### 🚀 Deploying to Vercel

The repository is pre-configured with `vercel.json` and `.vercelignore` for zero-configuration static deployment:
1. Go to [Vercel](https://vercel.com) and click **"Add New Project"**.
2. Select your repository: `Md-Ruwaid/D-opine`.
3. Click **Deploy**. Vercel will automatically serve the website and route APK downloads with proper `application/vnd.android.package-archive` MIME headers.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Local Database**: Room DB (SQLite) with KSP
- **Asynchronous**: Kotlin Coroutines & Flow
- **Background Architecture**:
  - `StrictLockAccessibilityService`: High-efficiency view tree inspection and anti-tamper shields
  - `StrictLockDeviceAdminReceiver`: Device policy manager for uninstall prevention
  - `BootReceiver`: Persistent background guard across reboots
  - `AppBlockerService`: Foreground service monitoring app lifecycles via `UsageStatsManager`
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 35/36 (Android 15 / 16)

---

## 🔨 Building from Source

To compile the debug APK:
```bash
./gradlew assembleDebug
```
The resulting APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License & Notice

This project was built for private, personal use. 100% original Kotlin and Jetpack Compose codebase.
