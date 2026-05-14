# ai-coach Supabase Edge Function

This Edge Function is the backend proxy scaffold for future LearnLift AI Coach features.

Android must not call OpenAI directly and must not contain AI provider API keys. Android will eventually call this Supabase function, and this function calls OpenAI from the backend using environment variables.

## Endpoint

```text
POST /functions/v1/ai-coach
```

Body:

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

## Environment Variables

Required:

- `OPENAI_API_KEY`

Recommended:

- `OPENAI_MODEL`

Optional:

- `AI_PROXY_MAX_INPUT_CHARS`
- `AI_PROXY_ENABLE_DEBUG_LOGS`

If `OPENAI_MODEL` is missing, the function uses `gpt-4.1-mini` as a safe scaffold default. Configure the model explicitly before production deployment.

## Security Notes

- Do not commit secrets.
- Do not add API keys to Android.
- Do not log full prompts by default.
- The function logs only minimal metadata when `AI_PROXY_ENABLE_DEBUG_LOGS=true`.
- No database writes are performed.
- No user data is stored by this function.
- The in-memory rate limiter is a starter safeguard only; production should use a more durable limit keyed by account, anonymous install ID, or another policy-approved identifier.

## Response Errors

Provider failure:

```json
{
  "error": "AI_PROVIDER_ERROR",
  "message": "AI Coach is temporarily unavailable."
}
```

Invalid provider JSON:

```json
{
  "error": "AI_RESPONSE_PARSE_ERROR",
  "message": "AI Coach returned an invalid response."
}
```

Validation errors return HTTP 400 with a specific error code and message.
