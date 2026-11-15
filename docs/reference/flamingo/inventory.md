# Flamingo — complete file inventory

Repository: <https://github.com/shouryadixitisverycool/Flamingo>  
Branch: `master` at commit `316df89`  
Tracked files documented here: **273** (13.8 MB).

Every path reported by `git ls-files` at the pinned commit appears exactly once
below. Paths are grouped by their parent directory. “Contents” is based on the
file's KDoc/declarations for source, its root resource type for XML, and its
actual format/dimensions for binary assets.

Generated 2026-11-15.

---

## Repository root

| File | Type | What it contains |
|---|---|---|
| `.gitignore` | Git | Git ignore rules |
| `LICENSE` | License | GNU General Public License v3.0 text |
| `README.md` | Markdown | “Flamingo” — Flamingo is a local music player for Android, styled after Apple Music, and is currently in development. Interface |
| `build.gradle` | Gradle | root module build script; plugins: com.android.application, com.android.library, org.jetbrains.kotlin.android, org.jetbrains.kotlin.plugin.compose; 0 dependency lines |
| `gradle.properties` | Properties | Gradle/project properties (org.gradle.jvmargs, android.useAndroidX, kotlin.code.style, android.nonTransitiveRClass) |
| `gradlew` | Script | Gradle wrapper launcher (Unix) |
| `gradlew.bat` | BAT | Project file |
| `settings.gradle` | Gradle | root module build script; 0 dependency lines |
| `stability_config.conf` | Config | Tool configuration file |

## `.idea/`

| File | Type | What it contains |
|---|---|---|
| `.idea/.gitignore` | Git | Git ignore rules |
| `.idea/gradle.xml` | XML | XML document rooted at `project` |
| `.idea/migrations.xml` | XML | XML document rooted at `project` |
| `.idea/misc.xml` | XML | XML document rooted at `project` |

## `app/`

| File | Type | What it contains |
|---|---|---|
| `app/.gitignore` | Git | Git ignore rules |
| `app/build.gradle` | Gradle | app module build script; plugins: com.android.application, org.jetbrains.kotlin.android, kotlin-parcelize, org.jetbrains.kotlin.plugin.compose; 36 dependency lines |
| `app/proguard-rules.pro` | ProGuard | R8/ProGuard keep and shrinking rules |

## `app/libs/`

| File | Type | What it contains |
|---|---|---|
| `app/libs/kenburnsview-1.0.7.aar` | AAR | Bundled Android library archive with classes.jar (9 entries) |
| `app/libs/lib-decoder-ffmpeg-release.aar` | AAR | Bundled Android library archive with classes.jar (14 entries) |
| `app/libs/libPhonograph-release.aar` | AAR | Bundled Android library archive with classes.jar (8 entries) |
| `app/libs/renderscript-toolkit-release.aar` | AAR | Bundled Android library archive with classes.jar (18 entries) |

## `app/src/androidTest/java/yos/music/player/`

| File | Type | What it contains |
|---|---|---|
| `app/src/androidTest/java/yos/music/player/ExampleInstrumentedTest.kt` | Kotlin | `yos.music.player` — Instrumented test, which will execute on an Android device. See [testing documentation](http://d.android.com/tools/testing). |

## `app/src/main/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/AndroidManifest.xml` | XML | Manifest: 11 permissions, 2 activities, 1 services |
| `app/src/main/ic_launcher-playstore.png` | PNG | PNG image, 512×512 |

## `app/src/main/java/yos/music/player/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/BaseActivity.kt` | Kotlin | `yos.music.player` — Declares class `BaseActivity`. |
| `app/src/main/java/yos/music/player/CrashActivity.kt` | Kotlin | `yos.music.player` — Declares class `CrashActivity`. |
| `app/src/main/java/yos/music/player/MainActivity.kt` | Kotlin | `yos.music.player` — Declares property `mediaController`, class `MainActivity`, function `rememberBluetoothAudioConnected`, function `BluetoothDevice`, function `readFile`. |
| `app/src/main/java/yos/music/player/YosBasicApplication.kt` | Kotlin | `yos.music.player` — Declares class `YosBasicApplication`, class `ImmutableListTypeAdapter`, class `UriTypeAdapter`, class `UriSerializer`, class `UriDeserializer`. |

## `app/src/main/java/yos/music/player/code/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/code/AnimatedArtworkLibrary.kt` | Kotlin | `yos.music.player.code` — Declares object `AnimatedArtworkLibrary`. |
| `app/src/main/java/yos/music/player/code/AudioMetadataUtils.kt` | Kotlin | `yos.music.player.code` — Declares object `AudioMetadataUtils`. |
| `app/src/main/java/yos/music/player/code/ListenHistoryManager.kt` | Kotlin | `yos.music.player.code` — Declares object `ListenHistoryManager`. |
| `app/src/main/java/yos/music/player/code/ListenHistoryTracker.kt` | Kotlin | `yos.music.player.code` — Declares class `ListenHistoryTracker`. |
| `app/src/main/java/yos/music/player/code/ListenStatsManager.kt` | Kotlin | `yos.music.player.code` — Declares object `ListenStatsManager`. |
| `app/src/main/java/yos/music/player/code/ListenStatsTracker.kt` | Kotlin | `yos.music.player.code` — Declares class `ListenStatsTracker`. |
| `app/src/main/java/yos/music/player/code/MediaController.kt` | Kotlin | `yos.music.player.code` — Declares object `MediaController`, class `YosPlaybackService`. |
| `app/src/main/java/yos/music/player/code/SleepTimer.kt` | Kotlin | `yos.music.player.code` — Sleep timer for the Now Playing overflow menu. Singleton; in-memory only (PRD §5.6.1 FR-ST-4 — no MMKV persistence, no AlarmManager). Process death cancels the timer. |
| `app/src/main/java/yos/music/player/code/SystemMediaControlResolver.kt` | Kotlin | `yos.music.player.code` — Declares class `SystemMediaControlResolver`. |
| `app/src/main/java/yos/music/player/code/VolumeChangeReceiver.kt` | Kotlin | `yos.music.player.code` — Declares class `VolumeChangeReceiver`. |
| `app/src/main/java/yos/music/player/code/YosRenderFactory.kt` | Kotlin | `yos.music.player.code` — Declares class `YosRenderFactory`. |

## `app/src/main/java/yos/music/player/code/utils/lrc/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/code/utils/lrc/YosAnimationConfig.kt` | Kotlin | `yos.music.player.code.utils.lrc` — YosLyricView 动画效果控制类 当前只有较少选项，等待后续开发 |
| `app/src/main/java/yos/music/player/code/utils/lrc/YosLrcFactory.kt` | Kotlin | `yos.music.player.code.utils.lrc` — Declares class `YosLrcFactory`, function `String`. |
| `app/src/main/java/yos/music/player/code/utils/lrc/YosMediaEvent.kt` | Kotlin | `yos.music.player.code.utils.lrc` — YosLyricView 媒体事件接口 之后可能在此接口实现更多其它功能 |
| `app/src/main/java/yos/music/player/code/utils/lrc/YosParsedLyricData.kt` | Kotlin | `yos.music.player.code.utils.lrc` — Declares class `YosParsedLyricData`. |
| `app/src/main/java/yos/music/player/code/utils/lrc/YosTtmlFactory.kt` | Kotlin | `yos.music.player.code.utils.lrc` — Declares class `YosTtmlFactory`. |
| `app/src/main/java/yos/music/player/code/utils/lrc/YosUIConfig.kt` | Kotlin | `yos.music.player.code.utils.lrc` — YosLyricView UI 控制类 该类仅管理在日常使用中不经常调节的选项，如果是 翻译开关、模糊效果 的开关请直接在调用 YosLyricView() 时修改 |

## `app/src/main/java/yos/music/player/code/utils/others/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/code/utils/others/BitmapResolver.kt` | Kotlin | `yos.music.player.code.utils.others` — Declares object `BitmapResolver`. |
| `app/src/main/java/yos/music/player/code/utils/others/SubStringX.kt` | Kotlin | `yos.music.player.code.utils.others` — Declares function `String`. |
| `app/src/main/java/yos/music/player/code/utils/others/Vibrator.kt` | Kotlin | `yos.music.player.code.utils.others` — Declares class `Vibrator`. |

## `app/src/main/java/yos/music/player/code/utils/player/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/code/utils/player/FadeExo.kt` | Kotlin | `yos.music.player.code.utils.player` — Fade-to-pause with a caller-supplied duration. Used by the sleep timer (PRD §5.6.4 FR-ST-9) so its fade can be longer than the play/pause button's 200ms default. |

## `app/src/main/java/yos/music/player/data/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/data/SettingOption.kt` | Kotlin | `yos.music.player.data` — Declares class `SettingOption`. |
| `app/src/main/java/yos/music/player/data/YosDataSaver.kt` | Kotlin | `yos.music.player.data` — Declares class `DataType`, property `NormalSaver`, property `SongListSaver`, property `SettingsSaver`, property `PlayListSaver`, class `YosDataSaver`. |

## `app/src/main/java/yos/music/player/data/libraries/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/data/libraries/ArtistLibrary.kt` | Kotlin | `yos.music.player.data.libraries` — Declares class `ArtistRelease`, class `ArtistSections`, object `ArtistLibrary`, function `YosMediaItem`, function `List`. |
| `app/src/main/java/yos/music/player/data/libraries/ListenStatsLibrary.kt` | Kotlin | `yos.music.player.data.libraries` — Declares class `ListenStatsEvent`, class `StatsPeriod`, class `StatsSummary`, class `StatsArtistEntry`, class `StatsAlbumEntry`, class `StatsTrackEntry`. |
| `app/src/main/java/yos/music/player/data/libraries/MediaItemExtra.kt` | Kotlin | `yos.music.player.data.libraries` — Declares property `defaultArtistsName`, property `defaultArtists`, property `defaultTitle`, property `defaultAlbum`, property `artistSplitExceptions`, property `MediaItem`. |
| `app/src/main/java/yos/music/player/data/libraries/MusicLibrary.kt` | Kotlin | `yos.music.player.data.libraries` — Declares class `Music`, class `Time`, class `PlayListV1`, class `PlayStatus`, class `Folder`, class `YosMediaItem`. |
| `app/src/main/java/yos/music/player/data/libraries/PlayListLibrary.kt` | Kotlin | `yos.music.player.data.libraries` — Per PRD §5.5 the playlist model carries the metadata needed by the Edit Playlist modal (description, custom cover) and the Library list (pin state + manual order within the pinned block). |
| `app/src/main/java/yos/music/player/data/libraries/SettingLibrary.kt` | Kotlin | `yos.music.player.data.libraries` — 聆听统计 - 上次选择的时间段 |
| `app/src/main/java/yos/music/player/data/libraries/YosMediaItemExtra.kt` | Kotlin | `yos.music.player.data.libraries` — Declares property `YosMediaItem`. |

## `app/src/main/java/yos/music/player/data/models/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/data/models/ImageViewModel.kt` | Kotlin | `yos.music.player.data.models` — Declares class `ImageViewModel`. |
| `app/src/main/java/yos/music/player/data/models/MainViewModel.kt` | Kotlin | `yos.music.player.data.models` — Declares class `MainViewModel`. |
| `app/src/main/java/yos/music/player/data/models/MediaViewModel.kt` | Kotlin | `yos.music.player.data.models` — Declares class `MediaViewModel`. |

## `app/src/main/java/yos/music/player/data/objects/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/data/objects/LibraryObject.kt` | Kotlin | `yos.music.player.data.objects` — Identifier of the playlist whose contents are being shown in [yos.music.player.ui.pages.library.NormalMusic], or null if the page is showing some other list (Songs, Album, Artist, Folder). |
| `app/src/main/java/yos/music/player/data/objects/MainViewModelObject.kt` | Kotlin | `yos.music.player.data.objects` — Declares object `MainViewModelObject`. |
| `app/src/main/java/yos/music/player/data/objects/MediaViewModelObject.kt` | Kotlin | `yos.music.player.data.objects` — Declares object `MediaViewModelObject`. |
| `app/src/main/java/yos/music/player/data/objects/SettingOptionsPageObject.kt` | Kotlin | `yos.music.player.data.objects` — Declares object `SettingOptionsPageObject`. |

## `app/src/main/java/yos/music/player/ui/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/NavHost.kt` | Kotlin | `yos.music.player.ui` — Declares interface `UI`, property `ReturnToNowPlayingOnBackKey`, function `NavController`, function `getNavUri`. |

## `app/src/main/java/yos/music/player/ui/pages/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/pages/Home.kt` | Kotlin | `yos.music.player.ui.pages` — Declares function `Home`, function `RecommendCard`, function `List`, class `RecentlyPlayedDisplayItem`, function `buildRecentlyPlayedDisplayItems`, function `RecentlyPlayedCard`. |
| `app/src/main/java/yos/music/player/ui/pages/HomeNav.kt` | Kotlin | `yos.music.player.ui.pages` — Declares object `HomePage`, function `HomeNav`. |
| `app/src/main/java/yos/music/player/ui/pages/NowPlaying.kt` | Kotlin | `yos.music.player.ui.pages` — Now Playing overflow menu. Opened from the three-dots icon in [ActionButtonsRow]. Per PRD §5.3 FR-OM-8 this composable receives a *snapshot* of the song that was playing when the sheet was opened — it does not re-subscribe to [MediaController.musicPlaying] for |
| `app/src/main/java/yos/music/player/ui/pages/Stats.kt` | Kotlin | `yos.music.player.ui.pages` — Declares property `STATS_ROW_MAX_ITEMS`, property `STATS_LIVE_REFRESH_MS`, function `selectedStatsPeriod`, function `rememberStatsSnapshot`, function `Stats`, function `StatsContent`. |
| `app/src/main/java/yos/music/player/ui/pages/StatsLists.kt` | Kotlin | `yos.music.player.ui.pages` — Declares function `StatsArtists`, function `StatsAlbums`, function `StatsTracks`, function `StatsArtistListRow`, function `StatsAlbumListRow`, function `StatsTrackListRow`. |

## `app/src/main/java/yos/music/player/ui/pages/library/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/pages/library/Item.kt` | Kotlin | `yos.music.player.ui.pages.library` — Declares function `SmallLabelItem`. |
| `app/src/main/java/yos/music/player/ui/pages/library/Library.kt` | Kotlin | `yos.music.player.ui.pages.library` — Declares function `Library`. |
| `app/src/main/java/yos/music/player/ui/pages/library/LibraryDivider.kt` | Kotlin | `yos.music.player.ui.pages.library` — Declares function `LibraryDivider`. |
| `app/src/main/java/yos/music/player/ui/pages/library/MusicDetailPage.kt` | Kotlin | `yos.music.player.ui.pages.library` — Declares function `MusicDetailPage`, function `MusicDetailCircleButton`, function `MusicDetailActionPill`, function `RowScope`, function `MusicDetailPillDivider`, function `MusicDetailTopBar`. |
| `app/src/main/java/yos/music/player/ui/pages/library/MusicList.kt` | Kotlin | `yos.music.player.ui.pages.library` — Declares property `SongContextMenuEstimatedHeight`, function `SongContextMenu`, function `SongContextMenuPopup`, function `SongContextMenuCard`, function `SongContextMenuHeader`, function `SongContextPlaylistDropDown`. |
| `app/src/main/java/yos/music/player/ui/pages/library/NormalMusic.kt` | Kotlin | `yos.music.player.ui.pages.library` — Apply the playlist view's chosen sort (PRD FR-M-05). Manual leaves the order as-is — the caller already constructed [this] in the playlist's saved order (via [convertToSongList]). |

## `app/src/main/java/yos/music/player/ui/pages/library/albums/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/pages/library/albums/AlbumInfo.kt` | Kotlin | `yos.music.player.ui.pages.library.albums` — Declares class `AlbumSortOption`, function `AlbumInfo`, function `AlbumHeroArtwork`, function `AlbumSortSheet`, function `AlbumAddToPlaylistSheet`, function `AlbumAddToPlaylistContent`. |
| `app/src/main/java/yos/music/player/ui/pages/library/albums/LocalAlbums.kt` | Kotlin | `yos.music.player.ui.pages.library.albums` — Declares function `LocalAlbums`, function `LazyGridItemScope`. |

## `app/src/main/java/yos/music/player/ui/pages/library/artists/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/pages/library/artists/ArtistInfo.kt` | Kotlin | `yos.music.player.ui.pages.library.artists` — Declares function `ArtistInfo`, function `ArtistHeroArtwork`, function `ArtistSectionHeader`, function `ArtistReleaseRow`, function `ArtistReleaseCard`, function `ArtistOverflowHeader`. |
| `app/src/main/java/yos/music/player/ui/pages/library/artists/ArtistSongs.kt` | Kotlin | `yos.music.player.ui.pages.library.artists` — Declares function `ArtistSongs`, function `ArtistSongItem`. |
| `app/src/main/java/yos/music/player/ui/pages/library/artists/LocalArtists.kt` | Kotlin | `yos.music.player.ui.pages.library.artists` — Declares function `LocalArtists`, function `LazyItemScope`. |

## `app/src/main/java/yos/music/player/ui/pages/library/playlists/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/pages/library/playlists/PendingPlayListDeletion.kt` | Kotlin | `yos.music.player.ui.pages.library.playlists` — Cross-screen handoff for the optimistic "delete with undo" pattern (PRD FR-M-10). The detail page deletes the playlist immediately (so the playlist disappears from the UI right away) and stashes the original record here; |
| `app/src/main/java/yos/music/player/ui/pages/library/playlists/PlayListAutoCover.kt` | Kotlin | `yos.music.player.ui.pages.library.playlists` — Auto-generated 2×2 collage of the playlist's first 4 unique album arts (PRD FR-E-05). |
| `app/src/main/java/yos/music/player/ui/pages/library/playlists/PlayListEditModal.kt` | Kotlin | `yos.music.player.ui.pages.library.playlists` — Full-screen modal exposing every edit operation defined by PRD §5.3: - rename (FR-E-06) - description (FR-E-07) - cover carousel — custom photo pick + 2×2 auto-collage (FR-E-03, FR-E-05) - drag-to-reorder via long-press handle (FR-E-10) - multi-sele… |
| `app/src/main/java/yos/music/player/ui/pages/library/playlists/PlayListOverflowSheet.kt` | Kotlin | `yos.music.player.ui.pages.library.playlists` — Internal screens hosted by [PlayListOverflowSheet]. Pattern mirrors the NowPlaying overflow menu (single sheet, swapped body) so sub-screens transition in place without the close + reopen animation that two separate sheets would cause. PRD §5.2 FR-M… |
| `app/src/main/java/yos/music/player/ui/pages/library/playlists/PlayListSearch.kt` | Kotlin | `yos.music.player.ui.pages.library.playlists` — Fuzzy search for the in-playlist search bar (PRD §5.1). **Scope (FR-S-05):** every human-readable text metadata field present on [YosMediaItem] is matched against the query — title, artists, album, album artist, genre, composer, writer, author, year. |
| `app/src/main/java/yos/music/player/ui/pages/library/playlists/PlayListSortPreference.kt` | Kotlin | `yos.music.player.ui.pages.library.playlists` — Sort options available on the playlist detail page. PRD FR-M-05. "Manual" reflects the playlist's saved drag-reorder (or insertion) order — the canonical source of truth. |
| `app/src/main/java/yos/music/player/ui/pages/library/playlists/PlayLists.kt` | Kotlin | `yos.music.player.ui.pages.library.playlists` — Pin-aware variant of [PlayListItem] used for the Library list (PRD §5.4). Adds a small pin badge over the cover and a queue-style reorder handle for pinned rows while reorder mode is active. |
| `app/src/main/java/yos/music/player/ui/pages/library/playlists/UndoSnackbarHost.kt` | Kotlin | `yos.music.player.ui.pages.library.playlists` — Activity-level host for the playlist delete-undo snackbar. Rendered inline at the activity root so the undo window outlives any single page's lifecycle — the user can tap Library → Songs while the timer is running and the snackbar stays put. |

## `app/src/main/java/yos/music/player/ui/pages/settings/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/pages/settings/Divider.kt` | Kotlin | `yos.music.player.ui.pages.settings` — 设置区块的填充，一般在 ListHeader 上面 |
| `app/src/main/java/yos/music/player/ui/pages/settings/Item.kt` | Kotlin | `yos.music.player.ui.pages.settings` — Declares function `SelectItem`, function `SelectObjectItem`, function `LabelItem`, function `SwitchItem`, function `DefaultItem`, function `Switch`. |
| `app/src/main/java/yos/music/player/ui/pages/settings/ListHeader.kt` | Kotlin | `yos.music.player.ui.pages.settings` — Declares function `ListHeader`. |
| `app/src/main/java/yos/music/player/ui/pages/settings/SettingBackground.kt` | Kotlin | `yos.music.player.ui.pages.settings` — Declares function `SettingBackground`. |
| `app/src/main/java/yos/music/player/ui/pages/settings/Settings.kt` | Kotlin | `yos.music.player.ui.pages.settings` — Declares function `Settings`, function `safeStartActivity`, function `startWeb`. |
| `app/src/main/java/yos/music/player/ui/pages/settings/StatsDataSection.kt` | Kotlin | `yos.music.player.ui.pages.settings` — Declares function `StatsDataSection`, function `StatsClearConfirmDialog`, function `StatsImportModeDialog`, function `Modifier`. |

## `app/src/main/java/yos/music/player/ui/pages/settings/audio/exoPlayer/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/pages/settings/audio/exoPlayer/ExoPlayerSettings.kt` | Kotlin | `yos.music.player.ui.pages.settings.audio.exoPlayer` — Declares function `ExoPlayerSettings`. |
| `app/src/main/java/yos/music/player/ui/pages/settings/audio/exoPlayer/MediaCodec.kt` | Kotlin | `yos.music.player.ui.pages.settings.audio.exoPlayer` — Declares function `MediaCodec`. |

## `app/src/main/java/yos/music/player/ui/pages/settings/extend/statusBarLyric/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/pages/settings/extend/statusBarLyric/LyricGetter.kt` | Kotlin | `yos.music.player.ui.pages.settings.extend.statusBarLyric` — Declares function `LyricGetter`. |

## `app/src/main/java/yos/music/player/ui/pages/settings/library/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/pages/settings/library/ArtistSplitSetting.kt` | Kotlin | `yos.music.player.ui.pages.settings.library` — Declares function `ArtistSplitSetting`. |
| `app/src/main/java/yos/music/player/ui/pages/settings/library/Overview.kt` | Kotlin | `yos.music.player.ui.pages.settings.library` — Declares function `LibraryOverview`, function `LazyItemScope`. |

## `app/src/main/java/yos/music/player/ui/pages/settings/others/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/pages/settings/others/About.kt` | Kotlin | `yos.music.player.ui.pages.settings.others` — Declares function `About`. |

## `app/src/main/java/yos/music/player/ui/pages/settings/performance/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/pages/settings/performance/LyricSetting.kt` | Kotlin | `yos.music.player.ui.pages.settings.performance` — Declares function `LyricSetting`. |
| `app/src/main/java/yos/music/player/ui/pages/settings/performance/NotificationSetting.kt` | Kotlin | `yos.music.player.ui.pages.settings.performance` — Declares function `NotificationSetting`. |

## `app/src/main/java/yos/music/player/ui/pages/settings/performance/userinterface/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/pages/settings/performance/userinterface/AnimatedAlbumCoverBlacklistSetting.kt` | Kotlin | `yos.music.player.ui.pages.settings.performance.userinterface` — Declares function `AnimatedAlbumCoverBlacklistSetting`. |
| `app/src/main/java/yos/music/player/ui/pages/settings/performance/userinterface/AnimatedAlbumCoversSetting.kt` | Kotlin | `yos.music.player.ui.pages.settings.performance.userinterface` — Declares function `AnimatedAlbumCoversSetting`, function `formatAnimatedAlbumCoverCacheSize`. |
| `app/src/main/java/yos/music/player/ui/pages/settings/performance/userinterface/ScreenCornerSetDialog.kt` | Kotlin | `yos.music.player.ui.pages.settings.performance.userinterface` — Declares function `ScreenCornerSetDialog`. |
| `app/src/main/java/yos/music/player/ui/pages/settings/performance/userinterface/UserInterfaceSetting.kt` | Kotlin | `yos.music.player.ui.pages.settings.performance.userinterface` — Declares function `UserInterfaceSetting`. |

## `app/src/main/java/yos/music/player/ui/theme/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/theme/Color.kt` | Kotlin | `yos.music.player.ui.theme` — Declares property `primary`, property `primaryDark`, property `headline`, property `headlineDark`, property `background`, property `backgroundDark`. |
| `app/src/main/java/yos/music/player/ui/theme/DeviceRoundedCornerShape.kt` | Kotlin | `yos.music.player.ui.theme` — Declares function `DeviceRoundedCornerShape`, function `getCornerRadiusTop`. |
| `app/src/main/java/yos/music/player/ui/theme/Theme.kt` | Kotlin | `yos.music.player.ui.theme` — Declares property `DarkColorScheme`, property `LightColorScheme`, function `YosMusicTheme`. |
| `app/src/main/java/yos/music/player/ui/theme/Type.kt` | Kotlin | `yos.music.player.ui.theme` — Declares property `Typography`. |
| `app/src/main/java/yos/music/player/ui/theme/YosRoundedCornerShape.kt` | Kotlin | `yos.music.player.ui.theme` — Declares function `YosRoundedCornerShape`, function `FloatArray`. |

## `app/src/main/java/yos/music/player/ui/widgets/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/widgets/YosLyricView.kt` | Kotlin | `yos.music.player.ui.widgets` — YosLyricView 主控件 |

## `app/src/main/java/yos/music/player/ui/widgets/audio/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/widgets/audio/MusicQualityIndicator.kt` | Kotlin | `yos.music.player.ui.widgets.audio` — Declares function `MusicQualityIndicator`. |

## `app/src/main/java/yos/music/player/ui/widgets/basic/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/widgets/basic/AnimatedAlbumCoverOverlay.kt` | Kotlin | `yos.music.player.ui.widgets.basic` — Declares class `AnimatedAlbumCoverState`, function `rememberAnimatedAlbumCoverState`, function `BoxScope`. |
| `app/src/main/java/yos/music/player/ui/widgets/basic/BottomNavgator.kt` | Kotlin | `yos.music.player.ui.widgets.basic` — Declares class `NavItem`, function `BottomNavigator`, function `RowScope`. |
| `app/src/main/java/yos/music/player/ui/widgets/basic/MenuBlur.kt` | Kotlin | `yos.music.player.ui.widgets.basic` — Declares function `MenuBlurBackground`, function `BlurredMenuContainer`, class `MenuBlurView`, function `Context`. |
| `app/src/main/java/yos/music/player/ui/widgets/basic/ProfileButton.kt` | Kotlin | `yos.music.player.ui.widgets.basic` — Declares property `profilePictureUri`, function `ProfileButton`. |
| `app/src/main/java/yos/music/player/ui/widgets/basic/RollingNumber.kt` | Kotlin | `yos.music.player.ui.widgets.basic` — Declares function `RollingNumberText`. |
| `app/src/main/java/yos/music/player/ui/widgets/basic/ShadowImage.kt` | Kotlin | `yos.music.player.ui.widgets.basic` — Declares class `ImageQuality`, function `getSizeFromQuality`, function `ShadowImage`, function `ShadowImageWithCache`. |
| `app/src/main/java/yos/music/player/ui/widgets/basic/Title.kt` | Kotlin | `yos.music.player.ui.widgets.basic` — Optional externally-owned list state. Lift this when the caller needs to drive scrolling (e.g. the in-playlist pull-to-reveal search, PRD §5.1, scrolls past a hidden search field on first composition). |
| `app/src/main/java/yos/music/player/ui/widgets/basic/YosBottomSheetDialog.kt` | Kotlin | `yos.music.player.ui.widgets.basic` — Visibility is `internal` so sibling widget packages (e.g. the playlist picker, the sleep-timer sheet) can reuse the same styling internals — corner radius from [SettingsLibrary.ScreenCorner], edge-to-edge insets, scrim alpha, haptic-on-open — without duplicati |
| `app/src/main/java/yos/music/player/ui/widgets/basic/YosRoundColumn.kt` | Kotlin | `yos.music.player.ui.widgets.basic` — Declares property `defaultCorner`, property `defaultPadding`, function `RoundColumn`, function `LazyListScope`, class `RoundColumnLazyListScope`. |
| `app/src/main/java/yos/music/player/ui/widgets/basic/YosTextField.kt` | Kotlin | `yos.music.player.ui.widgets.basic` — Declares function `SearchTextField`. |
| `app/src/main/java/yos/music/player/ui/widgets/basic/YosWrapper.kt` | Kotlin | `yos.music.player.ui.widgets.basic` — Declares function `YosWrapper`. |

## `app/src/main/java/yos/music/player/ui/widgets/effects/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/widgets/effects/DropShadow.kt` | Kotlin | `yos.music.player.ui.widgets.effects` — Declares class `ShadowType`, function `Modifier`. |
| `app/src/main/java/yos/music/player/ui/widgets/effects/OverlayEffect.kt` | Kotlin | `yos.music.player.ui.widgets.effects` — 在 Compose 上实现 Add 的效果。 注意：如果要使用 .alpha() 设置透明度，则必须在该 Modifier 之后，或者使用 .graphicLayer { this.alpha = 0.5f } 类此。 —— By Yos-X |
| `app/src/main/java/yos/music/player/ui/widgets/effects/YosFloatingLightView.kt` | Kotlin | `yos.music.player.ui.widgets.effects` — Declares class `Option`, function `YosFloatingLight`, function `imageResolve`. |

## `app/src/main/java/yos/music/player/ui/widgets/playlist/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/widgets/playlist/PlayListPickerSheet.kt` | Kotlin | `yos.music.player.ui.widgets.playlist` — Reusable bottom sheet for picking an existing playlist to add a song to, OR creating a new playlist. Modes: - **Picker mode** (`songToAdd != null`): shows "Create New Playlist" at the top followed by the user's existing playlists. |

## `app/src/main/java/yos/music/player/ui/widgets/sleeptimer/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/yos/music/player/ui/widgets/sleeptimer/SleepTimerSheet.kt` | Kotlin | `yos.music.player.ui.widgets.sleeptimer` — Sleep timer bottom sheet. Three internal screens: 1. **Preset list** (default) — radio-style preset choices, active status row on top if a timer is running, fade selector at the bottom. 2. **Custom duration** — two hour/minute wheel pickers + Start. 3. |

## `app/src/main/res/drawable/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/drawable/arrow_up_down.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/button_icon_play.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/button_icon_shuffle.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/flamingo_icon.xml` | XML | `vector` drawable (192dp×192dp) |
| `app/src/main/res/drawable/flamingo_icon_notification.png` | PNG | PNG image, 50×50 |
| `app/src/main/res/drawable/ic_action_add.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_action_camera.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_action_check.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_action_close.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_action_delete.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_action_drag_handle.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_action_edit.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_action_favorite.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_action_favorited.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_action_more.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_action_next.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_action_pin.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_action_play_next.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_action_search.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_action_sort.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_action_unpin.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_back.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_earphone.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_launcher_background.xml` | XML | `vector` drawable (108dp×108dp) |
| `app/src/main/res/drawable/ic_launcher_foreground.xml` | XML | `vector` drawable (108dp×108dp) |
| `app/src/main/res/drawable/ic_library_link_icon_album.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_library_link_icon_artists.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_library_link_icon_playlists.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_library_link_icon_songs.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_mini_repeat.xml` | XML | `vector` drawable (128dp×128dp) |
| `app/src/main/res/drawable/ic_mini_repeat_off.xml` | XML | `vector` drawable (128dp×128dp) |
| `app/src/main/res/drawable/ic_mini_repeat_one.xml` | XML | `vector` drawable (128dp×128dp) |
| `app/src/main/res/drawable/ic_mini_shuffle.xml` | XML | `vector` drawable (128dp×128dp) |
| `app/src/main/res/drawable/ic_mini_shuffle_off.xml` | XML | `vector` drawable (128dp×128dp) |
| `app/src/main/res/drawable/ic_nowplaying_airplay.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_dolby_atmos.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_favorite.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_favorited.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_fforward.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_lyrics.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_lyricson.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_more.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_more_fill.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_mp_fforward.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_mp_pause.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_nowplaying_mp_play.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_pause.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_nowplaying_play.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_queue.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_queueon.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_repeat.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_repeatone.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_rewind.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_shuffle.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_translate.xml` | XML | `vector` drawable (200dp×200dp) |
| `app/src/main/res/drawable/ic_nowplaying_translateon.xml` | XML | `vector` drawable (200dp×200dp) |
| `app/src/main/res/drawable/ic_nowplaying_volume.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_volume_full.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_quality_lossless.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_queue_reorder.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_repeat.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_repeat_off.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_repeat_one.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_setting_moon.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_settings_check.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_shuffle.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_shuffle_off.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_song_context_album.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_song_context_artist.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_song_context_playlist.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_speaker.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_swipe_delete.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_swipe_queue.xml` | XML | `vector` drawable (24dp×20.13dp) |
| `app/src/main/res/drawable/ic_tips_minus.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_tips_plus.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_tips_roundcorner.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_uitabbar_library.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_uitabbar_search.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_uitabbar_stats.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/media3_notification_pause.xml` | XML | `vector` drawable (128dp×128dp) |
| `app/src/main/res/drawable/media3_notification_play.xml` | XML | `vector` drawable (128dp×128dp) |
| `app/src/main/res/drawable/media3_notification_seek_to_next.xml` | XML | `vector` drawable (128dp×128dp) |
| `app/src/main/res/drawable/media3_notification_seek_to_previous.xml` | XML | `vector` drawable (128dp×128dp) |
| `app/src/main/res/drawable/placeholder_folder.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/placeholder_music_default_artwork.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/placeholder_playlist_default.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/placeholder_playlist_new.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/songcredits_monogram_person.xml` | XML | `vector` drawable |

## `app/src/main/res/drawable-nodpi/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/drawable-nodpi/placeholder_playlist_fav.webp` | WEBP | RIFF (little-endian) data, Web/P image |

## `app/src/main/res/mipmap-anydpi-v26/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` | XML | XML document rooted at `adaptive` |
| `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml` | XML | XML document rooted at `adaptive` |

## `app/src/main/res/mipmap-hdpi/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/mipmap-hdpi/ic_launcher.png` | PNG | PNG image, 72×72 |
| `app/src/main/res/mipmap-hdpi/ic_launcher_foreground.png` | PNG | PNG image, 162×162 |
| `app/src/main/res/mipmap-hdpi/ic_launcher_round.png` | PNG | PNG image, 72×72 |

## `app/src/main/res/mipmap-mdpi/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/mipmap-mdpi/ic_launcher.png` | PNG | PNG image, 48×48 |
| `app/src/main/res/mipmap-mdpi/ic_launcher_foreground.png` | PNG | PNG image, 108×108 |
| `app/src/main/res/mipmap-mdpi/ic_launcher_round.png` | PNG | PNG image, 48×48 |

## `app/src/main/res/mipmap-xhdpi/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/mipmap-xhdpi/ic_launcher.png` | PNG | PNG image, 96×96 |
| `app/src/main/res/mipmap-xhdpi/ic_launcher_foreground.png` | PNG | PNG image, 216×216 |
| `app/src/main/res/mipmap-xhdpi/ic_launcher_round.png` | PNG | PNG image, 96×96 |

## `app/src/main/res/mipmap-xxhdpi/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/mipmap-xxhdpi/ic_launcher.png` | PNG | PNG image, 144×144 |
| `app/src/main/res/mipmap-xxhdpi/ic_launcher_foreground.png` | PNG | PNG image, 324×324 |
| `app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png` | PNG | PNG image, 144×144 |

## `app/src/main/res/mipmap-xxxhdpi/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/mipmap-xxxhdpi/ic_launcher.png` | PNG | PNG image, 192×192 |
| `app/src/main/res/mipmap-xxxhdpi/ic_launcher_foreground.png` | PNG | PNG image, 432×432 |
| `app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png` | PNG | PNG image, 192×192 |

## `app/src/main/res/values/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values/attrs.xml` | XML | Resource values: no standard resources |
| `app/src/main/res/values/colors.xml` | XML | Resource values: 7 color; keys include `purple_200`, `purple_500`, `purple_700`, `teal_200`, `teal_700` |
| `app/src/main/res/values/ic_launcher_background.xml` | XML | Resource values: 1 color; keys include `ic_launcher_background` |
| `app/src/main/res/values/strings.xml` | XML | Resource values: 265 string; keys include `profile_picture`, `app_name`, `permission_grant_title`, `permission_grant_subtitle`, `permission_grant_button_positive` |
| `app/src/main/res/values/themes.xml` | XML | Resource values: 2 style; keys include `Theme.Flamingo`, `SplashTheme` |

## `app/src/main/res/values-ja/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-ja/strings.xml` | XML | Resource values (ja): 262 string; keys include `permission_grant_title`, `permission_grant_subtitle`, `permission_grant_button_positive`, `permission_grant_button_negative`, `page_home_title` |

## `app/src/main/res/values-night/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-night/colors.xml` | XML | Resource values (night): no standard resources |
| `app/src/main/res/values-night/themes.xml` | XML | Resource values (night): 2 style; keys include `Theme.Flamingo`, `SplashTheme` |

## `app/src/main/res/values-zh-rCN/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-zh-rCN/strings.xml` | XML | Resource values (zh-rCN): 261 string; keys include `permission_grant_title`, `permission_grant_subtitle`, `permission_grant_button_positive`, `permission_grant_button_negative`, `page_home_title` |

## `app/src/main/res/values-zh-rTW/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-zh-rTW/strings.xml` | XML | Resource values (zh-rTW): 261 string; keys include `permission_grant_title`, `permission_grant_subtitle`, `permission_grant_button_positive`, `permission_grant_button_negative`, `page_home_title` |

## `app/src/main/res/xml/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/xml/backup_rules.xml` | XML | XML document rooted at `full` |
| `app/src/main/res/xml/data_extraction_rules.xml` | XML | XML document rooted at `data` |

## `app/src/test/java/yos/music/player/`

| File | Type | What it contains |
|---|---|---|
| `app/src/test/java/yos/music/player/ExampleUnitTest.kt` | Kotlin | `yos.music.player` — Example local unit test, which will execute on the development machine (host). See [testing documentation](http://d.android.com/tools/testing). |

## `app/src/test/java/yos/music/player/code/`

| File | Type | What it contains |
|---|---|---|
| `app/src/test/java/yos/music/player/code/AnimatedArtworkLibraryTest.kt` | Kotlin | `yos.music.player.code` — Declares class `AnimatedArtworkLibraryTest`. |

## `app/src/test/java/yos/music/player/code/utils/lrc/`

| File | Type | What it contains |
|---|---|---|
| `app/src/test/java/yos/music/player/code/utils/lrc/YosTtmlFactoryTest.kt` | Kotlin | `yos.music.player.code.utils.lrc` — Declares class `YosTtmlFactoryTest`. |

## `docs/`

| File | Type | What it contains |
|---|---|---|
| `docs/LyricFormatSupport_en.md` | Markdown | “Lyric Formats Supported by Flamingo” — Last revised: June 19, 2026 |

## `docs/pics/`

| File | Type | What it contains |
|---|---|---|
| `docs/pics/1.png` | PNG | PNG image, 1080×2315 |
| `docs/pics/2.png` | PNG | PNG image, 1080×2315 |
| `docs/pics/3.png` | PNG | PNG image, 1080×2302 |
| `docs/pics/4.png` | PNG | PNG image, 1080×2317 |
| `docs/pics/5.png` | PNG | PNG image, 1080×2315 |
| `docs/pics/6.jpg` | JPG | JPEG image, 1080×2262 |
| `docs/pics/main_banner.jpg` | JPG | JPEG image, 1×1 |
| `docs/pics/overflow-menu-after.png` | PNG | PNG image, 1080×2412 |
| `docs/pics/overflow-menu-before.png` | PNG | PNG image, 1080×2412 |

## `docs/pics/search-bar-header/`

| File | Type | What it contains |
|---|---|---|
| `docs/pics/search-bar-header/albums-sticky-search.png` | PNG | PNG image, 1080×2412 |
| `docs/pics/search-bar-header/artists-sticky-search.png` | PNG | PNG image, 1080×2412 |
| `docs/pics/search-bar-header/playlist-search-filtered.png` | PNG | PNG image, 1080×2412 |
| `docs/pics/search-bar-header/songs-sticky-search.png` | PNG | PNG image, 1080×2412 |

## `gradle/wrapper/`

| File | Type | What it contains |
|---|---|---|
| `gradle/wrapper/gradle-wrapper.jar` | JAR | Bundled Java archive |
| `gradle/wrapper/gradle-wrapper.properties` | Properties | Gradle/project properties (distributionBase, distributionPath, distributionUrl, zipStoreBase) |

## `overscroll_core/`

| File | Type | What it contains |
|---|---|---|
| `overscroll_core/.gitignore` | Git | Git ignore rules |
| `overscroll_core/build.gradle` | Gradle | overscroll_core module build script; plugins: com.android.library, org.jetbrains.kotlin.android, org.jetbrains.kotlin.plugin.compose; 3 dependency lines |
| `overscroll_core/proguard-rules.pro` | ProGuard | R8/ProGuard keep and shrinking rules |

## `overscroll_core/src/main/`

| File | Type | What it contains |
|---|---|---|
| `overscroll_core/src/main/AndroidManifest.xml` | XML | Manifest: 0 permissions, 0 activities, 0 services |

## `overscroll_core/src/main/java/com/cormor/overscroll/core/`

| File | Type | What it contains |
|---|---|---|
| `overscroll_core/src/main/java/com/cormor/overscroll/core/OverScroll.kt` | Kotlin | `com.cormor.overscroll.core` — A parabolic rolling easing curve. When rolling in the same direction, the farther away from 0, the greater the "resistance"; the closer to 0, the smaller the "resistance"; |

## `overscroll_core/src/main/res/values/`

| File | Type | What it contains |
|---|---|---|
| `overscroll_core/src/main/res/values/strings.xml` | XML | Resource values: 1 string; keys include `app_name` |

