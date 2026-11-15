package com.craftworks.music.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.EnumSet

enum class ActionButtonType {
    SHUFFLE, FAVORITE, PLAY_NEXT, ADD_TO_QUEUE, ADD_TO_PLAYLIST, DOWNLOAD, SEPARATOR
}

@Serializable
data class ActionButton(
    var type: ActionButtonType,
    var inMenu: Boolean,
    @Transient
    var onClick: () -> Unit = {}
) {
    fun isCompatible(flags: EnumSet<ProviderFeature>): Boolean =
        when (type) {
            ActionButtonType.FAVORITE -> flags.contains(ProviderFeature.FAVORITES)
            ActionButtonType.ADD_TO_PLAYLIST -> flags.contains(ProviderFeature.PLAYLISTS)
            ActionButtonType.DOWNLOAD -> flags.contains(ProviderFeature.DOWNLOADS)
            else -> true
        }
}
