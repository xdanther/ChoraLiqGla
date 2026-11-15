# Now-playing backgrounds: Accord vs Flamingo

Both players derive the full-screen backdrop from the album cover, but they do it in
different ways. The important shared decision is that **the expensive image work
happens once per track**, not once per frame.

- Accord blurs a tiny cover with a single Android `RenderEffect` and slowly rotates
  three copies of it.
- Flamingo saturates and blurs the cover in software, then animates it with a
  `KenBurnsView`.

## Accord — `BlendView`

Files:

- `app/src/main/java/org/akanework/gramophone/ui/components/BlendView.kt`
- `app/src/main/res/layout/blend_background.xml`
- used by `PlayerBottomSheet.kt` and `FullBottomSheet.kt`

### How it is built

`BlendView` is a `ConstraintLayout` that inflates `blend_background.xml`. The layout
contains:

1. `bg` — a full-screen `ImageSwitcher`.
2. `rotate_frame` — a second full-screen container.
3. `type1` — an `ImageSwitcher` occupying the **top-left quarter**.
4. `type3` — an `ImageSwitcher` occupying the **bottom-right quarter**.

Every `ImageView` is created with `CENTER_CROP`, match-parent layout params and
`LAYER_TYPE_SOFTWARE`.

### The bitmap pipeline

`setImageUri()` runs on `Dispatchers.IO`:

1. Decode the cover with `inJustDecodeBounds`, then compute an `inSampleSize` so the
   decoded image is roughly **60 px** (`PICTURE_SIZE = 60`).
2. Decode again at that sample size.
3. `enhanceBitmap()` draws the bitmap through a `ColorMatrix` with
   `setSaturation(2.5f)`.
4. Crop the top-left and bottom-right halves for the two quarter layers.
5. Push the results into the three `ImageSwitcher`s, which cross-fade over
   `VIEW_TRANSIT_DURATION = 400 ms`.

The view is then scaled up with `adjustViewScale()`:

```kotlin
val finalScale = ceil(max(screenWidth / viewWidth, screenHeight / viewHeight))
scaleX = finalScale
scaleY = finalScale
```

### The blur

The blur is one effect on the whole view, not one per image:

```kotlin
setRenderEffect(
    RenderEffect.createBlurEffect(80f, 80f, Shader.TileMode.MIRROR)
)
```

`animateBlurRadius(enlarge, duration)` animates between **60 px** and **80 px** when
the player sheet expands or collapses. Because the source is only ~60 px, the blur is
cheap compared with blurring a full-resolution cover.

### The motion

A `Handler` posts `rotationRunnable` every **34 ms**:

```kotlin
imageViewTS.rotation = (imageViewTS.rotation + 1.2f) % 360   // full background
imageViewBE.rotation = (imageViewBE.rotation + 0.67f) % 360  // quarter layer
rotateFrame.rotation = (rotateFrame.rotation - 0.6f) % 360   // containing frame
```

The result is three copies of the same saturated, blurred cover drifting and
counter-rotating at different speeds. Rotation stops when the sheet is hidden
(`stopRotationAnimation`), so a collapsed player does no work.

A `contrast_blendOverlayColor` is drawn on top in `dispatchDraw`, and the
`ImageSwitcher` fade makes track changes cross-fade.

### Performance characteristics

- One small bitmap decode per track.
- One `RenderEffect` over the view.
- Three software-layer image views rotated by a handler every 34 ms.
- No per-frame gradient or blur recomputation.
- Rotation explicitly stopped when not visible.

## Flamingo — `YosFloatingLight`

Files:

- `app/src/main/java/yos/music/player/ui/widgets/effects/YosFloatingLightView.kt`
- called from `app/src/main/java/yos/music/player/ui/pages/NowPlaying.kt`

### The bitmap pipeline

`YosFloatingLight` takes an `album: () -> Uri?` and loads it with Coil on
`Dispatchers.IO`:

1. `ImageLoader.execute(ImageRequest(data = album()))`.
2. `BitmapResolver.bitmapCompress(...)`.
3. `imageResolve(bitmap)`:
   - `ColorMatrix().setSaturation(3f)`;
   - draw the saturated bitmap;
   - overlay `0x33000000` with `PorterDuff.Mode.OVERLAY`;
   - overlay `0x40000000`;
   - `BitmapResolver.blurBitmap(bitmap, 25)`.
4. Convert to a `Drawable` and hand it to the view.

The processed drawable is rebuilt only when the album URI changes.

### The motion

If `SettingsLibrary.NowplayingBackgroundEffect` is **true**, the background is an
`AndroidView` wrapping `KenBurnsView`:

```kotlin
KenBurnsView(it).apply {
    setTransitionGenerator(
        RandomTransitionGenerator(12000, AccelerateDecelerateInterpolator())
    )
}
```

`KenBurnsView` performs the pan/zoom on the native view. Flamingo pauses it when
playback is paused, when the lifecycle is below `RESUMED`, or when the mini player is
showing, and resumes it otherwise. A small `Option` enum (`Set`, `Pause`, `Resume`,
`Init`) prevents repeating the same operation on every recomposition.

The setting defaults to **false** (`SettingLibrary.kt`), so the default path is a
static `AsyncImage` of the same processed drawable; a `0x33000000` overlay image is
faded in over non-lyric pages at alpha `0.618`.

### Performance characteristics

- One Coil load, one software blur, one drawable per track.
- `KenBurnsView` animates a single bitmap; no recomposition per frame.
- Paused outside the resumed player.

## Side-by-side

| | Accord `BlendView` | Flamingo `YosFloatingLight` |
|---|---|---|
| Source resolution | ~60 px | Compressed cover, then blurred at 25 |
| Saturation | 2.5× | 3× |
| Darkening | `contrast_blendOverlayColor` | `0x33000000` overlay + `0x40000000` |
| Blur | `RenderEffect` 60–80 px on the view | `BitmapResolver.blurBitmap(..., 25)` |
| Motion | three layers rotating at 1.2°, 0.67°, −0.6° per 34 ms | `KenBurnsView`, 12 s random transitions |
| Track change | `ImageSwitcher` fade, 400 ms | new processed drawable, Coil cross-fade |
| Pause behaviour | rotation stopped when sheet hidden | `pause()` when not playing/resumed |
| Default | enabled with the player sheet | **off** by default; static image |

## How ChoraLiqGla uses these ideas

Implementation: `app/src/main/java/com/craftworks/music/ui/playing/background/ArtworkBlur.kt`

ChoraLiqGla cannot use `RenderEffect` as the primary path (it requires API 31) and
already depends on neither RenderScript Toolkit nor KenBurnsView. The adapted method
keeps the important part of both references: **a tiny, pre-processed texture plus
cheap layer motion**.

### The shared plate

`buildArtworkBackdrop(bitmap)` runs off the main thread once per track:

1. Centre-crop the cover to a square.
2. Draw it into a **64 px** bitmap.
3. Apply saturation **2.2×** and a slight brightness lift via `ColorMatrix`.
4. Darken with `0x40000000`.
5. Run **three box-blur passes** (horizontal + vertical) over the 64 px pixels.
6. Return an `ImageBitmap`.

The player then draws this one texture full-screen; no runtime blur and no gradient
allocation per frame.

### The three styles

`NowPlayingBackground` maps to `BackdropMotion`:

| Style | Implementation |
|---|---|
| **Blurred** | `StillPlate`: one static 64 px texture, scaled 1.06× |
| **Ken Burns** | `KenBurnsPlate`: 38 s zoom (1.06→1.18) and slow pan, Flamingo-style, via `graphicsLayer` |
| **Layered drift** | `LayeredDriftPlate`: a still base plus three counter-rotating copies of the plate, Accord-style |

`LayeredDriftPlate` originally used a fixed 1.55× scale for the rotating frame. A
rotated 16:9 rectangle needs roughly **2.04×** to keep its own corners covered, so
black wedges appeared during rotation. The current code computes the scale from the
real layer size:

```kotlin
val cover = hypot(size.width, size.height) /
    min(size.width, size.height).coerceAtLeast(1f) * 1.02f
```

It also draws an opaque copy of the plate under the rotating frame and backs the whole
backdrop with the artwork's field colour, so no rotation, cross-fade or slow artwork
load can expose the black window.

### Caching

The reference code loads the cover once and keeps it. ChoraLiqGla previously disabled
Coil's disk and memory caches in `NowPlayingViewModel` and in the player artwork
views, so every track change re-downloaded the cover before the palette could be
built. Those policies are now `ENABLED`; the artwork is fetched once and reused for
the palette, the backdrop and the on-screen cover.
