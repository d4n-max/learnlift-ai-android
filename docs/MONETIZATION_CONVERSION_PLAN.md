# LearnLift AI Monetization Conversion Plan

Last updated: 2026-06-08

## Core Principle

Free gets users studying. AI previews show the value. Premium removes AI friction and unlocks deeper learning support.

There is no hard app-wide paywall.

## Premium Touchpoint Audit

- Premium screen: plan status, benefits, Monthly/Yearly plans, purchase, restore, RevenueCat fallback.
- Settings: current plan, AI access, View Premium, Restore purchases.
- Home: soft Premium card below primary learning actions.
- Wrong-answer AI Coach: preview count, AI explanation CTA, local explanation fallback, View Premium after limit.
- Quiz Summary: AI Study Review teaser/generation and local summary fallback.
- Progress: AI Study Plan teaser/generation, Advanced guidance teaser, Weak Topics remain visible.
- Study Path Selection: Premium Study Pack preview/upgrade dialog and Coming soon states.
- Adaptive Quiz: Free basic access with Premium-ready copy only.
- Smart Review: basic spaced review stays Free.
- RevenueCat unavailable: app remains usable as Free with friendly messaging.

## Free Value Strategy

Free users keep:

- Existing free study paths.
- Basic flashcards.
- Basic quizzes.
- Daily Session.
- Basic Progress.
- Smart Coach.
- Smart Review basics.
- Adaptive Quiz basics.
- Local/static explanations.
- Limited AI Coach previews.
- Premium Study Pack previews where available.

Free must feel like a useful study app, not a broken trial.

## Premium Value Strategy

Premium focuses on:

- More AI Coach explanations.
- Higher AI daily limits.
- AI Quiz Review.
- 7-day AI Study Plans.
- Premium Study Packs.
- Smart learning support.

Use copy such as practice, prepare, build confidence, focus on weak topics, study smarter, and more AI help.

Avoid guaranteed job, interview, exam, certification, language, or career-success claims.

## Main Conversion Moments

### Wrong Answer AI Explanation

When Free previews remain:

```text
Free AI previews left today: X
```

CTA:

```text
Explain with AI Coach
```

When the Free limit is reached:

```text
You've used today's free AI Coach previews. Upgrade to Premium for more AI help, or continue with local explanations.
```

Buttons:

- View Premium
- Continue with local explanation

The local explanation remains visible. The app does not call Supabase after a local usage block.

### AI Quiz Review

Free users keep the local quiz summary. The AI Study Review card explains:

```text
AI Study Review is part of Premium. Get deeper feedback, recommended focus areas, and next-session suggestions.
```

Premium users can generate AI Study Review within the `quiz_summary` safety limit.

### AI Study Plan

Free users see:

```text
Create a 7-day AI Study Plan
Premium helps you plan what to study next.
```

Premium users can generate a 7-day plan within the `study_plan` safety limit.

### Premium Study Packs

Free users can preview available Premium packs:

- Preview pack
- View Premium
- Cancel

Premium users open full packs normally. Coming soon packs remain visible and disabled.

## Premium Screen Copy

Header:

```text
Unlock LearnLift AI Premium
```

Subtitle:

```text
Get more AI help, smarter review, and deeper progress support.
```

Available now:

- More AI Coach explanations.
- Higher AI daily limits.
- AI Quiz Review.
- 7-day AI Study Plans.
- Premium Study Packs.
- Smart learning support.

Coming soon / expanding:

- Advanced progress insights.
- More premium study paths.
- Deeper weak-topic coaching.

Trust/support:

- Restore purchases.
- Cancel anytime through Google Play.
- Premium status: Free / Premium active.

## Pricing

Target production pricing:

- Monthly: `€3.99 / month`.
- Yearly: `€24.99 / year`.

RevenueCat localized package prices are shown when available. Do not hardcode prices when packages are available.

If packages are unavailable, the app shows:

```text
Premium plans are temporarily unavailable. Please try again later.
```

The Yearly plan uses a `Best value` badge and copy:

```text
Save compared to monthly
```

## RevenueCat Rules

- Entitlement identifier: `premium`.
- Offering identifier: `default`.
- Google Play product IDs:
  - `learnlift_premium_monthly`.
  - `learnlift_premium_yearly`.
- Base plan IDs:
  - `monthly`.
  - `yearly`.

Premium is active only when:

```kotlin
customerInfo.entitlements["premium"]?.isActive == true
```

## Trial Experiment Notes

Do not mention a trial in the app unless RevenueCat/Google Play returns real introductory offer data.

Future experiments:

- No trial vs 7-day trial.
- 7-day trial vs 14-day trial.
- Yearly-first plan order vs current Monthly/Yearly visibility.

## Tester Feedback Questions

- Would you pay `€3.99/month` for more AI Coach explanations?
- Which Premium feature feels most valuable?
- Did the paywall feel clear?
- Did anything feel locked too early?
- Would yearly at `€24.99/year` feel fair?
- What would make Premium worth it?
- Did the Free app still feel useful?

## Manual QA Checklist

- Free user can use core app.
- Free user sees AI previews left.
- Free user reaches AI limit and sees Premium CTA.
- Local explanation remains available.
- Blocked AI limit does not call Supabase.
- View Premium opens Premium screen.
- Premium screen shows Monthly and Yearly.
- Yearly has Best value badge.
- Localized RevenueCat prices display when available.
- Purchase cancellation is graceful.
- Restore purchases works.
- Premium active removes Free preview countdown.
- Premium active disables duplicate purchase CTA.
- RevenueCat unavailable does not crash app.
- Settings shows Free/Premium correctly.
- Progress Premium teaser does not block basic progress.
- Study Pack preview/paywall works.
- AI Study Plan and AI Review Premium gating works.

## Future Optimization Ideas

- Add post-upgrade confirmation that points to AI Study Review and Premium Study Packs.
- A/B test headline emphasis: AI help vs study packs vs weekly plan.
- Add server-side AI quotas when there is an account or anonymous server identity.
- Consider local paywall impression cooldowns only if tester feedback says prompts feel repetitive.
