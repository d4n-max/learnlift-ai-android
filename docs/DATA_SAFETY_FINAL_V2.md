# Data Safety Final Draft v2

Last updated: 2026-05-22

This is a Play Console preparation checklist, not legal advice. Verify all answers against the final build, Privacy Policy, SDK behavior, RevenueCat configuration, Google Play Billing behavior, and Supabase backend behavior before submission.

## Current App Summary

LearnLift AI is local-first. The app does not require account login, does not include ads, and does not use cloud sync in the current v2 build.

Normal study progress is stored locally on the device through DataStore. Optional AI Coach actions contact the Supabase backend only when the user taps an AI action. Premium purchases and entitlement checks use Google Play and RevenueCat.

## Personal Info

Current status:

- No account login.
- No email collection in the Android app.
- No name, phone number, address, or profile collection.

Play Console draft:

- Personal info collected: likely `No`, unless RevenueCat/Google Play SDK classification requires purchase-related account metadata disclosure.

Verify before submission:

- RevenueCat SDK data disclosures.
- Google Play purchase flow disclosures.
- Privacy Policy language.

## App Activity

Current status:

- Study progress, selected path, flashcard status, quiz stats, topic weakness, AI usage counters, onboarding preferences, and reminder settings are stored locally.
- The app does not intentionally send normal study progress to a backend.
- AI Coach sends limited study/quiz context only after explicit user action.

Play Console draft:

- App activity collected by the app backend: `No` for normal progress.
- User-generated study context sent for AI: disclose if Play Console considers this app activity or user-provided content sent to a third party.

Verify before submission:

- Whether optional AI request payloads should be classified under App activity, App interactions, or Other user-generated content.

## App Info And Performance

Current status:

- No analytics or crash reporting SDK is intentionally included.
- No Firebase Crashlytics.

Play Console draft:

- App info and performance collected: `No`, unless Google Play/RevenueCat SDK diagnostics require disclosure.

Verify before submission:

- RevenueCat SDK data safety guidance.
- Google Play Billing data handling.

## Financial Info

Current status:

- Premium purchases are processed by Google Play.
- RevenueCat manages subscription entitlement status.
- The app does not directly process payment card numbers or private payment data.

Play Console draft:

- Financial info/payment info: handled by Google Play and RevenueCat.
- App does not directly collect payment card data.

Verify before submission:

- RevenueCat recommended Data Safety answers.
- Google Play Billing purchase disclosure requirements.

## Notifications

Current status:

- Local daily study reminders are optional.
- Reminders are scheduled on the device.
- No Firebase Cloud Messaging.
- No backend push notification server.
- Reminder preferences are stored locally.

Play Console draft:

- Notifications: local only.
- No notification data sent to backend.

## AI Coach

Current status:

- Android never calls OpenAI directly.
- Android contains no OpenAI API key.
- Android sends optional, user-initiated AI requests to Supabase Edge Function `ai-coach`.
- Supabase calls OpenAI server-side.
- Requests may include current quiz question, selected answer, correct answer, static explanation, score, missed topics, weak topic names, selected study path, and a short study goal.
- The app should not send personal profile data, email, resumes, private notes, or sensitive personal data.

Third parties:

- Supabase for backend proxy.
- OpenAI behind the Supabase backend.

Verify before submission:

- Supabase logging settings.
- OpenAI retention/data-use policy for selected API configuration.
- Privacy Policy accurately describes AI context transfer.

## Third Parties

Current v2 third parties:

- Supabase: AI backend proxy.
- OpenAI: AI responses behind backend.
- RevenueCat: subscription entitlement and purchase state.
- Google Play Billing: subscription checkout.

Not included:

- Ads.
- Firebase Cloud Messaging.
- Firebase Auth.
- Firebase Analytics.
- Cloud sync account backend.

## Data Sharing

Draft statement:

- No sale of user data.
- No ad network data sharing.
- Limited AI context may be sent to Supabase/OpenAI only after user action.
- Billing/subscription data is processed by Google Play and RevenueCat for purchase and entitlement functionality.

Verify before submission:

- Exact Play Console distinction between collected and shared data for RevenueCat and AI backend.

## Task 51 Verification Notes

- RevenueCat and Google Play Billing are used for Premium purchases and entitlement checks.
- Supabase and OpenAI are used only for optional AI Coach requests.
- AI requests are user-initiated.
- No account login is included.
- No cloud sync is included.
- Study progress is stored locally on device.
- Local daily reminders are device-local and optional.
- No ads are included.
- No sale of user data is planned or documented.
- Before submission, verify RevenueCat SDK, Google Play Billing, Supabase, and OpenAI disclosures directly in Play Console and the Privacy Policy.

## User Controls

- Users can reset progress in the app.
- No account deletion flow is needed because no account exists.
- Reminder settings can be disabled in Settings.
- Android notification permission can be managed through system settings.

## Data Safety Submission Blockers

- Privacy Policy must be updated before production.
- RevenueCat Data Safety guidance must be checked.
- Supabase/OpenAI backend logging behavior must be verified.
- Google Play subscriptions must be configured and tested.
- Final Play Console answers must match the exact release build and active SDKs.
