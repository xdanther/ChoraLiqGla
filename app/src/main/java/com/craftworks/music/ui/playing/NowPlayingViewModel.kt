package com.craftworks.music.ui.playing

import android.content.Context
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap
import com.craftworks.music.data.repository.LyricsRepository
import com.craftworks.music.data.repository.SongRepository
import com.craftworks.music.managers.settings.AppearanceSettingsManager
import com.craftworks.music.managers.settings.MiscSettingsManager
import com.craftworks.music.managers.settings.PlaybackSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class NowPlayingViewModel @Inject constructor (
    @ApplicationContext private val context: Context,
    val songRepository: SongRepository,
    val lyricsRepository: LyricsRepository,
    val appearanceSettingsManager: AppearanceSettingsManager,
    val playbackSettingsManager: PlaybackSettingsManager,
    val miscSettingsManager: MiscSettingsManager,
) : ViewModel() {
    private val _lyricsOpen = MutableStateFlow(false)
    val lyricsOpen = _lyricsOpen.asStateFlow()

    private val _playQueueOpen = MutableStateFlow(false)
    val playQueueOpen = _playQueueOpen.asStateFlow()

    private val _detailsOpen = MutableStateFlow(false)
    val detailsOpen = _detailsOpen.asStateFlow()

    private val _sleepTimerDialogOpen = MutableStateFlow(false)
    val sleepTimerDialogOpen = _sleepTimerDialogOpen.asStateFlow()

    fun setLyricsOpen(open: Boolean) { _lyricsOpen.value = open }
    fun setPlayQueueOpen(open: Boolean) { _playQueueOpen.value = open }
    fun setDetailsOpen(open: Boolean) { _detailsOpen.value = open }
    fun setSleepTimerDialogOpen(open: Boolean) { _sleepTimerDialogOpen.value = open }

    val backgroundStyle = appearanceSettingsManager.npBackgroundFlow
    val oledProtectionMode = appearanceSettingsManager.oledProtectionMode

    private val _paletteColors = MutableStateFlow<List<Color>>(emptyList())
    val paletteColors = _paletteColors.asStateFlow()

    /** The single colour model every now playing surface reads from. */
    private val _artworkColours = MutableStateFlow(ArtworkColours.default(true))
    val artworkColours = _artworkColours.asStateFlow()

    private val _iconTextColor = MutableStateFlow<Color>(Color.White)
    val iconTextColor = _iconTextColor.asStateFlow()

    private val _isBackgroundDark = MutableStateFlow(false)
    val isBackgroundDark = _isBackgroundDark.asStateFlow()

    private val _meta = MutableStateFlow(MediaItem.EMPTY)
    val metadata = _meta.asStateFlow()

    val autoPlay = playbackSettingsManager.autoPlayFlow
    fun setAutoPlay(autoPlay: Boolean) =
        viewModelScope.launch {
            playbackSettingsManager.setAutoPlay(autoPlay)
        }

    fun refreshLyrics(mediaMetadata: MediaMetadata?) {
        viewModelScope.launch {
            lyricsRepository.getLyrics(mediaMetadata, true)
        }
    }

    fun updatePaletteFromUri(uri: Uri?, currentBackgroundStyle: NowPlayingBackground, isSystemDark: Boolean) {
        if (currentBackgroundStyle == NowPlayingBackground.PLAIN) {
            _paletteColors.value = emptyList()
            val colours = ArtworkColours.default(isSystemDark)
            _artworkColours.value = colours
            _isBackgroundDark.value = isSystemDark
            _iconTextColor.value = colours.inkPrimary
            return
        }

        viewModelScope.launch {
            val colours = uri?.let { extractColoursFromUri(it.toString(), context) }
                ?: ArtworkColours.default(isSystemDark)

            _artworkColours.value = colours
            // Base first: the shared backgrounds draw in the order they are given.
            _paletteColors.value = listOf(colours.base, colours.accentA, colours.accentB)
            _isBackgroundDark.value = colours.isDark
            // Ink is computed from the artwork against the field colour, so labels are
            // real white (or real black) instead of a tinted system onBackground.
            _iconTextColor.value = colours.inkPrimary
        }
    }

    private suspend fun extractColoursFromUri(uri: String, context: Context): ArtworkColours? = coroutineScope {
        val request = ImageRequest.Builder(context)
            .data(uri)
            .allowHardware(false)
            .diskCachePolicy(CachePolicy.DISABLED)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .build()

        val bitmap = (context.imageLoader.execute(request) as? SuccessResult)?.image?.toBitmap()

        bitmap?.let { artwork ->
            withContext(Dispatchers.Default) { ArtworkPalette.extract(artwork) }
        }
    }

    fun downloadSong(metadata: MediaMetadata) {
        viewModelScope.launch {
            songRepository.downloadSong(metadata, miscSettingsManager.downloadTemplateFlow.first())
        }
    }
}