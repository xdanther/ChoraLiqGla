package com.craftworks.music.ui.screens.tv.settings

import android.text.format.Formatter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.craftworks.music.R
import com.craftworks.music.managers.settings.DownloadQuality
import com.craftworks.music.ui.viewmodels.LibrarySettingsViewModel

/**
 * Apple TV's settings pages are one flat, scannable list. The download options live
 * here together so the existing Playback page keeps its transcoding and scrobble
 * threshold controls untouched.
 */
@Preview(device = "id:tv_1080p", showSystemUi = true, showBackground = true)
@Composable
fun TvS_LibraryScreen(
    viewModel: LibrarySettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val quality by viewModel.quality.collectAsStateWithLifecycle()
    val wifiOnly by viewModel.wifiOnly.collectAsStateWithLifecycle()
    val offlineMode by viewModel.offlineMode.collectAsStateWithLifecycle()
    val normalizeVolume by viewModel.normalizeVolume.collectAsStateWithLifecycle()
    val skipSilence by viewModel.skipSilence.collectAsStateWithLifecycle()
    val autoPlay by viewModel.autoPlay.collectAsStateWithLifecycle()
    val saveQueueToServer by viewModel.saveQueueToServer.collectAsStateWithLifecycle()
    val scrobbleEnabled by viewModel.scrobbleEnabled.collectAsStateWithLifecycle()
    val continuePlayingRow by viewModel.continuePlayingRow.collectAsStateWithLifecycle()
    val searchAsYouType by viewModel.searchAsYouType.collectAsStateWithLifecycle()

    val downloadedSongs by viewModel.downloadedSongs.collectAsStateWithLifecycle()
    val totalBytes by viewModel.totalBytes.collectAsStateWithLifecycle()
    val activeDownloads by viewModel.activeDownloads.collectAsStateWithLifecycle()

    var showQualityDialog by remember { mutableStateOf(false) }
    var showDownloadsDialog by remember { mutableStateOf(false) }

    val downloadIcon = ImageVector.vectorResource(R.drawable.rounded_download_24)
    val storageSummary = stringResource(
        R.string.library_downloads_summary,
        downloadedSongs.size,
        Formatter.formatShortFileSize(context, totalBytes)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 40.dp, vertical = 32.dp)
    ) {
        item {
            SettingsSwitchItem(
                title = stringResource(R.string.library_offline_mode),
                icon = downloadIcon,
                checked = offlineMode,
                onCheckedChange = { viewModel.setOfflineMode(it) }
            )
        }

        item {
            SettingsSwitchItem(
                title = stringResource(R.string.library_wifi_only),
                icon = downloadIcon,
                checked = wifiOnly,
                onCheckedChange = { viewModel.setWifiOnly(it) }
            )
        }

        item {
            SettingsButtonItem(
                title = stringResource(R.string.library_download_quality),
                subtitle = stringResource(quality.labelRes),
                icon = downloadIcon,
                onClick = { showQualityDialog = true }
            )
        }

        item {
            val subtitle = if (activeDownloads > 0) {
                stringResource(R.string.library_downloads_active, activeDownloads, storageSummary)
            } else {
                storageSummary
            }

            SettingsButtonItem(
                title = stringResource(R.string.library_downloads),
                subtitle = subtitle,
                icon = downloadIcon,
                onClick = { showDownloadsDialog = true }
            )
        }

        item {
            SettingsSwitchItem(
                title = stringResource(R.string.library_auto_play),
                icon = ImageVector.vectorResource(R.drawable.round_music_note_24),
                checked = autoPlay,
                onCheckedChange = { viewModel.setAutoPlay(it) }
            )
        }

        item {
            SettingsSwitchItem(
                title = stringResource(R.string.library_normalize_volume),
                icon = ImageVector.vectorResource(R.drawable.rounded_replace_audio_24),
                checked = normalizeVolume,
                onCheckedChange = { viewModel.setNormalizeVolume(it) }
            )
        }

        item {
            SettingsSwitchItem(
                title = stringResource(R.string.library_skip_silence),
                icon = ImageVector.vectorResource(R.drawable.rounded_play_24),
                checked = skipSilence,
                onCheckedChange = { viewModel.setSkipSilence(it) }
            )
        }

        item {
            SettingsSwitchItem(
                title = stringResource(R.string.library_save_queue_server),
                icon = ImageVector.vectorResource(R.drawable.s_m_playback),
                checked = saveQueueToServer,
                onCheckedChange = { viewModel.setSaveQueueToServer(it) }
            )
        }

        item {
            SettingsSwitchItem(
                title = stringResource(R.string.library_scrobble),
                icon = ImageVector.vectorResource(R.drawable.s_p_transcoding),
                checked = scrobbleEnabled,
                onCheckedChange = { viewModel.setScrobbleEnabled(it) }
            )
        }

        item {
            SettingsSwitchItem(
                title = stringResource(R.string.library_continue_row),
                icon = ImageVector.vectorResource(R.drawable.rounded_home_24),
                checked = continuePlayingRow,
                onCheckedChange = { viewModel.setContinuePlayingRow(it) }
            )
        }

        item {
            SettingsSwitchItem(
                title = stringResource(R.string.library_search_as_you_type),
                icon = ImageVector.vectorResource(R.drawable.rounded_search_24),
                checked = searchAsYouType,
                onCheckedChange = { viewModel.setSearchAsYouType(it) }
            )
        }
    }

    if (showQualityDialog) {
        Dialog(onDismissRequest = { showQualityDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.width(560.dp)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.library_download_quality),
                        style = MaterialTheme.typography.titleMedium
                    )

                    DownloadQuality.values().forEach { option ->
                        SettingsButtonItem(
                            title = stringResource(option.labelRes),
                            subtitle = if (option == quality) {
                                stringResource(R.string.library_selected)
                            } else {
                                ""
                            },
                            icon = downloadIcon,
                            onClick = {
                                viewModel.setQuality(option)
                                showQualityDialog = false
                            }
                        )
                    }

                    Button(onClick = { showQualityDialog = false }) {
                        Text(stringResource(R.string.library_close))
                    }
                }
            }
        }
    }

    if (showDownloadsDialog) {
        Dialog(
            onDismissRequest = { showDownloadsDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .width(760.dp)
                    .padding(24.dp)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.library_downloads),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = storageSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (downloadedSongs.isEmpty()) {
                        Text(stringResource(R.string.library_empty_downloads))
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .heightIn(max = 360.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(downloadedSongs, key = { it.songId }) { entry ->
                                SettingsButtonItem(
                                    title = entry.title,
                                    subtitle = entry.artist,
                                    icon = ImageVector.vectorResource(R.drawable.rounded_playlist_remove_24),
                                    onClick = { viewModel.deleteDownload(entry.songId) }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (downloadedSongs.isNotEmpty()) viewModel.deleteAllDownloads()
                        },
                        enabled = downloadedSongs.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.library_delete_all))
                    }

                    Button(onClick = { showDownloadsDialog = false }) {
                        Text(stringResource(R.string.library_close))
                    }
                }
            }
        }
    }
}
