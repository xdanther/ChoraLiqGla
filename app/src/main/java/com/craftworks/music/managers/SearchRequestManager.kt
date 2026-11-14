package com.craftworks.music.managers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Carries an `ACTION_SEARCH` query from the activity (TV launcher, Assistant) to the
 * search screen, which is composed after the intent has already been handled.
 */
object SearchRequestManager {
    private val _query = MutableStateFlow<String?>(null)
    val query: StateFlow<String?> = _query.asStateFlow()

    fun request(value: String) {
        _query.value = value
    }

    fun consume() {
        _query.value = null
    }
}
