package com.craftworks.music.managers.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.craftworks.music.R
import com.craftworks.music.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** Download bitrates offered by the Library section. */
enum class DownloadQuality(val bitrate: Int?, val labelRes: Int) {
    ORIGINAL(null, R.string.library_download_quality_original),
    HIGH(320, R.string.library_download_quality_high),
    BALANCED(192, R.string.library_download_quality_balanced),
    DATA_SAVER(128, R.string.library_download_quality_data_saver);

    companion object {
        fun fromName(name: String?): DownloadQuality =
            values().find { it.name == name } ?: ORIGINAL
    }
}

/**
 * Everything the new "Library & Downloads" settings page owns.
 *
 * The defaults are the behaviour the app had before the options existed: original
 * quality downloads, normalised volume, scrobbling on, no offline mode, no server
 * queue, and the queue still resumes locally.
 */
@Singleton
class DownloadSettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val DOWNLOAD_QUALITY_KEY = stringPreferencesKey("download_quality")
        private val WIFI_ONLY_KEY = booleanPreferencesKey("download_wifi_only")
        private val OFFLINE_MODE_KEY = booleanPreferencesKey("offline_mode")
        private val NORMALIZE_VOLUME_KEY = booleanPreferencesKey("normalize_volume")
        private val SKIP_SILENCE_KEY = booleanPreferencesKey("skip_silence")
        private val SAVE_QUEUE_TO_SERVER_KEY = booleanPreferencesKey("save_queue_to_server")
        private val SCROBBLE_ENABLED_KEY = booleanPreferencesKey("scrobble_enabled")
        private val CONTINUE_PLAYING_ROW_KEY = booleanPreferencesKey("continue_playing_row")
        private val SEARCH_AS_YOU_TYPE_KEY = booleanPreferencesKey("search_as_you_type")
        private val REPLAY_GAIN_PREAMP_KEY = intPreferencesKey("replay_gain_preamp")
    }

    val qualityFlow: Flow<DownloadQuality> = context.dataStore.data.map { preferences ->
        DownloadQuality.fromName(preferences[DOWNLOAD_QUALITY_KEY])
    }

    suspend fun setQuality(quality: DownloadQuality) {
        withContext(NonCancellable) {
            context.dataStore.edit { preferences ->
                preferences[DOWNLOAD_QUALITY_KEY] = quality.name
            }
        }
    }

    val wifiOnlyFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[WIFI_ONLY_KEY] ?: true
    }

    suspend fun setWifiOnly(enabled: Boolean) {
        withContext(NonCancellable) {
            context.dataStore.edit { preferences ->
                preferences[WIFI_ONLY_KEY] = enabled
            }
        }
    }

    val offlineModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[OFFLINE_MODE_KEY] ?: false
    }

    suspend fun setOfflineMode(enabled: Boolean) {
        withContext(NonCancellable) {
            context.dataStore.edit { preferences ->
                preferences[OFFLINE_MODE_KEY] = enabled
            }
        }
    }

    val normalizeVolumeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NORMALIZE_VOLUME_KEY] ?: true
    }

    suspend fun setNormalizeVolume(enabled: Boolean) {
        withContext(NonCancellable) {
            context.dataStore.edit { preferences ->
                preferences[NORMALIZE_VOLUME_KEY] = enabled
            }
        }
    }

    val replayGainPreampFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[REPLAY_GAIN_PREAMP_KEY] ?: 0
    }

    suspend fun setReplayGainPreamp(decibels: Int) {
        withContext(NonCancellable) {
            context.dataStore.edit { preferences ->
                preferences[REPLAY_GAIN_PREAMP_KEY] = decibels.coerceIn(-12, 12)
            }
        }
    }

    val skipSilenceFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SKIP_SILENCE_KEY] ?: false
    }

    suspend fun setSkipSilence(enabled: Boolean) {
        withContext(NonCancellable) {
            context.dataStore.edit { preferences ->
                preferences[SKIP_SILENCE_KEY] = enabled
            }
        }
    }

    val saveQueueToServerFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SAVE_QUEUE_TO_SERVER_KEY] ?: false
    }

    suspend fun setSaveQueueToServer(enabled: Boolean) {
        withContext(NonCancellable) {
            context.dataStore.edit { preferences ->
                preferences[SAVE_QUEUE_TO_SERVER_KEY] = enabled
            }
        }
    }

    val scrobbleEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SCROBBLE_ENABLED_KEY] ?: true
    }

    suspend fun setScrobbleEnabled(enabled: Boolean) {
        withContext(NonCancellable) {
            context.dataStore.edit { preferences ->
                preferences[SCROBBLE_ENABLED_KEY] = enabled
            }
        }
    }

    val continuePlayingRowFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[CONTINUE_PLAYING_ROW_KEY] ?: true
    }

    suspend fun setContinuePlayingRow(enabled: Boolean) {
        withContext(NonCancellable) {
            context.dataStore.edit { preferences ->
                preferences[CONTINUE_PLAYING_ROW_KEY] = enabled
            }
        }
    }

    val searchAsYouTypeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SEARCH_AS_YOU_TYPE_KEY] ?: true
    }

    suspend fun setSearchAsYouType(enabled: Boolean) {
        withContext(NonCancellable) {
            context.dataStore.edit { preferences ->
                preferences[SEARCH_AS_YOU_TYPE_KEY] = enabled
            }
        }
    }
}
