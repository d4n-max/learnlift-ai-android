# Reviews And Notifications Implementation Plan

Last updated: 2026-06-17

Repository audited: `C:\Projects\learnlift-ai-android`

## Skills Used

- `product-capability`: converted the request into capability boundaries, non-goals, storage needs, rollout rules, and implementation order.
- `onboarding`: kept notification permission and review asks out of first launch, and placed habit prompts after setup or successful learning moments.
- `marketing-psychology`: used ethical nudge principles: timely, low-pressure prompts, no guilt, no "5-star" framing, and no review ask after negative states.
- `android-emulator-skill`: shaped the manual QA plan around Android 13+ permission testing, emulator/device validation, notification taps, and runtime state checks.
- `verification-loop`: structured the audit, source/doc review, QA gates, diff awareness, and build verification expectations for the later implementation task.
- `launch`: treated reviews and reminders as post-production retention/trust features with cautious rollout, tester-first review asks, and Play-policy-safe copy.

## Capability Summary

LearnLift AI should gain two production-safe local capabilities:

1. A Google Play In-App Review flow that asks only after stable, positive learning engagement and treats the Play Review API as best effort.
2. Local study reminders that help learners return for short practice sessions without push notifications, backend dependencies, aggressive urgency, or first-launch permission friction.

These capabilities must preserve the current no-login, no-cloud-sync, local-progress model. They must not change the package name, version fields, monetization model, analytics posture, or backend architecture.

## Explicit Non-Goals

- Do not implement code in this planning task.
- Do not change `applicationId`, package name, `versionCode`, or `versionName`.
- Do not add analytics, ads, login/auth, Firebase, cloud sync, backend databases, push notifications, or external notification providers.
- Do not gate any feature behind rating/review actions.
- Do not show review prompts during onboarding, first launch, paywall, purchase failures, AI failures, or negative learning moments.
- Do not overpromise learning, jobs, exams, interviews, fluency, or career outcomes.

## Current Audit Findings

### Manifest

File reviewed: `app/src/main/AndroidManifest.xml`

- `android.permission.INTERNET` is declared for AI/backend and billing-related network support.
- `android.permission.POST_NOTIFICATIONS` is already declared for Android 13+ local reminders.
- `android.permission.RECEIVE_BOOT_COMPLETED` is already declared.
- `DailyReminderReceiver` is registered with `android:exported="false"`.
- `ReminderBootReceiver` is registered with `android:exported="false"` and listens for `BOOT_COMPLETED`.
- No Play Review-specific manifest entry is needed.

### Gradle / Dependencies

File reviewed: `app/build.gradle.kts`

- Android app uses Kotlin, Jetpack Compose, Material 3, DataStore Preferences, and RevenueCat.
- Current namespace/application ID is `com.learnliftai.app`.
- Current SDK configuration: `compileSdk = 35`, `minSdk = 24`, `targetSdk = 35`.
- Current version fields in the audited file: `versionCode = 7`, `versionName = "1.0.0"`.
- DataStore dependency already exists: `androidx.datastore:datastore-preferences:1.1.1`.
- RevenueCat dependency exists.
- No WorkManager dependency exists.
- No Google Play In-App Review dependency exists yet.
- No Firebase dependency was found in the app Gradle file.

### Existing Notification / Reminder Code

Files reviewed:

- `app/src/main/java/com/learnliftai/app/notifications/DailyReminderScheduler.kt`
- `app/src/main/java/com/learnliftai/app/notifications/DailyReminderReceiver.kt`
- `app/src/main/java/com/learnliftai/app/notifications/ReminderBootReceiver.kt`
- `app/src/main/java/com/learnliftai/app/data/LocalReminderPreferencesRepository.kt`
- `app/src/main/java/com/learnliftai/app/domain/model/ReminderPreferences.kt`
- `docs/LOCAL_NOTIFICATIONS.md`

Findings:

- Daily reminders already exist.
- Scheduling uses `AlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, ...)`.
- No `SCHEDULE_EXACT_ALARM` permission is used or needed for current flexible reminder behavior.
- Notification channel exists with id `daily_study_reminders`.
- Notification small icon is `R.drawable.ic_notification_learnlift`.
- Notification tap opens `MainActivity`.
- Android 13+ notification permission is checked before posting in `DailyReminderReceiver`.
- Android 13+ runtime permission is requested from Settings only when enabling reminders.
- Reboot rescheduling exists through `ReminderBootReceiver`.
- Reminder preferences are stored locally in DataStore `learnlift_reminders`.
- Existing notification body can use streak and daily study minutes, but one existing string, `Keep your X-day streak going.`, is more streak-pressure oriented than the requested calm copy direction.
- Smart Review due notifications do not exist yet.
- Weekly progress reminders do not exist yet.
- There is no notification deep link to Daily Session or Smart Review yet.

### Settings

File reviewed: `app/src/main/java/com/learnliftai/app/ui/screens/SettingsScreen.kt`

Findings:

- Settings has a `Daily reminder` section.
- It shows reminder enabled/off status.
- It requests `POST_NOTIFICATIONS` only when the user taps `Enable daily study reminder`.
- If permission is denied, the UI sets `permissionDenied` and leaves reminders disabled.
- Reminder time choices are fixed presets: `08:00`, `12:00`, `19:00`, `21:00`.
- There is no "Open notification settings" action for permanently denied permission.
- There is no Smart Review reminder toggle.
- There is no weekly progress reminder toggle.
- There is no Rate LearnLift entry.

### Onboarding

Files reviewed:

- `app/src/main/java/com/learnliftai/app/ui/screens/OnboardingScreen.kt`
- `app/src/main/java/com/learnliftai/app/ui/navigation/LearnLiftApp.kt`
- `app/src/main/java/com/learnliftai/app/data/LocalOnboardingRepository.kt`
- `docs/ONBOARDING_AUDIT_PRODUCTION.md`

Findings:

- Onboarding is first-run, skippable, local-first, and does not show a paywall.
- It collects goal, daily study time, and recommended path.
- It does not request notification permission.
- It does not include reminder setup.
- It does not include review prompts.
- Completion stores onboarding state locally and navigates to Home.

### Home

File reviewed: `app/src/main/java/com/learnliftai/app/ui/screens/HomeScreen.kt`

Findings:

- Home provides entry points to Daily Session, Flashcards, Smart Review, Quiz, Adaptive Quiz, Settings, Premium, and study path selection.
- Home shows Smart Review due counts from local review state.
- Home is a good future surface for a non-blocking reminder suggestion if reminders are off, but only after onboarding and not as a permission dialog.
- Home should not show a review prompt directly on normal app open.

### Daily Session

File reviewed: `app/src/main/java/com/learnliftai/app/ui/screens/DailyStudySessionScreen.kt`

Findings:

- Daily Session has a clear positive completion summary.
- Completion records reviewed card counts, quiz answered/correct counts, quiz percentage, and updates progress.
- This is the strongest review trigger candidate after the user has completed at least 3 daily sessions and meets app-open day/cooldown thresholds.
- Do not trigger after a session dominated by wrong answers or errors. Use the summary state and local counters to decide.

### Smart Review / Flashcards

Files reviewed:

- `app/src/main/java/com/learnliftai/app/ui/screens/FlashcardsScreen.kt`
- `app/src/main/java/com/learnliftai/app/data/LocalFlashcardReviewRepository.kt`
- `app/src/main/java/com/learnliftai/app/domain/model/FlashcardReviewState.kt`

Findings:

- Smart Review is implemented as `FlashcardMode.SmartReview`.
- Due cards are computed locally from `FlashcardReviewState.nextReviewAt`, `status`, and `isDue()`.
- Smart Review completion is not a distinct current state; the screen tracks reviewed cards but has no explicit finish action.
- Smart Review due notification is technically possible from local review state, but needs careful duplicate prevention and should be phase 2.

### Quiz / Quiz Summary / AI Coach

File reviewed: `app/src/main/java/com/learnliftai/app/ui/screens/QuizScreen.kt`

Findings:

- Quiz Summary exists after quiz completion.
- Quiz completion records score and percentage.
- Wrong answer AI Coach is user-initiated after a wrong answer.
- AI Quiz Review is user-initiated from summary.
- AI success is currently internal UI state inside the Compose screen, not persisted as a durable successful learning event.
- Quiz Summary is a good review trigger candidate only after a decent result, such as `percentage >= 70`, with minimum successful actions and day thresholds met.
- Do not trigger after an AI error, AI fallback, usage-limit block, or immediately after a wrong answer.

### Progress

File reviewed: `app/src/main/java/com/learnliftai/app/ui/screens/ProgressScreen.kt`

Findings:

- Progress shows local stats, weak topics, Smart Review counts, AI Study Plan, and Settings link.
- This is a good future surface for a gentle reminder suggestion when reminders are off and the user has due review cards or some progress.
- It should not show a forced notification permission request or review prompt.

### DataStore / Local Preferences

Files reviewed:

- `LocalProgressRepository.kt`
- `LocalOnboardingRepository.kt`
- `LocalReminderPreferencesRepository.kt`
- `LocalFlashcardReviewRepository.kt`
- `LocalTopicPerformanceRepository.kt`
- `AiUsageRepository.kt`

Current local stores:

- `learnlift_progress`: selected path, flashcard totals, quiz totals, current streak, last study date.
- `learnlift_onboarding`: onboarding completion, goal, recommended path, daily minutes, completion timestamp.
- `learnlift_reminders`: reminder enabled, hour, minute, last scheduled timestamp.
- `learnlift_flashcard_review`: JSON-encoded flashcard review state with next due timestamps.
- Topic performance and AI usage are also local.

Missing local store:

- No review prompt state exists yet.

### Requested Docs

Reviewed:

- `docs/QA_CHECKLIST.md`
- `docs/BUG_BACKLOG.md`
- `docs/LAUNCH_PLAN_30_DAYS.md`
- `docs/ONBOARDING_AUDIT_PRODUCTION.md`
- `docs/LOCAL_NOTIFICATIONS.md`

Not present:

- `docs/MARKETING_PSYCHOLOGY_REVIEW.md`

## In-App Review Strategy

### API Choice

Use the official Google Play In-App Review API.

Recommended dependency for implementation:

```kotlin
implementation("com.google.android.play:review-ktx:<current-stable-version>")
```

Before implementation, check the current stable version from official Google documentation or Maven/Gradle metadata. Do not add a fake custom rating dialog as the final review mechanism.

### Direct API vs Pre-Prompt

Recommendation: use a lightweight pre-prompt, then call Play Review API only if the user taps `Rate LearnLift`.

Reasons:

- The pre-prompt gives users control.
- It avoids surprising users with an OS/Play dialog.
- It lets the app avoid asking users who are unhappy or busy.
- The copy can be calm and brand-consistent.

Do not use a custom star-rating gate that asks unhappy users for private feedback and happy users for public ratings. That pattern can become manipulative. A simple `Rate LearnLift` / `Not now` pre-prompt is enough.

### Review Prompt Copy

Pre-prompt title:

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

Avoid:

- `Give us 5 stars`
- `Support us with a 5-star review`
- guilt copy
- blocking UI
- review requests after errors, failed purchases, or difficult learning moments

### Trigger Policy

Minimum required conditions:

- User completed onboarding.
- App has been used on at least 2 distinct days.
- At least 3 successful learning actions have occurred.
- Prompt is evaluated only from a positive completion screen.
- At least 30 days have passed since the last review prompt attempt.
- No recent negative state occurred in the current flow.
- User has not selected `Don't ask again` if that option is added later.

Successful learning actions should include:

- Daily Session completed with at least one card reviewed or quiz answered.
- Quiz completed with a decent result, recommended threshold `>= 70%`.
- Smart Review session with at least one due card marked Known.
- AI Coach explanation succeeds.
- AI Quiz Review succeeds.

Positive trigger candidates:

- Daily Session summary, after the third completed daily session.
- Quiz Summary, only if percentage is at least 70 and no AI error is currently visible.
- Smart Review completion, after the user reviews due cards successfully. This may need an explicit finish state first.
- AI Coach explanation success, but only after returning to a natural completion state, not while reading the answer explanation.
- App return on the third distinct active day, but wait until the next positive completion screen before prompting.

Avoid trigger candidates:

- First launch.
- Onboarding.
- Splash/startup.
- Normal Home open.
- Paywall or Premium screen.
- Purchase failure, cancellation, restore failure, or product unavailable state.
- AI error, AI fallback, or AI usage-limit state.
- Immediately after a wrong answer.
- After notification permission denial.
- After app crash or visible error.
- More than once in a short period.

### Review State Storage

Create a local DataStore repository, for example `LocalReviewPromptRepository`, with a store name such as `learnlift_review_prompt`.

Suggested model:

```kotlin
data class ReviewPromptState(
    val successfulLearningActionsCount: Int = 0,
    val completedDailySessionsCount: Int = 0,
    val successfulSmartReviewCount: Int = 0,
    val successfulAiCoachExplanationsCount: Int = 0,
    val appOpenDays: Set<String> = emptySet(),
    val lastActiveDay: String? = null,
    val lastReviewPromptAttemptAt: Long? = null,
    val lastReviewPromptDismissedAt: Long? = null,
    val dontAskAgain: Boolean = false
)
```

If a `Set<String>` is too awkward in Preferences DataStore, store JSON or a compact comma-separated string of recent dates, capped to the most recent 30 days.

Persist prompt attempt before calling the Play Review API because the dialog may not show. The app should still respect cooldown after an API attempt.

### Review API Wrapper

Add a small wrapper, for example `PlayReviewPrompter`, that:

- Creates the ReviewManager.
- Requests `ReviewInfo`.
- Calls `launchReviewFlow(activity, reviewInfo)`.
- Catches failures and returns a non-blocking result.
- Never blocks navigation or learning completion.

Important behavior:

- Google Play may decide not to show the dialog even when the API call succeeds.
- Treat every API call as best effort.
- Do not show repeated fallback UI immediately after the API call.
- If the API fails, do nothing intrusive.

Optional later fallback:

- Add `Rate LearnLift` in Settings that opens the Play Store listing.
- Do not show a blocking fallback after an API failure.

## Notification / Reminder Strategy

### Capability Direction

Keep notifications local and habit-supportive.

Use existing `AlarmManager` daily reminder code for phase 1. It already fits the app because reminders are flexible habit nudges, not exact calendar alarms.

Recommended approach:

- Keep `AlarmManager.setInexactRepeating` for the daily reminder.
- Do not add WorkManager for the current daily reminder unless future requirements shift toward background work constraints instead of user-selected reminder times.
- Do not add exact alarms unless there is a strong product need and a Play-policy review of exact alarm permission impact.
- Add Smart Review due reminders only after daily reminders are polished and duplicate behavior is understood.

### WorkManager vs AlarmManager

AlarmManager fits LearnLift better now because:

- The user chooses a reminder time.
- Existing code already uses inexact repeating alarms.
- No exact-time guarantee is needed.
- No new dependency is required.
- It can wake around the target time and post a simple local notification.

WorkManager would fit if:

- The app needed opportunistic background checks instead of a user-selected time.
- Smart Review due reminders became a periodic background computation with flexible windows.
- Battery/network constraints mattered.

Recommendation: keep AlarmManager for daily reminders. For Smart Review due reminders, either reuse AlarmManager with one scheduled due-review check per day or defer until a simple in-app reminder surface proves useful.

### Permission Strategy

Current behavior is mostly correct:

- `POST_NOTIFICATIONS` is declared.
- Runtime permission is requested only when the user enables reminders in Settings.
- Permission denial keeps the app usable.

Planned refinements:

- Keep reminders disabled if permission is denied.
- Add copy that explains reminders can be enabled later.
- Add `Open notification settings` only when permission appears permanently denied or the user returns to Settings after denial.
- Do not ask for notification permission on first launch.
- Do not ask during onboarding unless the user explicitly taps `Set reminder` on an optional reminder card.
- Do not ask after review prompt dismissal or AI/purchase failures.

### Notification Types

Phase 1: Daily study reminder.

- Already implemented.
- Polish copy and permission/settings UX.
- Confirm no duplicate notifications.

Phase 2: Smart Review due reminder.

- Trigger only if due review cards exist.
- Do not send more than once per day.
- Respect global reminders enabled and optional Smart Review reminder toggle.
- Prefer a separate notification id and possibly a separate channel only if users need channel-level control.

Phase 3: Optional weekly progress reminder.

- Lower priority.
- Add only if early feedback suggests Progress is underused and reminders are not noisy.
- Keep disabled by default or tied to an explicit toggle.

### Notification Copy

Daily reminder:

```text
Title: Time for a quick study session
Body: Open LearnLift AI for a short focused practice session.
```

Smart Review due:

```text
Title: Cards ready for review
Body: Review due flashcards before they pile up.
```

Weekly progress:

```text
Title: Check your learning progress
Body: See weak topics and choose what to practice next.
```

Avoid:

- guilt
- fear
- `you are falling behind`
- `don't lose your streak`
- spammy urgency
- guaranteed outcomes

Current copy to reconsider:

- `Keep your X-day streak going.` It is not severe, but it leans on streak pressure. Replace with calmer copy for this implementation.

### Notification Storage Needs

Existing reminder state:

- `remindersEnabled`
- `reminderHour`
- `reminderMinute`
- `lastReminderScheduledAt`

Recommended additions if implementing Smart Review and permission-state UX:

- `smartReviewRemindersEnabled`
- `weeklyProgressReminderEnabled`
- `lastDailyNotificationShownAt`
- `lastSmartReviewNotificationShownAt`
- `lastWeeklyProgressNotificationShownAt`
- `lastNotificationPermissionDeniedAt` if needed for UI copy

Do not store notification permission as the source of truth. Always check the runtime permission and system notification state where possible.

### Settings UX Plan

Keep and polish:

- Enable daily reminder.
- Reminder time.
- Disable reminders.

Add or verify:

- Denied permission status.
- `Open notification settings` when useful.
- Smart Review reminder toggle if implemented.
- Optional weekly progress toggle only if implemented.
- Optional `Rate LearnLift` settings row as a non-blocking fallback to Play Store.

### Onboarding UX Plan

Do not force reminders during onboarding.

Best near-term option:

- Keep onboarding as-is for phase 1.
- After first successful session, optionally show a soft in-app reminder setup card.

Optional later onboarding completion card:

```text
Want a daily study reminder?
Set a gentle reminder for your daily practice.

Set reminder
Maybe later
```

If the user taps `Set reminder`, then request Android 13+ notification permission and enable scheduling. If the user taps `Maybe later`, continue normally.

## UX Placement Plan

Recommended surfaces:

- Onboarding completion: optional reminder setup only, not forced, and not in phase 1 unless activation routing is already being touched.
- Settings: full reminder controls and optional Rate LearnLift entry.
- Daily Session completion: review trigger candidate after thresholds.
- Quiz Summary: review trigger candidate only after decent score and threshold.
- Smart Review completion: review trigger candidate after due-card success, but add an explicit completion state first.
- Progress: non-blocking reminder suggestion if reminders are off and the user has due cards or weak topics.

Do not add prompts to:

- Splash screen.
- First app launch.
- Paywall.
- Purchase error state.
- AI unavailable state.
- Immediately after notification permission denial.

## Recommended Architecture

### Review Components

Add in implementation task:

- `domain/model/ReviewPromptState.kt`
- `data/LocalReviewPromptRepository.kt`
- `reviews/PlayReviewPrompter.kt` or equivalent.
- A small policy function, for example `ReviewPromptPolicy.canPrompt(...)`.
- A Compose pre-prompt dialog reused by Daily Session and Quiz Summary surfaces.

Policy should be pure where possible so it can be unit-tested without Android framework dependencies.

### Notification Components

Keep:

- `DailyReminderScheduler`
- `DailyReminderReceiver`
- `ReminderBootReceiver`
- `LocalReminderPreferencesRepository`

Polish in implementation task:

- Rename or generalize scheduler only if adding Smart Review reminders. Avoid broad refactors if only daily reminder copy/permission UX changes.
- Add notification copy constants or helper functions so QA can find copy easily.
- Add duplicate guards with last-shown timestamps if duplicate reminders are observed or Smart Review reminders are added.

### Data Ownership

All review prompt state and reminder state stays on device.

No analytics events should be added. If future analytics are introduced, review prompt and notification metrics need a separate privacy review.

## QA Checklist

### In-App Review

- [ ] First launch does not show review prompt.
- [ ] Onboarding does not show review prompt.
- [ ] Prompt does not show until minimum successful learning actions are met.
- [ ] Prompt does not show until minimum app-open days are met.
- [ ] Prompt appears only after a positive completion screen.
- [ ] `Not now` dismisses prompt and starts cooldown.
- [ ] `Rate LearnLift` calls Play Review API.
- [ ] App continues normally if the Play review dialog does not appear.
- [ ] API failure does nothing intrusive.
- [ ] Cooldown prevents repeated attempts for at least 30 days.
- [ ] Prompt does not appear after AI error or fallback.
- [ ] Prompt does not appear after purchase failure or cancellation.
- [ ] Prompt does not appear after notification permission denial.
- [ ] Prompt state persists after restart.
- [ ] Prompt state is local and reset only when app data is cleared.
- [ ] Optional Settings `Rate LearnLift` opens the Play Store listing without blocking usage.

### Notifications

- [ ] Android 13+ permission request appears only after enabling reminders or tapping `Set reminder`.
- [ ] Android 12 and below can enable reminders without runtime notification permission.
- [ ] Permission denial is handled gracefully.
- [ ] Permanent denial can guide user to app notification settings.
- [ ] Enable reminder schedules future local reminder.
- [ ] Change reminder time reschedules.
- [ ] Disable reminder cancels future daily reminders.
- [ ] Notification title/body use calm approved copy.
- [ ] Notification icon is current LearnLift notification icon.
- [ ] Notification tap opens app.
- [ ] No duplicate daily notifications.
- [ ] Reminder settings persist after app restart.
- [ ] Boot reschedule works when reminders are enabled.
- [ ] Boot does not schedule reminders when disabled.
- [ ] App remains usable without notification permission.
- [ ] Smart Review due notification works if implemented.
- [ ] Smart Review due notification does not fire when no cards are due.
- [ ] Weekly progress reminder works only if explicitly implemented.

### Build / Release

- [ ] `.\gradlew.bat assembleDebug` passes.
- [ ] Release build is not affected.
- [ ] Manifest has correct permissions.
- [ ] No package name change.
- [ ] No `versionCode` or `versionName` change in this implementation unless release owner explicitly requests it.
- [ ] No secrets committed.
- [ ] No Firebase, push provider, analytics, ads, backend DB, login, or cloud sync added.
- [ ] `git diff --check` passes.

## Rollout Plan

Phase 1:

- Status: implemented in `docs/REVIEWS_AND_NOTIFICATIONS_PHASE_1_IMPLEMENTATION.md`.
- Added local review state/counters.
- Added Play Review wrapper and low-pressure pre-prompt.
- Triggered only from Daily Session completion and Quiz Summary.
- Polished daily reminder copy and Settings denied-permission UX.
- Manual QA on Android 13+ and Android 12 or below is still required.

Phase 2:

- Status: Phase 2 Light implemented in `docs/REVIEWS_AND_NOTIFICATIONS_PHASE_2_IMPLEMENTATION.md`.
- Added optional Settings `Rate LearnLift` entry using the existing best-effort Play Review wrapper.
- Added optional post-session reminder setup card after successful Daily Session completion when reminders are disabled and local cooldown rules allow it.
- Deferred Smart Review due reminder to Phase 2B because duplicate-safe once-per-day notification scheduling needs a small dedicated implementation and QA pass.

Phase 3:

- Consider weekly progress reminder only after real user feedback.
- Review Play Store ratings/reviews and production feedback.
- Keep notification frequency conservative.

## Risks And Edge Cases

- Play Review dialog may not show even after successful API call.
- Review prompt cooldown must be based on attempt, not visible dialog confirmation.
- Calling the Review API too often can reduce effectiveness and harm UX.
- Without analytics, review prompt effectiveness will be judged by Play Console reviews and qualitative feedback.
- AlarmManager inexact reminders may not fire exactly at the selected minute.
- OEM battery restrictions may delay reminders.
- Android 13+ users can deny notification permission, including permanently.
- Reboot behavior depends on the device delivering `BOOT_COMPLETED`.
- Smart Review notifications can become noisy if due-card counts change often or duplicate alarms are scheduled.
- Streak-oriented notification copy may create pressure; prefer calm study-session copy.
- Local-only state means uninstall/reinstall clears reminder and review prompt history.

## Final Recommendation

Recommended implementation order:

1. Add local review trigger state/counters.
2. Add Play In-App Review dependency and wrapper.
3. Add review prompt trigger after positive completion events.
4. Add or polish reminder settings.
5. Add Android 13+ notification permission flow refinements.
6. Add/verify daily reminder scheduling.
7. Add Smart Review due reminder only if simple and safe.
8. Run QA checklist on physical device and emulator.

Recommended next Codex prompt:

```text
Implement docs/REVIEWS_AND_NOTIFICATIONS_IMPLEMENTATION_PLAN.md phase 1 only. Do not change package name or version fields. Add local review prompt state, Play In-App Review wrapper, low-pressure pre-prompt after positive Daily Session and Quiz Summary thresholds, and polish existing daily reminder copy/permission UX. Do not add analytics, ads, Firebase, push notifications, backend database, login, or cloud sync. Run assembleDebug and summarize QA.
```
