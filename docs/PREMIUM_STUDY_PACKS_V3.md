# Premium Study Packs v3

Last updated: 2026-06-08

## Overview

Premium Study Packs add deeper local study content while keeping the original LearnLift AI free paths useful. The app does not use an app-wide paywall.

Premium access is based only on the RevenueCat entitlement:

```text
premium
```

## Free Packs

- English Vocabulary & Speaking Prep
- Job Interview Prep
- IT / QA Interview Prep

These paths remain Free and open normally.

## Premium Packs Available Now

| Pack | Flashcards | Quiz questions | Preview |
| --- | ---: | ---: | ---: |
| SQL Interview Prep | 30 | 25 | First 5 cards and questions; includes query basics plus subquery/CTE preview |
| QA Advanced | 30 | 25 | First 5 cards and questions; includes strategy, risk, coverage, and regression planning |
| Automation Testing Basics | 30 | 25 | First 5 cards and questions; includes pyramid, UI/API checks, selectors, and flakiness |

## Coming Soon Premium Packs

- Python Basics
- JavaScript Basics
- Business English
- Technical Interview Prep

Coming soon packs are visible in Study Path Selection but disabled, so tapping them does not navigate to empty content.

## Preview Policy

Free users can see Premium packs and open a limited preview:

- First 5 flashcards
- First 5 quiz questions

When a Free user taps a Premium pack, the app shows:

- Preview pack
- View Premium
- Cancel

Premium users open the full pack normally.

## Content Behavior

Content is local JSON in `app/src/main/assets/study_content`.

Preview mode filters the loaded local content before Flashcards, Quiz, Adaptive Quiz, Daily Session, Smart Review, Progress, and Weak Topics receive it. Existing progress and topic-performance flows continue using the pack `pathId`.

## v3.4 Content QA Notes

- Active local packs were reviewed in `docs/CONTENT_QA_REPORT_V3_4.md`.
- Validation now checks schema, duplicate IDs, duplicate question text, option counts, correct answer IDs, difficulty values, topic presence, and summary counts.
- Active packs now include a clearer easy/medium/hard difficulty spread.
- Coming soon packs remain metadata-only and disabled until full content is created.

## v3.5 UX Notes

- Study Path Selection now shows richer Premium Study Pack cards with count summaries, Premium badges, Preview available badges, and Coming soon states.
- Free users tapping an available Premium pack see a pack summary dialog with Preview pack, View Premium, and Cancel.
- Flashcards preview mode now shows a clear preview-limit card at the final visible preview card.
- Coming soon packs show a simple dialog and do not open empty content.
- Premium active users bypass preview dialogs and receive full available pack content.

## Manual QA Checklist

- Free paths still open normally.
- Premium Study Packs section appears.
- Free user taps SQL pack and sees preview/paywall options.
- Free user can preview limited SQL content.
- Free user cannot access full premium pack.
- Free user reaches preview limit and sees View Premium / Back to Study Paths.
- View Premium opens Premium screen.
- Premium active user can access full SQL pack.
- Premium active user can access QA Advanced.
- Premium active user can access Automation Testing Basics.
- Coming soon packs do not crash.
- Flashcards work for premium pack.
- Quiz works for premium pack.
- Adaptive Quiz works for premium pack.
- Smart Review works for premium pack.
- Progress/Weak Topics works for premium pack.
- Reset Progress works.
- No white card artifacts.
- Build passes.

## Future Content Expansion Plan

- Expand each available Premium pack toward 80+ flashcards and 60+ quiz questions.
- Add beginner-friendly starter content for Python Basics and JavaScript Basics.
- Add workplace phrases, meeting language, and email scenarios to Business English.
- Add system design basics, debugging prompts, and behavioral technical scenarios to Technical Interview Prep.
- Consider per-path progress summaries after the current all-device progress model is upgraded.
