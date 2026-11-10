# Apple Music on Apple TV — design reference

Everything gathered about how Apple Music looks and behaves on Apple TV (tvOS 17/18,
the tvOS 26 Liquid Glass redesign and the tvOS 27 sidebar-first navigation), plus the
measurements Apple publishes and how they map onto this app's canvas.

Labels used below:

- **[Doc]** — stated by Apple in a user guide, HIG page, newsroom post or WWDC session.
- **[Obs]** — observed in hands-on reviews, screenshot walkthroughs or user reports.
- **[Calc]** — derived here by converting Apple's numbers to this app's canvas.

---

## 1. Character of the app

- **[Obs]** The Music app on Apple TV is a **dark, artwork-led** experience: black
  canvas, white type, and colour coming almost entirely from album covers. It is not a
  light "cards on a gradient" interface and it does not use a mobile-style floating
  navigation capsule. The pre-26 app reads as a dark canvas with bright text and
  colourful artwork ([Apple Support, Home screen](https://support.apple.com/guide/tv/start-listening-on-the-home-screen-atvbb2412fc9/18.0/tvos/18.0),
  [iMore tvOS walkthrough](https://www.imore.com/how-use-apple-music-music-app-apple-tv)).
- **[Doc]** tvOS design guidance for media apps: deliver edge-to-edge artwork, subtle
  and fluid animation, and an interface that is legible from across the room
  ([Designing for tvOS](https://developer.apple.com/design/human-interface-guidelines/designing-for-tvos)).
- **[Doc]** tvOS text is bold enough to read at distance; the platform minimum type size
  is 23pt, default body 29pt ([Typography](https://developer.apple.com/design/human-interface-guidelines/typography)).## 2. Navigation

- **[Doc]/[Obs]** Through tvOS 26 the Music app is entered through a horizontal row of
  destinations near the **top edge** — Home, New (discovery), Radio, Library, Search —
  with **Now Playing** appearing there when music is playing. Apple's guides put Home and
  Now Playing "at the top of the screen"; reviews describe it as a "top menu". Not a
  detached phone-style capsule
  ([Apple Support, Home](https://support.apple.com/guide/tv/start-listening-on-the-home-screen-atvbb2412fc9/tvos),
  [Apple Support, Discover new music](https://support.apple.com/guide/tv/discover-new-music-atvbc6334c54/tvos),
  [Apple Support, Control playback](https://support.apple.com/guide/tv/control-music-playback-atvbef12564e/18.0/tvos)).
- **[Doc]** Library is a **split view**, not a plain grid: the top destinations stay put
  and a **left sidebar** lists the library categories — **Pins, Recently Added,
  Playlists, Artists, Albums, Songs, Music Videos, Made For You, Composers**, plus genre
  rows. Pins is the only user-ordered list and holds **at most six** items
  ([Browse your Music library](https://support.apple.com/guide/tv/browse-music-in-your-library-atvb9ed3ea39/tvos)).
- **[Doc] tvOS 26** adds a **Sing** destination (curated rows by genre, artist and
  decade). Sing also ships as its own Home Screen icon, but it is only a shortcut that
  opens Music on the Sing tab, and it can be hidden in Settings › Apps › Music
  ([Use Apple Music Sing](https://support.apple.com/guide/tv/use-apple-music-sing-atvbd965bc0d/tvos),
  [9to5Mac](https://9to5mac.com/2025/09/12/tvos-26-adds-a-new-icon-to-your-home-screen-but-its-not-a-new-app/)).
- **[Doc] tvOS 27** guidance describes **sidebar-first** navigation instead: "open the
  sidebar, then select Library" / "open the sidebar, then select Now Playing" /
  "navigate to Sing at the top of the screen" in the same document set. Sidebars are a
  supported tvOS control, so treat "top labels ⇒ sidebar" as the direction of travel
  rather than a settled Music-specific redesign
  ([Control playback, tvOS 27](https://support.apple.com/guide/tv/control-music-playback-atvbef12564e/27/tvos/27),
  [HIG Sidebars](https://developer.apple.com/design/human-interface-guidelines/sidebars)).
- **[Obs]** Selected destination reads as the brightest text; unselected items sit at a
  lower opacity. Focus expands the item and illuminates it, per the tvOS focus system.
- **[Doc] tvOS 26** replaced this chrome with Liquid Glass. Liquid Glass is explicitly the
  *functional layer*: navigation and controls float above the content layer, let artwork
  show through, and gain their glass appearance **when focus moves to them**. Apple says
  the design "keeps the focus on what's playing"; AppleInsider's hands-on describes the
  top bar as translucent and subtly warping the imagery behind it
  ([Materials](https://developer.apple.com/design/human-interface-guidelines/materials),
  [Adopting Liquid Glass](https://developer.apple.com/documentation/TechnologyOverviews/adopting-liquid-glass),
  [Newsroom](https://www.apple.com/newsroom/2025/06/apple-tv-brings-a-beautiful-redesign-and-enhanced-home-entertainment-experience),
  [AppleInsider](https://appleinsider.com/articles/25/06/18/tvos-26-hands-on-sleek-liquid-glass-redesign-new-control-center-and-more)).
- **[Doc]** Liquid Glass effects require **Apple TV 4K (2nd generation) or newer**; an
  original 4K or an Apple TV HD keeps the pre-26 appearance
  ([Apple Support, tvOS 26](https://support.apple.com/guide/tv/whats-new-in-tvos-26-atvb457e13ae/26/tvos/26),
  [AppleInsider review](https://appleinsider.com/articles/25/09/14/tvos-26-review-high-on-polish-light-on-new-features-for-apple-tv)).

## 3. Home screen

- **[Doc]** Home is a vertically scrolling stack of **horizontal shelves**. Documented
  example order: **Top Picks**, **Recently Played**, **Made for You**, **Stations for
  You**, plus additional personalised groupings (genres, decades, similar artists,
  friends' listening) ([Apple Support, Home](https://support.apple.com/guide/tv/start-listening-on-the-home-screen-atvbb2412fc9/18.0/tvos/18.0)).
- **[Doc]** Top Picks is a mixture: curated mixes, new releases, interviews, stations,
  recently updated playlists — not only albums.
- **[Obs]** Tiles are **square** covers; the title sits **beneath** the artwork with a
  secondary identifying line. No tall poster tiles mixed into the shelves, no title
  band inside the card, no greeting block above the first shelf.
- **[Doc]** The neighbouring destinations are equally shelf-shaped. **New** carries
  featured items and playlists, trending songs, new releases, recently updated Apple
  Music playlists, global and local charts, and category tiles such as Music Videos or
  Moods and Activities. **Radio** is a stack of category rows plus a **Recently Played**
  row where newly created stations land, and **Sing** is curated rows by genre, artist or
  decade ([Discover new music](https://support.apple.com/guide/tv/discover-new-music-atvbc6334c54/tvos),
  [Use Apple Music Sing](https://support.apple.com/guide/tv/use-apple-music-sing-atvbd965bc0d/tvos)).
- **[Doc]** In Library the first row is **Pins**, then the category the sidebar has
  selected; the page is a two-pane split rather than one long shelf stack
  ([Browse your Music library](https://support.apple.com/guide/tv/browse-music-in-your-library-atvb9ed3ea39/tvos)).
- **[Calc]** Because tvOS lays out on a 1920×1080pt canvas that halves onto a 960×540dp
  Android TV canvas, Apple's five-column grid (320pt items, 40pt gaps) becomes
  **160dp cards with 20dp gaps**, which puts exactly five covers across a 960dp screen
  ([Layout](https://developer.apple.com/design/human-interface-guidelines/layout)).

## 4. Album / playlist detail

- **[Doc]** Opening an album or mix shows its song list with Play, Shuffle, per-song
  selection, add, and More options ([Apple Support, Home](https://support.apple.com/guide/tv/start-listening-on-the-home-screen-atvbb2412fc9/18.0/tvos/18.0)).
- **[Doc]** Long-pressing the clickpad on any item opens the same contextual menu the rest
  of the app uses: **Go to Artist, Go to Album, Add to a Playlist, View Lyrics, Create
  Station, Play Next, Play Last, Add to Library / Delete from Library, Pin / Unpin**
  ([Control playback](https://support.apple.com/guide/tv/control-music-playback-atvbef12564e/tvos),
  [Sing](https://support.apple.com/guide/tv/use-apple-music-sing-atvbd965bc0d/tvos),
  [Pin items](https://www.idownloadblog.com/2025/07/04/how-to-pin-items-apple-music/)).
- **[Obs]** The cover is a prominent square element in the header, with album
  information and the actions (Go to Artist, Add) associated with it, track list below
  ([iMore](https://www.imore.com/how-use-apple-music-music-app-apple-tv)).
- **[Obs]** Detail pages are tinted by the artwork and fall off to black — the same
  "let the cover light the page" treatment Apple describes for the Apple TV app's
  product pages, where a blurred showcase image becomes the background
  ([Creating a tvOS media catalog app](https://developer.apple.com/documentation/swiftui/creating-a-tvos-media-catalog-app-in-swiftui)).
- **[Obs]** The currently playing row is marked; sources do not document one exact
  highlight style, so treat any specific treatment as an implementation choice.
- **[Calc]** Practical row height for a two-line track row with Body 29pt + Caption 2
  23pt is 88–96pt → **44–48dp here**, with 6–8dp between rows.

## 5. Now Playing

- **[Doc]** Playback keeps artwork front and centre; controls are a secondary layer that
  appears on demand. Pressing the clickpad shows them, and they are reachable from
  anywhere on the system through Control Center's **Now Playing** tab
  ([Control playback](https://support.apple.com/guide/tv/control-music-playback-atvbef12564e/tvos)).
- **[Doc]** The control row sits at the **bottom of the screen**, with the transport and
  the timeline; on tvOS 26 a group of secondary icons sits **bottom-left**, including the
  Apple Music Sing microphone button
  ([AppleInsider, Sing](https://appleinsider.com/articles/25/06/13/apple-music-sing-gets-karaoke-mode-visualizer-in-tvos-26),
  [See lyrics](https://support.apple.com/en-us/105015)).
- **[Doc]** With controls shown, the remote splits the secondary panels by direction:
  **swipe down** → **Info, Queue, Lyrics, SharePlay**; **swipe up** → **Favorite,
  More Options**. More Options holds the item actions (add to library or playlist, create
  station, suggest less, View Full Lyrics, Visual Effects in mic mode)
  ([Control playback](https://support.apple.com/guide/tv/control-music-playback-atvbef12564e/tvos)).
- **[Doc]** The **queue** is a horizontal row of covers with the **currently playing item
  centred**; navigate up into it, then left/right. It is unavailable for stations, live
  radio and radio episodes. Play Next / Play Last can be invoked from any item's
  long-press menu instead ([Control playback](https://support.apple.com/guide/tv/control-music-playback-atvbef12564e/tvos)).
- **[Doc]** **Lyrics** are time-synced and scroll in time with the song. While they are
  shown, navigating up or down scrubs the lyrics and surfaces a time indicator for the
  line under focus; pressing the clickpad **seeks playback to that line**, and Back
  returns to the current position. `View Full Lyrics` (including credits) is a separate
  panel under More Options
  ([Control playback](https://support.apple.com/guide/tv/control-music-playback-atvbef12564e/tvos),
  [See lyrics](https://support.apple.com/en-us/105015)).
- **[Doc] tvOS 26** adds **translation and pronunciation**: a translation line and/or a
  phonetic pronunciation line render under each lyric line, and the original can be
  hidden. Which one is set larger is a Music setting (Settings › Apps › Music ›
  Larger Text). These options only appear when the song and the device language match
  ([See lyrics](https://support.apple.com/en-us/105015)).
- **[Obs]** Large square cover, track title and artist, transport row, thin progress bar;
  the background is derived from the artwork. Animated covers play on this screen where
  the release supports it ([r/AppleMusic](https://www.reddit.com/r/AppleMusic/comments/142lli8/animated_album_art_on_apple_tv/)).
- **[Obs]** tvOS 17 did **not** change the Apple TV player's presentation; the artwork
  presentation changes seen on other Apple Music platforms did not land there
  ([BGR tvOS 17 coverage](https://www.bgr.com/guides/tvos-17/)).
- **[Obs]** In tvOS 26 the controls sit on glass surfaces and react to the moving
  scrubber; in Apple Music Sing mic mode, **More Options › Visual Effects** adds a
  background visualiser with presets named **Stellar, Neon, Sparkle**, available only
  while a phone mic is connected
  ([AppleInsider](https://appleinsider.com/articles/25/06/13/apple-music-sing-gets-karaoke-mode-visualizer-in-tvos-26)).
- **[Doc]** The pre-26 lyric affordance was different: an on-screen **Lyrics button in the
  top-right** of the player (tvOS 13–14 era). Do not mix that placement with the current
  bottom control row
  ([MacRumors](https://www.macrumors.com/how-to/enable-time-synced-lyrics-apple-music-apple-tv/)).

## 6. Focus, motion, materials

- **[Doc]** "Embrace the tvOS focus system, letting it gently highlight and expand
  onscreen items as people move among them" ([Designing for tvOS](https://developer.apple.com/design/human-interface-guidelines/designing-for-tvos)).
- **[Doc]** The focus effect combines elevation, illumination, animation and subtle
  parallax sway; focused items often expand. Keep assets large enough and leave padding
  so a focused item cannot overlap neighbours or crop essential content
  ([Focus and selection](https://developer.apple.com/design/human-interface-guidelines/focus-and-selection),
  [Images](https://developer.apple.com/design/human-interface-guidelines/images)).
- **[Doc]** Apple publishes **no numeric focus scale**. Values such as 1.05× or 1.10× are
  implementation choices. Chora's TV cards currently inherit whatever scale
  `StandardCardContainer` applies, so this is not yet a tuned value.
- **[Doc]** tvOS 26 introduces Liquid Glass — a translucent material that "reflects and
  refracts its surroundings" — used in navigation and system experiences; standard
  materials continue to define structure in the content layer
  ([Materials](https://developer.apple.com/design/human-interface-guidelines/materials)).
  Beyond the translucent bar, focus-reactive glass controls and the Sing additions there
  is no public evidence of a Music-specific tvOS 26 *layout* change.
- **[Doc]** Focused controls get the glass treatment automatically, so a button's focus
  state is now *material*, not just a ring: on tvOS the highlight effect is a projection
  plus a **specular highlight** whose offset shifts for parallax
  ([Adopting Liquid Glass](https://developer.apple.com/documentation/TechnologyOverviews/adopting-liquid-glass),
  [SwiftUI highlight](https://developer.apple.com/documentation/swiftui/customhovereffect/highlight)).
- **[Doc]** Focusable items still have **five distinct states** — unfocused, focused,
  pressed/selected-by-choice, selected/persistent (e.g. favourite filled in), and
  unavailable. Design every one of them, not just focus
  ([Focus and selection](https://developer.apple.com/design/human-interface-guidelines/focus-and-selection)).
- **[Doc]** WWDC25 also describes a cross-platform shift to **bolder, left-aligned
  typography** ([WWDC25 design system session](https://developer.apple.com/videos/play/wwdc2025/356/)).

## 7. Typography (tvOS text styles)

**[Doc]** SF Pro is the tvOS system font; NY is also available. Point sizes are based
on 72 ppi at @1x / 144 ppi at @2x
([Typography](https://developer.apple.com/design/human-interface-guidelines/typography)).

| Style | Weight | Size | Leading | Emphasized | Halved [Calc] |
|---|---|---:|---:|---|---:|
| Title 1 | Medium | 76 | 96 | Bold | 38sp |
| Title 2 | Medium | 57 | 66 | Bold | 28.5sp |
| Title 3 | Medium | 48 | 56 | Bold | 24sp |
| Headline | Medium | 38 | 46 | Bold | 19sp |
| Subtitle 1 | Regular | 38 | 46 | Medium | 19sp |
| Callout | Medium | 31 | 38 | Bold | 15.5sp |
| Body | Medium | 29 | 36 | Bold | 14.5sp |
| Caption 1 | Medium | 25 | 32 | Bold | 12.5sp |
| Caption 2 | Medium | 23 | 30 | Bold | 11.5sp |

There is no Large Title style on tvOS. Shelf captions in a living-room UI are usually
set a step or two larger than Caption 1–2 so they read from a sofa.

## 8. Layout, safe area, grid

**[Doc]** Safe area on tvOS: **80pt** left/right, **60pt** top/bottom. At 1920×1080 that
leaves a 1760×960pt content area ([Layout](https://developer.apple.com/design/human-interface-guidelines/layout)).

| Columns | Item width | Horizontal spacing | Min vertical spacing |
|---:|---:|---:|---:|
| 2 | 860 | 40 | 100 |
| 3 | 560 | 40 | 100 |
| 4 | 410 | 40 | 100 |
| 5 | 320 | 40 | 100 |
| 6 | 260 | 40 | 100 |
| 7 | 217 | 40 | 100 |
| 8 | 184 | 40 | 100 |
| 9 | 160 | 40 | 100 |

**[Doc]** Leave extra space for titled rows and keep symmetrical partial items at the
screen edges so a shelf looks intentionally cropped rather than clipped. The 100pt
vertical spacing applies to grids, not to dense song lists.

**[Doc]** If the design uses a tab bar anyway, its geometry is fixed: **height 68pt**, top
edge **46pt** from the top of the screen, translucent by default with only the selected
tab opaque; when the bar is focused the selected tab gains a drop shadow, and overflow is
faded rather than clipped
([Tab bars](https://developer.apple.com/design/human-interface-guidelines/tab-bars)).

**[Calc]** Halved for a 960×540dp canvas: 40dp side margins, 30dp top/bottom, five 160dp
columns with 20dp gaps, 50dp between grid rows.

## 9. Artwork specifications

**[Doc]** 2:3 poster asset used by Top Shelf and sectioned rows
([Top Shelf](https://developer.apple.com/design/human-interface-guidelines/top-shelf)):

| Poster | Size |
|---|---|
| Actual image | 404 × 608 pt (@1x); 808 × 1216 px (@2x) |
| Focused / safe zone | 380 × 570 pt (@1x); 760 × 1140 px (@2x) |
| Unfocused display | 333 × 570 pt (@1x); 666 × 1140 px (@2x) |

**[Doc]** Top Shelf images scale and clip as they focus, so essential foreground
content must stay inside the safe zone. Music shelf tiles are square in practice, so
this poster geometry is only relevant to promotional shelves.

## 10. What this app implements, and why

| Apple behaviour | Implementation here |
|---|---|
| Sidebar / top destinations, Now Playing appears when playing | **Left icon rail**, `TvSideNavigation` in `MainActivity.kt`: `androidx.tv.material3.NavigationDrawer` starting closed. Items: Search, then the configurable bottom-nav list (Home, Albums, Songs — off by default, Artists, Radios, Playlists), **Now Playing** only while a media item exists, Settings pinned to the bottom. 24dp icons, labels only when expanded |
| Dark canvas, colour from artwork | TV branch of `MusicPlayerTheme` uses the **stock** `androidx.tv.material3` dark scheme and default typography. `Color.kt`'s purple Material 3 tokens are phone-only; there is no black/`#FA233B` Music palette |
| Home is a shelf stack | `TvHomeScreen`: `LazyColumn` with 24dp spacing — first a **320dp-tall `Carousel`** of shuffled albums (genre • artist • duration, big title, Play), then shelves with a `headlineSmall` semibold header and a `LazyRow` of cards |
| Square covers, captions beneath | `TvAlbumCard`: **128dp** wide, square `AsyncImage`, one-line title + artist below, built on `StandardCardContainer` |
| Generous margins, five-ish cards per shelf | 48dp horizontal margin, 24dp gaps, 24dp between shelves. At 960dp wide that is five full cards plus a partial sixth |
| Five-column grids exist too | The library album list (`TvAlbumScreen`) is a `LazyVerticalGrid(GridCells.Fixed(5))` with sort/filter chips above it |
| Now Playing: artwork + lyrics, controls on demand | `TvNowPlaying` (`ui/playing/tv`): artwork 320dp tall in the left half with title / album • year / artist, `LyricsView` in the right half, bottom control column (shuffle, prev, play, next, repeat + slider, max 480dp wide) that **auto-hides after 5s**; D-pad centre/up/down reveals it, left/right seek while hidden |
| Artwork-derived backdrop | `NowPlaying_Background` renders one of `PLAIN`, `STATIC_BLUR` (palette colours), or `ANIMATED_BLUR` (gradient, API 33+) behind the TV player |
| Focus expands and illuminates | Whatever `StandardCardContainer` / tv Material components provide (focus scale + shape); no custom scale, ring, glow or parallax layers |
| Secondary panels for Info/Queue/Lyrics | Not modelled. Lyrics are a permanent right-hand pane, not a toggled panel; there is no queue, Info or More Options surface |

Type in the TV UI is the stock `androidx.tv.material3` ramp (`headlineLarge` for the
carousel title, `headlineSmall` for shelf headers, Material defaults elsewhere). `Type.kt`
only overrides the phone's `bodyLarge`.

## 11. Known gaps versus Apple

- **Navigation shape.** Apple's Music navigation is labelled destinations (and, from
  tvOS 27, a sidebar with categories). Chora's TV UI is an icon-only closed
  `NavigationDrawer` with no New, no Sing, no Library split view and no Pins.
- **Home content.** One shelf shape: square album cards from the user's own provider
  (Recently Played / Recently Added / Most Played). No Top Picks, mixes, stations,
  editorial collections, charts or Pins row.
- **Now Playing panels.** No queue row, Info panel, SharePlay, Favorite or More Options;
  no long-press item menu on the player. Lyrics are always a pane beside the cover.
- **Lyrics interaction.** `LyricsView` is shared with the phone UI: no scroll-to-seek,
  no time indicator while scrubbing, no translation or pronunciation lines, no
  full-lyrics/credits panel, no Apple Music Sing controls.
- **Materials and focus.** No Liquid Glass analogue: no backdrop refraction, no specular
  highlight on focus. Only the stock Material TV focus scale/shape — no layered parallax.
- **Backdrop.** Palette colours and an optional gradient, not a blurred, magnified copy of
  the artwork; no animated artwork.
- **Layout numbers are inferred.** Apple publishes type, safe areas and grid widths, but
  not Music's exact card sizes, gaps or cover dimensions, so section 8's conversions are
  guidance, not Music's measured layout.
- **Grid and type drift.** Card width is 128dp (not the 160dp a five-column 960dp canvas
  implies), shelf spacing is 24dp and headers use Material defaults rather than the
  halved tvOS ramp in section 7.
- **Never visually verified in CI.** The app is built and lint-checked by GitHub Actions;
  nobody has rendered these screens in this environment. The only visual evidence is
  `Github/Images/TV/*.png` and the fastlane TV screenshots.

## 12. Sources

- [Apple HIG — Designing for tvOS](https://developer.apple.com/design/human-interface-guidelines/designing-for-tvos)
- [Apple HIG — Sidebars](https://developer.apple.com/design/human-interface-guidelines/sidebars)
- [Apple HIG — Tab bars](https://developer.apple.com/design/human-interface-guidelines/tab-bars)
- [Apple Developer — Adopting Liquid Glass](https://developer.apple.com/documentation/TechnologyOverviews/adopting-liquid-glass)
- [Apple Support — Browse your Music library](https://support.apple.com/guide/tv/browse-music-in-your-library-atvb9ed3ea39/tvos)
- [Apple Support — Use Apple Music Sing](https://support.apple.com/guide/tv/use-apple-music-sing-atvbd965bc0d/tvos)
- [Apple Support — See lyrics and sing on Apple TV](https://support.apple.com/en-us/105015)
- [Apple Support — Control playback (tvOS 27)](https://support.apple.com/guide/tv/control-music-playback-atvbef12564e/27/tvos/27)
- [AppleInsider — Apple Music Sing karaoke and visualiser](https://appleinsider.com/articles/25/06/13/apple-music-sing-gets-karaoke-mode-visualizer-in-tvos-26)
- [9to5Mac — The Sing icon is a Music shortcut](https://9to5mac.com/2025/09/12/tvos-26-adds-a-new-icon-to-your-home-screen-but-its-not-a-new-app/)
- [MacRumors — Old top-right Lyrics button](https://www.macrumors.com/how-to/enable-time-synced-lyrics-apple-music-apple-tv/)
- [iDownloadBlog — Pinning in Apple Music](https://www.idownloadblog.com/2025/07/04/how-to-pin-items-apple-music/)
- [Apple HIG — Layout](https://developer.apple.com/design/human-interface-guidelines/layout)
- [Apple HIG — Typography](https://developer.apple.com/design/human-interface-guidelines/typography)
- [Apple HIG — Focus and selection](https://developer.apple.com/design/human-interface-guidelines/focus-and-selection)
- [Apple HIG — Images](https://developer.apple.com/design/human-interface-guidelines/images)
- [Apple HIG — Materials](https://developer.apple.com/design/human-interface-guidelines/materials)
- [Apple HIG — Top Shelf](https://developer.apple.com/design/human-interface-guidelines/top-shelf)
- [Apple Support — Start listening on the Home screen](https://support.apple.com/guide/tv/start-listening-on-the-home-screen-atvbb2412fc9/18.0/tvos/18.0)
- [Apple Support — Control music playback](https://support.apple.com/guide/tv/control-music-playback-atvbef12564e/18.0/tvos)
- [Apple Support — Discover new music](https://support.apple.com/guide/tv/discover-new-music-atvbc6334c54/tvos)
- [Apple Newsroom — tvOS 26 redesign](https://www.apple.com/newsroom/2025/06/apple-tv-brings-a-beautiful-redesign-and-enhanced-home-entertainment-experience)
- [AppleInsider — tvOS 26 hands-on](https://appleinsider.com/articles/25/06/18/tvos-26-hands-on-sleek-liquid-glass-redesign-new-control-center-and-more)
- [Apple Developer — Creating a tvOS media catalog app in SwiftUI](https://developer.apple.com/documentation/swiftui/creating-a-tvos-media-catalog-app-in-swiftui)
- [WWDC25 — Get to know the new design system](https://developer.apple.com/videos/play/wwdc2025/356/)
- [iMore — How to use Apple Music on Apple TV](https://www.imore.com/how-use-apple-music-music-app-apple-tv)
- [BGR — tvOS 17](https://www.bgr.com/guides/tvos-17/)
- [r/AppleMusic — animated album art on Apple TV](https://www.reddit.com/r/AppleMusic/comments/142lli8/animated_album_art_on_apple_tv/)
