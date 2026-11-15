# Accord — complete file inventory

Repository: <https://github.com/emylfy/Accord>  
Branch: `alpha` at commit `653cb86`  
Tracked files documented here: **432** (6.3 MB).

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
| `build.gradle.kts` | Gradle | root module build script; plugins: com.android.application, com.android.library, com.google.devtools.ksp; 1 dependency lines |
| `gradle.properties` | Properties | Gradle/project properties (org.gradle.jvmargs, org.gradle.parallel, android.useAndroidX, kotlin.code.style) |
| `gradlew` | Script | Gradle wrapper launcher (Unix) |
| `gradlew.bat` | BAT | Project file |
| `readme.md` | Markdown | “Accord” —  This fork is no longer maintained. Originally I assumed [AccordLegacy](https://github.com/FoedusProgramme/AccordLegacy) was a dead project, which is why I started this fork. That turned out to be wrong — FoedusProgramme |
| `settings.gradle.kts` | Gradle | root module build script; plugins: org.gradle.toolchains.foojay-resolver-convention; 0 dependency lines |

## `.github/ISSUE_TEMPLATE/`

| File | Type | What it contains |
|---|---|---|
| `.github/ISSUE_TEMPLATE/bug_report.md` | Markdown | Markdown —  |
| `.github/ISSUE_TEMPLATE/feature_request.md` | Markdown | Markdown —  |

## `.github/workflows/`

| File | Type | What it contains |
|---|---|---|
| `.github/workflows/android.yml` | YAML | CI/configuration YAML |

## `app/`

| File | Type | What it contains |
|---|---|---|
| `app/.gitignore` | Git | Git ignore rules |
| `app/build.gradle.kts` | Gradle | app module build script; plugins: com.android.application, com.google.devtools.ksp; 24 dependency lines |
| `app/proguard-rules.pro` | ProGuard | R8/ProGuard keep and shrinking rules |

## `app/src/main/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/AndroidManifest.xml` | XML | Manifest: 9 permissions, 2 activities, 1 services |
| `app/src/main/ic_launcher-playstore.png` | PNG | PNG image, 512×512 |

## `app/src/main/java/com/google/android/material/slider/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/com/google/android/material/slider/BaseOverlaySlider.java` | Java | `com.google.android.material.slider` — The slider can function either as a continuous slider, or as a discrete slider. The mode of operation is controlled by the value of the step size. |
| `app/src/main/java/com/google/android/material/slider/OverlaySlider.java` | Java | `com.google.android.material.slider` — A widget that allows picking a value within a given range by sliding a thumb along a horizontal line. <p>{@inheritDoc} <p>{@code android:value}: <b>Optional.</b> The initial value of the slider. |

## `app/src/main/java/org/akanework/gramophone/logic/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/logic/GramophoneApplication.kt` | Kotlin | `org.akanework.gramophone.logic` — GramophoneApplication |
| `app/src/main/java/org/akanework/gramophone/logic/GramophoneExtensions.kt` | Kotlin | `org.akanework.gramophone.logic` — Declares function `Player`, function `MediaItem`, function `Activity`, function `Drawable`, function `TextView`, function `View`. |
| `app/src/main/java/org/akanework/gramophone/logic/GramophonePlaybackService.kt` | Kotlin | `org.akanework.gramophone.logic` — [GramophonePlaybackService] is a server service. It's using exoplayer2 as its player backend. |

## `app/src/main/java/org/akanework/gramophone/logic/comparators/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/logic/comparators/AlphaNumericComparator.kt` | Kotlin | `org.akanework.gramophone.logic.comparators` — An alphanumeric comparator for comparing strings in a human readable format. It uses a combination numeric and alphabetic comparisons to compare two strings. This class uses standard Java classes, independent of 3rd party libraries. |
| `app/src/main/java/org/akanework/gramophone/logic/comparators/SupportComparator.kt` | Kotlin | `org.akanework.gramophone.logic.comparators` — Declares class `SupportComparator`. |

## `app/src/main/java/org/akanework/gramophone/logic/data/db/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/logic/data/db/AppDatabase.kt` | Kotlin | `org.akanework.gramophone.logic.data.db` — Declares property `APP_DATABASE_FILE_NAME`, class `AppDatabase`. |

## `app/src/main/java/org/akanework/gramophone/logic/data/db/dao/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/logic/data/db/dao/MediaItemDao.kt` | Kotlin | `org.akanework.gramophone.logic.data.db.dao` — Add song to database. |
| `app/src/main/java/org/akanework/gramophone/logic/data/db/dao/PlaylistDao.kt` | Kotlin | `org.akanework.gramophone.logic.data.db.dao` — Add a new playlist. |

## `app/src/main/java/org/akanework/gramophone/logic/data/db/entity/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/logic/data/db/entity/MediaItem.kt` | Kotlin | `org.akanework.gramophone.logic.data.db.entity` — Declares property `MEDIA_ITEM_TABLE_NAME`, class `MediaItem`. |
| `app/src/main/java/org/akanework/gramophone/logic/data/db/entity/MediaItemWithPlaylist.kt` | Kotlin | `org.akanework.gramophone.logic.data.db.entity` — A [mediaItem] contains many [playlists]. |
| `app/src/main/java/org/akanework/gramophone/logic/data/db/entity/Playlist.kt` | Kotlin | `org.akanework.gramophone.logic.data.db.entity` — Declares property `PLAYLIST_TABLE_NAME`, class `Playlist`. |
| `app/src/main/java/org/akanework/gramophone/logic/data/db/entity/PlaylistMediaItemCrossRef.kt` | Kotlin | `org.akanework.gramophone.logic.data.db.entity` — Declares property `PLAYLIST_MEDIA_ITEM_CROSS_REF_TABLE_NAME`, class `PlaylistMediaItemCrossRef`. |
| `app/src/main/java/org/akanework/gramophone/logic/data/db/entity/PlaylistWithMediaItem.kt` | Kotlin | `org.akanework.gramophone.logic.data.db.entity` — A [playlist] contains many [mediaItems]. |

## `app/src/main/java/org/akanework/gramophone/logic/ui/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/logic/ui/CenteredImageSpan.kt` | Kotlin | `org.akanework.gramophone.logic.ui` — Declares class `CenteredImageSpan`. |
| `app/src/main/java/org/akanework/gramophone/logic/ui/CoilCrossfade.kt` | Kotlin | `org.akanework.gramophone.logic.ui` — Update the [Drawable]'s bounds inside [targetBounds] preserving aspect ratio. |
| `app/src/main/java/org/akanework/gramophone/logic/ui/CustomSmoothScroller.java` | Java | `org.akanework.gramophone.logic.ui` — {@link RecyclerView.SmoothScroller} implementation which uses a {@link LinearInterpolator} until the target position becomes a child of the RecyclerView and then uses a {@link DecelerateInterpolator} to slowly approach to target position. |
| `app/src/main/java/org/akanework/gramophone/logic/ui/MyBottomSheetBehavior.kt` | Kotlin | `org.akanework.gramophone.logic.ui` — Declares class `MyBottomSheetBehavior`. |
| `app/src/main/java/org/akanework/gramophone/logic/ui/MyRecyclerView.kt` | Kotlin | `org.akanework.gramophone.logic.ui` — Declares class `MyRecyclerView`, class `DefaultItemHeightHelper`. |
| `app/src/main/java/org/akanework/gramophone/logic/ui/RecyclerViewHelper.kt` | Kotlin | `org.akanework.gramophone.logic.ui` — Declares class `RecyclerViewHelper`, function `interface`. |

## `app/src/main/java/org/akanework/gramophone/logic/utils/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/logic/utils/CalculationUtils.kt` | Kotlin | `org.akanework.gramophone.logic.utils` — [CalculationUtils] contains some methods for internal calculation. |
| `app/src/main/java/org/akanework/gramophone/logic/utils/CircularShuffleOrder.kt` | Kotlin | `org.akanework.gramophone.logic.utils` — This shuffle order will take "firstIndex" as first song and play all songs after it. |
| `app/src/main/java/org/akanework/gramophone/logic/utils/ColorUtils.kt` | Kotlin | `org.akanework.gramophone.logic.utils` — Declares object `ColorUtils`. |
| `app/src/main/java/org/akanework/gramophone/logic/utils/DatabaseUtils.kt` | Kotlin | `org.akanework.gramophone.logic.utils` — Declares object `DatabaseUtils`. |
| `app/src/main/java/org/akanework/gramophone/logic/utils/EnvUtils.kt` | Kotlin | `org.akanework.gramophone.logic.utils` — Declares object `EnvUtils`. |
| `app/src/main/java/org/akanework/gramophone/logic/utils/FileOpUtils.kt` | Kotlin | `org.akanework.gramophone.logic.utils` — Declares object `FileOpUtils`. |
| `app/src/main/java/org/akanework/gramophone/logic/utils/LastPlayedManager.kt` | Kotlin | `org.akanework.gramophone.logic.utils` — Declares class `LastPlayedManager`, function `runCallback`, class `SafeDelimitedStringConcat`, class `SafeDelimitedStringDecat`, object `PrefsListUtils`. |
| `app/src/main/java/org/akanework/gramophone/logic/utils/LrcUtils.kt` | Kotlin | `org.akanework.gramophone.logic.utils` — Declares object `LrcUtils`, class `UsltFrameDecoder`. |
| `app/src/main/java/org/akanework/gramophone/logic/utils/MediaStoreUtils.kt` | Kotlin | `org.akanework.gramophone.logic.utils` — [MediaStoreUtils] contains all the methods for reading from mediaStore. |
| `app/src/main/java/org/akanework/gramophone/logic/utils/RecommendationFactory.kt` | Kotlin | `org.akanework.gramophone.logic.utils` — Declares class `RecommendationFactory`. |

## `app/src/main/java/org/akanework/gramophone/logic/utils/exoplayer/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/logic/utils/exoplayer/EndedWorkaroundPlayer.kt` | Kotlin | `org.akanework.gramophone.logic.utils.exoplayer` — If player in STATE_ENDED is resumed, state will be STATE_READY, on play button press it will update to STATE_ENDED and only then media3 will wrap around playlist for us. |
| `app/src/main/java/org/akanework/gramophone/logic/utils/exoplayer/GramophoneExtractorsFactory.kt` | Kotlin | `org.akanework.gramophone.logic.utils.exoplayer` — Declares class `GramophoneExtractorsFactory`. |
| `app/src/main/java/org/akanework/gramophone/logic/utils/exoplayer/GramophoneMediaSourceFactory.kt` | Kotlin | `org.akanework.gramophone.logic.utils.exoplayer` — Declares class `GramophoneMediaSourceFactory`. |
| `app/src/main/java/org/akanework/gramophone/logic/utils/exoplayer/GramophoneRenderFactory.kt` | Kotlin | `org.akanework.gramophone.logic.utils.exoplayer` — Declares class `GramophoneRenderFactory`. |

## `app/src/main/java/org/akanework/gramophone/ui/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/ui/BugHandlerActivity.kt` | Kotlin | `org.akanework.gramophone.ui` — BugHandlerActivity: An activity makes crash reporting easier. |
| `app/src/main/java/org/akanework/gramophone/ui/LibraryViewModel.kt` | Kotlin | `org.akanework.gramophone.ui` — LibraryViewModel: A ViewModel that contains library information. Used across the application. |
| `app/src/main/java/org/akanework/gramophone/ui/MainActivity.kt` | Kotlin | `org.akanework.gramophone.ui` — MainActivity: Core of gramophone, one and the only activity used across the application. |

## `app/src/main/java/org/akanework/gramophone/ui/adapters/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/ui/adapters/AlbumAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — Declares class `AlbumAdapter`. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/ArtistAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — [ArtistAdapter] is an adapter for displaying artists. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/BaseAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — Declares class `BaseAdapter`. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/BaseDecorAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — Declares class `BaseDecorAdapter`. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/BlacklistFolderAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — Declares class `BlacklistFolderAdapter`. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/DateAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — [DateAdapter] is an adapter for displaying dates. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/DetailedFolderAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — Declares class `DetailedFolderAdapter`. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/FolderAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — Declares class `FolderAdapter`. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/GenreAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — [GenreAdapter] is an adapter for displaying genres. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/HeaderAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — Declares class `HeaderAdapter`. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/HomepageCarouselAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — Declares class `HomepageCarouselAdapter`. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/LibraryCategoryAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — Declares class `LibraryCategoryAdapter`. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/LibraryHomeAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — Declares class `LibraryHomeAdapter`. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/MainPageAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — This is the ViewPager2 adapter. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/PlaylistAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — [PlaylistAdapter] is an adapter for displaying artists. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/RecommendAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — Declares class `RecommendAdapter`. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/SongAdapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — [SongAdapter] is an adapter for displaying songs. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/Sorter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — Declares class `Sorter`. |
| `app/src/main/java/org/akanework/gramophone/ui/adapters/ViewPager2Adapter.kt` | Kotlin | `org.akanework.gramophone.ui.adapters` — This is the ViewPager2 adapter. |

## `app/src/main/java/org/akanework/gramophone/ui/components/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/ui/components/BlendView.kt` | Kotlin | `org.akanework.gramophone.ui.components` — Declares class `BlendView`. |
| `app/src/main/java/org/akanework/gramophone/ui/components/CustomGridLayoutManager.kt` | Kotlin | `org.akanework.gramophone.ui.components` — CustomGridLayoutManager: A grid layout manager for making the grid view intact. |
| `app/src/main/java/org/akanework/gramophone/ui/components/CustomLinearLayoutManager.kt` | Kotlin | `org.akanework.gramophone.ui.components` — Declares class `CustomLinearLayoutManager`. |
| `app/src/main/java/org/akanework/gramophone/ui/components/CustomTextView.kt` | Kotlin | `org.akanework.gramophone.ui.components` — Declares class `CustomTextView`. |
| `app/src/main/java/org/akanework/gramophone/ui/components/FadingVerticalEdgeLayout.kt` | Kotlin | `org.akanework.gramophone.ui.components` — Declares class `FadingVerticalEdgeLayout`. |
| `app/src/main/java/org/akanework/gramophone/ui/components/FullBottomSheet.kt` | Kotlin | `org.akanework.gramophone.ui.components` — Fix abnormal smooth scroll when switch lyric line by click |
| `app/src/main/java/org/akanework/gramophone/ui/components/GridPaddingDecoration.kt` | Kotlin | `org.akanework.gramophone.ui.components` — Declares class `GridPaddingDecoration`. |
| `app/src/main/java/org/akanework/gramophone/ui/components/GridPaddingDecorationLibrary.kt` | Kotlin | `org.akanework.gramophone.ui.components` — Declares class `GridPaddingDecorationLibrary`. |
| `app/src/main/java/org/akanework/gramophone/ui/components/ItemSnapHelper.kt` | Kotlin | `org.akanework.gramophone.ui.components` — Declares class `ItemSnapHelper`. |
| `app/src/main/java/org/akanework/gramophone/ui/components/LyricPaddingDecoration.kt` | Kotlin | `org.akanework.gramophone.ui.components` — Declares class `LyricPaddingDecoration`. |
| `app/src/main/java/org/akanework/gramophone/ui/components/MarqueeTextView.kt` | Kotlin | `org.akanework.gramophone.ui.components` — [MarqueeTextView] is a kind of [AppCompatTextView] keeps the focus of the textView all the time so marquee can be displayed properly without needing to set focus manually. |
| `app/src/main/java/org/akanework/gramophone/ui/components/PlayerBottomSheet.kt` | Kotlin | `org.akanework.gramophone.ui.components` — Declares class `PlayerBottomSheet`. |

## `app/src/main/java/org/akanework/gramophone/ui/fragments/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/ui/fragments/AboutContributorFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — Declares class `AboutContributorFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/AboutFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — Declares class `AboutFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/AdapterFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — AdapterFragment: This fragment is the container for any list that contains recyclerview in the program. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/ArtistSubFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — ArtistSubFragment: Separated from GeneralSubFragment and will be merged into it in future development. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/BaseElevatedFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — BaseFragment: It is a base fragment for all main fragments that can appear in MainActivity's FragmentContainer. It creates material transitions easily and also make overlapping colors more convenient. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/BaseFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — BaseFragment: It is a base fragment for all main fragments that can appear in MainActivity's FragmentContainer. It creates material transitions easily and also make overlapping colors more convenient. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/BasePreferenceFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — BasePreferenceFragment: A base fragment for all SettingsTopFragment. It is used to make overlapping color easier. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/BaseSettingFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — Declares class `BaseSettingFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/BaseWrapperFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — Declares class `BaseWrapperFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/BrowseFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — ViewPagerFragment: A fragment that's in charge of displaying tabs and is connected to the drawer. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/DetailDialogFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — Declares class `DetailDialogFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/GeneralSubFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — GeneralSubFragment: Inherited from [BaseFragment]. Sub fragment of all possible item types. TODO: Artist / AlbumArtist |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/HomepageFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — Declares class `HomepageFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/LibraryAlbumSubFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — Declares class `LibraryAlbumSubFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/LibraryFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — Declares class `LibraryFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/LibrarySongSubFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — Declares class `LibrarySongSubFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/SearchFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — SearchFragment: A fragment that contains a search bar which browses the library finding items matching user input. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/ViewPagerFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments` — Declares class `ViewPagerFragment`. |

## `app/src/main/java/org/akanework/gramophone/ui/fragments/settings/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/java/org/akanework/gramophone/ui/fragments/settings/AppearanceSettingsTopFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments.settings` — Declares class `AppearanceSettingsFragment`, class `AppearanceSettingsTopFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/settings/AudioSettingsTopFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments.settings` — Declares class `AudioSettingsFragment`, class `AudioSettingsTopFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/settings/BehaviorSettingsTopFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments.settings` — Declares class `BehaviorSettingsFragment`, class `BehaviorSettingsTopFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/settings/BlacklistSettingsFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments.settings` — Declares class `BlacklistSettingsFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/settings/ExperimentalSettingsTopFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments.settings` — Declares class `ExperimentalSettingsFragment`, class `ExperimentalSettingsTopFragment`. |
| `app/src/main/java/org/akanework/gramophone/ui/fragments/settings/MainSettingsTopFragment.kt` | Kotlin | `org.akanework.gramophone.ui.fragments.settings` — Declares class `MainSettingsFragment`, class `MainSettingsTopFragment`. |

## `app/src/main/res/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/resources.properties` | Properties | Gradle/project properties (unqualifiedResLocale) |

## `app/src/main/res/anim/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/anim/slide_in_left.xml` | XML | XML document rooted at `set` |
| `app/src/main/res/anim/slide_in_right.xml` | XML | XML document rooted at `set` |
| `app/src/main/res/anim/slide_out_left.xml` | XML | XML document rooted at `set` |
| `app/src/main/res/anim/slide_out_right.xml` | XML | XML document rooted at `set` |

## `app/src/main/res/color/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/color/sl_check_button.xml` | XML | XML document rooted at `selector` |
| `app/src/main/res/color/sl_check_button_playlist_bg.xml` | XML | XML document rooted at `selector` |
| `app/src/main/res/color/sl_check_button_playlist_icon_tint.xml` | XML | XML document rooted at `selector` |
| `app/src/main/res/color/sl_fav_button.xml` | XML | XML document rooted at `selector` |

## `app/src/main/res/drawable/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/drawable/accord_mix_1.webp` | WEBP | RIFF (little-endian) data image, 1920×1920 |
| `app/src/main/res/drawable/accord_mix_1_banner.xml` | XML | `shape` drawable |
| `app/src/main/res/drawable/accord_mix_2.webp` | WEBP | RIFF (little-endian) data image, 1920×1920 |
| `app/src/main/res/drawable/accord_mix_2_banner.xml` | XML | `shape` drawable |
| `app/src/main/res/drawable/accord_mix_3.webp` | WEBP | RIFF (little-endian) data image, 1920×1920 |
| `app/src/main/res/drawable/accord_mix_3_banner.xml` | XML | `shape` drawable |
| `app/src/main/res/drawable/accord_mix_4.webp` | WEBP | RIFF (little-endian) data image, 1920×1920 |
| `app/src/main/res/drawable/accord_mix_4_banner.xml` | XML | `shape` drawable |
| `app/src/main/res/drawable/afs_popup_background.xml` | XML | `shape` drawable (44dp×44dp) |
| `app/src/main/res/drawable/akane_avatar.webp` | WEBP | RIFF (little-endian) data image, 640×640 |
| `app/src/main/res/drawable/apple_lossless_seeklogo.xml` | XML | `vector` drawable (150dp×90dp) |
| `app/src/main/res/drawable/awainatsu.webp` | WEBP | RIFF (little-endian) data image, 640×640 |
| `app/src/main/res/drawable/baseline_arrow_downward_24.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/baseline_arrow_upward_24.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/baseline_keyboard_double_arrow_down_24.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/bg_pause_anim.xml` | XML | `animated` drawable (94dp×94dp) |
| `app/src/main/res/drawable/bg_play_anim.xml` | XML | `animated` drawable (94dp×94dp) |
| `app/src/main/res/drawable/duo3.webp` | WEBP | RIFF (little-endian) data image, 460×460 |
| `app/src/main/res/drawable/fastscroller_thumb.xml` | XML | `shape` drawable (8dp×52dp) |
| `app/src/main/res/drawable/ic_accord_logo_alt.xml` | XML | `vector` drawable (21dp×28dp) |
| `app/src/main/res/drawable/ic_alarm_off.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_alarm_on.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_album.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_album_fixed.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_album_sort.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_align_center.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_apple_next.xml` | XML | `vector` drawable (27.398dp×27.398dp) |
| `app/src/main/res/drawable/ic_apple_pause.xml` | XML | `vector` drawable (38dp×38dp) |
| `app/src/main/res/drawable/ic_apple_play.xml` | XML | `vector` drawable (38dp×38dp) |
| `app/src/main/res/drawable/ic_apple_previous.xml` | XML | `vector` drawable (27.398dp×27.398dp) |
| `app/src/main/res/drawable/ic_apple_sort.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_arrow_back.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_arrow_back_ios.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_article.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_artist_sort.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_bullet_list_24dp.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_calendar.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_calendar_add.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_character.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_chevron_right.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_close.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_collections_bookmark.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_colors.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_contributors.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_dashboard_customize.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_default_cover.xml` | XML | `layer` drawable |
| `app/src/main/res/drawable/ic_default_cover_artist.xml` | XML | `layer` drawable |
| `app/src/main/res/drawable/ic_default_cover_date.xml` | XML | `layer` drawable |
| `app/src/main/res/drawable/ic_default_cover_favourite.xml` | XML | `layer` drawable |
| `app/src/main/res/drawable/ic_default_cover_fixed.xml` | XML | `layer` drawable |
| `app/src/main/res/drawable/ic_default_cover_genre.xml` | XML | `layer` drawable |
| `app/src/main/res/drawable/ic_default_cover_manuscript.xml` | XML | `layer` drawable |
| `app/src/main/res/drawable/ic_default_cover_playlist.xml` | XML | `layer` drawable |
| `app/src/main/res/drawable/ic_dot.xml` | XML | `vector` drawable (20dp×20dp) |
| `app/src/main/res/drawable/ic_draw_abstract.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_edit.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_equalizer.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_expand_more.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_experiment.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_favourite.xml` | XML | `selector` drawable |
| `app/src/main/res/drawable/ic_folder.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_format_align_center.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_format_bold.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_genres.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_gramophone_monochrome.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_gramophone_notification.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_groups.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_headphones.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_infinity.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_info.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_jigsaw.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_launcher_foreground.xml` | XML | `vector` drawable (108dp×108dp) |
| `app/src/main/res/drawable/ic_layout_type.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_library_link_icon_album.xml` | XML | `vector` drawable (40dp×40dp) |
| `app/src/main/res/drawable/ic_library_link_icon_artists.xml` | XML | `vector` drawable (40dp×40dp) |
| `app/src/main/res/drawable/ic_library_link_icon_playlists.xml` | XML | `vector` drawable (40dp×40dp) |
| `app/src/main/res/drawable/ic_library_link_icon_songs.xml` | XML | `vector` drawable (40dp×40dp) |
| `app/src/main/res/drawable/ic_lyrics.xml` | XML | `vector` drawable (27dp×26dp) |
| `app/src/main/res/drawable/ic_lyrics_btn.xml` | XML | `selector` drawable |
| `app/src/main/res/drawable/ic_manufacturing.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_media_pause_anim.xml` | XML | `animated` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_media_play_anim.xml` | XML | `animated` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_more_vert_alt.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_more_vert_bold.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_music_note_24dp.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_nowplaying_favorite.xml` | XML | `vector` drawable (28dp×28dp) |
| `app/src/main/res/drawable/ic_nowplaying_favorited.xml` | XML | `vector` drawable (28dp×28dp) |
| `app/src/main/res/drawable/ic_nowplaying_lyrics.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_lyricson.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_more.xml` | XML | `vector` drawable (28dp×28dp) |
| `app/src/main/res/drawable/ic_nowplaying_more_fill.xml` | XML | `vector` drawable (28dp×28dp) |
| `app/src/main/res/drawable/ic_nowplaying_mp_fforward.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_nowplaying_mp_pause.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_nowplaying_mp_play.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_nowplaying_queue.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_nowplaying_queueon.xml` | XML | `vector` drawable |
| `app/src/main/res/drawable/ic_palette.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_pause_filled.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_play_arrow.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_play_arrow_filled.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_playlist_play.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_playlist_play_filled.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_queue_btn.xml` | XML | `selector` drawable |
| `app/src/main/res/drawable/ic_quote_bubble_24dp.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_refresh.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_remove.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_repeat.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_repeat_off.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_repeat_one.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_rounded_corner.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_schedule.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_script.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_script_filled.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_search.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_settings.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_shuffle.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_shuffle_off.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_skip_next.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_skip_next_filled.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_skip_previous.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_skip_previous_filled.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_speaker_24dp.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_speaker_wave_3_24dp.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_star.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_star_filled.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/ic_text_fields.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_timer_24dp.xml` | XML | `vector` drawable (24dp×24dp) |
| `app/src/main/res/drawable/ic_transparent.xml` | XML | `vector` drawable (5dp×20dp) |
| `app/src/main/res/drawable/ic_warning.xml` | XML | `vector` drawable (32dp×32dp) |
| `app/src/main/res/drawable/lazar.webp` | WEBP | RIFF (little-endian) data image, 460×460 |
| `app/src/main/res/drawable/luka.webp` | WEBP | RIFF (little-endian) data, Web/P image |
| `app/src/main/res/drawable/media_seekbar_thumb.xml` | XML | `animated` drawable (5dp×20dp) |
| `app/src/main/res/drawable/nick.webp` | WEBP | RIFF (little-endian) data image, 460×460 |
| `app/src/main/res/drawable/popup_background.xml` | XML | `shape` drawable |
| `app/src/main/res/drawable/rp_buttons.xml` | XML | `ripple` drawable |
| `app/src/main/res/drawable/rp_buttons_dynamic.xml` | XML | `ripple` drawable |
| `app/src/main/res/drawable/selected_chip_background.xml` | XML | `selector` drawable |
| `app/src/main/res/drawable/skyd.webp` | WEBP | RIFF (little-endian) data image, 430×430 |
| `app/src/main/res/drawable/sl_check_timer.xml` | XML | `selector` drawable |
| `app/src/main/res/drawable/sp_shadow.xml` | XML | `layer` drawable |
| `app/src/main/res/drawable/tab_browse.xml` | XML | `vector` drawable (56dp×56dp) |
| `app/src/main/res/drawable/tab_home.xml` | XML | `vector` drawable (56dp×56dp) |
| `app/src/main/res/drawable/tab_library.xml` | XML | `vector` drawable (56dp×56dp) |
| `app/src/main/res/drawable/tab_search.xml` | XML | `vector` drawable (56dp×56dp) |

## `app/src/main/res/layout/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/layout/activity_bug_handler.xml` | XML | XML document rooted at `androidx.coordinatorlayout.widget.CoordinatorLayout` |
| `app/src/main/res/layout/activity_main.xml` | XML | XML document rooted at `androidx.coordinatorlayout.widget.CoordinatorLayout` |
| `app/src/main/res/layout/adapter_blacklist_folder_card.xml` | XML | Layout rooted at `LinearLayout`; ids: `checkbox`, `title`; custom views: com.google.android.material.checkbox.MaterialCheckBox |
| `app/src/main/res/layout/adapter_folder_card.xml` | XML | Layout rooted at `FrameLayout`; ids: `cover`, `title`, `subtitle`, `divider_list`; custom views: com.google.android.material.card.MaterialCardView, com.google.android.material.divider.MaterialDivider |
| `app/src/main/res/layout/adapter_grid_card.xml` | XML | XML document rooted at `androidx.constraintlayout.widget.ConstraintLayout` |
| `app/src/main/res/layout/adapter_list_card.xml` | XML | Layout rooted at `FrameLayout`; ids: `cover`, `title`, `artist`, `more`, `divider_list`; custom views: com.google.android.material.button.MaterialButton, com.google.android.material.card.MaterialCardView, com.google.android.material.divider.MaterialDivider |
| `app/src/main/res/layout/adapter_list_card_artist.xml` | XML | Layout rooted at `FrameLayout`; ids: `cover`, `title`, `more`, `divider_list`; custom views: com.google.android.material.button.MaterialButton, com.google.android.material.card.MaterialCardView, com.google.android.material.divider.MaterialDivider |
| `app/src/main/res/layout/adapter_list_card_larger.xml` | XML | Layout rooted at `FrameLayout`; ids: `cover`, `title`, `more`, `divider_list`; custom views: com.google.android.material.button.MaterialButton, com.google.android.material.card.MaterialCardView, com.google.android.material.divider.MaterialDivider |
| `app/src/main/res/layout/adapter_list_card_playlist.xml` | XML | Layout rooted at `FrameLayout`; ids: `cover`, `title`, `artist`, `close`; custom views: com.google.android.material.button.MaterialButton, com.google.android.material.card.MaterialCardView |
| `app/src/main/res/layout/blend_background.xml` | XML | Layout rooted at `merge`; ids: `blend_background`, `bg`, `rotate_frame`, `guideline`, `guideline_2`, `type1`; custom views: androidx.constraintlayout.widget.ConstraintLayout, androidx.constraintlayout.widget.Guideline |
| `app/src/main/res/layout/bottom_sheet.xml` | XML | Layout rooted at `merge`; ids: `player_layout`, `preview_player`, `stub_view`, `preview_song_name`, `preview_control`, `preview_next`; custom views: androidx.constraintlayout.widget.ConstraintLayout, com.google.android.material.button.MaterialButton, com.google.android.material.card.MaterialCardV… |
| `app/src/main/res/layout/dialog_about.xml` | XML | Layout rooted at `LinearLayout`; ids: `version` |
| `app/src/main/res/layout/fragment_about.xml` | XML | XML document rooted at `androidx.coordinatorlayout.widget.CoordinatorLayout` |
| `app/src/main/res/layout/fragment_about_inner.xml` | XML | XML document rooted at `androidx.coordinatorlayout.widget.CoordinatorLayout` |
| `app/src/main/res/layout/fragment_blacklist_settings.xml` | XML | XML document rooted at `androidx.coordinatorlayout.widget.CoordinatorLayout` |
| `app/src/main/res/layout/fragment_browse.xml` | XML | XML document rooted at `androidx.coordinatorlayout.widget.CoordinatorLayout` |
| `app/src/main/res/layout/fragment_general_sub.xml` | XML | XML document rooted at `androidx.coordinatorlayout.widget.CoordinatorLayout` |
| `app/src/main/res/layout/fragment_homepage.xml` | XML | XML document rooted at `androidx.coordinatorlayout.widget.CoordinatorLayout` |
| `app/src/main/res/layout/fragment_info_song.xml` | XML | XML document rooted at `androidx.coordinatorlayout.widget.CoordinatorLayout` |
| `app/src/main/res/layout/fragment_library.xml` | XML | XML document rooted at `androidx.coordinatorlayout.widget.CoordinatorLayout` |
| `app/src/main/res/layout/fragment_recyclerview.xml` | XML | Layout rooted at `FrameLayout`; ids: `recyclerview`; custom views: org.akanework.gramophone.logic.ui.MyRecyclerView |
| `app/src/main/res/layout/fragment_search.xml` | XML | XML document rooted at `androidx.coordinatorlayout.widget.CoordinatorLayout` |
| `app/src/main/res/layout/fragment_top_settings.xml` | XML | XML document rooted at `androidx.coordinatorlayout.widget.CoordinatorLayout` |
| `app/src/main/res/layout/fragment_viewpager.xml` | XML | XML document rooted at `androidx.viewpager2.widget.ViewPager2` |
| `app/src/main/res/layout/fragment_wrapper.xml` | XML | XML document rooted at `androidx.fragment.app.FragmentContainerView` |
| `app/src/main/res/layout/full_player.xml` | XML | Layout rooted at `merge`; ids: `full_player`, `playlist_frame`, `playlist_cover_frame`, `playlist_demo_cover`, `playlist_title_frame`, `playlist_song_name`; custom views: androidx.constraintlayout.widget.ConstraintLayout, androidx.recyclerview.widget.RecyclerView, com.google.android.material.bott… |
| `app/src/main/res/layout/general_decor.xml` | XML | Layout rooted at `FrameLayout`; ids: `song_counter`, `play_all`, `shuffle_all`, `sort`; custom views: com.google.android.material.button.MaterialButton |
| `app/src/main/res/layout/homepage_carousel.xml` | XML | Layout rooted at `LinearLayout`; ids: `hint_top`, `cover`, `banner`, `hint`; custom views: com.google.android.material.card.MaterialCardView |
| `app/src/main/res/layout/homepage_recommend_card.xml` | XML | Layout rooted at `LinearLayout`; ids: `cover`, `title`, `subtitle`; custom views: com.google.android.material.card.MaterialCardView |
| `app/src/main/res/layout/library_entry.xml` | XML | Layout rooted at `FrameLayout`; ids: `imageview`, `text`; custom views: com.google.android.material.divider.MaterialDivider |
| `app/src/main/res/layout/lyric_common.xml` | XML | Layout rooted at `com.google.android.material.card.MaterialCardView`; ids: `lyric_cardview`, `lyric_text`; custom views: com.google.android.material.card.MaterialCardView |
| `app/src/main/res/layout/lyric_common_translation.xml` | XML | Layout rooted at `com.google.android.material.card.MaterialCardView`; ids: `lyric_cardview`, `lyric_text`, `transition_text`; custom views: com.google.android.material.card.MaterialCardView |
| `app/src/main/res/layout/lyric_content.xml` | XML | XML document rooted at `TextView` |
| `app/src/main/res/layout/lyric_syllable.xml` | XML | Layout rooted at `com.google.android.material.card.MaterialCardView`; ids: `lyric_cardview`, `lyric_flexbox`; custom views: com.google.android.flexbox.FlexboxLayout, com.google.android.material.card.MaterialCardView |
| `app/src/main/res/layout/lyric_syllable_translation.xml` | XML | Layout rooted at `com.google.android.material.card.MaterialCardView`; ids: `lyric_cardview`, `lyric_flexbox`, `transition_text`; custom views: com.google.android.flexbox.FlexboxLayout, com.google.android.material.card.MaterialCardView |
| `app/src/main/res/layout/preference_basic.xml` | XML | Layout rooted at `com.google.android.material.card.MaterialCardView`; custom views: com.google.android.material.card.MaterialCardView |
| `app/src/main/res/layout/preference_category_md.xml` | XML | Layout rooted at `FrameLayout` |
| `app/src/main/res/layout/preference_dropdown_md.xml` | XML | Layout rooted at `com.google.android.material.card.MaterialCardView`; ids: `spinner`; custom views: com.google.android.material.card.MaterialCardView |
| `app/src/main/res/layout/preference_icon.xml` | XML | Layout rooted at `LinearLayout`; ids: `icon_frame` |
| `app/src/main/res/layout/preference_seekbar.xml` | XML | Layout rooted at `com.google.android.material.card.MaterialCardView`; ids: `icon_frame`, `title_container`, `seekbar_value`, `seekbar`; custom views: com.google.android.material.card.MaterialCardView |
| `app/src/main/res/layout/preference_switch.xml` | XML | Layout rooted at `com.google.android.material.card.MaterialCardView`; custom views: com.google.android.material.card.MaterialCardView |
| `app/src/main/res/layout/preference_switch_widget.xml` | XML | XML document rooted at `com.google.android.material.materialswitch.MaterialSwitch` |
| `app/src/main/res/layout/recently_added.xml` | XML | XML document rooted at `TextView` |

## `app/src/main/res/layout-land/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/layout-land/full_player.xml` | XML | Layout rooted at `merge`; ids: `full_player`, `drag`, `playlist_frame`, `playlist_cover_frame`, `playlist_demo_cover`, `playlist_title_frame`; custom views: androidx.constraintlayout.widget.ConstraintLayout, androidx.recyclerview.widget.RecyclerView, com.google.android.material.bottomsheet.Bottom… |

## `app/src/main/res/menu/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/menu/bottom_nav.xml` | XML | Menu with 4 items: @string/home, @string/browse, @string/library, @string/search |
| `app/src/main/res/menu/home_menu.xml` | XML | Menu with 3 items: @string/home_menu_refresh, @string/home_menu_equalizer, @string/home_menu_settings |
| `app/src/main/res/menu/more_menu.xml` | XML | Menu with 6 items: @string/play_next, @string/go_to_album, @string/go_to_artist, @string/details, @string/delete, @string/share |
| `app/src/main/res/menu/more_menu_less.xml` | XML | Menu with 1 items: @string/play_next |
| `app/src/main/res/menu/navigation_menu.xml` | XML | Menu with 16 items: @string/categories, @string/category_songs, @string/category_albums, @string/category_artists, @string/category_genres, @string/category_dates |
| `app/src/main/res/menu/sort_menu.xml` | XML | Menu with 12 items: @string/album_artist, @string/natural_order, @string/sort_by_name, @string/sort_by_artist, @string/sort_by_album, @string/sort_by_size |

## `app/src/main/res/mipmap-anydpi/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/mipmap-anydpi/ic_launcher.xml` | XML | XML document rooted at `adaptive` |

## `app/src/main/res/raw/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/raw/keep.xml` | XML | Resource values: no standard resources |

## `app/src/main/res/values/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values/arrays.xml` | XML | Resource values: 2 string; keys include `theme_switch`, `theme_switch_val` |
| `app/src/main/res/values/attrs.xml` | XML | Resource values: no standard resources |
| `app/src/main/res/values/colors.xml` | XML | Resource values: 166 color; keys include `contrast_themeColor`, `contrast_colorOnSurface`, `contrast_colorBackground`, `contrast_colorBackgroundAlpha`, `contrast_colorBackgroundElevated` |
| `app/src/main/res/values/dimens.xml` | XML | Resource values: 32 dimen; keys include `media_seekbar_progress_wavelength`, `media_seekbar_progress_amplitude`, `media_seekbar_progress_phase`, `media_seekbar_progress_stroke_width`, `list_round_corner_size` |
| `app/src/main/res/values/drawables.xml` | XML | Resource values: no standard resources |
| `app/src/main/res/values/ic_launcher_background.xml` | XML | Resource values: 1 color; keys include `ic_launcher_background` |
| `app/src/main/res/values/integers.xml` | XML | Resource values: 2 integer; keys include `filter_default_sec`, `round_corner_radius` |
| `app/src/main/res/values/strings.xml` | XML | Resource values: 216 string; keys include `home_menu_search`, `home_menu_shuffle`, `home_menu_settings`, `home_menu_refresh`, `category_songs` |
| `app/src/main/res/values/theme_overlays.xml` | XML | Resource values: 2 style; keys include `ThemeOverlay.AppTheme.MediumContrast`, `ThemeOverlay.AppTheme.HighContrast` |
| `app/src/main/res/values/themes.xml` | XML | Resource values: 24 style; keys include `Base.Theme.Gramophone`, `MyDialog`, `TextAppearance.App.BodyLarge`, `TextAppearance.App.HeadlineLarge`, `TextAppearance.App.TitleLarge` |

## `app/src/main/res/values-ar/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-ar/strings.xml` | XML | Resource values (ar): 143 string; keys include `dismiss`, `settings_blacklist`, `settings_album_round_corner`, `categories`, `settings_category_behavior` |

## `app/src/main/res/values-be/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-be/strings.xml` | XML | Resource values (be): 143 string; keys include `dismiss`, `categories`, `settings_category_behavior`, `operations`, `go_to_album` |

## `app/src/main/res/values-bn/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-bn/strings.xml` | XML | Resource values (bn): 62 string; keys include `dismiss`, `categories`, `settings_category_behavior`, `category_dates`, `sort_by_artist` |

## `app/src/main/res/values-cs/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-cs/strings.xml` | XML | Resource values (cs): 62 string; keys include `dialog_artist`, `category_artists`, `settings_app_theme`, `settings_player_options`, `settings_title_bold` |

## `app/src/main/res/values-de/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-de/strings.xml` | XML | Resource values (de): 143 string; keys include `home_menu_search`, `home_menu_shuffle`, `home_menu_settings`, `home_menu_refresh`, `category_songs` |

## `app/src/main/res/values-es/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-es/strings.xml` | XML | Resource values (es): 143 string; keys include `dismiss`, `categories`, `settings_category_behavior`, `operations`, `go_to_album` |

## `app/src/main/res/values-fa/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-fa/strings.xml` | XML | Resource values (fa): 143 string; keys include `settings_float_output_summary`, `settings_content_based_color_summary`, `settings_hardware_acceleration_summary`, `home_menu_shuffle`, `category_artists` |

## `app/src/main/res/values-fr/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-fr/strings.xml` | XML | Resource values (fr): 143 string; keys include `dismiss`, `categories`, `settings_category_behavior`, `operations`, `go_to_album` |

## `app/src/main/res/values-hu/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-hu/strings.xml` | XML | Resource values (hu): 143 string; keys include `category_artists`, `follow_system`, `category_genres`, `category_playlists`, `category_songs` |

## `app/src/main/res/values-ia/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-ia/strings.xml` | XML | Resource values (ia): 143 string; keys include `sort_by_artist`, `categories`, `settings_category_appearance`, `dialog_artist`, `settings_category_behavior` |

## `app/src/main/res/values-in/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-in/strings.xml` | XML | Resource values (in): 121 string; keys include `settings_category_links`, `compact_list`, `settings_customization_summary`, `settings_play_on_launch_summary`, `refreshed_songs` |

## `app/src/main/res/values-it/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-it/strings.xml` | XML | Resource values (it): no standard resources |

## `app/src/main/res/values-ja/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-ja/strings.xml` | XML | Resource values (ja): 175 string; keys include `home_menu_search`, `home_menu_shuffle`, `home_menu_settings`, `home_menu_refresh`, `category_songs` |

## `app/src/main/res/values-nb-rNO/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-nb-rNO/strings.xml` | XML | Resource values (nb-rNO): 64 string; keys include `dismiss`, `categories`, `settings_category_behavior`, `operations`, `go_to_album` |

## `app/src/main/res/values-ne/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-ne/strings.xml` | XML | Resource values (ne): 51 string; keys include `sort_by_add_date`, `settings_about_gramophone`, `dialog_album_artist`, `settings_about_app`, `dialog_album` |

## `app/src/main/res/values-night/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-night/colors.xml` | XML | Resource values (night): 154 color; keys include `contrast_themeColor`, `contrast_colorOnSurface`, `contrast_colorBackground`, `contrast_colorBackgroundElevated`, `contrast_colorBackgroundBottomNav` |
| `app/src/main/res/values-night/dimens.xml` | XML | Resource values (night): no standard resources |
| `app/src/main/res/values-night/theme_overlays.xml` | XML | Resource values (night): 2 style; keys include `ThemeOverlay.AppTheme.MediumContrast`, `ThemeOverlay.AppTheme.HighContrast` |
| `app/src/main/res/values-night/themes.xml` | XML | Resource values (night): 1 style; keys include `Theme.Splash` |

## `app/src/main/res/values-pl/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-pl/strings.xml` | XML | Resource values (pl): 121 string; keys include `dismiss`, `settings_blacklist`, `settings_album_round_corner`, `categories`, `settings_category_behavior` |

## `app/src/main/res/values-pt/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-pt/strings.xml` | XML | Resource values (pt): 121 string; keys include `category_songs`, `category_albums`, `category_artists`, `category_genres`, `shuffle` |

## `app/src/main/res/values-pt-rBR/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-pt-rBR/strings.xml` | XML | Resource values (pt-rBR): 138 string; keys include `dismiss`, `settings_album_round_corner`, `categories`, `settings_category_behavior`, `operations` |

## `app/src/main/res/values-ro/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-ro/strings.xml` | XML | Resource values (ro): 138 string; keys include `category_artists`, `category_genres`, `home_menu_search`, `home_menu_settings`, `home_menu_shuffle` |

## `app/src/main/res/values-ru/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-ru/strings.xml` | XML | Resource values (ru): 143 string; keys include `home_menu_search`, `home_menu_shuffle`, `home_menu_settings`, `home_menu_refresh`, `category_songs` |

## `app/src/main/res/values-tr/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-tr/strings.xml` | XML | Resource values (tr): 117 string; keys include `dismiss`, `categories`, `settings_category_behavior`, `operations`, `go_to_album` |

## `app/src/main/res/values-uk/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-uk/strings.xml` | XML | Resource values (uk): 139 string; keys include `dismiss`, `categories`, `settings_category_behavior`, `operations`, `go_to_album` |

## `app/src/main/res/values-v31/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-v31/themes.xml` | XML | Resource values (v31): 3 style; keys include `Day.Theme.Splash`, `Night.Theme.Splash`, `Theme.Gramophone` |

## `app/src/main/res/values-vi/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-vi/strings.xml` | XML | Resource values (vi): 116 string; keys include `categories`, `settings_category_behavior`, `operations`, `go_to_album`, `unknown_album` |

## `app/src/main/res/values-zh-rCN/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-zh-rCN/strings.xml` | XML | Resource values (zh-rCN): 175 string; keys include `app_name`, `home_menu_search`, `home_menu_shuffle`, `home_menu_settings`, `home_menu_refresh` |

## `app/src/main/res/values-zh-rTW/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/values-zh-rTW/strings.xml` | XML | Resource values (zh-rTW): 105 string; keys include `home_menu_search`, `home_menu_shuffle`, `home_menu_settings`, `home_menu_refresh`, `category_songs` |

## `app/src/main/res/xml/`

| File | Type | What it contains |
|---|---|---|
| `app/src/main/res/xml/backup_rules.xml` | XML | XML document rooted at `full` |
| `app/src/main/res/xml/data_extraction_rules.xml` | XML | XML document rooted at `data` |
| `app/src/main/res/xml/settings_about.xml` | XML | Preference screen with 4 keys: `app_name`, `app_version`, `package_type`, `contributors` |
| `app/src/main/res/xml/settings_appearance.xml` | XML | Preference screen with 1 keys: `theme_mode` |
| `app/src/main/res/xml/settings_audio.xml` | XML | Preference screen with 3 keys: `floatoutput`, `ps_hardware_acc`, `skip_silence` |
| `app/src/main/res/xml/settings_behavior.xml` | XML | Preference screen with 5 keys: `mediastore_filter`, `blacklist`, `autoplay`, `trim_lyrics`, `album_covers` |
| `app/src/main/res/xml/settings_experimental.xml` | XML | Preference screen with 1 keys: `crash` |
| `app/src/main/res/xml/settings_top.xml` | XML | Preference screen with 7 keys: `appearance`, `behavior`, `library`, `personalization`, `audio`, `experimental` |

## `app/src/test/java/org/akanework/gramophone/`

| File | Type | What it contains |
|---|---|---|
| `app/src/test/java/org/akanework/gramophone/LrcUtilsTest.kt` | Kotlin | `org.akanework.gramophone` — Declares class `LrcUtilsTest`. |

## `fastlane/metadata/android/en-US/images/phoneScreenshots/`

| File | Type | What it contains |
|---|---|---|
| `fastlane/metadata/android/en-US/images/phoneScreenshots/Browse.jpg` | JPG | JPEG image, 72×72 |
| `fastlane/metadata/android/en-US/images/phoneScreenshots/Home.jpg` | JPG | JPEG image, 72×72 |
| `fastlane/metadata/android/en-US/images/phoneScreenshots/Library.jpg` | JPG | JPEG image, 72×72 |
| `fastlane/metadata/android/en-US/images/phoneScreenshots/Lyrics.png` | PNG | PNG image, 1080×2400 |
| `fastlane/metadata/android/en-US/images/phoneScreenshots/Player.jpg` | JPG | JPEG image, 72×72 |
| `fastlane/metadata/android/en-US/images/phoneScreenshots/Search.jpg` | JPG | JPEG image, 72×72 |

## `gradle/wrapper/`

| File | Type | What it contains |
|---|---|---|
| `gradle/wrapper/gradle-wrapper.jar` | JAR | Bundled Java archive |
| `gradle/wrapper/gradle-wrapper.properties` | Properties | Gradle/project properties (distributionBase, distributionPath, distributionUrl, networkTimeout) |

## `libs/`

| File | Type | What it contains |
|---|---|---|
| `libs/lib-decoder-ffmpeg-release.aar` | AAR | Bundled Android library archive with classes.jar (14 entries) |

## `recyclerview/`

| File | Type | What it contains |
|---|---|---|
| `recyclerview/.gitignore` | Git | Git ignore rules |
| `recyclerview/build.gradle.kts` | Gradle | recyclerview module build script; plugins: com.android.library; 6 dependency lines |
| `recyclerview/proguard-rules.pro` | ProGuard | R8/ProGuard keep and shrinking rules |

## `recyclerview/res-public/values/`

| File | Type | What it contains |
|---|---|---|
| `recyclerview/res-public/values/public_attrs.xml` | XML | Resource values: no standard resources |

## `recyclerview/res/values/`

| File | Type | What it contains |
|---|---|---|
| `recyclerview/res/values/attrs.xml` | XML | Resource values: no standard resources |
| `recyclerview/res/values/dimens.xml` | XML | Resource values: 6 dimen; keys include `item_touch_helper_max_drag_scroll_per_frame`, `item_touch_helper_swipe_escape_velocity`, `item_touch_helper_swipe_escape_max_velocity`, `fastscroll_default_thickness`, `fastscroll_minimum_range` |
| `recyclerview/res/values/ids.xml` | XML | Resource values: no standard resources |

## `recyclerview/src/main/`

| File | Type | What it contains |
|---|---|---|
| `recyclerview/src/main/AndroidManifest.xml` | XML | Manifest: 0 permissions, 0 activities, 0 services |

## `recyclerview/src/main/java/androidx/recyclerview/widget/`

| File | Type | What it contains |
|---|---|---|
| `recyclerview/src/main/java/androidx/recyclerview/widget/AdapterHelper.java` | Java | `androidx.recyclerview.widget` — Helper class that can enqueue and process adapter update operations. <p> To support animations, RecyclerView presents an older version the Adapter to best represent previous state of the layout. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/AdapterListUpdateCallback.java` | Java | `androidx.recyclerview.widget` — ListUpdateCallback that dispatches update events to the given adapter. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/AsyncDifferConfig.java` | Java | `androidx.recyclerview.widget` — Configuration object for {@link ListAdapter}, {@link AsyncListDiffer}, and similar background-thread list diffing adapter logic. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/AsyncListDiffer.java` | Java | `androidx.recyclerview.widget` — Helper for computing the difference between two lists via {@link DiffUtil} on a background thread. <p> It can be connected to a {@link RecyclerView.Adapter RecyclerView.Adapter}, and will signal the adapter of changes between sumbitted lists. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/AsyncListUtil.java` | Java | `androidx.recyclerview.widget` — A utility class that supports asynchronous content loading. <p> It can be used to load Cursor data in chunks without querying the Cursor on the UI Thread while keeping UI and cache synchronous for better user experience. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/BatchingListUpdateCallback.java` | Java | `androidx.recyclerview.widget` — Wraps a {@link ListUpdateCallback} callback and batches operations that can be merged. <p> For instance, when 2 add operations comes that adds 2 consecutive elements, BatchingListUpdateCallback merges them and calls the wrapped callback only once. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ChildHelper.java` | Java | `androidx.recyclerview.widget` — Helper class to manage children. <p> It wraps a RecyclerView and adds ability to hide some children. There are two sets of methods provided by this class. <b>Regular</b> methods are the ones that replicate ViewGroup methods like getChildAt, getChildCount etc. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ConcatAdapter.java` | Java | `androidx.recyclerview.widget` — An {@link Adapter} implementation that presents the contents of multiple adapters in sequence. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ConcatAdapterController.java` | Java | `androidx.recyclerview.widget` — All logic for the {@link ConcatAdapter} is here so that we can clearly see a separation between an adapter implementation and merging logic. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/DefaultItemAnimator.java` | Java | `androidx.recyclerview.widget` — This implementation of {@link RecyclerView.ItemAnimator} provides basic animations on remove, add, and move events that happen to the items in a RecyclerView. RecyclerView uses a DefaultItemAnimator by default. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/DiffUtil.java` | Java | `androidx.recyclerview.widget` — DiffUtil is a utility class that calculates the difference between two lists and outputs a list of update operations that converts the first list into the second one. <p> It can be used to calculate updates for a RecyclerView Adapter. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/DividerItemDecoration.java` | Java | `androidx.recyclerview.widget` — DividerItemDecoration is a {@link RecyclerView.ItemDecoration} that can be used as a divider between items of a {@link LinearLayoutManager}. It supports both {@link #HORIZONTAL} and {@link #VERTICAL} orientations. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/FastScroller.java` | Java | `androidx.recyclerview.widget` — Class responsible to animate and provide a fast scroller. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/GapWorker.java` | Java | `androidx.recyclerview.widget` — Temporary storage for prefetch Tasks that execute in {@link #prefetch(long)}. Task objects are pooled in the ArrayList, and never removed to avoid allocations, but always cleared in between calls. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/GridLayoutManager.java` | Java | `androidx.recyclerview.widget` — A {@link RecyclerView.LayoutManager} implementations that lays out items in a grid. <p> By default, each item occupies 1 span. You can change it by providing a custom {@link SpanSizeLookup} instance via {@link #setSpanSizeLookup(SpanSizeLookup)}. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ItemTouchHelper.java` | Java | `androidx.recyclerview.widget` — This is a utility class to add swipe to dismiss and drag & drop support to RecyclerView. <p> It works with a RecyclerView and a Callback class, which configures what type of interactions are enabled and also receives events when user performs these actions. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ItemTouchUIUtil.java` | Java | `androidx.recyclerview.widget` — Utility class for {@link ItemTouchHelper} which handles item transformations for different API versions. <p/> This class has methods that map to {@link ItemTouchHelper.Callback}'s drawing methods. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ItemTouchUIUtilImpl.java` | Java | `androidx.recyclerview.widget` — Package private class to keep implementations. Putting them inside ItemTouchUIUtil makes them public API, which is not desired in this case. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/LayoutState.java` | Java | `androidx.recyclerview.widget` — Helper class that keeps temporary state while {LayoutManager} is filling out the empty space. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/LinearLayoutManager.java` | Java | `androidx.recyclerview.widget` — A {@link RecyclerView.LayoutManager} implementation which provides similar functionality to {@link android.widget.ListView}. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/LinearSmoothScroller.java` | Java | `androidx.recyclerview.widget` — {@link RecyclerView.SmoothScroller} implementation which uses a {@link LinearInterpolator} until the target position becomes a child of the RecyclerView and then uses a {@link DecelerateInterpolator} to slowly approach to target position. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/LinearSnapHelper.java` | Java | `androidx.recyclerview.widget` — Implementation of the {@link SnapHelper} supporting snapping in either vertical or horizontal orientation. <p> The implementation will snap the center of the target child view to the center of the attached {@link RecyclerView}. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ListAdapter.java` | Java | `androidx.recyclerview.widget` — {@link RecyclerView.Adapter RecyclerView.Adapter} base class for presenting List data in a {@link RecyclerView}, including computing diffs between Lists on a background thread. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ListUpdateCallback.java` | Java | `androidx.recyclerview.widget` — An interface that can receive Update operations that are applied to a list. <p> This class can be used together with DiffUtil to detect changes between two lists. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/MessageThreadUtil.java` | Java | `androidx.recyclerview.widget` — Replica of android.os.Message. Unfortunately, cannot use it without a Handler and don't want to create a thread just for this component. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/NestedAdapterWrapper.java` | Java | `androidx.recyclerview.widget` — Wrapper for each adapter in {@link ConcatAdapter}. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/OpReorderer.java` | Java | `androidx.recyclerview.widget` — Declares class `OpReorderer`. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/OrientationHelper.java` | Java | `androidx.recyclerview.widget` — Helper class for LayoutManagers to abstract measurements depending on the View's orientation. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/PagerSnapHelper.java` | Java | `androidx.recyclerview.widget` — Implementation of the {@link SnapHelper} supporting pager style snapping in either vertical or horizontal orientation. <p> PagerSnapHelper can help achieve a similar behavior to {@link androidx.viewpager.widget.ViewPager}. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/RecyclerView.java` | Java | `androidx.recyclerview.widget` — A flexible view for providing a limited window into a large data set. <h3>Glossary of terms:</h3> <ul> <li><em>Adapter:</em> A subclass of {@link Adapter} responsible for providing views that represent items in a data set.</li> <li><em>Position:</em> The posit |
| `recyclerview/src/main/java/androidx/recyclerview/widget/RecyclerViewAccessibilityDelegate.java` | Java | `androidx.recyclerview.widget` — The AccessibilityDelegate used by RecyclerView. <p> This class handles basic accessibility actions and delegates them to LayoutManager. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ScrollbarHelper.java` | Java | `androidx.recyclerview.widget` — A helper class to do scroll offset calculations. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/SimpleItemAnimator.java` | Java | `androidx.recyclerview.widget` — A wrapper class for ItemAnimator that records View bounds and decides whether it should run move, change, add or remove animations. This class also replicates the original ItemAnimator API. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/SnapHelper.java` | Java | `androidx.recyclerview.widget` — Class intended to support snapping for a {@link RecyclerView}. <p> SnapHelper tries to handle fling as well but for this to work properly, the {@link RecyclerView.LayoutManager} must implement the {@link RecyclerView.SmoothScroller.ScrollVectorProvider} interf |
| `recyclerview/src/main/java/androidx/recyclerview/widget/SortedList.java` | Java | `androidx.recyclerview.widget` — A Sorted list implementation that can keep items in order and also notify for changes in the list such that it can be bound to a {@link RecyclerView.Adapter RecyclerView.Adapter}. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/SortedListAdapterCallback.java` | Java | `androidx.recyclerview.widget` — A {@link SortedList.Callback} implementation that can bind a {@link SortedList} to a {@link RecyclerView.Adapter}. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/StableIdStorage.java` | Java | `androidx.recyclerview.widget` — Used by {@link ConcatAdapter} to isolate item ids between nested adapters, if necessary. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/StaggeredGridLayoutManager.java` | Java | `androidx.recyclerview.widget` — A LayoutManager that lays out children in a staggered grid formation. It supports horizontal & vertical layout as well as an ability to layout children in reverse. <p> Staggered grids are likely to have gaps at the edges of the layout. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ThreadUtil.java` | Java | `androidx.recyclerview.widget` — Declares interface `ThreadUtil`. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/TileList.java` | Java | `androidx.recyclerview.widget` — A sparse collection of tiles sorted for efficient access. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ViewBoundsCheck.java` | Java | `androidx.recyclerview.widget` — A utility class used to check the boundaries of a given view within its parent view based on a set of boundary flags. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ViewInfoStore.java` | Java | `androidx.recyclerview.widget` — This class abstracts all tracking for Views to run animations. |
| `recyclerview/src/main/java/androidx/recyclerview/widget/ViewTypeStorage.java` | Java | `androidx.recyclerview.widget` — Used by {@link ConcatAdapter} to isolate view types between nested adapters, if necessary. |

## `recyclerview/src/main/java/org/helixform/fluidrecyclerview/`

| File | Type | What it contains |
|---|---|---|
| `recyclerview/src/main/java/org/helixform/fluidrecyclerview/EdgeEffectAdapter.kt` | Kotlin | `org.helixform.fluidrecyclerview` — A class that provides the required methods from {@link android.widget.EdgeEffect} class, of which some operations are replaced by the custom edge effect. |
| `recyclerview/src/main/java/org/helixform/fluidrecyclerview/FluidRubberBand.kt` | Kotlin | `org.helixform.fluidrecyclerview` — Declares class `FluidRubberBand`. |
| `recyclerview/src/main/java/org/helixform/fluidrecyclerview/FluidScroller.kt` | Kotlin | `org.helixform.fluidrecyclerview` — Declares class `FluidScroller`. |
| `recyclerview/src/main/java/org/helixform/fluidrecyclerview/FluidSpringBack.kt` | Kotlin | `org.helixform.fluidrecyclerview` — Declares class `FluidSpringBack`. |
| `recyclerview/src/main/java/org/helixform/fluidrecyclerview/FluidVelocityTracker.kt` | Kotlin | `org.helixform.fluidrecyclerview` — Declares property `MAX_SAMPLE_COUNT`, class `FluidVelocityTracker`. |
| `recyclerview/src/main/java/org/helixform/fluidrecyclerview/OverScrollerAdapter.kt` | Kotlin | `org.helixform.fluidrecyclerview` — A class that provides the required methods from {@link android.widget.OverScroller} class, of which some operations are replaced by the custom scroller. |
| `recyclerview/src/main/java/org/helixform/fluidrecyclerview/SystemVelocityTrackerAdapter.kt` | Kotlin | `org.helixform.fluidrecyclerview` — Adapter class for implementing {@link VelocityTracker} interface for {@link android.view.VelocityTracker} class. |
| `recyclerview/src/main/java/org/helixform/fluidrecyclerview/VelocityTracker.kt` | Kotlin | `org.helixform.fluidrecyclerview` — Defines the common methods for different velocity tracker implementations. |
| `recyclerview/src/main/java/org/helixform/fluidrecyclerview/VelocityTrackerFactory.kt` | Kotlin | `org.helixform.fluidrecyclerview` — A helper class to create different types of {@link VelocityTracker} instances. |

