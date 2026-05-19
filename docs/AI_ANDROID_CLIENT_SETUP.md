# AI Android Client Setup

## Overview

The Android app now has a small AI Coach client that can call the Supabase `ai-coach` Edge Function after explicit user action.

Android still must never contain an OpenAI API key and must never call OpenAI directly. It only calls the Supabase backend proxy.

## Backend URL Configuration

The backend URL is configured as a non-secret BuildConfig value:

```kotlin
BuildConfig.SUPABASE_AI_COACH_URL
```

Default debug endpoint:

```text
https://hfeyfsqfggtajowlaeap.supabase.co/functions/v1/ai-coach
```

For local builds, set this Gradle property without committing secrets:

```properties
SUPABASE_AI_COACH_URL=https://YOUR_PROJECT_REF.supabase.co/functions/v1/ai-coach
```

This URL is not a secret. Do not put OpenAI keys, Supabase service role keys, or private tokens in Android config.

If Android receives HTTP `401` or `403`, confirm the Supabase Edge Function has Verify JWT turned off for the current unauthenticated v1/v2 testing flow. Do not add a Supabase service role key to Android.

Debug Logcat tag:

```text
LearnLiftAiCoach
```

Debug builds log the configured endpoint host/path, action, HTTP status code, sanitized response preview, backend error code, and parsing success/failure. Logs must not include OpenAI keys, Supabase service-role keys, or full secrets.

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

Successful response wrapper:

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

Android parses the wrapper first, then parses the nested `result` object for the selected action.

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

When local topic weakness tracking is available, the weak topic payload may include top locally tracked weak topic names in addition to topics missed in the current quiz. The app does not send full study history or detailed per-topic performance records.

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

## Backend Enablement Status

Real AI responses require:

- Supabase function deployed.
- `OPENAI_API_KEY` configured in Supabase secrets.
- `OPENAI_MODEL` configured in Supabase secrets.
- OpenAI API billing/quota active.

OpenAI API billing/quota has been enabled manually for v2 testing. The previous known backend failure was `429 insufficient_quota`.

If quota is exhausted again, the key is invalid, the model is unavailable, the backend returns invalid JSON, or the device has no network, Android handles the failure gracefully.

Known backend error codes that Android treats as fallback-safe:

- `AI_PROVIDER_ERROR`
- `AI_RESPONSE_PARSE_ERROR`
- `AI_PROXY_CONFIGURATION_ERROR`
- `OPENAI_INSUFFICIENT_QUOTA`
- `OPENAI_INVALID_API_KEY`
- `OPENAI_MODEL_NOT_FOUND`
- `OPENAI_RATE_LIMIT_EXCEEDED`

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

These calls must happen only after the user taps the corresponding AI button. There should be no AI request on screen load.

### Backend Failure

Test with a deployed function but missing/invalid OpenAI quota, invalid model, bad backend URL, or no network. The Android app should show friendly fallback text and preserve local study behavior.

## Physical Device Checklist

- Build with the deployed Supabase function URL.
- Install on a physical Android device.
- Answer a quiz question incorrectly.
- Tap `Explain with AI Coach`.
- Confirm either real AI explanation or the local static explanation fallback.
- Complete a quiz.
- Tap `Generate AI Study Review`.
- Confirm either AI review or local Smart Coach fallback.
- Open Progress.
- Tap `Generate 7-Day Study Plan`.
- Confirm either AI plan or local Recommended Focus fallback.
- Turn off network and repeat one AI action.
- Confirm the app does not crash.
- Confirm static explanations and rule-based Smart Coach remain available.
- Confirm no OpenAI key exists in Android code/resources.

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
