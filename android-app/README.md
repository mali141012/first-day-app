# First Day — Offline Habit Tracker (Android, Jetpack Compose)

A private, offline-first habit tracker. No accounts, no cloud, no tracking.
Your data lives entirely on your device.

## Features

- **Today screen** — see today's scheduled habits, tap a circle to mark done
- **Habit detail** — current/longest streak, 7/30/90-day completion rate, year-long heatmap
- **Create/edit** — emoji + color picker, daily / specific days / X-times-week frequencies, local reminders
- **Stats** — weekly & monthly bar charts, total completions, best streak across all habits
- **Settings** — dark mode (system/on/off), start-of-week (Sun/Mon), JSON export/import, about
- **Reminders** — local notifications via AlarmManager, reschedules after reboot
- **Backup** — export/import all habits + completion history as a JSON file

## Tech stack

- Kotlin 2.0, Jetpack Compose (Material 3)
- Room database (local SQLite, no network)
- DataStore Preferences for settings
- Navigation Compose
- kotlinx.serialization for JSON backup
- Custom Canvas-drawn illustrations and charts (no image assets needed)

## How to build

1. Open Android Studio (Hedgehog or newer — requires AGP 8.5+).
2. `File → Open` and select the `android-app/` folder.
3. Let Gradle sync finish. The project uses the Gradle version catalog
   (`gradle/libs.versions.toml`) for all dependencies.
4. Connect a device or emulator (API 26+ / Android 8.0+).
5. Press **Run** (Shift+F10).

## Building a release AAB

From the command line or Android Studio's Build menu:

```
./gradlew :app:bundleRelease
```

This produces `app/build/outputs/bundle/release/app-release.aab`.
Sign it with your keystore before uploading to the Play Store:

```
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  app/build/outputs/bundle/release/app-release.aab my-key-alias
```

Or configure signing in `app/build.gradle.kts` under `signingConfigs`.

## Project structure

```
app/src/main/java/com/firstday/habits/
├── FirstDayApp.kt          # Application — initializes DB, repository, settings
├── MainActivity.kt         # Single-activity entry point + NavHost
├── navigation/             # Routes + bottom nav items
├── data/
│   ├── dao/                # Room DAOs (HabitDao, CompletionDao)
│   ├── db/                 # RoomDatabase + type converters
│   ├── entity/             # Room entities + domain mappers
│   ├── prefs/              # DataStore settings
│   └── repository/         # HabitRepository + BackupManager
├── domain/
│   ├── model/              # Habit, Frequency, stats models
│   └── util/               # DateUtils, StatsCalculator, WeekPlan
├── reminder/               # AlarmManager scheduling + boot receiver
└── ui/
    ├── theme/              # Color palette, typography, Material 3 theme
    ├── components/         # CompletionCircle, CalendarHeatmap, BarChart, illustrations
    └── screens/            # home, detail, create, stats, settings (+ ViewModels)
```

## No internet permission

The manifest declares zero network permissions. All data is stored locally via
Room. Reminders use AlarmManager + NotificationManager — no push service.
