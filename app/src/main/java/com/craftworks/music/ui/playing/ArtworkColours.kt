package com.craftworks.music.ui.playing

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin

/**
 * The colour model every now playing surface reads from.
 *
 * Apple's MusicKit hands the app a `backgroundColor` and four contrast-checked text
 * colours per artwork instead of a bag of generic swatches, and the result never looks
 * washed out: one dominant hue carries the screen, and the text colour is chosen by
 * contrast against it.
 *
 * The field is always the artwork's own dominant hue. Companions are the artwork's
 * *other* colours where it has them, and when it does not they are the same hue at a
 * different lightness — never a rotated hue, which is what turned every warm cover
 * orange in the previous version.
 */
@Immutable
data class ArtworkColours(
    /** The field colour: the artwork's dominant hue at a mid-dark OKLCH lightness. */
    val base: Color,
    /** Companions, taken from the artwork's other hues where it has them. */
    val accentA: Color,
    val accentB: Color,
    /** A lighter version of the dominant hue, used as a moving highlight. */
    val highlight: Color,
    /** Ink derived from [base]'s real luminance, never from the system theme. */
    val inkPrimary: Color,
    val inkSecondary: Color,
    val inkTertiary: Color,
    /** Whether [base] is dark enough that white ink is the readable choice. */
    val isDark: Boolean
) {
    companion object {
        val NeutralDark = ArtworkColours(
            base = Color(0xFF1F1F22),
            accentA = Color(0xFF3B3B40),
            accentB = Color(0xFF2B2B30),
            highlight = Color(0xFF4A4A50),
            inkPrimary = Color.White,
            inkSecondary = Color.White.copy(alpha = 0.74f),
            inkTertiary = Color.White.copy(alpha = 0.56f),
            isDark = true
        )

        val NeutralLight = ArtworkColours(
            base = Color(0xFFF2F2F7),
            accentA = Color(0xFFD8D8DE),
            accentB = Color(0xFFE6E6EC),
            highlight = Color(0xFFFFFFFF),
            inkPrimary = Color.Black,
            inkSecondary = Color.Black.copy(alpha = 0.74f),
            inkTertiary = Color.Black.copy(alpha = 0.56f),
            isDark = false
        )

        /** What to show before artwork arrives, or when it has no usable colour. */
        fun default(dark: Boolean): ArtworkColours = if (dark) NeutralDark else NeutralLight
    }
}

/**
 * Extracts [ArtworkColours] from album art.
 *
 * Pixels are grouped into twelve OKLCH hue bins weighted by squared chroma, so large
 * grey or black areas cannot vote. The heaviest bin is the field colour, and the next
 * two *distinct* bins become the companions, which keeps a red cover red and a blue
 * cover blue. Artwork with a single hue gets companions of that same hue at different
 * lightness instead of invented colours.
 */
object ArtworkPalette {

    private const val SAMPLE_SIZE = 48

    /**
     * Below this OKLCH chroma a pixel is treated as grey and ignored for the hue.
     *
     * The floor matters: at 0.012 the faint warm pixels in a black-and-white or
     * near-grey cover (skin, sepia, wood) still voted, and since the field colour had a
     * chroma floor of its own, every such cover came out orange. Only pixels that are
     * genuinely coloured get a say now.
     */
    private const val MIN_CHROMA = 0.03

    private const val HUE_BINS = 12

    private class Cluster(
        val hue: Double,
        val chroma: Double,
        val lightness: Double,
        val weight: Double,
        /** Share of the sampled artwork this hue actually covers, 0..1. */
        val share: Float = 0f
    )

    fun extract(bitmap: Bitmap): ArtworkColours {
        val sample = if (bitmap.width > SAMPLE_SIZE || bitmap.height > SAMPLE_SIZE) {
            Bitmap.createScaledBitmap(bitmap, SAMPLE_SIZE, SAMPLE_SIZE, true)
        } else {
            bitmap
        }

        val width = sample.width
        val height = sample.height
        if (width <= 0 || height <= 0) return ArtworkColours.default(true)

        val pixels = IntArray(width * height)
        sample.getPixels(pixels, 0, width, 0, 0, width, height)

        val binA = DoubleArray(HUE_BINS)
        val binB = DoubleArray(HUE_BINS)
        val binChroma = DoubleArray(HUE_BINS)
        val binLightness = DoubleArray(HUE_BINS)
        val binWeight = DoubleArray(HUE_BINS)
        val binCount = IntArray(HUE_BINS)
        var totalWeight = 0.0
        var lightnessSum = 0.0
        var lightnessCount = 0

        for (argb in pixels) {
            val colour = Oklch.fromArgb(argb)
            // Near black and near white carry no hue worth following.
            if (colour.l < 0.06 || colour.l > 0.97) continue

            lightnessSum += colour.l
            lightnessCount++

            if (colour.c < MIN_CHROMA) continue

            // Squaring the chroma lets one strong shape beat a large muted field.
            val w = colour.c * colour.c
            val bin = (((colour.h + PI) / (2 * PI)) * HUE_BINS)
                .toInt()
                .coerceIn(0, HUE_BINS - 1)

            binA[bin] += cos(colour.h) * w
            binB[bin] += sin(colour.h) * w
            binChroma[bin] += colour.c * w
            binLightness[bin] += colour.l * w
            binWeight[bin] += w
            binCount[bin] += 1
            totalWeight += w
        }

        val meanLightness = if (lightnessCount > 0) lightnessSum / lightnessCount else 0.5

        // Greyscale artwork: keep it neutral but still give it a body colour.
        if (totalWeight < 1e-6) {
            val lightness = (0.32 + meanLightness * 0.10).coerceIn(0.30, 0.42)
            return build(
                Cluster(hue = 0.0, chroma = 0.0, lightness = lightness, weight = 0.0),
                null,
                null
            )
        }

        val clusters = (0 until HUE_BINS)
            .filter { binWeight[it] > 0.0 }
            .map { index ->
                Cluster(
                    hue = atan2(binB[index], binA[index]),
                    chroma = binChroma[index] / binWeight[index],
                    lightness = binLightness[index] / binWeight[index],
                    weight = binWeight[index],
                    share = binCount[index].toFloat() / lightnessCount.coerceAtLeast(1)
                )
            }
            .sortedByDescending { it.weight }

        // Neighbouring bins describe the same colour, so companions have to be a real
        // distance away in hue before they count as the artwork's second colour.
        val chosen = mutableListOf<Cluster>()
        for (candidate in clusters) {
            if (chosen.size == 3) break
            if (chosen.all { hueDistance(it.hue, candidate.hue) > 0.45 }) chosen += candidate
        }

        // If nothing is actually colourful, or the winning hue is only a logo-sized
        // patch of an otherwise grey cover, stay neutral instead of tinting the whole
        // screen with it.
        if (chosen.first().chroma < MIN_CHROMA || chosen.first().share < 0.10f) {
            return build(
                Cluster(hue = 0.0, chroma = 0.0, lightness = meanLightness, weight = 0.0),
                null,
                null
            )
        }

        return build(chosen.first(), chosen.getOrNull(1), chosen.getOrNull(2))
    }

    private fun build(primary: Cluster, second: Cluster?, third: Cluster?): ArtworkColours {
        // Grey artwork keeps zero chroma: a floor here is what tinted greyscale covers.
        val baseChroma = if (primary.chroma <= 0.0) 0.0 else primary.chroma.coerceIn(0.07, 0.14)

        // The field drifts only a little with the artwork's own lightness: dark covers
        // stay dark, but never collapse to black and never wash out.
        val baseLightness = (0.38 + (primary.lightness - 0.5) * 0.06).coerceIn(0.34, 0.42)
        val base = Oklch.toColor(baseLightness, baseChroma, primary.hue)

        fun companionChroma(value: Double, min: Double, max: Double): Double =
            if (baseChroma <= 0.0) 0.0 else value.coerceIn(min, max)

        val accentA = second
            ?.let { Oklch.toColor(0.54, companionChroma(it.chroma, 0.05, 0.13), it.hue) }
            ?: Oklch.toColor(0.26, baseChroma * 0.90, primary.hue)

        val accentB = third
            ?.let { Oklch.toColor(0.46, companionChroma(it.chroma, 0.045, 0.12), it.hue) }
            ?: Oklch.toColor(0.31, baseChroma * 0.95, primary.hue)

        val highlight = Oklch.toColor(
            (baseLightness + 0.28).coerceAtMost(0.66),
            companionChroma(baseChroma * 0.5, 0.03, 0.08),
            primary.hue
        )

        val dark = base.luminance() < 0.5f
        val ink = if (dark) Color.White else Color.Black

        return ArtworkColours(
            base = base,
            accentA = accentA,
            accentB = accentB,
            highlight = highlight,
            inkPrimary = ink,
            inkSecondary = ink.copy(alpha = 0.74f),
            inkTertiary = ink.copy(alpha = 0.56f),
            isDark = dark
        )
    }

    /** Shortest distance between two hues, in radians. */
    private fun hueDistance(a: Double, b: Double): Double {
        val raw = kotlin.math.abs(a - b) % (2 * PI)
        return if (raw > PI) 2 * PI - raw else raw
    }
}

/** OKLCH, the perceptual polar form of OKLab. */
private object Oklch {

    class Lch(val l: Double, val c: Double, val h: Double)

    fun fromArgb(argb: Int): Lch {
        val r = toLinear(((argb shr 16) and 0xFF) / 255.0)
        val g = toLinear(((argb shr 8) and 0xFF) / 255.0)
        val b = toLinear((argb and 0xFF) / 255.0)

        val l = Math.cbrt(0.4122214708 * r + 0.5363325363 * g + 0.0514459929 * b)
        val m = Math.cbrt(0.2119034982 * r + 0.6806995451 * g + 0.1073969566 * b)
        val s = Math.cbrt(0.0883024619 * r + 0.2817188376 * g + 0.6299787005 * b)

        val lightness = 0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s
        val a = 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s
        val bb = 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s

        return Lch(lightness, hypot(a, bb), atan2(bb, a))
    }

    /**
     * Converts back to sRGB, reducing chroma until the colour fits the gamut so a
     * requested chroma can never clip into a different hue.
     */
    fun toColor(lightness: Double, chroma: Double, hue: Double): Color {
        var c = chroma
        repeat(24) {
            val rgb = rgbOf(lightness, c, hue)
            if (rgb.all { it >= -0.0005 && it <= 1.0005 }) {
                return pack(rgb)
            }
            c *= 0.92
        }
        return pack(rgbOf(lightness, 0.0, hue))
    }

    private fun rgbOf(lightness: Double, chroma: Double, hue: Double): DoubleArray {
        val a = chroma * cos(hue)
        val b = chroma * sin(hue)

        val l = (lightness + 0.3963377774 * a + 0.2158037573 * b).pow(3)
        val m = (lightness - 0.1055613458 * a - 0.0638541728 * b).pow(3)
        val s = (lightness - 0.0894841775 * a - 1.2914855480 * b).pow(3)

        val r = 4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s
        val g = -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s
        val bb = -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s

        return doubleArrayOf(fromLinear(r), fromLinear(g), fromLinear(bb))
    }

    private fun pack(rgb: DoubleArray): Color = Color(
        red = rgb[0].toFloat().coerceIn(0f, 1f),
        green = rgb[1].toFloat().coerceIn(0f, 1f),
        blue = rgb[2].toFloat().coerceIn(0f, 1f)
    )

    private fun toLinear(channel: Double): Double =
        if (channel <= 0.04045) channel / 12.92 else ((channel + 0.055) / 1.055).pow(2.4)

    private fun fromLinear(channel: Double): Double {
        val c = channel.coerceIn(0.0, 1.0)
        return if (c <= 0.0031308) c * 12.92 else 1.055 * c.pow(1.0 / 2.4) - 0.055
    }
}
