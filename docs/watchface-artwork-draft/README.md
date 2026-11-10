# AuraClock watch-face artwork draft

This branch records the current draft asset handoff.

## Current state

- A local draft pack was prepared with 15 JPG background concepts in `res/drawable/`.
- The pack is not production-ready: several concepts were cropped from generated contact sheets and may repeat or mismatch the intended face style.
- No Kotlin renderer changes are included.
- The existing `Motorsport M` and `Speedmaster Racing` faces are protected and must remain unchanged.
- The eight Compose-drawn faces are not part of this image pack.

## Asset pack

The current local archive is named `auraclock_watchface_assets.zip` in the working session. It contains a README and these draft resource names:

- face_club_retrograde.jpg
- face_casio_royale.jpg
- face_racing_yellow.jpg
- face_sports_pods.jpg
- face_cyber_3d.jpg
- face_mecha_tactical.jpg
- face_bmw_wheel.jpg
- face_titanium_red.jpg
- face_nike_sport.jpg
- face_orange_diver.jpg
- face_hawker_hunter.jpg
- face_rotary_gauge.jpg
- face_radial_telemetry.jpg
- face_cockpit_yellow.jpg
- face_seiko_digiana.jpg

## Important

These files are draft artwork only. They have not been installed into the Android app or tested on-device. Review every face at actual watch size before merging or releasing. The generated backgrounds should not contain hands, time, date, or baked-in complications because Compose draws the live clock elements.

The binary asset files could not be transferred through the available GitHub file-writing action in this session, so this branch does not pretend to contain those JPGs.
