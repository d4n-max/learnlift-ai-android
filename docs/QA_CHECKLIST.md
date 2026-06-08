# LearnLift AI QA Checklist

Complete this checklist before generating a signed AAB and uploading LearnLift AI to Google Play.

## QA Scope

- [ ] Full LearnLift AI v3.x manual regression completed
- [ ] Free user flows tested
- [ ] Premium user flows tested
- [ ] RevenueCat purchase and restore flows tested
- [ ] Supabase AI Coach flows tested
- [ ] Build and release checks completed
- [ ] Google Play testing completed

## Test Environment

| Field | Value |
| --- | --- |
| Status | Not started / In progress / Passed / Failed / Blocked |
| App build type | Debug / Release / Closed Testing / Production |
| Build source | Local / CI / Google Play |
| Backend environment | Local Supabase / Staging / Production |
| RevenueCat environment | Test Store / Google Play sandbox / Production |
| Network | Wi-Fi / Mobile data / Offline |
| Date |  |
| Tester |  |
| Notes |  |

## Build / Version

| Field | Value |
| --- | --- |
| applicationId |  |
| versionCode |  |
| versionName |  |
| Git branch |  |
| Git commit |  |
| Signed AAB path |  |
| RevenueCat key source |  |
| USE_REVENUECAT_TEST_STORE |  |
| Notes |  |

## Devices

| Device | Android version | Screen size | Theme | Install source | Tester | Status | Notes |
| --- | --- | --- | --- | --- | --- | --- | --- |
|  |  |  | Light |  |  |  |  |
|  |  |  | Dark |  |  |  |  |
|  |  | Small screen |  |  |  |  |  |
|  | Android 13+ |  |  |  |  |  | Notification permission |

## Critical Blockers Before Release

Release must fail if any of these are found:

- [ ] Crash on launch
- [ ] Wrong RevenueCat key or Test Store key used in release
- [ ] Broken Premium purchase
- [ ] Broken restore purchases
- [ ] AI backend hard crash
- [ ] Empty premium packs
- [ ] App icon, splash icon, or notification icon artifacts
- [ ] `local.properties` or secrets staged
- [ ] `versionCode` not incremented above the previous Play upload

## 1. App Launch And Startup

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Fresh install succeeds | [ ] |  |  |  |  |
| First launch opens app without crash | [ ] |  |  |  |  |
| Splash screen appears correctly | [ ] |  |  |  |  |
| App icon is current LearnLift icon | [ ] |  |  |  |  |
| Notification icon is current LearnLift icon | [ ] |  |  |  |  |
| No crash on cold start | [ ] |  |  |  |  |
| No crash on warm start | [ ] |  |  |  |  |
| Light mode renders correctly, if supported | [ ] |  |  |  |  |
| Dark mode renders correctly, if supported | [ ] |  |  |  |  |

## 2. Onboarding

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| First-run onboarding appears | [ ] |  |  |  |  |
| Choose goal works | [ ] |  |  |  |  |
| Choose daily time works | [ ] |  |  |  |  |
| Recommended path appears | [ ] |  |  |  |  |
| Recommended path can be accepted | [ ] |  |  |  |  |
| Skip onboarding works | [ ] |  |  |  |  |
| Restart onboarding from Settings works | [ ] |  |  |  |  |
| Onboarding does not show again after completion | [ ] |  |  |  |  |
| Onboarding survives app restart while incomplete | [ ] |  |  |  |  |

## 3. Home

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Selected path shown correctly | [ ] |  |  |  |  |
| Daily Session CTA works | [ ] |  |  |  |  |
| Flashcards CTA works | [ ] |  |  |  |  |
| Quiz CTA works | [ ] |  |  |  |  |
| Smart Review entry works | [ ] |  |  |  |  |
| Adaptive Quiz entry works | [ ] |  |  |  |  |
| Premium Study Packs entry works | [ ] |  |  |  |  |
| Premium teaser does not block core usage | [ ] |  |  |  |  |
| Home reloads correctly after app restart | [ ] |  |  |  |  |

## 4. Study Paths

### Free Paths

| Path | Opens Normally | Flashcards | Quiz | Daily Session | Persists | Notes |
| --- | --- | --- | --- | --- | --- | --- |
| English Vocabulary & Speaking Prep | [ ] | [ ] | [ ] | [ ] | [ ] |  |
| Job Interview Prep | [ ] | [ ] | [ ] | [ ] | [ ] |  |
| IT / QA Interview Prep | [ ] | [ ] | [ ] | [ ] | [ ] |  |

### Premium Packs

| Pack | Premium Badge | Free Preview | Premium Full Access | Coming Soon Safe | Notes |
| --- | --- | --- | --- | --- | --- |
| SQL Interview Prep | [ ] | [ ] | [ ] | N/A |  |
| QA Advanced | [ ] | [ ] | [ ] | N/A |  |
| Automation Testing Basics | [ ] | [ ] | [ ] | N/A |  |
| Python Basics | [ ] | N/A | N/A | [ ] |  |
| JavaScript Basics | [ ] | N/A | N/A | [ ] |  |
| Business English | [ ] | N/A | N/A | [ ] |  |
| Technical Interview Prep | [ ] | N/A | N/A | [ ] |  |

### Study Path Regression

- [ ] Free paths open normally
- [ ] Premium packs show Premium badge
- [ ] Preview mode works for free users
- [ ] Premium users get full access
- [ ] Coming soon packs do not crash
- [ ] Coming soon packs do not open empty/broken screens
- [ ] Switching paths works
- [ ] Selected path persists after restart

## 5. Flashcards

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Regular flashcards work | [ ] |  |  |  |  |
| Reveal answer works | [ ] |  |  |  |  |
| Next works | [ ] |  |  |  |  |
| Previous works | [ ] |  |  |  |  |
| Known works | [ ] |  |  |  |  |
| Needs Review works | [ ] |  |  |  |  |
| Long text wraps | [ ] |  |  |  |  |
| No clipped text | [ ] |  |  |  |  |
| Premium preview flashcards are limited correctly | [ ] |  |  |  |  |
| Premium full access works | [ ] |  |  |  |  |
| Empty-state copy is friendly and non-crashing | [ ] |  |  |  |  |

## 6. Smart Review / Spaced Repetition

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Due cards shown | [ ] |  |  |  |  |
| Needs Review cards return quickly | [ ] |  |  |  |  |
| Known cards move forward | [ ] |  |  |  |  |
| No due cards empty state appears | [ ] |  |  |  |  |
| Progress counts update | [ ] |  |  |  |  |
| Reset progress clears review state | [ ] |  |  |  |  |
| Works for free paths | [ ] |  |  |  |  |
| Works for premium packs | [ ] |  |  |  |  |

## 7. Quiz

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Quiz starts for each free path | [ ] |  |  |  |  |
| Quiz starts for premium preview packs | [ ] |  |  |  |  |
| Quiz starts for premium full packs | [ ] |  |  |  |  |
| Answers selectable | [ ] |  |  |  |  |
| Correct state shown | [ ] |  |  |  |  |
| Incorrect state shown | [ ] |  |  |  |  |
| Explanation appears | [ ] |  |  |  |  |
| Quiz summary appears | [ ] |  |  |  |  |
| No duplicate counting from repeated taps | [ ] |  |  |  |  |
| Premium preview quiz is limited correctly | [ ] |  |  |  |  |
| Full premium quiz works | [ ] |  |  |  |  |
| Long options wrap correctly | [ ] |  |  |  |  |

## 8. Adaptive Quiz

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Entry point visible | [ ] |  |  |  |  |
| Works with no weak topics | [ ] |  |  |  |  |
| Works after weak topics are created | [ ] |  |  |  |  |
| Prioritizes weak topics | [ ] |  |  |  |  |
| No duplicate questions in one session | [ ] |  |  |  |  |
| Results update progress | [ ] |  |  |  |  |
| Works for free paths | [ ] |  |  |  |  |
| Works for premium packs | [ ] |  |  |  |  |
| No white rectangle UI artifacts | [ ] |  |  |  |  |

## 9. Topic Weakness Tracking

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Wrong answers create weak topics | [ ] |  |  |  |  |
| Correct answers improve topic status | [ ] |  |  |  |  |
| Progress shows weak topics | [ ] |  |  |  |  |
| Smart Coach uses weak topics | [ ] |  |  |  |  |
| Reset progress clears weak topics | [ ] |  |  |  |  |
| Topic stats persist after restart | [ ] |  |  |  |  |
| Topic stats are scoped to selected path | [ ] |  |  |  |  |

## 10. Progress

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Stats display correctly | [ ] |  |  |  |  |
| Selected path progress shown | [ ] |  |  |  |  |
| Weak topics visible | [ ] |  |  |  |  |
| Smart Review counts visible | [ ] |  |  |  |  |
| AI Study Plan card visible if implemented | [ ] |  |  |  |  |
| Premium teaser does not block basic progress | [ ] |  |  |  |  |
| Reset progress works | [ ] |  |  |  |  |
| No crash with premium packs | [ ] |  |  |  |  |
| Progress persists after restart | [ ] |  |  |  |  |

## 11. AI Coach Explanations

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Wrong answer AI Coach button appears | [ ] |  |  |  |  |
| Loading state appears | [ ] |  |  |  |  |
| Real AI response appears | [ ] |  |  |  |  |
| Local explanation remains available | [ ] |  |  |  |  |
| Offline fallback works | [ ] |  |  |  |  |
| Retry works | [ ] |  |  |  |  |
| No technical errors shown to user | [ ] |  |  |  |  |
| AI usage limit blocks calls locally | [ ] |  |  |  |  |
| No Supabase call after local limit reached | [ ] |  |  |  |  |
| Free and Premium limits differ correctly | [ ] |  |  |  |  |

## 12. AI Quiz Review

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Local quiz summary visible for all users | [ ] |  |  |  |  |
| Free user sees Premium teaser or limited preview | [ ] |  |  |  |  |
| Premium user can generate AI Study Review | [ ] |  |  |  |  |
| Loading state works | [ ] |  |  |  |  |
| AI response card shows summary | [ ] |  |  |  |  |
| AI response card shows recommended focus | [ ] |  |  |  |  |
| AI response card shows next session suggestion | [ ] |  |  |  |  |
| AI response card shows encouragement | [ ] |  |  |  |  |
| Fallback works offline | [ ] |  |  |  |  |
| Retry works | [ ] |  |  |  |  |
| Usage limits apply | [ ] |  |  |  |  |

## 13. AI Study Plan

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Free user sees Premium teaser | [ ] |  |  |  |  |
| Premium user can generate 7-day study plan | [ ] |  |  |  |  |
| Plan shows 7 days | [ ] |  |  |  |  |
| Each day has focus | [ ] |  |  |  |  |
| Each day has tasks | [ ] |  |  |  |  |
| Start Daily Session CTA works if implemented | [ ] |  |  |  |  |
| Fallback works offline | [ ] |  |  |  |  |
| Usage limits apply | [ ] |  |  |  |  |
| No crash if AI backend unavailable | [ ] |  |  |  |  |

## 14. Premium / RevenueCat / Paywall

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Premium screen opens | [ ] |  |  |  |  |
| Monthly plan shown | [ ] |  |  |  |  |
| Yearly plan shown | [ ] |  |  |  |  |
| Yearly marked Best value | [ ] |  |  |  |  |
| Localized RevenueCat prices shown when available | [ ] |  |  |  |  |
| No Test Store products in production build | [ ] |  |  |  |  |
| No `test_` RevenueCat key in release | [ ] |  |  |  |  |
| Purchase opens Google Play purchase sheet | [ ] |  |  |  |  |
| Cancelled purchase handled gracefully | [ ] |  |  |  |  |
| Successful purchase activates Premium entitlement | [ ] |  |  |  |  |
| Restore purchases works | [ ] |  |  |  |  |
| Premium active state shown | [ ] |  |  |  |  |
| Premium active disables duplicate purchase CTA | [ ] |  |  |  |  |
| RevenueCat unavailable does not crash app | [ ] |  |  |  |  |

## 15. Premium Study Packs

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Free user sees Premium pack lock/preview flow | [ ] |  |  |  |  |
| Preview pack works | [ ] |  |  |  |  |
| Preview limit message appears | [ ] |  |  |  |  |
| View Premium opens Premium screen | [ ] |  |  |  |  |
| Premium active user opens full pack | [ ] |  |  |  |  |
| Flashcards work for premium packs | [ ] |  |  |  |  |
| Quiz works for premium packs | [ ] |  |  |  |  |
| Smart Review works for premium packs | [ ] |  |  |  |  |
| Adaptive Quiz works for premium packs | [ ] |  |  |  |  |
| Progress works for premium packs | [ ] |  |  |  |  |
| Coming soon packs do not open empty/broken screens | [ ] |  |  |  |  |

## 16. Local Reminders / Notifications

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Reminders disabled by default | [ ] |  |  |  |  |
| Enabling reminders requests notification permission on Android 13+ | [ ] |  |  |  |  |
| Permission denial handled gracefully | [ ] |  |  |  |  |
| Reminder time can be changed | [ ] |  |  |  |  |
| Notification appears | [ ] |  |  |  |  |
| Notification icon is current LearnLift icon, not old LA icon | [ ] |  |  |  |  |
| Tapping notification opens app | [ ] |  |  |  |  |
| Disabling reminder cancels future reminders | [ ] |  |  |  |  |
| Settings persist after restart | [ ] |  |  |  |  |

## 17. Settings

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Current plan shown correctly | [ ] |  |  |  |  |
| AI access status shown correctly | [ ] |  |  |  |  |
| View Premium works | [ ] |  |  |  |  |
| Restore purchases works | [ ] |  |  |  |  |
| Reset Progress works | [ ] |  |  |  |  |
| Restart onboarding works | [ ] |  |  |  |  |
| Reminder settings work | [ ] |  |  |  |  |
| App info/data notes clear | [ ] |  |  |  |  |

## 18. Content Validation

Run:

```powershell
node scripts\validate-study-content.mjs
```

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Content validation script passes | [ ] |  |  |  |  |
| All path IDs valid | [ ] |  |  |  |  |
| All IDs unique | [ ] |  |  |  |  |
| All quiz `correctAnswerIds` valid | [ ] |  |  |  |  |
| No empty questions/answers | [ ] |  |  |  |  |
| Premium pack counts correct | [ ] |  |  |  |  |
| No duplicate or near-duplicate obvious content | [ ] |  |  |  |  |
| First 5 preview items are high quality | [ ] |  |  |  |  |

## 19. Supabase AI Backend

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| `ai-coach` deployed | [ ] |  |  |  |  |
| Verify JWT OFF for current no-login build | [ ] |  |  |  |  |
| `explain_answer` works | [ ] |  |  |  |  |
| `quiz_summary` works | [ ] |  |  |  |  |
| `study_plan` works | [ ] |  |  |  |  |
| `AI_RESPONSE_PARSE_ERROR` does not happen repeatedly | [ ] |  |  |  |  |
| Backend returns clean JSON | [ ] |  |  |  |  |
| Android never contains OpenAI key | [ ] |  |  |  |  |
| Android never contains Supabase service role key | [ ] |  |  |  |  |

## 20. Data Safety / Privacy

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| No account login | [ ] |  |  |  |  |
| No cloud sync | [ ] |  |  |  |  |
| Progress stored locally | [ ] |  |  |  |  |
| AI calls are user-initiated | [ ] |  |  |  |  |
| Only limited study context sent to AI backend | [ ] |  |  |  |  |
| RevenueCat/Google Play used for purchases | [ ] |  |  |  |  |
| No ads | [ ] |  |  |  |  |
| Reset progress works | [ ] |  |  |  |  |

## 21. UI Polish

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| No white rectangle artifacts | [ ] |  |  |  |  |
| Onboarding cards clean | [ ] |  |  |  |  |
| Adaptive Quiz card clean | [ ] |  |  |  |  |
| Smart Review card clean | [ ] |  |  |  |  |
| Premium screen clean | [ ] |  |  |  |  |
| App icon correct | [ ] |  |  |  |  |
| Splash icon correct | [ ] |  |  |  |  |
| Notification icon correct | [ ] |  |  |  |  |
| Text not clipped | [ ] |  |  |  |  |
| Buttons not hidden by bottom nav | [ ] |  |  |  |  |
| Small screen layout readable | [ ] |  |  |  |  |

## 22. Build / Release Checks

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| Git status clean before release | [ ] |  |  |  |  |
| `local.properties` not committed | [ ] |  |  |  |  |
| No `.jks` / `.keystore` committed | [ ] |  |  |  |  |
| No `.aab` committed | [ ] |  |  |  |  |
| No `app/build` committed | [ ] |  |  |  |  |
| No `.gradle` committed | [ ] |  |  |  |  |
| `versionCode` higher than previous Play upload | [ ] |  |  |  |  |
| `assembleDebug` passes | [ ] |  |  |  |  |
| Signed release AAB generated | [ ] |  |  |  |  |
| Correct RevenueCat Android key used for release | [ ] |  |  |  |  |
| `USE_REVENUECAT_TEST_STORE=false` for release | [ ] |  |  |  |  |
| Upload `app-release.aab`, not `app-debug.aab` | [ ] |  |  |  |  |

## 23. Google Play Testing

| Check | Status | Device | Tester | Date | Notes |
| --- | --- | --- | --- | --- | --- |
| App installed from Closed Testing / Production track | [ ] |  |  |  |  |
| Real paywall prices visible | [ ] |  |  |  |  |
| Google Play purchase sheet appears | [ ] |  |  |  |  |
| Tester account works | [ ] |  |  |  |  |
| License tester account works | [ ] |  |  |  |  |
| Production access ready | [ ] |  |  |  |  |
| Release notes ready | [ ] |  |  |  |  |
| Store listing matches shipped functionality | [ ] |  |  |  |  |

## Tester Notes

| Date | Tester | Area | Status | Notes / Bug Link |
| --- | --- | --- | --- | --- |
|  |  |  |  |  |
|  |  |  |  |  |
|  |  |  |  |  |

## Final QA Verdict

Choose one:

- [ ] Ready for signed AAB
- [ ] Needs fixes
- [ ] Blocked

Final notes:

| Field | Value |
| --- | --- |
| Final status |  |
| Release owner |  |
| QA owner |  |
| Date |  |
| Blocking bugs |  |
| Follow-up bugs |  |
