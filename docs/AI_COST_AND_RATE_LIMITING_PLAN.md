# AI Cost And Rate Limiting Plan

## Purpose

This plan describes how LearnLift AI can add real AI Coach features later without losing cost control, privacy discipline, or MVP reliability.

The current app remains local-only. No AI calls, backend calls, billing, analytics, or network permissions are implemented in the current task.

## Cost Control Principles

- Use static explanations and rule-based Smart Coach tips first.
- Trigger AI only on explicit user action.
- Keep prompts short.
- Keep responses short.
- Use structured JSON outputs.
- Limit request body size.
- Limit response token count.
- Cache repeated explanations where possible.
- Rate limit daily usage.
- Track endpoint usage later before expanding AI features.

## Suggested V1 Limits

### Free Users

Possible future limits:

- 2-3 AI previews per day.
- Wrong-answer explanations only at first.
- Rule-based Smart Coach remains available without AI.

### Premium Users

Possible future limits:

- Higher daily explanation limits.
- Quiz summary AI review.
- 7-day study plan generation.
- Higher but still capped usage to prevent runaway cost.

Billing and entitlement enforcement must be implemented later in a separate task before Premium limits are active.

## Endpoint-Level Limits

### `/ai/explain-answer`

Recommended limits:

- Lowest cost endpoint.
- Available first.
- Maximum one explanation per incorrect answer tap.
- Cache by study path ID + question + selected answer + correct answer.
- Short response only.

### `/ai/quiz-summary`

Recommended limits:

- Medium cost endpoint.
- Trigger only after quiz completion and user action.
- Limit weak topics to 3-5 items.
- Short response only.

### `/ai/study-plan`

Recommended limits:

- Highest cost endpoint among v1 features.
- Trigger only on explicit user action.
- Limit to 7 days for v1.
- Limit each day to 2-3 tasks.
- Rate limit more strictly than explanations.

## Backend Rate Limiting

The backend proxy should implement rate limits before calling the AI provider.

Potential identifiers:

- Anonymous installation/session identifier if implemented later.
- IP address as a coarse fallback.
- Account ID only if authentication is added later.

Suggested starting limits:

- `/ai/explain-answer`: 3 free requests/day.
- `/ai/quiz-summary`: 1 free request/day.
- `/ai/study-plan`: 1 free request/week.

These are planning numbers and should be revisited after tester feedback and actual model cost estimates.

## Request Size Limits

Suggested backend validation:

- `studyPathId`: max 80 characters.
- `topic`: max 120 characters.
- `difficulty`: max 40 characters.
- `question`: max 800 characters.
- `selectedAnswer`: max 400 characters.
- `correctAnswer`: max 400 characters.
- `staticExplanation`: max 1,000 characters.
- `goal`: max 300 characters.
- Topic arrays: max 5 items.
- `days`: v1 supports only `7`.

Reject oversized requests before building prompts.

## Response Limits

Suggested maximum response sizes:

- Wrong answer explanation: 5 short fields, each under 500 characters.
- Quiz summary: 4 short fields, recommended focus capped to 3 items.
- 7-day plan: 7 days, 1 focus per day, 2-3 tasks per day.

The backend should set provider max tokens conservatively and validate output length after generation.

## Caching Strategy

Use caching where it does not create privacy risk.

Good candidates:

- Wrong-answer explanations for bundled local quiz questions.
- Static explanation rewrites based on the same question and answers.

Avoid caching:

- Personal user goals if they may contain private information.
- Raw free-text prompts.
- Anything containing names, emails, resumes, or sensitive details.

If caching is added, store only normalized, minimal keys and safe generated output.

## Logging Strategy

Backend should log minimal metadata only:

- timestamp
- endpoint
- anonymous/session identifier if implemented later
- token/cost estimate
- success/error

Avoid logging:

- Full prompts.
- Full AI responses.
- Personal data.
- Raw goals if free text may contain private information.

Temporary debug logging should be disabled before release.

## Privacy And Data Safety Impact

Future AI features will send quiz/study context to a backend. Before enabling real AI:

- Update Privacy Policy.
- Update Google Play Data Safety answers.
- Explain that selected study context may be sent to backend for AI help.
- Avoid sending personal data.
- Do not require an account initially unless a future task adds authentication.
- Do not store raw conversations in v1.
- If analytics, crash reporting, billing, authentication, or cloud sync is added later, update Data Safety again.

## Failure Handling

AI should never block core study flows.

If backend fails:

- Wrong answer explanation falls back to static explanation.
- Quiz summary falls back to rule-based Smart Coach recommendation.
- Study plan shows a friendly retry message or simple local suggestion.

Timeouts should be short enough to keep the app feeling responsive.

## Rollout Plan

### Phase 1

Current rule-based Smart Coach only.

### Phase 2

Backend proxy with validation, rate limiting, environment variables, minimal logging, and mocked AI responses.

### Phase 3

Wrong-answer explanation from Android after explicit user action.

### Phase 4

Quiz summary AI review.

### Phase 5

7-day study plan.

### Phase 6

Premium limits and billing integration.
