# AI Backend Testing

## Overview

These examples test the Supabase Edge Function backend proxy directly.

If the function returns `AI_RESPONSE_PARSE_ERROR`, the OpenAI request succeeded but the model output could not be converted into a usable JSON object. A common root cause is model output starting with a markdown fence such as ```json, or adding text around the JSON object. The backend now tries raw JSON, stripped code fences, and the first balanced JSON object found inside extra prose before returning this error.

Function URL:

```text
https://PROJECT_REF.supabase.co/functions/v1/ai-coach
```

Local Supabase URL:

```text
http://127.0.0.1:54321/functions/v1/ai-coach
```

If your Supabase function requires authorization, include:

```text
Authorization: Bearer YOUR_SUPABASE_ANON_KEY
```

Do not use an OpenAI API key from Android or in these client-side requests.

## Supabase Dashboard Deployment Note

If you deploy manually through the Supabase Dashboard function editor, the function must either:

- use a single self-contained `index.ts`, or
- include every imported local file in the function editor.

The current `supabase/functions/ai-coach/index.ts` is intentionally self-contained for Dashboard deployment. It does not import local files such as `./promptTemplates.ts`, `./responseSchemas.ts`, or `./types.ts`.

CLI deployment can still deploy the folder normally:

```powershell
supabase functions deploy ai-coach
```

## PowerShell Setup

```powershell
$FunctionUrl = "https://PROJECT_REF.supabase.co/functions/v1/ai-coach"
$Headers = @{
  "Content-Type" = "application/json"
  "Authorization" = "Bearer YOUR_SUPABASE_ANON_KEY"
}
```

For local testing without authorization:

```powershell
$FunctionUrl = "http://127.0.0.1:54321/functions/v1/ai-coach"
$Headers = @{
  "Content-Type" = "application/json"
}
```

## Test `explain_answer`

```powershell
$Body = @{
  action = "explain_answer"
  payload = @{
    studyPathId = "english-vocabulary-speaking"
    topic = "polite phrases"
    difficulty = "easy"
    question = "Which phrase is the most polite way to ask for help?"
    selectedAnswer = "Help me now."
    correctAnswer = "Could you help me, please?"
    staticExplanation = "Could you help me, please? is polite because it uses could and please."
  }
} | ConvertTo-Json -Depth 6

Invoke-RestMethod -Method Post -Uri $FunctionUrl -Headers $Headers -Body $Body
```

Expected success shape:

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
$Body = @{
  action = "quiz_summary"
  payload = @{
    studyPathId = "job-interview-prep"
    score = 6
    totalQuestions = 10
    incorrectTopics = @("STAR method", "salary expectations")
    weakTopics = @("STAR method", "salary expectations")
  }
} | ConvertTo-Json -Depth 6

Invoke-RestMethod -Method Post -Uri $FunctionUrl -Headers $Headers -Body $Body
```

Expected success shape:

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
$Body = @{
  action = "study_plan"
  payload = @{
    studyPathId = "it-qa-interview-prep"
    goal = "Prepare for a QA interview"
    days = 7
    level = "beginner"
  }
} | ConvertTo-Json -Depth 8

Invoke-RestMethod -Method Post -Uri $FunctionUrl -Headers $Headers -Body $Body
```

Expected success shape:

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

## Redeploy After Backend Changes

After changing files under `supabase/functions/ai-coach`, redeploy:

```powershell
supabase functions deploy ai-coach
```

Then rerun the three action tests in this file.

For AI parsing reliability, test `explain_answer` several times, not just once. The bug this doc covers was inconsistent: some OpenAI responses returned clean JSON, while others were wrapped in markdown-style fences.

## Expected Error Behavior

Non-POST request:

```powershell
Invoke-RestMethod -Method Get -Uri $FunctionUrl -Headers $Headers
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
$Body = @{ action = "unknown"; payload = @{} } | ConvertTo-Json -Depth 4
Invoke-RestMethod -Method Post -Uri $FunctionUrl -Headers $Headers -Body $Body
```

Expected HTTP 400 with:

```json
{
  "error": "INVALID_ACTION",
  "message": "Action must be explain_answer, quiz_summary, or study_plan."
}
```

Provider, quota, invalid key, missing model, or temporary backend failure:

```json
{
  "error": "AI_PROVIDER_ERROR",
  "message": "AI Coach is temporarily unavailable."
}
```

Specific provider-related error codes may include:

- `OPENAI_INSUFFICIENT_QUOTA`
- `OPENAI_INVALID_API_KEY`
- `OPENAI_MODEL_NOT_FOUND`
- `OPENAI_RATE_LIMIT_EXCEEDED`
- `AI_PROVIDER_ERROR`

Invalid AI JSON response:

```json
{
  "error": "AI_RESPONSE_PARSE_ERROR",
  "message": "AI Coach returned an invalid response."
}
```

Android should display friendly fallback messaging for all of these and keep local explanations or Smart Coach recommendations available.

In debug mode only, the backend may log a sanitized AI output preview for parse failures:

- maximum 300 characters
- no full prompt
- no full raw response
- obvious API-key-shaped values redacted

Debug logs may also include:

- action
- model
- parse path: `direct_json`, `stripped_code_fence`, `extracted_object`, `fallback_defaults`, or `parse_error`

## Android Client Failure Testing

All Android AI calls are user-initiated.

### Placeholder URL

1. Leave `SUPABASE_AI_COACH_URL` as the default placeholder.
2. Build and launch the app.
3. Answer a quiz question incorrectly.
4. Tap `Explain with AI Coach`.
5. Confirm the app does not crash and the static explanation remains visible.

### Bad Backend URL

1. Build with a bad URL:

```powershell
.\gradlew.bat assembleDebug -PSUPABASE_AI_COACH_URL=https://example.com/functions/v1/ai-coach
```

2. Tap each AI button.
3. Confirm each request fails gracefully.

### OpenAI Quota Or Billing Failure

1. Configure `SUPABASE_AI_COACH_URL` with your deployed function.
2. Use a backend setup that returns `OPENAI_INSUFFICIENT_QUOTA` or `AI_PROVIDER_ERROR`.
3. Tap the AI buttons in Quiz, Quiz Summary, or Progress.
4. Confirm Android shows friendly fallback messaging such as:

```text
AI Coach is temporarily unavailable. Here's the local explanation instead.
```

### Success Testing

1. Confirm Supabase secrets are configured:
   - `OPENAI_API_KEY`
   - `OPENAI_MODEL`
   - `AI_PROXY_MAX_INPUT_CHARS`
   - `AI_PROXY_ENABLE_DEBUG_LOGS`
2. Confirm OpenAI billing/quota is active.
3. Deploy the latest `ai-coach` function.
4. Run the three PowerShell action tests above.
5. Build Android with the deployed function URL.
6. Confirm success responses on a physical device.
