# RevenueCat Setup

## Overview

LearnLift AI uses RevenueCat for Premium subscription entitlement support. RevenueCat manages product lookup, purchase state, restore purchases, and the Premium entitlement. Google Play handles the actual subscription checkout for Play testing and production.

RevenueCat public SDK keys are safe to ship in the Android app. Do not add RevenueCat private API keys, OpenAI keys, Supabase service role keys, Google Play service credentials, keystores, or payment credentials to Android code or this repository.

Android SDK dependency:

```kotlin
implementation("com.revenuecat.purchases:purchases:10.6.0")
```

The app uses separate public SDK key configuration for Google Play and RevenueCat Test Store. Do not commit real keys to this repository.

```text
REVENUECAT_ANDROID_PUBLIC_API_KEY=...
REVENUECAT_TEST_STORE_API_KEY=...
USE_REVENUECAT_TEST_STORE=false
```

## Required Identifiers

RevenueCat entitlement identifier:

```text
premium
```

RevenueCat entitlement display name:

```text
LearnLift AI Premium
```

RevenueCat offering identifier:

```text
default
```

RevenueCat package/base plan names:

```text
monthly
yearly
annual
```

Google Play subscription product IDs:

```text
learnlift_premium_monthly
learnlift_premium_yearly
```

Google Play base plan IDs:

```text
monthly
yearly
```

Production target pricing:

- Monthly: `€3.99 / month`
- Yearly: `€24.99 / year`

The app checks only `customerInfo.entitlements["premium"]?.isActive == true` for Premium. Do not use `LearnLift AI Premium`, `monthly`, `yearly`, `annual`, `learnlift_premium_monthly`, or `learnlift_premium_yearly` as entitlement identifiers.

## Test Store Versus Google Play

RevenueCat Test Store products can appear as package/product names such as `monthly` and `yearly`, and Test Store prices may appear as values like `$9.99` and `$79.98`. Those values are expected only when `USE_REVENUECAT_TEST_STORE=true` in a debug build.

For Google Play closed testing and production, real subscription prices come from Google Play Console products:

- `learnlift_premium_monthly` with base plan `monthly`
- `learnlift_premium_yearly` with base plan `yearly`

The app displays localized package prices returned by RevenueCat. If RevenueCat returns Google Play products, the app displays Play Console prices. If RevenueCat returns Test Store packages in debug/local testing, the app displays Test Store prices. If both real Play products and looser test-style packages are present, the app prefers exact real product IDs:

- `learnlift_premium_monthly`
- `learnlift_premium_yearly`

If offerings or packages are unavailable, the app falls back to placeholder prices `Monthly €3.99 / month` and `Yearly €24.99 / year` and disables purchase CTA until a RevenueCat package is available.

The paywall marks Yearly as `Best value` while keeping Monthly visible and selectable. RevenueCat localized prices always take priority over placeholders when packages are available.

Debug builds use the Android Store public SDK key by default. To use RevenueCat Test Store locally, explicitly set `USE_REVENUECAT_TEST_STORE=true`.

Release builds always use `REVENUECAT_ANDROID_PUBLIC_API_KEY`. They never use the Test Store key, and Gradle fails the release build if the Android Store key starts with `test_`.

## Google Play Console Setup

1. Open Google Play Console for package name:

```text
com.learnliftai.app
```

2. Create subscription product:

```text
Product ID: learnlift_premium_monthly
Base plan ID: monthly
Type: Auto-renewing
Billing period: Monthly
Target price: €3.99
```

3. Create subscription product:

```text
Product ID: learnlift_premium_yearly
Base plan ID: yearly
Type: Auto-renewing
Billing period: Yearly
Target price: €24.99
```

4. Activate the products and base plans for the intended testing track.
5. Add tester accounts as license testers and/or closed testers.
6. Upload a build to the internal or closed testing track. Purchase flows for Play products should be tested from a Play-installed build, not only a sideloaded debug APK.

## RevenueCat Dashboard Setup

1. Create or open the RevenueCat project.
2. Add an Android app with package name:

```text
com.learnliftai.app
```

3. Connect Google Play to RevenueCat with the required Play service credentials in the RevenueCat dashboard. Do not commit those credentials to this repo.
4. Import or add Google Play products:
   - `learnlift_premium_monthly`
   - `learnlift_premium_yearly`
5. Create entitlement identifier:

```text
premium
```

6. Set entitlement display name:

```text
LearnLift AI Premium
```

7. Attach both Google Play products to entitlement `premium`.
8. Create offering:

```text
default
```

9. Add packages to offering `default`:
   - `monthly` -> `learnlift_premium_monthly`
   - `yearly` or `annual` -> `learnlift_premium_yearly`
10. Set offering `default` as the current offering.
11. Confirm a test purchase returns CustomerInfo where entitlement `premium` is active.

## Android Package Resolution

The app loads RevenueCat offerings by preferring:

1. RevenueCat current offering.
2. Offering identifier `default` as fallback.

Monthly package recognition:

- package identifier is `monthly`
- package identifier contains `monthly`
- package type is monthly
- product identifier contains `monthly`
- product identifier is exactly `learnlift_premium_monthly`

Yearly package recognition:

- package identifier is `yearly`
- package identifier is `annual`
- package identifier contains `yearly`
- package identifier contains `annual`
- package type is annual
- product identifier contains `yearly`
- product identifier contains `annual`
- product identifier is exactly `learnlift_premium_yearly`

Exact real Google Play product IDs are selected before looser Test Store/package matches.

## Android Configuration

The Android app reads these non-secret BuildConfig values:

```kotlin
BuildConfig.REVENUECAT_ANDROID_PUBLIC_API_KEY
BuildConfig.REVENUECAT_TEST_STORE_API_KEY
BuildConfig.USE_REVENUECAT_TEST_STORE
BuildConfig.REVENUECAT_PUBLIC_API_KEY
```

`BuildConfig.REVENUECAT_PUBLIC_API_KEY` is the selected runtime key:

- Debug default: Android Store key.
- Debug with `USE_REVENUECAT_TEST_STORE=true`: Test Store key.
- Release: Android Store key only.

Default placeholders are intentionally not real API keys:

```text
REVENUECAT_ANDROID_PUBLIC_API_KEY_HERE
REVENUECAT_TEST_STORE_API_KEY_HERE
```

For local/testing builds, pass keys as Gradle properties:

```powershell
.\gradlew.bat assembleDebug -PREVENUECAT_ANDROID_PUBLIC_API_KEY=public_android_store_sdk_key_here
```

To opt into Test Store explicitly for debug-only local testing:

```powershell
.\gradlew.bat assembleDebug -PREVENUECAT_ANDROID_PUBLIC_API_KEY=public_android_store_sdk_key_here -PREVENUECAT_TEST_STORE_API_KEY=test_store_public_sdk_key_here -PUSE_REVENUECAT_TEST_STORE=true
```

Do not set `USE_REVENUECAT_TEST_STORE=true` for Play closed-testing or release builds.

RevenueCat is configured once in `LearnLiftApplication.onCreate()`. Premium state refreshes use RevenueCat customer info and offerings through `PremiumRepository`.

## Testing With RevenueCat Test Store

- Test Store purchase dialog may show products named `monthly` and `yearly`.
- Test Store prices may show `$9.99` and `$79.98`.
- Test Store requires a debug build with `USE_REVENUECAT_TEST_STORE=true`.
- Debug builds use the Android Store key by default, so seeing `Test Store Purchase` means the Test Store flag/key is active.
- Test Store packages still must be attached to entitlement `premium`.
- `TEST VALID PURCHASE` should update Current plan to Premium.
- `TEST FAILED PURCHASE` should show a friendly purchase error.
- Restore purchases should refresh CustomerInfo and update Premium state.
- In debug builds, filter logcat by `LearnLiftPremium` to inspect selected product/package/offering and active/all entitlement identifiers.

## Testing With Google Play Closed Testing

RevenueCat purchases with Google Play subscriptions generally require:

- App package name matches Play Console and RevenueCat.
- Build is installed from Google Play internal or closed testing.
- Tester account is added as a license tester and/or closed tester.
- Subscription products and base plans are active.
- RevenueCat has synced Google Play products.
- Offering `default` is current.
- Packages `monthly` and `yearly`/`annual` are in offering `default`.
- Products are attached to entitlement `premium`.

## Common Issues

### Products Not Found

Check:

- Product IDs match RevenueCat and Google Play.
- Base plan IDs match `monthly` and `yearly`.
- Products and base plans are active in Play Console.
- App was installed from Play testing track.
- RevenueCat has synced Google Play products.
- Offering `default` includes monthly and yearly/annual packages.

### Entitlement Not Active After Purchase

Check:

- Products are attached to entitlement `premium`.
- Entitlement identifier is exactly `premium`.
- Test Store package `monthly`/`yearly` is attached to `premium`.
- Google Play products are attached to `premium`.
- RevenueCat customer profile shows entitlement `premium` active.
- Debug logcat under `LearnLiftPremium` shows `activeEntitlements=[premium]`.

### Package Name Mismatch

Confirm:

```text
com.learnliftai.app
```

is used in Android, Play Console, and RevenueCat.

## What This Integration Does Not Add

- Google Play Billing code directly.
- Backend billing server.
- Login or account system.
- Cloud sync.
- Analytics.
- Ads.
- OpenAI API keys.
- Supabase service role keys.
- RevenueCat private API keys.
