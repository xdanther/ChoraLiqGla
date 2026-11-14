package com.craftworks.music.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.craftworks.music.data.model.DownloadEntry
import com.craftworks.music.data.model.DownloadStatus
import com.craftworks.music.managers.OfflineDownloadManager
import com.craftworks.music.managers.settings.DownloadQuality
import com.craftworks.music.managers.settings.DownloadSettingsManager
import com.craftworks.music.managers.settings.PlaybackSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibrarySettingsViewModel @Inject constructor(
    private val downloads: OfflineDownloadManager,
    private val downloadSettings: DownloadSettingsManager,
    private val playbackSettings: PlaybackSettingsManager
) : ViewModel() {

    val downloadedSongs: StateFlow<List<DownloadEntry>> = downloads.entries
        .map { entries ->
            entries.values
                .filter { it.status == DownloadStatus.COMPLETED }
                .sortedBy { it.title.lowercase() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalBytes: StateFlow<Long> = downloads.entries
        .map { entries ->
            entries.values
                .filter { it.status == DownloadStatus.COMPLETED }
                .sumOf { it.fileSize }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0L)

    val activeDownloads: StateFlow<Int> = downloads.entries
        .map { entries ->
            entries.values.count { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.QUEUED }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val quality: StateFlow<DownloadQuality> = downloadSettings.qualityFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DownloadQuality.ORIGINAL)

    val wifiOnly: StateFlow<Boolean> = downloadSettings.wifiOnlyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val offlineMode: StateFlow<Boolean> = downloadSettings.offlineModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val normalizeVolume: StateFlow<Boolean> = downloadSettings.normalizeVolumeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val skipSilence: StateFlow<Boolean> = downloadSettings.skipSilenceFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val saveQueueToServer: StateFlow<Boolean> = downloadSettings.saveQueueToServerFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val scrobbleEnabled: StateFlow<Boolean> = downloadSettings.scrobbleEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val continuePlayingRow: StateFlow<Boolean> = downloadSettings.continuePlayingRowFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val searchAsYouType: StateFlow<Boolean> = downloadSettings.searchAsYouTypeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val autoPlay: StateFlow<Boolean> = playbackSettings.autoPlayFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun setQuality(value: DownloadQuality) = viewModelScope.launch {
        downloadSettings.setQuality(value)
    }

    fun setWifiOnly(value: Boolean) = viewModelScope.launch {
        downloadSettings.setWifiOnly(value)
    }

    fun setOfflineMode(value: Boolean) = viewModelScope.launch {
        downloadSettings.setOfflineMode(value)
    }

    fun setNormalizeVolume(value: Boolean) = viewModelScope.launch {
        downloadSettings.setNormalizeVolume(value)
    }

    fun setSkipSilence(value: Boolean) = viewModelScope.launch {
        downloadSettings.setSkipSilence(value)
    }

    fun setSaveQueueToServer(value: Boolean) = viewModelScope.launch {
        downloadSettings.setSaveQueueToServer(value)
    }

    fun setScrobbleEnabled(value: Boolean) = viewModelScope.launch {
        downloadSettings.setScrobbleEnabled(value)
    }

    fun setContinuePlayingRow(value: Boolean) = viewModelScope.launch {
        downloadSettings.setContinuePlayingRow(value)
    }

    fun setSearchAsYouType(value: Boolean) = viewModelScope.launch {
        downloadSettings.setSearchAsYouType(value)
    }

    fun setAutoPlay(value: Boolean) = viewModelScope.launch {
        playbackSettings.setAutoPlay(value)
    }

    fun deleteDownload(songId: String) = downloads.delete(songId)

    fun deleteAllDownloads() = downloads.deleteAll()
}
