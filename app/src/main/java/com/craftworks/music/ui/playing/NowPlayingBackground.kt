package com.craftworks.music.ui.playing

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.craftworks.music.ui.playing.background.ArtworkBlurBackground
import com.craftworks.music.ui.playing.background.BackdropMotion
import com.craftworks.music.ui.playing.background.PlainBackground

enum class NowPlayingBackground {
    PLAIN, STATIC_BLUR, ANIMATED_BLUR,

    /** The drifting cover; now the animated plate on every form factor. */
    DRIFTING_BLUR
}

@Preview
@Stable
@Composable
fun NowPlaying_Background(
    colorPalette: List<Color> = emptyList(),
    backgroundStyle: NowPlayingBackground = NowPlayingBackground.STATIC_BLUR,
    artwork: ImageBitmap? = null,
    colours: ArtworkColours = ArtworkColours.default(true),
    overlayColor: Color = Color.Transparent
) {
    if (backgroundStyle == NowPlayingBackground.PLAIN || (colorPalette.isEmpty() && artwork == null)) {
        PlainBackground()
        return
    }

    ArtworkBlurBackground(
        artwork = artwork,
        colours = colours,
        // Each style is one of the reference players' methods:
        // static = a still plate, animated = Flamingo's Ken Burns pan, drifting =
        // Accord's counter-rotating layers.
        motion = when (backgroundStyle) {
            NowPlayingBackground.STATIC_BLUR -> BackdropMotion.NONE
            NowPlayingBackground.ANIMATED_BLUR -> BackdropMotion.KEN_BURNS
            NowPlayingBackground.DRIFTING_BLUR -> BackdropMotion.LAYERED_DRIFT
            NowPlayingBackground.PLAIN -> BackdropMotion.NONE
        },
        overlayColor = overlayColor,
        modifier = Modifier.fillMaxSize()
    )
}
