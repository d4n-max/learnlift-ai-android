# Premium Gating Rules

Last updated: 2026-05-19

## Tester-Safe Policy

LearnLift AI does not use a hard app-wide paywall. Free users and closed testers can still use the core v1 app even if RevenueCat, Google Play products, or purchases are unavailable.

Premium gating currently applies only to AI-powered usage limits and Premium/Advanced Insights positioning.

## Free Features

Free users keep access to:

- Home dashboard.
- Current three v1 study paths:
  - English Vocabulary & Speaking Prep.
  - Job Interview Prep.
  - IT / QA Interview Prep.
- Flashcards.
- Quizzes.
- Daily study session.
- Basic progress.
- Local/static explanations.
- Rule-based Smart Coach recommendations.
- Limited AI Coach previews.
- Settings and local reset.

Current v1 study paths must not be locked behind Premium.

## Premium Features Available Now

Premium is detected through RevenueCat entitlement:

```text
premium
```

Premium currently unlocks or expands:

- Higher local AI Coach daily limits.
- More AI answer explanations each day.
- AI Study Review with higher daily limits.
- 7-day AI Study Plan access.
- Premium active/status UI.
- Purchase and restore flow through RevenueCat.

Premium does not remove local fallback behavior. If AI fails, the app still shows local explanations or Smart Coach recommendations.

## Premium Features Coming Soon

These should be shown as planned or coming soon, not as fully implemented:

- Advanced weakness tracking.
- Deeper progress insights.
- Premium study packs.
- Future custom/adaptive practice.
- Future premium study packs:
  - SQL Interview Prep.
  - QA Advanced.
  - Automation Basics.
  - Python Basics.

No new study content is added in this gating task.

## AI Limit Behavior

Free local limits:

- `explain_answer`: 3 requests per local day.
- `quiz_summary`: 1 request per local day.
- `study_plan`: 0 requests per local day.

Premium local limits:

- `explain_answer`: 50 requests per local day.
- `quiz_summary`: 20 requests per local day.
- `study_plan`: 10 requests per local day.

Free limit reached message:

```text
You've used today's free AI Coach previews. Upgrade to Premium for more AI help, or continue with local explanations.
```

Premium safety limit reached message:

```text
You've reached today's AI Coach safety limit. Please try again tomorrow.
```

Blocked local requests do not call Supabase. Requests count when the app starts sending the AI request, even if the backend or network later fails.

## RevenueCat Unavailable

If RevenueCat is unavailable or products are not configured:

- The app defaults to Free.
- Core study features remain usable.
- Premium screen shows friendly unavailable/fallback messaging.
- Purchases fail gracefully.
- Restore purchases can be retried.
- No crash should occur.

## Advanced Insights

Free users see:

```text
Advanced Insights are part of Premium
```

Premium users see:

```text
Advanced Insights coming soon
```

Basic progress remains available for everyone.

## Future Stronger Gating Plan

Future production gating may add:

- Server-side AI quotas.
- Supabase Auth or another account/session identifier.
- Premium-only advanced insights.
- Premium-only future study packs.
- More precise free AI previews.

Before stronger gating ships, run full closed-testing QA and update Privacy Policy, Data Safety, Play Console copy, and billing QA docs.

## Manual QA Checklist

- Free user can use flashcards.
- Free user can use quizzes.
- Free user can use daily session.
- Free user can see progress.
- Free user can use limited AI previews.
- Free AI limit reached shows upgrade CTA.
- `View Premium` opens Premium screen.
- Premium active increases AI limits.
- Premium screen separates Available now from Coming soon.
- Premium screen does not show fake claims.
- Restore purchases updates plan.
- RevenueCat unavailable does not crash app.
- Existing three paths are not locked.
