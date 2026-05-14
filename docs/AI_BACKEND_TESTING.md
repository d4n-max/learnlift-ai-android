# AI Backend Testing

## Overview

These examples test the Supabase Edge Function backend proxy directly. Android is not integrated yet.

Replace:

- `YOUR_SUPABASE_PROJECT_REF`
- `YOUR_SUPABASE_ANON_KEY` if your Supabase function requires authorization

Function URL:

```text
https://YOUR_SUPABASE_PROJECT_REF.functions.supabase.co/ai-coach
```

For local testing:

```text
http://127.0.0.1:54321/functions/v1/ai-coach
```

## Headers

Use:

```text
Content-Type: application/json
Authorization: Bearer YOUR_SUPABASE_ANON_KEY
```

Depending on Supabase settings, local testing may not require the authorization header.

## Test `explain_answer`

```powershell
curl -X POST "https://YOUR_SUPABASE_PROJECT_REF.functions.supabase.co/ai-coach" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer YOUR_SUPABASE_ANON_KEY" `
  -d '{
    "action": "explain_answer",
    "payload": {
      "studyPathId": "english-vocabulary-speaking",
      "topic": "polite phrases",
      "difficulty": "easy",
      "question": "Which phrase is the most polite way to ask for help?",
      "selectedAnswer": "Help me now.",
      "correctAnswer": "Could you help me, please?",
      "staticExplanation": "Could you help me, please? is polite because it uses could and please."
    }
  }'
```

Expected shape:

```json
{
  "action": "explain_answer",
  "result": {
    "title": "...",
    "conciseExplanation": "...",
    "whyCorrectAnswerWorks": "...",
    "studyTip": "...",
    "recommendedTopic": "..."
  }
}
```

## Test `quiz_summary`

```powershell
curl -X POST "https://YOUR_SUPABASE_PROJECT_REF.functions.supabase.co/ai-coach" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer YOUR_SUPABASE_ANON_KEY" `
  -d '{
    "action": "quiz_summary",
    "payload": {
      "studyPathId": "job-interview-prep",
      "score": 6,
      "totalQuestions": 10,
      "incorrectTopics": ["STAR method", "salary expectations"],
      "weakTopics": ["STAR method", "salary expectations"]
    }
  }'
```

Expected shape:

```json
{
  "action": "quiz_summary",
  "result": {
    "summary": "...",
    "recommendedFocus": ["...", "..."],
    "nextSessionSuggestion": "...",
    "encouragement": "..."
  }
}
```

## Test `study_plan`

```powershell
curl -X POST "https://YOUR_SUPABASE_PROJECT_REF.functions.supabase.co/ai-coach" `
  -H "Content-Type: application/json" `
  -H "Authorization: Bearer YOUR_SUPABASE_ANON_KEY" `
  -d '{
    "action": "study_plan",
    "payload": {
      "studyPathId": "it-qa-interview-prep",
      "goal": "Prepare for a QA interview",
      "days": 7,
      "level": "beginner"
    }
  }'
```

Expected shape:

```json
{
  "action": "study_plan",
  "result": {
    "title": "...",
    "days": [
      {
        "day": 1,
        "focus": "...",
        "tasks": ["...", "..."]
      }
    ]
  }
}
```

The `study_plan` action caps `days` at 7.

## Error Tests

Non-POST request:

```powershell
curl -X GET "https://YOUR_SUPABASE_PROJECT_REF.functions.supabase.co/ai-coach"
```

Expected:

```json
{
  "error": "METHOD_NOT_ALLOWED",
  "message": "Only POST requests are supported."
}
```

Invalid action:

```powershell
curl -X POST "https://YOUR_SUPABASE_PROJECT_REF.functions.supabase.co/ai-coach" `
  -H "Content-Type: application/json" `
  -d '{ "action": "unknown", "payload": {} }'
```

Expected HTTP 400 with `INVALID_ACTION`.

Provider failure:

```json
{
  "error": "AI_PROVIDER_ERROR",
  "message": "AI Coach is temporarily unavailable."
}
```

Response parse failure:

```json
{
  "error": "AI_RESPONSE_PARSE_ERROR",
  "message": "AI Coach returned an invalid response."
}
```

## Android Client Failure Testing

Task 35 adds Android client calls to this Supabase proxy, but all calls are user-initiated and fallback-safe.

To test the Android fallback path:

1. Leave `SUPABASE_AI_COACH_URL` as the default placeholder.
2. Build and launch the app.
3. Answer a quiz question incorrectly.
4. Tap `Explain with AI Coach`.
5. Confirm the app does not crash and the static explanation remains visible.

To test backend quota or billing failure:

1. Configure `SUPABASE_AI_COACH_URL` with your deployed function.
2. Leave OpenAI billing/quota inactive or use a backend setup that returns `AI_PROVIDER_ERROR`.
3. Tap the AI buttons in Quiz, Quiz Summary, or Progress.
4. Confirm Android shows friendly fallback messaging such as:

```text
AI Coach is temporarily unavailable. Here's the local explanation instead.
```

Real AI success requires deployed Supabase function secrets and active OpenAI API billing/quota.
