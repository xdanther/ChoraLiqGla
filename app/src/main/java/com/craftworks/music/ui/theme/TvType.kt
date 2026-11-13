package com.craftworks.music.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Apple's tvOS text styles, halved.
 *
 * tvOS lays a 10-foot interface out on a 1920x1080 point grid; an Android TV at 1080p
 * runs at density 2, so one tvOS point is 0.5dp. Every size below is therefore the real
 * tvOS value divided by two — Large Title 76 → 38, Title 3 48 → 24, Body 29 → 14.5,
 * Caption 1 25 → 12.5 — and line heights follow in the same ratio.
 *
 * Everything stays in sp, so Compose applies the system font scale and the in-app TV
 * text size on top of these values instead of them being frozen pixels.
 */
object TvType {
    private val family = FontFamily.Default

    val largeTitle = TextStyle(
        fontFamily = family,
        fontWeight = FontWeight.Bold,
        fontSize = 38.sp,
        lineHeight = 41.sp,
        letterSpacing = (-0.4).sp
    )
    val title1 = TextStyle(
        fontFamily = family,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 38.sp,
        letterSpacing = (-0.3).sp
    )
    val title2 = TextStyle(
        fontFamily = family,
        fontWeight = FontWeight.Bold,
        fontSize = 28.5.sp,
        lineHeight = 33.sp,
        letterSpacing = (-0.2).sp
    )
    val title3 = TextStyle(
        fontFamily = family,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 29.sp
    )
    val headline = TextStyle(
        fontFamily = family,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 25.sp
    )
    val body = TextStyle(
        fontFamily = family,
        fontWeight = FontWeight.Normal,
        fontSize = 14.5.sp,
        lineHeight = 19.sp
    )
    val callout = TextStyle(
        fontFamily = family,
        fontWeight = FontWeight.Normal,
        fontSize = 12.5.sp,
        lineHeight = 16.sp
    )
    val subhead = TextStyle(
        fontFamily = family,
        fontWeight = FontWeight.Normal,
        fontSize = 11.5.sp,
        lineHeight = 15.sp
    )
    val footnote = TextStyle(
        fontFamily = family,
        fontWeight = FontWeight.Normal,
        fontSize = 10.5.sp,
        lineHeight = 14.sp
    )
    val caption1 = TextStyle(
        fontFamily = family,
        fontWeight = FontWeight.Normal,
        fontSize = 9.5.sp,
        lineHeight = 12.sp
    )
    val caption2 = TextStyle(
        fontFamily = family,
        fontWeight = FontWeight.Normal,
        fontSize = 9.sp,
        lineHeight = 11.sp
    )
}

/**
 * The TV Material token set, mapped onto [TvType] so components that ask for
 * `MaterialTheme.typography.*` (cards, list rows, buttons, dialogs) sit on the same
 * Apple ramp as the screens that name a style directly.
 *
 * Token sizes repeat where Material has more steps than Apple's scale does.
 */
val TvTypography = androidx.tv.material3.Typography(
    displayLarge = TvType.largeTitle,
    displayMedium = TvType.title1,
    displaySmall = TvType.title2,
    headlineLarge = TvType.title2,
    headlineMedium = TvType.title3,
    headlineSmall = TvType.headline,
    titleLarge = TvType.headline,
    titleMedium = TvType.body.copy(fontWeight = FontWeight.Medium),
    titleSmall = TvType.callout.copy(fontWeight = FontWeight.Medium),
    bodyLarge = TvType.body,
    bodyMedium = TvType.callout,
    bodySmall = TvType.footnote,
    labelLarge = TvType.callout.copy(fontWeight = FontWeight.Medium),
    labelMedium = TvType.footnote.copy(fontWeight = FontWeight.Medium),
    labelSmall = TvType.footnote.copy(fontWeight = FontWeight.Medium)
)
