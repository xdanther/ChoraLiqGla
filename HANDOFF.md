# Handoff — ChoraLiqGla player visuals

**Date:** 2026-11-15  
**Repo:** `xdanther/ChoraLiqGla`  
**Branch:** `freebuff/afa4b0b0429eb2ef0b5f4f5e`  
**Last pushed commit:** `b7dfcaa` (reference-player documentation only; the visual changes below are uncommitted)

## Goal

ChoraLiqGla is an Android music player with an Apple Music-inspired now-playing
screen. The current work is to take the strongest player/background/lyric ideas from
two reference apps and make them native, optional ChoraLiqGla features without
breaking the existing styles:

- **Accord** — `emylfy/Accord`, branch `alpha` @ `653cb86`.
- **Flamingo** — `shouryadixitisverycool/Flamingo`, branch `master` @ `316df89`
  (fork of `Yos-X/FlamingoSank`).

The reference documentation and complete per-file inventories live in
`docs/reference/`. The player must keep working on phone, TV and landscape.

## Current status

| Area | Status |
|---|---|
| Background styles | Existing `Plain`, `Blurred`, `Ken Burns`, `Layered drift` unchanged; three new styles added: `Floating light`, `Kaleidoscope`, `Aurora` |
| Lyric presentation | Existing look retained as `Classic`; two new modes added: `Gradient sweep` (Accord) and `Spotlight` (Flamingo) |
| Lyric focus | Active line now sits at **42%** of the viewport instead of 20%, i.e. slightly above the middle |
| Picker coverage | Phone settings, TV settings, phone dialog and TV dialog all expose the new options |
| Persistence | DataStore keys `np_background_type` and new `np_lyrics_style`; enum names are stored |
| Build | `./gradlew :app:compileReleaseKotlin` → **BUILD SUCCESSFUL** (only existing deprecation warnings) |
| Visual QA | **Not done** — no usable emulator/device in this environment; motion, legibility and blur strength still need a real-screen check |
| TV caveat | The separate `appleTvLyrics` pane has its own renderer; the new lyric modes apply to the shared TV lyric view, not that pane |
| Git | Changes below are in the working tree, **not committed**; the earlier reference docs were pushed in `b7dfcaa` |

## Decisions made

1. **Preserve existing styles.** No existing background enum name, label or rendering
   path was renamed or changed; new values were appended to the enums.
2. **Reuse the 64 px backdrop.** All background motion is applied to the pre-baked,
   blurred artwork plate, not recomputed per frame. New styles use Compose
   `graphicsLayer`/`Canvas`, so they remain cheap on TV.
3. **Map the references explicitly.**
   - `Floating light` adapts Flamingo's additive `overlayEffect`/floating-light idea.
   - `Kaleidoscope` extends Accord's quarter-layer `BlendView` with mirrored tiles.
   - `Aurora` uses the artwork's own `ArtworkColours` palette as moving bands.
4. **Lyric modes are opt-in.** `Classic` is the default, so existing users see no
   change. `Gradient sweep` disables distance blur and fills the active line with a
   left-to-right gradient. `Spotlight` forces stronger neighbour blur (up to 16 dp)
   and enlarges the active line.
5. **Lyrics stay above centre.** The focus offset is `0.42f` of the viewport height,
   which gives a line centre around 44–45% on a typical phone.
6. **Both form factors matter.** Every new picker option is present on phone and TV;
   the shared `LyricsView` and the TV Apple-style lyric pane are separate code paths.

## Files

### Files changed or added for this work

| File | What it does |
|---|---|
| `app/src/main/java/com/craftworks/music/ui/playing/background/ArtworkBlur.kt` | Builds the 64 px artwork plate and renders all six backdrop motions. |
| `app/src/main/java/com/craftworks/music/ui/playing/NowPlayingBackground.kt` | Defines the background-style enum and maps it to a `BackdropMotion`. |
| `app/src/main/java/com/craftworks/music/ui/playing/NowPlayingLyricsStyle.kt` | Defines `Classic`, `Gradient sweep` and `Spotlight` lyric modes. |
| `app/src/main/java/com/craftworks/music/ui/playing/NowPlayingLyrics.kt` | Lyric list, AutoScroll/recenter logic, 42% focus offset and lyric-style plumbing. |
| `app/src/main/java/com/craftworks/music/ui/playing/lyrics/SyncedLyricItem.kt` | Renders whole-line lyrics, including gradient fill and spotlight blur. |
| `app/src/main/java/com/craftworks/music/ui/playing/lyrics/WordSyncedLyricItem.kt` | Renders word-synced lyrics and applies the selected lyric blur/scale mode. |
| `app/src/main/java/com/craftworks/music/managers/settings/AppearanceSettingsManager.kt` | Persists background style and the new `np_lyrics_style` preference. |
| `app/src/main/java/com/craftworks/music/ui/elements/dialogs/appearance/BackgroundDialog.kt` | Phone background-style picker; includes the three new labels. |
| `app/src/main/java/com/craftworks/music/ui/elements/dialogs/appearance/NowPlayingLyricsStyleDialog.kt` | Phone lyric-style picker. |
| `app/src/main/java/com/craftworks/music/ui/elements/dialogs/tv/AppearanceDialogs.kt` | TV dialogs, including background and lyric-style pickers. |
| `app/src/main/java/com/craftworks/music/ui/screens/settings/SettingsAppearance.kt` | Phone Appearance screen; exposes lyric style. |
| `app/src/main/java/com/craftworks/music/ui/screens/tv/settings/TvSettingsAppearance.kt` | TV Appearance screen; exposes lyric style. |
| `app/src/main/res/values/strings_settings.xml` | Labels for the new backgrounds and lyric modes. |
| `HANDOFF.md` | This handoff note. |

### Existing files a new chat should read first

| File | What it does |
|---|---|
| `app/src/main/java/com/craftworks/music/ui/playing/NowPlaying.kt` | Chooses TV, landscape or portrait player; owns background overlay logic. |
| `app/src/main/java/com/craftworks/music/ui/playing/NowPlayingPortrait.kt` | Phone player: artwork, title/artist, lyrics and controls. |
| `app/src/main/java/com/craftworks/music/ui/playing/NowPlayingLandscape.kt` | Landscape player and its lyric pane. |
| `app/src/main/java/com/craftworks/music/ui/playing/tv/TvNowPlaying.kt` | TV player, TV lyrics pane and TV-specific lyric placement. |
| `app/src/main/java/com/craftworks/music/ui/playing/NowPlayingViewModel.kt` | Loads artwork, extracts the palette and builds the backdrop. |
| `app/src/main/java/com/craftworks/music/ui/playing/ArtworkColours.kt` | OKLCH artwork palette extraction used by all backgrounds. |
| `app/src/main/java/com/craftworks/music/ui/playing/background/StaticBlur.kt` | Artwork-colour fallback shown before the plate is ready. |
| `app/src/main/java/com/craftworks/music/ui/playing/background/Plain.kt` | Plain background used by the `Plain` style. |
| `app/src/main/java/com/craftworks/music/ui/playing/background/AnimatedGradient.kt` | Older API-33 shader gradient, kept as reference/fallback. |
| `app/src/main/java/com/craftworks/music/ui/playing/lyrics/InterludeIndicator.kt` | Three bouncing dots for instrumental gaps. |
| `app/src/main/java/com/craftworks/music/ui/playing/NowPlayingElements.kt` | Shared playback controls and progress slider. |
| `app/src/main/java/com/craftworks/music/ui/playing/NowPlayingMiniPlayer.kt` | Collapsed mini player. |
| `app/src/main/java/com/craftworks/music/ui/playing/NowPlayingDetails.kt` | Track details sheet. |
| `app/src/main/java/com/craftworks/music/ui/playing/PlayQueue.kt` | Up-next queue sheet. |
| `app/src/main/java/com/craftworks/music/ui/playing/tv/NowPlayingElements.kt` | TV-specific shared controls. |
| `app/src/main/java/com/craftworks/music/ui/elements/dialogs/appearance/NowPlayingTitleAlignmentDialog.kt` | Shared alignment picker used by title and lyrics settings. |
| `app/src/main/java/com/craftworks/music/ui/elements/dialogs/appearance/ThemeDialog.kt` | Theme picker. |
| `app/src/main/java/com/craftworks/music/ui/elements/dialogs/appearance/HomeItemsDialog.kt` | Home-section picker. |
| `app/src/main/java/com/craftworks/music/ui/elements/dialogs/appearance/NavbarItemsDialog.kt` | Bottom-navigation picker. |
| `app/src/main/java/com/craftworks/music/ui/elements/dialogs/appearance/NameDialog.kt` | Username dialog. |
| `app/src/main/java/com/craftworks/music/ui/elements/dialogs/appearance/SongListActionButtonsDialog.kt` | Detail-action button editor. |
| `app/src/main/java/com/craftworks/music/ui/elements/dialogs/tv/Dialogs.kt` | Reusable TV dialog components. |
| `docs/reference/README.md` | Index and provenance for the reference-player documentation. |
| `docs/reference/background-methods.md` | Code-level comparison of Accord `BlendView`, Flamingo `YosFloatingLight` and our plate. |
| `docs/reference/accord/README.md` | Accord architecture guide. |
| `docs/reference/accord/inventory.md` | All 432 Accord tracked files, one row each. |
| `docs/reference/flamingo/README.md` | Flamingo architecture guide. |
| `docs/reference/flamingo/inventory.md` | All 273 Flamingo tracked files, one row each. |

The repository has 390 tracked files in total; this note lists the player,
appearance, settings and reference-doc files that matter to this workstream rather
than duplicating unrelated library/network code.

## Next steps

1. Build/install a release APK and check all seven background styles and all three
   lyric modes on a phone and a TV; tune glow opacity, motion speed and 16 dp blur.
2. Decide whether `Spotlight` should force blur even when the user's “Lyrics Blur”
   switch is off. It currently does, to keep the mode recognisable.
3. Check `Gradient sweep` on long lines, duet/agent lines, RTL and plain unsynced
   lyrics; the current fill is line-level and left-to-right.
4. Add translations for `appearance_now_playing_lyrics_style` and the three
   `lyrics_style_*` labels (English fallback works meanwhile).
5. Update `docs/reference/background-methods.md`, which still describes only the
   original three ChoraLiqGla styles, with the three new styles and lyric modes.
6. Commit and push the visual changes once QA is done; the reference docs are already
   pushed in `b7dfcaa`.
7. Optionally add screenshots or short recordings to the reference docs.
