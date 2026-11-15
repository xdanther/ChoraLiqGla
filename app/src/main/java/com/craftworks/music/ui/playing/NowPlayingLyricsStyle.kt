package com.craftworks.music.ui.playing

/**
 * How the now-playing lyric list presents the active line.
 *
 * The first mode is the existing look. The other two are adapted from the reference
 * players: Accord's `CustomTextView` shader and Flamingo's `YosLyricView` focus blur.
 */
enum class NowPlayingLyricsStyle {
    /** The current behaviour: optional distance blur and word-synced highlighting. */
    CLASSIC,

    /** Accord's method: the active line fills with colour from left to right as it is sung. */
    GRADIENT,

    /** Flamingo's method: the active line is sharp and large while its neighbours blur away. */
    SPOTLIGHT
}
