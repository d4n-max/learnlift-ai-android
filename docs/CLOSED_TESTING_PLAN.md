# LearnLift AI Closed Testing Plan

## Overview

- App name: LearnLift AI
- Package name: `com.learnliftai.app`
- Current release stage: Internal testing completed / preparing for Closed Testing
- Goal: recruit at least 12 testers for 14 continuous days
- Recommended buffer: recruit 15-20 testers to reduce risk

Google Play requirements can vary by account type and may change over time. For newer personal developer accounts, Google documentation currently describes a closed testing requirement with at least 12 opted-in testers for the last 14 days continuously before applying for production access. Always confirm the current requirement in Play Console before starting the test.

## Closed Testing Objective

The goal is not only to hit the minimum tester count. Closed Testing should collect meaningful feedback, confirm installation from Google Play, test core flows on real Android devices, and improve the app before applying for production access.

Closed Testing should answer:

- Can invited testers opt in successfully?
- Can testers install and launch the app from Google Play?
- Do the core study flows work outside the developer's emulator?
- Are the store listing, privacy, and Data Safety disclosures accurate?
- Are there confusing screens, content issues, or device-specific problems?

## Tester Sources

Planned and recommended tester sources:

- Testers Community app
- Friends
- Colleagues
- Android users from personal network
- Small developer communities if needed

Use real testers only. Do not use fake, bot, or low-quality testing sources.

## Tester Requirements

Each tester should:

- Use an Android device.
- Use a Google account added to the closed testing list or Google Group.
- Open the opt-in link.
- Join the test.
- Install the app from Google Play.
- Keep access for the 14-day testing period.
- Test at least the key flows once.
- Provide short feedback.

Testers do not need to use the app every day unless Play Console or current Google guidance explicitly requires it. The practical goal is that testers remain opted in for the full period and provide meaningful feedback.

## Recommended Tester Count

- Minimum required: 12 testers
- Recommended target: 15-20 testers

Reason: some testers may not install, may not opt in correctly, may use the wrong Google account, may uninstall early, or may not provide feedback.

## Closed Testing Timeline

### Day 0

- Prepare closed testing track.
- Upload AAB.
- Add testers.
- Send opt-in instructions.

### Days 1-2

- Confirm tester opt-ins.
- Confirm installs.
- Ask testers to launch app and select a study path.

### Days 3-5

- Ask testers to test Flashcards and Quiz.
- Ask testers to report any confusing questions, explanations, or layout issues.

### Days 6-8

- Ask testers to test Daily Study Session and Progress.
- Ask testers to restart the app and confirm progress persists.

### Days 9-11

- Ask testers to test Settings, Reset Progress, and Light/Dark mode.
- Ask for screenshots or short videos if anything looks wrong.

### Days 12-14

- Collect final feedback.
- Fix critical issues if needed.
- Prepare production access request notes.

## Core Flows Testers Should Test

- Install from Google Play.
- Launch app.
- Select study path.
- Use Flashcards.
- Use Quiz.
- Complete Daily Study Session.
- Check Progress.
- Restart app and confirm progress persists.
- Reset Progress.
- Test Light/Dark mode if possible.

## Feedback Tracking

Track feedback in a spreadsheet, form, issue tracker, or simple document.

Recommended fields:

- Tester name or alias
- Google account email used for testing
- Device model
- Android version
- Install success
- Tested flows
- Bugs found
- UX feedback
- Screenshots/videos
- Date feedback received

## Risk Areas

- Testers use the wrong Google account.
- Testers forget to opt in.
- Testers install but never open app.
- Not enough real feedback is collected.
- Play Console production access is rejected despite 14 days.
- Privacy policy is missing or incomplete.
- Data Safety answers do not match actual app behavior.
- `versionCode` is reused on a new upload.
- A critical bug is found late in the 14-day period.

## Mitigation Plan

- Recruit more than 12 testers.
- Target 15-20 testers before starting the 14-day period.
- Send clear opt-in and install instructions.
- Follow up every few days.
- Ask testers to confirm install and first launch.
- Collect screenshots when possible.
- Keep `docs/BUG_BACKLOG.md` updated.
- Avoid misleading store claims.
- Keep Data Safety accurate.
- Increment `versionCode` for every new uploaded build.

## Production Access Preparation

Prepare notes for the production access request:

- Summary of what was tested
- Number of testers
- Testing period dates
- Feedback collected
- Bugs found
- Bugs fixed
- Known limitations
- App purpose
- Target audience
- Why the app is ready for production

Keep the tone factual. Do not claim production readiness if critical bugs remain.

## Success Criteria

Closed testing is successful if:

- At least 12 testers opted in for 14 continuous days.
- Enough testers installed and launched the app.
- Key flows were tested.
- No critical crash remains.
- Feedback was collected and documented.
- QA report and bug backlog are updated.

## References

- Google Play Console Help: Set up an open, closed, or internal test.
- Google Play Console Help: App testing requirements for new personal developer accounts.
