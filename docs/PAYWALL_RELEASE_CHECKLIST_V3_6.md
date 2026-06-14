# Paywall Release Checklist v3.6

Last updated: 2026-06-08

Use this checklist before uploading a v3.6 build with Premium subscriptions enabled.

## Local Build Configuration

- [ ] `local.properties` exists locally and is not committed.
- [ ] `REVENUECAT_ANDROID_PUBLIC_API_KEY` is configured locally for Google Play / Closed Testing.
- [ ] `REVENUECAT_TEST_STORE_API_KEY` is present only for explicit debug Test Store testing.
- [ ] `USE_REVENUECAT_TEST_STORE=false` for release and Play-installed testing.
- [ ] Release build does not use a `test_` RevenueCat key.
- [ ] No RevenueCat private keys are in Android code or repository docs.
- [ ] No `.aab`, `.apk`, `.jks`, `.keystore`, `app/build`, `.gradle`, or `local.properties` files are committed.

## RevenueCat Configuration

- [ ] Entitlement identifier is exactly `premium`.
- [ ] Entitlement display name is `LearnLift AI Premium`.
- [ ] Offering identifier is exactly `default`.
- [ ] Offering `default` is current.
- [ ] Monthly package maps to `learnlift_premium_monthly`.
- [ ] Yearly package maps to `learnlift_premium_yearly`.
- [ ] Monthly package/base plan identifier is `monthly`.
- [ ] Yearly package/base plan identifier is `yearly` or recognized annual package.
- [ ] Products are attached to entitlement `premium`.
- [ ] RevenueCat dashboard shows active customer entitlement after a test purchase.

## Google Play Subscription Configuration

- [ ] Google Play subscription `learnlift_premium_monthly` exists.
- [ ] Monthly base plan `monthly` is active.
- [ ] Google Play subscription `learnlift_premium_yearly` exists.
- [ ] Yearly base plan `yearly` is active.
- [ ] Monthly price is correct in Play Console.
- [ ] Yearly price is correct in Play Console.
- [ ] Products are active for the intended testing or production track.
- [ ] Tester account is added as a license tester and/or closed tester.

## In-App Paywall QA

- [ ] Premium screen opens from Home.
- [ ] Premium screen opens from Settings.
- [ ] Premium screen opens from Progress Premium teaser.
- [ ] Premium screen opens from Premium Study Pack preview dialog.
- [ ] Premium screen opens from Flashcards preview-limit CTA.
- [ ] Monthly plan is visible.
- [ ] Yearly plan is visible.
- [ ] Yearly plan shows `Best value`.
- [ ] RevenueCat localized prices display when products are available.
- [ ] Purchase CTA is disabled when package is unavailable.
- [ ] Purchase CTA is disabled while purchase is in progress.
- [ ] Premium active state disables duplicate purchase CTA.
- [ ] `Maybe later` or equivalent escape hatch is visible.

## Purchase / Restore QA

- [ ] Purchase opens Google Play purchase sheet from a Play-installed build.
- [ ] Test Store dialog does not appear in production/release build.
- [ ] Cancelled purchase is handled gracefully.
- [ ] Failed purchase is handled gracefully.
- [ ] Successful monthly purchase activates `premium`.
- [ ] Successful yearly purchase activates `premium`.
- [ ] Restore purchases works.
- [ ] Premium active state persists after app restart.
- [ ] RevenueCat unavailable state does not crash the app.
- [ ] Core Free study tools remain usable if products are unavailable.

## Final Paywall Verdict

Paywall release status: **Blocked by paywall setup until Play-installed purchase and restore QA pass**.
