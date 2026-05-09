# LearnLift AI Internal Testing Checklist

Use either an Android emulator or a real Android device connected by USB.

## Setup

### Android Emulator

- Open Android Studio.
- Open Device Manager.
- Start the target emulator.
- If `adb` is available, confirm the emulator is visible:

```powershell
adb devices
```

- The emulator should appear in the list as `device`.
- Build and install the debug app:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

- Alternatively, select the emulator in Android Studio and press Run.

### Physical Android Device

- Confirm USB debugging is enabled on the Android device.
- Confirm the device is visible:

```powershell
adb devices
```

- Build and install the debug app:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

- Alternatively, select the connected device in Android Studio and press Run.

## Manual Test Checklist

### App Launch

- App installs successfully on the emulator or physical device.
- Launcher label shows `LearnLift AI`.
- App opens without crashing.
- Home shows LearnLift AI branding and tagline.

### Study Path Selection

- Open study path selection from Home.
- Select English Vocabulary & Speaking Prep.
- Select Job Interview Prep.
- Select IT / QA Interview Prep.
- Confirm Home updates with the selected path.
- Restart the app and confirm the selected path is restored.

### Flashcards

- Open Flashcards from bottom navigation.
- Confirm selected path flashcards load.
- Reveal an answer.
- Mark Known.
- Mark Needs Review.
- Move Previous and Next.
- Confirm session stats update.

### Quiz

- Open Quiz from bottom navigation.
- Answer a question.
- Confirm the answer locks after selection.
- Confirm correct or incorrect feedback appears.
- Confirm explanation appears.
- Complete the quiz and review the summary.

### Daily Session

- Start Daily Session from Home.
- Confirm intro shows selected path and session contents.
- Complete flashcards phase.
- Complete quiz phase.
- Confirm summary values look correct.
- Finish and confirm the app returns to Home.

### Progress Persistence

- Review at least one flashcard or complete a quiz/session.
- Open Progress and confirm stats update.
- Close and reopen the app.
- Confirm progress stats are still present.

### Reset Progress

- Open Progress or Settings.
- Tap Reset Progress Stats.
- Cancel once and confirm stats remain.
- Reset again and confirm stats clear.
- Confirm selected study path remains selected.

### Settings

- Open Settings from Home or Progress.
- Confirm app branding appears.
- Confirm current study path appears if selected.
- Confirm app info shows `Version 0.1.0 MVP`, `Local MVP`, and local device storage.
- Confirm future feature placeholders appear.

### Navigation

- Bottom navigation opens Home, Flashcards, Quiz, and Progress.
- Settings can return to Home.
- System Back from Settings, Study Path Selection, and Daily Session returns to Home.
- Daily Session can return to Home after completion.
- No screen content is cut off on the emulator or physical device.
