# AI Android Client Setup

## Overview

The Android app now has a small AI Coach client that can call the Supabase `ai-coach` Edge Function after explicit user action.

Android still must never contain an OpenAI API key and must never call OpenAI directly. It only calls the Supabase backend proxy.

## Backend URL Configuration

The backend URL is configured as a non-secret BuildConfig value:

```kotlin
BuildConfig.SUPABASE_AI_COACH_URL
```

Default placeholder:

```text
https://YOUR_PROJECT_REF.supabase.co/functions/v1/ai-coach
```

For local builds, set this Gradle property without committing secrets:

```properties
SUPABASE_AI_COACH_URL=https://YOUR_PROJECT_REF.supabase.co/functions/v1/ai-coach
```

This URL is not a secret. Do not put OpenAI keys, Supabase service role keys, or private tokens in Android config.

## Android Client Structure

Files:

- `app/src/main/java/com/learnliftai/app/data/ai/AiCoachModels.kt`
- `app/src/main/java/com/learnliftai/app/data/ai/AiCoachService.kt`
- `app/src/main/java/com/learnliftai/app/data/ai/AiCoachRepository.kt`

The client uses `HttpURLConnection` and Android JSON classes to avoid adding new HTTP dependencies.

## Supported Backend Actions

The Android client posts to:

```text
POST /functions/v1/ai-coach
```

Request shape:

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

## UI Entry Points

### Quiz Wrong Answer

After an incorrect quiz answer, the user can tap:

```text
Explain with AI Coach
```

The app sends only the current question, selected answer, correct answer, topic, difficulty, study path ID, and static explanation.

### Quiz Summary

On the quiz summary screen, the user can tap:

```text
Generate AI Study Review
```

The app sends only score, total question count, study path ID, and weak/missed topics.

### Progress

On Progress, the user can tap:

```text
Generate 7-Day Study Plan
```

The app sends the selected study path ID, a short goal based on the selected path, `days = 7`, and `level = beginner`.

## Fallback Behavior

All AI calls are optional and user-initiated.

If the backend is unavailable, quota is exhausted, JSON parsing fails, or the device has no network:

- Quiz wrong answer keeps the static local explanation visible.
- Quiz summary keeps the rule-based Recommended Focus visible.
- Progress keeps the local Recommended Focus visible.
- The app shows a friendly message instead of technical backend details.

Common user-facing fallback:

```text
AI Coach is temporarily unavailable. Here's the local explanation instead.
```

## Current Known Blocker

Real AI responses require:

- Supabase function deployed.
- `OPENAI_API_KEY` configured in Supabase secrets.
- `OPENAI_MODEL` configured in Supabase secrets.
- OpenAI API billing/quota active.

If OpenAI billing or quota is not active, the backend may return `AI_PROVIDER_ERROR`, `insufficient_quota`, or a temporary unavailable message. Android handles this gracefully.

## Testing

### Placeholder URL

With the default placeholder URL, the app should still launch and local study flows should work. AI buttons should show fallback messaging and should not crash.

### Configured Backend URL

Set the Gradle property:

```powershell
.\gradlew.bat assembleDebug -PSUPABASE_AI_COACH_URL=https://YOUR_PROJECT_REF.supabase.co/functions/v1/ai-coach
```

Then test:

- Incorrect quiz answer -> `Explain with AI Coach`
- Quiz summary -> `Generate AI Study Review`
- Progress -> `Generate 7-Day Study Plan`

### Backend Failure

Test with a deployed function but missing/invalid OpenAI quota. The Android app should show friendly fallback text and preserve local study behavior.

## Not Included

This task does not add:

- OpenAI API keys in Android.
- Direct OpenAI calls from Android.
- Supabase service role keys in Android.
- Retrofit, Ktor, OkHttp, or Supabase Android SDK.
- Billing.
- Premium gating.
- Login.
- Cloud sync.
- Analytics.
- Sensitive personal data storage.
