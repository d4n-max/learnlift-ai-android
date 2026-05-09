# LearnLift AI

Elevate Your Skills, Effortlessly.

LearnLift AI is an Android skill-building and study coach MVP. It helps learners choose a study path, review flashcards, take quizzes, complete a short daily study session, and track progress locally on the device.

## Current MVP Features

- Study path selection
- Flashcards
- Quiz mode
- Daily study session
- Local progress tracking
- Settings

## Current Study Paths

- English Vocabulary & Speaking Prep
- Job Interview Prep
- IT / QA Interview Prep

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- DataStore
- Local JSON content

## Brand Colors

- Primary purple: `#553C9A`
- Accent pink: `#ED64A6`

## Run Locally From Android Studio

1. Open this repository in Android Studio.
2. Let Gradle sync finish.
3. Start an Android emulator from Device Manager, or connect a real Android device with USB debugging enabled.
4. Select the emulator or connected device.
5. Run the `app` configuration.

## Verify Connected Android Device

```powershell
adb devices
```

The device should appear in the list as `device`.

## Build Debug APK

```powershell
.\gradlew.bat assembleDebug
```

## Install On Connected Android Device

```powershell
.\gradlew.bat installDebug
```

## Debug APK Location

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Known Limitations

- Local-only data
- No cloud sync
- No authentication
- No payments
- No live AI Coach yet
- Starter content only

## Internal Testing Checklist

- App launches and shows LearnLift AI branding.
- Study path selection works for all three paths.
- Home dashboard updates after selecting a path.
- Flashcards can reveal answers and mark Known / Needs Review.
- Quiz mode shows feedback, explanations, and summary.
- Daily Study Session completes and returns to Home.
- Progress values persist after app restart.
- Reset progress clears stats while keeping the selected path.
- Settings shows app info, current path, future features, and reset action.
- Bottom navigation works across Home, Flashcards, Quiz, and Progress.
