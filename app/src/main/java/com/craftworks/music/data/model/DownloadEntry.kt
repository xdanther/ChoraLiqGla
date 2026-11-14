package com.craftworks.music.data.model

import kotlinx.serialization.Serializable

enum class DownloadStatus { QUEUED, DOWNLOADING, COMPLETED, FAILED }

/**
 * One downloaded song, keyed by the provider's song id.
 *
 * The record is deliberately flat instead of a download-node graph: Subsonic ids are
 * stable, and album/playlist state can always be derived from the songs they contain.
 */
@Serializable
data class DownloadEntry(
    val songId: String,
    val providerId: String,
    val title: String,
    val artist: String,
    val album: String? = null,
    val durationMs: Long = 0,
    val artworkUrl: String? = null,
    val filePath: String = "",
    val fileSize: Long = 0,
    val format: String = "mp3",
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val progress: Float = 0f,
    val addedAt: Long = 0,
    val error: String? = null
) {
    val isComplete: Boolean
        get() = status == DownloadStatus.COMPLETED
}
