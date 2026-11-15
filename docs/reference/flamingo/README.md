# Flamingo — repository guide

Flamingo is a **local** Android music player styled after Apple Music. It reads the
device's MediaStore library, plays with Media3/ExoPlayer, and implements its own
Compose UI, synced-lyric engine, animated artwork and now-playing background.

**Snapshot:** branch `master`, commit `316df89`, 273 tracked files (13.8 MB,
including four bundled `.aar` libraries). Complete per-file coverage is in
[inventory.md](inventory.md).

> The repository is a fork of
> [Yos-X/FlamingoSank](https://github.com/Yos-X/FlamingoSank), GPL-3.0. The README
> says “minimum support for Android 5.0”, but the build file sets `minSdk 23`
> (Android 6.0); trust the build file.

## At a glance

| Property | Value |
|---|---|
| Package / application id | `yos.music.player` |
| Version | `2.1.0` (versionCode 3) |
| SDK | min 23, target 34, compile 34 |
| UI | Jetpack Compose + Material 3, Cupertino icons, Haze blur |
| Playback | Media3 `MediaSessionService` + ExoPlayer + `FadeExo` |
| Library | MediaStore via bundled `libPhonograph`, local storage only |
| Persistence | MMKV + `ComposeDataSaver` + Gson |
| Images | Coil 2 |
| Build | Groovy Gradle, modules `:app` and `:overscroll_core` |

## Modules and bundled libraries

```
Flamingo/
├── app/                 application, Compose UI, playback, data
├── overscroll_core/     reusable overscroll physics (OverScroll.kt)
├── docs/                README screenshots and lyric-format documentation
├── app/libs/            four local AARs
└── gradle/              wrapper
```

`app/libs/` ships:

| AAR | Role in the app |
|---|---|
| `kenburnsview-1.0.7.aar` | native Ken Burns pan/zoom for the player background |
| `lib-decoder-ffmpeg-release.aar` | FFmpeg audio decoder fallback |
| `libPhonograph-release.aar` | MediaStore reader (`uk.akane.libphonograph`) |
| `renderscript-toolkit-release.aar` | bitmap blur used by the background pipeline |

These are libraries, not source in this repository; the inventory marks them as
bundled archives.

## Application start-up

**`YosBasicApplication.kt`**

- Installs an uncaught-exception handler that opens `CrashActivity` with the stack
  trace and terminates the process.
- Initialises MMKV.
- Builds a Gson instance with a custom `UriTypeAdapter` and registers
  `ComposeDataSaver` converters for `Folder`, `PlayList`, `IntArray`,
  `YosMediaItem` and `YosStringWrapper`.

**`MainActivity.kt`** (≈94 KB) is the single Compose host:

- edge-to-edge window, splash and runtime permissions (including notifications);
- shared-element transitions between the mini player and full player;
- the draggable/paged player sheet;
- Bluetooth audio connection state (`rememberBluetoothAudioConnected`);
- back handling that returns to Now Playing;
- navigation between Home, Library, Settings and the full player.

`BaseActivity` provides the shared activity setup; `CrashActivity` renders the crash
text.

## Playback

**`code/MediaController.kt`** (≈58 KB) is a singleton facade around Media3:

- `ensureInitialized(context)` connects a `MediaController` to
  `YosPlaybackService`.
- Holds the queue model: `mainMusicList`, `playingMusicList`,
  `nextInQueueMusicList`, `historyMusicList`, `orderedPlayingMusicList`,
  `queueShuffleEnabled` and `musicPlaying`.
- Persists/restores queue state (`saveQueueState`, `saveDataWithDelay`).
- Handles next/previous, shuffle/repeat, queue drag-reordering
  (`moveNextInQueueItemDuringDrag`, `moveUpNextItemDuringDrag`) and
  `syncQueueStateFromController`.
- Integrates the lyric APIs and status-bar lyric updates
  (`stopStatusBarLyricUpdater`).
- Defines `YosPlaybackService : MediaSessionService` at the bottom of the file,
  including `onGetSession` and custom notification buttons.

**`code/utils/player/FadeExo.kt`** — fade-to-pause/fade-to-play with caller-supplied
duration, used by both the play button and the sleep timer.

**`code/SleepTimer.kt`** — in-memory sleep timer for the Now Playing menu. Process
death cancels it; there is no AlarmManager persistence.

**`code/VolumeChangeReceiver.kt`**, `SystemMediaControlResolver.kt` and
`YosRenderFactory.kt` handle volume keys, media-button routing and the renderer
factory.

## Library and data

**`data/libraries/MusicLibrary.kt`** is the local library layer:

- Scans MediaStore with `uk.akane.libphonograph.reader.Reader`, configured through
  `ReaderConfiguration`.
- Defines the main models: `YosMediaItem`, `PlayListV1`, `PlayStatus`, `Folder`,
  `Time`, plus conversion extensions `toMediaItem` / `toYosMediaItem` and the
  `thumb`/`uri` helpers.
- Persists library and queue state with MMKV + ComposeDataSaver; `PlayListV1` is
  `Parcelable` and stores main/playing/next/history lists and their URI fallbacks.

Other data files:

| File | Purpose |
|---|---|
| `ArtistLibrary.kt` | artist grouping/normalisation |
| `PlayListLibrary.kt` | playlist model and operations |
| `MediaItemExtra.kt`, `YosMediaItemExtra.kt` | extras attached to Media3 items |
| `SettingLibrary.kt` | every persisted setting (see below) |
| `ListenStatsLibrary.kt` | listening statistics storage |
| `data/models/*` | `MainViewModel`, `MediaViewModel`, `ImageViewModel` state holders |
| `data/objects/*` | process-wide singletons (`MediaViewModelObject`, etc.) |

## UI

Compose screens live in `ui/pages/`:

| Screen | Files |
|---|---|
| Home | `Home.kt`, `HomeNav.kt` — recommendations, recently played songs/albums |
| Now Playing | `NowPlaying.kt` (≈122 KB) — artwork, lyrics, queue, controls, volume, AirPlay/Bluetooth affordances |
| Library | `library/Library.kt`, `NormalMusic.kt`, `MusicList.kt`, `MusicDetailPage.kt`, `Item.kt`, `LibraryDivider.kt` |
| Albums | `library/albums/AlbumInfo.kt`, `LocalAlbums.kt` |
| Artists | `library/artists/ArtistInfo.kt`, `ArtistSongs.kt`, `LocalArtists.kt` |
| Playlists | `library/playlists/PlayLists.kt`, `PlayListEditModal.kt`, `PlayListOverflowSheet.kt`, `PlayListSearch.kt`, `PlayListAutoCover.kt`, `PendingPlayListDeletion.kt`, `PlayListSortPreference.kt`, `UndoSnackbarHost.kt` |
| Settings | `settings/Settings.kt` and the `settings/**` sub-pages |
| Statistics | `Stats.kt`, `StatsLists.kt` |

Navigation is route-based (`ui/NavHost.kt` defines `UI.HomePage`, `UI.Library`,
`UI.NormalMusic`, album/artist/playlist routes and `UI.Settings.*`). The mini player
is part of `MainActivity`; Now Playing is a full-screen page with shared-element
artwork transitions.

Reusable widgets:

- `ui/widgets/YosLyricView.kt` (≈70 KB) — the lyric renderer.
- `ui/widgets/basic/*` — title bars, bottom navigator, menu blur, shadow image,
  rolling numbers, pickers, text fields, bottom sheets, wrappers.
- `ui/widgets/effects/*` — `YosFloatingLight` (background), `OverlayEffect`,
  `DropShadow`.
- `ui/widgets/audio/MusicQualityIndicator.kt` — lossless/quality badges.
- `ui/widgets/playlist/PlayListPickerSheet.kt`,
  `ui/widgets/sleeptimer/SleepTimerSheet.kt`.

## Lyrics

The lyric engine is split into parser and renderer:

| File | Role |
|---|---|
| `code/utils/lrc/YosLrcFactory.kt` | parses the supported LRC variants into timed lines |
| `code/utils/lrc/YosTtmlFactory.kt` | parses TTML (including word/syllable timing) |
| `code/utils/lrc/YosParsedLyricData.kt` | parsed lyric model |
| `code/utils/lrc/YosAnimationConfig.kt` | animation toggles for `YosLyricView` |
| `code/utils/lrc/YosUIConfig.kt` | rarely changed UI options |
| `code/utils/lrc/YosMediaEvent.kt` | media-event interface for the lyric view |
| `ui/widgets/YosLyricView.kt` | Compose renderer: line scrolling, blur, translation, karaoke progress |

`docs/LyricFormatSupport_en.md` (in the upstream repo, listed in the inventory)
documents the exact lyric-format support. Tests exist for TTML parsing and animated
artwork (`YosTtmlFactoryTest`, `AnimatedArtworkLibraryTest`).

The app also integrates external lyric providers: Lyric Getter API, Super Lyric and
notification-ticker lyrics, exposed through `MediaController`.

## Background and visual effects

**`ui/widgets/effects/YosFloatingLightView.kt`** builds the now-playing backdrop: a
Coil-loaded cover, saturation ×3, dark overlays, `BitmapResolver.blurBitmap(…, 25)`
and either a static image or a `KenBurnsView` with 12-second random transitions. The
detailed method and its ChoraLiqGla adaptation are in
[../background-methods.md](../background-methods.md).

Related effects:

- `AnimatedArtworkLibrary.kt` + `AnimatedAlbumCoverOverlay.kt` — animated album
  covers, with a blacklist and per-setting controls.
- `MenuBlur.kt`, `Haze` dependency — blur behind menus/bars.
- `OverlayEffect.kt`, `DropShadow.kt` — overlay and shadow helpers.
- `overscroll_core/OverScroll.kt` — custom overscroll physics used by scrolling
  containers.

## Settings

`data/libraries/SettingLibrary.kt` is the single settings registry. Values are
`ComposeDataSaver` states backed by MMKV, keyed by strings such as
`settings_performance_ui_nowplaying_background_effect` (default **false**),
`settings_performance_lyric_blur_effect` and `settings_performance_ui_blur_effect`.

The settings UI is grouped under `ui/pages/settings/`:

- `audio/exoPlayer/` — ExoPlayer and MediaCodec options.
- `performance/` — lyric blur, notification, UI blur.
- `performance/userinterface/` — animated covers, blacklist, screen corners.
- `library/` — artist splitting and library overview.
- `extend/statusBarLyric/` — Lyric Getter integration.
- `others/About.kt`.

## Resources and assets

- `app/src/main/res/drawable/` — 88 XML/vector icons and placeholders.
- `app/src/main/res/values/` — strings, colours, themes, dimens, attrs.
- `values-night/`, `values-ja/`, `values-zh-rCN/`, `values-zh-rTW/` — dark theme and
  translations.
- `mipmap-*` — launcher icons at five densities.
- `docs/pics/` — README screenshots and banner.
- `app/src/main/res/xml/` — backup and data-extraction rules.

Every one of these paths, including each icon and each translation file, has a row in
[inventory.md](inventory.md).

## Build and tests

```bash
./gradlew assembleRelease
```

The project uses Groovy Gradle and a `google()` / `mavenCentral()` / JitPack setup.
Unit tests are under `app/src/test/java/yos/music/player/`; the useful ones are
`YosTtmlFactoryTest` and `AnimatedArtworkLibraryTest`. Instrumentation has only the
generated example test.

## Key files, in reading order

1. `YosBasicApplication.kt` — MMKV, Gson, crash handling.
2. `MainActivity.kt` — Compose host, player sheet, permissions, navigation.
3. `code/MediaController.kt` — playback service, queue, notification, lyric APIs.
4. `data/libraries/MusicLibrary.kt` — MediaStore library and models.
5. `data/libraries/SettingLibrary.kt` — all persisted settings.
6. `ui/pages/NowPlaying.kt` — the full player.
7. `ui/widgets/YosLyricView.kt` — lyric rendering.
8. `code/utils/lrc/YosLrcFactory.kt` and `YosTtmlFactory.kt` — lyric parsing.
9. `ui/widgets/effects/YosFloatingLightView.kt` — the background.
10. `ui/pages/library/` — songs, albums, artists and playlists.

For every other path, see [inventory.md](inventory.md).
