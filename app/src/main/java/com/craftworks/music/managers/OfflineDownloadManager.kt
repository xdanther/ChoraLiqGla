package com.craftworks.music.managers

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.craftworks.music.data.model.DownloadEntry
import com.craftworks.music.data.model.DownloadStatus
import com.craftworks.music.data.providers.media.MediaProvider
import com.craftworks.music.data.providers.media.subsonic.SubsonicMediaProvider
import com.craftworks.music.dataStore
import com.craftworks.music.managers.settings.DownloadSettingsManager
import com.craftworks.music.managers.settings.PlaybackSettingsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * The offline engine.
 *
 * Downloads are written to app-private storage, one file per song, and recorded in a
 * DataStore index. Playback resolves the file late in the media pipeline, so a queue
 * that was built while a song was still streaming keeps working after it downloads,
 * and deleting a download does not disturb the queue either.
 */
@Singleton
class OfflineDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadSettings: DownloadSettingsManager,
    private val playbackSettings: PlaybackSettingsManager
) {
    companion object {
        private val INDEX_KEY = stringPreferencesKey("offline_downloads_index")
        private const val MAX_PARALLEL_DOWNLOADS = 2
        private const val PROGRESS_STEP = 0.01f
        private const val BUFFER_SIZE = 64 * 1024
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val semaphore = Semaphore(MAX_PARALLEL_DOWNLOADS)
    private val runningCalls = ConcurrentHashMap<String, Call>()

    private val _entries = MutableStateFlow<Map<String, DownloadEntry>>(emptyMap())
    val entries: StateFlow<Map<String, DownloadEntry>> = _entries.asStateFlow()

    init {
        scope.launch { loadIndex() }
    }

    /** Absolute path of a completed download, or null when the song is not on disk. */
    fun localPath(songId: String): String? {
        val entry = _entries.value[songId] ?: return null
        if (entry.status != DownloadStatus.COMPLETED) return null
        return entry.filePath.takeIf { it.isNotBlank() && File(it).exists() }
    }

    fun isDownloaded(songId: String): Boolean = localPath(songId) != null

    suspend fun ensureLoaded() {
        if (_entries.value.isEmpty() && context.dataStore.data.first()[INDEX_KEY] != null) {
            loadIndex()
        }
    }

    fun downloadSong(
        metadata: MediaMetadata,
        template: String,
        playlistName: String = "",
        playlistIndex: String = ""
    ) {
        // The template is kept on the call for the exported-library work; the offline
        // path itself is a hash so two songs with the same title cannot collide.
        val songId = metadata.extras?.getString("id") ?: return
        val providerId = metadata.extras?.getString("providerId") ?: return
        if (providerId.isBlank()) return
        if (_entries.value[songId]?.status == DownloadStatus.DOWNLOADING) return
        if (isDownloaded(songId)) return

        val entry = DownloadEntry(
            songId = songId,
            providerId = providerId,
            title = metadata.title?.toString().orEmpty(),
            artist = metadata.artist?.toString().orEmpty(),
            album = metadata.albumTitle?.toString(),
            durationMs = metadata.durationMs ?: 0,
            artworkUrl = metadata.artworkUri?.toString(),
            format = (metadata.extras?.getString("format") ?: "mp3").lowercase(),
            status = DownloadStatus.QUEUED,
            addedAt = System.currentTimeMillis()
        )

        publish(entry)
        persistIndex()

        scope.launch {
            semaphore.withPermit { performDownload(songId) }
        }
    }

    fun downloadItems(
        items: List<MediaItem>,
        template: String,
        playlistName: String = "",
        firstIndex: Int = 1
    ) {
        items.forEachIndexed { index, item ->
            downloadSong(item.mediaMetadata, template, playlistName, (firstIndex + index).toString())
        }
    }

    fun delete(songId: String) {
        runningCalls.remove(songId)?.cancel()
        val entry = _entries.value[songId] ?: return
        entry.filePath.takeIf { it.isNotBlank() }?.let { File(it).delete() }
        _entries.value = _entries.value - songId
        persistIndex()
    }

    fun deleteAll() {
        runningCalls.values.forEach { it.cancel() }
        runningCalls.clear()
        _entries.value.values
            .map { it.filePath }
            .filter { it.isNotBlank() }
            .forEach { File(it).delete() }
        _entries.value = emptyMap()
        persistIndex()
    }

    private suspend fun performDownload(songId: String) {
        val entry = _entries.value[songId] ?: return
        if (entry.status == DownloadStatus.COMPLETED) return

        val provider = MediaProviderManager.getProvider(entry.providerId)
        if (provider == null) {
            fail(songId, "Provider is not available")
            return
        }

        if (downloadSettings.wifiOnlyFlow.first() && !isOnUnmeteredNetwork()) {
            fail(songId, "Waiting for Wi-Fi")
            return
        }

        val quality = downloadSettings.qualityFlow.first()
        val transcodingFormat = playbackSettings.transcodingFormatFlow.first().ifBlank { "opus" }
        val transcoded = quality.bitrate != null

        val url = runCatching {
            if (transcoded) {
                provider.getStreamUrl(songId, true, quality.bitrate, transcodingFormat)
            } else {
                provider.getStreamUrl(songId, false)
            }
        }.getOrElse { error ->
            fail(songId, error.message ?: "Could not build the download URL")
            return
        }

        if (url.isBlank()) {
            fail(songId, "The provider returned an empty download URL")
            return
        }

        val extension = if (transcoded) {
            transcodingFormat.lowercase()
        } else {
            entry.format.ifBlank { "mp3" }.lowercase()
        }

        val directory = File(context.filesDir, "downloads").apply { mkdirs() }
        val fileName = "${sha1(songId)}.$extension"
        val finalFile = File(directory, fileName)
        val tempFile = File(directory, "$fileName.part")

        publish(
            entry.copy(
                status = DownloadStatus.DOWNLOADING,
                progress = 0f,
                error = null,
                filePath = finalFile.absolutePath,
                format = extension
            )
        )

        val call = clientFor(provider).newCall(Request.Builder().url(url).build())
        runningCalls[songId] = call

        try {
            call.execute().use { response ->
                if (!response.isSuccessful) {
                    fail(songId, "Server returned HTTP ${response.code}")
                    return
                }

                val body = response.body
                if (body == null) {
                    fail(songId, "The server sent an empty response")
                    return
                }

                val totalBytes = body.contentLength()
                var writtenBytes = 0L
                var lastProgress = 0f

                tempFile.outputStream().use { output ->
                    body.byteStream().use { input ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        while (true) {
                            val read = input.read(buffer)
                            if (read <= 0) break
                            output.write(buffer, 0, read)
                            writtenBytes += read

                            if (totalBytes > 0) {
                                val progress = (writtenBytes.toFloat() / totalBytes).coerceIn(0f, 1f)
                                if (progress - lastProgress >= PROGRESS_STEP) {
                                    lastProgress = progress
                                    update(songId) { it.copy(progress = progress) }
                                }
                            }
                        }
                    }
                }

                if (tempFile.length() <= 0L) {
                    tempFile.delete()
                    fail(songId, "The download was empty")
                    return
                }

                if (finalFile.exists()) finalFile.delete()
                if (!tempFile.renameTo(finalFile)) {
                    tempFile.copyTo(finalFile, overwrite = true)
                    tempFile.delete()
                }

                update(songId) {
                    it.copy(
                        status = DownloadStatus.COMPLETED,
                        progress = 1f,
                        fileSize = finalFile.length(),
                        filePath = finalFile.absolutePath,
                        error = null
                    )
                }
                persistIndex()
            }
        } catch (error: Exception) {
            if (runningCalls[songId] == call) {
                tempFile.delete()
                fail(songId, error.message ?: "Download failed")
            }
        } finally {
            runningCalls.remove(songId)
        }
    }

    private fun clientFor(provider: MediaProvider): OkHttpClient {
        val allowSelfSigned =
            (provider as? SubsonicMediaProvider)?.providerData?.allowSelfSignedCert == true

        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)

        if (allowSelfSigned) {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )

            val sslContext = SSLContext.getInstance("SSL").apply {
                init(null, trustAllCerts, SecureRandom())
            }

            builder.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
        }

        return builder.build()
    }

    private fun isOnUnmeteredNetwork(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return true

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
    }

    private suspend fun loadIndex() {
        val stored = context.dataStore.data.first()[INDEX_KEY]
        val entries = stored
            ?.let { runCatching { json.decodeFromString<List<DownloadEntry>>(it) }.getOrNull() }
            ?: emptyList()

        val restored = entries.associateBy { it.songId }.mapValues { (_, entry) ->
            if (entry.status == DownloadStatus.COMPLETED &&
                (entry.filePath.isBlank() || !File(entry.filePath).exists())
            ) {
                entry.copy(status = DownloadStatus.FAILED, progress = 0f, error = "File missing")
            } else {
                entry
            }
        }

        _entries.value = restored
    }

    private fun update(songId: String, transform: (DownloadEntry) -> DownloadEntry) {
        val current = _entries.value[songId] ?: return
        _entries.value = _entries.value + (songId to transform(current))
    }

    private fun publish(entry: DownloadEntry) {
        _entries.value = _entries.value + (entry.songId to entry)
    }

    private fun fail(songId: String, message: String) {
        update(songId) {
            it.copy(
                status = DownloadStatus.FAILED,
                progress = 0f,
                error = message
            )
        }
        persistIndex()
    }

    private fun persistIndex() {
        val snapshot = _entries.value.values.toList()
        scope.launch {
            context.dataStore.edit { preferences ->
                preferences[INDEX_KEY] = json.encodeToString(snapshot)
            }
        }
    }

    private fun sha1(value: String): String {
        val digest = MessageDigest.getInstance("SHA-1").digest(value.toByteArray())
        return digest.joinToString("") { byte -> "%02x".format(byte) }
    }
}
