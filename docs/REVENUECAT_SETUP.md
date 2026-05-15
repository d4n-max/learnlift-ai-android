# RevenueCat Setup

## Overview

LearnLift AI uses RevenueCat for Premium subscription entitlement support.

RevenueCat public SDK keys are safe to ship in the app. Do not add RevenueCat private API keys, OpenAI keys, Supabase service role keys, or Google Play service credentials to Android code or this repository.

## Expected Identifiers

RevenueCat entitlement:

```text
premium
```

RevenueCat offering:

```text
default
```

Google Play subscription product IDs:

```text
learnlift_premium_monthly
learnlift_premium_yearly
```

Recommended pricing:

- Monthly: `€3.99`
- Yearly: `€24.99`

## RevenueCat Dashboard Setup

1. Create a RevenueCat project.
2. Add an Android app with package name:

```text
com.learnliftai.app
```

3. Add Google Play service credentials if RevenueCat requires them for product syncing.
4. In Google Play Console, create subscription products:
   - `learnlift_premium_monthly`
   - `learnlift_premium_yearly`
5. In RevenueCat, import or create matching products.
6. Create entitlement:

```text
premium
```

7. Attach both products to the `premium` entitlement.
8. Create offering:

```text
default
```

9. Add monthly and yearly packages to the default offering.

## Android Configuration

The Android app reads a non-secret BuildConfig value:

```kotlin
BuildConfig.REVENUECAT_PUBLIC_API_KEY
```

Default placeholder:

```text
REVENUECAT_PUBLIC_API_KEY_HERE
```

For local/testing builds, pass the public SDK key as a Gradle property:

```powershell
.\gradlew.bat assembleDebug -PREVENUECAT_PUBLIC_API_KEY=public_android_sdk_key_here
```

Or place it in a local, uncommitted Gradle properties file.

Do not commit real private keys or Google service credentials.

## Testing With Google Play

RevenueCat purchases with Google Play subscriptions generally require:

- App package name matches Play Console and RevenueCat.
- Build installed from Google Play internal/closed testing track.
- Tester account is added as a license tester or test track tester.
- Products are active and available to the test build.
- Product IDs match exactly.
- RevenueCat offering has monthly/yearly packages.

If the app is sideloaded from Android Studio, purchase flows may fail or products may not appear. The app should handle this gracefully.

## Common Issues

### Products Not Found

Check:

- Product IDs match RevenueCat and Google Play.
- Products are active in Play Console.
- App was installed from Play testing track.
- RevenueCat has synced Google Play products.
- Offering `default` includes monthly and yearly packages.

### Package Name Mismatch

Confirm:

```text
com.learnliftai.app
```

is used in Android, Play Console, and RevenueCat.

### Entitlement Not Active After Purchase

Check:

- Products are attached to entitlement `premium`.
- Purchase completed in Google Play.
- Restore purchases was attempted.
- RevenueCat customer info shows the entitlement.

### Restore Purchases

Use the in-app Restore purchases button from Premium or Settings. This asks RevenueCat to sync purchases for the current Google Play account.

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
