package com.craftworks.music.ui.playing.background

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Four large colour fields over the artwork's base colour.
 *
 * The previous version faded each field to transparent with its centre outside the
 * screen, so the middle of the display was the theme background, i.e. black, and the
 * colour only showed as a pale rim. Now the base is the artwork's own field colour, the
 * centres sit inside the frame and each radius covers the whole display, so colour
 * reaches every pixel.
 */
@Stable
@Composable
fun StaticBlurBackground(
    colors: List<Color?>,
    overlayColor: Color = Color.Transparent
) {
    if (colors.isEmpty())
        return

    val animatedColors = colors.map { color ->
        animateColorAsState(
            targetValue = color ?: Color.Black,
            animationSpec = tween(durationMillis = 1500),
            label = "Animated Colors"
        ).value
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                onDrawBehind {
                    fun at(index: Int) = animatedColors[index % animatedColors.size]
                    val reach = maxOf(size.width, size.height) * 1.5f

                    fun field(color: Color, x: Float, y: Float) {
                        drawRect(
                            Brush.radialGradient(
                                colors = listOf(
                                    color.copy(alpha = 0.65f),
                                    color.copy(alpha = 0.28f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width * x, size.height * y),
                                radius = reach
                            )
                        )
                    }

                    drawRect(color = at(0))
                    field(at(1), 0.85f, 0.15f)
                    field(at(2), 0.15f, 0.85f)
                    field(at(1), 0.12f, 0.08f)
                    field(at(2), 0.9f, 0.9f)

                    drawRect(color = overlayColor)
                }
            }
    )
}
