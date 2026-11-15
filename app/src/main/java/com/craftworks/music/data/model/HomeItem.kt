package com.craftworks.music.data.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeItem(
    var key: String,
    var enabled: Boolean = true
)
