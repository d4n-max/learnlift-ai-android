# Signed AAB Steps v3.8

Last updated: 2026-06-08

Use these manual steps to generate the LearnLift AI v3.8 signed Android App Bundle. Do not commit generated release artifacts or signing material.

## Before AAB

- [ ] Repository path is `C:\Projects\learnlift-ai-android`.
- [ ] `git status` is clean except intentional release-prep docs before final commit.
- [ ] `local.properties` is configured locally.
- [ ] `USE_REVENUECAT_TEST_STORE=false`.
- [ ] `REVENUECAT_ANDROID_PUBLIC_API_KEY` is configured.
- [ ] `versionCode` is increased above the latest uploaded Play version.
- [ ] `versionName` is `0.3.0`.
- [ ] `.\gradlew.bat clean` passes.
- [ ] `.\gradlew.bat assembleDebug` passes.
- [ ] `node scripts\validate-study-content.mjs` passes.
- [ ] `.\gradlew.bat :app:processDebugMainManifest` passes.
- [ ] Supabase `ai-coach` is deployed.
- [ ] `docs/QA_CHECKLIST.md` is completed for the release candidate.
- [ ] Paywall verification is completed or the release remains blocked.
- [ ] Supabase AI release smoke tests are completed or the release remains blocked.

## Android Studio

1. Open `C:\Projects\learnlift-ai-android`.
2. Let Gradle sync finish.
3. Confirm the `app` module syncs without errors.
4. Select **Build -> Generate Signed Bundle / APK**.
5. Select **Android App Bundle**.
6. Use the existing LearnLift upload key.
7. Select build variant: `release`.
8. Generate the bundle.

Expected output:

```text
app/build/outputs/bundle/release/app-release.aab
```

## Do Not Commit

- `app-release.aab`
- Any `.aab` or `.apk`
- Any `.jks` or `.keystore`
- Keystore passwords
- `local.properties`
- `.gradle/`
- `app/build/`

## Recommended Tag After Successful Upload

Do not create this tag until the Play upload is accepted and post-upload verification passes.

```text
v0.3.0-play-release
```

Alternative:

```text
learnlift-v3.8-release
```
