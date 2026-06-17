# Reviews And Notifications Phase 1 Implementation

Last updated: 2026-06-17

Repository: `C:\Projects\learnlift-ai-android`

## Summary

Phase 1 implements a conservative Google Play In-App Review prompt and polishes the existing local daily reminder behavior.

The app now tracks local review prompt eligibility in DataStore, evaluates a pure policy after positive completion events, shows a low-pressure pre-prompt, and calls the official Google Play In-App Review API as best effort. Daily reminders keep the existing local `AlarmManager.setInexactRepeating` scheduling model, channel id, icon, tap behavior, and boot receiver.

## Files Changed

Created:

- `app/src/main/java/com/learnliftai/app/domain/model/ReviewPromptState.kt`
- `app/src/main/java/com/learnliftai/app/data/LocalReviewPromptRepository.kt`
- `app/src/main/java/com/learnliftai/app/reviews/ReviewPromptPolicy.kt`
- `app/src/main/java/com/learnliftai/app/reviews/PlayReviewPrompter.kt`
- `app/src/main/java/com/learnliftai/app/reviews/ReviewPromptDialog.kt`
- `app/src/test/java/com/learnliftai/app/reviews/ReviewPromptPolicyTest.kt`
- `docs/REVIEWS_AND_NOTIFICATIONS_PHASE_1_IMPLEMENTATION.md`

Modified:

- `app/build.gradle.kts`
- `app/src/main/java/com/learnliftai/app/ui/navigation/LearnLiftApp.kt`
- `app/src/main/java/com/learnliftai/app/notifications/DailyReminderScheduler.kt`
- `app/src/main/java/com/learnliftai/app/notifications/DailyReminderReceiver.kt`
- `app/src/main/java/com/learnliftai/app/ui/screens/SettingsScreen.kt`
- `docs/REVIEWS_AND_NOTIFICATIONS_IMPLEMENTATION_PLAN.md`
- `docs/QA_CHECKLIST.md`
- `docs/LOCAL_NOTIFICATIONS.md`
- `docs/BUG_BACKLOG.md`

## Review Trigger Policy

The review prompt policy is in `ReviewPromptPolicy.canPrompt(...)`.

Minimum conditions:

- Onboarding is completed.
- Prompt is evaluated from a positive completion event.
- There is no recent negative state for the current flow.
- User has not opted out through `dontAskAgain`.
- App has been opened on at least 2 distinct local dates.
- At least 3 successful learning actions have been recorded.
- At least 30 days have passed since the last review prompt attempt or dismissal.

Current successful learning actions:

- Daily Session completion with at least one reviewed card or quiz answer.
- Quiz completion with score `>= 70%`.

Deferred counters are present but not wired in Phase 1:

- Smart Review successful completions.
- AI Coach explanation successes.

## Review Prompt Copy

Title:

```text
Enjoying LearnLift AI?
```

Body:

```text
A quick rating helps us improve the app for more learners.
```

Actions:

```text
Rate LearnLift
Not now
```

The prompt does not ask for 5 stars, does not use guilt copy, and does not gate app features.

## DataStore State

Review prompt state is stored locally in `learnlift_review_prompt`.

Stored fields:

- `successfulLearningActionsCount`
- `completedDailySessionsCount`
- `successfulSmartReviewCount`
- `successfulAiCoachExplanationsCount`
- `appOpenDays`
- `lastActiveDay`
- `lastReviewPromptAttemptAt`
- `lastReviewPromptDismissedAt`
- `dontAskAgain`

`appOpenDays` is stored as a compact comma-separated local date list capped to the most recent 30 dates.

## PlayReviewPrompter Behavior

`PlayReviewPrompter`:

- Safely unwraps an `Activity` from the current Compose context.
- Creates the official Google Play `ReviewManager`.
- Requests `ReviewInfo`.
- Launches the review flow if `ReviewInfo` is available.
- Catches failures and logs only safe debug messages.
- Treats the API as best effort.
- Does not block normal app navigation or learning completion.

The app saves `lastReviewPromptAttemptAt` before calling the Play Review API because Google Play may choose not to show a dialog.

## Trigger Points

### Daily Session

Status: implemented.

When the user taps `Finish and return Home`, LearnLift AI:

- saves normal progress as before.
- increments `completedDailySessionsCount`.
- increments `successfulLearningActionsCount` if at least one card was reviewed or quiz question was answered.
- evaluates review prompt policy.
- shows the pre-prompt only if thresholds are met.

### Quiz Summary

Status: implemented.

When the user completes a quiz and taps `See summary`, LearnLift AI:

- saves normal quiz progress as before.
- increments `successfulLearningActionsCount` only if the score is at least `70%`.
- evaluates review prompt policy.
- shows the pre-prompt only if thresholds are met.

No review prompt was added to first launch, onboarding, Home normal open, Settings normal open, Premium, purchase failure, AI unavailable, AI fallback, AI usage-limit, or wrong-answer screens.

## Daily Reminder Copy Changes

Notification title:

```text
Time for a quick study session
```

Notification body:

```text
Open LearnLift AI for a short focused practice session.
```

Removed phase-1 streak-pressure copy:

```text
Keep your X-day streak going.
```

Preserved:

- `AlarmManager.setInexactRepeating`
- channel id `daily_study_reminders`
- small icon `R.drawable.ic_notification_learnlift`
- tap opens `MainActivity`
- boot reschedule behavior
- no WorkManager
- no exact alarm permission

## Notification Permission Behavior

Settings still requests `POST_NOTIFICATIONS` only when the user taps `Enable daily study reminder`.

If permission is denied:

- reminders remain disabled.
- Settings shows: `Notifications are off. You can enable them later from Android settings.`
- Settings offers `Open notification settings`.
- the app remains usable.

No notification permission request was added to first launch or onboarding.

## Deferred To Phase 2

- Smart Review due notifications.
- Weekly progress reminders.
- Optional Settings `Rate LearnLift` row.
- Smart Review review-prompt trigger.
- AI Coach success review-prompt trigger.
- Post-session reminder setup card.
- Notification deep links into Daily Session or Smart Review.
- Daily reminder duplicate-last-shown timestamp guard, unless manual QA finds duplicates.

## Manual QA Checklist

### In-App Review

- [ ] First launch does not show review prompt.
- [ ] Onboarding does not show review prompt.
- [ ] Normal Home open does not show review prompt.
- [ ] Prompt appears only after threshold and positive completion.
- [ ] Daily Session completion increments local review state.
- [ ] Quiz Summary with score below 70% does not increment successful review action.
- [ ] Quiz Summary with score 70% or higher increments successful review action.
- [ ] `Not now` starts cooldown.
- [ ] `Rate LearnLift` records attempt before Play Review API call.
- [ ] `Rate LearnLift` calls Play Review API.
- [ ] App continues if Play dialog does not appear.
- [ ] Prompt does not show after AI error or fallback.
- [ ] Prompt does not show after purchase failure or cancellation.
- [ ] Prompt state persists after restart.

### Notifications

- [ ] Android 13+ permission request appears only after enabling reminders.
- [ ] Permission denial is handled gracefully.
- [ ] Denied permission shows friendly copy.
- [ ] `Open notification settings` opens Android settings.
- [ ] Enabling reminder schedules reminder.
- [ ] Changing time reschedules reminder.
- [ ] Disabling reminder cancels future reminders.
- [ ] Notification uses calm copy.
- [ ] Notification icon is current LearnLift icon.
- [ ] Tapping notification opens app.
- [ ] No duplicate daily notifications.
- [ ] Boot reschedule still works when reminders are enabled.
- [ ] Boot does not reschedule when reminders are disabled.

### Build And Privacy

- [ ] `.\gradlew.bat clean` passes.
- [ ] `.\gradlew.bat assembleDebug` passes.
- [ ] `.\gradlew.bat installDebug` passes if a device/emulator is connected.
- [ ] `git diff --check` passes.
- [ ] No package name change.
- [ ] No versionCode/versionName change.
- [ ] No analytics, ads, Firebase, push notifications, backend database, login/auth, or cloud sync added.
- [ ] No secrets or generated build artifacts are committed.
