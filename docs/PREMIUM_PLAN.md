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
- AI weak-topic review.
- Personalized 7-day study plans.
- Unlimited quizzes.
- Unlimited daily sessions.
- Full study packs.
- Advanced progress insights.
- Personalized recommendations.

Premium features should avoid guaranteed exam, job, career, or certification success claims.

## Pricing Placeholders

Fallback UI placeholders:

- Monthly: `€3.99`
- Yearly: `€24.99`

When RevenueCat offerings are configured, the app displays the prices returned by RevenueCat/Google Play instead.

## RevenueCat Configuration

Expected product IDs:

- `learnlift_premium_monthly`
- `learnlift_premium_yearly`

Expected entitlement:

- `premium`

Expected offering:

- `default`

## Current Build Status

The current build includes:

- RevenueCat Android SDK.
- Public RevenueCat API key placeholder.
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
