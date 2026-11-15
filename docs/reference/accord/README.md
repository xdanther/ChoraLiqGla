# Accord — repository guide

Accord is a local Android music player with an Apple-inspired design: synced lyrics,
gapless playback, playlists, folder browsing and third-party equalizer support. It is
a fork of [Gramophone](https://github.com/AkaneTan/Gramophone) /
[AccordLegacy](https://github.com/FoedusProgramme/AccordLegacy).

> **Status:** the repository's own `readme.md` says this fork is **no longer
> maintained** and points at Accord 2.0 distributed through Telegram. The code here
> is useful as a reference, not as an active upstream.

**Snapshot:** branch `alpha`, commit `653cb86`, 432 tracked files (6.3 MB, excluding
the `.git` directory). Complete per-file coverage is in
[inventory.md](inventory.md).

## At a glance

| Property | Value |
|---|---|
| Package / application id | `org.akanework.gramophone` / `uk.akane.accord` |
| Version | `beta2` (versionCode 18) |
| SDK | min 31, target 36, compile 36 |
| Language | Kotlin, with some vendored Java |
| UI | XML layouts, Fragments, `RecyclerView`, ViewPager2-style paging |
| Playback | AndroidX Media3 `MediaLibraryService` + ExoPlayer |
| Storage | Room (`app.db`), SharedPreferences, MediaStore |
| Images | Coil 3 |
| Build | Gradle Kotlin DSL, KSP, two modules |

## Modules

```
Accord/
├── app/            the application
├── recyclerview/   vendored AndroidX RecyclerView + fluid overscroll additions
├── gradle/         wrapper
└── fastlane/       store metadata and screenshots
```

`settings.gradle.kts` includes `:app` and `:recyclerview`. The app module also
compiles `libs/lib-decoder-ffmpeg-release.aar` and two overlay-slider Java classes
under `com.google.android.material.slider`.

## Application start-up

**`logic/GramophoneApplication.kt`**

- Implements `SingletonImageLoader.Factory`, so Coil 3 is configured globally rather
  than per screen.
- Installs itself as the default uncaught-exception handler and launches
  `ui/BugHandlerActivity` with the stack trace before terminating.
- Owns the `SharedPreferences` instance used by the preference screens.
- Applies the theme mode through `AppCompatDelegate`.

**`ui/MainActivity.kt`**

- Single `AppCompatActivity` hosting `R.layout.activity_main`: a
  `FragmentContainerView`, bottom navigation and a persistent
  `PlayerBottomSheet`.
- Requests audio permission, triggers `updateLibraryWithInCoroutine`, keeps the
      splash screen until the library is ready (with a 3 s safety timeout), and applies
      the player sheet's insets to the fragment container.
- Exposes `libraryViewModel` and the player sheet to fragments.

## Playback

**`logic/GramophonePlaybackService.kt`** (670 lines)

A `MediaLibraryService` and `MediaSessionService.Listener`:

- Creates the ExoPlayer instance with a custom renderers/extractors setup
  (`logic/utils/exoplayer/`) and the FFmpeg decoder.
- Publishes a `MediaLibrarySession` and answers `onGetSession`.
- Handles `onPlaybackResumption`, so playback can be restored by the system.
- Reacts to track transitions, playing state, shuffle/repeat and timeline changes.
- Builds the media notification and handles custom session commands.
- Extracts embedded lyrics from track metadata using `LrcUtils` and publishes them
  for the player UI.
- Uses `EndedWorkaroundPlayer` to work around an ExoPlayer end-of-stream edge case.

**`logic/GramophoneExtensions.kt`** (702 lines)

The shared toolbox: `Player` extensions (queue manipulation, repeat/shuffle, media
item helpers), `MediaItem`/`MediaMetadata` helpers, activity/context utilities,
bitmap and drawable helpers, and the lyric `CustomTextView` gradient animations
(`updateGradient`, `resetShader`, `scaleText`).

**`logic/utils/`**

| Area | Files |
|---|---|
| Media scanning | `MediaStoreUtils.kt` (837 lines), `FileOpUtils.kt` |
| Playback helpers | `LastPlayedManager.kt`, `CircularShuffleOrder.kt`, `RecommendationFactory.kt` |
| Lyrics | `LrcUtils.kt` |
| Colours | `ColorUtils.kt` |
| ExoPlayer | `EndedWorkaroundPlayer.kt`, `GramophoneExtractorsFactory.kt`, `GramophoneMediaSourceFactory.kt`, `GramophoneRenderFactory.kt` |

## Library and persistence

**`ui/LibraryViewModel.kt`** exposes `LiveData` for albums, album artists, artists,
genres, dates, playlists and two folder trees (full and shallow).

**Room database** (`logic/data/db/`):

- `AppDatabase` — `app.db`, version 1, singleton.
- Entities: `Playlist`, `MediaItem`, `PlaylistMediaItemCrossRef`, plus relation
  classes `PlaylistWithMediaItem` and `MediaItemWithPlaylist`.
- DAOs: `PlaylistDao`, `MediaItemDao`.

MediaStore remains the source of truth for the music library; Room stores
user-created playlists and their membership. `MediaStoreUtils` also defines the
library model types (`Album`, `Artist`, `Genre`, `Date`, `Playlist`, `FileNode`,
`Lyric`) used throughout the UI.

## UI layer

Accord uses classic Android views rather than Compose:

- **Fragments** — `HomepageFragment`, `BrowseFragment`, `LibraryFragment`,
  `SearchFragment`, `ArtistSubFragment`, `LibraryAlbumSubFragment`,
  `LibrarySongSubFragment`, the `Base*` fragments, and the settings fragments under
  `ui/fragments/settings/`.
- **Adapters** — one per list type: albums, artists, songs, playlists, genres,
  folders, headers, recommendations, library categories and the homepage carousel.
- **Components** — `BlendView` (background), `FullBottomSheet` (2,603 lines, the
  expanded player), `PlayerBottomSheet` (579 lines, collapsed player + transitions),
  `CustomTextView` (gradient lyric text), `MarqueeTextView`,
  `FadingVerticalEdgeLayout`, custom layout managers and decorations.
- **Layouts** — `activity_main.xml`, `full_player.xml`, `fragment_*.xml`, adapter
  rows and lyric layouts under `app/src/main/res/layout/`.
- **Preferences** — XML screens in `res/xml/` (`settings_appearance`,
  `settings_audio`, `settings_behavior`, `settings_experimental`, `settings_top`,
  `settings_about`) hosted by the matching fragments.

### Player presentation

`PlayerBottomSheet` owns the collapsed player and a `BlendView`. When the sheet is
expanded it hands the same `BlendView` to `FullBottomSheet`, animates its blur radius
and starts the rotation animation; when collapsed it sets alpha to 0 and stops the
animation. The background method is documented in
[../background-methods.md](../background-methods.md).

## Lyrics

`logic/utils/LrcUtils.kt`:

- Reads lyrics from Vorbis `LYRICS`, ID3 `USLT`/`SYLT` binary frames, or m4a text
  frames.
- Parses LRC/SRT into timed lines and supports Walaoke / iTunes duet labels
  (`Male`, `Female`, `Duet`, `Background`, `Voice1`, `Voice2`).
- Is covered by `app/src/test/java/org/akanework/gramophone/LrcUtilsTest.kt`.

The player renders lyrics with `CustomTextView`, which uses a shader whose progress
is advanced by playback time; `GramophoneExtensions` contains the colour/scale
animations around line changes.

## Settings

Settings are `PreferenceFragmentCompat` screens backed by XML:

| Screen | XML | Fragment |
|---|---|---|
| Main | `settings_top.xml` | `MainSettingsTopFragment` |
| Appearance | `settings_appearance.xml` | `AppearanceSettingsTopFragment` |
| Audio | `settings_audio.xml` | `AudioSettingsTopFragment` |
| Behaviour | `settings_behavior.xml` | `BehaviorSettingsTopFragment` |
| Experimental | `settings_experimental.xml` | `ExperimentalSettingsTopFragment` |
| Blacklist | `fragment_blacklist_settings.xml` | `BlacklistSettingsFragment` |
| About | `settings_about.xml` | `AboutFragment` / `AboutContributorFragment` |

Appearance values (theme, player background, lyrics alignment) are read through
`PreferenceManager`/`SharedPreferences` and observed by the player.

## Vendored code

The inventory marks these as third-party sources shipped in the repo:

- `recyclerview/src/main/java/androidx/recyclerview/widget/**` — a vendored AndroidX
  RecyclerView (RecyclerView.java alone is 14,408 lines), plus
  `org.helixform.fluidrecyclerview.**` for fluid overscroll/rubber-band physics.
- `app/src/main/java/com/google/android/material/slider/BaseOverlaySlider.java` and
  `OverlaySlider.java` — Material slider variants used by the player.
- `libs/lib-decoder-ffmpeg-release.aar` — FFmpeg decoder.

## Resources

- 20+ translation sets under `app/src/main/res/values-*`.
- `values/colors.xml` (11 KB) defines the contrast/theming palette, including
  `contrast_blendOverlayColor`.
- `layout/` contains the activity, player sheets, fragment screens, adapter rows and
  lyric views.
- `drawable/` contains the icon set and default covers; each file has its own row in
  the inventory.

## Build and tests

```bash
./gradlew assembleRelease
```

The repo readme gives the same command. CI is defined in
`.github/workflows/android.yml`. The only unit test in this snapshot is
`LrcUtilsTest`; most of the UI has no automated coverage.

## Key files, in reading order

1. `logic/GramophoneApplication.kt` — process-wide setup.
2. `ui/MainActivity.kt` — host activity, permissions, library bootstrap.
3. `logic/GramophonePlaybackService.kt` — Media3 service and player.
4. `logic/GramophoneExtensions.kt` — player/media/UI helpers.
5. `ui/components/PlayerBottomSheet.kt` and `FullBottomSheet.kt` — player UI.
6. `ui/components/BlendView.kt` — the now-playing background.
7. `logic/utils/MediaStoreUtils.kt` — library scanning and model types.
8. `logic/data/db/` — Room schema.
9. `logic/utils/LrcUtils.kt` — lyric extraction and parsing.
10. `ui/adapters/` and `ui/fragments/` — list and screen layer.

For every other path, see [inventory.md](inventory.md).
