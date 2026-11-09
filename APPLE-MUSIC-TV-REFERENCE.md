# Apple Music on Apple TV — design reference

Everything gathered about how Apple Music looks and behaves on Apple TV (tvOS 17/18
and the tvOS 26 Liquid Glass redesign), plus the measurements Apple publishes and how
they map onto this app's canvas.

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
  is 23pt, default body 29pt ([Typography](https://developer.apple.com/design/human-interface-guidelines/typography)).

## 2. Navigation

- **[Doc]/[Obs]** A horizontal row of destinations near the top edge — Home, New
  (discovery), Browse, Radio, Library, Search — with **Now Playing** appearing there
  when music is playing. Apple's guides put Home and Now Playing "at the top of the
  screen"; reviews describe it as a "top menu". Not a detached phone-style capsule
  ([Apple Support, Discover new music](https://support.apple.com/guide/tv/discover-new-music-atvbc6334c54/tvos),
  [Apple Support, Control playback](https://support.apple.com/guide/tv/control-music-playback-atvbef12564e/18.0/tvos)).
- **[Obs]** Selected destination reads as the brightest text; unselected items sit at a
  lower opacity. Focus expands the item and illuminates it, per the tvOS focus system.
- **[Doc] tvOS 26** replaced this chrome with Liquid Glass: Applenotes the design "keeps
  the focus on what's playing" and AppleInsider's hands-on describes the **top bar as
  translucent and subtly warping the imagery behind it**
  ([Newsroom](https://www.apple.com/newsroom/2025/06/apple-tv-brings-a-beautiful-redesign-and-enhanced-home-entertainment-experience),
  [AppleInsider](https://appleinsider.com/articles/25/06/18/tvos-26-hands-on-sleek-liquid-glass-redesign-new-control-center-and-more)).

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
- **[Calc]** Because tvOS lays out on a 1920×1080pt canvas that halves onto a 960×540dp
  Android TV canvas, Apple's five-column grid (320pt items, 40pt gaps) becomes
  **160dp cards with 20dp gaps**, which puts exactly five covers across a 960dp screen
  ([Layout](https://developer.apple.com/design/human-interface-guidelines/layout)).

## 4. Album / playlist detail

- **[Doc]** Opening an album or mix shows its song list with Play, Shuffle, per-song
  selection, add, and More options ([Apple Support, Home](https://support.apple.com/guide/tv/start-listening-on-the-home-screen-atvbb2412fc9/18.0/tvos/18.0)).
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

- **[Doc]** Playback keeps artwork front and centre; the controls are secondary layers.
  On tvOS 18, showing playback controls and swiping **down** reveals **Info, Queue,
  Lyrics, SharePlay**; swiping **up** reveals **Favorite and More Options**. Lyrics are
  real-time on tvOS 26, with translation and pronunciation for Apple Music Sing
  ([Apple Support, Control playback](https://support.apple.com/guide/tv/control-music-playback-atvbef12564e/18.0/tvos),
  [Newsroom](https://www.apple.com/newsroom/2025/06/apple-tv-brings-a-beautiful-redesign-and-enhanced-home-entertainment-experience)).
- **[Obs]** Large square cover, track title and artist, transport row, thin progress
  bar; the background is derived from the artwork. Animated covers play on this screen
  where the release supports it ([r/AppleMusic](https://www.reddit.com/r/AppleMusic/comments/142lli8/animated_album_art_on_apple_tv/)).
- **[Obs]** tvOS 17 did **not** change the Apple TV player's presentation; the artwork
  presentation changes seen on other Apple Music platforms did not land there
  ([BGR tvOS 17 coverage](https://www.bgr.com/guides/tvos-17/)).
- **[Obs]** The redesigned playback controls sit on glass-like surfaces; buttons react
  to the moving scrubber ([AppleInsider](https://appleinsider.com/articles/25/06/18/tvos-26-hands-on-sleek-liquid-glass-redesign-new-control-center-and-more)).

## 6. Focus, motion, materials

- **[Doc]** "Embrace the tvOS focus system, letting it gently highlight and expand
  onscreen items as people move among them" ([Designing for tvOS](https://developer.apple.com/design/human-interface-guidelines/designing-for-tvos)).
- **[Doc]** The focus effect combines elevation, illumination, animation and subtle
  parallax sway; focused items often expand. Keep assets large enough and leave padding
  so a focused item cannot overlap neighbours or crop essential content
  ([Focus and selection](https://developer.apple.com/design/human-interface-guidelines/focus-and-selection),
  [Images](https://developer.apple.com/design/human-interface-guidelines/images)).
- **[Doc]** Apple publishes **no numeric focus scale**. Values such as 1.05× or 1.10× are
  implementation choices; this app uses 1.08 with a white in-bounds ring.
- **[Doc]** tvOS 26 introduces Liquid Glass — a translucent material that "reflects and
  refracts its surroundings" — used in navigation and system experiences; standard
  materials continue to define structure in the content layer
  ([Materials](https://developer.apple.com/design/human-interface-guidelines/materials)).
  There is no public evidence of a Music-specific tvOS 26 layout change beyond the
  translucent top bar and glass playback controls.
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
| Black canvas, white type, colour from artwork | `Background #000000`, `Surface #1C1C1E`, `OnSurface #FFFFFF`, `OnSurfaceMuted #A1A1A8`, Apple Music red `#FA233B` for state only (`Theme.kt`) |
| Translucent top navigation, labels on the margin | Full-width bar, tabs left-aligned at 40dp, active tab white semibold, others 60% white, focus = white pill with black type (`TopBar.kt`) |
| Square covers, captions beneath | `SquareArtTile` + `TileCaption`; the old 2:3 poster with a colour-band footer is gone (`Cards.kt`) |
| Five-column grid | `Space.cardWidth = 160dp`, `Space.rowSpacing = 20dp`, `Space.screenHorizontal = 40dp` |
| Safe area 80/60pt | 40dp sides, 30dp top/bottom |
| Home starts on the first shelf | No greeting item, no hero block (`HomeScreen.kt`) |
| Persistent playback, artwork-tinted detail | Flush full-width bar with a hairline progress line (`MiniPlayer.kt`); album page tinted by the cover's dominant colour falling to black (`AlbumScreen.kt`) |
| Focus expands and illuminates | 1.08 scale, white in-bounds ring with a soft dark outer edge, lift shadow only while focused (`Focusable.kt`) |
| Secondary layers for Info/Queue/Lyrics | Bottom bar of Now Playing opens overlays instead of competing with the cover (`NowPlayingScreen.kt`) |

Type used here, derived from the halved table: page title 34sp bold, shelf header 22sp
semibold, card title 16sp medium, secondary 14sp normal, body 17sp.

## 11. Known gaps versus Apple

- **Backdrop blur.** Liquid Glass relies on real-time backdrop blur; Compose here
  approximates it with a translucent dark fill and a gradient scrim.
- **Parallax.** Only scale + ring + shadow are implemented; no layered-artwork tilt.
- **Artwork animation.** Animated covers are not rendered; Coil loads static images.
- **Layout numbers are inferred.** Apple publishes type, safe areas and grid widths,
  but not Music's exact card sizes, gaps or cover dimensions, so the values in section
  10 are conversions plus judgement.
- **Never visually verified in CI.** The app is built and lint-checked by GitHub
  Actions; nobody has rendered these screens in this environment.

## 12. Sources

- [Apple HIG — Designing for tvOS](https://developer.apple.com/design/human-interface-guidelines/designing-for-tvos)
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
