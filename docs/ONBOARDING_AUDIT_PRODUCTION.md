# LearnLift AI Production Onboarding Audit

Last updated: 2026-06-17

## Verdict

Production verdict: good foundation, activation can be faster.

The current onboarding is safe for a live Google Play app. It is short, local-first, skippable, and does not introduce an aggressive paywall. The main activation opportunity is after setup: `Start learning` currently finishes onboarding and lands on Home, where the user must choose a second CTA before their first meaningful learning action. For stronger activation, the first-run path should make the next action feel immediate: start a short daily session, flashcards, or quiz from the selected path.

## Activation Target

Target first meaningful learning action:

- Choose a goal.
- Choose or accept a study path.
- Start flashcards, quiz, or a daily session quickly.
- Understand that AI Coach helps after mistakes, without making onboarding feel like an AI sales pitch.

Recommended activation event:

- Primary: first flashcard reviewed, first quiz answer submitted, or first daily session started.
- Secondary: onboarding completed with selected path and daily goal persisted.

## Current Flow

Source reviewed:

- `app/src/main/java/com/learnliftai/app/ui/screens/OnboardingScreen.kt`
- `app/src/main/java/com/learnliftai/app/ui/navigation/LearnLiftApp.kt`
- `app/src/main/java/com/learnliftai/app/data/LocalOnboardingRepository.kt`
- `app/src/main/java/com/learnliftai/app/domain/model/OnboardingPreferences.kt`
- `app/src/main/java/com/learnliftai/app/data/StudyPathRepository.kt`
- `app/src/main/java/com/learnliftai/app/ui/screens/HomeScreen.kt`
- `app/src/main/java/com/learnliftai/app/ui/screens/DailyStudySessionScreen.kt`
- `app/src/main/java/com/learnliftai/app/ui/screens/SettingsScreen.kt`
- `docs/ONBOARDING_V2.md`

Current onboarding screens:

1. Welcome
   - App name, tagline, value copy, local-first message.
   - CTA: `Get started`.
   - Secondary: `Skip for now`.

2. Choose your goal
   - Improve English for work.
   - Prepare for job interviews.
   - Prepare for IT / QA interviews.
   - Build a daily learning habit.
   - CTA: `Continue`.
   - Secondary: `Skip for now`.

3. Choose daily time
   - 5, 10, 15, or 20 minutes.
   - Default is 10 minutes.
   - CTA: `Continue`.
   - Secondary: `Skip for now`.

4. Recommended path
   - Shows recommended free path.
   - Allows `Change path`.
   - CTA: `Start learning`.
   - Secondary: `Skip for now`.

After completion:

- `LocalOnboardingRepository.completeOnboarding()` persists onboarding completion, goal, recommended path id, daily minutes, and completion timestamp in local DataStore.
- `LearnLiftApp` sets the selected study path in progress storage.
- App navigates to Home.
- Home shows selected path, daily goal, quick actions, Smart Review, local Smart Coach guidance, Premium teaser, and progress stats.

Skip behavior:

- `Skip for now` marks onboarding complete.
- Default path becomes `job-interview-prep`.
- Daily goal becomes 10 minutes.
- App navigates to Home.

Reminder behavior:

- Reminders are not requested during onboarding.
- Settings contains the reminder controls.
- Default reminder state is off, with a default time of 19:00.
- Future reminder setup should remain optional. If added near onboarding completion, it should show a soft `Set reminder` / `Maybe later` choice and request Android 13+ notification permission only after the user taps `Set reminder`.

Review prompt behavior:

- Onboarding does not show review prompts.
- Future Google Play In-App Review prompts should wait until after positive learning thresholds described in `docs/REVIEWS_AND_NOTIFICATIONS_IMPLEMENTATION_PLAN.md`.

Premium behavior:

- Onboarding does not show a purchase screen.
- Premium appears later on Home and in Settings.
- This is appropriate for production; do not add an aggressive onboarding paywall.

## Checks

### Too Long / Too Short

Finding: The flow is appropriately short at four screens, but the final step adds one extra click before real study begins.

Impact: Users who tap `Start learning` may expect to begin the selected path immediately. Landing on Home is understandable, but it slightly weakens the activation moment.

Recommendation: Keep four screens, but change the final CTA or destination so the user starts a short daily session directly, or lands on Home with a very prominent first-session CTA.

Priority: P1.

### Clear Value Proposition

Finding: The welcome copy explains habits, practice, local-first setup, and setup speed. The goal and path screens are clear, but AI Coach value is not introduced before the user reaches mistakes.

Impact: The app's AI differentiation may not be obvious during onboarding, while still correctly avoiding hype and paywall pressure.

Recommendation: Add a small non-pushy value line on the final screen or first Home state: `After mistakes, AI Coach can explain what went wrong and what to practice next.`

Priority: P2.

### Skip Works

Finding: Source review confirms skip completes onboarding, sets `job-interview-prep`, persists a 10-minute goal, and routes to Home.

Impact: Good. The user is not trapped.

Recommendation: Keep skip behavior. Consider making the skip result explicit on Home with copy such as `Job Interview Prep is selected. You can change it anytime.`

Priority: P3.

### No Visual Artifacts

Finding: Static source review shows onboarding uses a scrollable Compose column, reusable cards/buttons, simple text, and no remote images or complex media. This lowers artifact risk. Device screenshots were not generated during this audit.

Impact: Likely safe, but final production confidence still requires device QA across small phones, large phones, dark mode, and font scaling.

Recommendation: Run the QA checklist below on at least one small device profile and one large device profile before the next release.

Priority: P2.

### Selected Path Persists

Finding: Completion stores the recommended path in onboarding preferences and separately sets the selected path through `LocalProgressRepository` from `LearnLiftApp`.

Impact: Good. Home and study modes can use the selected path immediately.

Recommendation: Add regression QA for app restart after completing onboarding and after skip.

Priority: P2.

### Daily Goal Persists

Finding: `dailyStudyMinutes` is persisted in `learnlift_onboarding` and used by Home, Settings, and Progress.

Impact: Good. The selected daily goal has visible continuity after onboarding.

Recommendation: The current Settings screen shows the daily goal but does not appear to let users edit it directly. Add a future task to edit daily goal from Settings without restarting onboarding.

Priority: P2.

### Reminder Setup Is Not Too Pushy

Finding: Reminders are not asked for during onboarding. They are opt-in from Settings.

Impact: Strong production choice. It avoids an early permission/friction spike before the learner has experienced value.

Recommendation: Keep reminders out of first-run onboarding for phase 1. Later, trigger a soft reminder prompt only after the first completed session or after the user returns on day 2. If an onboarding completion reminder card is added, it must not request permission until the user explicitly taps `Set reminder`.

Priority: P3.

### Review Prompt Is Correctly Absent

Finding: Onboarding does not currently show an in-app review prompt.

Impact: Good. Users have not experienced enough value during onboarding to make a review ask appropriate.

Recommendation: Keep review prompts out of onboarding and first launch. Use Daily Session completion or Quiz Summary only after the thresholds in `docs/REVIEWS_AND_NOTIFICATIONS_IMPLEMENTATION_PLAN.md` are satisfied.

Priority: P1.

### Gets To First Session Fast

Finding: The user can reach a first session quickly, but not directly. Path completion sends the user to Home, then they tap `Start Daily Session`, `Start Flashcards`, or `Start Quiz`.

Impact: This is the biggest activation friction. The setup is short, but the first learning action is still one screen and one decision away.

Recommendation: Make the post-onboarding default action more decisive. Best option: final CTA becomes `Start 5-card session` or `Start first session` and opens Daily Session. Home remains available after completion.

Priority: P1.

## Friction Points

### P1: Final CTA Does Not Start Learning Directly

Finding: `Start learning` completes onboarding and navigates to Home.

Impact: The CTA promises action, but the user gets a dashboard. This can reduce first-session starts.

Recommendation: Either route directly to Daily Session after completion, or rename the CTA to `Go to Home` and add a stronger Home first-session prompt. For activation, direct Daily Session is better.

### P1: Home Has Too Many First Choices

Finding: Home quick actions show Daily Session, Flashcards, Smart Review, Quiz, Adaptive Quiz, and Change Study Path.

Impact: This is useful for returning users, but first-run users may pause because there are too many valid first actions.

Recommendation: Add a first-run Home state until the first study action is completed:

- Primary: `Start first daily session`.
- Secondary: `Try flashcards`.
- Tertiary: `Take a quiz`.

### P2: AI Coach Value Is Late

Finding: AI Coach appears in quiz mistake and review contexts, but onboarding does not preview the behavior.

Impact: Users may not understand the AI benefit until later.

Recommendation: Add one lightweight sentence after path recommendation or on first Home: `When you miss a quiz question, AI Coach can explain the mistake and suggest what to review.`

### P2: Daily Goal Cannot Be Edited Directly Later

Finding: Settings shows daily goal and allows restarting onboarding, but direct daily-goal editing is not part of the reviewed settings flow.

Impact: Users who choose too low or too high a goal may restart onboarding or ignore the goal.

Recommendation: Add direct daily goal editing in Settings as a small retention improvement.

### P3: `Build a daily learning habit` Defaults To Job Interview Prep On Fresh Install

Finding: Habit goal maps to existing selected path if present, otherwise `job-interview-prep`.

Impact: On a fresh install, a habit-focused learner may get a career-specific path that feels less neutral.

Recommendation: Consider making the habit default choose between the three free paths, or defaulting to a general path if one is added later.

### P3: Premium Teaser Appears On First Home

Finding: Home includes a Premium teaser after the main learning cards.

Impact: It is not aggressive and appears after learning actions, so it is production-safe. Still, brand-new users may see monetization before experiencing AI Coach.

Recommendation: Keep Premium out of onboarding. Consider hiding or lowering the Home Premium teaser until the user completes one session or uses one AI preview.

## Recommended Improvements

### Screen Order

Keep the current order:

1. Welcome.
2. Goal.
3. Daily time.
4. Recommended path.

Do not add a reminder screen, login screen, paywall screen, or permission screen before first value.

Recommended future change:

1. Welcome.
2. Goal.
3. Daily time.
4. Recommended path with first-session CTA.
5. Directly open Daily Session intro.

### CTA Wording

Current:

- `Get started`
- `Continue`
- `Start learning`
- `Skip for now`

Recommended:

- Welcome primary: `Set my goal`
- Goal primary: `Continue`
- Daily time primary: `Choose my path`
- Recommended path primary: `Start first session`
- Recommended path secondary: `Change path`
- Skip: keep `Skip for now`

If direct Daily Session routing is not implemented yet, use:

- Final CTA: `Go to my dashboard`
- Home first-run CTA: `Start first session`

### Copy Suggestions

Welcome:

Current direction is good. Recommended variant:

`Set one goal, get a focused study path, and start practicing in under a minute. Progress stays local on this device.`

Goal screen subtitle:

`Pick the outcome you want LearnLift AI to support first.`

Daily time subtitle:

`Choose a realistic daily target. You can adjust it later.`

Daily time options:

- `5 minutes` - `Quick reset`
- `10 minutes` - `Recommended start`
- `15 minutes` - `Steady practice`
- `20 minutes` - `Deeper session`

Recommended path subtitle:

`Based on your goal, start here today.`

Recommended daily goal card:

`Start with a focused session today. You can change your path later from Home or Settings.`

AI Coach line:

`After quiz mistakes, AI Coach can explain what went wrong and suggest what to review next.`

First Home prompt:

`Your path is ready. Start a short session now to create your first progress signal.`

### Default Path Suggestions

Current free goal mapping is mostly sensible:

- English for work -> English Vocabulary & Speaking Prep.
- Job interviews -> Job Interview Prep.
- IT / QA interviews -> IT / QA Interview Prep.
- Daily habit -> Job Interview Prep by default.

Recommended adjustment:

- Keep the first three mappings.
- For daily habit, show the recommended path but make `Change path` more visible, or add a neutral general habit path in the future.

### First Session CTA

Recommended first implementation:

- Change final onboarding CTA to `Start first session`.
- After persisting onboarding and selected path, open Daily Session instead of Home.
- Daily Session intro already starts with a clear `Start session` CTA and limited content.

Alternative if routing is kept unchanged:

- Add a first-run Home card above Quick Actions.
- Primary CTA: `Start first session`.
- Secondary CTA: `Try flashcards`.
- Hide the full Quick Actions list until after one study action.

### Premium Mention Timing

Current timing is acceptable:

- No Premium in onboarding.
- Premium teaser appears on Home after path setup.
- Premium gates are attached to AI limits, study plans, and Premium packs.

Recommended refinement:

- Do not mention Premium during onboarding.
- Consider delaying the Home Premium teaser until after first session completion, first quiz completion, or first AI Coach preview.
- If Premium is mentioned early, frame it softly as expanded help, never as a blocker.

## QA Checklist

Fresh install:

- [ ] Onboarding appears on first launch.
- [ ] Bottom navigation is hidden during onboarding.
- [ ] Welcome screen has no clipping on small devices.
- [ ] `Get started` or updated CTA advances to goal selection.
- [ ] `Skip for now` works from every onboarding step.
- [ ] Skip selects `job-interview-prep`.
- [ ] Skip saves 10-minute daily goal.
- [ ] Skip does not show Premium or reminder prompts.

Goal selection:

- [ ] Each goal can be selected.
- [ ] Selected card state is visually clear in light mode.
- [ ] Selected card state is visually clear in dark mode.
- [ ] Long goal labels do not clip with large font settings.
- [ ] Goal-to-path mapping is correct.

Daily time:

- [ ] Default is 10 minutes.
- [ ] 5, 10, 15, and 20 minutes can be selected.
- [ ] Selected daily goal persists after completion.
- [ ] Home shows the selected daily goal.
- [ ] Progress and Settings show the selected daily goal.

Recommended path:

- [ ] Recommended path appears for each goal.
- [ ] `Change path` opens path selection within onboarding.
- [ ] Only free paths are available inside onboarding.
- [ ] Selected manual path persists.
- [ ] Final CTA completes onboarding.
- [ ] Final CTA either starts Daily Session or lands on Home with a clear first-session CTA.

Persistence:

- [ ] App restart does not show onboarding again after completion.
- [ ] Selected study path remains selected after restart.
- [ ] Daily goal remains selected after restart.
- [ ] Onboarding completion timestamp is saved.
- [ ] Settings `Restart onboarding` returns to onboarding.
- [ ] Restart onboarding does not clear progress, Premium state, AI usage, topic stats, or flashcard review state.

First learning action:

- [ ] New user can start Daily Session within one tap after onboarding completion.
- [ ] New user can start Flashcards from Home.
- [ ] New user can start Quiz from Home.
- [ ] First flashcard review updates progress.
- [ ] First quiz answer records topic performance.
- [ ] Wrong quiz answer exposes local explanation.
- [ ] AI Coach action is discoverable after mistakes where available.

Reminder behavior:

- [ ] Onboarding does not request notification permission.
- [ ] Onboarding does not enable reminders automatically.
- [ ] Optional reminder setup, if added, only requests permission after explicit `Set reminder` tap.
- [ ] Settings reminder default is off.
- [ ] Enabling reminder is opt-in and local.

Review behavior:

- [ ] Onboarding does not show review prompt.
- [ ] First launch does not show review prompt.
- [ ] Completing onboarding does not immediately ask for a review.
- [ ] Review prompt waits for positive learning thresholds.

Premium behavior:

- [ ] Onboarding does not show Premium.
- [ ] Onboarding does not block free paths behind Premium.
- [ ] Home Premium teaser does not prevent first learning action.
- [ ] AI Coach/Premium copy does not imply core study is paywalled.

Visual QA:

- [ ] Small phone portrait has no text clipping.
- [ ] Large phone portrait layout looks balanced.
- [ ] Dark mode contrast is readable.
- [ ] Light mode contrast is readable.
- [ ] Font scale 1.3x remains usable.
- [ ] Cards and CTAs do not overlap bottom navigation after onboarding.
- [ ] Back behavior returns to previous onboarding step.

## Next Implementation Task

Recommended next task:

`Route onboarding completion to the first Daily Session and update final CTA copy.`

Scope:

- Change final onboarding CTA from `Start learning` to `Start first session`.
- After `completeOnboarding()` and `setSelectedStudyPathId()`, open `DailyStudySessionScreen` for the selected path.
- On session finish, return to Home as currently implemented.
- Add source or manual QA coverage for completion, skip, app restart, and Settings restart onboarding.

Small alternative:

`Add first-run Home activation card.`

Scope:

- Detect completed onboarding with zero study actions.
- Show a top Home card with `Start first session`.
- Keep Flashcards and Quiz as secondary actions.
- Hide or move the Premium teaser until after first study action.
