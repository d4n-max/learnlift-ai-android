# Topic Weakness Tracking

Last updated: 2026-05-19

## Purpose

Topic weakness tracking gives LearnLift AI a local signal for topics that need review. It supports better Progress insights, stronger rule-based Smart Coach recommendations, and future adaptive quizzes or spaced repetition.

This feature is local-only. It does not add a backend database, login, cloud sync, analytics, or remote tracking.

## What Is Tracked

For each study path and topic, the app stores:

- `pathId`
- `topic`
- `correctAnswers`
- `wrongAnswers`
- `totalAttempts`
- `lastPracticedAt`
- `lastWrongAt`
- simple difficulty counters:
  - `easyAttempts`
  - `mediumAttempts`
  - `hardAttempts`

Derived values:

- `accuracyPercent`
- `weaknessScore`
- `isWeakTopic`
- `needsReview`
- status label:
  - `Needs review`
  - `Improving`
  - `Strong`
  - `Not enough data`

## Where It Is Stored

Topic performance is stored locally with DataStore in:

```text
learnlift_topic_performance
```

Stats are encoded as a compact JSON string. They are keyed by study path and topic, such as:

```text
job-interview-prep -> STAR method
it-qa-interview-prep -> regression testing
english-vocabulary-speaking -> polite phrases
```

## How It Updates

Quiz answers:

- Correct answer increments `correctAnswers` and `totalAttempts`.
- Wrong answer increments `wrongAnswers`, `totalAttempts`, and updates `lastWrongAt`.
- `lastPracticedAt` updates for every first answer.
- Existing quiz guards prevent duplicate counts for repeated taps on the same question.

Flashcards:

- Marking `Known` increments the positive signal for that topic.
- Marking `Needs review` increments the review signal for that topic.
- Existing flashcard session rating guards avoid repeatedly counting the same unchanged card rating.

Daily session:

- Daily session quiz answers update topic stats.
- Daily session flashcard Known / Needs Review actions update topic stats.
- Existing session locks prevent duplicate tap counting.

## Weakness Scoring

The current simple score is:

```text
wrongAnswers * 2 + (100 - accuracyPercent) / 20 + recentWrongBonus
```

Rules:

- Wrong answers matter most.
- Low accuracy increases the score.
- A recent wrong answer adds a small bonus.
- Topics with at least 2 attempts are ranked more confidently.
- Topics with only 1 wrong attempt can appear as `Not enough data` rather than a confirmed weak topic.

## Progress UI

Progress shows a compact `Topics to Review` section:

- Top 3 to 5 local topic signals.
- Topic label.
- Status label.
- Accuracy or “needs more practice data.”
- A small Premium insight note for Premium users.

Free users still see basic weak topic tracking. It is not hidden behind Premium.

## Smart Coach Integration

Rule-based Smart Coach uses topic performance when available:

- Weak topics become Recommended Focus topics.
- Strong topics can trigger reinforcement suggestions.
- If no topic data exists, the app keeps the existing default recommendation flow.

## AI Coach Integration

AI quiz summary requests can use the current quiz missed topics plus locally tracked weak topic names.

The app does not send full study history. It sends only a short list of topic names needed for the AI Study Review.

## Reset Behavior

Reset Progress clears:

- Local progress stats.
- Streak data.
- Topic performance stats.

The selected study path remains selected.

## Privacy Notes

- Topic performance stays on the device.
- No account is required.
- No cloud sync is used.
- No backend database is used.
- AI requests may include only selected weak topic names when the user taps an AI review action.
- Full local study history is not sent to AI.

## Future Adaptive Quiz Integration

Adaptive Quiz now uses topic performance to prioritize weak topics while keeping normal Quiz mode available.

Future tasks can further use topic performance to:

- Build spaced repetition.
- Recommend flashcard review sets.
- Power Premium advanced insights.
- Generate safer, smaller AI prompts from topic summaries instead of full history.

## Manual QA Checklist

- Complete a quiz with wrong answers on one topic.
- Progress shows that topic under `Topics to Review`.
- Complete quiz or flashcard work with correct/Known signals and verify the topic improves.
- Reset Progress clears topic stats.
- Restart app preserves topic stats.
- Smart Coach uses weak topics when available.
- AI Quiz Review sends top weak topics if available.
- Repeated taps on one quiz question do not duplicate-count.
- Flashcard repeated same-rating taps do not duplicate-count.
- Daily session answer locks prevent duplicate topic counts.
- Works for all three study paths.
