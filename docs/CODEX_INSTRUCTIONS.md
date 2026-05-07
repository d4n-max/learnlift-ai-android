# Codex Instructions

## Project Rules

- Use Kotlin and Jetpack Compose.
- Use simple MVVM.
- Use Material 3.
- Keep features small and testable.
- Do not add unnecessary libraries.
- Do not add Firebase, payments, authentication, or AI API calls until explicitly requested.
- Follow the brand guide colors when creating UI.
- Structure the app so new study paths can be added later through local JSON content.
- After each task, summarize changed files, how to test, and the next recommended step.

## Architecture Guidance

- Prefer simple, readable code over premature abstraction.
- Keep UI state explicit and easy to inspect.
- Separate screen UI, state, and content loading where practical.
- Use local JSON content for the MVP.
- Organize content by study path from the beginning.
- Keep feature boundaries clear: dashboard, study path selection, daily study session, flashcards, quiz, and progress.
- Avoid hard-coding the product to one study niche.

## UI Guidance

- Build with Jetpack Compose and Material 3 components.
- Use the primary purple as the main brand color.
- Use the accent pink sparingly for highlights, progress, buttons, or important states.
- Keep the UI clean, modern, professional, friendly, and aspirational.
- Avoid childish visuals or overly playful UI patterns.

## Dependency Guidance

- Start with Android and Compose defaults.
- Add libraries only when they clearly reduce complexity or are explicitly requested.
- Do not introduce networking, backend, analytics, payment, authentication, or AI dependencies during the MVP unless the user asks for them.

## Task Completion Format

After each development task, provide:

- Changed files
- How to test
- Next recommended step
