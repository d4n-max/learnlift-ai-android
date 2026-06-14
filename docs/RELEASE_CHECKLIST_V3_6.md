# Release Checklist v3.6

Last updated: 2026-06-08

Use this checklist for the LearnLift AI v3.6 Google Play release/update.

## Current Release Reference

| Field | Current value |
| --- | --- |
| Package name | `com.learnliftai.app` |
| Current versionCode | `5` |
| Current versionName | `0.2.1` |
| RevenueCat entitlement | `premium` |
| RevenueCat offering | `default` |
| Monthly product | `learnlift_premium_monthly` |
| Yearly product | `learnlift_premium_yearly` |

## Before Build

- [ ] Repository path confirmed as `C:\Projects\learnlift-ai-android`.
- [ ] Git status clean.
- [ ] `local.properties` present locally but not committed.
- [ ] RevenueCat Android public SDK key configured.
- [ ] `USE_REVENUECAT_TEST_STORE=false`.
- [ ] No Test Store key selected for release.
- [ ] `versionCode` incremented above previous Play upload.
- [ ] `versionName` updated if needed and documented.
- [ ] Package name remains `com.learnliftai.app`.
- [ ] Supabase `ai-coach` deployed.
- [ ] Content validation passes.
- [ ] `docs/QA_CHECKLIST.md` completed.
- [ ] `docs/PAYWALL_RELEASE_CHECKLIST_V3_6.md` completed.
- [ ] `docs/SUPABASE_AI_RELEASE_CHECKLIST_V3_6.md` completed.
- [ ] `docs/DATA_SAFETY_V3_6_REVIEW.md` reviewed.
- [ ] Store listing ready from `docs/PLAY_STORE_LISTING_V3_FINAL.md`.
- [ ] Screenshot plan ready from `docs/SCREENSHOT_PLAN_V3_6.md`.
- [ ] Feature graphic plan ready from `docs/FEATURE_GRAPHIC_V3_6_PLAN.md`.

## Build

- [x] Run `.\gradlew.bat assembleDebug`.
- [x] Run `node scripts\validate-study-content.mjs`.
- [x] Optionally run `.\gradlew.bat installDebug`.
- [ ] If install fails due to signature mismatch:
  - [ ] Run `adb uninstall com.learnliftai.app`.
  - [ ] Rerun `.\gradlew.bat installDebug`.
- [ ] In Android Studio, select Build -> Generate Signed Bundle / APK.
- [ ] Select Android App Bundle.
- [ ] Select release variant.
- [ ] Use the correct upload key.
- [ ] Generate `app-release.aab`.
- [ ] Confirm the generated AAB path.
- [ ] Do not commit the AAB.

## Upload

- [ ] Open Google Play Console.
- [ ] Choose Closed Testing or Production.
- [ ] Upload `app-release.aab`.
- [ ] Do not upload `app-debug.apk`.
- [ ] Paste v3.6 release notes from `docs/PLAY_CONSOLE_RELEASE_NOTES.md`.
- [ ] Verify Data Safety.
- [ ] Verify Privacy Policy.
- [ ] Verify store listing.
- [ ] Upload screenshots.
- [ ] Upload feature graphic.
- [ ] Verify app icon.
- [ ] Review Play Console warnings.
- [ ] Submit rollout or review.

## Post Upload

- [ ] Install from Play testing or production track.
- [ ] Verify real prices.
- [ ] Verify Google Play purchase sheet opens.
- [ ] Verify cancelled purchase is graceful.
- [ ] Verify successful purchase activates Premium.
- [ ] Verify restore purchases works.
- [ ] Verify Premium active unlocks full available Premium packs.
- [ ] Verify no Test Store dialog appears.
- [ ] Verify AI Coach explanation works.
- [ ] Verify AI Quiz Review works.
- [ ] Verify 7-Day Study Plan works.
- [ ] Verify local fallback works when offline.
- [ ] Verify Free core flows remain usable.

## Artifact Safety

- [x] No `.aab` committed.
- [x] No `.apk` committed.
- [x] No `.jks` or `.keystore` committed.
- [x] No `local.properties` committed.
- [x] No `app/build` committed.
- [x] No `.gradle` committed.
- [x] No OpenAI keys committed.
- [x] No Supabase service role keys committed.
- [x] No RevenueCat private keys committed.

Latest local verification run, 2026-06-08:

- `.\gradlew.bat assembleDebug`: passed.
- `node scripts\validate-study-content.mjs`: passed.
- `.\gradlew.bat installDebug`: passed on `Pixel_8_Pro(AVD) - 17`.
- Secret scan: no OpenAI-looking `sk-` values, Supabase service role key matches, or RevenueCat private key assignments found in tracked-scope scan. `OPENAI_API_KEY` references were documentation/example placeholders only.
- Ignored generated/local files observed: `local.properties`, `.gradle/`, `app/build/`, and `app/release/app-release.aab`; keep them uncommitted.

## Final Status

Current v3.6 release checklist status: **Ready for screenshot capture**.

Not ready for signed AAB upload until final QA, Play-installed paywall verification, Supabase AI production smoke tests, Data Safety review, screenshot capture, and feature graphic upload assets are complete.
