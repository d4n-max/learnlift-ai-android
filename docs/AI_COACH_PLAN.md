# LearnLift AI Coach Plan

## 1. Overview

AI Coach is a planned future feature for LearnLift AI that will provide contextual study support, explanations, recommendations, and review guidance. It should extend the existing learning flow rather than replace it.

AI Coach should support the current LearnLift AI experience by helping users understand what they are studying, why an answer is correct or incorrect, and what to practice next. It should enhance flashcards, quizzes, daily sessions, and progress review while keeping the app focused on practical skill building.

AI Coach is not part of the current MVP implementation. The MVP should continue to work with local JSON content and manually written explanations first.

## 2. Core Use Cases

Future AI Coach use cases include:

- Explain incorrect quiz answers in simple language.
- Generate personalized study suggestions.
- Summarize weak topics after a quiz or daily session.
- Create 7-day study plans.
- Generate extra practice questions from existing topics.
- Help users understand concepts they struggle with.
- Provide motivational but realistic feedback.

## 3. MVP-Safe First Version

The first AI Coach implementation should be intentionally limited. It should focus on narrow, low-risk learning assistance instead of becoming a general chatbot.

The first version should be limited to:

- Quiz answer explanations.
- Weak topic summaries.
- Simple study recommendations.
- Static or mocked AI responses first.
- No open-ended general chatbot at the beginning.

This keeps the feature testable, easier to moderate, and aligned with the current MVP roadmap.

## 4. What AI Coach Should Not Do

AI Coach should have clear product and safety boundaries.

AI Coach should not:

- Make guaranteed career, interview, exam, certification, or learning success promises.
- Provide harmful, misleading, or high-stakes advice.
- Replace teachers, mentors, therapists, doctors, lawyers, financial advisors, or other qualified professionals.
- Ask for unnecessary personal data.
- Store sensitive user data without a clear reason and user consent.
- Expose API keys in the Android app.
- Generate unsupported claims.

AI Coach should communicate uncertainty when appropriate and keep feedback practical, supportive, and grounded in the app's study content.

## 5. Technical Architecture Recommendation

The Android app should never call an AI provider directly with a secret API key. Mobile apps can be inspected, decompiled, proxied, or modified, which means any API key shipped inside the Android app should be considered exposed.

Recommended architecture:

1. Android app sends a limited AI Coach request to a secure backend proxy.
2. Backend proxy validates the request and builds the prompt from approved templates.
3. Backend proxy calls the AI provider using a server-side secret API key.
4. AI provider returns a response to the backend proxy.
5. Backend proxy applies safety, formatting, and cost controls.
6. Android app receives only the final AI response.

The backend proxy should handle:

- API key storage.
- Rate limiting.
- Request validation.
- Prompt templates.
- Safety rules.
- Logging minimal metadata.
- Cost control.

The Android app should receive only the final AI response needed for the current learning moment.

## 6. Suggested Backend Options

Realistic future backend options include:

- Supabase Edge Functions.
- Firebase Cloud Functions.
- Railway-hosted lightweight API.
- Cloudflare Workers.
- Simple Node backend.
- Simple Kotlin backend.

For this project, Supabase Edge Functions or a Railway-hosted backend could be good future choices. Supabase Edge Functions may fit well if the project later adopts Supabase for storage or auth. Railway may fit well for a small custom API with straightforward deployment.

No backend should be added until explicitly requested.

## 7. Prompting Strategy

AI Coach prompts should be narrow, grounded, and specific to the selected study path.

Prompt design principles:

- Keep prompts specific to the selected study path.
- Include the current question.
- Include the selected answer.
- Include the correct answer.
- Include the existing explanation when available.
- Include the topic.
- Include difficulty.
- Ask for concise explanations.
- Ask for an encouraging tone.
- Avoid overlong responses.
- Avoid hallucinated facts.
- Prefer grounding in local JSON content where possible.

Prompts should ask the model to explain, clarify, or recommend based on known app content. The model should not invent certifications, policies, scores, or guarantees that are not present in the provided context.

## 8. Privacy And Data Handling

AI Coach should minimize data collection and data transfer.

Privacy principles:

- Send only necessary context to the backend.
- Avoid sending personal data.
- Avoid storing raw user conversations in the MVP.
- Keep local progress local unless cloud sync is added later.
- Explain data usage clearly if AI is enabled later.

Example safe context could include a study path ID, topic ID, question text, selected answer, correct answer, and local explanation. The app should avoid sending names, email addresses, resumes, private notes, health information, financial information, or any other sensitive personal data unless a future feature has a clear need and consent model.

## 9. Cost Control

AI Coach should be designed to control costs from the beginning.

Cost control strategies:

- Limit AI requests per user or session.
- Start with AI only after quiz completion or wrong answers.
- Cache repeated explanations where possible.
- Use local/static explanations first.
- Add premium limits later if monetization is introduced.

The first implementation should prefer deterministic local content and mocked responses before any paid AI provider is connected.

## 10. Implementation Phases

### Phase 1

- Keep current static JSON explanations.
- Improve explanations manually.

### Phase 2

- Add AI Coach mock interface in Android.
- Show mocked AI suggestions on quiz summary.

### Phase 3

- Add backend proxy.
- Add real AI explanation endpoint.
- Use AI only for incorrect quiz answers.

### Phase 4

- Add personalized 7-day study plan generation.
- Add weak-topic review suggestions.

### Phase 5

- Add optional premium AI Coach limits if payments are implemented later.

## 11. Android App Integration Points

Possible future screens and components:

- Quiz summary AI suggestion card.
- Daily session summary AI review.
- Progress screen AI recommendation.
- Flashcard explanation helper.
- Study plan generator.

These should be added only after the base MVP study path, flashcard, quiz, and progress flows are stable.

## 12. Risks And Mitigations

### Hallucinated Explanations

Risk: AI may produce incorrect or unsupported explanations.

Mitigations:

- Ground prompts in local JSON content.
- Keep responses short and focused.
- Use AI mainly to rephrase known explanations at first.
- Add fallback static explanations.

### High API Cost

Risk: Frequent AI requests may become expensive.

Mitigations:

- Limit requests per session.
- Trigger AI only for incorrect answers or summaries.
- Cache repeated responses.
- Start with static and mocked content.

### Slow Responses

Risk: AI calls may make the app feel sluggish.

Mitigations:

- Show loading states.
- Use short prompts.
- Add backend timeouts.
- Fall back to local explanations when needed.

### Privacy Concerns

Risk: Users may not understand what data is sent to AI systems.

Mitigations:

- Send only necessary learning context.
- Avoid personal data.
- Do not store raw conversations in the MVP.
- Explain AI data usage clearly before enabling the feature.

### App Store Policy Concerns

Risk: AI-generated content may raise policy or safety review questions.

Mitigations:

- Keep the feature educational and narrow.
- Avoid high-stakes advice.
- Provide clear disclaimers where needed.
- Add content safety rules in the backend proxy.

### Users Overtrusting AI

Risk: Users may treat AI feedback as guaranteed or authoritative.

Mitigations:

- Avoid success guarantees.
- Use careful language.
- Encourage practice and review.
- Prefer explanations grounded in app content.

## 13. Open Decisions

- Which backend will be used?
- Which AI provider will be used?
- Will AI Coach be free, limited, or premium?
- What user data can be sent safely?
- Should AI responses be cached?
- Should AI be enabled only after account/auth exists?
