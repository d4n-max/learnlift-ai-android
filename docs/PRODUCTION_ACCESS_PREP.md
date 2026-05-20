# Production Access Prep

Last updated: 2026-05-20

This document prepares LearnLift AI v2 for Google Play production access review after closed testing. Do not claim production readiness until remaining blockers are resolved.

## Closed Testing Requirements Checklist

- [ ] Run closed testing with the required number of testers for the required duration based on the current Play Console policy.
- [ ] Confirm at least 12 testers are invited or active if required by the account's policy.
- [ ] Keep testing open for the required testing period.
- [ ] Collect tester feedback from multiple devices.
- [ ] Verify installs from the Play closed-testing track, not only local debug installs.
- [ ] Verify subscription products through Google Play closed testing.
- [ ] Fix or document critical bugs before requesting production access.

## Tester Feedback Collection Template

Ask testers:

- Device model and Android version:
- Did onboarding appear and complete correctly?
- Which study path did you test?
- Did Flashcards and Smart Review work?
- Did Quiz and Adaptive Quiz work?
- Did AI Coach show an explanation or fallback safely?
- Did Progress update after study activity?
- Did local reminders request permission correctly?
- Did Premium screen and restore purchases behave clearly?
- Did anything crash, freeze, clip, or feel misleading?
- Overall feedback:

## v1 To v2 Changes

LearnLift AI v2 adds:

- Real AI Coach through Supabase backend proxy.
- AI answer explanations.
- AI usage limits.
- RevenueCat Premium entitlement and purchase readiness.
- Premium gating rules for higher AI limits.
- Topic weakness tracking.
- Adaptive Quiz Mode.
- Smart Review spaced repetition flashcards.
- Onboarding v2.
- Local daily study reminders.
- Expanded v2 store and Data Safety documentation.

Core Free functionality remains available:

- Current study paths.
- Flashcards.
- Quizzes.
- Daily Session.
- Progress.
- Smart Coach.
- Local explanations and fallback behavior.

## Production Access Answers Draft

### What does the app do?

LearnLift AI is an Android study coach app that helps users practice English vocabulary, job interview preparation, and IT / QA interview basics through flashcards, quizzes, daily sessions, progress tracking, Smart Review, Adaptive Quiz, and optional AI Coach explanations.

### Who is it for?

The app is for job seekers, English learners, beginner IT / QA learners, students, and self-learners who want short, structured study sessions and local progress tracking without needing an account.

### What did testers test?

Testers should test onboarding, study path selection, Home, Flashcards, Smart Review, Quiz, Adaptive Quiz, Daily Session, Progress, Settings, local reminders, AI Coach success/fallback behavior, Premium screen, purchase cancellation, restore purchases, and Free mode.

### What feedback did you receive?

Use actual tester quotes or summarized feedback after closed testing. Do not invent feedback.

Template:

- Testers found the daily study flow clear.
- Testers requested clearer AI fallback copy.
- Testers confirmed Free study modes remained usable without purchase.
- Testers reported any bugs listed in `docs/BUG_BACKLOG.md`.

### What bugs did you fix?

Use actual bug fixes after QA. Current tracked examples may include:

- AI response parsing hardening.
- Android AI response wrapper parsing.
- Duplicate tap protections in study flows.
- Local fallback behavior for AI/backend failures.

### Why is the app ready for production?

Draft answer only after blockers are closed:

LearnLift AI is ready for production because the core study flows are stable, Free users can use the app without a purchase, AI failures fall back safely, subscription behavior has been verified in closed testing, Data Safety and Privacy Policy have been updated, and testers confirmed the app launches, saves local progress, and handles study sessions reliably.

## Remaining Blockers

- [ ] Complete current Play Console closed-testing duration.
- [ ] Collect real tester feedback.
- [ ] Verify Google Play subscription products and RevenueCat entitlement `premium`.
- [ ] Confirm OpenAI quota and Supabase function deployment for production.
- [ ] Publish/update Privacy Policy.
- [ ] Complete Play Console Data Safety.
- [ ] Capture final screenshots and feature graphic.
- [ ] Generate signed AAB with correct upload key.
- [ ] Confirm no secrets or generated build artifacts are committed.
