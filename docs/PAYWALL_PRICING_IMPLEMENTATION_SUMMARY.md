# Paywall Pricing Implementation Summary

Date: 2026-06-17

## Pricing Decision

Early production pricing remains:

- Monthly: `€3.99 / month`
- Yearly: `€24.99 / year`

RevenueCat localized package prices are still used whenever packages are available. The euro prices are fallback/placeholder display only when RevenueCat package prices are unavailable.

## No-Trial Decision

No trial is shown or launched by default.

Reason: the Free tier already acts as the trial through useful core study features and limited AI previews. LearnLift AI should validate baseline paid conversion and Premium usage before testing a 7-day trial, 14-day trial, or higher pricing.

Do not show trial text unless Google Play and RevenueCat return a real configured introductory offer.

## Yearly Best Value Logic

Yearly remains visible alongside Monthly and is marked:

```text
Best value
```

Yearly helper copy:

```text
Best value for steady weekly practice.
Save compared to monthly
```

Monthly remains visible and selectable with helper copy:

```text
Flexible access to more AI help and full Premium Study Packs.
```

## Paywall Headline And Subtitle

Premium screen headline:

```text
Study with more AI help every day
```

Premium screen subtitle:

```text
Unlock more explanations, AI quiz feedback, 7-day plans, and full Premium Study Packs when you want deeper practice.
```

## Premium Benefits

The Premium benefits section now uses:

```text
Available with Premium
```

Included benefits:

- More AI Coach explanations for wrong answers.
- Higher daily AI limits.
- AI Study Review after quizzes.
- 7-day AI Study Plans.
- Full Premium Study Packs.
- Smarter support for what to practice next.

## Active Vs Coming-Soon Pack Split

Active Premium Study Packs:

- SQL Interview Prep.
- QA Advanced.
- Automation Testing Basics.

Coming soon:

- Python Basics.
- JavaScript Basics.
- Business English.
- Technical Interview Prep.

The app does not claim coming-soon packs are available, and coming-soon packs must not open empty content.

## AI Limit Copy

Free AI limit reached copy:

```text
You've used today's free AI Coach explanations. Premium gives you more AI help for mistakes like this, and the local explanation is still available.
```

Actions:

- View Premium.
- Continue with local explanation.

The local explanation remains visible and local quota blocks still prevent Supabase calls.

## AI Quiz Review Copy

Free card/body copy:

```text
Get an AI review of what went wrong, which topics to revisit, and what to practice next.
```

Gate copy:

```text
AI Study Review is part of Premium. Your quiz summary stays free; Premium adds deeper feedback and next-session suggestions.
```

The local quiz summary remains free.

## AI Study Plan Copy

Free gate copy:

```text
Create a 7-day plan from your selected path, daily goal, weak topics, quiz results, and cards due for review.
```

CTA:

```text
View Premium
```

The screenshot demo state remains debug-only and release-disabled.

## Premium Study Pack Preview Copy

Preview dialog title:

```text
Unlock Premium Study Packs
```

Preview dialog body:

```text
Preview this pack before you upgrade. Premium unlocks every card and quiz question in SQL Interview Prep, QA Advanced, and Automation Testing Basics.
```

Actions:

- Preview pack.
- View Premium.
- Cancel.

Preview limit copy:

```text
That's the free preview for this pack. Premium unlocks the full pack so you can keep practicing.
```

Preview remains first 5 flashcards and first 5 quiz questions.

## Premium Active State

Premium active copy:

```text
Premium active. More AI help, AI Study Review, 7-day plans, and full Premium Study Packs are unlocked.
```

Duplicate purchase remains disabled when Premium is active. Restore purchases remains visible.

## Manage Subscription Behavior

When RevenueCat returns a non-empty `CustomerInfo.managementURL`, active Premium users see:

```text
Manage subscription
```

This appears on:

- Premium screen.
- Settings Premium section.

The app opens the returned RevenueCat management URL. It does not hardcode a Google Play subscription management URL.

If no management URL is available, the app keeps Google Play cancellation trust copy.

## Trust Copy

Premium screen trust copy:

```text
Purchases are handled securely by Google Play through RevenueCat. Cancel anytime in Google Play. Your free study tools stay available.
```

Avoided:

- Fake urgency.
- Fake scarcity.
- Guilt copy.
- Trial claims without configured offers.
- Guaranteed job, interview, exam, language, certification, or career-success claims.

## RevenueCat Checks Preserved

- Entitlement identifier: `premium`.
- Offering identifier: `default`.
- Product IDs: `learnlift_premium_monthly`, `learnlift_premium_yearly`.
- Base plan IDs: `monthly`, `yearly`.
- Product IDs are not used as entitlements.
- Restore purchases remains available.
- Purchase cancellation remains friendly.
- RevenueCat unavailable state remains friendly.
- Release builds must not use a RevenueCat Test Store key.
- Release builds fail if the Android key starts with `test_`.

## QA Checklist

- [ ] Free app remains useful: onboarding, Free paths, flashcards, quizzes, Daily Session, Smart Review, Adaptive Quiz, Progress, Settings.
- [ ] Premium screen shows the new headline/subtitle.
- [ ] Monthly and Yearly are both visible and selectable.
- [ ] Yearly shows `Best value` and `Save compared to monthly`.
- [ ] No trial copy appears.
- [ ] RevenueCat localized prices appear when packages are available.
- [ ] Fallback prices are used only when packages are unavailable.
- [ ] Purchase CTA is disabled when packages are unavailable.
- [ ] Purchase CTA is disabled while purchasing.
- [ ] Purchase CTA is disabled when Premium is active.
- [ ] Restore purchases is visible.
- [ ] Manage subscription appears only for active Premium users with a management URL.
- [ ] Active and coming-soon Premium Study Packs are split.
- [ ] AI limit reached copy keeps local explanation available.
- [ ] Blocked local AI limit does not call Supabase.
- [ ] AI Study Review gate says the local quiz summary stays free.
- [ ] AI Study Plan gate uses selected path, daily goal, weak topics, quiz results, and due review cards copy.
- [ ] Premium Study Pack preview dialog uses the updated body copy.
- [ ] Preview limit uses the updated full-pack copy.
- [ ] Coming-soon packs do not open empty content.
- [ ] Release build cannot use RevenueCat Test Store.
- [ ] Secret scan finds no committed secrets, keystores, AABs, local properties, app build outputs, or Gradle caches.
