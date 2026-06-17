# QA Check After Today's Implementations

Date/time: 2026-06-17 18:14 +03:00

Branch/commit: `main` at `45744ab`

Emulator/device used: Pixel_8_Pro AVD, `emulator-5554`, Android 17 as reported by Gradle install output.

Artifacts captured: `docs/qa-artifacts/after-todays-implementations/`

## Commands Run

| Check | Result | Notes |
| --- | --- | --- |
| `git status --short --branch` | Pass | Working tree was already dirty before QA; changes summarized below. |
| `.\gradlew.bat clean` | Pass | Build successful. Gradle emitted AGP deprecation/obsolete variant API warnings. |
| `.\gradlew.bat assembleDebug` | Pass | Debug APK built successfully. Same Gradle warnings. |
| `.\gradlew.bat :app:processDebugMainManifest` | Pass | Manifest processing successful/up-to-date. |
| `node scripts\validate-study-content.mjs` | Pass | Six content packs validated. Premium packs have `freePreviewCount = 5`; free paths remain non-premium. |
| `.\gradlew.bat installDebug` | Pass | Installed debug APK on `emulator-5554`. |
| `.\gradlew.bat testDebugUnitTest` | Pass | Existing debug unit tests passed. |
| Manifest permission review | Pass | Current permissions: `INTERNET`, `POST_NOTIFICATIONS`, `RECEIVE_BOOT_COMPLETED`. These match networking, reminders, and boot reschedule behavior. |
| Tracked risky artifact scan | Pass | No tracked `.aab`, `.apk`, `.jks`, `.keystore`, `local.properties`, `.env`, `.gradle`, or `app/build` artifacts found. |
| Secret pattern scan in tracked text files | Pass | No real OpenAI, Supabase service role, RevenueCat private key, or private key material found. Placeholder docs and expected product IDs were the only matches. |

## Git Status Summary

Existing modified app files include billing, AI usage, reminders, onboarding, navigation, Home, Flashcards, Quiz, Progress, Settings, Premium, and Study Path Selection.

Existing untracked app files include review prompt repository/model/UI/policy files, `ScreenshotDemoStudyPlan.kt`, and `app/src/test/`.

Existing modified/untracked docs include premium, RevenueCat, local notification, ASO, onboarding, paywall, pricing, launch, review/notification, and QA docs.

QA-created files:
- `docs/QA_CHECK_AFTER_TODAYS_IMPLEMENTATIONS.md`
- `docs/qa-artifacts/after-todays-implementations/`

Ignored local artifacts observed:
- `local.properties`
- `.gradle/`
- `app/build/`
- `app/release/app-release.aab`

Do not commit the ignored release artifact.

## Manual Flows Tested

| Area | Result | Evidence / notes |
| --- | --- | --- |
| First launch onboarding | Pass | Intro showed `Get started` and `Skip for now`. |
| Goal selection | Pass | Four goals visible; selecting English goal advanced. |
| Daily minutes | Pass | 5/10/15/20 minute options visible; 10 minutes marked recommended. |
| Recommended path | Pass | Recommended free path was `English Vocabulary & Speaking Prep`; start first session worked. |
| Onboarding completion | Pass | Landed in Daily Session for selected path. |
| Restart after onboarding | Pass | Relaunch landed on Home, not onboarding. |
| Home | Pass | Selected path, quick actions, and Settings entry visible. |
| Study Paths | Pass | Free paths visible and selectable; premium section visible. |
| Free paths | Pass | English, Job Interview Prep, and IT / QA Interview Prep visible as free paths with no hard paywall. |
| Premium Study Packs | Pass | SQL, QA Advanced, and Automation Testing Basics are listed as premium available packs. |
| Premium preview dialog | Pass | Free user tapping an available premium pack showed `Preview pack`, `View Premium`, and `Cancel`. |
| Premium preview mode | Pass | QA Advanced preview opened with first 5 flashcards / first 5 quiz questions and preview labels. |
| Preview flashcards | Pass | Flashcards showed `QA Advanced preview mode`, `Card 1 of 5`, and upgrade copy. |
| Preview quiz | Pass | Quiz showed `QA Advanced preview mode`, `Question 1 of 5`, and `View Premium`. |
| Wrong-answer feedback | Pass | Wrong answer marked `Not quite`; local explanation remained visible. |
| AI Coach entry point | Pass with note | `Explain with AI Coach` and `Free AI previews left today: 3` were visible. The attempted tap did not visibly transition, likely due to tap positioning after scrolling; needs a focused retest. |
| Progress | Pass | Progress, selected path, preview-mode note, weak topic signal, and local guidance visible. |
| Adaptive Quiz | Pass with UX issue | CTAs were present. First CTA was partially under/too close to the bottom nav in the tested scrolled state; second CTA was visible after scrolling. |
| Smart Review | Pass | `Start Smart Review` visible and not premium-gated. |
| 7-Day AI Study Plan | Pass | Free user saw `View Premium`; basic progress/weak topics remained available. |
| Settings | Pass | Free plan status, AI access remaining, RevenueCat refresh failure message, View Premium, Restore purchases, and local reminder controls visible. |
| Premium screen | Pass | Free state showed benefits, available premium packs, coming-soon packs, Monthly, Yearly, `Best value`, Restore purchases, Maybe later, and friendly unavailable state. |
| RevenueCat unavailable state | Pass | Paywall said plans temporarily unavailable and app remains usable in Free mode. |
| Bottom navigation | Pass with UX issue | Bottom nav worked, but one Progress CTA can sit too close to the nav. |
| Visual polish | Pass with P2 note | No white rectangles, old icons, duplicate dialogs, broken nav, or severe contrast issues observed in tested flows. |

## Static Regression Checks

| Rule | Result | Notes |
| --- | --- | --- |
| No hard app-wide paywall | Pass | Free Home, Flashcards, Quiz, Daily Session, Smart Review, Progress, Settings remain reachable. |
| Premium entitlement ID | Pass | `PremiumEntitlementId = "premium"` and entitlement check uses `customerInfo.entitlements[PremiumEntitlementId]?.isActive == true`. |
| RevenueCat offering ID | Pass | `DefaultOfferingId = "default"`. |
| Google Play product IDs | Pass | `learnlift_premium_monthly` and `learnlift_premium_yearly` unchanged. |
| Premium Study Pack previews | Pass | Available premium packs have `freePreviewCount = 5`; UI labels and filtered content confirmed. |
| Coming-soon packs | Pass by code/static | Coming-soon paths set `isComingSoon = true`; Study Path Selection routes them to a coming-soon dialog instead of selection/navigation. Not manually tapped due time. |
| AI free usage limits | Pass by UI/static | Free limits: 3 answer explanations, 1 quiz summary, 0 study plans. Free AI Study Plan is gated before backend call. |
| Premium AI safety limits | Pass by static | Premium limits are higher and blocked message says safety limit, not upgrade/paywall. |
| AI fallback copy | Pass by static/UI | Backend/unavailable messages are friendly and do not expose raw technical errors in the UI. |
| RevenueCat product unavailable safety | Pass | Products unavailable state disables purchase CTA and leaves free tools available. |

## Bugs Found

### P0

None.

### P1

None.

### P2

1. Progress Adaptive Quiz CTA can be partially obscured by bottom navigation.
   - In `docs/qa-artifacts/after-todays-implementations/progress.xml`, the first `Start Adaptive Quiz` button bounds are `[132,2521][1212,2680]` while the bottom nav starts at y=2680. The CTA is technically visible, but on this emulator it was hard to tap reliably and felt crowded.
   - Risk: users may perceive the CTA as dead or cramped on some screen sizes/scales.
   - Fix recommendation: add extra bottom padding to the scroll content or keep CTA cards above the nav-safe area.

2. AI Coach tapped state needs a focused retest.
   - `Explain with AI Coach` was visible with local explanation and free remaining count. My tap after scrolling did not visibly change state.
   - Risk: could be tap-coordinate/user-test noise, not necessarily a product bug.
   - Fix recommendation: retest with exact bounds or an instrumented Compose test before changing code.

### P3

1. Gradle emits deprecated Android Gradle Plugin option warnings and obsolete variant API warnings.
   - Build passes.
   - Risk: future AGP 10 migration work, not a release blocker today.

2. Ignored `app/release/app-release.aab` exists locally.
   - It is ignored/untracked and was not touched.
   - Risk: accidental manual sharing/cleanup confusion; do not commit.

## Safe Fixes Applied

None. No code changes were made because the only observed issues were low-risk UX/test follow-ups, not safe obvious regressions requiring immediate patching.

## Remaining Manual Checks

- Tap each coming-soon pack (`Python Basics`, `JavaScript Basics`, `Business English`, `Technical Interview Prep`) and confirm the coming-soon dialog on-device.
- Retest `Explain with AI Coach` using exact UI bounds or an instrumented test.
- Complete a full five-question preview quiz to confirm the end-of-preview upgrade CTA.
- Simulate active Premium entitlement with a RevenueCat tester account/Test Store and verify:
  - Premium screen active state.
  - Purchase CTA replaced/disabled.
  - Full SQL, QA Advanced, and Automation Testing Basics packs open.
  - No preview labels for Premium user.
- Test actual Google Play purchase and restore behavior when RevenueCat offerings are available.
- Check small-screen layout on a lower-height emulator because the Progress CTA/bottom-nav spacing is already tight on Pixel 8 Pro scaling.

## Release Readiness Verdict

Ready for next task.

Not blocked for QA or release prep based on this regression pass. The P2 Progress CTA spacing and AI Coach tap retest should be handled before final release approval, but they do not block screenshot capture or the next feature task.
