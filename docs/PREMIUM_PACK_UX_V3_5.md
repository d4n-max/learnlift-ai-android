# Premium Pack UX v3.5

Last updated: 2026-06-08

## Current UX Audit

- Study Path Selection has separate `Free Learning Paths` and `Premium Study Packs` sections.
- Free paths open normally and remain above Premium discovery.
- Premium cards show Premium, Preview available, Coming soon, category, difficulty, daily time, and count summaries when local content exists.
- Free users tapping an available Premium pack see an unlock dialog with Preview pack, View Premium, and Cancel.
- Free preview mode loads only the first 5 flashcards and first 5 quiz questions.
- Flashcards and Quiz show a subtle Preview mode message for Free users.
- Flashcards now show a preview-limit card at the final preview card with View Premium and Back to Study Paths.
- Coming-soon packs show a simple coming-soon dialog and do not open empty content.
- Premium users open available Premium packs without preview labels or limits.
- Premium screen shows Premium Study Packs as a core benefit and marks incomplete packs as coming soon.
- RevenueCat unavailable state defaults safely to Free and keeps core study tools usable.

## Free User Flow

1. Open Study Path Selection.
2. Review Free Learning Paths and Premium Study Packs.
3. Tap SQL Interview Prep, QA Advanced, or Automation Testing Basics.
4. See `Unlock Premium Study Packs`.
5. Choose `Preview pack`, `View Premium`, or `Cancel`.
6. Preview mode opens the selected pack with the first 5 flashcards and quiz questions.
7. At the end of Flashcards preview, the app shows:

```text
You've reached the free preview limit for this pack. Unlock Premium to continue.
```

8. The user can open Premium or return to Study Paths.

## Premium User Flow

1. Premium entitlement `premium` is active.
2. Open Study Path Selection.
3. Tap an available Premium Study Pack.
4. The full pack opens without lock dialog or preview mode labels.
5. Flashcards, Quiz, Smart Review, Adaptive Quiz, Weak Topics, AI Study Review, and AI Study Plan use the selected premium pack context.

## Preview Policy

- Available Premium packs: first 5 flashcards and first 5 quiz questions for Free users.
- Preview mode must remain useful and representative.
- Known / Needs Review still works for preview cards.
- Quiz summary and local explanations still work for preview questions.
- AI Study Review keeps Premium/usage-limit gating.
- Preview uses `take(freePreviewCount)`, so packs with fewer than 5 items would not crash.

## Coming Soon Policy

- Coming-soon packs remain visible in Study Path Selection.
- Tapping shows:

```text
This Premium Study Pack is coming soon.
```

- Coming-soon packs must not open empty Flashcards or Quiz screens.
- Coming-soon packs must not be described as fully available in Play Store copy.

## Premium Screen Copy

Header:

```text
Unlock LearnLift AI Premium
```

Subtitle:

```text
Get more AI help, Premium Study Packs, and smarter practice support.
```

Available now:

- More AI Coach explanations
- AI Quiz Review
- 7-day AI Study Plans
- Premium Study Packs
- Higher AI daily limits
- Smart learning support

Premium Study Packs include:

- SQL Interview Prep
- QA Advanced
- Automation Testing Basics
- Python Basics (coming soon)
- JavaScript Basics (coming soon)
- Business English (coming soon)
- Technical Interview Prep (coming soon)

Trust copy:

- Cancel anytime through Google Play.
- Restore purchases if already subscribed.
- Premium active status appears when entitlement is active.

## RevenueCat Dependency

- Premium entitlement identifier: `premium`.
- Offering identifier: `default`.
- Google Play product IDs:
  - `learnlift_premium_monthly`
  - `learnlift_premium_yearly`
- App checks only:

```kotlin
customerInfo.entitlements["premium"]?.isActive == true
```

- RevenueCat unavailable defaults to Free safely.
- Product IDs, package IDs, and display names are not used as entitlements.

## Manual QA Checklist

Free user:

- Free paths open normally.
- Premium Study Packs are visible.
- Free user taps SQL pack.
- Lock/preview dialog appears.
- Preview pack works.
- Preview mode label is visible in Flashcards and Quiz.
- Preview flashcards are limited.
- Preview quiz is limited.
- Preview limit shows unlock CTA in Flashcards.
- View Premium opens Premium screen.
- Coming-soon pack does not crash.

Premium user:

- Restore purchases activates Premium if entitlement is active.
- Premium screen shows Premium active.
- SQL full pack opens.
- QA Advanced full pack opens.
- Automation Testing Basics full pack opens.
- Python/JavaScript/Business English/Technical Interview stay coming soon until complete.
- Flashcards work.
- Quiz works.
- Smart Review works.
- Adaptive Quiz works.
- Progress weak topics work.
- AI Study Review works.
- AI Study Plan works.

Regression:

- Onboarding works.
- Home works.
- Free quizzes work.
- AI Coach explain answer works.
- Reminders work.
- Settings works.
- No white rectangles.
- No old icons.
- No crash on app restart.
