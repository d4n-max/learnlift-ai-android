# Data Safety v3.6 Review

Last updated: 2026-06-08

This is a release-prep review for Google Play Data Safety. It is not legal advice. Confirm final Play Console answers against the exact release build, Privacy Policy, RevenueCat SDK behavior, Google Play Billing behavior, Supabase backend settings, and OpenAI configuration.

## Current Data Claims

| Claim | Current status | Notes |
| --- | --- | --- |
| No account login | Confirmed by app scope | No auth or account creation is included. |
| No cloud sync | Confirmed by app scope | Progress uses local DataStore; no account sync backend. |
| Progress stored locally | Confirmed by app docs/code behavior | Selected path, progress, weak topics, reminders, and AI usage counters are local. |
| AI calls are user-initiated | Confirmed by UX/docs | AI calls occur after tapping AI Coach, AI Quiz Review, or AI Study Plan actions. |
| AI calls send limited context | Confirmed by AI docs | Payloads contain compact quiz/study context, not full history or payment data. |
| Supabase backend handles AI proxy | Confirmed by docs/code | Android calls Supabase `ai-coach`; Android does not call OpenAI directly. |
| OpenAI used behind Supabase | Confirmed by backend docs | `OPENAI_API_KEY` belongs in Supabase secrets only. |
| RevenueCat and Google Play Billing used for purchases | Confirmed by docs/dependency | RevenueCat manages products, purchase state, restore, and entitlement. Google Play handles checkout. |
| Local reminders are device-only | Confirmed by docs | No Firebase Cloud Messaging or push backend. |
| No ads | Confirmed by docs/code scope | No ad SDK is intentionally included. |
| No sale of user data | Current product claim | Must be reflected in Privacy Policy and Play Console. |

## AI Data Transfer

AI actions may send limited context to the Supabase AI backend:

- Wrong-answer explanation: current question, selected answer, correct answer, static explanation, topic, difficulty, and study path context.
- AI Quiz Review: score, number correct/wrong, wrong topics, weak topic names, up to 3 wrong question samples, and difficulty summary.
- 7-Day Study Plan: selected path, onboarding goal if available, daily study minutes, weak topic names, due review count, and recent quiz summary.

Android must not send:

- OpenAI API keys.
- Supabase service role keys.
- RevenueCat private keys.
- Payment card details.
- Account login data.
- Full local study history.
- Private user notes or resumes.

## Third Parties

- Supabase: backend proxy for optional AI requests.
- OpenAI: AI provider behind Supabase.
- RevenueCat: subscription entitlement and purchase-state management.
- Google Play Billing: subscription checkout and purchase handling.

Not included:

- Ads.
- Analytics.
- Firebase Auth.
- Firebase Cloud Messaging.
- Cloud sync account backend.

## Play Console Items To Confirm Before Upload

- [ ] RevenueCat SDK Data Safety guidance.
- [ ] Google Play Billing purchase disclosure requirements.
- [ ] Whether optional AI request payloads are classified as App activity, App interactions, or User-provided content.
- [ ] Whether RevenueCat purchase identifiers or diagnostics count as collected/shared data.
- [ ] Supabase logging behavior for Edge Function requests.
- [ ] OpenAI retention/data-use policy for the selected API configuration.
- [ ] Privacy Policy text matches optional AI and purchase flows.
- [ ] Data deletion language explains local reset and no account deletion requirement.
- [ ] Encryption in transit is enabled for Supabase, RevenueCat, and Google Play interactions.

## Recommended Play Console Positioning

- Account creation: not required.
- Ads: no.
- Cloud sync: no.
- Local progress: stored on device.
- AI: optional, user-initiated, sends limited study context to backend.
- Purchases: handled through Google Play and RevenueCat.
- Data sale: no.

## Release Verdict

Data Safety status: **Manual review required before Play upload**.

The current product claims are consistent with project docs, but final Play Console answers must be confirmed against SDK vendor guidance and the final Privacy Policy.
