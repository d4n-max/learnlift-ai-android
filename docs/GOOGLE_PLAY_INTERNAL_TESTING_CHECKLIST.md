# Google Play Internal Testing Checklist

## Google Play Console Setup

- [ ] Create or open the Google Play Console app record.
- [ ] Confirm developer account access.
- [ ] Confirm app type is App.
- [ ] Confirm category is Education.
- [ ] Confirm app is free unless monetization is added later.

## App Identity

- [ ] App name is `LearnLift AI`.
- [ ] Tagline is `Elevate Your Skills, Effortlessly.`
- [ ] Package name is `com.learnliftai.app`.
- [ ] Version name is `0.1.0`.
- [ ] Version code is `1`.
- [ ] App icon is present.
- [ ] Feature graphic placeholder is prepared.
- [ ] Screenshots are captured for phone form factor.

## Store Listing

- [ ] Short description drafted.
- [ ] Full description drafted.
- [ ] Current study paths listed accurately.
- [ ] Current exclusions are clear: no live AI Coach, login, cloud sync, payments, or guaranteed results.
- [ ] Keywords / ASO notes captured for later refinement.

## Required Google Play Forms

- [ ] Privacy policy requirement reviewed.
- [ ] Data Safety form prepared from `docs/DATA_SAFETY_DRAFT.md`.
- [ ] App access form completed: no login required.
- [ ] Ads declaration completed: no ads currently.
- [ ] Content rating questionnaire completed.
- [ ] Target audience selected.
- [ ] Internal testers list created or selected.

## Release Setup

- [ ] Release notes prepared from `docs/RELEASE_NOTES.md`.
- [ ] AAB generated with safe signing setup.
- [ ] AAB uploaded to the internal testing track.
- [ ] Internal testing track reviewed.
- [ ] Tester opt-in link copied.
- [ ] Tester instructions shared with internal testers.

## Manual Pre-Upload Checklist

- [x] Build succeeds.
- [x] Install succeeds.
- [x] App launches.
- [x] Full emulator QA passed.
- [x] Reset progress works.
- [x] Local persistence works.
- [x] No obvious crashes documented.
- [x] README updated.
- [x] QA report updated.

## Screenshot Checklist

- [ ] Home Dashboard
- [ ] Study Path Selection
- [ ] Flashcards
- [ ] Quiz question and explanation
- [ ] Daily Study Session
- [ ] Progress Screen
- [ ] Settings Screen

## Notes Before Upload

- Do not create or commit production signing keys.
- Do not add Firebase, authentication, payments, backend code, cloud sync, analytics, or AI API calls for this internal testing prep.
- Confirm Data Safety answers again before submitting the internal testing release.
