# Content QA Report v3.4

Date: 2026-06-08

## Scope

Reviewed active local JSON study content in `app/src/main/assets/study_content`.

Active packs reviewed:

- English Vocabulary & Speaking Prep
- Job Interview Prep
- IT / QA Interview Prep
- SQL Interview Prep
- QA Advanced
- Automation Testing Basics

Coming-soon premium metadata reviewed, but no JSON content exists yet:

- Python Basics
- JavaScript Basics
- Business English
- Technical Interview Prep

## Summary Counts

| Pack | Status | Flashcards | Quiz questions | Topics | Flashcard difficulty | Quiz difficulty | Verdict |
| --- | --- | ---: | ---: | ---: | --- | --- | --- |
| English Vocabulary & Speaking Prep | Free | 84 | 63 | 12 | 40 easy / 32 medium / 12 hard | 32 easy / 22 medium / 9 hard | Ready |
| Job Interview Prep | Free | 80 | 60 | 17 | 29 easy / 39 medium / 12 hard | 26 easy / 24 medium / 10 hard | Ready |
| IT / QA Interview Prep | Free | 60 | 50 | 16 | 33 easy / 18 medium / 9 hard | 24 easy / 18 medium / 8 hard | Ready |
| SQL Interview Prep | Premium | 30 | 25 | 15 | 10 easy / 14 medium / 6 hard | 9 easy / 12 medium / 4 hard | Ready |
| QA Advanced | Premium | 30 | 25 | 12 | 7 easy / 18 medium / 5 hard | 5 easy / 15 medium / 5 hard | Ready |
| Automation Testing Basics | Premium | 30 | 25 | 12 | 8 easy / 17 medium / 5 hard | 7 easy / 13 medium / 5 hard | Ready |
| Python Basics | Premium coming soon | 0 | 0 | 0 | Not applicable | Not applicable | Needs expansion |
| JavaScript Basics | Premium coming soon | 0 | 0 | 0 | Not applicable | Not applicable | Needs expansion |
| Business English | Premium coming soon | 0 | 0 | 0 | Not applicable | Not applicable | Needs expansion |
| Technical Interview Prep | Premium coming soon | 0 | 0 | 0 | Not applicable | Not applicable | Needs expansion |

## Issues Found

- English Vocabulary & Speaking Prep had one duplicate flashcard question for `deadline`.
- All packs had no `hard` difficulty items before this pass, which made difficulty progression feel too flat.
- QA Advanced was entirely `medium`, despite containing some beginner definitions and several judgment-heavy advanced items.
- SQL Interview Prep covered fundamentals well but was light on explicit subquery, CTE, and constraint coverage.
- SQL Interview Prep and IT / QA Interview Prep shared one exact `SELECT` flashcard question across packs.
- Premium previews for SQL and QA Advanced were valid but could better show learning value with mixed difficulty.
- The validation script did not yet enforce several content QA rules requested for v3.4.

## Issues Fixed

- Replaced the duplicate English `deadline` flashcard with a `milestone` workplace vocabulary card.
- Reworded the IT / QA beginner SQL flashcard so it no longer duplicates the SQL pack and better matches QA beginner context.
- Rebalanced difficulty labels across every active pack while keeping the underlying content intact.
- Added explicit SQL coverage for subqueries, CTEs, and UNIQUE constraints.
- Strengthened SQL preview content by including subquery/CTE concepts in the first five flashcards.
- Rebalanced QA Advanced so definitions are easier and advanced judgment items are marked harder.
- Preserved all active pack counts; no premium content value was reduced.

## Topic Coverage

| Pack | Topic count | Top coverage areas | Missing or weaker areas |
| --- | ---: | --- | --- |
| English Vocabulary & Speaking Prep | 12 | Everyday vocabulary, workplace English, professional phrases, interview phrases, grammar, natural English | Could expand pronunciation and live speaking prompts later. |
| Job Interview Prep | 17 | STAR, behavioral questions, communication choices, motivation, salary, conflict, failure, interviewer questions | Could add more role-specific examples later. |
| IT / QA Interview Prep | 16 | Test cases, bug reports, API basics, Agile/Scrum, manual testing, regression, severity/priority | Could later add more mobile/web device matrix examples. |
| SQL Interview Prep | 15 | SELECT, WHERE, GROUP BY, HAVING, joins, NULL, aggregates, keys, indexes, subqueries, CTEs, constraints | Could later add window functions and date handling once the pack expands. |
| QA Advanced | 12 | Strategy, risk, coverage, exploratory testing, triage, release readiness, reporting, non-functional testing | Could later add test metrics misuse, stakeholder negotiation, and incident follow-up examples. |
| Automation Testing Basics | 12 | Pyramid, UI/API testing, selectors, flaky tests, data, assertions, POM, CI, maintainability | Could later add fixture design, mocking, and contract testing. |

## Preview Quality Notes

- SQL Interview Prep preview now shows core query basics plus subquery/CTE value, giving Free users a stronger sample of premium depth.
- QA Advanced preview now includes basic strategy/risk concepts and a harder regression planning item.
- Automation Testing Basics preview already covers pyramid, UI/API testing, selectors, and flaky tests; it remains representative.
- Premium previews remain limited to the first 5 flashcards and first 5 quiz questions.

## Validation Improvements

`scripts/validate-study-content.mjs` now checks:

- Valid JSON parsing.
- Required root, flashcard, quiz, and option fields.
- Empty strings in required fields.
- `pathId` matching the JSON file name.
- Per-pack and global duplicate IDs.
- Per-pack and global duplicate flashcard question text.
- Per-pack and global duplicate quiz question text.
- Allowed difficulty values: `easy`, `medium`, `hard`.
- Exactly 4 quiz options.
- `correctAnswerId` matching exactly one option.
- Duplicate option IDs.
- Missing or blank topics.
- Minimum content counts per active pack.
- Topic count summary.
- Difficulty distribution summary.
- Premium preview metadata expectations.

Latest validation result: Passed.

## Remaining Concerns

- The four coming-soon premium packs still need full content before they can be sold as available packs.
- Beginner packs now include some hard-labeled items, but they should remain practical rather than intimidating during manual QA.
- Long flashcard answers and quiz explanations should still be checked on small devices for wrapping and clipping.
- Technical content is accurate for general interview preparation, but future deeper packs should identify database- or framework-specific behavior when relevant.

## Manual QA Checklist

- All free packs open.
- All active premium packs show correctly.
- Premium preview content is strong.
- Full premium access works.
- Coming-soon packs stay disabled and do not open empty content.
- Flashcards render long content correctly.
- Quiz options are not clipped.
- Explanations wrap correctly.
- Adaptive Quiz works for new packs.
- Smart Review works for new packs.
- Weak Topics update correctly for new packs.
- AI Quiz Review can summarize new packs.
- AI Study Plan can use new packs.
- No crash when switching packs.
- No empty pack opens as complete.
- Reset progress works.
