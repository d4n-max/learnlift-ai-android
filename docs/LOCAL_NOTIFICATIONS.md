# Local Notifications

Last updated: 2026-06-17

## Overview

LearnLift AI supports optional local daily study reminders. Reminders are scheduled on the device only and do not use Firebase Cloud Messaging, backend push notifications, analytics, ads, account login, or cloud sync.

Reminders are disabled by default. The user must enable them from Settings.

## Stored Reminder Preferences

Reminder preferences are stored locally with DataStore in `learnlift_reminders`.

Stored fields:

- `remindersEnabled`
- `reminderHour`
- `reminderMinute`
- `lastReminderScheduledAt`
- `lastReminderSuggestionDismissedAt`
- `reminderSuggestionDismissCount`
- `lastNotificationPermissionDeniedAt`

Default state:

- reminders disabled
- suggested time: `19:00`

## Notification Permission

The manifest includes:

```text
android.permission.POST_NOTIFICATIONS
```

On Android 13 and newer, the app requests notification permission only when the user taps `Enable daily study reminder` in Settings.

If permission is denied:

- the app does not crash.
- reminders remain disabled.
- Settings shows `Notifications are off. You can enable them later from Android settings.`
- Settings can open Android app notification/settings controls through `Open notification settings`.

## Notification Channel

Channel:

- id: `daily_study_reminders`
- name: `Daily study reminders`
- description: `Reminders to continue your LearnLift AI study habit`

The channel is created on app startup and before showing reminders.

## Notification Icon

Local reminders use a dedicated small notification icon:

```text
R.drawable.ic_notification_learnlift
```

This is separate from the launcher and splash icon resources. Android small notification icons should be simple white/monochrome artwork on a transparent background, so the reminder notification does not use the colorful launcher icon or the old pink `LA` foreground asset.

No large notification icon is currently set.

## Scheduling Approach

The app uses Android `AlarmManager.setInexactRepeating` with `RTC_WAKEUP`.

This avoids exact-alarm special permissions. The reminder is flexible and may not fire at the exact minute on every device, which is acceptable for a habit reminder.

When reminders are enabled:

- the app schedules a daily reminder at the selected time.

When reminder time changes:

- the app schedules the reminder with the new time.

When reminders are disabled:

- the app cancels the local alarm.

## Reboot Behavior

The manifest includes:

```text
android.permission.RECEIVE_BOOT_COMPLETED
```

After device reboot, a lightweight boot receiver checks local reminder preferences and reschedules the daily reminder only if `remindersEnabled=true`.

No heavy work is performed on boot.

## Notification Copy

Notification title:

```text
Time for a quick study session
```

Notification body:

```text
Open LearnLift AI for a short focused practice session.
```

The copy intentionally avoids guilt, fear, streak pressure, spammy urgency, and guaranteed outcomes.

Tapping the notification opens the app. It currently opens the normal app entry point rather than deep-linking directly into Daily Session.

## Onboarding Relation

Onboarding does not force notification opt-in. The selected daily study minutes can be used in reminder copy and Settings shows the current daily goal.

After a successful Daily Session, Home can show an optional reminder setup card only when reminders are disabled and local cooldown rules allow it.

Post-session reminder setup copy:

```text
Title: Want a daily study reminder?
Body: Set a gentle reminder for short practice sessions.
Actions: Set reminder / Maybe later
```

Cooldown rules:

- `Maybe later` suppresses the card for 7 days.
- The card stops after 3 dismissals.
- A notification permission denial suppresses the card for 24 hours.
- Android 13+ notification permission is requested only after `Set reminder`.

## Privacy

Reminder preferences stay on the device. No notification data is sent to a server.

## Future Improvements

- Add a custom time picker instead of fixed time presets.
- Avoid reminding later the same day after a completed Daily Session.
- Deep-link notification taps directly into Daily Session.
- Add Smart Review due reminders in Phase 2B only after a duplicate-safe once-per-day scheduler is designed.
- Add weekly progress reminders only if users ask for them or Progress needs a gentle opt-in nudge.

## Manual QA Checklist

- Reminders are disabled by default.
- Enabling reminder requests permission on Android 13+.
- Denying permission does not crash app.
- Denied permission shows friendly copy and optional Android settings action.
- Granting permission schedules reminder.
- Changing reminder time reschedules.
- Disabling reminder cancels.
- App opens from notification tap.
- Notification does not show the old pink `LA` icon.
- Notification small icon appears as a clean LearnLift-style monochrome mark.
- Notification copy uses the calm Phase 1 title and body.
- Settings shows correct reminder status.
- Post-session reminder setup card appears only after successful Daily Session when reminders are disabled.
- `Maybe later` dismissal cooldown works.
- Android 13+ permission is requested only after `Set reminder`.
- Restart app preserves reminder setting.
- Reboot reschedules only if reminders are enabled.
- Light/dark Settings UI is readable.
- No network is required.
- No backend calls are made.
