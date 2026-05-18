# LearnLift AI Release Checklist

Use this checklist for the next Google Play closed-testing and production-candidate build.

## Latest QA Snapshot

- Last QA update: 2026-05-18.
- Debug build: passed with `.\gradlew.bat assembleDebug`.
- Content validation: passed for English, Job Interview, and IT / QA expanded content counts.
- Merged manifest: contains `android.permission.INTERNET` and `com.android.vending.BILLING`.
- Physical device install: needs retest because ADB reported no connected devices during the Task 39 shell QA pass.
- Production readiness: not ready for production until physical-device smoke testing and Google Play subscription purchase testing pass.

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
- [ ] Confirm merged manifest path:
  `app\build\intermediates\merged_manifest\debug\processDebugMainManifest\AndroidManifest.xml`

## Content And Regression QA

- [ ] Run `node scripts\validate-study-content.mjs`.
- [ ] Confirm English has at least 80 flashcards and 60 quiz questions.
- [ ] Confirm Job Interview has at least 80 flashcards and 60 quiz questions.
- [ ] Confirm IT / QA has at least 60 flashcards and 50 quiz questions.
- [ ] Test Home.
- [ ] Test Study Path Selection.
- [ ] Test Flashcards.
- [ ] Test Quiz.
- [ ] Test Daily Session.
- [ ] Test Progress.
- [ ] Test Settings.
- [ ] Test Premium screen.
- [ ] Test Smart Coach recommendations.
- [ ] Test AI fallback behavior.
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
- [ ] Complete Data Safety using `docs/DATA_SAFETY_DRAFT.md`.
- [ ] Refresh store listing draft before production.
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
- [ ] AI backend quota/failure fallback does not crash.
- [ ] No unexpected hard paywalls are present.
