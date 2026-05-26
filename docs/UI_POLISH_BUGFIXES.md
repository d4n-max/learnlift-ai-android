# UI Polish Bugfixes

Last updated: 2026-05-26

## Summary

This pass fixes visible screenshot blockers found on a physical Android device before Play Store screenshot capture.

## Issues Fixed

### Local Reminder Notification Icon

Status: fixed.

Local reminder notifications still showed the old pink `LA` icon after launcher and splash icons were restored.

Root cause:

- The notification builder used `R.drawable.ic_launcher_foreground` as the small notification icon.
- That resource can resolve to launcher foreground artwork, which is not appropriate for Android notification small icons and could show stale `LA` branding.

Fix:

- Added a dedicated monochrome transparent notification vector: `R.drawable.ic_notification_learnlift`.
- Updated local reminder notifications to use `setSmallIcon(R.drawable.ic_notification_learnlift)`.
- No large notification icon is currently set.
- Launcher and splash icons remain separate resources.

Files:

- `app/src/main/res/drawable/ic_notification_learnlift.xml`
- `app/src/main/java/com/learnliftai/app/notifications/DailyReminderScheduler.kt`

### Shared Card Inner Background Artifact

Status: fixed.

Root cause:

- The repeated white rectangle artifact was tied to highlighted/tinted card patterns where the outer Card surface and the padded inner content area could render as different surfaces on physical device screenshots.
- Smart Review still used a translucent `primaryContainer.copy(alpha = 0.36f)` fill, which made the mismatch visible on the top info card.

Shared fix:

- Updated `LearnLiftCard` so its inner padded content column fills the card width and paints the same `containerColor` as the outer Card.
- This keeps themed cards visually cohesive and prevents a white/default inner rectangle from showing behind text or buttons.

Files:

- `app/src/main/java/com/learnliftai/app/ui/components/LearnLiftComponents.kt`

### Smart Review White Rectangle

Status: fixed.

The Smart Review top info card showed an unwanted white rectangle behind:

- `1 card due now`
- `Needs Review cards return quickly...`
- `Continue all flashcards`

Fix:

- Replaced the translucent Smart Review fill with opaque `MaterialTheme.colorScheme.primaryContainer`.
- Kept the subtle pink border accent.
- Updated body copy to use `onPrimaryContainer` for readable theme-aware contrast.
- Kept the button and layout unchanged.

Files:

- `app/src/main/java/com/learnliftai/app/ui/screens/FlashcardsScreen.kt`

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
- [ ] Local reminder notification does not show the old pink `LA` icon.
- [ ] Local reminder notification uses the clean monochrome LearnLift notification icon.
- [ ] Onboarding welcome screen has no white rectangle artifact.
- [ ] Onboarding Choose your goal selected option has no white rectangle artifact.
- [ ] All onboarding goal cards use lavender/purple surfaces.
- [ ] Onboarding buttons look correct.
- [ ] Onboarding flow still completes.
- [ ] Adaptive Quiz top card has no white rectangle artifact.
- [ ] Adaptive Quiz content remains readable.
- [ ] Smart Review top info card has no white rectangle artifact.
- [ ] Smart Review `Continue all flashcards` button looks clean.
- [ ] Normal Flashcards still work.
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
