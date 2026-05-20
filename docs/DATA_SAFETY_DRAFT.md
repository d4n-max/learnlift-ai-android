# LearnLift AI Data Safety Draft

## Current App Behavior

LearnLift AI is primarily a local-first Android app. Study content and normal study progress remain on the device, while optional AI Coach and Premium billing features may contact third-party services when configured and used.

- The app stores progress locally on the device using DataStore.
- The app uses local JSON content bundled with the app.
- The app does not require login.
- The app does not use cloud sync.
- The app does not send study context to AI automatically.
- Optional AI Coach actions may send limited study context to the Supabase backend proxy only when the user taps an AI action.
- Local AI usage counters are stored on the device to limit accidental AI cost. These counters are not synced to a backend in the current build.
- Premium subscription checks and purchases are processed through Google Play and RevenueCat. RevenueCat is used to manage subscription entitlement status. The Android app includes a RevenueCat public SDK key, which is not a private secret.
- The app does not directly handle private payment card data.
- The app does not include ads.
- The app does not include analytics or crash reporting based on the current code scan.
- The app does not intentionally collect personal information.

## Data Collected

Study progress is stored locally. External data transfer is limited to optional AI Coach requests and Premium billing/entitlement checks when those features are used.

The app should not send personal profile data, email, account data, resumes, private notes, device identifiers, or sensitive personal data for AI Coach actions.

## Data Stored Locally

The app stores basic study progress locally on the user's device, including:

- Selected study path
- Flashcard progress counts
- Quiz completion stats
- Last quiz score
- Streak and last study date
- Local AI usage date and per-action AI request counters
- Local topic performance by study path and topic
- Local reminder preferences for optional daily study notifications

## Data Sharing

The app may interact with these services:

- Supabase Edge Function AI backend proxy: used only for optional, user-initiated AI Coach requests.
- RevenueCat and Google Play: used for Premium product lookup, subscription purchase flow, restore purchases, and subscription entitlement status checks.

The app does not use an ad network, analytics service, account backend, Firebase, or cloud sync in the current implementation.

Local daily study reminders are scheduled on the device. There is no push notification server, no Firebase Cloud Messaging, and no reminder data is sent to a backend.

## Data Deletion

Users can reset progress in the app.

The reset action clears local progress stats while keeping the app usable without an account.

## Security

Normal study progress is stored locally and is not cloud synced.

No OpenAI API keys, Supabase service-role keys, RevenueCat private API keys, backend credentials, or payment credentials should be stored in the Android app. The RevenueCat public SDK key is a client-side configuration value, not a private secret.

## Google Play Data Safety Draft Answers

- Data collected: likely none externally for the current MVP.
- Data shared: review before release because optional AI Coach and RevenueCat billing integrations may contact third-party services.
- Data processed ephemerally: AI Coach study context should be processed only for the requested response and not stored in v1.
- Account creation: not required.
- User deletion request process: in-app reset clears local progress; no server account exists.
- Encryption in transit: required for Supabase AI proxy, RevenueCat, and Google Play interactions.
- Payments: subscription checkout is handled by Google Play; the app checks subscription status through RevenueCat.

## Future Warning

If AI Coach, analytics, authentication, crash reporting, payments, cloud sync, remote content, or backend services are added later, the Data Safety form must be reviewed and updated before release.

Future AI Coach work must also avoid placing secret API keys in the Android app. Any AI provider integration should use a secure backend proxy as described in `docs/AI_COACH_PLAN.md`.

Task 34 adds Supabase Edge Function backend proxy scaffolding for future AI Coach features.

Task 35 adds Android client integration for optional, user-initiated AI Coach actions. When a user taps an AI action, the app may send limited study context to the Supabase backend proxy, such as the current quiz question, selected answer, correct answer, static explanation, quiz score, weak topics, selected study path, and a short study goal. The app should not send personal profile data, email, account data, resumes, private notes, device identifiers, or sensitive personal data for these AI actions.

Before enabling real AI in production or broader testing, update the Privacy Policy and Google Play Data Safety answers to describe this backend data transfer. Real AI responses also require the backend to have OpenAI API billing/quota active.

Task 41 hardens the real AI backend/client path for v2 testing. OpenAI API billing/quota has been enabled manually, but the app must still treat AI as optional. If OpenAI, Supabase, the network, quota, model configuration, or response parsing fails, Android keeps local explanations and rule-based Smart Coach recommendations available.

Task 43 adds client-side AI usage counters with DataStore. These counters track only the local date and counts for AI explanation, quiz summary, and study plan requests. They are used for local cost control and are not a server-side abuse-protection system.

Task 45 adds local topic weakness tracking. Topic performance is stored on the device by study path and topic. It is not synced to a backend. If the user taps AI Study Review, the app may send only selected weak topic names to the Supabase AI backend; it does not send full local study history.

Task 49 adds optional local notification reminders. Reminder settings are stored locally, notification permission is requested only when the user enables reminders, and reminders are scheduled on-device with Android local scheduling. No notification data is sent to Supabase, RevenueCat, OpenAI, Google Play, Firebase, or any app backend.

Task 36 adds RevenueCat subscription entitlement support and a billing-ready Premium screen. Billing and subscription purchases are processed by Google Play and RevenueCat. The app checks Premium entitlement status through RevenueCat, and it must continue to work in Free mode if RevenueCat products or Play testing are not ready.

Task 37 documents Google Play subscription mapping for `learnlift_premium_monthly` and `learnlift_premium_yearly`, RevenueCat package mapping for `monthly` and `yearly`/`annual`, and entitlement identifier `premium`. No private payment card data is handled directly by the app. AI backend behavior remains separate and sends limited study context only after explicit user action.

Before enabling paid subscriptions in production, update the Privacy Policy and Google Play Data Safety answers to describe RevenueCat and Google Play billing/subscription processing. Do not claim the app has no third-party services once RevenueCat is enabled.
