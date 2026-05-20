# Screenshot Plan v2

Last updated: 2026-05-20

Do not generate or upload screenshots until the final QA build is installed on a clean physical device or emulator.

## Google Play Phone Screenshot Requirements

- Format: PNG or JPEG.
- Quantity: 2 to 8 phone screenshots.
- Recommended aspect ratio: 9:16.
- Each side must be between 320 px and 3840 px.
- Avoid fake claims, private data, debug overlays, status-bar clutter, and broken AI/billing states.

## Phone Screenshot Set

### 1. Onboarding / Goal Selection

- Show: Choose your goal screen with the four goal options.
- Caption: `Start with your learning goal`
- Why it matters: shows first-run setup and personalization.
- Premium/AI required: No.
- Clean test data: fresh install or Settings restart onboarding.

### 2. Home Dashboard With Selected Path

- Show: Home with selected study path, daily goal, quick actions, Smart Review, and Today's Focus.
- Caption: `Plan your daily study session`
- Why it matters: communicates the app hub and core workflow.
- Premium/AI required: No.
- Clean test data: choose Job Interview or English path; keep cards readable.

### 3. Flashcards / Smart Review

- Show: Smart Review card or flashcard question with Reveal Answer.
- Caption: `Review flashcards with spaced repetition`
- Why it matters: highlights flashcards and Smart Review.
- Premium/AI required: No.
- Clean test data: mark at least one card Needs Review so Smart Review has content.

### 4. Quiz / Adaptive Quiz

- Show: Adaptive Quiz or quiz question with topic/difficulty chips.
- Caption: `Practice with adaptive quizzes`
- Why it matters: shows active study and adaptive practice.
- Premium/AI required: No.
- Clean test data: create weak topic signals first if showing Adaptive Quiz personalization.

### 5. AI Coach Explanation

- Show: Wrong answer AI Coach explanation card with title, explanation, study tip, and recommended topic.
- Caption: `Get AI-powered answer explanations`
- Why it matters: shows real AI Coach value.
- Premium/AI required: AI backend must be active; Premium not required for screenshot unless showing Premium state.
- Clean test data: use a harmless quiz question; avoid long clipped output.

### 6. Progress With Weak Topics

- Show: Progress screen with Topics to Review and Flashcard Review section.
- Caption: `Focus on your weakest topics`
- Why it matters: shows progress tracking, topic weakness, Smart Coach direction.
- Premium/AI required: No.
- Clean test data: complete a quiz with a few wrong answers first.

### 7. Premium Screen

- Show: Premium screen with Available now / Coming soon, monthly/yearly package display, and restore purchases.
- Caption: `Unlock more AI help with Premium`
- Why it matters: shows monetization clearly and honestly.
- Premium/AI required: RevenueCat offerings preferred; fallback pricing acceptable for draft screenshots if clearly not misleading.
- Clean test data: avoid technical setup errors or product-unavailable message in final store screenshots.

### 8. Daily Reminder / Settings Or Daily Session

- Show: Settings Daily reminder section, or Daily Session intro if reminder UI feels too settings-heavy.
- Caption: `Build a daily study habit`
- Why it matters: shows habit loop and local reminder support.
- Premium/AI required: No.
- Clean test data: reminders can be off or enabled; avoid showing permission-denied state.

## Optional Tablet Screenshots

If tablet screenshots are needed, reuse:

- Home dashboard.
- Quiz or Adaptive Quiz.
- Progress with weak topics.
- Premium screen.

Prepare both 7-inch and 10-inch variants only after phone screenshots are final.

## Capture Notes

- Use light mode unless dark mode is specifically needed for a second set.
- Use realistic local progress but avoid clutter.
- Keep AI responses concise and readable.
- Do not show guaranteed success claims.
- Do not show private keys, debug logs, or test credentials.
- Confirm screenshots match the Play Store listing text and available features.
