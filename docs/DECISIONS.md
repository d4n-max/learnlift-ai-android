# LearnLift AI Decisions

This document records product and technical decisions as the project evolves.

## Decision Log

### 2026-05-07: Initial Platform Direction

- Decision: Build LearnLift AI as an Android app.
- Reason: The first target platform is Android.

### 2026-05-07: Initial Package Name

- Decision: Use `com.learnliftai.app`.
- Reason: Matches the app name and provides a stable Android package identifier.

### 2026-05-07: Product Positioning

- Decision: Position LearnLift AI as a general skill-building and study coach app.
- Reason: The product should support users preparing for interviews, exams, certifications, language improvement, and career growth instead of being limited to one niche.

### 2026-05-07: Initial MVP Strategy

- Decision: Support multiple study paths from the beginning while keeping the first version simple.
- Reason: A study-path model gives the app room to grow and avoids hard-coding the experience around a single content category.

### 2026-05-07: Initial Study Paths

- Decision: Start with English Vocabulary & Speaking Prep, Job Interview Prep, and IT / QA Interview Prep as an optional sample path.
- Reason: These paths cover broad early user needs while keeping the initial content manageable.

### 2026-05-07: MVP Content Source

- Decision: Use local JSON content organized by study path for the MVP.
- Reason: Keeps the first version simple and avoids backend complexity while allowing new paths to be added later.

### 2026-05-07: Excluded Services

- Decision: Exclude Firebase, authentication, payments, live AI chat, backend admin tools, multi-language support, and social features from the MVP.
- Reason: These features are outside the first release scope and would add unnecessary complexity before validating the core study experience.

## Pending Decisions

- Local persistence mechanism for progress and streaks.
- Exact JSON schema for study paths and content.
- Initial content size for MVP testing.
- Whether first-run guidance is needed for the first version.
- How much IT / QA content to include in the optional sample path.
