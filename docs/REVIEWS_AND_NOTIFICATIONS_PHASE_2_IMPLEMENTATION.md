# Reviews And Notifications Phase 2 Light Implementation

Last updated: 2026-06-17

Repository: `C:\Projects\learnlift-ai-android`

## Summary

Phase 2 Light adds two safe retention surfaces:

- A non-blocking `Rate LearnLift` entry in Settings.
- A gentle post-session daily reminder setup card after a successful Daily Session, only when reminders are disabled and local cooldown rules allow it.

Smart Review due notifications are deferred to Phase 2B. Weekly progress reminders are not implemented.

## Files Changed

Created:

- `docs/REVIEWS_AND_NOTIFICATIONS_PHASE_2_IMPLEMENTATION.md`

Modified:

- `app/src/main/java/com/learnliftai/app/domain/model/ReminderPreferences.kt`
- `app/src/main/java/com/learnliftai/app/data/LocalReminderPreferencesRepository.kt`
- `app/src/main/java/com/learnliftai/app/ui/navigation/LearnLiftApp.kt`
- `app/src/main/java/com/learnliftai/app/ui/screens/HomeScreen.kt`
- `app/src/main/java/com/learnliftai/app/ui/screens/SettingsScreen.kt`
- `docs/REVIEWS_AND_NOTIFICATIONS_IMPLEMENTATION_PLAN.md`
- `docs/QA_CHECKLIST.md`
- `docs/LOCAL_NOTIFICATIONS.md`
- `docs/BUG_BACKLOG.md`

## Settings Rate LearnLift Behavior

Settings now includes a `Reviews` section with:

```text
Title: Rate LearnLift
Body: Share a quick rating on Google Play.
CTA: Rate LearnLift
```

Tapping `Rate LearnLift` calls the existing `PlayReviewPrompter`. The entry is user-initiated, non-blocking, does not ask for `5 stars`, does not gate any feature, and does not change review-prompt cooldown state.

If Google Play does not show the review dialog or the Play Review API is unavailable, the existing prompter fails safely and app usage continues.

## Post-Session Reminder Setup Behavior

After a successful Daily Session returns the user to Home, LearnLift can show an optional card when all of these are true:

- onboarding is complete.
- daily reminders are disabled.
- the session reviewed at least one card or included quiz questions.
- notification permission was not denied in the last 24 hours.
- the local reminder suggestion cooldown allows it.
- the Phase 1 review prompt was not queued for the same completion.

Card copy:

```text
Title: Want a daily study reminder?
Body: Set a gentle reminder for short practice sessions.
Actions: Set reminder / Maybe later
```

`Set reminder` uses the existing reminder permission flow:

- Android 13+ requests `POST_NOTIFICATIONS` only after the tap.
- Android 12 and below enables the reminder directly.
- If permission is granted, the existing default reminder time is enabled and scheduled.
- If permission is denied, reminders stay disabled, the card is dismissed, and a local denial timestamp is stored.

`Maybe later` dismisses the card and stores local cooldown state.

## Cooldown Rules

New local reminder fields in `learnlift_reminders`:

- `lastReminderSuggestionDismissedAt`
- `reminderSuggestionDismissCount`
- `lastNotificationPermissionDeniedAt`

Rules:

- `Maybe later` suppresses the card for 7 days.
- The card stops after 3 dismissals.
- A notification permission denial suppresses the card for 24 hours.
- Enabling reminders hides the card.

## Smart Review Due Reminder

Status: deferred to Phase 2B.

Reason: due-card counts can be read from local review state, but the app does not yet have a small duplicate-safe once-per-day Smart Review notification scheduler. Adding that now would require new notification cadence state, background scheduling decisions, and duplicate-prevention QA beyond the Phase 2 Light scope. Daily reminder behavior remains unchanged.

Proposed Phase 2B requirements:

- no notification when no cards are due.
- no more than once per day.
- respect global reminders enabled.
- respect notification permission.
- prevent duplicate notifications.
- tap opens the app.
- use `R.drawable.ic_notification_learnlift`.

Approved future copy:

```text
Title: Cards ready for review
Body: Review due flashcards before they pile up.
```

## Manual QA Checklist

### Settings Rate LearnLift

- [ ] Settings row appears under `Reviews`.
- [ ] Tapping `Rate LearnLift` does not block app usage.
- [ ] No `5 stars` copy appears.
- [ ] App remains usable if Google Play/API is unavailable.

### Post-Session Reminder Setup

- [ ] Card does not show on first launch.
- [ ] Card does not show during onboarding.
- [ ] Card shows only after successful Daily Session when reminders are disabled.
- [ ] `Set reminder` requests permission only after tap on Android 13+.
- [ ] Permission denial keeps app usable and reminders disabled.
- [ ] `Maybe later` dismisses the card.
- [ ] 7-day dismissal cooldown works.
- [ ] 3-dismiss cap works.
- [ ] Card does not appear repeatedly.

### Regression

- [ ] Daily reminder still works.
- [ ] Review prompt Phase 1 still works.
- [ ] Onboarding still works.
- [ ] Home still works.
- [ ] Daily Session still works.
- [ ] Quiz Summary still works.
- [ ] Settings still works.
- [ ] No package name change.
- [ ] No `versionCode` or `versionName` change.

## What Remains For Phase 3

- Weekly progress reminder.
- Weekly progress notification toggle.
- Any additional notification channels, only if user-level control is needed.
- Notification deep links, if future UX calls for them.
