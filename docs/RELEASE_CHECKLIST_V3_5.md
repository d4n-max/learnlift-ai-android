# Release Checklist v3.5

Last updated: 2026-06-08

Final verdict: Needs QA fixes before release.

Reason: local content validation, debug build, and emulator install pass, but Google Play closed-testing purchase flow, signed AAB generation, final screenshots, and physical-device billing/AI smoke tests still need completion.

## Repository

- [x] Confirm repository path is `C:\Projects\learnlift-ai-android`.
- [ ] Confirm `git status` contains only intentional source and documentation changes.
- [ ] Confirm no generated build artifacts are committed.
- [ ] Confirm no secrets are committed.
- [x] Keep package name unchanged.
- [x] Keep versionCode/versionName unchanged for this task.

## Build Checks

- [x] Run `node scripts\validate-study-content.mjs`.
- [x] Run `.\gradlew.bat assembleDebug`.
- [x] Run `.\gradlew.bat installDebug` on available emulator.
- [ ] Run `git diff --check` before final commit.
- [ ] Generate signed release AAB only when ready for Play upload.

## Content Validation

- [x] Free packs validate.
- [x] Available Premium packs validate.
- [x] Coming-soon packs remain metadata-only and disabled.
- [x] No duplicate question text found by validator.
- [x] Difficulty and topic summaries are documented in `docs/CONTENT_QA_REPORT_V3_4.md`.

## Paywall Checks

- [x] Premium entitlement remains `premium`.
- [x] Offering identifier remains `default`.
- [x] Monthly product ID remains `learnlift_premium_monthly`.
- [x] Yearly product ID remains `learnlift_premium_yearly`.
- [x] RevenueCat unavailable defaults to Free safely.
- [x] Premium screen disables purchase CTA when packages are unavailable.
- [ ] Test real Google Play purchase sheet from Play-installed build.
- [ ] Test successful monthly subscription activates `premium`.
- [ ] Test successful yearly subscription activates `premium`.
- [ ] Test restore purchases from Play-installed build.

## Premium Pack Checks

- [ ] Free user sees Premium Study Packs.
- [ ] Free user taps SQL and sees preview/paywall dialog.
- [ ] Preview pack opens first 5 flashcards.
- [ ] Preview quiz opens first 5 quiz questions.
- [ ] Flashcard preview limit shows View Premium and Back to Study Paths.
- [ ] View Premium opens Premium screen.
- [ ] Premium active user opens SQL full pack.
- [ ] Premium active user opens QA Advanced full pack.
- [ ] Premium active user opens Automation Testing Basics full pack.
- [ ] Coming-soon pack shows coming-soon dialog and no empty content.
- [ ] Smart Review works for Premium pack.
- [ ] Adaptive Quiz works for Premium pack.
- [ ] Weak Topics update for Premium pack.
- [ ] AI Study Review works for Premium pack.
- [ ] AI Study Plan references selected Premium pack.

## Regression Checks

- [ ] Onboarding works.
- [ ] Home works.
- [ ] Free Flashcards work.
- [ ] Free Quiz works.
- [ ] Daily Session works.
- [ ] Progress works.
- [ ] Settings works.
- [ ] AI Coach explain answer works or falls back safely.
- [ ] Local reminders work.
- [ ] App restart preserves selected path and Premium state.
- [ ] Reset progress works.
- [ ] No white rectangle artifacts.
- [ ] No old icon references.

## Screenshot Needs

- [ ] Home with selected path.
- [ ] Study Path Selection with Free and Premium sections.
- [ ] Premium pack preview dialog.
- [ ] Premium screen with Monthly/Yearly cards.
- [ ] Flashcards preview mode.
- [ ] Quiz or AI Study Review.
- [ ] Progress with Weak Topics.
- [ ] Settings Premium state.

## Signed AAB Steps

1. Open Android Studio.
2. Open `C:\Projects\learnlift-ai-android`.
3. Sync Gradle.
4. Select Build -> Generate Signed Bundle / APK.
5. Select Android App Bundle.
6. Use the existing upload key stored outside the repository.
7. Select release build variant.
8. Generate `app-release.aab`.
9. Upload to Google Play Closed Testing first.
10. Do not commit the AAB.

## Play Store Update Steps

- [ ] Use release notes from `docs/PLAY_CONSOLE_RELEASE_NOTES.md`.
- [ ] Confirm v3 listing copy avoids guaranteed outcomes.
- [ ] Confirm Data Safety still reflects no ads, no login, no cloud sync.
- [ ] Confirm RevenueCat/Google Play purchase processing is disclosed.
- [ ] Upload screenshots after v3.5 manual QA.
- [ ] Test billing from Play-installed build before production access.
*** Delete File: docs/PLAY_CONSOLE_RELEASE_NOTES.md
*** Add File: docs/PLAY_CONSOLE_RELEASE_NOTES.md
# Play Console Release Notes

Last updated: 2026-06-08

## LearnLift AI v3.5 Update

Includes:

- Premium Study Packs polish
- Free preview flow for premium packs
- Improved Premium screen
- Better Premium active state
- AI Study Review and Study Plan readiness
- Content QA improvements
- UI polish and stability improvements

Known limitations:

- No account login
- No cloud sync
- Progress is stored locally on device
- AI features require internet
- Premium purchases depend on Google Play and RevenueCat configuration

## Short Version

LearnLift AI v3.5 polishes Premium Study Packs, adds a clearer free preview flow, improves the Premium screen and active state, and includes content QA plus UI stability improvements.

Known limitations: no login, no cloud sync, local-only progress, internet required for AI, and Premium depends on Google Play/RevenueCat configuration.

Tester notes:

- Please test Free paths, Premium Study Pack previews, Premium active full-pack access, Flashcards, Quiz, Smart Review, Adaptive Quiz, Progress, Settings, AI Study Review, AI Study Plan, purchase, cancellation, and restore purchases.
- Please confirm Free study flows remain useful without purchase.
- Please report crashes, blank screens, clipped text, confusing billing states, unexpected paywalls, old icons, white rectangles, or AI fallback issues.
