# AI Study Plans v3.1

Last updated: 2026-06-08

## Purpose

AI Study Plans give Premium users a short 7-day plan based on local study context. The feature is optional and user-initiated from the Progress screen.

## Payload Sent

Android sends only compact planning context:

- selected study path ID and title
- onboarding goal, if available
- daily study minutes
- top weak topic names, max 5
- due Smart Review count
- recent quiz score summary, if available
- Premium/Free state string for copy guidance
- requested days, capped at 7
- level

Android does not send full quiz history, all study content, personal data, payment data, OpenAI keys, or Supabase service role keys.

## Response Shape

```json
{
  "title": "...",
  "days": [
    {
      "day": 1,
      "focus": "...",
      "tasks": ["...", "..."]
    }
  ]
}
```

The backend validates exactly 7 days for the v3.1 request.

## Premium Behavior

Free users see a teaser:

```text
Create a 7-day AI Study Plan
Premium helps you plan what to study next.
```

The Free CTA opens Premium. Free users do not call Supabase for study plans.

Premium users can generate a 7-day plan within the Premium `study_plan` safety limit.

## Fallback Behavior

Loading state:

```text
Building your 7-day study plan...
```

Failure state:

```text
AI Study Plan is temporarily unavailable. Try again later or continue with your daily session.
```

Retry is manual only. There are no automatic retry loops.

## Local Persistence Behavior

The last generated AI Study Plan is kept in memory for the current Progress screen session. It is not stored in a backend and is not persisted after app restart.

Future improvement: save the last generated plan locally in DataStore with a generated-at date and selected path ID.

## Manual QA Checklist

- Free user sees teaser.
- View Premium opens Premium screen.
- Premium active user can generate 7-day plan.
- Plan displays 7 days.
- Tasks are readable.
- Start Daily Session works.
- Offline fallback works.
- AI usage limit blocks calls locally.
- App restart behavior is acceptable and documented as in-memory only.
- Existing Progress, Weak Topics, Smart Review, and Daily Session still work.
