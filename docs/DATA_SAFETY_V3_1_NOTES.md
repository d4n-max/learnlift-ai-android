# Data Safety Notes v3.1

Last updated: 2026-06-08

## AI Study Review

AI Study Review is optional and user-initiated. When the user taps generate, Android sends only:

- selected study path ID and title
- quiz score percentage
- number correct and wrong
- top wrong topic names
- selected weak topic names
- up to 3 wrong question samples from the completed quiz
- difficulty summary

The app does not send full study history, all local content, payment data, account data, or device contact data.

## AI 7-Day Study Plan

AI Study Plan is optional and user-initiated. When a Premium user taps generate, Android sends only:

- selected study path ID and title
- onboarding goal, if available
- daily study minutes
- top weak topic names, max 5
- due Smart Review count
- recent quiz score summary, if available
- Premium/Free state string only for copy guidance

The app does not send full quiz history, all local content, payment data, account data, or device contact data.

## Local Data

- No account login is required.
- No cloud sync is used.
- Local progress stays on the device.
- Weak topics and flashcard review state stay on the device except compact topic names sent for user-initiated AI actions.
- Local reminders are scheduled on the device.

## Network And AI

- AI requires internet.
- Android calls only the Supabase `ai-coach` backend.
- Android does not contain OpenAI API keys.
- Android does not contain Supabase service role keys.
- OpenAI provider secrets remain in Supabase secrets.

## Monetization And Ads

- Premium purchases are handled by Google Play and RevenueCat.
- No ads are added in v3.1.
- No analytics are added in v3.1.
