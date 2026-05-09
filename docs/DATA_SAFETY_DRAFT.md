# LearnLift AI Data Safety Draft

## Current MVP Behavior

LearnLift AI is currently a local-only Android MVP.

- The app stores progress locally on the device using DataStore.
- The app uses local JSON content bundled with the app.
- The app does not require login.
- The app does not send user data to a backend.
- The app does not use AI API calls.
- The app does not include ads.
- The app does not include payments.
- The app does not include analytics or crash reporting based on the current code scan.
- The app does not intentionally collect personal information.

## Data Collected

Likely none externally for the current MVP.

The current app does not intentionally collect, transmit, or share personal data with external services.

## Data Stored Locally

The app stores basic study progress locally on the user's device, including:

- Selected study path
- Flashcard progress counts
- Quiz completion stats
- Last quiz score
- Streak and last study date

## Data Sharing

None currently.

The current MVP does not share user data with third parties because it does not use a backend, analytics service, ad network, cloud sync, payment provider, or AI provider.

## Data Deletion

Users can reset progress in the app.

The reset action clears local progress stats while keeping the app usable without an account.

## Security

The current MVP is local-only and does not transmit user study progress over the network.

No API keys, backend credentials, payment credentials, or AI provider keys should be stored in the Android app.

## Google Play Data Safety Draft Answers

- Data collected: likely none externally for the current MVP.
- Data shared: no.
- Data processed ephemerally: not applicable for external services.
- Account creation: not required.
- User deletion request process: in-app reset clears local progress; no server account exists.
- Encryption in transit: not applicable because the MVP does not transmit user data.

## Future Warning

If AI Coach, analytics, authentication, crash reporting, payments, cloud sync, remote content, or backend services are added later, the Data Safety form must be reviewed and updated before release.

Future AI Coach work must also avoid placing secret API keys in the Android app. Any AI provider integration should use a secure backend proxy as described in `docs/AI_COACH_PLAN.md`.
