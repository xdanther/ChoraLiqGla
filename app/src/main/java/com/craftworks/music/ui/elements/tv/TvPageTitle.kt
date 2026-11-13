package com.craftworks.music.ui.elements.tv

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.craftworks.music.ui.theme.TvType

/**
 * The destination title that sits above a TV list, set at tvOS Title 2 so every screen
 * reads from across the room and matches the home shelf headers.
 */
@Composable
fun TvPageTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = TvType.title2,
        color = MaterialTheme.colorScheme.onBackground,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}
