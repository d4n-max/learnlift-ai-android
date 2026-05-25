# UI Polish Bugfixes

Last updated: 2026-05-25

## Summary

This pass fixes visible screenshot blockers found on a physical Android device before Play Store screenshot capture.

## Issues Fixed

### Adaptive Quiz White Rectangle

Status: fixed and rechecked.

The Adaptive Quiz intro card for `Practice your weakest topics` used a translucent themed container color. On device screenshots, that could composite against the underlying light surface as an unwanted white rectangular block inside the card.

Fix:

- Replaced translucent card fill with opaque `MaterialTheme.colorScheme.primaryContainer`.
- Kept a subtle pink border accent.
- Updated body text to use `onPrimaryContainer` for better contrast.
- Preserved existing text:
  - `Practice your weakest topics`
  - `Focused topics: ...`
  - `Premium will unlock deeper adaptive practice.`

Files:

- `app/src/main/java/com/learnliftai/app/ui/screens/QuizScreen.kt`

### Onboarding White Rectangle

Status: fixed.

The onboarding welcome card and selected onboarding option card used translucent or near-white card fills. On physical screenshots, the selected `Improve English for work` card could show a white rectangular content area behind the text.

Fix:

- Replaced translucent card fill with opaque `MaterialTheme.colorScheme.primaryContainer`.
- Kept the pink/purple border treatment.
- Updated body copy color to `onPrimaryContainer`.
- Updated the shared selected option card to use an opaque lavender/purple selected surface.
- Updated unselected option cards to use a light lavender surface instead of the near-white default surface.
- Changed the selected leading accent from a weighted spacer to a fixed-width pink pill so it cannot create layout artifacts.
- Preserved the existing onboarding content and flow.

Files:

- `app/src/main/java/com/learnliftai/app/ui/screens/OnboardingScreen.kt`

### Launcher Icon Artifacts

Status: fixed.

The previous bugfix attempts replaced the original LearnLift AI icon with a newly generated mark. That was incorrect. The original LearnLift AI icon asset was found in Git history at commit `9f57456` (`Use LearnLift AI icon for launcher and home branding`) and restored.

Fix:

- Restored `learnlift_ai_app_icon.png` from commit `9f57456`.
- Restored launcher PNG assets from commit `9f57456`.
- Restored adaptive icon foreground references to `@drawable/learnlift_ai_app_icon`, matching the original launcher setup.
- AndroidManifest still references `@mipmap/ic_launcher` and `@mipmap/ic_launcher_round`.
- Fresh uninstall may be required on physical devices because some launchers cache app icons.

Files:

- `app/src/main/res/drawable/learnlift_ai_app_icon.png`
- `app/src/main/res/mipmap-mdpi/ic_launcher.png`
- `app/src/main/res/mipmap-mdpi/ic_launcher_round.png`
- `app/src/main/res/mipmap-hdpi/ic_launcher.png`
- `app/src/main/res/mipmap-hdpi/ic_launcher_round.png`
- `app/src/main/res/mipmap-xhdpi/ic_launcher.png`
- `app/src/main/res/mipmap-xhdpi/ic_launcher_round.png`
- `app/src/main/res/mipmap-xxhdpi/ic_launcher.png`
- `app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png`
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png`
- `app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png`

### Splash Icon Artifacts

Status: fixed.

The splash/loading icon could inherit the incorrect generated icon if it referenced the wrong foreground. It now uses the restored original LearnLift AI icon asset.

Fix:

- Android 12+ splash uses `@drawable/learnlift_ai_app_icon`.
- Splash background remains brand purple.
- Set the legacy window background to brand purple to avoid a white launch flash.

Files:

- `app/src/main/res/values/themes.xml`
- `app/src/main/res/values-v31/themes.xml`
- `app/src/main/res/drawable/ic_launcher_foreground.xml`
- `app/src/main/res/drawable/ic_launcher_background.xml`

## Manual QA Checklist

- [ ] Fresh install app.
- [ ] Launcher icon looks clean in app drawer.
- [ ] Launcher icon is the original purple LearnLift AI rounded icon restored from Git history.
- [ ] Launcher icon has no white square/corner artifacts.
- [ ] Splash/loading icon looks clean.
- [ ] Splash/loading icon matches the restored LearnLift AI launcher icon.
- [ ] Splash/loading icon is centered and not cropped.
- [ ] Onboarding welcome screen has no white rectangle artifact.
- [ ] Onboarding Choose your goal selected option has no white rectangle artifact.
- [ ] All onboarding goal cards use lavender/purple surfaces.
- [ ] Onboarding buttons look correct.
- [ ] Onboarding flow still completes.
- [ ] Adaptive Quiz top card has no white rectangle artifact.
- [ ] Adaptive Quiz content remains readable.
- [ ] Home still works.
- [ ] Quiz still works.
- [ ] AI Coach still works.
- [ ] Progress still works.
- [ ] Premium screen still works.
- [ ] Light/dark mode if supported.
- [ ] Physical Android device check.

## Build Validation

Run:

```powershell
.\gradlew.bat assembleDebug
```

If possible:

```powershell
.\gradlew.bat installDebug
```

If install fails due to signature mismatch:

```powershell
adb uninstall com.learnliftai.app
.\gradlew.bat installDebug
```
