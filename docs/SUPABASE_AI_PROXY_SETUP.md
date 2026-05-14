# Supabase AI Proxy Setup

## Overview

LearnLift AI uses a Supabase Edge Function named `ai-coach` as the future secure AI backend proxy.

The Android app must not contain AI provider keys and must not call OpenAI directly. Android integration is intentionally not enabled yet.

## Prerequisites

Install the Supabase CLI:

```powershell
npm install -g supabase
```

Log in:

```powershell
supabase login
```

Link the project from the repository root:

```powershell
supabase link --project-ref YOUR_SUPABASE_PROJECT_REF
```

## Configure Secrets

Set secrets in Supabase. Do not commit real secrets to the repository.

```powershell
supabase secrets set OPENAI_API_KEY=your_openai_api_key_here
supabase secrets set OPENAI_MODEL=gpt-4.1-mini
```

Optional settings:

```powershell
supabase secrets set AI_PROXY_MAX_INPUT_CHARS=6000
supabase secrets set AI_PROXY_ENABLE_DEBUG_LOGS=false
```

`OPENAI_MODEL` should be configured before deployment. The function has a scaffold default of `gpt-4.1-mini` if the variable is missing, but production should be explicit.

## Run Locally

From the repository root:

```powershell
supabase functions serve ai-coach --env-file .env.local
```

Use `.env.local` only on your machine. Do not commit it.

Example local environment file:

```text
OPENAI_API_KEY=your_openai_api_key_here
OPENAI_MODEL=gpt-4.1-mini
AI_PROXY_MAX_INPUT_CHARS=6000
AI_PROXY_ENABLE_DEBUG_LOGS=false
```

## Deploy

```powershell
supabase functions deploy ai-coach
```

After deployment, the function URL is:

```text
https://YOUR_SUPABASE_PROJECT_REF.functions.supabase.co/ai-coach
```

Supabase projects may require an anon key or authorization header depending on project settings. Keep public Supabase anon configuration separate from AI provider secrets.

## Production CORS

The function includes permissive CORS headers for early testing.

Before production, restrict CORS to approved origins if the endpoint is used from web clients. Android clients are not protected by browser CORS, so backend rate limiting and request validation still matter.

## Data Safety And Privacy

Before enabling real AI in production:

- Update the Privacy Policy.
- Update Google Play Data Safety.
- Explain that selected quiz/study context may be sent to the backend for AI help.
- Do not send personal data.
- Do not store raw user content in v1.
- Keep OpenAI API keys only in Supabase secrets.

## Android Integration Later

Android integration is intentionally not part of this task.

Future Android work should add:

- `AiCoachService`
- `AiCoachRepository`
- request/response models
- loading/success/error UI states
- fallback to static explanations and rule-based Smart Coach tips

The Android app must never include `OPENAI_API_KEY`.
