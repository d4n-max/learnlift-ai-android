# LearnLift AI Release Checklist

Use this checklist for the next Google Play closed-testing and production-candidate build.

## Latest QA Snapshot

- Last QA update: 2026-05-22.
- Version target: `versionCode = 4`, `versionName = "0.2.0"` for the v2 production candidate. This bumps the previous `3 / 0.1.2` candidate for the next Play upload.
- Debug build: blocked in this shell by Android SDK 35 license/access setup. `.\gradlew.bat clean` and `.\gradlew.bat :app:processDebugMainManifest` pass.
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
- [x] Set v2 production candidate `versionCode` to `4`.
- [x] Set v2 production candidate `versionName` to `0.2.0`.
- [ ] If `versionCode = 4` has already been uploaded to Google Play, bump the next candidate to `versionCode = 5` and `versionName = "0.2.1"` before upload.
- [ ] Confirm Settings displays the intended app version.

## Build

- [ ] Accept/install Android SDK 35 components and licenses.
- [x] Run `.\gradlew.bat clean`.
- [x] Run `.\gradlew.bat :app:processDebugMainManifest`.
- [ ] Run `.\gradlew.bat assembleDebug` after SDK 35 license/access setup is fixed.
- [ ] Run `.\gradlew.bat installDebug` on a test device or emulator.
- [ ] Run a release build command when ready for upload.
- [ ] Generate a signed AAB for Play Console upload.
- [ ] Store signing materials outside the repository.

## Manual Signed AAB Steps

Do not store keystore passwords, private key material, or generated AAB files in this repository.

1. Open Android Studio.
2. Open `C:\Projects\learnlift-ai-android`.
3. Sync Gradle.
4. Select Build -> Generate Signed Bundle / APK.
5. Select Android App Bundle.
6. Use the existing LearnLift upload key v2.
7. Select build variant: `release`.
8. Generate `app-release.aab`.
9. Confirm expected output:
   `app/build/outputs/bundle/release/app-release.aab`
10. Upload the AAB to Google Play Closed Testing first.
11. Use release notes from `docs/PLAY_CONSOLE_RELEASE_NOTES.md`.
12. Do not commit the AAB.

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
- [ ] Test monetization conversion UX from `docs/MONETIZATION_CONVERSION_PLAN.md`.
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
- [ ] Confirm Google Play base plan `monthly` maps to product `learnlift_premium_monthly`.
- [ ] Confirm Google Play base plan `yearly` maps to product `learnlift_premium_yearly`.
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

## Play Store Assets

- [ ] App icon: 512 x 512.
- [ ] Feature graphic: 1024 x 500.
- [ ] Phone screenshots: 2 to 8 screenshots.
- [ ] Optional 7-inch tablet screenshots.
- [ ] Optional 10-inch tablet screenshots.
- [ ] Capture screenshots after the v2 production-candidate build is installed.
- [ ] Screenshots show real AI response, Adaptive Quiz, Smart Review, Progress weak topics, Premium screen, and Onboarding.
- [ ] Screenshots avoid debug overlays, private data, test credentials, broken AI states, unavailable billing states, and guaranteed success claims.

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
- [ ] Free AI limit reached keeps local explanation/recommendation visible and does not call Supabase.
- [ ] Premium screen shows Yearly as `Best value` without hiding Monthly.
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

## Task 51 Final Status

Blocked by local SDK environment for full debug build in this shell.

Ready for signed AAB generation after:

- Android SDK 35 package access and licenses are fixed.
- `.\gradlew.bat assembleDebug` passes.
- Final Play upload version is confirmed unused in Google Play.
- Closed-testing billing and AI smoke tests are completed.
