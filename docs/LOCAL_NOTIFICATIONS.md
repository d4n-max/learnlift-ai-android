# Local Notifications

Last updated: 2026-05-20

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
- Settings shows `Notification permission is disabled.`

## Notification Channel

Channel:

- id: `daily_study_reminders`
- name: `Daily study reminders`
- description: `Reminders to continue your LearnLift AI study habit`

The channel is created on app startup and before showing reminders.

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
LearnLift AI
```

Body uses local state when available:

- `Keep your 3-day streak going.`
- `Ready for today's 10-minute study session?`
- `Review a few flashcards today.`

Tapping the notification opens the app. It currently opens the normal app entry point rather than deep-linking directly into Daily Session.

## Onboarding Relation

Onboarding does not force notification opt-in. The selected daily study minutes can be used in reminder copy and Settings shows the current daily goal.

## Privacy

Reminder preferences stay on the device. No notification data is sent to a server.

## Future Improvements

- Add a custom time picker instead of fixed time presets.
- Avoid reminding later the same day after a completed Daily Session.
- Add an optional onboarding final-step reminder opt-in after more QA.
- Deep-link notification taps directly into Daily Session.

## Manual QA Checklist

- Reminders are disabled by default.
- Enabling reminder requests permission on Android 13+.
- Denying permission does not crash app.
- Granting permission schedules reminder.
- Changing reminder time reschedules.
- Disabling reminder cancels.
- App opens from notification tap.
- Settings shows correct reminder status.
- Restart app preserves reminder setting.
- Reboot reschedules only if reminders are enabled.
- Light/dark Settings UI is readable.
- No network is required.
- No backend calls are made.
