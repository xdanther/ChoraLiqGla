# Handoff — ChoraLiqGla (TV-only)

**Date:** 2026-11-15  
**Repo:** `xdanther/ChoraLiqGla`  
**Branch:** `freebuff/afa4b0b0429eb2ef0b5f4f5e`  
**Last pushed commit:** `81e06db` (player visuals). The TV-only cleanup below is uncommitted.

## Goal

ChoraLiqGla is now an **Android TV-only** music player. The phone UI, phone player,
phone settings, phone navigation, phone dialogs and phone store screenshots have been
removed. Shared playback, library, providers, artwork backgrounds and lyrics remain.

## Current state

| Area | Status |
|---|---|
| Entry point | `MainActivity` is TV-only; no bottom navigation, mini player, bottom sheets or phone onboarding |
| Navigation | `SetupNavGraph` exposes only TV destinations; `Screen.NowPlaying` is the TV player |
| Player | `NowPlayingContent` always renders `TvNowPlaying`; portrait/landscape branches removed |
| Settings | TV settings only; phone appearance/settings screens and dialogs removed |
| Manifest | Leanback is required; `LAUNCHER` category removed, `LEANBACK_LAUNCHER` only |
| Screenshots | `fastlane/.../phoneScreenshots` removed; TV screenshots remain |
| Background styles | `Plain`, `Static blur`, `Ken Burns`, `Layered drift`, `Floating light`, `Kaleidoscope`, `Aurora` |
| Lyric modes | `Classic` (default), `Gradient sweep`, `Spotlight`; active line at 42% of the viewport |
| Theme | TV palette and TV text scaling only |
| Build | `./gradlew :app:compileReleaseKotlin` → **BUILD SUCCESSFUL** (existing warnings only) |
| Visual QA | **Not done** — no usable emulator/device here; TV layout, focus and motion still need a real-screen check |
| Git | TV-only changes are in the working tree, **not committed or pushed** |

## What was removed

- Phone player files: `NowPlayingPortrait`, `NowPlayingLandscape`, `NowPlayingMiniPlayer`,
  `NowPlayingDetails`, `PlayQueue`, `NowPlayingElements`.
- Phone screens: home, songs, albums, album details, artists, artist details, playlists,
  playlist details, radio, search/home lists and the phone settings screens.
- Phone-only cards and lists: `AlbumCard`, `ArtistCard`, `PlaylistCard`, `ProviderCard`,
  `RadioCard`, `SongCard`, `SongLazyLists`, `SongListActionButtons`, `GenrePill`,
  `TopBarWithSearch`, `VerticalText`, `RefreshRipple`.
- Phone-only dialogs: misc, onboarding, playback, playlist, provider, radio, rating,
  appearance dialogs and `dialogFocusable`.
- Phone-specific `NowPlayingViewModel` panel state and the phone lyric-pane layout branch.
- Phone store screenshots.

Shared models were preserved: `HomeItem` moved to `data/model/HomeItem.kt` and the
action-button setting model moved to `data/model/ActionButton.kt`.

## Files to read first

| File | Role |
|---|---|
| `app/src/main/java/com/craftworks/music/MainActivity.kt` | TV activity, intents, permissions, onboarding |
| `app/src/main/java/com/craftworks/music/NavGraph.kt` | TV-only navigation graph |
| `app/src/main/java/com/craftworks/music/data/model/Screen.kt` | TV routes |
| `app/src/main/java/com/craftworks/music/ui/playing/NowPlaying.kt` | TV player host and background overlay |
| `app/src/main/java/com/craftworks/music/ui/playing/tv/TvNowPlaying.kt` | TV player, TV lyric pane and focus handling |
| `app/src/main/java/com/craftworks/music/ui/playing/NowPlayingLyrics.kt` | Shared lyric list, autoscroll and 42% focus |
| `app/src/main/java/com/craftworks/music/ui/playing/background/ArtworkBlur.kt` | Six artwork motions |
| `app/src/main/java/com/craftworks/music/ui/theme/Theme.kt` | TV-only theme |
| `app/src/main/java/com/craftworks/music/ui/screens/tv/settings/TvSettingsAppearance.kt` | TV background/lyric pickers |
| `docs/reference/` | Accord/Flamingo reference documentation |

## Next steps

1. Build and install a release APK on an Android TV device or emulator.
2. Verify the TV launcher entry, sidebar focus, playback, search, settings and the player.
3. Check all seven artwork styles and all three lyric modes on a real screen; tune motion,
   blur and glow strength.
4. Commit and push the TV-only cleanup once the build/install check is satisfactory.
