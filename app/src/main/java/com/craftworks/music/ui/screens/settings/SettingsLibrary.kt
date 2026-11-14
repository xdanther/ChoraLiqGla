package com.craftworks.music.ui.screens.settings

import android.text.format.Formatter
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.craftworks.music.R
import com.craftworks.music.managers.settings.DownloadQuality
import com.craftworks.music.ui.elements.dialogs.dialogFocusable
import com.craftworks.music.ui.viewmodels.LibrarySettingsViewModel

/**
 * Library & Downloads: the options that belong to the offline engine, the queue and
 * volume normalisation. It sits next to Playback rather than replacing it, so every
 * existing playback option keeps its current place.
 */
@Preview(showSystemUi = false, showBackground = true)
@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun S_LibraryScreen(
    navHostController: NavHostController = rememberNavController(),
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

    var showQualityDialog by remember { mutableStateOf(false) }
    var showDownloadsDialog by remember { mutableStateOf(false) }

    val downloadIcon = ImageVector.vectorResource(R.drawable.rounded_download_24)
    val storageSummary = stringResource(
        R.string.library_downloads_summary,
        downloadedSongs.size,
        Formatter.formatShortFileSize(context, totalBytes)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.settings_library)) },
                navigationIcon = {
                    IconButton(
                        onClick = { navHostController.popBackStack() },
                        modifier = Modifier.size(56.dp, 70.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            tint = MaterialTheme.colorScheme.onBackground,
                            contentDescription = stringResource(R.string.settings_library),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .dialogFocusable()
        ) {
            Column(
                Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SettingsSection {
                    SettingsDialogButton(
                        settingsName = stringResource(R.string.library_download_quality),
                        settingsSubtitle = stringResource(quality.labelRes),
                        settingsIcon = downloadIcon,
                        toggleEvent = { showQualityDialog = true }
                    )

                    SettingsSwitch(
                        wifiOnly,
                        stringResource(R.string.library_wifi_only),
                        downloadIcon,
                        toggleEvent = { viewModel.setWifiOnly(!wifiOnly) }
                    )

                    SettingsSwitch(
                        offlineMode,
                        stringResource(R.string.library_offline_mode),
                        downloadIcon,
                        toggleEvent = { viewModel.setOfflineMode(!offlineMode) }
                    )

                    SettingsDialogButton(
                        settingsName = stringResource(R.string.library_downloads),
                        settingsSubtitle = storageSummary,
                        settingsIcon = downloadIcon,
                        toggleEvent = { showDownloadsDialog = true }
                    )
                }

                SettingsSection {
                    SettingsSwitch(
                        autoPlay,
                        stringResource(R.string.library_auto_play),
                        ImageVector.vectorResource(R.drawable.round_music_note_24),
                        toggleEvent = { viewModel.setAutoPlay(!autoPlay) }
                    )

                    SettingsSwitch(
                        normalizeVolume,
                        stringResource(R.string.library_normalize_volume),
                        ImageVector.vectorResource(R.drawable.rounded_replace_audio_24),
                        toggleEvent = { viewModel.setNormalizeVolume(!normalizeVolume) }
                    )

                    SettingsSwitch(
                        skipSilence,
                        stringResource(R.string.library_skip_silence),
                        ImageVector.vectorResource(R.drawable.rounded_play_24),
                        toggleEvent = { viewModel.setSkipSilence(!skipSilence) }
                    )
                }

                SettingsSection {
                    SettingsSwitch(
                        saveQueueToServer,
                        stringResource(R.string.library_save_queue_server),
                        ImageVector.vectorResource(R.drawable.s_m_playback),
                        toggleEvent = { viewModel.setSaveQueueToServer(!saveQueueToServer) }
                    )

                    SettingsSwitch(
                        scrobbleEnabled,
                        stringResource(R.string.library_scrobble),
                        ImageVector.vectorResource(R.drawable.s_p_transcoding),
                        toggleEvent = { viewModel.setScrobbleEnabled(!scrobbleEnabled) }
                    )
                }

                SettingsSection {
                    SettingsSwitch(
                        continuePlayingRow,
                        stringResource(R.string.library_continue_row),
                        ImageVector.vectorResource(R.drawable.rounded_home_24),
                        toggleEvent = { viewModel.setContinuePlayingRow(!continuePlayingRow) }
                    )

                    SettingsSwitch(
                        searchAsYouType,
                        stringResource(R.string.library_search_as_you_type),
                        ImageVector.vectorResource(R.drawable.rounded_search_24),
                        toggleEvent = { viewModel.setSearchAsYouType(!searchAsYouType) }
                    )
                }
            }
        }
    }

    if (showQualityDialog) {
        AlertDialog(
            onDismissRequest = { showQualityDialog = false },
            title = { Text(stringResource(R.string.library_download_quality)) },
            text = {
                Column {
                    DownloadQuality.values().forEach { option ->
                        TextButton(
                            onClick = {
                                viewModel.setQuality(option)
                                showQualityDialog = false
                            }
                        ) {
                            Text(
                                text = stringResource(option.labelRes),
                                color = if (option == quality) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showQualityDialog = false }) {
                    Text(stringResource(R.string.library_close))
                }
            }
        )
    }

    if (showDownloadsDialog) {
        AlertDialog(
            onDismissRequest = { showDownloadsDialog = false },
            title = { Text(stringResource(R.string.library_downloads)) },
            text = {
                if (downloadedSongs.isEmpty()) {
                    Text(stringResource(R.string.library_empty_downloads))
                } else {
                    LazyColumn(Modifier.heightIn(max = 320.dp)) {
                        items(downloadedSongs, key = { it.songId }) { entry ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        text = entry.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = entry.artist,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteDownload(entry.songId) }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = stringResource(R.string.library_delete_download),
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (downloadedSongs.isNotEmpty()) {
                    TextButton(onClick = { viewModel.deleteAllDownloads() }) {
                        Text(stringResource(R.string.library_delete_all))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDownloadsDialog = false }) {
                    Text(stringResource(R.string.library_close))
                }
            }
        )
    }
}

@Composable
private fun SettingsSection(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        content()
    }
}
