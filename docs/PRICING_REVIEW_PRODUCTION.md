# LearnLift AI Pricing Review for Production

Last updated: 2026-06-17

## Summary Recommendation

For early production, keep LearnLift AI Premium at:

- Monthly: `€3.99 / month`
- Yearly: `€24.99 / year`

Use the yearly plan as the recommended `Best value` option, but keep the monthly plan visible and easy to choose. Do not launch with a free trial by default. First validate whether users understand the Premium value, whether AI usage limits create healthy upgrade intent, and whether paid conversion exists at the current price.

The practical posture is: launch with a friendly accessible price, protect Premium AI from feeling cheap through limits and packaging, then raise price only after there is evidence that conversion, retention, and AI cost coverage support it.

## 1. Current Pricing Assessment

The current planned pricing is well suited for an early production Android freemium app:

- It is low-friction for students, self-learners, and career switchers.
- It gives LearnLift AI a simple two-plan subscription model with no ads and no Stripe complexity.
- It creates a clear annual upsell without making the monthly plan feel punitive.
- It is safer while product-market fit, AI cost behavior, and Premium feature usage are still being validated.

The main risk is underpricing the AI layer. Premium includes AI Coach explanations, higher AI daily limits, AI Quiz Review, 7-day AI Study Plans, Premium Study Packs, and smart learning support. Those benefits have real usage cost and perceived value. The launch price should therefore be treated as an early production price, not a forever price.

## 2. Pros and Cons of `€3.99/mo + €24.99/yr`

### Pros

- Lower purchase friction for early users.
- Easier to defend for a study app without a large brand yet.
- Strong fit for Google Play self-serve conversion.
- Good for learning which Premium features actually drive upgrades.
- Annual price feels accessible and can help reduce churn.
- Leaves room for a later price increase once Premium value is clearer.

### Cons

- The annual plan is heavily discounted versus monthly.
- May anchor LearnLift AI as a low-price utility instead of a premium AI coach.
- Could be too low if AI usage costs grow quickly.
- May make future increases feel larger if not framed as an early production price.
- Annual revenue per subscriber may be modest if heavy AI users choose yearly.

## 3. Pros and Cons of `€4.99/mo + €39.99/yr`

### Pros

- Better reflects the value of AI-powered study support.
- Improves margin protection for AI usage.
- Makes Premium feel more substantial.
- Creates more annual revenue from committed users.
- Gives more room for discounts, offers, and localized pricing later.

### Cons

- Higher friction before the product has strong conversion proof.
- The yearly price is a larger jump from the current planned setup.
- May reduce early subscriber volume and slow learning.
- More pressure on onboarding, paywall copy, and feature completeness.
- Could feel premature if Premium Study Packs and AI personalization are still expanding.

## 4. Trial Recommendation

### No Trial

Recommended for launch.

Use no trial for early production because LearnLift AI already has a useful Free tier and limited AI previews. The Free experience acts as the trial. Users can study, hit AI value moments, and upgrade when they want more help.

This also avoids:

- Trial abuse.
- Confusion around Google Play trial renewal.
- Extra RevenueCat and Play Console offer QA.
- Users subscribing before they understand the Premium value.

### 7-Day Trial

Good future test after the purchase flow and Premium usage data are stable.

A 7-day trial may work if the app can reliably guide users into high-value Premium moments quickly, such as AI Quiz Review, 7-day Study Plan generation, and Premium Study Packs. It should be tested only after baseline conversion is known without a trial.

### 14-Day Trial

Not recommended for early production.

A 14-day trial may be too generous for the current product shape. It can delay revenue, increase trial-only usage of AI features, and make it harder to understand willingness to pay. Consider it only if the app later has a richer guided onboarding loop and strong trial-to-paid measurement.

## 5. Annual Discount Framing

At `€3.99/month`, paying monthly for 12 months equals `€47.88/year`. A `€24.99/year` plan is about 48% lower than monthly.

That is a strong discount. It should be framed simply:

```text
Best value
Save compared to monthly
```

Avoid overly precise savings claims unless the app calculates them from localized RevenueCat or Google Play prices. If localized prices vary, generic copy is safer and cleaner.

If moving later to `€4.99/month + €39.99/year`, the yearly plan would be about 33% lower than monthly. That is a healthier long-term discount structure and may be better once Premium value is proven.

## 6. Best Value Badge Recommendation

Put the `Best value` badge on Yearly.

Reasoning:

- It improves annual plan salience without hiding Monthly.
- It supports retention and cash flow.
- It keeps the paywall simple for Google Play users.
- It matches the current docs and planned paywall behavior.

Do not badge Monthly as recommended. Monthly should remain the low-commitment option, while Yearly is the value option.

## 7. What to Test First

Test in this order:

1. No trial at `€3.99/month + €24.99/year`.
2. Paywall copy emphasis: more AI Coach help versus study plans versus Premium Study Packs.
3. Yearly-first visual treatment while keeping Monthly selectable.
4. 7-day trial only after baseline purchase and retention data exist.
5. Higher pricing: `€4.99/month + €39.99/year`.

Do not test too many variables at once. The first useful signal is whether users who experience AI value are willing to pay at all.

## 8. When to Increase Price

Consider increasing price when several of these are true:

- Users are upgrading without frequent price objections.
- Premium conversion is healthy for the app's traffic quality.
- Premium users are retaining after the first renewal cycle.
- AI usage costs are predictable and covered by subscription revenue.
- AI Quiz Review, 7-day Study Plans, and Premium Study Packs are visibly valuable in usage data or tester feedback.
- The app has enough positive reviews, store credibility, or repeat usage to support a stronger price.

Recommended next increase path:

- Keep existing subscribers on their original price where feasible.
- Set new users to `€4.99/month`.
- Move yearly toward `€39.99/year` when the annual plan's value is easier to defend.

Avoid increasing price purely because competitors charge more. Raise price when LearnLift AI has stronger value proof.

## 9. How to Avoid Underpricing Premium AI

To avoid making Premium AI feel too cheap:

- Keep Free useful but limited around AI-powered moments.
- Preserve higher AI daily limits as a Premium benefit.
- Treat AI Quiz Review and 7-day AI Study Plans as high-value Premium features.
- Avoid unlimited-language unless the backend can safely support it.
- Use copy like `more AI help`, `deeper feedback`, and `focused study support`.
- Avoid guaranteed outcomes such as job, exam, certification, or career success.
- Monitor AI cost per Premium user before expanding limits.
- Consider raising yearly pricing before greatly expanding AI limits.

Premium should feel like deeper support, not just removal of annoyance.

## 10. Play Console Products and Base Plans

Do not change product IDs.

Keep the current Google Play subscription product IDs:

- `learnlift_premium_monthly`
- `learnlift_premium_yearly`

Keep the current base plan IDs:

- `monthly`
- `yearly`

Recommended early production setup:

| Product ID | Base plan ID | Billing period | Launch price |
| --- | --- | --- | --- |
| `learnlift_premium_monthly` | `monthly` | Monthly | `€3.99` |
| `learnlift_premium_yearly` | `yearly` | Yearly | `€24.99` |

RevenueCat should continue to use:

- Entitlement: `premium`
- Offering: `default`
- Monthly package mapped to `learnlift_premium_monthly`
- Yearly or annual package mapped to `learnlift_premium_yearly`

If testing trials later, configure them as Google Play introductory offers or base plan offers and let RevenueCat return the real offer data. Do not mention a trial in the app unless Play Console and RevenueCat are actually configured for that offer.

## Final Recommendation

Launch production with `€3.99/month + €24.99/year`, no trial, and Yearly marked as `Best value`.

This is the best practical setup for early production because it balances low purchase friction, simple RevenueCat and Google Play operations, and enough room to learn from real users. The alternative `€4.99/month + €39.99/year` is a strong candidate for a later increase once Premium AI value, retention, and cost coverage are proven.
