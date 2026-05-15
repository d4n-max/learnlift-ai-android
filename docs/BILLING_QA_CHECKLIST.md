# Billing QA Checklist

## Configuration

- App launches without `REVENUECAT_PUBLIC_API_KEY` configured.
- Premium screen shows friendly unavailable messaging with placeholder prices.
- Free mode remains usable when RevenueCat is unavailable.
- No OpenAI keys, RevenueCat private keys, or Supabase service role keys are in Android code/resources.

## Premium Screen

- Premium screen opens from Home.
- Premium screen opens from Settings.
- Premium screen opens from Progress teaser.
- Monthly package displays placeholder price when offerings are unavailable.
- Yearly package displays placeholder price when offerings are unavailable.
- Monthly package displays RevenueCat price when configured.
- Yearly package displays RevenueCat price when configured.
- User can select monthly.
- User can select yearly.
- Subscribe button is disabled if package is unavailable.
- Subscribe button starts purchase flow when package is available.
- Friendly message appears if products are not configured yet.

## Purchase Flow

- Purchase cancellation shows a non-scary cancellation message.
- Purchase error shows a friendly error message.
- Purchase success updates current plan to Premium.
- Premium active status appears after successful purchase.
- Entitlement persists after app restart.
- Purchase fails gracefully if app is not installed from Google Play.
- Purchase fails gracefully if products are not active in Play Console.

## Restore Purchases

- Restore purchases button appears on Premium screen.
- Restore purchases button appears in Settings.
- Restore success updates Premium active state.
- Restore with no active purchase shows a friendly message.
- Restore failure does not crash the app.

## Existing App Flows

- Home still works in Free mode.
- Study Path Selection still works.
- Flashcards still work.
- Quiz still works.
- Daily Session still works.
- Progress still works.
- Settings still work.
- Premium screen still works offline or with unavailable products.
- Local DataStore progress still persists.
- AI fallback still works if AI backend quota is unavailable.

## Data Safety

- RevenueCat/Google Play purchase processing is documented.
- App still has no ads.
- App still has no login.
- App still has no cloud sync.
- AI backend context transfer remains user-initiated only.
