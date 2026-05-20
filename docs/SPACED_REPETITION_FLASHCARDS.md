# Spaced Repetition Flashcards

Last updated: 2026-05-20

## Overview

LearnLift AI includes a simple local spaced repetition layer for flashcards. Normal Flashcards mode still shows all cards, and Smart Review focuses on cards that are due or marked Needs Review.

This feature is local only. It does not use a backend database, login, cloud sync, analytics, ads, AI calls, or payment gating.

## Review State Model

Each reviewed card can have a local `FlashcardReviewState`:

- `cardId`
- `pathId`
- `topic`
- `status`: `New`, `Learning`, `Review`, or `Known`
- `knownCount`
- `needsReviewCount`
- `lastReviewedAt`
- `nextReviewAt`
- `intervalDays`

Timestamps are stored as epoch millis from the local device clock.

## Storage

Review state is persisted locally with DataStore in `learnlift_flashcard_review`.

State is keyed by study path ID and flashcard ID. No flashcard review state leaves the device.

## Scheduling Rules

When a card is marked `Needs Review`:

- status becomes `Review`
- `needsReviewCount` increases by 1
- `lastReviewedAt` is set to now
- `nextReviewAt` is set to now
- the card becomes high priority in Smart Review

When a card is marked `Known`:

- `knownCount` increases by 1
- status becomes `Known`
- `lastReviewedAt` is set to now
- the next review interval grows:
  - first Known: 1 day
  - second Known: 3 days
  - third Known: 7 days
  - later Known: 14 days

This is intentionally simpler than a full SM-2 algorithm.

## Smart Review

Smart Review shows due and overdue cards first, including cards currently marked Needs Review.

If no cards are due, the app shows a friendly empty state and offers `Continue all flashcards`.

## Normal Flashcards

Normal Flashcards mode still shows all flashcards for the selected study path. It is not replaced or hidden by Smart Review.

## Progress And Home

Progress shows a compact Flashcard Review section:

- cards due today
- cards marked Needs Review
- known cards
- new cards remaining
- Start Smart Review CTA

Home shows a compact Smart Review card with the due count and a Review now action.

## Topic Weakness Integration

Flashcard actions still update topic signals:

- Needs Review contributes a local review signal for that topic.
- Known contributes a local confidence signal for that topic.

Smart Review does not replace quiz-based topic weakness tracking. It adds card-level review scheduling alongside topic-level insights.

## Reset Behavior

Reset Progress clears:

- overall progress totals
- topic performance stats
- flashcard review state
- due dates
- known and Needs Review counts

The selected study path remains selected.

## Free Vs Premium

Smart Review is available to Free users and closed testers. It is not hard-locked behind Premium.

Premium may later add:

- deeper spaced repetition insights
- advanced review planning
- cross-path review suggestions

These are future improvements and should be shown as coming soon until implemented.

## Manual QA Checklist

- Regular Flashcards still work.
- Mark a card Needs Review.
- The card appears in Smart Review.
- Mark a card Known.
- The due date moves forward.
- Known card count increases.
- No due cards empty state works.
- Progress shows due/review/known counts.
- Reset Progress clears review state.
- App restart preserves review state.
- Smart Review works for all three study paths.
- Rapid duplicate taps do not double-count the same session rating.
- Free user can access Smart Review.
- Premium active does not break Smart Review.
