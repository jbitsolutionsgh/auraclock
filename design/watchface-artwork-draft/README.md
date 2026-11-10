# AuraClock watch-face artwork draft

This branch stages the current concept asset pack for review. It is intentionally kept separate from the app's production drawable resources.

## Protected faces
- Motorsport M — unchanged
- Speedmaster Racing — unchanged

## Draft pack
The current local draft archive contains 15 JPG concept backgrounds named to correspond to existing image-backed face resource identifiers. They were cropped from generated contact-sheet artwork, so some are repetitive or imperfectly matched. They are **not production-ready** and should not replace the app's drawable resources until reviewed on-device.

## Faces drawn in Compose
The eight Compose-rendered faces are not part of this artwork pack and should remain untouched.

## Next review
1. Inspect each concept background at actual watch-face size.
2. Reject duplicates and mismatched concepts.
3. Replace approved images individually in `app/src/main/res/drawable/`.
4. Verify live hands, ticks, date and complications still render correctly.

The separate ZIP prepared for review is not committed as production assets in this branch.
