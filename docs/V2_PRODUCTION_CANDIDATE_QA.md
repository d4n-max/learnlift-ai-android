# LearnLift AI v2 Production Candidate QA

Last updated: 2026-05-22

Use this checklist for the v2 production candidate before Closed Testing upload and again after installing from the Play testing track.

## Install And Startup

- [ ] Fresh install succeeds.
- [ ] App launches without crash.
- [ ] No blank screen appears.
- [ ] No blocking loading state appears.
- [ ] App name shows as LearnLift AI.
- [ ] No crash after force stop and restart.

## Onboarding And Persistence

- [ ] First-run onboarding appears.
- [ ] Onboarding can be completed.
- [ ] Daily onboarding choices save correctly.
- [ ] Selected path persists after restart.
- [ ] Settings restart onboarding flow works if available.

## Study Paths And Flashcards

- [ ] English path is selectable.
- [ ] Job Interview path is selectable.
- [ ] IT / QA path is selectable.
- [ ] Flashcards open for each path.
- [ ] Smart Review opens.
- [ ] Reveal answer works.
- [ ] Known / Needs Review works.
- [ ] Smart Review spaced repetition state updates.
- [ ] Long flashcard content wraps without clipping.

## Quiz And Adaptive Quiz

- [ ] Standard quiz opens.
- [ ] Answer selection works.
- [ ] Explanations appear.
- [ ] Quiz summary appears.
- [ ] Adaptive Quiz opens.
- [ ] Adaptive Quiz focuses on weaker topics after weak-topic data exists.
- [ ] Topic weakness tracking updates after missed answers.
- [ ] Smart Coach recommendations appear after quiz activity.

## AI Coach

- [ ] Wrong answer AI Coach button appears.
- [ ] Wrong answer AI Coach returns a real explanation when backend is available.
- [ ] Local explanation remains visible after AI success.
- [ ] Offline AI fallback works.
- [ ] Bad backend URL fallback works.
- [ ] AI usage limit reached message appears for Free limits.
- [ ] Blocked limit request does not crash and keeps local guidance visible.
- [ ] Quiz summary AI Study Review succeeds or fails gracefully.
- [ ] Progress 7-day AI Study Plan succeeds for Premium or fails gracefully.

## Daily Session And Progress

- [ ] Daily Session starts.
- [ ] Daily Session uses the intended short subset.
- [ ] Rapid repeated taps do not double-count.
- [ ] Completion summary appears.
- [ ] Progress updates after completion.
- [ ] Progress weak topics appear.
- [ ] Progress persists after restart.
- [ ] Reset Progress works.
- [ ] Reset Progress keeps the app usable.

## Settings And Reminders

- [ ] Settings opens.
- [ ] Plan status is clear.
- [ ] App version is correct for the candidate.
- [ ] Data/local storage notes are clear.
- [ ] Notification permission request appears on Android 13+ when needed.
- [ ] Local reminder can be enabled.
- [ ] Local reminder time can be changed.
- [ ] Local reminder can be disabled.
- [ ] Reminder survives restart/boot where supported.

## Premium And Billing

- [ ] Premium screen opens.
- [ ] Premium screen shows Free or Premium accurately.
- [ ] Premium screen separates available features from coming soon items.
- [ ] Purchase CTA is disabled while purchase is in progress.
- [ ] Already-Premium state disables purchase CTA and shows Premium active.
- [ ] RevenueCat restore purchases works or fails gracefully.
- [ ] RevenueCat Test Store valid purchase activates Premium.
- [ ] RevenueCat Test Store failed purchase shows a friendly error.
- [ ] Google Play monthly product maps to `learnlift_premium_monthly`.
- [ ] Google Play yearly product maps to `learnlift_premium_yearly`.
- [ ] Entitlement identifier is exactly `premium`.
- [ ] Free core study flows remain usable without purchase.

## Manifest And Release Hygiene

- [ ] Billing permission is present in merged manifest.
- [ ] Internet permission is present in merged manifest.
- [ ] POST_NOTIFICATIONS permission is present in merged manifest.
- [ ] No OpenAI API key is in Android.
- [ ] No Supabase service role key is in Android.
- [ ] No RevenueCat private key is in Android.
- [ ] No keystore or signed AAB is committed.
- [ ] No generated build artifacts are committed.
