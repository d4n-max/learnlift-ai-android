# AI Backend Proxy Plan

## Core Rule

The Android app must never call an AI provider directly with a secret API key.

Future AI Coach requests must follow this path:

1. Android app sends a small, validated request to a LearnLift AI backend proxy.
2. Backend proxy validates the request and builds a prompt from approved templates.
3. Backend proxy calls the AI provider using a server-side environment variable.
4. Backend proxy applies response limits and safety checks.
5. Android app receives only the final structured response.

No AI API keys, provider SDK secrets, or backend credentials should be shipped inside the Android app.

## AI Features For V1

### 1. Explain Wrong Answer

Trigger: user answers a quiz question incorrectly and chooses to ask for help.

The app sends minimal quiz context:

- Study path ID.
- Topic.
- Difficulty.
- Question text.
- Selected answer.
- Correct answer.
- Existing static explanation.

The backend returns a concise explanation and one study tip.

### 2. Quiz Summary / Weak-Topic Review

Trigger: user completes a quiz and chooses AI review.

The app sends:

- Study path ID.
- Score.
- Total question count.
- Incorrect topics.
- Weak topics.

The backend returns a short weak-topic summary, recommended focus topics, next session suggestion, and encouragement.

### 3. 7-Day Study Plan

Trigger: user selects a study path and goal, then requests a plan.

The app sends:

- Study path ID.
- Goal.
- Number of days, initially `7`.
- Level, initially `beginner` unless selected later.

The backend returns a concise 7-day plan with daily focus and task lists.

## Explicit Exclusions

V1 AI Coach should not include:

- Open-ended general AI chat.
- Medical, legal, financial, therapy, or other high-stakes advice.
- Guaranteed job, exam, certification, interview, or career success promises.
- Storage of sensitive personal data.
- AI-generated claims not grounded in provided app content.
- Automatic AI calls for every screen or every answer.

## Proposed Endpoints

### POST `/ai/explain-answer`

Request:

```json
{
  "studyPathId": "job-interview-prep",
  "topic": "STAR method",
  "difficulty": "beginner",
  "question": "What does the S in STAR stand for?",
  "selectedAnswer": "Skill",
  "correctAnswer": "Situation",
  "staticExplanation": "STAR stands for Situation, Task, Action, Result."
}
```

Response:

```json
{
  "title": "Review the STAR method",
  "conciseExplanation": "The S stands for Situation. It sets the context before you explain your task, action, and result.",
  "whyCorrectAnswerWorks": "Situation is the first step because interviewers need the background before they can understand what you did.",
  "studyTip": "Practice one answer by naming the Situation in a single sentence before moving to Task.",
  "recommendedTopic": "STAR method"
}
```

### POST `/ai/quiz-summary`

Request:

```json
{
  "studyPathId": "job-interview-prep",
  "score": 7,
  "totalQuestions": 10,
  "incorrectTopics": ["STAR method", "strengths and weaknesses"],
  "weakTopics": ["STAR method", "concise answers"]
}
```

Response:

```json
{
  "summary": "You are close to a strong score. The main opportunity is making interview answers more structured and concise.",
  "recommendedFocus": ["STAR method", "concise answers"],
  "nextSessionSuggestion": "Review 3 flashcards on answer structure, then take one short quiz.",
  "encouragement": "Good progress. A focused review round should help the next attempt feel smoother."
}
```

### POST `/ai/study-plan`

Request:

```json
{
  "studyPathId": "job-interview-prep",
  "goal": "Prepare for a junior QA interview",
  "days": 7,
  "level": "beginner"
}
```

Response:

```json
{
  "title": "7-Day Job Interview Prep Plan",
  "days": [
    {
      "day": 1,
      "focus": "Interview answer structure",
      "tasks": ["Review STAR method flashcards", "Practice one answer out loud"]
    }
  ]
}
```

## Security Requirements

- AI provider API key must exist only in backend environment variables.
- Android app must not contain AI API keys or backend secrets.
- Backend must validate request shape and required fields.
- Backend must reject unknown endpoint payloads.
- Backend must limit request body size.
- Backend must limit string lengths for question, answer, explanation, topic, and goal fields.
- Backend must rate limit requests per anonymous device/session identifier if implemented later.
- Backend must avoid logging full prompts unless temporarily required for debugging.
- Backend should log minimal metadata only:
  - timestamp
  - endpoint
  - anonymous/session identifier if implemented later
  - token/cost estimate
  - success/error
- Backend should not store raw user content in v1.
- Backend should return safe, concise educational guidance.
- Backend should time out slow AI provider requests and return a fallback-friendly error.

## Backend Options Compared

| Option | Setup complexity | Cost control | Env vars | Rate limiting | Logging | Deployment | Android integration | Notes |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Supabase Edge Functions | Medium | Good with quotas and function-level checks | Strong | Manual or database-backed | Good dashboard and logs | Simple once Supabase project exists | HTTPS endpoints | Best if LearnLift later uses Supabase auth/storage |
| Railway Node.js API | Low to medium | Good with app-level middleware | Strong | Straightforward middleware | Good service logs | Very simple Git deploy | HTTPS endpoints | Flexible and familiar for a small custom API |
| Cloudflare Workers | Medium | Very good at edge limits | Strong | Strong platform options | Good but edge-oriented | Simple after setup | HTTPS endpoints | Excellent scale, but adds Workers-specific patterns |
| Firebase Cloud Functions | Medium to high | Good, but Firebase footprint grows | Strong | Available through platform/app logic | Good | Simple if already in Firebase | HTTPS callable or REST | Not ideal now because Firebase is intentionally excluded |

## Recommendation

Original primary recommendation for LearnLift AI v1: **Railway-hosted Node.js API**.

Why Railway is best for this project right now:

- Fastest practical setup for a solo Android developer.
- Simple REST endpoints that match the Android contract.
- Easy environment variables for AI provider keys.
- Familiar middleware for validation, rate limiting, request size limits, and logging.
- Clear deployment path from a small Git repository.
- Avoids introducing Firebase, auth, or database decisions too early.

Selected implementation for Task 34: **Supabase Edge Functions**.

Supabase is a strong option if LearnLift AI soon needs authentication, cloud sync, or server-side storage. It is slightly more platform-shaped than Railway, but it becomes a good practical choice when the project wants deployable serverless functions without adding a separate Node hosting service.

Task 34 adds a Supabase Edge Function scaffold named `ai-coach`. It exposes one action-routed endpoint:

```text
POST /functions/v1/ai-coach
```

Supported actions:

- `explain_answer`
- `quiz_summary`
- `study_plan`

Android is integrated with this backend through the optional, user-initiated AI Coach client. Local static explanations and rule-based Smart Coach recommendations remain the fallback path.

## Implementation Phases

### Phase 1

Current rule-based Smart Coach only. No network calls.

### Phase 2

Create backend proxy with validation, prompt templates, rate limits, environment variables, and safe provider responses.

### Phase 3

Android calls backend for wrong answer explanations only after explicit user action.

### Phase 4

Add quiz summary AI review after quiz completion.

### Phase 5

Add 7-day study plan generation.

### Phase 6

Add Premium limits and billing integration in a separate task.

## Task 41 Hardening Notes

The Supabase `ai-coach` function now normalizes provider errors such as insufficient quota, invalid API key, missing model, rate limits, and response parsing failures into predictable JSON responses. Android treats these failures as fallback-safe and keeps local explanations or Smart Coach guidance visible.

Recommended OpenAI model configuration:

```text
OPENAI_MODEL=gpt-4.1-mini
```
