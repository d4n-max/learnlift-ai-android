# Onboarding v2

Last updated: 2026-05-20

## Purpose

Onboarding v2 helps first-run users choose a learning goal, recommended study path, and daily study time before entering the main LearnLift AI app.

The flow is local-only. It does not add account login, cloud sync, analytics, ads, backend tables, AI calls, or Premium purchase prompts.

## Screens

The onboarding flow uses four short screens:

1. Welcome
   - App name and tagline.
   - Short value copy.
   - `Get started` CTA.
   - `Skip for now` option.

2. Choose your goal
   - Improve English for work.
   - Prepare for job interviews.
   - Prepare for IT / QA interviews.
   - Build a daily learning habit.

3. Choose daily time
   - 5 minutes.
   - 10 minutes.
   - 15 minutes.
   - 20 minutes.
   - Default selection is 10 minutes.

4. Recommended path
   - Shows the recommended path.
   - Allows changing the path before completion.
   - `Start learning` completes onboarding.

## Persisted Fields

Onboarding uses local DataStore in `learnlift_onboarding`.

Persisted fields:

- `hasCompletedOnboarding`: Boolean.
- `onboardingGoal`: String.
- `recommendedStudyPathId`: String.
- `dailyStudyMinutes`: Int.
- `onboardingCompletedAt`: Long.

## Goal-To-Path Mapping

- Improve English for work -> `english-vocabulary-speaking`.
- Prepare for job interviews -> `job-interview-prep`.
- Prepare for IT / QA interviews -> `it-qa-interview-prep`.
- Build a daily learning habit -> existing selected path if available, otherwise `job-interview-prep`.

## After Onboarding

After completion:

- onboarding state is saved locally.
- selected study path is set to the recommended path.
- the app navigates to Home.
- Home shows the selected path and recommended daily goal.
- Daily Session, Flashcards, Quiz, Progress, Smart Review, and Adaptive Quiz continue using the selected path.

## Skip Behavior

`Skip for now` marks onboarding complete, sets the default path to `job-interview-prep`, and uses a 10-minute daily goal.

Users can change the study path later from Home or Settings.

## Reset Onboarding

Settings includes `Restart onboarding`.

This clears onboarding preferences only and immediately returns the user to the onboarding flow. It does not reset progress, topic stats, AI usage, Premium state, or flashcard review state.

## Premium Behavior

Onboarding does not show a purchase screen and does not require Premium.

Future Premium messaging can be introduced after closed-testing QA, but onboarding should stay focused on learning setup.

## Manual QA Checklist

- Fresh install shows onboarding.
- `Get started` advances to goal selection.
- Each goal maps to the correct path.
- Daily time selection persists.
- `Start learning` navigates to Home.
- Selected path is set.
- Home shows the daily goal.
- App restart does not show onboarding again.
- Settings `Restart onboarding` shows onboarding again.
- `Skip for now` uses `job-interview-prep`.
- Home, Study Path, Quiz, Flashcards, Daily Session, Progress, Smart Review, Adaptive Quiz, Settings, Premium, and AI fallback still work.
- Light and dark mode are readable.
- No network is required.

## Future Improvements

- Let users edit daily study minutes from Settings without restarting onboarding.
- Add a small progress reminder based on the selected daily goal.
- Add optional goal-specific Smart Coach copy after more QA.
