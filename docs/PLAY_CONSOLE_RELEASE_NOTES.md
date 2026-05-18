# Play Console Release Notes

## Next Closed Testing Build

Prepared: 2026-05-18

Closed testing update for LearnLift AI.

Includes:

- Expanded study content for English, Job Interview, and IT / QA preparation
- Smart Coach recommendations
- AI Coach backend integration with safe fallback behavior
- Premium upgrade screen
- RevenueCat billing and subscription entitlement readiness
- Improved local progress and study flow

Known limitations:

- Real AI responses require OpenAI API billing/quota to be enabled
- Premium purchases require final Google Play and RevenueCat product configuration
- Progress is stored locally on the device
- No account login
- No cloud sync

QA note:

- Current debug build passes.
- Physical-device install and Google Play subscription purchase flow still need final closed-testing verification.

## Tester Notes

- Please test Home, Study Path Selection, Flashcards, Quiz, Daily Session, Progress, Settings, and Premium.
- Please confirm that free study flows remain usable without a purchase.
- Please test restore purchases after any successful subscription test purchase.
- Please report any crash, blank screen, clipped text, unexpected paywall, or confusing billing state.
