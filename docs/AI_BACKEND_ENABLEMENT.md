# AI Backend Enablement

## Purpose

LearnLift AI can now use the Supabase `ai-coach` Edge Function as the production-safe proxy for real AI Coach responses.

Android must never call OpenAI directly and must never contain an OpenAI API key. Android sends only small, user-initiated study requests to the Supabase backend proxy.

## Required Supabase Secrets

Set these in Supabase, not in Android code and not in committed files:

```powershell
supabase secrets set OPENAI_API_KEY=your_openai_api_key
supabase secrets set OPENAI_MODEL=gpt-4.1-mini
supabase secrets set AI_PROXY_MAX_INPUT_CHARS=6000
supabase secrets set AI_PROXY_ENABLE_DEBUG_LOGS=false
```

Required:

- `OPENAI_API_KEY`
- `OPENAI_MODEL`

Optional:

- `AI_PROXY_MAX_INPUT_CHARS`
- `AI_PROXY_ENABLE_DEBUG_LOGS`

Recommended model for v2 foundation:

```text
gpt-4.1-mini
```

The model is configurable through `OPENAI_MODEL`, so it can be changed later without updating Android.

## Known Previous Failure

The previous backend blocker was:

```text
429 insufficient_quota
```

OpenAI API billing/quota must be active for real AI responses. If quota is missing or exhausted, the backend returns a clean fallback-safe error instead of exposing raw provider details to Android.

## Deploy

From the repository root:

```powershell
supabase functions deploy ai-coach
```

The deployed endpoint should be:

```text
https://PROJECT_REF.supabase.co/functions/v1/ai-coach
```

Android should be built with:

```powershell
.\gradlew.bat assembleDebug -PSUPABASE_AI_COACH_URL=https://PROJECT_REF.supabase.co/functions/v1/ai-coach
```

`SUPABASE_AI_COACH_URL` is not a secret. Do not add OpenAI keys, Supabase service role keys, or private tokens to Android.

Redeploy the function after any parser, prompt, schema, or error-handling change under `supabase/functions/ai-coach`.

## Supported Actions

The function accepts:

```json
{
  "action": "explain_answer",
  "payload": {}
}
```

Supported actions:

- `explain_answer`
- `quiz_summary`
- `study_plan`

## Success Response Shape

Explain answer:

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

Quiz summary:

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

Study plan:

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

## Failure Response Shape

Known provider, quota, model, key, and parsing failures return clean JSON:

```json
{
  "error": "AI_PROVIDER_ERROR",
  "message": "AI Coach is temporarily unavailable."
}
```

Other possible error codes:

- `OPENAI_INSUFFICIENT_QUOTA`
- `OPENAI_INVALID_API_KEY`
- `OPENAI_MODEL_NOT_FOUND`
- `OPENAI_RATE_LIMIT_EXCEEDED`
- `AI_RESPONSE_PARSE_ERROR`
- `AI_PROXY_CONFIGURATION_ERROR`
- `RATE_LIMITED`
- `INVALID_ACTION`
- `INVALID_PAYLOAD`

Android treats these as fallback-safe failures and keeps local explanations or Smart Coach recommendations available.

## `AI_RESPONSE_PARSE_ERROR`

`AI_RESPONSE_PARSE_ERROR` means OpenAI returned text, but the backend could not turn it into the expected action result.

Common causes:

- The model returned markdown.
- The model wrapped JSON in ```json fences.
- The model added prose before or after JSON.
- The model omitted required fields.
- The model returned the wrong type for a required field.

The backend parser now attempts:

1. `JSON.parse` on raw output text.
2. `JSON.parse` after stripping markdown code fences, including a leading ```json fence when the closing fence is missing from the preview.
3. `JSON.parse` on the first balanced JSON object found inside the output.
4. Schema normalization and validation for the requested action.
5. Safe fallback defaults for incomplete but parseable objects.

Fallback defaults include:

- `explain_answer`: default title, concise explanation from `staticExplanation` when needed, generic study tip, and payload topic.
- `quiz_summary`: score-based summary, weak-topic focus list, next-session suggestion, and encouragement.
- `study_plan`: a safe basic plan with the requested number of days when day entries are malformed.

If parsing still fails, the backend returns:

```json
{
  "error": "AI_RESPONSE_PARSE_ERROR",
  "message": "AI Coach returned an invalid response."
}
```

Do not expose full raw AI output to users. When `AI_PROXY_ENABLE_DEBUG_LOGS=true`, the function may log only:

- action
- model
- parse path: `direct_json`, `stripped_code_fence`, `extracted_object`, `fallback_defaults`, or `parse_error`
- sanitized output preview capped at 300 characters

It must not log OpenAI keys, full prompts, full user payloads, Supabase secrets, or full raw AI output.

For reliability testing, run the same `explain_answer` request multiple times after redeploying because the original failure was intermittent.

## Backend Safety Checks

The Supabase function:

- Allows `POST` requests only.
- Validates JSON body shape.
- Validates action names.
- Validates payload fields for each action.
- Limits request size through `AI_PROXY_MAX_INPUT_CHARS`.
- Uses structured JSON response schemas.
- Parses AI output safely.
- Returns `AI_RESPONSE_PARSE_ERROR` if parsing or schema validation fails.
- Does not write to a database.
- Does not store user data.
- Uses `OPENAI_API_KEY` only from Supabase environment variables.
- Logs only minimal metadata when `AI_PROXY_ENABLE_DEBUG_LOGS=true`.

Debug logs must not include full prompts, full AI responses, API keys, or personal data.

## Manual Android Test Checklist

On a physical Android device:

- Configure `SUPABASE_AI_COACH_URL` with the deployed function URL.
- Answer a quiz question incorrectly.
- Tap `Explain with AI Coach`.
- Confirm a real AI explanation appears when backend quota is active.
- Temporarily use a bad backend URL and confirm the local explanation remains visible.
- Complete a quiz and tap `Generate AI Study Review`.
- Confirm success response or local Smart Coach fallback.
- Open Progress and tap `Generate 7-Day Study Plan`.
- Confirm success response or local Recommended Focus fallback.
- Turn off network and confirm no crash.
- Confirm no automatic AI calls happen on screen load.
- Confirm Android contains no OpenAI key.

## Production Notes

Before broader production rollout:

- Confirm OpenAI billing/quota is active.
- Confirm Supabase function deployment is current.
- Confirm privacy policy and Google Play Data Safety mention user-initiated AI study-context transfer.
- Add daily AI limits before scaling usage.
- Keep local Smart Coach and static explanations available.
