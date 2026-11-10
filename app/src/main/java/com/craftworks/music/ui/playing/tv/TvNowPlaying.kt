@file:OptIn(UnstableApi::class)

package com.craftworks.music.ui.playing.tv

import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.craftworks.music.R
import com.craftworks.music.data.model.id
import com.craftworks.music.data.model.providerId
import com.craftworks.music.data.repository.LyricsState
import com.craftworks.music.managers.MediaProviderManager
import com.craftworks.music.managers.settings.AppearanceSettingsManager
import com.craftworks.music.managers.settings.OLEDProtectionMode
import com.craftworks.music.player.ChoraMediaLibraryService
import com.craftworks.music.ui.elements.tv.TvHorizontalSongCard
import com.craftworks.music.ui.playing.LyricsView
import com.craftworks.music.ui.screens.tv.requestFocusOnFirstGainingVisibility
import com.craftworks.music.utils.StringUtils
import com.gigamole.composefadingedges.marqueeHorizontalFadingEdges
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

@kotlin.OptIn(FlowPreview::class)
@Preview(device = "id:tv_1080p", showBackground = true, showSystemUi = true)
@Composable
fun TvNowPlaying(
    mediaController: MediaController? = null,
    iconColor: Color = Color.Black,
    metadata: MediaMetadata? = null,
    onRefreshLyrics: () -> Unit = {}
) {
    var controlsVisible by remember { mutableStateOf(false) }
    var infoOpen by remember { mutableStateOf(false) }
    var lyricsOpen by remember { mutableStateOf(false) }

    val lyrics by LyricsState.lyrics.collectAsStateWithLifecycle()

    // Auto-hide after 5 seconds of visibility
    val interactionFlow = remember { MutableSharedFlow<Unit>(extraBufferCapacity = 1) }

    val oledProtectionMode by AppearanceSettingsManager(LocalContext.current)
        .oledProtectionMode.collectAsStateWithLifecycle(OLEDProtectionMode.OFF)

    LaunchedEffect(Unit) {
        interactionFlow
            .debounce(5000.milliseconds)
            .collect { controlsVisible = false }
    }

    val iconTextColor by animateColorAsState(
        targetValue = iconColor,
        animationSpec = tween(1000, 0, FastOutSlowInEasing),
        label = "Animated text color"
    )

    val isRadio = metadata?.mediaType == MediaMetadata.MEDIA_TYPE_RADIO_STATION
    val canShowLyrics = !isRadio && lyrics != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusable(true)
            .onKeyEvent { keyEvent ->
                if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false

                when {
                    keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_BACK && infoOpen -> {
                        infoOpen = false
                        true
                    }

                    keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_BACK && lyricsOpen -> {
                        lyricsOpen = false
                        true
                    }

                    !controlsVisible -> {
                        when (keyEvent.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_DPAD_UP -> {
                                controlsVisible = true
                                interactionFlow.tryEmit(Unit)
                                true
                            }

                            KeyEvent.KEYCODE_DPAD_LEFT -> {
                                mediaController?.seekBack()
                                true
                            }

                            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                                mediaController?.seekForward()
                                true
                            }

                            else -> false
                        }
                    }

                    else -> false
                }
            }
    ) {
        when (oledProtectionMode) {
            OLEDProtectionMode.MINIMAL -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TvHorizontalSongCard(
                        song = mediaController?.currentMediaItem ?: MediaItem.EMPTY,
                        modifier = Modifier
                            .focusable(false)
                            .graphicsLayer {
                                alpha = 0.75f
                            },
                        showTrackNumber = false,
                        onClick = { },
                        onLongClick = { }
                    )
                }
            }

            OLEDProtectionMode.LYRICS_ONLY -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 48.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = buildString {
                            append(metadata?.title)
                            append(" • ")
                            append(metadata?.albumTitle)
                            append(" • ")
                            append(metadata?.artist)
                        },
                        color = iconTextColor,
                        maxLines = 1,
                        modifier = Modifier
                            .widthIn(max = 480.dp)
                            .marqueeHorizontalFadingEdges(marqueeProvider = { Modifier.basicMarquee() })
                    )

                    if (!isRadio) {
                        Spacer(Modifier.height(16.dp))
                        TvPlaybackProgress(
                            mediaController = mediaController,
                            color = iconTextColor,
                            modifier = Modifier.widthIn(max = 480.dp)
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                    LyricsView(
                        iconTextColor,
                        true,
                        mediaController,
                        PaddingValues(24.dp),
                        onRefreshLyrics
                    )
                }
            }

            else -> {
                BoxWithConstraints(Modifier.fillMaxSize()) {
                    val coverSize = minOf(maxWidth * 0.38f, maxHeight * 0.40f)

                    // Artwork, blurred out to a colour wash behind everything
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(metadata?.artworkUri)
                            .placeholderMemoryCacheKey(metadata?.id)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = 1.25f
                                scaleY = 1.25f
                                alpha = 0.7f
                            }
                            .blur(70.dp)
                    )

                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0f to Color.Black.copy(alpha = 0.55f),
                                    0.35f to Color.Black.copy(alpha = 0.15f),
                                    1f to Color.Black.copy(alpha = 0.85f)
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 40.dp)
                            .padding(top = 28.dp, bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = metadata?.albumTitle?.toString()
                                ?.ifBlank { null }
                                ?: metadata?.artist?.toString().orEmpty(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = iconTextColor.copy(alpha = 0.78f),
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.weight(0.5f))

                        AlbumArtHero(metadata = metadata, size = coverSize)

                        Spacer(Modifier.height(18.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PlayingIndicator(
                                color = iconTextColor,
                                mediaController = mediaController
                            )
                            Text(
                                text = metadata?.title?.toString().orEmpty(),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = iconTextColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = metadata?.artist?.toString().orEmpty(),
                            fontSize = 17.sp,
                            color = iconTextColor.copy(alpha = 0.62f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.weight(1f))

                        if (!isRadio) {
                            AnimatedVisibility(visible = !controlsVisible) {
                                TvPlaybackProgress(
                                    mediaController = mediaController,
                                    color = iconTextColor
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { infoOpen = !infoOpen },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                colors = ButtonDefaults.colors(
                                    containerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    contentColor = iconTextColor,
                                    disabledContentColor = iconTextColor.copy(alpha = 0.3f)
                                )
                            ) {
                                Text(
                                    text = "Info",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = iconTextColor.copy(alpha = 0.85f)
                                )
                            }

                            Spacer(Modifier.weight(1f))

                            if (canShowLyrics) {
                                Button(
                                    onClick = { lyricsOpen = !lyricsOpen },
                                    contentPadding = PaddingValues(10.dp),
                                    colors = ButtonDefaults.colors(
                                        containerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        contentColor = iconTextColor,
                                        disabledContentColor = iconTextColor.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            if (lyricsOpen) R.drawable.lyrics_active else R.drawable.lyrics_inactive
                                        ),
                                        contentDescription = "Lyrics",
                                        tint = iconTextColor.copy(alpha = if (lyricsOpen) 1f else 0.8f),
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = infoOpen,
                        enter = fadeIn(tween(200)),
                        exit = fadeOut(tween(200)),
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        TrackInfoCard(
                            metadata = metadata,
                            color = iconTextColor,
                            modifier = Modifier.padding(start = 40.dp, bottom = 92.dp)
                        )
                    }

                    if (lyricsOpen) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 92.dp)
                                .background(Color.Black.copy(alpha = 0.62f))
                                .focusable(false)
                                .focusProperties { canFocus = false }
                        ) {
                            LyricsView(
                                iconTextColor,
                                true,
                                mediaController,
                                PaddingValues(horizontal = 96.dp, vertical = 24.dp),
                                onRefreshLyrics
                            )
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 300)
            ) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + scaleIn(
                initialScale = 0.8f,
                transformOrigin = TransformOrigin(0.5f, 1f),
            ),
            exit = fadeOut(
                animationSpec = tween(durationMillis = 300)
            ) + slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ) + scaleOut(
                targetScale = 0.8f,
                transformOrigin = TransformOrigin(0.5f, 1f)
            ),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 92.dp)
                .align(Alignment.BottomCenter)
                .focusGroup()
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .focusGroup(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusGroup()
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyDown) {
                                if (controlsVisible) {
                                    if (keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_UP || keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_BACK) {
                                        controlsVisible = false
                                        return@onKeyEvent true
                                    }
                                    interactionFlow.tryEmit(Unit)
                                }
                            }
                            false
                        },
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ChoraMediaLibraryService.getInstance()?.player?.let {
                        ShuffleButton(
                            it,
                            Modifier
                                .size(IconButtonDefaults.SmallButtonSize)
                                .focusProperties {
                                    up = FocusRequester.Cancel
                                }
                        )

                        PreviousSongButton(
                            it,
                            Modifier
                                .size(IconButtonDefaults.MediumButtonSize)
                                .focusProperties {
                                    up = FocusRequester.Cancel
                                }
                        )

                        PlayPauseButton(
                            it,
                            Modifier
                                .size(IconButtonDefaults.LargeButtonSize)
                                .requestFocusOnFirstGainingVisibility()
                                .focusProperties {
                                    up = FocusRequester.Cancel
                                }
                        )

                        NextSongButton(
                            it,
                            Modifier
                                .size(IconButtonDefaults.MediumButtonSize)
                                .focusProperties {
                                    up = FocusRequester.Cancel
                                }
                        )

                        RepeatButton(
                            it,
                            Modifier
                                .size(IconButtonDefaults.SmallButtonSize)
                                .focusProperties {
                                    up = FocusRequester.Cancel
                                }
                        )
                    }
                }

                if (!isRadio)
                    PlaybackProgressSlider(iconTextColor, mediaController)
            }
        }
    }
}

@Composable
private fun AlbumArtHero(metadata: MediaMetadata?, size: Dp) {
    val context = LocalContext.current
    val request = ImageRequest.Builder(context)
        .data(metadata?.artworkUri)
        .placeholderMemoryCacheKey(metadata?.id)
        .diskCachePolicy(CachePolicy.DISABLED)
        .memoryCachePolicy(CachePolicy.DISABLED)
        .crossfade(true)
        .build()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = request,
            contentDescription = "Album Cover Art",
            fallback = painterResource(R.drawable.placeholder),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .shadow(
                    elevation = 28.dp,
                    shape = RoundedCornerShape(10.dp),
                    clip = true,
                    ambientColor = Color.Black,
                    spotColor = Color.Black
                )
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        // Faint reflection under the artwork, the way Apple's player seats the cover
        AsyncImage(
            model = request,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter,
            modifier = Modifier
                .width(size)
                .height(size * 0.30f)
                .graphicsLayer {
                    scaleY = -1f
                    alpha = 0.2f
                }
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Black, Color.Transparent)
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }
        )
    }
}

@Composable
private fun PlayingIndicator(color: Color, mediaController: MediaController?) {
    var playing by remember { mutableStateOf(mediaController?.isPlaying == true) }

    DisposableEffect(mediaController) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
            }
        }
        mediaController?.addListener(listener)
        playing = mediaController?.isPlaying == true
        onDispose { mediaController?.removeListener(listener) }
    }

    val transition = rememberInfiniteTransition(label = "Playing indicator")
    val first by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(460), RepeatMode.Reverse),
        label = "First bar"
    )
    val second by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(tween(620), RepeatMode.Reverse),
        label = "Second bar"
    )
    val third by transition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(540), RepeatMode.Reverse),
        label = "Third bar"
    )

    Row(
        modifier = Modifier.height(16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        listOf(first, second, third).forEach { fraction ->
            Box(
                Modifier
                    .width(3.dp)
                    .fillMaxHeight(if (playing) fraction else 0.35f)
                    .clip(CircleShape)
                    .background(color.copy(alpha = if (playing) 0.9f else 0.35f))
            )
        }
    }
}

@Composable
private fun TvPlaybackProgress(
    mediaController: MediaController?,
    color: Color,
    modifier: Modifier = Modifier
) {
    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var playing by remember { mutableStateOf(false) }

    DisposableEffect(mediaController) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playing = isPlaying
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                position = newPosition.positionMs
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                duration = mediaController?.duration?.coerceAtLeast(0L) ?: 0L
            }
        }

        mediaController?.addListener(listener)
        position = mediaController?.currentPosition ?: 0L
        duration = mediaController?.duration?.coerceAtLeast(0L) ?: 0L
        playing = mediaController?.isPlaying == true

        onDispose { mediaController?.removeListener(listener) }
    }

    LaunchedEffect(mediaController, playing) {
        while (isActive && playing) {
            mediaController?.let {
                position = it.currentPosition
                duration = it.duration.coerceAtLeast(0L)
            }
            delay(500L)
        }
    }

    val fraction = if (duration > 0L) {
        (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val remaining = (duration - position).coerceAtLeast(0L)

    Column(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.22f))
        ) {
            Box(
                Modifier
                    .fillMaxWidth(fraction)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = StringUtils.formatSeconds((position / 1000L).toInt()),
                fontSize = 13.sp,
                fontWeight = FontWeight.Light,
                color = color.copy(alpha = 0.55f)
            )
            Text(
                text = "-" + StringUtils.formatSeconds((remaining / 1000L).toInt()),
                fontSize = 13.sp,
                fontWeight = FontWeight.Light,
                color = color.copy(alpha = 0.55f)
            )
        }
    }
}

@Composable
private fun TrackInfoCard(
    metadata: MediaMetadata?,
    color: Color,
    modifier: Modifier = Modifier
) {
    val format = metadata?.extras?.getString("format")?.uppercase()
    val bitrate = metadata?.extras?.getLong("bitrate")?.takeIf { it > 0 }
    val year = metadata?.recordingYear ?: 0
    val provider = MediaProviderManager.getProvider(metadata?.providerId ?: "")
        ?.providerName
        ?.let { stringResource(it) }

    Column(
        modifier = modifier
            .widthIn(max = 380.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.Black.copy(alpha = 0.66f))
            .padding(horizontal = 22.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = metadata?.albumTitle?.toString() ?: "Unknown album",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = metadata?.artist?.toString().orEmpty(),
            fontSize = 14.sp,
            color = color.copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (year > 0)
            Text(
                text = year.toString(),
                fontSize = 14.sp,
                color = color.copy(alpha = 0.55f)
            )

        val formatLine = listOfNotNull(format, bitrate?.let { "$it kbps" }, provider)
        if (formatLine.isNotEmpty())
            Text(
                text = formatLine.joinToString(" · "),
                fontSize = 13.sp,
                color = color.copy(alpha = 0.5f),
                maxLines = 1
            )
    }
}
