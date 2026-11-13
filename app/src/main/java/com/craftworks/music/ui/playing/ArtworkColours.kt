package com.craftworks.music.ui.playing

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin

/**
 * One colour model for the whole now playing screen.
 *
 * Apple's MusicKit hands the app a `backgroundColor` and four contrast-checked text
 * colours per artwork instead of a bag of generic swatches, and the result never looks
 * washed out: one dominant hue carries the screen, the rest are its rotations, and the
 * text colour is chosen by contrast against that hue.
 *
 * AndroidX `Palette` gives us the opposite: six swatches that are allowed to be
 * "light muted", so a pastel cover produced a pastel background, and the text was a
 * tinted system `onBackground` instead of white. This replaces that with a single
 * computed model in OKLCH, where lightness and chroma are chosen deliberately.
 */
@Immutable
data class ArtworkColours(
    /** The field colour: the cover's dominant hue at a mid-dark OKLCH lightness. */
    val base: Color,
    /** Companions, rotated from [base]'s hue and kept at a usable chroma. */
    val accentA: Color,
    val accentB: Color,
    /** A lighter version of the hue, used as a moving highlight. */
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
 * The dominant hue is the chroma-weighted average CSS direction of every colourful
 * pixel, which is stable on covers with a few saturated elements and falls back to
 * neutral on greyscale art. Values are then chosen rather than inherited, so a pale
 * cover still gets a saturated field and a dark cover never collapses to black.
 */
object ArtworkPalette {

    private const val SAMPLE_SIZE = 48

    /** Below this OKLCH chroma a pixel is treated as grey and ignored for the hue. */
    private const val MIN_CHROMA = 0.012

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

        var sumA = 0.0
        var sumB = 0.0
        var weightedLightness = 0.0
        var weight = 0.0
        var lightnessSum = 0.0
        var lightnessCount = 0

        for (argb in pixels) {
            val colour = Oklch.fromArgb(argb)
            // Near black and near white carry no hue worth following.
            if (colour.l < 0.06 || colour.l > 0.97) continue

            lightnessSum += colour.l
            lightnessCount++

            if (colour.c < MIN_CHROMA) continue

            // Squaring the chroma lets one strong shape beat a large grey field.
            val w = colour.c * colour.c
            sumA += cos(colour.h) * w
            sumB += sin(colour.h) * w
            weightedLightness += colour.l * w
            weight += w
        }

        val meanLightness = if (lightnessCount > 0) lightnessSum / lightnessCount else 0.5

        // Greyscale artwork: keep it neutral but still give it a body colour.
        if (weight < 1e-6) {
            val l = (0.30 + meanLightness * 0.12).coerceIn(0.30, 0.44)
            return build(l, 0.0, 0.0)
        }

        val hue = atan2(sumB, sumA)
        val coherence = hypot(sumA, sumB) / weight
        val artLightness = weightedLightness / weight

        // The field sits at light 0.40 wherever the art sits, drifting only slightly
        // with the artwork so dark and bright covers still get a colourful base.
        val baseL = (0.40 + (artLightness - 0.50) * 0.10).coerceIn(0.34, 0.44)
        // A chroma floor is what stops pastel covers from producing pastel screens.
        val baseC = (coherence * 1.15).coerceIn(0.055, 0.165)

        return build(baseL, baseC, hue)
    }

    private fun build(baseL: Double, baseC: Double, hue: Double): ArtworkColours {
        val base = Oklch.toColor(baseL, baseC, hue)
        val accentA = Oklch.toColor((baseL + 0.24).coerceAtMost(0.68), (baseC * 1.15).coerceIn(0.06, 0.19), hue + 0.55)
        val accentB = Oklch.toColor((baseL + 0.12).coerceAtMost(0.60), (baseC * 1.00).coerceIn(0.055, 0.17), hue - 0.70)
        val highlight = Oklch.toColor((baseL + 0.34).coerceAtMost(0.78), (baseC * 0.55).coerceIn(0.03, 0.12), hue + 0.12)

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
