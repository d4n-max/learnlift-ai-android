# AI Usage Limits

Last updated: 2026-05-19

## Purpose

LearnLift AI uses local AI usage limits to reduce accidental OpenAI API costs during early v2 testing. These limits apply only to AI-powered actions. Flashcards, quizzes, daily sessions, progress, local explanations, and rule-based Smart Coach recommendations remain available.

## Current Limits

### Free

- `explain_answer`: 3 requests per local day.
- `quiz_summary`: 1 request per local day.
- `study_plan`: 0 requests per local day.

The 7-day study plan is exposed in the UI, but Free users are directed to Premium for this AI action. A weekly Free study-plan preview can be added later if needed.

Free users can continue using local explanations, rule-based Smart Coach, basic progress, flashcards, quizzes, and daily sessions when this limit is reached.

### Premium

Premium is detected through the RevenueCat entitlement identifier:

```text
premium
```

Premium local safety limits:

- `explain_answer`: 50 requests per local day.
- `quiz_summary`: 20 requests per local day.
- `study_plan`: 10 requests per local day.

These are generous testing limits, not unlimited production quotas.

Premium users see `Premium AI access active` in AI entry points. If the Premium safety limit is reached, the app blocks the AI call locally and keeps local fallback guidance available.

## What Counts

An AI request counts when the app is about to send the request to the Supabase `ai-coach` backend.

This means failed sent requests count toward the daily limit. This is intentional because network failures, Supabase errors, OpenAI errors, and parse failures can still consume app/backend resources or encourage repeated tapping.

Blocked local requests do not call Supabase and do not increment counters.

## Local Persistence

Counters are stored locally with DataStore:

- `dateKey`
- `lastResetDate`
- `aiExplainAnswerCount`
- `aiQuizSummaryCount`
- `aiStudyPlanCount`

Counters reset automatically when the local date changes.

No account login, backend database, analytics, or cloud sync is used for this feature.

## User-Facing Messages

Free limit reached:

```text
You've used today's free AI previews. Upgrade to Premium for more AI help, or continue with local study tools.
```

Premium limit reached:

```text
You've reached today's AI Coach safety limit. Please try again tomorrow.
```

Small usage labels:

- `Free AI previews left today: 2`
- `AI previews reset tomorrow`
- `Premium AI access active`

## Premium CTA

When a Free user reaches an AI limit, the app shows `View Premium`. This opens the existing Premium screen. It does not force a purchase and does not block local study features.

Limit-reached AI UI should avoid showing a repeated retry CTA for locally blocked requests. The local explanation or Smart Coach recommendation remains visible, and the upgrade path is optional.

## Local-Only Limitation

Client-side limits are not a true security boundary. They reduce accidental cost and repeated tapping in early testing, but they can be bypassed by a modified client.

A production abuse-protection plan should add server-side quotas with:

- Supabase Auth or another account/session identifier.
- Server-side request counting.
- Durable quota storage.
- Per-user and per-IP safeguards.
- Backend logging of minimal usage metadata.

## Manual QA Checklist

- Free user can send 3 wrong-answer `explain_answer` requests.
- 4th Free `explain_answer` request is blocked locally.
- Blocked Free request does not hit Supabase.
- Local explanation remains visible after the block.
- `View Premium` opens the Premium screen.
- Free user can send 1 `quiz_summary` request per day when the AI Quiz Review preview is available.
- Free `study_plan` is Premium-gated before any Supabase call and keeps Progress usable.
- Premium entitlement active switches to Premium limits.
- App restart preserves today's usage count.
- Next-day reset is testable by changing device date or clearing app data in a test build.
- Offline AI call falls back safely and counts if the request was started.
- Restore purchases does not clear or corrupt AI usage state.
