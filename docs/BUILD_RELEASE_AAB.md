# Build Release AAB

## Purpose

Generate the first Android App Bundle for Google Play Internal Testing.

This document prepares the LearnLift AI project for a safe internal testing AAB build without committing signing keys, keystore files, passwords, or generated build outputs.

## Prerequisites

- Android Studio installed.
- Android SDK installed.
- Gradle build working in the developer's local PowerShell / Android Studio environment.
- QA passed according to `docs/QA_INTERNAL_TEST_REPORT.md`.
- Google Play Console app record prepared.
- Google Play App Signing plan understood before upload.

## Version

- Package name: `com.learnliftai.app`
- Version name: `0.1.0`
- Version code: `1`

These values are configured in `app/build.gradle.kts`.

## Debug Build Commands

Build the debug APK:

```powershell
.\gradlew.bat assembleDebug
```

Install the debug APK on a connected emulator or device:

```powershell
.\gradlew.bat installDebug
```

## Release AAB Commands

Clean the project:

```powershell
.\gradlew.bat clean
```

Generate the release Android App Bundle:

```powershell
.\gradlew.bat bundleRelease
```

Expected AAB output path:

```text
app\build\outputs\bundle\release\app-release.aab
```

Do not commit the generated `.aab` file to Git.

## Release Signing Notes

- Do not commit keystore files.
- Do not commit keystore passwords.
- Do not commit `keystore.properties` or `signing.properties`.
- Keep signing credentials outside Git.
- Use Google Play App Signing when setting up the Play Console release flow.
- If release signing is not configured locally yet, use Android Studio / Google Play App Signing setup when preparing the upload.
- Do not add production signing secrets to Gradle files.

Current project note: no production signing secrets are configured in the repository. This is intentional for safety.

## Git Safety

The repository `.gitignore` should exclude:

- `*.jks`
- `*.keystore`
- `keystore.properties`
- `signing.properties`
- `local.properties`
- `*.aab`
- `*.apk`

Before committing release prep changes, confirm no generated bundle, APK, keystore, or signing properties file is staged.

## Troubleshooting

### Android SDK License Issues

If Gradle reports missing or unaccepted SDK licenses, open Android Studio SDK Manager and accept/install the required SDK platform and build tools.

You can also use the SDK Manager command-line tools if they are available:

```powershell
sdkmanager --licenses
```

### adb Not Found

If `adb devices` is not recognized, use Android Studio Run or add Android SDK `platform-tools` to PATH.

### Emulator Not Detected

- Start the emulator from Android Studio Device Manager.
- Confirm the emulator is fully booted.
- If `adb` is available, run:

```powershell
adb devices
```

### Windows File Lock Issues

If Gradle cannot delete or overwrite build files:

- Close Android Studio preview windows if needed.
- Stop running emulators if they are locking files.
- Close file explorers opened inside `build` folders.
- Retry a clean build.

### Clean Build Steps

Use:

```powershell
.\gradlew.bat clean
.\gradlew.bat bundleRelease
```

If the issue persists, restart Android Studio and PowerShell, then rerun the commands.

## Pre-Upload Reminder

- Confirm `versionName` is `0.1.0`.
- Confirm `versionCode` is `1`.
- Confirm package name is `com.learnliftai.app`.
- Confirm QA is marked ready for internal testing.
- Confirm Data Safety answers are still accurate.
- Confirm no signing secrets or generated build outputs are committed.
