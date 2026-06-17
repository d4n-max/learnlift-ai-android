# LearnLift AI Premium Plan

## Overview

Premium is the planned monetization layer for LearnLift AI. The current build includes RevenueCat SDK integration, Premium entitlement checks, a billing-ready Premium screen, purchase flow handling, and restore purchases.

No current MVP feature is locked behind Premium. Testers can continue using Home, study path selection, flashcards, quizzes, daily sessions, progress, settings, local persistence, local JSON content, and rule-based Smart Coach tips.

## Free Plan

The Free plan currently includes:

- Starter study paths.
- Flashcards.
- Quizzes.
- Daily study sessions.
- Basic progress tracking.
- Rule-based Smart Coach tips.
- Local-only DataStore persistence.
- Limited AI Coach previews.

## Planned Premium Plan

Premium is positioned as more AI help and deeper study support, not as a requirement to use the app.

Available with Premium:

- More AI Coach explanations for wrong answers.
- Higher daily AI limits.
- AI Study Review after quizzes.
- 7-day AI Study Plans.
- Full Premium Study Packs.
- Smarter support for what to practice next.

Premium coming soon or expanding:

- Advanced progress insights.
- More study pack content.
- More personalized recommendations.

Premium Study Packs available now:

- SQL Interview Prep.
- QA Advanced.
- Automation Testing Basics.

Premium Study Packs coming soon:

- Python Basics.
- JavaScript Basics.
- Business English.
- Technical Interview Prep.

Premium features should avoid guaranteed exam, job, career, or certification success claims.

## Public Pricing

Planned public pricing:

- Monthly: `€3.99 / month`
- Yearly: `€24.99 / year`
- Trial: none at launch

When RevenueCat offerings are configured, the app displays prices returned by RevenueCat. RevenueCat Test Store may return test prices such as `$9.99` and `$79.98`; Google Play closed testing and production prices should come from Play Console products.

Placeholder prices are used only when RevenueCat offerings or packages are unavailable.

The Yearly plan should be visually recommended with a `Best value` badge and `Save compared to monthly` helper while keeping the Monthly plan visible and selectable. Do not mention a 7-day or 14-day trial unless Google Play and RevenueCat return a real configured introductory offer.

## RevenueCat Configuration

Expected product IDs:

- `learnlift_premium_monthly`
- `learnlift_premium_yearly`

Expected entitlement:

- `premium`

The app uses `customerInfo.entitlements["premium"]?.isActive == true` as the Premium check. It does not use the display name `LearnLift AI Premium`, package IDs `monthly` / `yearly`, or product IDs `learnlift_premium_monthly` / `learnlift_premium_yearly` as entitlement identifiers.

Expected offering:

- `default`

Expected RevenueCat package/base plan names:

- `monthly`
- `yearly`
- `annual`

If RevenueCat Test Store displays `monthly` during a test purchase, that is the package/base plan selected for purchase. The `monthly` package still must be attached to the `premium` entitlement in RevenueCat before the app can switch to Premium after purchase.

Google Play base plan IDs:

- `monthly`
- `yearly`

The Android paywall prefers RevenueCat current offering, then falls back to offering `default`. It recognizes monthly/yearly packages by package identifier, package type, and product identifier, and prefers exact real Google Play product IDs over looser Test Store matches when both are available.

RevenueCat public SDK key configuration:

- `REVENUECAT_ANDROID_PUBLIC_API_KEY` for Google Play / Closed Testing.
- `REVENUECAT_TEST_STORE_API_KEY` for explicit debug-only Test Store testing.
- `USE_REVENUECAT_TEST_STORE=false` by default.

Debug builds use the Android Store key by default. Test Store is used only when `USE_REVENUECAT_TEST_STORE=true`. Release builds always use the Android Store key and fail clearly if it starts with `test_`.

## Current Build Status

The current build includes:

- RevenueCat Android SDK `10.6.0`.
- Separate RevenueCat Android Store and Test Store public SDK key configuration.
- Premium entitlement check.
- Premium gating rules for higher AI usage limits.
- Premium Study Packs foundation with Free preview access.
- Premium Study Pack v3.5 UX polish with richer pack cards, pack summary preview dialogs, coming-soon dialogs, and preview-limit CTAs.
- Local starter content for SQL Interview Prep, QA Advanced, and Automation Testing Basics.
- Premium-ready AI Quiz Review on quiz summary.
- Premium-gated 7-day AI Study Plan on Progress.
- Production paywall headline: `Study with more AI help every day`.
- Production paywall subtitle: `Unlock more explanations, AI quiz feedback, 7-day plans, and full Premium Study Packs when you want deeper practice.`
- Clearer 7-Day Study Plan teaser copy for Free users:
  - `Create a 7-day AI Study Plan`
  - `Create a 7-day plan from your selected path, daily goal, weak topics, quiz results, and cards due for review.`
  - `View Premium`
- Premium benefits section copy uses `Available with Premium` instead of `Available now`.
- Premium active state copy explains that more AI help, AI Study Review, 7-day plans, and full Premium Study Packs are unlocked.
- Active subscribers see `Manage subscription` when RevenueCat returns a management URL.
- Monthly/yearly package display.
- Current/default offering loading.
- Real Google Play product ID recognition for `learnlift_premium_monthly` and `learnlift_premium_yearly`.
- Purchase flow.
- Restore purchases.
- Friendly fallback if RevenueCat or Play products are not configured.

The current build does not include:

- Google Play Billing.
- Google Play Billing direct integration.
- RevenueCat private API keys.
- Hard Premium gates for core MVP content.
- Locked current v1 study paths.
- Firebase.
- Authentication.
- Cloud sync.
- Analytics.

RevenueCat uses Google Play Billing underneath the SDK. LearnLift AI does not call Google Play Billing directly.

## Future Billing Implementation

Future billing work may add stricter Premium limits after closed testing validates purchase behavior.

Future work may include:

- Clear cancellation and renewal messaging.
- Play Store compliant product text.
- QA for free and paid states.
- No direct AI provider secrets in the Android app.
- Optional Premium gating for higher AI usage limits.
- Optional advanced insights access for Premium users.

## Closed Testing Policy

During closed testing, current Free features remain usable even if billing products, RevenueCat offerings, or Google Play purchases are unavailable. Premium is optional and should not hard-block Home, study path selection, flashcards, quizzes, daily sessions, progress, settings, Smart Coach, local content, DataStore persistence, or AI fallback behavior.

AI Coach UI may use subtle Premium-ready positioning during closed testing, but current AI actions remain available for validation and keep local fallback behavior when the backend is unavailable.

Task 43 adds local AI usage limits for cost control. Free users receive limited AI previews, while Premium users receive higher local safety limits. These limits apply only to AI-powered calls and do not block flashcards, quizzes, daily sessions, progress, local explanations, or rule-based Smart Coach recommendations.

Task 44 defines tester-safe gating rules. Premium currently expands AI daily limits and Premium status UI while keeping all current v1 study paths and core study modes usable for Free users. Advanced insights and future premium study packs should be shown as coming soon until they are implemented.

## Conversion UX

Premium prompts should appear after users experience value, especially when a Free user reaches the daily AI Coach preview limit after trying AI explanations.

Primary conversion message:

```text
You've used today's free AI Coach explanations. Premium gives you more AI help for mistakes like this, and the local explanation is still available.
```

The app should keep local explanations visible, offer `View Premium`, and avoid repeated retry prompts when the request is blocked locally by usage limits.

Premium Study Pack conversion should happen in context. Free users can preview available packs first, then see `View Premium` after the preview limit. Coming-soon packs should remain visible but must not open empty content or imply unavailable packs are complete.
