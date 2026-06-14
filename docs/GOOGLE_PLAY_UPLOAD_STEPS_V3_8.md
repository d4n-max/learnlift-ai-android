# Google Play Upload Steps v3.8

Last updated: 2026-06-08

Use these steps after generating the signed v3.8 AAB.

## Upload

1. Open Google Play Console.
2. Select **LearnLift AI**.
3. Go to **Test and release**.
4. Choose **Closed Testing** or **Production**, depending on the current rollout plan.
5. Create a new release.
6. Upload:

```text
app/build/outputs/bundle/release/app-release.aab
```

7. Paste release notes from `docs/PLAY_CONSOLE_RELEASE_NOTES.md`.
8. Check app bundle warnings.
9. Confirm Data Safety.
10. Confirm Privacy Policy.
11. Confirm store listing from `docs/PLAY_STORE_LISTING_V3_FINAL.md`.
12. Confirm screenshots from `docs/SCREENSHOT_PLAN_V3_6.md` or newer captured v3.8 assets.
13. Confirm feature graphic from `docs/FEATURE_GRAPHIC_V3_6_PLAN.md` or newer generated v3.8 asset.
14. Review the release.
15. Send for review or start rollout.

## Post-Upload Verification

- [ ] Install from Google Play.
- [ ] Verify real paywall prices.
- [ ] Verify Google Play purchase sheet opens.
- [ ] Verify there is no Test Store dialog.
- [ ] Verify Premium entitlement becomes active after purchase.
- [ ] Verify Restore purchases works.
- [ ] Verify AI Coach answer explanations.
- [ ] Verify AI Quiz Review.
- [ ] Verify 7-day AI Study Plan.
- [ ] Verify available Premium Study Packs.
- [ ] Verify coming-soon Premium packs do not open empty/broken content.
- [ ] Verify Free study paths still work without purchase.
- [ ] Verify no Play Console warnings block rollout.

## Release Caution

Do not use `docs/PLAY_STORE_LISTING_DRAFT.md` for upload copy. Use `docs/PLAY_STORE_LISTING_V3_FINAL.md` unless a newer final listing is created.
