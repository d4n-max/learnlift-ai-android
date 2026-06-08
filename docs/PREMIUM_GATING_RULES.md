# Premium Gating Rules

Last updated: 2026-05-19

## Tester-Safe Policy

LearnLift AI does not use a hard app-wide paywall. Free users and closed testers can still use the core v1 app even if RevenueCat, Google Play products, or purchases are unavailable.

Premium gating currently applies to AI-powered usage limits, Premium/Advanced Insights positioning, and v3 Premium Study Pack full access.

## Free Features

Free users keep access to:

- Home dashboard.
- Current three v1 study paths:
  - English Vocabulary & Speaking Prep.
  - Job Interview Prep.
  - IT / QA Interview Prep.
- Flashcards.
- Smart Review spaced repetition for current study paths.
- Quizzes.
- Daily study session.
- Basic progress.
- Local/static explanations.
- Rule-based Smart Coach recommendations.
- Limited AI Coach previews.
- Adaptive Quiz basic access.
- Basic due review counts.
- Settings and local reset.
- Preview access to available Premium Study Packs.

Current v1 study paths must not be locked behind Premium.

## Premium Features Available Now

Premium is detected through RevenueCat entitlement:

```text
premium
```

Premium currently unlocks or expands:

- Higher local AI Coach daily limits.
- More AI answer explanations each day.
- AI Quiz Review with Free preview allowance and Premium safety limits.
- AI Study Review with higher daily limits.
- 7-day AI Study Plan access.
- Premium active/status UI.
- Purchase and restore flow through RevenueCat.
- Value-based paywall copy with Monthly and Yearly plans.
- Full access to Premium Study Packs:
  - SQL Interview Prep.
  - QA Advanced.
  - Automation Testing Basics.

Premium does not remove local fallback behavior. If AI fails, the app still shows local explanations or Smart Coach recommendations.

## Premium Features Coming Soon

These should be shown as planned or coming soon, not as fully implemented:

- Advanced weakness tracking.
- Deeper progress insights.
- Future custom/adaptive practice.
- Deeper adaptive practice.
- Deeper spaced repetition insights.
- Advanced review planning.
- Future Premium Study Packs:
  - Python Basics.
  - JavaScript Basics.
  - Business English.
  - Technical Interview Prep.

Available Premium Study Packs provide Free preview mode with the first 5 flashcards and first 5 quiz questions. Coming soon packs are visible but disabled.

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

When this message appears, the app blocks the AI call locally, keeps local explanations or Smart Coach guidance visible, and shows `View Premium`. It should not keep prompting users to retry the same locally blocked AI request.

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

## RevenueCat / Google Play Product Rules

- Premium entitlement identifier must remain exactly `premium`.
- RevenueCat offering identifier should be `default`, and it should be set as the current offering.
- Google Play product IDs must be:
  - `learnlift_premium_monthly`
  - `learnlift_premium_yearly`
- Google Play base plan IDs must be:
  - `monthly`
  - `yearly`
- The Android app checks only `customerInfo.entitlements["premium"]?.isActive == true`.
- The Android app must not treat `monthly`, `yearly`, `annual`, `learnlift_premium_monthly`, `learnlift_premium_yearly`, or `LearnLift AI Premium` as entitlement identifiers.

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

## Conversion Placement Policy

Premium prompts are allowed in:

- AI limit reached states.
- Progress Advanced Insights teaser.
- Settings Premium section.
- A subtle Home card for higher AI Coach limits.
- Study Path Selection when a Free user taps an available Premium Study Pack.
- Quiz Summary when Free AI Quiz Review allowance is used.
- Progress when Free users view the AI Study Plan teaser.

Premium prompts should not interrupt onboarding, hide core study modes, or prevent Free users from using flashcards, quizzes, daily sessions, progress, Smart Coach, Smart Review, local explanations, or basic Adaptive Quiz.

Premium prompts for study packs must include a respectful Preview pack option, a View Premium action, and a Cancel action. At the end of preview Flashcards, Free users should see:

```text
You've reached the free preview limit for this pack. Unlock Premium to continue.
```

Actions:

- View Premium
- Back to Study Paths

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
- Free user can preview first 5 cards/questions from available Premium packs.
- Free user sees Preview mode labels for available Premium pack previews.
- Free user reaches preview limit and sees unlock CTA.
- Premium active user can open full available Premium packs.
- Coming soon Premium packs show a coming-soon dialog and do not navigate to empty content.
- Free user sees AI Study Plan teaser and `View Premium`.
- Premium active user can generate AI Quiz Review and 7-day AI Study Plan within safety limits.
