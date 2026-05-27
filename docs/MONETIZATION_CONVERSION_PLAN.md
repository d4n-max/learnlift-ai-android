# LearnLift AI Monetization Conversion Plan

Last updated: 2026-05-27

## Strategy

Premium should feel like the natural next step after users experience value from LearnLift AI, especially after they use AI Coach previews. Free should remain useful and trustworthy.

## Free Value

Free users can keep using:

- Current study paths.
- Flashcards.
- Smart Review.
- Quizzes.
- Daily Session.
- Basic Progress.
- Smart Coach.
- Local/static explanations.
- Basic Adaptive Quiz.
- Limited AI Coach previews.

There is no hard app-wide paywall.

## Premium Value

Premium focuses on more personalized help:

- More AI Coach explanations.
- Higher local AI daily limits.
- Premium AI access.
- Smarter learning support.
- Future advanced progress insights.
- Future premium study packs.
- Future AI study plans.

Premium copy must avoid guaranteed job, interview, exam, certification, fluency, or career-success claims.

## Main Conversion Moment

The strongest upgrade moment is after a wrong quiz answer when a Free user wants another AI explanation.

When previews remain, the UI shows:

```text
Free AI previews left today: X
```

When the Free limit is reached, the app blocks the AI call locally and shows:

```text
You've used today's free AI Coach previews. Upgrade to Premium for more AI help, or continue with local explanations.
```

The local explanation remains visible. The primary CTA is:

```text
View Premium
```

The app does not call Supabase/OpenAI after a local usage block.

## Soft Conversion Placements

Current soft conversion placements:

- Wrong-answer AI limit reached: `View Premium`.
- Quiz summary AI Study Review limit reached: `View Premium`.
- Progress 7-Day Study Plan limit reached: `View Premium`.
- Progress Advanced Insights teaser: Free users see Premium positioning without losing basic progress.
- Settings Premium section: shows Current plan and AI access.
- Home Premium teaser: subtle prompt for higher AI Coach limits.

No aggressive popups are used. Onboarding does not show a purchase screen.

## Paywall Copy

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
- Premium AI access.
- Smarter learning support.

Coming soon:

- Advanced progress insights.
- More study paths.
- Premium study packs.
- AI study plans.

## Pricing

Target production pricing:

- Monthly: `€3.99 / month`.
- Yearly: `€24.99 / year`.

RevenueCat localized package prices are shown when available. Placeholder prices are shown only when packages are unavailable.

The Yearly plan is visually recommended with a `Best value` badge. Monthly remains visible and selectable.

## RevenueCat Rules

- Entitlement identifier: `premium`.
- Offering identifier: `default`.
- Google Play product IDs:
  - `learnlift_premium_monthly`.
  - `learnlift_premium_yearly`.
- Base plan IDs:
  - `monthly`.
  - `yearly`.

Premium should become active only when:

```kotlin
customerInfo.entitlements["premium"]?.isActive == true
```

## Tester Feedback Questions

- Would you pay `€3.99/month` for more AI Coach explanations?
- Would yearly access at `€24.99/year` feel fair?
- Which Premium feature feels most valuable?
- Did the paywall feel clear?
- Did anything feel locked too early?
- Did the Free app still feel useful?
- What would make Premium worth it?

## Future Improvements

- Add server-side AI quotas when authentication or anonymous server-side identity exists.
- Add better post-upgrade success guidance.
- Test alternate Premium copy after closed testing feedback.
- Consider a limited-time free AI preview campaign only after cost controls are server-side.
