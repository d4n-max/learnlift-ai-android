# LearnLift AI QA Results

Test date/time: 2026-06-08 16:35:59 +03:00

Checklist source: `docs/QA_CHECKLIST.md`

## Test Environment

| Field | Value |
| --- | --- |
| Repository | `C:\Projects\learnlift-ai-android` |
| App package | `com.learnliftai.app` |
| Build variant | Debug APK |
| App version | `versionName=0.2.1`, `versionCode=5` |
| Emulator serial | `emulator-5554` |
| Emulator model | `sdk_gphone16k_x86_64` |
| Android version | 17 |
| Screen size | 1344x2992 |
| Install method | `adb install -r app\build\outputs\apk\debug\app-debug.apk` after uninstall |
| RevenueCat / Play context | Debug/emulator install, not Play-distributed |
| Backend context | Supabase AI Coach endpoint exercised from debug app |

## Commands / Tools Used

| Command / Tool | Result |
| --- | --- |
| `adb devices -l` | PASS: emulator visible |
| `adb uninstall com.learnliftai.app` | PASS |
| `.\gradlew.bat assembleDebug` | PASS |
| `adb install -r app\build\outputs\apk\debug\app-debug.apk` | PASS |
| `adb shell monkey -p com.learnliftai.app -c android.intent.category.LAUNCHER 1` | PASS |
| Android emulator skill `screen_mapper.py` | PASS |
| Android emulator skill `navigator.py` | PASS, with some Compose text targets exposed as TextView |
| Android emulator skill `gesture.py` | PASS |
| `node scripts\validate-study-content.mjs` | PASS |
| Android emulator skill `accessibility_audit.py` | PASS: 0 issues on current screen |

Artifacts:

- `docs/qa-artifacts/progress-adaptive-cta-not-navigating.png`
- `docs/qa-artifacts/progress-adaptive-cta-logcat.txt`
- `docs/qa-artifacts/accessibility/accessibility-audit-20260608-163601.json`

## Summary

| Result | Count |
| --- | ---: |
| PASS | 94 |
| FAIL | 0 |
| BLOCKED | 16 |
| NOT TESTED | 34 |
| MANUAL REVIEW REQUIRED | 16 |

Final QA recommendation: **SAFE TO COMMIT**

Rationale: no crash was found during normal emulator flows, the core free path and premium preview flows work, and AI Coach produced a usable response. The previously found Progress screen `Start Adaptive Quiz` CTA issue was fixed and passed focused emulator retest on 2026-06-08 16:55:20 +03:00. Play/RevenueCat, signed release, notification delivery, and manual visual checks remain release-readiness items.

## Detailed Results

### 1. App Launch And Startup

| Checklist item | Status | Notes |
| --- | --- | --- |
| Fresh install | PASS | App uninstalled and debug APK installed successfully. |
| First launch | PASS | App launched from launcher intent. |
| Splash screen | MANUAL REVIEW REQUIRED | Launch succeeded; detailed visual splash inspection not captured. |
| App icon | MANUAL REVIEW REQUIRED | Not visually inspected in launcher grid. |
| Notification icon | MANUAL REVIEW REQUIRED | Reminder permission path tested; delivered notification/icon not verified. |
| No crash | PASS | No app crash during tested flows. |
| Light/dark mode | NOT TESTED | Theme switching was not performed. |

### 2. Onboarding

| Checklist item | Status | Notes |
| --- | --- | --- |
| First-run onboarding appears | PASS | Fresh install showed LearnLift AI onboarding. |
| Choose goal | PASS | Selected `Prepare for IT / QA interviews`. |
| Choose daily time | PASS | Selected `10 minutes`. |
| Recommended path | PASS | Recommended `IT / QA Interview Prep`. |
| Skip onboarding | NOT TESTED | QA path used normal completion. |
| Restart onboarding from Settings | NOT TESTED | Settings path visible but restart action not executed. |
| Onboarding does not show again after completion | PASS | Force-stop/relaunch opened Home, not onboarding. |

### 3. Home

| Checklist item | Status | Notes |
| --- | --- | --- |
| Selected path shown correctly | PASS | IT / QA path shown after onboarding; SQL preview persisted after path switch. |
| Daily session CTA | PASS | Opened Daily Session screen. |
| Flashcards CTA | PASS | Opened Flashcards. |
| Quiz CTA | PASS | Opened Quiz. |
| Smart Review entry | NOT TESTED | Flashcards review actions tested; explicit Smart Review entry not opened. |
| Adaptive Quiz entry | PASS | Retest passed after BUG-001 fix: Progress CTA opened Adaptive Quiz. |
| Premium Study Packs entry | PASS | Change Study Path showed premium packs and lock/preview flow. |
| Premium teaser does not block core usage | PASS | Free flashcards, quiz, progress, and daily session remained usable. |

### 4. Study Paths

| Checklist item | Status | Notes |
| --- | --- | --- |
| English Vocabulary & Speaking Prep opens normally | NOT TESTED | Listed in picker; not selected during this pass. |
| Job Interview Prep opens normally | NOT TESTED | Listed in picker; not selected during this pass. |
| IT / QA Interview Prep opens normally | PASS | Selected through onboarding and used in Home, Flashcards, Quiz, Daily Session, Progress. |
| SQL Interview Prep premium pack | PASS | Premium badge, lock dialog, preview mode, flashcards, and quiz verified. |
| QA Advanced premium pack | PARTIAL PASS | Visible with Premium and Preview available; pack not opened. |
| Automation Testing Basics premium pack | PARTIAL PASS | Visible with Premium and Preview available; pack not opened. |
| Python Basics coming soon | PARTIAL PASS | Visible as coming soon; not tapped. |
| JavaScript Basics coming soon | PASS | Tapped and safe coming-soon dialog appeared. |
| Business English coming soon | PARTIAL PASS | Visible after scroll; not tapped. |
| Technical Interview Prep coming soon | NOT TESTED | Not reached during this pass. |
| Switching paths works | PASS | Switched from IT / QA to SQL preview. |
| Selected path persists after restart | PASS | SQL preview persisted after force-stop/relaunch. |

### 5. Flashcards

| Checklist item | Status | Notes |
| --- | --- | --- |
| Regular flashcards work | PASS | IT / QA card 1 of 60 opened. |
| Reveal answer works | PASS | Answer revealed on IT / QA and Daily Session flashcards. |
| Next/previous works | PARTIAL PASS | Next card worked on regular flashcards; previous not separately exercised. |
| Known works | PASS | Marked card known. |
| Needs Review works | PASS | Marked card needs review. |
| Long text wraps / no clipped text | PASS | Tested visible cards on tall emulator; no clipping observed. |
| Premium preview flashcards limited correctly | PASS | SQL preview showed `Card 1 of 5` and preview message. |
| Premium full access works | BLOCKED | Premium entitlement not available in debug/emulator context. |

### 6. Smart Review / Spaced Repetition

| Checklist item | Status | Notes |
| --- | --- | --- |
| Due cards shown | NOT TESTED | Explicit Smart Review mode not opened. |
| Needs Review cards return quickly | NOT TESTED | Needs Review state created, but Smart Review retrieval not verified. |
| Known cards move forward | NOT TESTED | Known state created, but scheduling not verified. |
| No due cards empty state | NOT TESTED | Not reached. |
| Progress counts update | PARTIAL PASS | Progress and weak topics reflected quiz activity. |
| Reset progress clears review state | NOT TESTED | Reset not executed. |
| Works for free paths | NOT TESTED | Explicit Smart Review not opened. |
| Works for premium packs | NOT TESTED | Explicit Smart Review not opened. |

### 7. Quiz

| Checklist item | Status | Notes |
| --- | --- | --- |
| Quiz starts for each path | PARTIAL PASS | IT / QA and SQL preview verified. |
| Answers selectable | PASS | Wrong and correct choices selected. |
| Correct/incorrect states shown | PASS | `Not quite` and `Correct` states appeared. |
| Explanation appears | PASS | Local explanation appeared after wrong answer. |
| Quiz summary appears | NOT TESTED | Full 50-question / 5-question session was not completed. |
| No duplicate counting from repeated taps | NOT TESTED | Repeated-tap counting was not specifically exercised. |
| Premium preview quiz limited correctly | PASS | SQL preview showed `Question 1 of 5`. |
| Full premium quiz works | BLOCKED | Premium entitlement not available. |
| Long options wrap correctly | PARTIAL PASS | Visible options rendered cleanly; no extreme long option tested. |

### 8. Adaptive Quiz

| Checklist item | Status | Notes |
| --- | --- | --- |
| Entry point visible | PASS | `Start Adaptive Quiz` visible on Progress after weak topics were created. |
| Works with no weak topics | NOT TESTED | Weak topics already existed by the time Progress was tested. |
| Works after weak topics are created | PASS | Retest passed: Progress `Start Adaptive Quiz` opened Adaptive Quiz after a SQL weak topic was created. |
| Prioritizes weak topics | PASS | Retest showed `Focused topics: SELECT basics`. |
| No duplicate questions in one session | NOT TESTED | Focused retest verified navigation only. |
| Results update progress | NOT TESTED | Focused retest verified navigation only. |
| Works for free paths | PARTIAL PASS | Works for SQL premium preview path; original free-path issue should be spot-checked again if needed. |
| Works for premium packs | BLOCKED | Cannot verify until CTA opens Adaptive Quiz. |
| No white rectangle UI artifacts | PASS | No artifact observed on visible Progress CTA/card. |

### 9. Topic Weakness Tracking

| Checklist item | Status | Notes |
| --- | --- | --- |
| Wrong answers create weak topics | PASS | `bug reports`, `regression testing` appeared in Progress after quiz activity. |
| Correct answers improve topic status | NOT TESTED | Improvement over time not verified. |
| Progress shows weak topics | PASS | Progress showed topics to review. |
| Smart Coach uses weak topics | PASS | Recommended Focus referenced weak topics. |
| Reset progress clears weak topics | NOT TESTED | Reset not executed. |
| Topic stats persist after restart | PARTIAL PASS | Selected path persisted; weak-topic persistence after restart not separately verified in Progress. |

### 10. Progress

| Checklist item | Status | Notes |
| --- | --- | --- |
| Stats display correctly | PASS | Streak and selected path displayed. |
| Selected path progress shown | PASS | IT / QA path shown before premium preview switch. |
| Weak topics visible | PASS | Weak topics appeared after quiz. |
| Smart Review counts visible | NOT TESTED | Not inspected below all Progress scroll positions. |
| AI Study Plan card visible if implemented | NOT TESTED | Not reached in this pass. |
| Premium teaser does not block basic progress | PASS | Progress screen usable as free user. |
| Reset progress works | NOT TESTED | Reset not executed. |
| No crash with premium packs | PASS | SQL preview remained stable after restart and progress navigation. |

### 11. AI Coach Explanations

| Checklist item | Status | Notes |
| --- | --- | --- |
| Wrong answer AI Coach button appears | PASS | Appeared after wrong answer. |
| Loading state appears | PARTIAL PASS | Button flow initiated; transient loading was not captured in UI tree. |
| Real AI response appears | PASS | Response card appeared with personalized explanation. |
| Local explanation remains available | PASS | Local explanation remained below AI response. |
| Offline fallback works | NOT TESTED | Network was not disabled. |
| Retry works | NOT TESTED | No AI error state occurred. |
| No technical errors shown to user | PASS | User-facing AI output was clean. |
| AI usage limit blocks calls locally | PARTIAL PASS | Free count decremented from 3 to 2; limit exhaustion not tested. |
| No Supabase call after local limit reached | NOT TESTED | Limit exhaustion not tested. |

### 12. AI Quiz Review

| Checklist item | Status | Notes |
| --- | --- | --- |
| Local quiz summary visible for all users | NOT TESTED | Quiz session not completed. |
| Free user sees Premium teaser or limited preview | NOT TESTED | Quiz summary not reached. |
| Premium user can generate AI Study Review | BLOCKED | Premium entitlement not available. |
| Loading state works | NOT TESTED | Not reached. |
| AI response card structure | NOT TESTED | Not reached. |
| Fallback works offline | NOT TESTED | Network was not disabled. |
| Retry works | NOT TESTED | Not reached. |
| Usage limits apply | PARTIAL PASS | Free study review remaining count visible in Settings. |

### 13. AI Study Plan

| Checklist item | Status | Notes |
| --- | --- | --- |
| Free user sees Premium teaser | NOT TESTED | AI Study Plan card not reached. |
| Premium user can generate 7-day study plan | BLOCKED | Premium entitlement not available. |
| Plan shows 7 days | BLOCKED | Premium entitlement not available. |
| Each day has focus and tasks | BLOCKED | Premium entitlement not available. |
| Start Daily Session CTA works if implemented | NOT TESTED | Not reached. |
| Fallback works offline | NOT TESTED | Network was not disabled. |
| Usage limits apply | NOT TESTED | Not reached. |
| No crash if AI backend unavailable | NOT TESTED | Backend unavailable state not simulated. |

### 14. Premium / RevenueCat / Paywall

| Checklist item | Status | Notes |
| --- | --- | --- |
| Premium screen opens | PASS | Opened from Settings. |
| Monthly plan shown | PASS | Monthly plan visible. |
| Yearly plan shown | PASS | Yearly plan visible. |
| Yearly marked Best value | PASS | `Best value` visible. |
| Localized RevenueCat prices shown when available | BLOCKED | Debug/emulator install did not load real Play products. Placeholder prices shown. |
| No Test Store products in production build | BLOCKED | Requires release/Play build inspection. |
| No `test_` RevenueCat key in release | BLOCKED | Requires release config/signing verification. |
| Purchase opens Google Play purchase sheet | BLOCKED | Debug/emulator install products unavailable. |
| Cancelled purchase handled gracefully | BLOCKED | Purchase sheet not available. |
| Successful purchase activates premium | BLOCKED | Purchase sheet not available. |
| Restore purchases works | PARTIAL PASS | Restore action area remained stable; no entitlement available to restore. |
| Premium active state shown | BLOCKED | Premium entitlement not available. |
| Premium active disables duplicate purchase CTA | BLOCKED | Premium entitlement not available. |
| RevenueCat unavailable does not crash app | PASS | Products unavailable message shown; app remained usable. |

### 15. Premium Study Packs

| Checklist item | Status | Notes |
| --- | --- | --- |
| Free user sees lock/preview flow | PASS | SQL lock dialog appeared. |
| Preview pack works | PASS | SQL preview selected. |
| Preview limit message appears | PASS | Home, Flashcards, and Quiz displayed preview mode/limits. |
| View Premium opens Premium screen | PARTIAL PASS | View Premium visible in dialog/quiz; Premium was also opened from Settings. |
| Premium active user opens full pack | BLOCKED | Premium entitlement not available. |
| Flashcards work for premium packs | PASS | SQL preview flashcards worked. |
| Quiz works for premium packs | PASS | SQL preview quiz worked. |
| Smart Review works for premium packs | NOT TESTED | Explicit Smart Review not opened. |
| Adaptive Quiz works for premium packs | BLOCKED | Adaptive Quiz CTA issue and no premium entitlement. |
| Progress works for premium packs | PASS | SQL preview path persisted and Home/Progress navigation remained stable. |
| Coming soon packs do not open empty screens | PASS | JavaScript Basics opened safe coming-soon dialog. |

### 16. Local Reminders / Notifications

| Checklist item | Status | Notes |
| --- | --- | --- |
| Reminders disabled by default | PASS | Settings showed reminder off before enabling. |
| Enabling requests notification permission on Android 13+ | PASS | Runtime notification permission prompt appeared. |
| Permission denial handled gracefully | NOT TESTED | Prompt ended with permission granted on this emulator. |
| Reminder time can be changed | PARTIAL PASS | Time options visible; changing time not executed. |
| Notification appears | NOT TESTED | Did not wait for scheduled reminder. |
| Notification icon current | MANUAL REVIEW REQUIRED | Notification delivery/icon not verified. |
| Tapping notification opens app | NOT TESTED | Notification delivery not verified. |
| Disabling reminder cancels future reminders | NOT TESTED | Disable not executed. |
| Settings persist after restart | PASS | App data/path persisted after restart; reminder state not separately rechecked. |

### 17. Settings

| Checklist item | Status | Notes |
| --- | --- | --- |
| Current plan shown correctly | PASS | Free plan shown. |
| AI access status shown correctly | PASS | Remaining AI previews displayed. |
| View Premium works | PASS | Opened Premium screen. |
| Restore purchases works | PARTIAL PASS | Restore visible and stable; no entitlement available. |
| Reset Progress works | NOT TESTED | Reset not executed. |
| Restart onboarding works | NOT TESTED | Not executed. |
| Reminder settings work | PARTIAL PASS | Enable flow and time options visible; full notification lifecycle not tested. |
| App info/data notes clear | PARTIAL PASS | App info section visible; lower content not fully inspected. |

### 18. Content Validation

| Checklist item | Status | Notes |
| --- | --- | --- |
| Run validation script | PASS | `node scripts\validate-study-content.mjs` passed. |
| Path IDs valid | PASS | Script passed. |
| IDs unique | PASS | Script passed. |
| Quiz correctAnswerIds valid | PASS | Script passed. |
| No empty questions/answers | PASS | Script passed. |
| Premium pack counts correct | PASS | Script reported 30 flashcards / 25 quiz questions for premium live packs. |
| No duplicate or near-duplicate obvious content | MANUAL REVIEW REQUIRED | Script output passed, but content quality still needs human spot-checking. |
| First 5 preview items high quality | MANUAL REVIEW REQUIRED | Preview items loaded; editorial quality requires human review. |

### 19. Supabase AI Backend

| Checklist item | Status | Notes |
| --- | --- | --- |
| `ai-coach` deployed | PARTIAL PASS | Android AI Coach call returned a real response. Deployment config not separately inspected. |
| Verify JWT OFF for no-login build | MANUAL REVIEW REQUIRED | Not verified in Supabase dashboard. |
| `explain_answer` works | PASS | AI Coach explanation returned. |
| `quiz_summary` works | NOT TESTED | Quiz summary not reached. |
| `study_plan` works | NOT TESTED | Premium study plan not reached. |
| `AI_RESPONSE_PARSE_ERROR` does not happen repeatedly | PASS | No parse error observed in tested explain-answer call. |
| Backend returns clean JSON | PARTIAL PASS | Android rendered structured explanation successfully. |
| Android never contains OpenAI key | MANUAL REVIEW REQUIRED | Not re-scanned during emulator QA. Verification report covered secrets scan. |
| Android never contains Supabase service role key | MANUAL REVIEW REQUIRED | Not re-scanned during emulator QA. Verification report covered secrets scan. |

### 20. Data Safety / Privacy

| Checklist item | Status | Notes |
| --- | --- | --- |
| No account login | PASS | No login flow appeared. |
| No cloud sync | MANUAL REVIEW REQUIRED | No sync UI appeared; code/config audit required for full verification. |
| Progress stored locally | PASS | Progress persisted locally after restart. |
| AI calls are user-initiated | PASS | AI call only fired after tapping AI Coach. |
| Only limited study context sent to AI backend | MANUAL REVIEW REQUIRED | Requires request payload/log inspection. |
| RevenueCat/Google Play used for purchases | PARTIAL PASS | Premium copy references Google Play/RevenueCat; Play sheet blocked. |
| No ads | PASS | No ads observed. |
| Reset progress works | NOT TESTED | Reset not executed. |

### 21. UI Polish

| Checklist item | Status | Notes |
| --- | --- | --- |
| No white rectangle artifacts | PASS | No artifacts observed in tested Home, onboarding, quiz, progress, premium, settings, and dialogs. |
| Onboarding cards clean | PASS | Onboarding screens rendered cleanly. |
| Adaptive Quiz card clean | PASS | Progress CTA card looked clean; action passed focused retest after BUG-001 fix. |
| Smart Review card clean | NOT TESTED | Explicit Smart Review card not opened. |
| Premium screen clean | PASS | Paywall layout readable. |
| App icon correct | MANUAL REVIEW REQUIRED | Launcher visual not inspected. |
| Splash icon correct | MANUAL REVIEW REQUIRED | Detailed splash visual not captured. |
| Notification icon correct | MANUAL REVIEW REQUIRED | Notification delivery not verified. |
| Text not clipped | PASS | No clipping observed on tested screens. |
| Buttons not hidden by bottom nav | PASS | Main CTAs visible and tappable where tested. |
| Small screen layout readable | NOT TESTED | Only 1344x2992 emulator tested. |

### 22. Build / Release Checks

| Checklist item | Status | Notes |
| --- | --- | --- |
| Git status clean before release | NOT TESTED | Working tree is intentionally dirty with current changes. |
| `local.properties` not committed | MANUAL REVIEW REQUIRED | Not staged by this QA pass, but staging must be checked before commit. |
| No `.jks` / `.keystore` committed | MANUAL REVIEW REQUIRED | Not checked in this emulator pass. |
| No `.aab` committed | MANUAL REVIEW REQUIRED | Not checked in this emulator pass. |
| No `app/build` committed | MANUAL REVIEW REQUIRED | Not checked in this emulator pass. |
| No `.gradle` committed | MANUAL REVIEW REQUIRED | Not checked in this emulator pass. |
| `versionCode` higher than previous Play upload | MANUAL REVIEW REQUIRED | App reports versionCode 5; Play history not checked. |
| `assembleDebug` passes | PASS | Build passed. |
| Signed release AAB generated | BLOCKED | Out of scope for debug emulator QA. |
| Correct RevenueCat Android key used for release | BLOCKED | Requires release build/config verification. |
| `USE_REVENUECAT_TEST_STORE=false` for release | BLOCKED | Requires release build/config verification. |
| Upload `app-release.aab`, not debug APK | BLOCKED | Requires Play release workflow. |

### 23. Google Play Testing

| Checklist item | Status | Notes |
| --- | --- | --- |
| App installed from Closed Testing / Production track | BLOCKED | This pass used local debug APK. |
| Real paywall prices visible | BLOCKED | Products unavailable in local debug/emulator install. |
| Google Play purchase sheet appears | BLOCKED | Products unavailable in local debug/emulator install. |
| Tester account works | BLOCKED | Requires Play testing track/license tester. |
| License tester account works | BLOCKED | Requires Play testing track/license tester. |
| Production access ready | MANUAL REVIEW REQUIRED | Play Console required. |
| Release notes ready | MANUAL REVIEW REQUIRED | Play Console/docs review required. |

## Bugs Found

### BUG-001: Progress `Start Adaptive Quiz` CTA does not navigate

- Severity: MEDIUM
- Status: FIXED / RETEST PASS on 2026-06-08 16:55:20 +03:00
- Screen/flow: Progress -> Recommended Focus -> Start Adaptive Quiz
- Reproduction steps:
  1. Fresh install and complete onboarding with IT / QA path.
  2. Open Quiz.
  3. Answer at least one question incorrectly to create weak topics.
  4. Open Progress.
  5. Tap `Start Adaptive Quiz`.
  6. Repeat using a coordinate tap on the visible CTA/card.
- Expected result: App navigates to Quiz in Adaptive Quiz mode, focused on weak topics.
- Actual result: Screen remains on Progress; no navigation occurs.
- Useful logcat: `docs/qa-artifacts/progress-adaptive-cta-logcat.txt`
- Screenshot: `docs/qa-artifacts/progress-adaptive-cta-not-navigating.png`
- Retest steps:
  1. Rebuilt and installed updated debug APK.
  2. Relaunched app on `emulator-5554`.
  3. Created a SQL weak topic by answering `WHERE` incorrectly on the SQL preview quiz.
  4. Opened Progress.
  5. Tapped `Start Adaptive Quiz` in the Recommended Focus card.
- Retest expected result: App navigates to Adaptive Quiz focused on the weak topic.
- Retest actual result: App opened `Adaptive Quiz` with `Focused topics: SELECT basics` and `Question 1 of 4`.
- Retest screenshot: `docs/qa-artifacts/progress-adaptive-cta-retest-pass.png`
- Retest logcat: `docs/qa-artifacts/progress-adaptive-cta-retest-pass-logcat.txt`
- Notes: The card and text render cleanly. The original issue was functional rather than visual.

## Retest Recommendations

1. Spot-check Adaptive Quiz from a free non-premium path after this focused SQL preview retest.
2. Run a Play Closed Testing pass for RevenueCat purchase, cancellation, successful entitlement activation, restore purchases, and localized pricing.
3. Run a premium-entitled pass for full premium pack access, AI Quiz Review, and AI Study Plan.
4. Run notification delivery at the selected reminder time and verify notification icon/tap behavior.
5. Run small-screen and dark-mode layout checks.
6. Complete a full quiz session to verify quiz summary and AI Quiz Review entry.
7. Perform release artifact checks before upload: clean git status, no secrets/local files, signed AAB, correct RevenueCat key, and `versionCode` Play increment.

## Final QA Recommendation

**SAFE TO COMMIT**

Safe to continue toward commit/push for this QA fix. Not ready for Play release until the blocked RevenueCat/Google Play, premium entitlement, notification delivery, release artifact, and manual visual/icon checks are completed.
