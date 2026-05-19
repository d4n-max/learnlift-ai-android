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

## Planned Premium Plan

Premium may eventually unlock:

- AI-powered answer explanations.
- AI Study Review.
- Personalized 7-day study plans.
- Advanced progress insights.
- Unlimited practice.
- Full study packs.
- Personalized recommendations.

Premium features should avoid guaranteed exam, job, career, or certification success claims.

## Public Pricing

Planned public pricing:

- Monthly: `€3.99`
- Yearly: `€24.99`

When RevenueCat offerings are configured, the app displays prices returned by RevenueCat. RevenueCat Test Store may return test prices such as `$9.99` and `$79.98`; Google Play closed testing and production prices should come from Play Console products.

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

Public Android SDK key:

- `test_uGjsdFBOtYwIdTQWqiwHkULbYor`

## Current Build Status

The current build includes:

- RevenueCat Android SDK `10.6.0`.
- Public RevenueCat Android SDK key.
- Premium entitlement check.
- Monthly/yearly package display.
- Purchase flow.
- Restore purchases.
- Friendly fallback if RevenueCat or Play products are not configured.

The current build does not include:

- Google Play Billing.
- Google Play Billing direct integration.
- RevenueCat private API keys.
- Hard Premium gates for core MVP content.
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
