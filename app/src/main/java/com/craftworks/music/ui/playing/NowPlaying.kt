package com.craftworks.music.ui.playing

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.craftworks.music.data.model.Screen
import com.craftworks.music.data.model.artists
import com.craftworks.music.managers.settings.OLEDProtectionMode
import com.craftworks.music.ui.playing.tv.TvNowPlaying

enum class NowPlayingAlignment {
    LEFT, CENTER, RIGHT
}

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun NowPlayingContent(
    mediaController: MediaController? = null,
    metadata: MediaMetadata? = null,
    navHostController: NavHostController = rememberNavController(),
    viewModel: NowPlayingViewModel = viewModel()
) {
    val backgroundStyle by viewModel.backgroundStyle.collectAsStateWithLifecycle(NowPlayingBackground.STATIC_BLUR)
    val backgroundDarkMode by viewModel.isBackgroundDark.collectAsStateWithLifecycle()
    val oledProtectionMode by viewModel.oledProtectionMode.collectAsStateWithLifecycle(OLEDProtectionMode.OFF)
    val colors by viewModel.paletteColors.collectAsStateWithLifecycle()
    val artworkColours by viewModel.artworkColours.collectAsStateWithLifecycle()
    val backgroundArtwork by viewModel.backgroundArtwork.collectAsStateWithLifecycle()
    val iconTextColor by viewModel.iconTextColor.collectAsStateWithLifecycle()

    // The player is TV-only now; its uiMode never sets the night bit, so always use
    // the dark artwork colours instead of asking the system theme.
    val isSystemDark = true

    LaunchedEffect(metadata?.artworkUri, backgroundStyle) {
        viewModel.updatePaletteFromUri(metadata?.artworkUri, backgroundStyle, isSystemDark)
    }

    val targetOverlayColor = when {
        oledProtectionMode != OLEDProtectionMode.OFF -> Color.Black.copy(0.7f)
        backgroundStyle == NowPlayingBackground.KEN_BURNS -> {
            if (backgroundDarkMode) Color.Black.copy(0.2f) else Color.White.copy(0.2f)
        }
        else -> Color.Transparent
    }

    NowPlaying_Background(
        colorPalette = colors,
        backgroundStyle = backgroundStyle,
        artwork = backgroundArtwork,
        colours = artworkColours,
        overlayColor = targetOverlayColor
    )

    TvNowPlaying(
        mediaController,
        iconTextColor,
        metadata,
        palette = colors,
        colours = artworkColours,
        onDownload = { metadata?.let { viewModel.downloadSong(it) } },
        onRefreshLyrics = { viewModel.refreshLyrics(metadata) },
        onToggleFavorite = { songId, favorite -> viewModel.setSongFavorite(songId, favorite) },
        onGoToAlbum = {
            val albumId = metadata?.extras?.getString("albumId")
            if (!albumId.isNullOrBlank()) {
                navHostController.navigate(
                    Screen.AlbumDetails(albumId, metadata?.artworkUri?.toString().orEmpty())
                ) { launchSingleTop = true }
            }
        },
        onGoToArtist = {
            val artistId = metadata?.extras?.getString("artistId")
                ?: metadata?.artists?.firstOrNull()?.id
            if (!artistId.isNullOrBlank()) {
                navHostController.navigate(
                    Screen.ArtistDetails(artistId, metadata?.artworkUri?.toString())
                ) { launchSingleTop = true }
            }
        }
    )
}

@Composable
fun dpToPx(dp: Int): Int {
    return with(LocalDensity.current) { dp.dp.toPx() }.toInt()
}
