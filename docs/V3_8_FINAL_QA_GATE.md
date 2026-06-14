# v3.8 Final QA Gate

Last updated: 2026-06-08

Repository path: `C:\Projects\learnlift-ai-android`

## Release Readiness Audit

| Field | Current status |
| --- | --- |
| Package name | `com.learnliftai.app` |
| App name | `LearnLift AI` |
| Version code | `6` |
| Version name | `0.3.0` |
| Min SDK | `24` |
| Target SDK | `35` |
| Compile SDK | `35` |
| RevenueCat entitlement | `premium` |
| RevenueCat offering | `default` |
| Monthly product | `learnlift_premium_monthly` |
| Yearly product | `learnlift_premium_yearly` |
| Supabase function | `ai-coach` |

## Version Recommendation

Current local version before this prep was:

- `versionCode = 5`
- `versionName = "0.2.1"`

Recommended v3.8 release version:

- `versionCode = 6`, if Play Console confirms version code 6 has not already been uploaded.
- `versionName = "0.3.0"`

`app/build.gradle.kts` was changed to `versionCode = 6` and `versionName = "0.3.0"` for the v3.8 release candidate. Confirm in Play Console before upload; if version code 6 is already used, increment to the next unused value.

## Permissions

Manifest source: `app/src/main/AndroidManifest.xml`.

- `android.permission.INTERNET`: declared for optional AI/Supabase and network billing support.
- `android.permission.POST_NOTIFICATIONS`: declared for local reminders on Android 13+.
- `android.permission.RECEIVE_BOOT_COMPLETED`: declared to restore local reminders after reboot.
- `com.android.vending.BILLING`: expected from RevenueCat/Google Play Billing merged manifest; verify with `:app:processDebugMainManifest`.

## RevenueCat Status

| Check | Status |
| --- | --- |
| Android public SDK key configured locally | Pass locally; value not documented |
| RevenueCat Test Store key configured locally | Pass locally; value not documented |
| `USE_REVENUECAT_TEST_STORE=false` for release | Pass locally |
| Release build cannot use Test Store key | Pass in Gradle guard |
| Release build fails if Android key starts with `test_` | Pass in Gradle guard |
| Entitlement check remains `customerInfo.entitlements["premium"]?.isActive == true` | Pass |
| Offering remains `default` | Pass |
| Play-installed purchase QA | Blocked/manual |
| Restore purchases QA | Blocked/manual |

## Supabase AI Status

| Check | Status |
| --- | --- |
| `ai-coach` function deployed | Manual review required |
| JWT OFF for current no-login app | Manual review required |
| `OPENAI_API_KEY` set in Supabase secrets | Manual review required |
| `OPENAI_MODEL` set | Manual review required |
| `AI_PROXY_MAX_INPUT_CHARS` set | Manual review required |
| `AI_PROXY_ENABLE_DEBUG_LOGS=false` | Manual review required |
| `explain_answer` smoke test | Manual review required |
| `quiz_summary` smoke test | Manual review required |
| `study_plan` smoke test | Manual review required |
| No OpenAI key in Android | Pass by tracked-scope secret scan |

## Store Assets Status

| Asset | Status |
| --- | --- |
| Play listing | Ready from `docs/PLAY_STORE_LISTING_V3_FINAL.md` |
| Release notes | Ready from `docs/PLAY_CONSOLE_RELEASE_NOTES.md` |
| Screenshots | Manual review required |
| Feature graphic | Manual review required |
| Data Safety | Manual review required |

## Known Open Blockers From Backlog

- BUG-011: stale older listing draft exists; do not use it for upload.
- BUG-012: Play-installed purchase flow has not been verified.
- BUG-013: final screenshots and feature graphic are not confirmed ready.
- BUG-016: live Supabase AI success path needs final release smoke testing.
- BUG-030: signed AAB has not been generated/uploaded/verified from Google Play.

## Gate Status Fields

| Gate | Status | Notes |
| --- | --- | --- |
| Build status | Pass | `.\gradlew.bat clean`, `.\gradlew.bat assembleDebug`, and `.\gradlew.bat :app:processDebugMainManifest` passed on 2026-06-08. |
| Content validation status | Pass | `node scripts\validate-study-content.mjs` passed on 2026-06-08. |
| RevenueCat status | Ready for signed AAB; manual Play QA required | Local release config and Gradle guard pass. Play-installed purchase and restore QA remain required after upload. |
| Supabase AI status | Manual review required | Requires deployed-function smoke tests for `explain_answer`, `quiz_summary`, and `study_plan`. |
| Store assets status | Manual review required | Screenshots and feature graphic require final capture/upload. |
| Data Safety status | Manual review required | Confirm Play Console answers against final SDK/vendor behavior. |
| QA checklist status | Manual review required | v3.7 QA is reported complete by task context; final release-candidate checklist still needs owner signoff. |

## Validation Results

Commands run on 2026-06-08:

| Command | Result | Notes |
| --- | --- | --- |
| `.\gradlew.bat clean` | Pass | Build cleanup completed. Existing Gradle/AGP deprecation warnings only. |
| `.\gradlew.bat assembleDebug` | Pass | Debug APK built successfully after version bump. Existing Gradle/AGP deprecation warnings only. |
| `node scripts\validate-study-content.mjs` | Pass | All free and active Premium content files validated. |
| `.\gradlew.bat :app:processDebugMainManifest` | Pass | Manifest processing completed successfully. |

Merged debug manifest permissions verified:

- `android.permission.INTERNET`
- `android.permission.POST_NOTIFICATIONS`
- `android.permission.RECEIVE_BOOT_COMPLETED`
- `com.android.vending.BILLING`
- `android.permission.ACCESS_NETWORK_STATE`, merged from dependencies.

Secret and artifact scan:

- No OpenAI-looking `sk-` values found in tracked-scope scan.
- `OPENAI_API_KEY` matches are documentation/example placeholders only.
- No Supabase service role matches found.
- No RevenueCat private/secret key assignments found.
- `test_` matches are documentation, `.env.example`, debug/Test Store guard/config references, and QA notes; release Gradle config forces the Android public key and blocks `test_` release keys.
- Local RevenueCat config check passed without printing values: `local.properties` exists, Android public key property exists, Test Store key property exists, `USE_REVENUECAT_TEST_STORE=false`, and Android public key does not start with `test_`.
- Ignored local/generated artifacts observed: `local.properties`, `.gradle/`, `app/build/`, and `app/release/app-release.aab`.

`.gitignore` coverage verified for:

- `local.properties`
- `.gradle/`
- `app/build/`
- `*.aab`
- `*.apk`
- `*.jks`
- `*.keystore`

## Final Verdict

Current v3.8 verdict: **Ready for signed AAB**.

The local project is ready for manual signed AAB generation. It is not yet ready for final Play rollout until Play-installed billing verification, Supabase AI smoke tests, store asset confirmation, Data Safety confirmation, and signed AAB upload validation are complete.
