# Play Console Internal Testing Setup

## 1. Overview

- App name: LearnLift AI
- Package name: `com.learnliftai.app`
- Version: `0.1.0` MVP
- Release type: Google Play Internal Testing
- Artifact: `app-release.aab`

Current app behavior:

- Uses local JSON content.
- Stores progress locally with DataStore.
- Does not require login.
- Does not use a backend.
- Does not include ads.
- Does not include payments.
- Does not include live AI Coach yet.

This guide is for creating the app in Google Play Console, completing the required setup sections, uploading the first AAB, and creating the first internal testing release. It does not publish the app to production.

## 2. Pre-Upload Checklist

- [ ] QA status checked in `docs/QA_INTERNAL_TEST_REPORT.md`.
- [ ] `versionName` checked: `0.1.0`.
- [ ] `versionCode` checked: `1`.
- [ ] Package name checked: `com.learnliftai.app`.
- [ ] App icon checked.
- [ ] README updated.
- [ ] Release notes ready in `docs/RELEASE_NOTES.md`.
- [ ] Data Safety draft ready in `docs/DATA_SAFETY_DRAFT.md`.
- [ ] Privacy policy URL ready: `PRIVACY_POLICY_URL_HERE`.
- [ ] AAB generated locally.
- [ ] AAB not committed to Git.
- [ ] Keystore/signing files not committed to Git.

## 3. Generate AAB

Use PowerShell from the project root.

Clean the project:

```powershell
.\gradlew.bat clean
```

Generate the release AAB:

```powershell
.\gradlew.bat bundleRelease
```

Expected output:

```text
app\build\outputs\bundle\release\app-release.aab
```

Use this AAB for Play Console upload.

Continue using the debug commands for local device or emulator testing:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

Do not commit generated build artifacts, including `.aab` or `.apk` files.

## 4. Create App In Google Play Console

1. Open Google Play Console.
2. Select **Create app**.
3. Enter app name: `LearnLift AI`.
4. Choose the default language.
5. Choose whether this is an app or game: select **App**.
6. Choose free or paid: select the intended pricing for the MVP, likely **Free** unless monetization is added later.
7. Accept the required declarations after reviewing them carefully.
8. Create the app.

## 5. Main Store Listing

Use `docs/PLAY_STORE_LISTING_DRAFT.md` as the source draft.

Copy or prepare:

- App name: `LearnLift AI`
- Short description
- Full description
- App icon
- Feature graphic placeholder
- Phone screenshots

Screenshots needed:

- Home Dashboard
- Study Path Selection
- Flashcards
- Quiz question and explanation
- Daily Study Session
- Progress Screen
- Settings Screen

Avoid misleading claims:

- Do not claim live AI Coach is available yet.
- Do not claim account login, cloud sync, certificates, payments, or guaranteed results.
- Do not imply progress is synced across devices.
- Do not imply progress is per study path in the current MVP.

## 6. App Content

The developer will likely need to complete these Play Console sections:

- Privacy Policy
- App access
- Ads
- Content rating
- Target audience and content
- Data Safety

Current expected answers for this MVP:

- No login required.
- No ads currently.
- No in-app purchases currently.
- No user-generated content.
- Educational app.
- Local-only progress.
- No personal data intentionally collected externally.
- Data Safety must match actual code behavior.

Review each Play Console question carefully. If the app changes later, update the answers before uploading a new build.

## 7. Privacy Policy

Privacy policy URL placeholder:

```text
PRIVACY_POLICY_URL_HERE
```

Google Play Data Safety requires accurate disclosure. Google documentation says developers should ensure they have added a privacy policy when completing the Data Safety form, and Google Play policy requires a privacy policy link for apps on Google Play.

Recommendation:

- Create a simple, accurate privacy policy before Play submission.
- State that the current MVP stores progress locally on the device.
- State that the current MVP does not require login, does not use a backend, and does not intentionally collect personal information.
- Include a privacy contact method.
- Keep the policy hosted at a public, stable URL.

This is a product/release planning note, not legal advice.

## 8. Data Safety

Use `docs/DATA_SAFETY_DRAFT.md` as the source draft.

Current draft position:

- The app currently does not send data to a server.
- The app stores selected study path and progress locally on device.
- The app does not currently include analytics.
- The app does not currently include ads.
- The app does not currently include account login.
- The app does not currently include payments.
- The app does not currently include backend services.
- The app does not currently include AI API calls.
- User can reset progress in the app.

If AI Coach, analytics, crash reporting, authentication, payments, cloud sync, remote content, or backend services are added later, Data Safety must be updated before release.

Note: Google Play documentation says apps that are exclusively active on internal testing tracks may be exempt from inclusion in the Data Safety section, but the Data Safety draft should still be prepared now so the app is ready for future closed, open, or production tracks.

## 9. Internal Testing Track

Google's official Play Console help describes internal testing as a fast way to distribute an app to up to 100 testers for initial QA.

Steps:

1. Go to **Release > Testing > Internal testing**.
2. Create or choose a tester list.
3. Add tester email addresses.
4. Create a new release.
5. Upload `app-release.aab`.
6. Add release notes from `docs/RELEASE_NOTES.md`.
7. Review the release.
8. Fix any Play Console warnings.
9. Start rollout to internal testing.
10. Copy the tester opt-in link.
11. Send the opt-in link to testers.

Internal testing is intended for quick QA distribution to invited testers. Do not treat internal testing approval as production readiness.

## 10. Tester Instructions

Send testers a short message with these instructions:

1. Use the invited Google account.
2. Open the opt-in link.
3. Accept the testing invitation.
4. Install LearnLift AI from Google Play.
5. Test all three study paths.
6. Report issues with:
   - Device model
   - Android version
   - Issue description
   - Steps to reproduce
   - Screenshot or video if possible

## 11. First Tester Checklist

- [ ] Install from Google Play.
- [ ] Launch app.
- [ ] Choose a study path.
- [ ] Test flashcards.
- [ ] Test quiz.
- [ ] Test daily session.
- [ ] Test progress.
- [ ] Restart app and confirm progress persists.
- [ ] Reset progress.
- [ ] Test light mode.
- [ ] Test dark mode.

## 12. Troubleshooting

### AAB Upload Rejected

- Confirm the uploaded file is `app-release.aab`.
- Confirm package name is `com.learnliftai.app`.
- Confirm the AAB was generated from the correct branch/workspace.
- Check Play Console warnings for signing, SDK, version, or policy issues.

### VersionCode Already Used

Every uploaded build must use a new `versionCode`.

For the next internal build, increment `versionCode` from `1` to `2`.

### Missing Privacy Policy

- Add a public privacy policy URL.
- Replace `PRIVACY_POLICY_URL_HERE`.
- Make sure the policy matches actual app behavior.

### Data Safety Incomplete

- Use `docs/DATA_SAFETY_DRAFT.md`.
- Confirm the answers match the app code.
- Recheck if analytics, crash reporting, AI, auth, backend, payments, or cloud sync are ever added.

### Content Rating Incomplete

- Complete the Play Console content rating questionnaire.
- Current app positioning: educational app, no user-generated content, no social features, no ads, no in-app purchases.

### App Access Incomplete

- Current MVP does not require login.
- Mark that all functionality is available without special access unless this changes later.

### No Testers Added

- Create an internal tester email list.
- Add tester Google account emails.
- Save the tester list before creating or rolling out the release.

### Opt-In Link Not Working

- Confirm the tester is using the invited Google account.
- Confirm the release has rolled out to internal testing.
- Wait and retry if the release link was just created.
- Recopy the opt-in link from Play Console.

### Release Still In Review

- Internal testing can still have Play Console checks or review states.
- Do not assume review is instant.
- Monitor Play Console for warnings or required actions.

### Tester Cannot See App

- Confirm the tester accepted the opt-in invitation.
- Confirm the tester is signed into Google Play with the invited account.
- Confirm the app is compatible with the tester's device.
- Confirm the internal testing release is active.

## 13. Post-Upload Notes

- Do not assume production readiness after internal testing.
- Collect tester feedback.
- Fix bugs before broader testing.
- Increment `versionCode` for every new uploaded build.
- Keep `versionName` semantic, for example `0.1.1` for the next internal build.
- Update release notes for every build.
- Update Data Safety and privacy policy if app behavior changes.

## References

- Google Play Console Help: Set up an open, closed, or internal test.
- Google Play Console Help: Provide information for Google Play's Data Safety section.
- Google Play Console Help: User Data policy.
