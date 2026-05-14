# AI Android Integration Contract

## Purpose

This document defines the future Android contract for real AI Coach features. It is documentation only. The current Android app must remain local-only until a backend proxy is implemented and explicitly enabled.

The Android app must never call an AI provider directly. It should call only the LearnLift AI backend proxy.

## Future Android Components

### `AiCoachService`

Network boundary responsible for calling backend proxy endpoints.

Future responsibilities:

- Send HTTPS requests to backend proxy.
- Decode structured JSON responses.
- Apply client-side timeout handling.
- Return typed success or error results.

Not part of the current task:

- Retrofit.
- Ktor.
- OkHttp.
- API base URL configuration.
- Network permission.

### `AiCoachRepository`

Feature-facing data layer for AI Coach requests.

Future responsibilities:

- Map screen state into request models.
- Call `AiCoachService`.
- Provide fallback responses when backend fails.
- Keep AI feature logic away from Compose UI.

### `AiCoachUiState`

Future UI state model:

```kotlin
sealed interface AiCoachUiState {
    data object Idle : AiCoachUiState
    data object Loading : AiCoachUiState
    data class Success<T>(val data: T) : AiCoachUiState
    data class Error(val message: String, val fallbackText: String? = null) : AiCoachUiState
}
```

Expected states:

- Idle: no AI request started.
- Loading: user requested AI help and request is in progress.
- Success: backend returned structured response.
- Error: backend failed, timed out, or returned invalid data.

Error state must keep the study flow usable.

## Request And Response Models

### Explain Answer

Future request:

```kotlin
data class ExplainAnswerRequest(
    val studyPathId: String,
    val topic: String,
    val difficulty: String,
    val question: String,
    val selectedAnswer: String,
    val correctAnswer: String,
    val staticExplanation: String
)
```

Future response:

```kotlin
data class ExplainAnswerResponse(
    val title: String,
    val conciseExplanation: String,
    val whyCorrectAnswerWorks: String,
    val studyTip: String,
    val recommendedTopic: String
)
```

### Quiz Summary

Future request:

```kotlin
data class QuizSummaryRequest(
    val studyPathId: String,
    val score: Int,
    val totalQuestions: Int,
    val incorrectTopics: List<String>,
    val weakTopics: List<String>
)
```

Future response:

```kotlin
data class QuizSummaryResponse(
    val summary: String,
    val recommendedFocus: List<String>,
    val nextSessionSuggestion: String,
    val encouragement: String
)
```

### Study Plan

Future request:

```kotlin
data class StudyPlanRequest(
    val studyPathId: String,
    val goal: String,
    val days: Int,
    val level: String
)
```

Future response:

```kotlin
data class StudyPlanResponse(
    val title: String,
    val days: List<StudyPlanDay>
)

data class StudyPlanDay(
    val day: Int,
    val focus: String,
    val tasks: List<String>
)
```

## Backend Endpoints

Android should call only these backend proxy endpoints:

- `POST /ai/explain-answer`
- `POST /ai/quiz-summary`
- `POST /ai/study-plan`

The app should not call AI provider URLs.

## Future UI Entry Points

### Wrong Quiz Answer

Entry point: button after an incorrect quiz answer.

Suggested label:

- `Ask Smart Coach`
- `Ask AI Coach`

Behavior:

- User taps button.
- UI enters loading state.
- Backend returns explanation.
- If backend fails, show static explanation already available from local JSON.

### Quiz Summary

Entry point: quiz summary screen.

Suggested label:

- `AI Study Review`

Behavior:

- User taps button.
- Backend returns weak-topic summary and next focus recommendation.
- If backend fails, keep rule-based Smart Coach recommendation visible.

### Study Plan

Entry point: Progress or Premium screen.

Suggested label:

- `Generate 7-Day Study Plan`

Behavior:

- User confirms selected path and goal.
- Backend returns 7-day plan.
- If backend fails, show a simple local fallback or prompt user to try again later.

## Fallback Requirements

Android must keep current MVP flows usable if AI fails.

- Wrong answer explanation falls back to static JSON explanation.
- Quiz summary falls back to rule-based Smart Coach recommendations.
- Study plan falls back to a simple local message or no generated plan.
- Flashcards, quiz, daily session, progress, and settings must not depend on AI availability.

## Free And Premium Relationship

Possible future gating:

- Free users may get 2-3 AI previews per day.
- Premium users may get higher daily limits.
- AI explanations and study plans may become Premium features.
- Rule-based Smart Coach tips should remain available as local guidance.

Billing, entitlements, RevenueCat, or Google Play Billing must be implemented in a separate task before real Premium gating is enforced.

## Privacy Requirements For Android

- Send only the minimal context required for each AI feature.
- Avoid sending personal data.
- Do not send names, email addresses, resumes, private notes, health data, financial data, or legal details.
- No account should be required for v1 AI unless a future task adds authentication.
- Show clear copy before enabling real AI so users understand study context is sent to a backend.

## Not Implemented Yet

This contract does not add:

- Network permission.
- Retrofit, Ktor, OkHttp, or any network library.
- Backend implementation.
- AI provider SDK.
- API keys.
- Billing.
- Authentication.
- Cloud sync.
- Analytics.
- Changes to existing app behavior.
