# LearnLift AI Production Paywall Audit

Date: 2026-06-16

## Verdict

Production readiness verdict: ready with minor conversion-copy improvements recommended.

LearnLift AI's paywall and Premium gating are production-safe in the current codebase. The app keeps the Free experience useful, avoids an app-wide hard paywall, presents Premium as expanded AI and study support, uses RevenueCat/Google Play for purchases, keeps restore purchases visible, and blocks release builds from using a RevenueCat Test Store key.

The main production risk is not a blocking billing or gating issue. The larger opportunity is conversion clarity: the Premium screen and upgrade moments are accurate, but several messages are still feature-list oriented. The next iteration should make the value more concrete by explaining what Premium helps the learner do at the moment they hit the gate.

## Scope Reviewed

- Premium screen.
- Settings Premium section.
- AI limit reached screen and wrong-answer AI Coach flow.
- AI Quiz Review gating.
- AI Study Plan gating.
- Premium Study Pack preview flow.
- RevenueCat purchase and restore handling.
- `docs/PREMIUM_PLAN.md`.
- `docs/PREMIUM_GATING_RULES.md`.
- `docs/MONETIZATION_CONVERSION_PLAN.md`.
- `docs/BILLING_QA_CHECKLIST.md`.

## Current Paywall Status

The Premium screen contains the required production components:

- Current plan state: Free or Premium.
- Premium active state: purchase CTA becomes `Premium active`.
- Monthly and Yearly plan cards.
- Yearly `Best value` badge.
- Price display from RevenueCat packages when available.
- Placeholder pricing only when packages are unavailable.
- Purchase disabled when no RevenueCat package exists.
- Restore purchases button.
- `Maybe later` escape hatch.
- Trust copy: Google Play/RevenueCat handling, cancellation through Google Play, Free tools remain available.
- Friendly unavailable state: `Premium plans are temporarily unavailable. Please try again later.`

Billing implementation status:

- RevenueCat SDK is integrated.
- Entitlement check uses only `premium`.
- Offering lookup prefers current offering and falls back to `default`.
- Monthly/yearly package matching prefers exact Google Play product IDs before looser package or Test Store matches.
- Purchase cancellation returns a friendly message.
- Restore purchases refreshes CustomerInfo and updates the Premium state.
- Release Gradle config always selects `REVENUECAT_ANDROID_PUBLIC_API_KEY`.
- Release Gradle config fails if the Android Store key starts with `test_`.

## Conversion Moments

### Premium Screen

Status: production-safe.

The screen is clear and non-aggressive. It makes restore visible, disables unavailable purchases, and tells users the Free app remains usable. The strongest improvement is to replace generic headline and helper copy with outcome-driven wording.

Current headline:

```text
Unlock LearnLift AI Premium
```

Recommended headline:

```text
Study with more AI help every day
```

Recommended subtitle:

```text
Unlock more explanations, AI quiz feedback, 7-day plans, and full Premium Study Packs when you want deeper practice.
```

### Settings Premium Section

Status: production-safe.

Settings clearly shows the current plan, daily AI preview counts, View Premium, and Restore purchases. Premium active state is clear. This is a good low-pressure conversion and support location.

Recommended Free-state copy:

```text
Free includes flashcards, quizzes, daily sessions, progress, Smart Coach, and a few AI previews each day.
```

Recommended Premium-state copy:

```text
Premium is active. More AI explanations, AI Study Review, 7-day plans, and full Premium Study Packs are unlocked.
```

### AI Limit Reached And Wrong-Answer AI Coach

Status: production-safe.

This is the best conversion moment because the user has already answered a question and is asking for help. The implementation blocks exhausted Free AI calls locally and keeps the local explanation visible, so it avoids a hostile hard stop.

Current limit copy:

```text
You've used today's free AI Coach previews. Upgrade to Premium for more AI help, or continue with local explanations.
```

Recommended limit copy:

```text
You've used today's free AI Coach explanations. Premium gives you more AI help for mistakes like this, and the local explanation is still available.
```

Recommended buttons:

```text
View Premium
Continue with local explanation
```

### AI Quiz Review Gating

Status: production-safe.

Free users still receive the quiz summary and local Smart Coach recommendation. Premium gates the AI Study Review after the Free allowance. The copy is accurate and avoids exaggerated claims.

Recommended card copy:

```text
Get an AI review of what went wrong, which topics to revisit, and what to practice next.
```

Recommended gate copy:

```text
AI Study Review is part of Premium. Your quiz summary stays free; Premium adds deeper feedback and next-session suggestions.
```

### AI Study Plan Gating

Status: production-safe.

Free users see a teaser and can continue using Progress, Weak Topics, Smart Review, Daily Session, and local recommendations. The current copy is safe, but it can better explain what inputs Premium uses.

Recommended Free-state copy:

```text
Create a 7-day plan from your selected path, daily goal, weak topics, quiz results, and cards due for review.
```

Recommended CTA:

```text
View Premium
```

### Premium Study Pack Preview Flow

Status: production-safe.

Free users can preview available Premium packs before upgrading. Available Premium packs expose the first 5 flashcards and first 5 quiz questions. Coming-soon packs show a coming-soon dialog and do not open empty content.

Recommended dialog copy:

```text
Preview this pack before you upgrade. Premium unlocks every card and quiz question in SQL Interview Prep, QA Advanced, and Automation Testing Basics.
```

Recommended preview-limit copy:

```text
That's the free preview for this pack. Premium unlocks the full pack so you can keep practicing.
```

## Recommended Copy

### Paywall Headline

Primary:

```text
Study with more AI help every day
```

Alternative:

```text
Unlock deeper practice with LearnLift AI Premium
```

### Plan Card Copy

Monthly:

```text
Monthly
Flexible access to more AI help and full Premium Study Packs.
```

Yearly:

```text
Yearly
Best value for steady weekly practice.
```

Yearly badge:

```text
Best value
```

Yearly helper:

```text
Save compared to monthly
```

### Feature Bullets

Recommended Available now bullets:

- More AI Coach explanations for wrong answers.
- Higher daily AI limits.
- AI Study Review after quizzes.
- 7-day AI Study Plans.
- Full Premium Study Packs.
- Smarter support for what to practice next.

Recommended Premium Study Pack bullets:

- SQL Interview Prep.
- QA Advanced.
- Automation Testing Basics.
- More packs expanding over time.

Recommended Coming soon bullets:

- Advanced progress insights.
- More Premium study paths.
- Deeper weak-topic coaching.

### Upgrade Moment Copy

AI limit reached:

```text
You've used today's free AI Coach explanations. Premium gives you more AI help for mistakes like this, and the local explanation is still available.
```

AI Quiz Review:

```text
Your quiz summary stays free. Premium adds AI feedback, focus areas, and next-session suggestions.
```

AI Study Plan:

```text
Premium can build a 7-day plan from your goal, weak topics, quiz results, and due review cards.
```

Premium Study Pack preview:

```text
Preview a few cards and questions for free. Premium unlocks the full pack.
```

### Premium Active State

Recommended:

```text
Premium active. More AI help, AI Study Review, 7-day plans, and full Premium Study Packs are unlocked.
```

### Restore Purchases Placement

Keep restore purchases in both places:

- Premium screen, below the purchase CTA.
- Settings Premium section, always visible.

Recommended button text:

```text
Restore purchases
```

Recommended restoring text:

```text
Restoring purchases...
```

### Why Premium Explanation

Recommended short explanation:

```text
Free keeps your core study tools available. Premium is for learners who want more AI help, deeper review, weekly planning, and full Premium Study Packs.
```

### Trust Copy

Recommended:

```text
Purchases are handled securely by Google Play through RevenueCat. Cancel anytime in Google Play. Your free study tools stay available.
```

Avoid:

- Trial claims unless RevenueCat/Google Play returns real introductory offer data.
- Guaranteed job, exam, interview, certification, language, or career success claims.
- Fake scarcity, guilt-trip copy, or hidden close actions.

## Issues Found

### P1: None found

No production-blocking paywall, billing, entitlement, or hard-gating issue was found in the reviewed code and docs.

### P2: Premium screen copy is accurate but feature-list heavy

The main paywall explains what is included, but it does not yet strongly connect Premium to the user's in-app problem: needing more help after mistakes, reviewing weak topics, and planning the next week. This is a conversion issue, not a compliance blocker.

Recommended next task: update Premium screen headline, subtitle, feature bullets, and plan helper text.

### P2: No direct subscription management link is surfaced

`PremiumUiState` includes `managementUrl`, but the UI does not expose a `Manage subscription` action for active Premium users. Cancellation is still handled gracefully through Google Play copy, but an active-state management link would reduce support friction if implemented with an Android intent.

Recommended next task: add `Manage subscription` on the Premium screen and Settings when `managementUrl` is present.

### P3: Coming-soon packs appear in Premium benefits alongside available packs

The screen marks Python, JavaScript, Business English, and Technical Interview Prep as coming soon, so it is not misleading. However, mixing them under `Premium Study Packs include` may dilute clarity.

Recommended next task: split active Premium Study Packs and Coming soon packs into separate subsections.

### P3: Home Premium teaser emphasizes packs more than AI value

Home currently points to Premium Study Packs. That is useful, but Premium's strongest value may be the AI help moments. A future Home teaser could rotate or adapt based on whether the user has used AI previews.

Recommended next task: consider copy that says `Premium adds more AI help and full Study Packs` after users have tried AI Coach.

## Safe Changes To Implement Next

These are safe follow-up code changes, but were not made in this audit:

- Update Premium screen headline to `Study with more AI help every day`.
- Update Premium subtitle to mention explanations, AI quiz feedback, 7-day plans, and full Premium Study Packs.
- Replace feature bullets with more outcome-oriented copy.
- Split active Premium Study Packs from coming-soon packs.
- Update Monthly and Yearly helper text to explain use cases.
- Add `Manage subscription` for active Premium users when RevenueCat returns `managementUrl`.
- Update AI limit reached copy to explain that local explanations remain available.
- Update AI Study Plan teaser to name the data used: selected path, goal, weak topics, quiz results, due review cards.
- Keep Restore purchases visible in Premium and Settings.

## Manual QA Checklist

### Free Usefulness

- [ ] New Free user can complete onboarding without seeing Premium.
- [ ] Free user can choose any current v1 Free study path.
- [ ] Free user can use flashcards.
- [ ] Free user can use quizzes.
- [ ] Free user can use Daily Session.
- [ ] Free user can use Smart Review.
- [ ] Free user can use Adaptive Quiz basic mode.
- [ ] Free user can view Progress and Weak Topics.
- [ ] Free user can use local Smart Coach recommendations.
- [ ] Free user can use local/static explanations.

### Premium Screen

- [ ] Premium screen opens from Home or Premium teaser.
- [ ] Premium screen opens from Settings.
- [ ] Premium screen opens from AI limit reached state.
- [ ] Premium screen opens from AI Study Review gate.
- [ ] Premium screen opens from AI Study Plan gate.
- [ ] Premium screen opens from Premium Study Pack preview dialog.
- [ ] Monthly plan is visible.
- [ ] Yearly plan is visible.
- [ ] Yearly plan shows `Best value`.
- [ ] Prices match RevenueCat/Google Play localized package prices when packages are available.
- [ ] Placeholder prices are not purchasable when RevenueCat packages are unavailable.
- [ ] Purchase CTA is disabled while purchase is in progress.
- [ ] Purchase CTA is disabled when Premium is active.
- [ ] `Maybe later` exits the paywall.
- [ ] Restore purchases is visible.

### RevenueCat And Google Play

- [ ] Entitlement identifier is `premium`.
- [ ] Offering identifier is `default`.
- [ ] Product IDs are `learnlift_premium_monthly` and `learnlift_premium_yearly`.
- [ ] Release build uses `REVENUECAT_ANDROID_PUBLIC_API_KEY`.
- [ ] Release build does not use `REVENUECAT_TEST_STORE_API_KEY`.
- [ ] Release build fails if the Android Store key starts with `test_`.
- [ ] Google Play purchase sheet appears in closed testing or production.
- [ ] Purchase cancellation leaves the user in Free or existing Premium state.
- [ ] Purchase failure shows friendly copy.
- [ ] Successful purchase activates Premium.
- [ ] Restore purchases activates Premium for an existing subscriber.
- [ ] Restore with no active subscription shows friendly copy.
- [ ] Premium remains active after app restart.

### RevenueCat Unavailable

- [ ] App launches when RevenueCat is unavailable.
- [ ] Free study flows still work.
- [ ] Premium screen shows friendly unavailable copy.
- [ ] Purchase CTA is disabled if packages are unavailable.
- [ ] Restore failure shows friendly copy.
- [ ] No crash occurs on network failure.

### AI Gating

- [ ] Free user sees AI explanation previews left.
- [ ] Free user can request AI Coach explanations before the daily limit.
- [ ] Free AI limit reached shows Premium CTA.
- [ ] Free AI limit reached keeps the local explanation visible.
- [ ] Free blocked AI request does not call Supabase.
- [ ] Premium user receives higher AI explanation limit.
- [ ] Premium safety limit shows a non-billing safety-limit message.
- [ ] AI Quiz Review is gated after Free allowance.
- [ ] Free quiz summary remains visible.
- [ ] Premium user can generate AI Study Review within safety limits.
- [ ] Free AI Study Plan shows a Premium CTA without blocking Progress.
- [ ] Premium user can generate a 7-day AI Study Plan within safety limits.

### Premium Study Packs

- [ ] Free user can tap an available Premium Study Pack and see Preview pack, View Premium, and Cancel.
- [ ] Free preview opens first 5 flashcards.
- [ ] Free preview opens first 5 quiz questions.
- [ ] Preview mode is clearly labeled.
- [ ] End of flashcard preview shows View Premium and Back to Study Paths.
- [ ] Premium active user opens full available Premium packs.
- [ ] Coming-soon packs show coming-soon copy.
- [ ] Coming-soon packs do not navigate to empty content.

## Documentation Alignment

The reviewed docs are aligned with the implementation:

- `docs/PREMIUM_PLAN.md` correctly defines Free value, Premium value, pricing policy, RevenueCat configuration, and no hard core-feature gates.
- `docs/PREMIUM_GATING_RULES.md` correctly defines Free and Premium AI limits, allowed prompt locations, RevenueCat fallback behavior, and preview-pack rules.
- `docs/MONETIZATION_CONVERSION_PLAN.md` correctly frames conversion moments and avoids misleading trial or success claims.
- `docs/BILLING_QA_CHECKLIST.md` covers Test Store, Google Play closed testing, fallback states, cancellation, restore, and regression QA.

## Final Recommendation

Ship posture is acceptable after manual Google Play purchase and restore QA. The next product task should be a copy/UI polish pass, not a billing rewrite: sharpen the paywall headline, make plan cards more benefit-oriented, split active and coming-soon packs, add a visible subscription management link for active subscribers, and make every upgrade moment say what Free users can still do.
