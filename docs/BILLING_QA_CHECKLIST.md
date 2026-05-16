# Billing QA Checklist

## Configuration

- App launches with the public RevenueCat SDK key configured.
- App still launches if a different public SDK key is supplied through `REVENUECAT_PUBLIC_API_KEY`.
- No OpenAI keys, RevenueCat private keys, or Supabase service role keys are in Android code/resources.
- Gradle dependency resolves `com.revenuecat.purchases:purchases:10.6.0`.
- Merged manifest includes `com.android.vending.BILLING`.
- App checks entitlement identifier `premium` only.

## Premium Screen

- Premium screen opens from Home.
- Premium screen opens from Settings.
- Premium screen opens from Progress teaser.
- Monthly package displays label `Monthly`.
- Yearly or annual package displays label `Yearly`.
- Test Store prices are displayed as returned by RevenueCat.
- Google Play prices are displayed as returned by RevenueCat/Play Console.
- Placeholder prices display when offerings are unavailable: `Monthly €3.99`, `Yearly €24.99`.
- Purchase CTA is disabled when package is unavailable.
- Purchase CTA is disabled while purchase is in progress.
- Purchase CTA is disabled when Premium is active and shows `Premium active`.
- Restore purchases button shows loading text while restoring.
- Friendly message appears if products or offerings are unavailable.

## A. RevenueCat Test Store QA

- Test Store purchase dialog opens.
- Test Store products `monthly` and `yearly` are visible when configured.
- Test Store prices such as `$9.99` and `$79.98` display as returned by RevenueCat.
- `TEST VALID PURCHASE` activates Premium.
- Current plan updates to Premium after valid purchase.
- RevenueCat customer profile shows entitlement `premium` active.
- `TEST FAILED PURCHASE` shows a friendly error and does not crash.
- Cancelled purchase shows a non-scary cancellation message.
- Restore purchases refreshes CustomerInfo and updates Premium state.
- Restart app preserves Premium state.
- Debug logcat under `LearnLiftPremium` shows active/all entitlement identifiers and purchased product identifiers.

## B. Google Play Closed Testing QA

- App is installed from Google Play internal or closed testing track.
- Tester account is a license tester and/or closed tester.
- Monthly package is visible.
- Yearly package is visible.
- Monthly price matches Play Console target price `€3.99`.
- Yearly price matches Play Console target price `€24.99`.
- Purchase flow opens Google Play purchase sheet.
- Successful monthly test purchase activates Premium.
- Successful yearly test purchase activates Premium.
- Cancelled purchase does not crash and leaves user in Free or existing Premium state.
- Failed purchase shows a friendly error.
- Restore purchases works.
- Entitlement `premium` remains active after app restart.
- RevenueCat customer profile shows the matching Google Play product and active entitlement `premium`.

## C. Fallback QA

- RevenueCat key missing or invalid: app remains usable.
- Products unavailable: Premium screen shows friendly unavailable message.
- Offering unavailable: Premium screen shows fallback placeholder prices.
- Network unavailable: Premium screen does not crash.
- User remains Free when entitlement is inactive.
- Home still works.
- Study flows still work.
- Restore failure shows a friendly message.
- Purchase completed but entitlement inactive shows: `Purchase completed, but Premium entitlement is not active yet. Check RevenueCat product-entitlement setup.`

## D. Regression QA

- Home works.
- Study Path Selection works.
- Flashcards work.
- Quiz works.
- Daily Session works.
- Progress works.
- Settings works.
- Premium screen works.
- DataStore progress persists.
- Local JSON study content loads.
- Smart Coach works.
- AI fallback works.
- No feature is hard-blocked unexpectedly.
- Closed testers can continue using the app if RevenueCat products are unavailable.

## Data Safety

- RevenueCat/Google Play purchase processing is documented.
- App still has no ads.
- App still has no login.
- App still has no cloud sync.
- AI backend context transfer remains user-initiated only.
