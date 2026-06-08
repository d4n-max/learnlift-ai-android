# AI Quiz Review v3.1

Last updated: 2026-06-08

## Purpose

AI Quiz Review gives users an optional, user-initiated review after completing a quiz. The normal local quiz summary and Smart Coach recommendation remain visible even when AI is unavailable.

## Payload Sent

Android sends only compact quiz context:

- `studyPathId`
- `studyPathTitle`
- quiz score percentage
- number correct
- number wrong
- top wrong topics
- selected weak topic names
- wrong question sample, max 3
- difficulty summary

Android does not send full study history, all local content, personal data, payment data, OpenAI keys, or Supabase service role keys.

## Response Shape

```json
{
  "summary": "...",
  "recommendedFocus": ["...", "..."],
  "nextSessionSuggestion": "...",
  "encouragement": "..."
}
```

## Free And Premium Behavior

Free users can use the existing `quiz_summary` daily preview allowance when available.

If the local Free allowance is used, Android does not call Supabase and shows:

```text
AI Quiz Review is part of Premium. Upgrade to get deeper feedback and personalized next steps.
```

Premium users can generate AI Quiz Review within the Premium `quiz_summary` safety limit.

## Failure Behavior

Loading state:

```text
Generating your AI study review...
```

Failure state:

```text
AI Study Review is temporarily unavailable. Here's your local summary instead.
```

Retry is manual only. There are no automatic retry loops.

## Manual QA Checklist

- Complete quiz as Free user.
- Verify local summary remains visible.
- Attempt AI Quiz Review.
- Verify Free preview or Premium teaser behavior.
- Premium active user can generate AI Review.
- Offline fallback works.
- Retry works.
- AI usage limit blocks calls locally.
- No crash if backend fails.
- Existing local explanations still appear for quiz answers.
