# Release Paywall Verification v3.8

Last updated: 2026-06-17

Use this checklist before generating the signed v3.8 AAB and again after installing the uploaded build from Google Play.

## Required Local Configuration

- [ ] `local.properties` exists locally and is not committed.
- [ ] `REVENUECAT_ANDROID_PUBLIC_API_KEY` is configured locally for Google Play or Closed Testing.
- [ ] `REVENUECAT_TEST_STORE_API_KEY` is present only for explicit debug Test Store testing.
- [ ] `USE_REVENUECAT_TEST_STORE=false` for release and Play-installed testing.
- [ ] RevenueCat config loads from Gradle property, environment variable, or `local.properties`.
- [ ] Gradle config log shows safe metadata only and does not print full key values.
- [ ] Release build fails if the Android public key is missing or placeholder.
- [ ] Release build uses the Android RevenueCat public SDK key.
- [ ] Release build does not use a key starting with `test_`.
- [ ] No RevenueCat private or secret API keys are committed.

## RevenueCat Dashboard

- [ ] Entitlement identifier is exactly `premium`.
- [ ] Offering identifier is exactly `default`.
- [ ] Current offering is `default`.
- [ ] Monthly product is active: `learnlift_premium_monthly`.
- [ ] Yearly product is active: `learnlift_premium_yearly`.
- [ ] Monthly base plan is active: `monthly`.
- [ ] Yearly base plan is active: `yearly`.
- [ ] Monthly package maps to `learnlift_premium_monthly`.
- [ ] Yearly package maps to `learnlift_premium_yearly`.
- [ ] Both products are attached to entitlement `premium`.

## App Behavior Before Upload

- [ ] Premium screen opens from Home.
- [ ] Premium screen opens from Settings.
- [ ] Premium screen opens from Progress.
- [ ] Premium screen opens from Premium Study Pack preview flow.
- [ ] Monthly and yearly plans are visible.
- [ ] `BuildConfig.REVENUECAT_PUBLIC_API_KEY` is not placeholder for the tested build.
- [ ] Purchase CTA is disabled when products are unavailable.
- [ ] Restore purchases remains available.
- [ ] Free study paths remain usable if RevenueCat is unavailable.

## Google Play Verification After Upload

- [ ] Install v3.8 from Google Play Closed Testing or Production.
- [ ] Confirm real localized monthly price appears.
- [ ] Confirm real localized yearly price appears.
- [ ] Confirm Google Play purchase sheet opens.
- [ ] Confirm purchase dialog does not say `Test Store Purchase`.
- [ ] Confirm successful monthly purchase activates Premium.
- [ ] Confirm successful yearly purchase activates Premium.
- [ ] Confirm Premium active state persists after app restart.
- [ ] Confirm Restore purchases works.
- [ ] Confirm Premium active unlocks full available Premium packs.
- [ ] Confirm cancelled purchase is graceful and does not crash.

## Release Verdict

Paywall status: **Blocked by paywall setup until Play-installed purchase and restore QA pass**.

The Android release guard is present in Gradle, but final release readiness requires Google Play-installed billing verification.
