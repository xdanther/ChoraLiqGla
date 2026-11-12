@file:OptIn(UnstableApi::class)

package com.craftworks.music.ui.playing.tv

import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.snap
import com.craftworks.music.ui.playing.NowPlayingAlignment
import com.craftworks.music.ui.playing.NowPlayingBackground
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/** How long the player bar lingers after the last remote key press. */
private const val BAR_AUTO_HIDE_MILLIS = 6_500L

@Preview(device = "id:tv_1080p", showBackground = true, showSystemUi = true)
@Composable
fun TvNowPlaying(
    mediaController: MediaController? = null,
    iconColor: Color = Color.Black,
    metadata: MediaMetadata? = null,
    onGoToAlbum: () -> Unit = {},
    onDownload: () -> Unit = {},
    onRefreshLyrics: () -> Unit = {}
) {
    val context = LocalContext.current
    val settings = remember(context) { AppearanceSettingsManager(context) }

    val showAppleBar by settings.showApplePlayerBar.collectAsStateWithLifecycle(true)
    val showClassicBar by settings.showClassicPlayerBar.collectAsStateWithLifecycle(false)
    val largeLyrics by settings.largeLyricsText.collectAsStateWithLifecycle(true)
    val appleLyrics by settings.appleTvLyrics.collectAsStateWithLifecycle(false)
    val oledProtectionMode by settings.oledProtectionMode.collectAsStateWithLifecycle(OLEDProtectionMode.OFF)
    val backgroundStyle by settings.npBackgroundFlow
        .collectAsStateWithLifecycle(NowPlayingBackground.STATIC_BLUR)

    var barVisible by remember { mutableStateOf(false) }
    var infoOpen by remember { mutableStateOf(false) }
    var lyricsOpen by remember { mutableStateOf(false) }
    var sleepTimerOpen by remember { mutableStateOf(false) }
    var lastInteraction by remember { mutableLongStateOf(0L) }

    val lyrics by LyricsState.lyrics.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val pageFocus = remember { FocusRequester() }
    val barFocus = remember { FocusRequester() }
    val classicBarFocus = remember { FocusRequester() }
    var barFocusTicket by remember { mutableIntStateOf(0) }
    var barOwnsFocus by remember { mutableStateOf(false) }

    // The bar is a visiting guest: it fades away again so the artwork stays the main thing.
    LaunchedEffect(barVisible, lastInteraction, infoOpen) {
        if (!barVisible) return@LaunchedEffect
        if (infoOpen) return@LaunchedEffect
        delay(BAR_AUTO_HIDE_MILLIS)
        barVisible = false
    }

    // Focus can only follow the bar once its nodes exist, so it waits for composition
    LaunchedEffect(barVisible, showAppleBar) {
        if (barVisible) {
            if (showAppleBar) barFocus.requestFocusSafely() else classicBarFocus.requestFocusSafely()
        } else {
            pageFocus.requestFocusSafely()
        }
    }

    LaunchedEffect(barFocusTicket) {
        if (barFocusTicket > 0) barFocus.requestFocusSafely()
    }

    val iconTextColor by animateColorAsState(
        targetValue = iconColor,
        animationSpec = tween(1000, 0, FastOutSlowInEasing),
        label = "Animated text color"
    )

    // A slow, endless drift over the blurred cover, the way the iOS player breathes
    val drift = rememberInfiniteTransition(label = "Backdrop drift")
    val driftScale by drift.animateFloat(
        initialValue = 1.18f,
        targetValue = 1.34f,
        animationSpec = infiniteRepeatable(tween(26_000, easing = LinearEasing), RepeatMode.Reverse),
        label = "drift scale"
    )
    val driftX by drift.animateFloat(
        initialValue = -22f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(tween(34_000, easing = LinearEasing), RepeatMode.Reverse),
        label = "drift x"
    )
    val driftY by drift.animateFloat(
        initialValue = 14f,
        targetValue = -14f,
        animationSpec = infiniteRepeatable(tween(30_000, easing = LinearEasing), RepeatMode.Reverse),
        label = "drift y"
    )
    val isRadio = metadata?.mediaType == MediaMetadata.MEDIA_TYPE_RADIO_STATION
    val canShowLyrics = !isRadio && lyrics != null

    // A song with lyrics opens them on its own; closing them sticks for that track
    LaunchedEffect(canShowLyrics, lyrics) {
        if (canShowLyrics) lyricsOpen = true
    }

    fun revealBar() {
        lastInteraction = System.nanoTime()
        barVisible = true
    }

    fun hideBar() {
        barVisible = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // The plain, static and animated styles are drawn by NowPlaying_Background;
            // only the drifting style needs an opaque base here.
            .background(
                if (backgroundStyle == NowPlayingBackground.DRIFTING_BLUR) Color.Black
                else Color.Transparent
            )
            .focusRequester(pageFocus)
            .focusable(true)
            .onKeyEvent { keyEvent ->
                if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false
                val keyCode = keyEvent.nativeKeyEvent.keyCode

                // While a bar button holds focus the bar handles its own keys and the
                // D-Pad is left alone, otherwise left/right seek instead of moving
                // between Info and Lyrics.
                if (barVisible && barOwnsFocus) return@onKeyEvent false

                when {
                    keyCode == KeyEvent.KEYCODE_BACK && infoOpen -> {
                        infoOpen = false
                        true
                    }

                    keyCode == KeyEvent.KEYCODE_BACK && lyricsOpen -> {
                        lyricsOpen = false
                        true
                    }

                    keyCode == KeyEvent.KEYCODE_BACK && barVisible -> {
                        barVisible = false
                        pageFocus.requestFocusSafely()
                        true
                    }

                    // Nothing on screen yet: the DPAD itself drives playback.
                    !barVisible -> when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            mediaController?.seekBack()
                            revealBar()
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            mediaController?.seekForward()
                            revealBar()
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            mediaController.togglePlayback()
                            revealBar()
                            true
                        }

                        // UP is the way into the bar: it comes up already selected
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            revealBar()
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_MENU -> {
                            revealBar()
                            true
                        }

                        else -> false
                    }

                    // Bar is up but the page still owns the focus.
                    else -> when (keyCode) {
                        // UP moves the selection back into the bar instead of hiding it
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            lastInteraction = System.nanoTime()
                            barFocusTicket++
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            // Step back into the bar's buttons
                            lastInteraction = System.nanoTime()
                            barFocusTicket++
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            mediaController?.seekBack()
                            revealBar()
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            mediaController?.seekForward()
                            revealBar()
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            mediaController.togglePlayback()
                            revealBar()
                            true
                        }

                        else -> false
                    }
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
                    val sideBySideLyrics = lyricsOpen && canShowLyrics
                    val coverSize = if (sideBySideLyrics) {
                        minOf(maxWidth * 0.26f, maxHeight * 0.38f)
                    } else {
                        minOf(maxWidth * 0.40f, maxHeight * 0.50f)
                    }

                    // Artwork, blurred out to a colour wash behind everything
                    if (backgroundStyle == NowPlayingBackground.DRIFTING_BLUR)
                    AsyncImage(
                        model = ImageRequest.Builder(context)
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
                                scaleX = driftScale
                                scaleY = driftScale
                                translationX = driftX
                                translationY = driftY
                                alpha = 0.7f
                            }
                            .blur(70.dp)
                    )

                    if (backgroundStyle == NowPlayingBackground.DRIFTING_BLUR)
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

                    if (sideBySideLyrics) {
                        // Lyrics get the right half, the artwork keeps the left
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 56.dp)
                                .padding(top = 24.dp, bottom = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(0.42f)
                                    .fillMaxHeight(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                AlbumArtHero(metadata = metadata, size = coverSize)

                                Spacer(Modifier.height(14.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PlayingIndicator(
                                        color = iconTextColor,
                                        mediaController = mediaController
                                    )
                                    Text(
                                        text = metadata?.title?.toString().orEmpty(),
                                        fontSize = 21.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = iconTextColor,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = metadata?.artist?.toString().orEmpty(),
                                    fontSize = 15.sp,
                                    color = iconTextColor.copy(alpha = 0.62f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(Modifier.width(32.dp))

                            Box(
                                modifier = Modifier
                                    .weight(0.58f)
                                    .fillMaxHeight()
                                    .padding(vertical = 12.dp)
                            ) {
                                if (appleLyrics) {
                                    TvLyricsPane(
                                        tint = iconTextColor,
                                        largeText = largeLyrics,
                                        metadata = metadata,
                                        mediaController = mediaController,
                                        settings = settings
                                    )
                                } else {
                                    // The shared view: every lyric setting behaves here too
                                    CompositionLocalProvider(
                                        LocalDensity provides Density(
                                            density = LocalDensity.current.density,
                                            fontScale = LocalDensity.current.fontScale *
                                                    if (largeLyrics) 1.35f else 1f
                                        )
                                    ) {
                                        LyricsView(
                                            iconTextColor,
                                            true,
                                            mediaController,
                                            PaddingValues(horizontal = 12.dp),
                                            onRefreshLyrics
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 40.dp)
                                .padding(top = 28.dp, bottom = 24.dp),
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
                            onGoToAlbum = onGoToAlbum,
                            onRefreshLyrics = onRefreshLyrics,
                            onPlayNext = { mediaController.insertAfterCurrent() },
                            onAddToQueue = { mediaController.appendToQueue() },
                            modifier = Modifier.padding(start = 40.dp, bottom = 150.dp)
                        )
                    }
                }
            }
        }

        // Dim the bottom of the artwork while the bar is up so its text stays legible
        AnimatedVisibility(
            visible = barVisible && (showAppleBar || showClassicBar),
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.45f to Color.Black.copy(alpha = 0.35f),
                            1f to Color.Black.copy(alpha = 0.75f)
                        )
                    )
            )
        }

        if (sleepTimerOpen)
            TvSleepTimerSheet(
                onDismiss = { sleepTimerOpen = false },
                onPick = { minutes ->
                    ChoraMediaLibraryService.getInstance()?.setSleepTimer(minutes)
                    sleepTimerOpen = false
                }
            )

        // The bar only exists while the remote is in use
        AnimatedVisibility(
            visible = barVisible && showAppleBar,
            enter = fadeIn(tween(durationMillis = 300)) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(tween(durationMillis = 300)) + slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .padding(bottom = 24.dp)
                .focusGroup()
        ) {
            ApplePlayerBar(
                mediaController = mediaController,
                color = iconTextColor,
                isRadio = isRadio,
                canShowLyrics = canShowLyrics,
                lyricsOpen = lyricsOpen,
                infoOpen = infoOpen,
                infoFocus = barFocus,
                pageFocus = pageFocus,
                onBarFocusChanged = { barOwnsFocus = it },
                onDismiss = {
                    barVisible = false
                    barOwnsFocus = false
                    pageFocus.requestFocusSafely()
                },
                onToggleLyrics = {
                    lyricsOpen = !lyricsOpen
                    lastInteraction = System.nanoTime()
                },
                onToggleInfo = {
                    infoOpen = !infoOpen
                    lastInteraction = System.nanoTime()
                },
                onDownload = onDownload,
                onOpenSleepTimer = { sleepTimerOpen = true },
                onInteraction = { lastInteraction = System.nanoTime() }
            )
        }

        // The app's original transport bar, kept behind its own setting
        AnimatedVisibility(
            visible = barVisible && showClassicBar,
            enter = fadeIn(tween(durationMillis = 300)) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut(tween(durationMillis = 300)) + slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
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
                                lastInteraction = System.nanoTime()
                                if (keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                                    keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_BACK
                                ) {
                                    barVisible = false
                                    focusManager.clearFocus()
                                    pageFocus.requestFocusSafely()
                                    return@onKeyEvent true
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
                                .focusRequester(classicBarFocus)
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

/**
 * Apple's bar: the skip/lyrics circles above a full-width scrubber, and an Info pill
 * in the bottom-left corner.
 */
@Composable
private fun ApplePlayerBar(
    mediaController: MediaController?,
    color: Color,
    isRadio: Boolean,
    canShowLyrics: Boolean,
    lyricsOpen: Boolean,
    infoOpen: Boolean,
    infoFocus: FocusRequester,
    pageFocus: FocusRequester,
    onBarFocusChanged: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onToggleLyrics: () -> Unit,
    onToggleInfo: () -> Unit,
    onDownload: () -> Unit,
    onOpenSleepTimer: () -> Unit,
    onInteraction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { onBarFocusChanged(it.hasFocus) }
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    onInteraction()
                    // UP puts the bar away instead of re-selecting its buttons forever
                    if (keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                        keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_BACK
                    ) {
                        onDismiss()
                        return@onKeyEvent true
                    }
                }
                false
            },
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onToggleInfo,
                modifier = Modifier
                    .focusRequester(infoFocus)
                    .focusProperties {
                        down = pageFocus
                        up = FocusRequester.Cancel
                    },
                shape = ButtonDefaults.shape(shape = CircleShape),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
                colors = ButtonDefaults.colors(
                    containerColor = Color.White.copy(alpha = 0.14f),
                    disabledContainerColor = Color.White.copy(alpha = 0.14f),
                    contentColor = color,
                    disabledContentColor = color.copy(alpha = 0.3f),
                    focusedContainerColor = Color.White,
                    focusedContentColor = Color.Black
                )
            ) {
                Text(
                    text = "Info",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.width(18.dp))

            if (!isRadio)
                Box(Modifier.weight(1f)) {
                    TvPlaybackProgress(mediaController = mediaController, color = color)
                }
            else
                Spacer(Modifier.weight(1f))

            Spacer(Modifier.width(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                BarCircleButton(
                    icon = R.drawable.rounded_download_24,
                    contentDescription = "Download",
                    active = false,
                    modifier = Modifier.focusProperties {
                        down = pageFocus
                        up = FocusRequester.Cancel
                    },
                    onClick = onDownload
                )

                BarCircleButton(
                    icon = R.drawable.rounded_timer_24,
                    contentDescription = "Sleep timer",
                    active = false,
                    modifier = Modifier.focusProperties {
                        down = pageFocus
                        up = FocusRequester.Cancel
                    },
                    onClick = onOpenSleepTimer
                )

                if (canShowLyrics) {
                    BarCircleButton(
                        icon = if (lyricsOpen) R.drawable.lyrics_active else R.drawable.lyrics_inactive,
                        contentDescription = "Lyrics",
                        active = lyricsOpen,
                        modifier = Modifier.focusProperties {
                            down = pageFocus
                            up = FocusRequester.Cancel
                        },
                        onClick = onToggleLyrics
                    )
                }
            }
        }
    }
}

@Composable
private fun BarCircleButton(
    icon: Int,
    contentDescription: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(52.dp),
        shape = ButtonDefaults.shape(shape = CircleShape),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.colors(
            containerColor = Color.White.copy(alpha = if (active) 0.30f else 0.14f),
            disabledContainerColor = Color.White.copy(alpha = 0.14f),
            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.3f),
            focusedContainerColor = Color.White,
            focusedContentColor = Color.Black
        )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(26.dp)
        )
    }
}

private fun MediaController?.insertAfterCurrent() {
    val player = this ?: return
    val item = player.currentMediaItem ?: return
    val index = (player.currentMediaItemIndex + 1).coerceAtMost(player.mediaItemCount)
    player.addMediaItem(index, item)
}

private fun MediaController?.appendToQueue() {
    val player = this ?: return
    val item = player.currentMediaItem ?: return
    player.addMediaItem(item)
}

private fun MediaController?.togglePlayback() {
    val player = this ?: return
    if (player.isPlaying) player.pause() else player.play()
}

/** requestFocus throws when the node is not attached; the bar may not be composed yet. */
private fun FocusRequester.requestFocusSafely(): Boolean =
    try {
        requestFocus()
        true
    } catch (_: IllegalStateException) {
        false
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

/**
 * tvOS-style lyrics: the lines themselves are the interface, big and rounded over
 * the drifting artwork, the sung line fully lit and the rest faded back, with the
 * three sheet dots showing where in the song the reader is.
 */
@Composable
private fun TvLyricsPane(
    tint: Color,
    largeText: Boolean,
    metadata: MediaMetadata?,
    mediaController: MediaController?,
    settings: AppearanceSettingsManager,
    modifier: Modifier = Modifier
) {
    val lyrics = LyricsState.lyrics.collectAsStateWithLifecycle().value ?: return

    // The same preferences the shared lyric view reads, so a switch cannot mean two
    // different things depending on which pane happens to be on screen.
    val speed by settings.lyricsAnimationSpeedFlow.collectAsStateWithLifecycle(660)
    val alignment by settings.nowPlayingLyricsAlignment
        .collectAsStateWithLifecycle(NowPlayingAlignment.CENTER)
    val autoScroll by settings.lyricsAutoScroll.collectAsStateWithLifecycle(true)
    val recenter by settings.lyricsRecenterAfterScroll.collectAsStateWithLifecycle(true)
    val bounce by settings.lyricsBounce.collectAsStateWithLifecycle(true)

    val lines = remember(lyrics) {
        lyrics.lines
            .map { line -> line.startMs to line.lines.joinToString(" ") { it.text }.trim() }
            .filter { it.second.isNotBlank() }
    }

    if (lines.isEmpty()) return

    var positionMs by remember { mutableIntStateOf(mediaController?.currentPosition?.toInt() ?: 0) }

    LaunchedEffect(mediaController) {
        val controller = mediaController ?: return@LaunchedEffect
        while (true) {
            positionMs = controller.currentPosition.toInt()
            delay(250)
        }
    }

    val activeIndex = remember(positionMs, lines) {
        lines.indexOfLast { it.first <= positionMs }.coerceAtLeast(0)
    }

    val listState = rememberLazyListState()
    LaunchedEffect(activeIndex, autoScroll, recenter) {
        if (!autoScroll) return@LaunchedEffect
        val onScreen = listState.layoutInfo.visibleItemsInfo.any { it.index == activeIndex }
        if (recenter || !onScreen) {
            runCatching { listState.animateScrollToItem(activeIndex) }
        }
    }

    val textAlign = when (alignment) {
        NowPlayingAlignment.LEFT -> TextAlign.Start
        NowPlayingAlignment.CENTER -> TextAlign.Center
        NowPlayingAlignment.RIGHT -> TextAlign.End
    }
    val emphasisSpec: AnimationSpec<Float> =
        if (bounce) tween((2400 - speed).coerceIn(160, 700)) else snap()
    val size = if (largeText) 34.sp else 28.sp

    BoxWithConstraints(modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = maxHeight * 0.36f),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            itemsIndexed(lines) { index, line ->
                val emphasis by animateFloatAsState(
                    targetValue = if (index == activeIndex) 1f else 0f,
                    animationSpec = emphasisSpec,
                    label = "line emphasis"
                )

                Text(
                    text = line.second,
                    fontSize = size * (0.86f + 0.14f * emphasis),
                    fontWeight = if (index == activeIndex) FontWeight.Bold else FontWeight.Medium,
                    fontFamily = FontFamily.SansSerif,
                    lineHeight = size * 1.16f,
                    textAlign = textAlign,
                    color = tint.copy(alpha = 0.40f + 0.60f * emphasis),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun TvSleepTimerSheet(
    onDismiss: () -> Unit,
    onPick: (Int) -> Unit
) {
    val first = remember { FocusRequester() }

    LaunchedEffect(Unit) { runCatching { first.requestFocus() } }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1C1C1E))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Sleep timer",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            listOf(15, 30, 45, 60).forEachIndexed { index, minutes ->
                Button(
                    onClick = { onPick(minutes) },
                    modifier = if (index == 0) Modifier.focusRequester(first) else Modifier,
                    shape = ButtonDefaults.shape(shape = RoundedCornerShape(10.dp)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    colors = ButtonDefaults.colors(
                        containerColor = Color.White.copy(alpha = 0.08f),
                        disabledContainerColor = Color.White.copy(alpha = 0.08f),
                        contentColor = Color.White,
                        disabledContentColor = Color.White.copy(alpha = 0.3f),
                        focusedContainerColor = Color.White,
                        focusedContentColor = Color.Black
                    )
                ) {
                    Text(text = "$minutes minutes", fontSize = 15.sp)
                }
            }

            Button(
                onClick = { onPick(0) },
                shape = ButtonDefaults.shape(shape = RoundedCornerShape(10.dp)),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                colors = ButtonDefaults.colors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    contentColor = Color.White.copy(alpha = 0.7f),
                    disabledContentColor = Color.White.copy(alpha = 0.3f),
                    focusedContainerColor = Color.White,
                    focusedContentColor = Color.Black
                )
            ) {
                Text(text = "Turn off", fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun TrackInfoCard(
    metadata: MediaMetadata?,
    color: Color,
    onGoToAlbum: () -> Unit = {},
    onRefreshLyrics: () -> Unit = {},
    onPlayNext: () -> Unit = {},
    onAddToQueue: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val ctaFocus = remember { FocusRequester() }
    val albumId = metadata?.extras?.getString("albumId").orEmpty()

    // The card is opened from the bar, so it claims focus to make the pill reachable
    LaunchedEffect(albumId) {
        if (albumId.isNotBlank()) runCatching { ctaFocus.requestFocus() }
    }

    val durationMs = metadata?.durationMs ?: 0L
    val minutes = durationMs / 60_000L
    val seconds = (durationMs % 60_000L) / 1000L
    val format = metadata?.extras?.getString("format")?.uppercase()
    val bitrate = metadata?.extras?.getLong("bitrate")?.takeIf { it > 0 }
    val lossless = format?.contains("FLAC") == true ||
            format?.contains("ALAC") == true ||
            (bitrate ?: 0) >= 700

    Column(
        modifier = modifier
            .widthIn(max = 420.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black.copy(alpha = 0.42f))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(metadata?.artworkUri)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                fallback = painterResource(R.drawable.placeholder),
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = metadata?.title?.toString().orEmpty(),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = listOfNotNull(
                        metadata?.artist?.toString()?.takeIf { it.isNotBlank() },
                        metadata?.albumTitle?.toString()?.takeIf { it.isNotBlank() }
                    ).joinToString(" – "),
                    fontSize = 14.sp,
                    color = color.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (durationMs > 0)
                Text(
                    text = "$minutes min, $seconds sec",
                    fontSize = 13.sp,
                    color = color.copy(alpha = 0.7f)
                )

            if (lossless)
                Text(
                    text = "Lossless",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        if (albumId.isNotBlank())
        Button(
            onClick = onGoToAlbum,
            modifier = Modifier
                .focusRequester(ctaFocus)
                .focusProperties { up = FocusRequester.Cancel },
            shape = ButtonDefaults.shape(shape = RoundedCornerShape(50)),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            colors = ButtonDefaults.colors(
                containerColor = Color.White.copy(alpha = 0.16f),
                disabledContainerColor = Color.White.copy(alpha = 0.16f),
                contentColor = Color(0xFF4D9BFF),
                disabledContentColor = Color(0xFF4D9BFF).copy(alpha = 0.3f),
                focusedContainerColor = Color.White,
                focusedContentColor = Color(0xFF1D5FD0)
            )
        ) {
            Text(text = "Go to Album", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = onPlayNext,
            modifier = Modifier.focusProperties { up = FocusRequester.Cancel },
            shape = ButtonDefaults.shape(shape = RoundedCornerShape(50)),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.colors(
                containerColor = Color.White.copy(alpha = 0.12f),
                disabledContainerColor = Color.White.copy(alpha = 0.12f),
                contentColor = color.copy(alpha = 0.85f),
                disabledContentColor = color.copy(alpha = 0.3f),
                focusedContainerColor = Color.White,
                focusedContentColor = Color.Black
            )
        ) {
            Text(text = "Play next", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = onAddToQueue,
            modifier = Modifier.focusProperties { up = FocusRequester.Cancel },
            shape = ButtonDefaults.shape(shape = RoundedCornerShape(50)),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.colors(
                containerColor = Color.White.copy(alpha = 0.12f),
                disabledContainerColor = Color.White.copy(alpha = 0.12f),
                contentColor = color.copy(alpha = 0.85f),
                disabledContentColor = color.copy(alpha = 0.3f),
                focusedContainerColor = Color.White,
                focusedContentColor = Color.Black
            )
        ) {
            Text(text = "Add to queue", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = onRefreshLyrics,
            modifier = Modifier.focusProperties { up = FocusRequester.Cancel },
            shape = ButtonDefaults.shape(shape = RoundedCornerShape(50)),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            colors = ButtonDefaults.colors(
                containerColor = Color.White.copy(alpha = 0.12f),
                disabledContainerColor = Color.White.copy(alpha = 0.12f),
                contentColor = color.copy(alpha = 0.85f),
                disabledContentColor = color.copy(alpha = 0.3f),
                focusedContainerColor = Color.White,
                focusedContentColor = Color.Black
            )
        ) {
            Text(text = "Refresh lyrics", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        }
        }
    }
}

