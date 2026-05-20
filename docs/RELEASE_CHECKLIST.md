# LearnLift AI Release Checklist

Use this checklist for the next Google Play closed-testing and production-candidate build.

## Latest QA Snapshot

- Last QA update: 2026-05-20.
- Debug build: passed with `.\gradlew.bat assembleDebug`.
- Content validation: passed for English, Job Interview, and IT / QA expanded content counts.
- Merged manifest: contains `android.permission.INTERNET`, `android.permission.POST_NOTIFICATIONS`, `android.permission.RECEIVE_BOOT_COMPLETED`, and `com.android.vending.BILLING` when RevenueCat billing dependencies are merged.
- Physical device install: passed on `SM-A566B` for recent debug builds.
- Production readiness: not ready for production until closed testing, Google Play subscription purchase testing, final screenshots, Privacy Policy, and Data Safety submission pass.

## Repository

- [ ] Confirm repository path is `C:\Projects\learnlift-ai-android`.
- [ ] Confirm `git status` contains only intentional source and documentation changes.
- [ ] Confirm working tree is clean before generating the Play upload artifact.
- [ ] Confirm no generated build artifacts are committed.
- [ ] Confirm no private OpenAI keys, Supabase service role keys, RevenueCat private keys, keystores, or payment credentials are committed.

## Versioning

- [ ] Confirm package name remains `com.learnliftai.app`.
- [ ] Increase `versionCode` only when preparing the next uploaded Play build.
- [ ] Update `versionName` only when preparing a named release.
- [ ] Confirm Settings displays the intended app version.

## Build

- [ ] Accept/install Android SDK 35 components and licenses.
- [ ] Run `.\gradlew.bat :app:processDebugMainManifest`.
- [ ] Run `.\gradlew.bat assembleDebug`.
- [ ] Run `.\gradlew.bat installDebug` on a test device or emulator.
- [ ] Run a release build command when ready for upload.
- [ ] Generate a signed AAB for Play Console upload.
- [ ] Store signing materials outside the repository.

## Manifest And Permissions

- [ ] Confirm merged manifest contains `com.android.vending.BILLING`.
- [ ] Confirm merged manifest contains `android.permission.INTERNET`.
- [ ] Confirm merged manifest contains `android.permission.POST_NOTIFICATIONS`.
- [ ] Confirm merged manifest contains `android.permission.RECEIVE_BOOT_COMPLETED`.
- [ ] Confirm merged manifest path:
  `app\build\intermediates\merged_manifest\debug\processDebugMainManifest\AndroidManifest.xml`

## Content And Regression QA

- [ ] Run `node scripts\validate-study-content.mjs`.
- [ ] Confirm English has at least 80 flashcards and 60 quiz questions.
- [ ] Confirm Job Interview has at least 80 flashcards and 60 quiz questions.
- [ ] Confirm IT / QA has at least 60 flashcards and 50 quiz questions.
- [ ] Test Home.
- [ ] Test first-run onboarding and Settings restart onboarding.
- [ ] Test local daily reminder opt-in, permission denial/grant, time change, and disable behavior.
- [ ] Test Study Path Selection.
- [ ] Test Flashcards.
- [ ] Test Quiz.
- [ ] Test Daily Session.
- [ ] Test Progress.
- [ ] Test Settings.
- [ ] Test Premium screen.
- [ ] Test Premium gating rules from `docs/PREMIUM_GATING_RULES.md`.
- [ ] Test Smart Coach recommendations.
- [ ] Test AI fallback behavior.
- [ ] Test AI usage limits for Free and Premium states.
- [ ] Test light and dark theme.

## RevenueCat

- [ ] Confirm public SDK key is configured only as a client-side public key.
- [ ] Confirm entitlement identifier is exactly `premium`.
- [ ] Confirm offering identifier is `default`.
- [ ] Confirm current offering is set to `default`.
- [ ] Confirm package `monthly` maps to `learnlift_premium_monthly`.
- [ ] Confirm package `yearly` or `annual` maps to `learnlift_premium_yearly`.
- [ ] Confirm products are attached to entitlement `premium`.
- [ ] Confirm Test Store valid purchase activates Premium.
- [ ] Confirm failed purchase shows a friendly error.
- [ ] Confirm restore purchases refreshes CustomerInfo and plan state.

## Google Play Subscriptions

- [ ] Create subscription product `learnlift_premium_monthly`.
- [ ] Create base plan `monthly`.
- [ ] Set monthly type to auto-renewing.
- [ ] Set monthly target price to EUR 3.99.
- [ ] Create subscription product `learnlift_premium_yearly`.
- [ ] Create base plan `yearly`.
- [ ] Set yearly type to auto-renewing.
- [ ] Set yearly target price to EUR 24.99.
- [ ] Connect Google Play to RevenueCat.
- [ ] Import or add Google Play products in RevenueCat.
- [ ] Test from the Play closed-testing track.

## Play Console

- [ ] Upload signed AAB to Closed Testing.
- [ ] Add release notes from `docs/PLAY_CONSOLE_RELEASE_NOTES.md`.
- [ ] Confirm tester accounts are license testers / closed testers.
- [ ] Complete Data Safety using `docs/DATA_SAFETY_FINAL_V2.md`.
- [ ] Publish or update Privacy Policy before production.
- [ ] Use final listing from `docs/PLAY_STORE_LISTING_FINAL_V2.md`.
- [ ] Add final screenshots.
- [ ] Add feature graphic.
- [ ] Confirm app icon readiness.
- [ ] Complete production access request materials after closed testing evidence is available.

## Final Release Gate

- [ ] Closed testing install works.
- [ ] Monthly subscription purchase works.
- [ ] Yearly subscription purchase works.
- [ ] Purchase cancellation does not crash.
- [ ] Restore purchases works.
- [ ] Premium state persists after restart.
- [ ] Free core flows remain usable.
- [ ] Current three v1 study paths are not locked.
- [ ] Free AI limit reached opens Premium screen through `View Premium`.
- [ ] Premium active state uses higher AI limits.
- [ ] AI backend quota/failure fallback does not crash.
- [ ] No unexpected hard paywalls are present.

## Final v2 Production Checklist

- [ ] Confirm repository path is `C:\Projects\learnlift-ai-android`.
- [ ] Confirm git working tree is clean before release artifact generation.
- [ ] Confirm all source changes are intentional.
- [ ] Run `git diff --check`.
- [ ] Run a secret scan for OpenAI keys, Supabase service role keys, RevenueCat private keys, Google service account JSON, keystore files, `local.properties`, `app/build`, and `.gradle`.
- [ ] Run `.\gradlew.bat assembleDebug`.
- [ ] Run `.\gradlew.bat installDebug`.
- [ ] Run `.\gradlew.bat :app:processDebugMainManifest`.
- [ ] Confirm package name remains `com.learnliftai.app`.
- [ ] Increase `versionCode` only when preparing the next Play upload.
- [ ] Generate signed AAB with the correct upload key.
- [ ] Confirm `android.permission.INTERNET` is present.
- [ ] Confirm `android.permission.POST_NOTIFICATIONS` is present.
- [ ] Confirm `android.permission.RECEIVE_BOOT_COMPLETED` is present.
- [ ] Confirm billing permission is present through RevenueCat/Google Play Billing dependency.
- [ ] Confirm Supabase `ai-coach` function is deployed.
- [ ] Confirm Supabase Verify JWT setting is documented for current no-login testing.
- [ ] Confirm OpenAI billing/quota is enabled.
- [ ] Confirm Android has no OpenAI API key and no Supabase service role key.
- [ ] Confirm RevenueCat products are mapped.
- [ ] Confirm entitlement identifier is exactly `premium`.
- [ ] Confirm Play subscriptions are active/configured.
- [ ] Confirm Premium purchase, cancellation, and restore behavior in closed testing.
- [ ] Complete Data Safety with `docs/DATA_SAFETY_FINAL_V2.md`.
- [ ] Publish/update Privacy Policy.
- [ ] Upload final screenshots from `docs/SCREENSHOT_PLAN_V2.md`.
- [ ] Upload feature graphic.
- [ ] Upload app icon.
- [ ] Complete closed testing requirement and collect tester feedback.
- [ ] Submit production access request using `docs/PRODUCTION_ACCESS_PREP.md`.
