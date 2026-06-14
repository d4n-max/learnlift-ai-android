# Supabase AI Release Checklist v3.6

Last updated: 2026-06-08

Use this checklist before releasing v3.6 with AI Coach, AI Quiz Review, or AI 7-Day Study Plan enabled.

## Deployment

- [ ] Supabase project is linked or accessible.
- [ ] `ai-coach` Edge Function is deployed.
- [ ] Function URL matches the Android `SUPABASE_AI_COACH_URL` configuration.
- [ ] Verify JWT is OFF for the current no-login app, or document the required anon-auth header.
- [ ] CORS remains acceptable for Android usage and any approved web usage.

## Supabase Secrets

- [ ] `OPENAI_API_KEY` is set in Supabase secrets.
- [ ] `OPENAI_MODEL` is set.
- [ ] `AI_PROXY_MAX_INPUT_CHARS` is set.
- [ ] `AI_PROXY_ENABLE_DEBUG_LOGS=false`.
- [ ] No OpenAI key is in Android.
- [ ] No Supabase service role key is in Android.
- [ ] No Supabase service role key is committed to this repository.

## Backend Action Tests

Run the PowerShell examples in `docs/AI_BACKEND_TESTING.md`.

- [ ] `explain_answer` works.
- [ ] `quiz_summary` works.
- [ ] `study_plan` works.
- [ ] Invalid action returns a clean error.
- [ ] Non-POST request returns a clean error.
- [ ] `AI_RESPONSE_PARSE_ERROR` does not happen repeatedly.
- [ ] Backend returns clean JSON.
- [ ] Backend logs do not print secrets.

## Android Smoke Tests

- [ ] Wrong-answer AI Coach explanation works when online.
- [ ] Local explanation remains visible.
- [ ] AI Quiz Review works after quiz completion.
- [ ] 7-Day Study Plan works for Premium user.
- [ ] Free user does not call Supabase for Premium-only study plan.
- [ ] AI usage limits block locally before backend call when exhausted.
- [ ] Offline fallback works.
- [ ] Backend unavailable fallback works.
- [ ] No technical backend errors are shown directly to users.

## Privacy / Safety

- [ ] AI payloads include only compact study context.
- [ ] No full study history is sent.
- [ ] No payment data is sent to AI backend.
- [ ] No account data is sent because no account exists.
- [ ] Privacy Policy and Data Safety copy describe optional AI context transfer.

## Final Supabase Verdict

Supabase AI release status: **Manual review required before Play upload**.

The Android path is documented and build-ready, but final release requires deployed-function smoke tests for `explain_answer`, `quiz_summary`, and `study_plan`.
