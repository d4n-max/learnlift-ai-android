# Adaptive Quiz Mode

Last updated: 2026-05-20

## Purpose

Adaptive Quiz gives LearnLift AI a focused practice mode that prioritizes local weak topics while preserving the existing normal Quiz mode.

Adaptive Quiz is local-only. It does not add backend selection, auth, cloud sync, analytics, ads, or new AI calls.

## User-Facing Behavior

Users can start:

- `Start Quiz` for the normal quiz flow.
- `Adaptive Quiz` for focused practice.

Normal Quiz remains unchanged. It still uses the selected study path quiz content, normal answer behavior, local explanations, AI Coach buttons, progress saving, and topic performance updates.

Adaptive Quiz reuses the existing Quiz screen and shows:

- Header: `Adaptive Quiz`
- Intro card: `Practice your weakest topics`
- Focused topics when weak topic data exists
- Fallback copy when no weak topic data exists:

```text
Complete a quiz to unlock more personalized adaptive practice.
```

## Selection Logic

Default Adaptive Quiz length:

```text
10 questions
```

If fewer questions are available, the app uses the available valid questions and does not crash.

When weak topic data exists:

- About 70% of questions come from weak topics.
- About 20% come from mixed review topics.
- Remaining slots prefer easy/beginner confidence builders when available.
- Duplicate questions are avoided within one quiz session.
- If a bucket has too few questions, the selector fills from remaining available questions.

When no weak topic data exists:

- Adaptive Quiz falls back to a balanced topic mix.
- Questions are spread across topics where possible.
- The UI explains that personalization improves after more quiz answers.

## Weak Topic Ranking

Adaptive Quiz uses `TopicPerformance` from local weakness tracking.

Ranking favors:

- Confirmed weak topics.
- Higher `weaknessScore`.
- More wrong answers.
- Lower confidence topics.

Topics with only one wrong attempt can be included, but they should not dominate confirmed weak topics.

## Free vs Premium

Free users can use Adaptive Quiz. It is not hard-locked.

Free copy:

```text
Premium will unlock deeper adaptive practice.
```

Premium copy:

```text
Premium adaptive practice active
```

No daily Adaptive Quiz attempt limit is added in this task. Future Premium gating may add deeper adaptive practice, longer adaptive sessions, or more advanced weakness analysis.

## Progress And Smart Coach

Progress shows a `Start Adaptive Quiz` CTA in `Topics to Review`.

Smart Coach recommends Adaptive Quiz when weak topics exist:

```text
Try an Adaptive Quiz focused on your weak topics.
```

Adaptive Quiz answers update:

- Normal quiz completion stats.
- Topic performance stats.
- Smart Coach recommendations.
- Progress persistence.

## Reset Behavior

Reset Progress clears topic performance stats. After reset, Adaptive Quiz falls back to balanced practice until new topic data exists.

## Edge Cases

Handled:

- No selected path: existing quiz empty state.
- No quiz questions: existing quiz empty state.
- No weak topic data: balanced adaptive mix.
- Very few questions: uses available questions.
- All weak topics have no matching questions: fills from mixed/random available questions.
- Duplicate topics: topic names are de-duplicated for focus display.
- Reset progress: topic performance is cleared.
- Switching path: quiz state and adaptive selection reset.
- App restart: topic stats persist locally.

## Manual QA Checklist

- Normal quiz still works.
- Adaptive Quiz appears on Home.
- Adaptive Quiz appears from Progress `Topics to Review`.
- Adaptive Quiz works with no weak topics.
- Create a weak topic by answering wrong.
- Adaptive Quiz prioritizes that topic.
- No duplicate questions appear in one adaptive session.
- Adaptive answers update topic stats.
- Progress weak topics update after adaptive quiz.
- Reset Progress clears adaptive recommendations.
- Free user can still use normal quiz.
- Premium active state does not break adaptive quiz.
- Physical device layout is readable.
