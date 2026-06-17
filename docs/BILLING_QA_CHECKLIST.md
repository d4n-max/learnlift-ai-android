# Billing QA Checklist

## Configuration

- App launches with the public RevenueCat SDK key configured.
- App launches with `REVENUECAT_ANDROID_PUBLIC_API_KEY` configured for Google Play / Closed Testing.
- RevenueCat keys can be loaded from Gradle properties, environment variables, or uncommitted `local.properties`.
- `local.properties` contains `REVENUECAT_ANDROID_PUBLIC_API_KEY`, `REVENUECAT_TEST_STORE_API_KEY`, and `USE_REVENUECAT_TEST_STORE=false`.
- Debug builds use the Android Store key by default.
- Debug builds use RevenueCat Test Store only when `USE_REVENUECAT_TEST_STORE=true`.
- Release builds always use the Android Store key and never the Test Store key.
- Release build fails if `REVENUECAT_ANDROID_PUBLIC_API_KEY` is missing or `REVENUECAT_ANDROID_PUBLIC_API_KEY_HERE`.
- Release build fails clearly if `REVENUECAT_ANDROID_PUBLIC_API_KEY` starts with `test_`.
- Gradle logs only safe RevenueCat key metadata: source, startsWithTest, and isPlaceholder.
- No OpenAI keys, RevenueCat private keys, or Supabase service role keys are in Android code/resources.
- Gradle dependency resolves `com.revenuecat.purchases:purchases:10.6.0`.
- Merged manifest includes `com.android.vending.BILLING`.
- App checks entitlement identifier `premium` only.
- App loads RevenueCat current offering first, with offering `default` as fallback.
- App prefers exact Google Play product IDs over looser Test Store/package matches when both are available.

## Premium Screen

- Premium screen opens from Home.
- Premium screen opens from Settings.
- Premium screen opens from Progress teaser.
- Premium screen opens from Premium Study Pack preview dialog.
- Premium screen opens from preview-limit CTA.
- Monthly package displays label `Monthly`.
- Yearly or annual package displays label `Yearly`.
- Yearly package shows `Best value`.
- Monthly helper copy says `Flexible access to more AI help and full Premium Study Packs.`
- Yearly helper copy says `Best value for steady weekly practice.` and `Save compared to monthly`.
- No trial copy appears unless RevenueCat/Google Play returns a real configured introductory offer.
- Premium headline says `Study with more AI help every day`.
- Premium subtitle mentions explanations, AI quiz feedback, 7-day plans, and full Premium Study Packs.
- Active Premium Study Packs are separated from coming-soon packs.
- Test Store prices are displayed only when explicitly enabled for debug Test Store testing.
- Google Play prices are displayed as returned by RevenueCat/Play Console.
- Test Store prices should not appear if real Google Play packages are available in the current/default offering.
- Placeholder prices display when offerings are unavailable: `Monthly €3.99 / month`, `Yearly €24.99 / year`.
- Purchase CTA is disabled when package is unavailable.
- Purchase CTA is disabled while purchase is in progress.
- Purchase CTA is disabled when Premium is active and shows `Premium active`.
- Restore purchases button shows loading text while restoring.
- Manage subscription appears for active Premium users when RevenueCat returns a management URL.
- Friendly message appears if products or offerings are unavailable: `Premium plans are temporarily unavailable. Please try again later.`

## A. RevenueCat Test Store QA

- Build debug with `USE_REVENUECAT_TEST_STORE=true`.
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
- App uses `REVENUECAT_ANDROID_PUBLIC_API_KEY`, not `REVENUECAT_TEST_STORE_API_KEY`.
- `USE_REVENUECAT_TEST_STORE=false`.
- Purchase dialog does not say `Test Store Purchase`.
- Tester account is a license tester and/or closed tester.
- Monthly package is visible.
- Yearly package is visible.
- Monthly package maps to product `learnlift_premium_monthly` and base plan `monthly`.
- Yearly package maps to product `learnlift_premium_yearly` and base plan `yearly`.
- Monthly price matches Play Console target price `€3.99`.
- Yearly price matches Play Console target price `€24.99`.
- Purchase flow opens Google Play purchase sheet.
- Successful monthly test purchase activates Premium.
- Successful yearly test purchase activates Premium.
- Cancelled purchase does not crash and leaves user in Free or existing Premium state.
- Failed purchase shows a friendly error.
- Restore purchases works.
- Manage subscription opens the RevenueCat/Google Play management URL when available.
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
- Free AI limit reached shows `View Premium`, keeps local explanation/recommendation visible, and does not offer repeated retry for a locally blocked request.
- Free AI limit reached says: `You've used today's free AI Coach explanations. Premium gives you more AI help for mistakes like this, and the local explanation is still available.`
- Free Premium Study Pack preview flow shows Preview pack, View Premium, and Cancel.
- Free Premium Study Pack preview dialog explains that Premium unlocks every card and quiz question in SQL Interview Prep, QA Advanced, and Automation Testing Basics.
- Preview limit says: `That's the free preview for this pack. Premium unlocks the full pack so you can keep practicing.`
- Premium active user opens available Premium Study Packs without preview limits.
- Coming-soon Premium Study Packs do not open empty content.

## Data Safety

- RevenueCat/Google Play purchase processing is documented.
- App still has no ads.
- App still has no login.
- App still has no cloud sync.
- AI backend context transfer remains user-initiated only.

## Closed Testing Conversion Feedback

Ask testers:

- Would you pay `€3.99/month` for more AI Coach explanations?
- Which Premium feature feels most valuable?
- Did the paywall feel clear?
- Did anything feel locked too early?
- Would yearly at `€24.99/year` feel fair?
- What would make Premium worth it?
