package com.craftworks.music.ui.playing.background

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.craftworks.music.ui.playing.ArtworkColours
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/** The whole backdrop is a 64 px texture, exactly like Accord's 60 px blend source. */
private const val BACKDROP_SIZE = 64

/** Three box passes over 64 px is free and hides the worst of the pixel noise. */
private const val BLUR_PASSES = 3

/** How the texture moves. Each value mirrors one of the reference players. */
enum class BackdropMotion {
    /** A still plate. */
    NONE,

    /** Flamingo's method: one cover, slowly panning and zooming like a Ken Burns photo. */
    KEN_BURNS,

    /** Accord's method: three copies of the cover rotating against each other. */
    LAYERED_DRIFT,

    /** Flamingo's floating light: additive colour glows drifting over the cover. */
    FLOATING_LIGHT,

    /** Accord's quarter layers, mirrored into four slow kaleidoscope tiles. */
    KALEIDOSCOPE,

    /** The artwork's own palette flowing as broad aurora bands. */
    AURORA
}

/**
 * Builds the plate Apple Music puts behind its player: the cover, centre cropped,
 * saturated, darkened and blurred down to 64 px.
 *
 * Everything expensive happens here, once per track, off the main thread. The player
 * then draws a single stretched texture instead of recomputing full-screen gradients
 * every frame, which is what made the old wash slow on TV.
 */
fun buildArtworkBackdrop(artwork: Bitmap): ImageBitmap {
    val side = min(artwork.width, artwork.height).coerceAtLeast(1)
    val left = (artwork.width - side) / 2
    val top = (artwork.height - side) / 2

    val tiny = Bitmap.createBitmap(BACKDROP_SIZE, BACKDROP_SIZE, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(tiny)

    // Both references push saturation hard (2.5 in Accord, 3 in Flamingo) before the
    // blur, because blurring averages colour away and the result otherwise reads grey.
    val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
        colorFilter = ColorMatrixColorFilter(
            ColorMatrix().apply {
                setSaturation(2.2f)
                postConcat(
                    ColorMatrix(
                        floatArrayOf(
                            1.05f, 0f, 0f, 0f, 8f,
                            0f, 1.05f, 0f, 0f, 8f,
                            0f, 0f, 1.05f, 0f, 8f,
                            0f, 0f, 0f, 1f, 0f
                        )
                    )
                )
            }
        )
    }

    canvas.drawBitmap(
        artwork,
        Rect(left, top, left + side, top + side),
        Rect(0, 0, BACKDROP_SIZE, BACKDROP_SIZE),
        paint
    )

    // Keep the field mid-dark so white labels stay readable on every cover, the way
    // MusicKit hands the player a contrast-checked background colour.
    canvas.drawColor(0x40000000)

    boxBlur(tiny, BLUR_PASSES)
    return tiny.asImageBitmap()
}

private fun boxBlur(bitmap: Bitmap, passes: Int) {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    val scratch = IntArray(pixels.size)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    repeat(passes) {
        blurHorizontal(pixels, scratch, width, height)
        blurVertical(scratch, pixels, width, height)
    }

    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
}

private fun blurHorizontal(source: IntArray, target: IntArray, width: Int, height: Int) {
    for (y in 0 until height) {
        val row = y * width
        for (x in 0 until width) {
            val left = source[row + (x - 1).coerceAtLeast(0)]
            val centre = source[row + x]
            val right = source[row + (x + 1).coerceAtMost(width - 1)]
            target[row + x] = average(left, centre, right)
        }
    }
}

private fun blurVertical(source: IntArray, target: IntArray, width: Int, height: Int) {
    for (y in 0 until height) {
        val row = y * width
        val above = (y - 1).coerceAtLeast(0) * width
        val below = (y + 1).coerceAtMost(height - 1) * width
        for (x in 0 until width) {
            target[row + x] = average(
                source[above + x],
                source[row + x],
                source[below + x]
            )
        }
    }
}

private fun average(a: Int, b: Int, c: Int): Int {
    val alpha = ((a ushr 24) + (b ushr 24) + (c ushr 24)) / 3
    val red = (((a shr 16) and 0xFF) + ((b shr 16) and 0xFF) + ((c shr 16) and 0xFF)) / 3
    val green = (((a shr 8) and 0xFF) + ((b shr 8) and 0xFF) + ((c shr 8) and 0xFF)) / 3
    val blue = ((a and 0xFF) + (b and 0xFF) + (c and 0xFF)) / 3
    return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
}

/** One soft additive light, used by the Flamingo-style floating light plate. */
private fun DrawScope.drawGlow(
    colour: Color,
    x: Float,
    y: Float,
    radiusFactor: Float,
    alpha: Float
) {
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                colour.copy(alpha = alpha),
                colour.copy(alpha = alpha * 0.35f),
                Color.Transparent
            ),
            center = Offset(size.width * x, size.height * y),
            radius = max(size.width, size.height) * radiusFactor
        ),
        blendMode = BlendMode.Plus
    )
}

/** One mirrored, rotating quarter of the Accord-style kaleidoscope. */
private fun DrawScope.drawKaleidoscopeTile(
    image: ImageBitmap,
    column: Int,
    row: Int,
    angle: Float,
    mirrored: Boolean,
    zoom: Float,
    alpha: Float
) {
    val halfWidth = size.width / 2f
    val halfHeight = size.height / 2f
    val left = halfWidth * column
    val top = halfHeight * row
    val side = (max(size.width, size.height) * 2f).roundToInt()

    clipRect(left, top, left + halfWidth, top + halfHeight) {
        withTransform({
            translate(left + halfWidth / 2f, top + halfHeight / 2f)
            rotate(angle, Offset.Zero)
            scale(if (mirrored) -zoom else zoom, zoom, Offset.Zero)
        }) {
            drawImage(
                image = image,
                dstOffset = IntOffset(-side / 2, -side / 2),
                dstSize = IntSize(side, side),
                alpha = alpha,
                filterQuality = FilterQuality.Medium
            )
        }
    }
}

/**
 * The player backdrop.
 *
 * One 64 px texture, drawn full bleed and scaled up. Motion is confined to
 * [Modifier.graphicsLayer], so the render thread only updates layer matrices; nothing
 * is recomposed or redrawn per frame. When the cover changes, the new plate
 * crossfades in over 650 ms, the same transition both references use.
 */
@Composable
fun ArtworkBlurBackground(
    artwork: ImageBitmap?,
    colours: ArtworkColours,
    motion: BackdropMotion,
    overlayColor: Color = Color.Transparent,
    modifier: Modifier = Modifier
) {
    // The field colour sits under everything, so no transition, rotation or slow
    // artwork load can ever flash the window background through as black.
    Box(modifier.fillMaxSize().background(colours.base)) {
        if (artwork == null) {
            // Artwork has not arrived yet: keep the artwork's own colour on screen
            // instead of flashing black.
            StaticBlurBackground(
                colors = listOf(colours.base, colours.accentA, colours.accentB),
                overlayColor = Color.Transparent
            )
        } else {
            Crossfade(
                targetState = artwork,
                animationSpec = tween(650, easing = FastOutSlowInEasing),
                label = "artwork backdrop crossfade"
            ) { image ->
                when (motion) {
                    BackdropMotion.NONE -> StillPlate(image)
                    BackdropMotion.KEN_BURNS -> KenBurnsPlate(image)
                    BackdropMotion.LAYERED_DRIFT -> LayeredDriftPlate(image)
                    BackdropMotion.FLOATING_LIGHT -> FloatingLightPlate(image, colours)
                    BackdropMotion.KALEIDOSCOPE -> KaleidoscopePlate(image)
                    BackdropMotion.AURORA -> AuroraPlate(image, colours)
                }
            }
        }

        // Contrast plate: the blurred cover is bright in places, and Apple Music keeps
        // its labels white by darkening the top and bottom rather than the centre.
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.34f),
                        0.30f to Color.Black.copy(alpha = 0.08f),
                        0.62f to Color.Black.copy(alpha = 0.10f),
                        1f to Color.Black.copy(alpha = 0.58f)
                    )
                )
        )

        if (overlayColor != Color.Transparent) {
            Box(Modifier.fillMaxSize().background(overlayColor))
        }
    }
}

@Composable
private fun StillPlate(image: ImageBitmap) {
    Image(
        bitmap = image,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        filterQuality = FilterQuality.Medium,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                // Slightly larger than the frame so the 64 px texture never shows an
                // unfilled edge when the plate is cropped.
                scaleX = 1.06f
                scaleY = 1.06f
            }
    )
}

/** Flamingo's KenBurnsView: one cover, 12 s random pans, here a slow loop. */
@Composable
private fun KenBurnsPlate(image: ImageBitmap) {
    val transition = rememberInfiniteTransition(label = "ken burns")
    val zoom by transition.animateFloat(
        initialValue = 1.06f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            tween(38_000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "ken burns zoom"
    )
    val panX by transition.animateFloat(
        initialValue = -0.014f,
        targetValue = 0.014f,
        animationSpec = infiniteRepeatable(
            tween(31_000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "ken burns pan x"
    )
    val panY by transition.animateFloat(
        initialValue = 0.012f,
        targetValue = -0.012f,
        animationSpec = infiniteRepeatable(
            tween(43_000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "ken burns pan y"
    )

    Image(
        bitmap = image,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        filterQuality = FilterQuality.Medium,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = zoom
                scaleY = zoom
                translationX = panX * size.width
                translationY = panY * size.height
            }
    )
}

/**
 * Flamingo's `YosFloatingLight` and its additive `overlayEffect`, translated to
 * Compose: the cover sits dimmed underneath while two palette lights drift across
 * it. `BlendMode.Plus` is the same additive blend Flamingo uses.
 */
@Composable
private fun FloatingLightPlate(image: ImageBitmap, colours: ArtworkColours) {
    val transition = rememberInfiniteTransition(label = "floating light")
    val driftX by transition.animateFloat(
        initialValue = -0.055f,
        targetValue = 0.055f,
        animationSpec = infiniteRepeatable(
            tween(24_000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "floating light drift x"
    )
    val driftY by transition.animateFloat(
        initialValue = 0.04f,
        targetValue = -0.04f,
        animationSpec = infiniteRepeatable(
            tween(31_000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "floating light drift y"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(9_000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "floating light breathe"
    )

    Box(Modifier.fillMaxSize()) {
        Image(
            bitmap = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.Medium,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 1.12f
                    scaleY = 1.12f
                    alpha = 0.58f
                }
        )

        Canvas(Modifier.fillMaxSize()) {
            drawGlow(
                colour = colours.highlight,
                x = 0.25f + driftX,
                y = 0.30f + driftY,
                radiusFactor = 0.62f,
                alpha = 0.52f * breathe
            )
            drawGlow(
                colour = colours.accentA,
                x = 0.76f - driftX,
                y = 0.68f - driftY,
                radiusFactor = 0.58f,
                alpha = 0.44f * breathe
            )
            drawGlow(
                colour = colours.accentB,
                x = 0.5f,
                y = 0.5f,
                radiusFactor = 0.82f,
                alpha = 0.26f
            )

            // A soft vignette keeps the lights from flattening the labels at the edges.
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f)),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = max(size.width, size.height) * 0.74f
                )
            )
        }
    }
}

/**
 * The quarter-layer trick from Accord's BlendView, taken further: each quarter shows
 * a mirrored, independently rotating copy of the same plate. Because the source is
 * 64 px, four texture draws and two matrices per frame are all this costs.
 */
@Composable
private fun KaleidoscopePlate(image: ImageBitmap) {
    val transition = rememberInfiniteTransition(label = "kaleidoscope")
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(110_000, easing = LinearEasing)),
        label = "kaleidoscope spin"
    )
    val zoom by transition.animateFloat(
        initialValue = 1.45f,
        targetValue = 1.62f,
        animationSpec = infiniteRepeatable(
            tween(17_000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "kaleidoscope zoom"
    )

    Box(Modifier.fillMaxSize()) {
        Image(
            bitmap = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.Medium,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 1.08f
                    scaleY = 1.08f
                    alpha = 0.40f
                }
        )

        Canvas(Modifier.fillMaxSize()) {
            drawKaleidoscopeTile(image, 0, 0, spin, mirrored = false, zoom = zoom, alpha = 0.60f)
            drawKaleidoscopeTile(image, 1, 0, -spin * 0.82f, mirrored = true, zoom = zoom, alpha = 0.52f)
            drawKaleidoscopeTile(image, 0, 1, -spin * 1.18f, mirrored = true, zoom = zoom, alpha = 0.52f)
            drawKaleidoscopeTile(image, 1, 1, spin * 0.94f, mirrored = false, zoom = zoom, alpha = 0.60f)

            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.48f)),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = max(size.width, size.height) * 0.78f
                )
            )
        }
    }
}

/**
 * A flowing colour field built from the artwork's own palette, inspired by the
 * saturated overlays Flamingo paints over its blurred cover. Two broad diagonal bands
 * move in opposite directions behind a dimmed plate.
 */
@Composable
private fun AuroraPlate(image: ImageBitmap, colours: ArtworkColours) {
    val transition = rememberInfiniteTransition(label = "aurora")
    val travel by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(26_000, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = "aurora travel"
    )
    val tilt by transition.animateFloat(
        initialValue = -0.12f,
        targetValue = 0.12f,
        animationSpec = infiniteRepeatable(
            tween(19_000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "aurora tilt"
    )

    Box(Modifier.fillMaxSize()) {
        Image(
            bitmap = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.Medium,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = 1.10f
                    scaleY = 1.10f
                    alpha = 0.44f
                }
        )

        Canvas(Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        colours.accentA.copy(alpha = 0.58f),
                        colours.highlight.copy(alpha = 0.38f),
                        Color.Transparent
                    ),
                    start = Offset(
                        width * (-0.35f + travel * 0.55f),
                        height * (-0.12f + tilt)
                    ),
                    end = Offset(
                        width * (0.65f + travel * 0.45f),
                        height * (0.92f + tilt)
                    )
                ),
                blendMode = BlendMode.Screen
            )

            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        colours.accentB.copy(alpha = 0.46f),
                        Color.Transparent,
                        colours.accentA.copy(alpha = 0.34f)
                    ),
                    start = Offset(
                        width * (1.18f - travel * 0.5f),
                        height * (0.08f - tilt)
                    ),
                    end = Offset(
                        width * (0.18f - travel * 0.4f),
                        height * (1.08f - tilt)
                    )
                ),
                blendMode = BlendMode.Screen
            )

            drawRect(
                brush = Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.30f),
                    0.45f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.48f)
                )
            )
        }
    }
}

/**
 * Accord's BlendView, translated to Compose: a full cover plus two enlarged copies
 * spinning against each other inside a slowly counter-rotating frame. The old view
 * needed a huge RenderEffect because its source was sharp; our plate is already
 * blurred, so the same motion costs three texture draws and no blur pass.
 */
@Composable
private fun LayeredDriftPlate(image: ImageBitmap) {
    val transition = rememberInfiniteTransition(label = "layered drift")
    val baseSpin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(70_000, easing = LinearEasing)),
        label = "base spin"
    )
    val topSpin by transition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(52_000, easing = LinearEasing)),
        label = "top spin"
    )
    val frameSpin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(96_000, easing = LinearEasing)),
        label = "frame spin"
    )

    Box(Modifier.fillMaxSize()) {
        // Opaque base: even if a rotating layer exposes an edge, the cover stays on
        // screen instead of the black window behind it.
        StillPlate(image)

        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                    rotationZ = frameSpin
                    // A rotated rectangle only covers its old bounds when it is scaled
                    // by its diagonal over its shorter side. On 16:9 that is ~2.04;
                    // the old fixed 1.55 left black wedges at the corners.
                    val cover = hypot(size.width, size.height) /
                        min(size.width, size.height).coerceAtLeast(1f) * 1.02f
                    scaleX = cover
                    scaleY = cover
                }
        ) {
            StillPlate(image)

            Image(
                bitmap = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.Medium,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = baseSpin
                        scaleX = 1.9f
                        scaleY = 1.9f
                    }
            )
            Image(
                bitmap = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.Medium,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = topSpin
                        scaleX = 2.4f
                        scaleY = 2.4f
                        alpha = 0.62f
                    }
            )
            Image(
                bitmap = image,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.Medium,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = -topSpin * 0.6f
                        scaleX = 2.0f
                        scaleY = 2.0f
                        alpha = 0.45f
                    }
            )
        }
    }
}
