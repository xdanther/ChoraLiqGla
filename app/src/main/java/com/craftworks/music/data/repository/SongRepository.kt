package com.craftworks.music.data.repository

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.craftworks.music.data.model.LibraryType
import com.craftworks.music.data.model.MediaQuery
import com.craftworks.music.data.model.ScrobbleEvent
import com.craftworks.music.managers.MediaProviderManager
import com.craftworks.music.managers.OfflineDownloadManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val offlineDownloadManager: OfflineDownloadManager
) {

    suspend fun getSongs(query: MediaQuery.SongListQuery): List<MediaItem> = coroutineScope {
        MediaProviderManager.currentProvider.value?.getSongList(query)?.map { it.toMediaItem() } ?: listOf()
    }

    suspend fun getSong(songId: String): MediaItem? = coroutineScope {
        MediaProviderManager.currentProvider.value?.getSongDetail(songId)?.toMediaItem()
    }
    
    suspend fun setSongRating(
        songId: String, rating: Int = 0
    ) {
        MediaProviderManager.currentProvider.value?.setRating(listOf(songId), rating, LibraryType.SONG)
    }

    suspend fun getSimilarSongs(songId: String, count: Int) : List<MediaItem> = coroutineScope {
        MediaProviderManager.currentProvider.value?.getSimilarSongs(songId, count)?.map { it.toMediaItem() } ?: listOf()
    }

    suspend fun scrobbleSong(songId: String, position: Int, playbackRate: Float, event: ScrobbleEvent?, submission: Boolean) {
        MediaProviderManager.currentProvider.value?.scrobble(
            id=songId,
            position = position,
            playbackRate = playbackRate,
            event = event,
            submission = submission
        )
    }

    /**
     * Downloads now go through [OfflineDownloadManager], which keeps an index, reports
     * progress and makes the file available to the player without a second download.
     */
    fun downloadSong(song: MediaMetadata, template: String, playlistName: String = "{playlist}", playlistIndex: String = "{playlist_index}") {
        offlineDownloadManager.downloadSong(song, template, playlistName, playlistIndex)
    }
}
