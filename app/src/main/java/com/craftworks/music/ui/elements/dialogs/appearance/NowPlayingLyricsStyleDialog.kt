package com.craftworks.music.ui.elements.dialogs.appearance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.craftworks.music.R
import com.craftworks.music.ui.elements.bounceClick
import com.craftworks.music.ui.playing.NowPlayingLyricsStyle

@Composable
@Preview
fun NowPlayingLyricsStyleDialog(
    setShowDialog: (Boolean) -> Unit = { },
    selection: NowPlayingLyricsStyle = NowPlayingLyricsStyle.CLASSIC,
    onSet: (NowPlayingLyricsStyle) -> Unit = { }
) {
    val selected by remember { mutableStateOf(selection) }

    AlertDialog(
        onDismissRequest = { setShowDialog(false) },
        title = { Text(stringResource(R.string.appearance_now_playing_lyrics_style)) },
        text = {
            Column {
                NowPlayingLyricsStyle.entries.forEach { style ->
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .selectable(
                                selected = style == selected,
                                onClick = {
                                    onSet(style)
                                    setShowDialog(false)
                                },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = style == selected,
                            onClick = { onSet(style) },
                            modifier = Modifier.bounceClick()
                        )
                        Text(
                            text = stringResource(
                                when (style) {
                                    NowPlayingLyricsStyle.CLASSIC -> R.string.lyrics_style_classic
                                    NowPlayingLyricsStyle.GRADIENT -> R.string.lyrics_style_gradient
                                    NowPlayingLyricsStyle.SPOTLIGHT -> R.string.lyrics_style_spotlight
                                }
                            ),
                            fontWeight = FontWeight.Normal,
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = { }
    )
}
