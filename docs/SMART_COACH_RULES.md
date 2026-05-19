# Smart Coach Rules

## Overview

Smart Coach recommendations are local, rule-based study suggestions for the MVP. They do not use live AI, backend services, payments, analytics, authentication, Firebase, cloud sync, or network calls.

The feature is designed as practical guidance that helps learners choose a next study step from progress already saved on the device.

## Data Used

The local rules may use:

- Quiz score percentage.
- Quiz weak or missed topics from the current quiz summary.
- Daily session score.
- Daily session cards reviewed.
- Daily session Needs Review count.
- Daily session topics marked for review.
- Persisted local progress totals from DataStore.
- Last quiz score and current study streak from local progress.
- Local per-topic performance stored on the device.

No extra personal data is stored for this feature. No data leaves the device.

## Recommendation Types

Recommendations use a small local model:

- `title`
- `message`
- `focusTopics`
- `actionLabel`
- `type`

The `type` is one of:

- `Encouragement`
- `Review`
- `Warning`
- `NextStep`

## Quiz Summary Rules

- Score `80%` or higher: encourage the learner to keep the streak going or try fresh practice.
- Score `50%` to `79%`: recommend reviewing missed topics, then taking another short quiz.
- Score below `50%`: recommend reviewing flashcards and basics before another quiz.
- If missed topics exist, show up to 3 focus topics.

## Daily Session Rules

- If the session has missed quiz topics or Needs Review flashcard topics, recommend reviewing those topics next.
- If quiz score is below `50%`, recommend flashcards before another quiz.
- If quiz score is `80%` or higher, encourage keeping the streak going.
- Otherwise, suggest a short quiz or another flashcard pass.

## Progress Rules

- If no local progress exists, recommend starting with a daily session.
- If many reviewed flashcards are marked Needs Review, recommend reviewing flashcards before quizzing.
- If most reviewed flashcards are Known, encourage a quiz or daily session.
- If the last quiz score is low, recommend flashcards first.
- If the last quiz score is high, encourage a quiz or harder practice.
- If a streak exists, the recommendation may mention keeping it going.
- If weak topic performance exists, recommend the top local weak topics before generic score-based guidance.
- If strong topic performance exists and no weak topic is urgent, encourage reinforcing a strong topic with a quiz.

## Future AI Coach

The future AI Coach may replace or enhance these deterministic rules after a backend proxy and privacy model are explicitly added. The Android app should not call an AI provider directly with secrets.

Until then, Smart Coach remains local guidance only.

## Topic Weakness Rules

- Topics with wrong answers or Needs Review flashcard signals can appear as Recommended Focus topics.
- Topics with at least 2 attempts and accuracy below 70% are treated as weak topics.
- Topics with only 1 wrong signal may be shown as needing more practice data.
- Smart Coach shows up to 3 focus topics.
- If no topic data exists, Smart Coach falls back to the existing progress and quiz score rules.
