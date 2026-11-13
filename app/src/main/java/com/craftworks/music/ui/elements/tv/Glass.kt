package com.craftworks.music.ui.elements.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp

/**
 * Liquid Glass, as tvOS 27 uses it.
 *
 * A true refraction needs the content behind the surface, and only a panel-sized
 * surface can afford it — the TV sidebar records the page into a GraphicsLayer and
 * blurs it, which is why that panel reads as real glass. For everything smaller, the
 * material is three things: a translucent tint, a specular edge that is bright along
 * the lit side and fades away, and a highlight that blooms when the remote lands on it.
 * [glassRim] draws exactly that over whatever the surface already fills, so it composes
 * with the colours a Button or Card already has.
 */
fun Modifier.glassRim(
    shape: Shape,
    focused: Boolean = false,
    strength: Float = 1f,
): Modifier {
    val s = strength.coerceIn(0f, 1f)

    return this
        .drawWithContent {
            drawContent()

            // The glass is filled through the shape's outline, so it is never clipped to
            // the node — a button can still grow on focus while its sheen stays inside
            // its own outline.
            val outline = shape.createOutline(size, layoutDirection, this)

            // The lit edge: white at the top of the surface, gone by the middle, with a
            // faint bounce of light along the bottom so the glass has thickness.
            drawOutline(
                outline = outline,
                brush = Brush.verticalGradient(
                    0f to Color.White.copy(alpha = 0.16f * s),
                    0.45f to Color.Transparent,
                    0.92f to Color.Transparent,
                    1f to Color.White.copy(alpha = 0.06f * s)
                )
            )

            // Focus: light pools under the top edge instead of the fill going solid.
            if (focused) {
                drawOutline(
                    outline = outline,
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.14f * s),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.5f, 0f),
                        radius = size.maxDimension * 0.8f
                    )
                )
            }
        }
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
                0f to Color.White.copy(alpha = 0.55f * s),
                0.55f to Color.White.copy(alpha = 0.16f * s),
                1f to Color.White.copy(alpha = 0.24f * s)
            ),
            shape = shape
        )
}

/**
 * A filled glass panel: the sheet, popover and info-card material.
 *
 * [base] is the surface the panel would have had without glass, lightened slightly at
 * the top so the fill falls away from the light source before [glassRim] adds the edge.
 */
fun Modifier.liquidGlass(
    shape: Shape,
    focused: Boolean = false,
    base: Color = Color(0xFF1C1C1E),
    strength: Float = 1f
): Modifier = this
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                lerp(base, Color.White, 0.07f).copy(alpha = 0.95f),
                base.copy(alpha = 0.93f)
            )
        ),
        shape = shape
    )
    .glassRim(shape = shape, focused = focused, strength = strength)
