# Project Requirements Document: LearnLift AI Redesign

## 1. Product Overview
**Product Name:** LearnLift AI  
**Tagline:** Elevate Your Skills, Effortlessly.  
**Category:** AI-powered Study Coach / Education / Career Skills  
**Platform:** Android (Mobile)  

### Vision
To provide a premium, modern, and motivational learning experience that leverages AI to personalize study paths for students, job seekers, and career switchers. The redesign focuses on moving away from generic educational visuals toward an "Intelligent Ascendance" aesthetic—calm, sophisticated, and high-fidelity.

---

## 2. Target Audience
*   **Students:** Academic learners looking for efficient study tools.
*   **Job Seekers:** Individuals preparing for interviews (General or Tech/QA).
*   **Career Switchers:** Professionals upskilling in new domains.
*   **English Learners:** Users focusing on vocabulary and conversational fluency.

---

## 3. Brand & Design Identity
### Visual Language
*   **Style:** Material 3 inspired, premium, modern, and uncluttered.
*   **Color Palette:**
    *   **Primary Purple:** #553C9A (Brand anchor)
    *   **Accent Pink:** #ED64A6 (Sparse highlights, key states)
    *   **Surface:** Soft lavender/off-white (Light mode) to avoid "plain white" defaults.
*   **UI Characteristics:** 24px rounded corners, subtle gradients for "high-value" elements, soft shadows, and generous whitespace.

---

## 4. Key Functional Modules

### 4.1 Home Dashboard
*   **Branded Header:** LA Logo, app name, and tagline.
*   **Active Path:** Prominent card showing current progress in the selected study path.
*   **Primary Action:** "Start Daily Session" CTA.
*   **Secondary Actions:** Quick access to Flashcards and Quizzes.
*   **Momentum Tracker:** At-a-glance streak, last score, and card counts.
*   **AI Coach Integration:** Personalized focus recommendations directly on the home feed.

### 4.2 Study Path Selection
*   **Discovery:** Card-based browsing of specialized curricula (English, Job Prep, Tech/QA).
*   **Metadata:** Display difficulty, daily time commitment, and category for each path.
*   **State Management:** Clear visual distinction for the currently active path.

### 4.3 Learning Modes
*   **Flashcards:** Elegant card-flipping interface with "Needs Review" and "I Know This" actions. Includes topic/difficulty tagging.
*   **Quiz Mode:** Multiple-choice format with immediate feedback. Includes an "AI Coach" button for deep-dive explanations on incorrect answers.

### 4.4 Progress & Insights
*   **Personal Dashboard:** Detailed breakdown of cards mastered vs. needing review.
*   **Performance Metrics:** Quiz completion rates and average scores.
*   **Predictive AI:** "Recommended Focus" areas based on recall latency and accuracy.
*   **Premium Teaser:** Advanced trend analysis locked behind the premium tier.

---

## 5. Premium Strategy (Cognitive Flow)
The "Premium" tier is positioned as a value-add for serious learners, emphasizing "Cognitive Flow" and "Unlimited Potential."
*   **Features:** AI Coach Explanations, 7-Day Study Plans, Unlimited Practice, Full Content Packs, and Advanced Analytics.
*   **Monetization:** Monthly and Yearly subscription models with "Best Value" highlighting.

---

## 6. Technical Considerations
*   **Implementation:** Optimized for Jetpack Compose (Android).
*   **Theming:** Systematic use of tokens for Light and Dark mode parity.
*   **Consistency:** Shared component library for Top App Bars, Bottom Navigation, and Card structures.
