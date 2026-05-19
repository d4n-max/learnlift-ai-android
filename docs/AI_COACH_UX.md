# AI Coach UX

Last updated: 2026-05-19

## Current Scope

AI Coach is available as a user-initiated study helper. It calls the Supabase `ai-coach` backend only after the user taps an AI action, and it keeps local explanations and Smart Coach recommendations available when AI is unavailable.

The current build does not hard-gate AI with Premium. The UI may describe AI Coach as Premium-ready, but testers can still use the current AI actions during validation.

## Wrong Answer Explanation

When a user answers a quiz question incorrectly, the quiz screen keeps the local explanation visible and offers an optional AI action:

- Button: `Explain with AI Coach`
- Loading text: `AI Coach is thinking...`
- Success card header: `AI Coach`
- Success card subtitle: `Personalized explanation`

The success card is structured into:

- AI title from the backend response.
- Main explanation from `conciseExplanation`.
- `Why the correct answer works`.
- `Study tip`.
- `Recommended topic`.

The local/static explanation remains visible below the AI response under `Local explanation`.

If AI fails, the UI shows a friendly fallback message and keeps the local explanation visible. It does not expose raw backend errors, OpenAI errors, stack traces, or debug details.

## AI Study Review

On quiz summary, AI Study Review remains optional and user-initiated:

- Button: `Generate AI Study Review`
- Loading text: `Generating your AI study review...`
- Success card subtitle: `AI Study Review`

The success card is structured into:

- `Summary`
- `Recommended focus`
- `Next session suggestion`
- `Encouragement`

If AI fails, the quiz summary keeps the local rule-based Recommended Focus visible and offers a single manual retry.

## 7-Day Study Plan

The Progress screen exposes an optional 7-day AI study plan action when a study path is selected:

- Button: `Generate 7-Day Study Plan`
- Loading text: `AI Coach is drafting...`
- Success card subtitle: `7-day plan`

The plan displays:

- Plan title.
- Day entries.
- Focus for each day.
- Short task list for each day.

If AI fails, the screen keeps the local Recommended Focus section available and offers a single manual retry.

## Loading, Retry, and Duplicate Tap Behavior

AI requests are never automatic on screen load. They run only after the user taps an AI button.

While a request is loading:

- The active AI button is disabled.
- A small loading indicator appears.
- Duplicate rapid taps are ignored by checking the loading state before launching a request.

If a request fails:

- The UI shows a friendly fallback.
- A retry button is shown where it is useful.
- Retry is manual only.
- There is no automatic retry loop.

## Usage Limit UI

AI entry points show subtle usage text:

- Free users see remaining previews, such as `Free AI previews left today: 2`.
- Free users see `AI previews reset tomorrow` after the local daily limit is used.
- Premium users see `Premium AI access active`.

When a Free user reaches a local AI limit, the app does not call Supabase. It shows a friendly message, keeps local explanations or Smart Coach recommendations available, and offers `View Premium`.

Failed requests count if the app already started the AI request. This prevents repeated failed requests from creating uncontrolled backend/API cost.

## Debug Details

Normal users do not see debug details.

Debug builds may log sanitized AI client diagnostics with the `LearnLiftAiCoach` tag:

- Configured endpoint host/path.
- Action sent.
- HTTP status code.
- Sanitized response preview.
- Parsing success or failure.

Logs must not include OpenAI API keys, Supabase service role keys, full prompts, private secrets, or excessive response bodies.

## Future Premium Positioning

Planned future behavior:

- Free users receive limited AI previews.
- Premium users receive higher local AI safety limits.
- AI explanations, AI Study Review, and 7-day plans may become Premium features.

The current implementation limits only AI-powered calls. It does not hard-paywall core study features.

## Manual QA Checklist

- Wrong answer -> tap AI Coach -> successful AI explanation appears.
- Wrong answer -> disable internet or use a bad backend URL -> friendly fallback appears.
- Retry after failure sends one new request and does not crash.
- Rapid repeated taps do not launch duplicate concurrent requests.
- Local explanation remains visible after AI success.
- Local explanation remains visible after AI failure.
- Long AI explanations wrap without clipping.
- Quiz summary AI Study Review success displays all sections.
- Quiz summary AI Study Review failure keeps local Recommended Focus visible.
- Progress 7-day study plan success displays day entries clearly.
- Progress 7-day study plan failure keeps local Recommended Focus visible.
- Light theme text contrast is readable.
- Dark theme text contrast is readable.
- Physical device scrolling keeps buttons and cards reachable.
