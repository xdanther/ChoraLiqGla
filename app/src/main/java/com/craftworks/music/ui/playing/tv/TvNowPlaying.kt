@file:OptIn(UnstableApi::class)

package com.craftworks.music.ui.playing.tv

import android.view.KeyEvent
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import android.os.Build
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.lerp
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
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.craftworks.music.util.QrCode
import kotlinx.coroutines.launch
import com.craftworks.music.player.ChoraMediaLibraryService
import com.craftworks.music.ui.elements.tv.TvHorizontalSongCard
import com.craftworks.music.ui.playing.LyricsView
import com.craftworks.music.ui.screens.tv.requestFocusOnFirstGainingVisibility
import com.craftworks.music.utils.StringUtils
import com.gigamole.composefadingedges.marqueeHorizontalFadingEdges
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/** How long the player bar lingers after the last remote key press. */
private const val BAR_AUTO_HIDE_MILLIS = 5_000L

@Preview(device = "id:tv_1080p", showBackground = true, showSystemUi = true)
@Composable
fun TvNowPlaying(
    mediaController: MediaController? = null,
    iconColor: Color = Color.Black,
    metadata: MediaMetadata? = null,
    palette: List<Color> = emptyList(),
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
    var queueOpen by remember { mutableStateOf(false) }
    var moreOpen by remember { mutableStateOf(false) }
    var shareOpen by remember { mutableStateOf(false) }

    val sheetAlbumId = metadata?.extras?.getString("albumId").orEmpty()
    val sheetSongId = metadata?.extras?.getString("id").orEmpty()
    var lyricsOpen by remember { mutableStateOf(false) }
    var sleepTimerOpen by remember { mutableStateOf(false) }
    var lastInteraction by remember { mutableLongStateOf(0L) }

    // Shared by the bar's lyrics button and the More sheet's row
    val toggleLyrics = {
        lyricsOpen = !lyricsOpen
        lastInteraction = System.nanoTime()
    }

    val lyrics by LyricsState.lyrics.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val pageFocus = remember { FocusRequester() }
    val barFocus = remember { FocusRequester() }
    val classicBarFocus = remember { FocusRequester() }
    var barFocusTicket by remember { mutableIntStateOf(0) }
    var barOwnsFocus by remember { mutableStateOf(false) }

    // The bar is a visiting guest: it fades away again so the artwork stays the main thing.
    LaunchedEffect(barVisible, lastInteraction, infoOpen, queueOpen, moreOpen, shareOpen) {
        if (!barVisible) return@LaunchedEffect
        if (infoOpen || queueOpen || moreOpen || shareOpen) return@LaunchedEffect
        delay(BAR_AUTO_HIDE_MILLIS)
        barVisible = false
    }

    // Focus can only follow the bar once its nodes exist, so it waits for composition
    // The bar only exists once its enter animation has composed a node, so focus is
    // retried instead of asked for once: otherwise the bar appears but the page keeps
    // the remote and left/right seek instead of moving through its buttons.
    LaunchedEffect(barVisible, showAppleBar) {
        if (barVisible) {
            repeat(16) {
                val got = if (showAppleBar) {
                    barFocus.requestFocusSafely()
                } else {
                    classicBarFocus.requestFocusSafely()
                }
                if (got) return@LaunchedEffect
                delay(40)
            }
        } else {
            pageFocus.requestFocusSafely()
        }
    }

    LaunchedEffect(barFocusTicket) {
        if (barFocusTicket > 0) {
            repeat(16) {
                if (barFocus.requestFocusSafely()) return@LaunchedEffect
                delay(40)
            }
        }
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
                        minOf(maxWidth * 0.26f, maxHeight * 0.46f)
                    } else {
                        minOf(maxWidth * 0.28f, maxHeight * 0.50f)
                    }

                    // A wash built from the cover's own colours, drifting slowly
                    if (backgroundStyle == NowPlayingBackground.DRIFTING_BLUR)
                        PaletteWash(palette)

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
                                        fontSize = 24.sp,
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
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = iconTextColor.copy(alpha = 0.62f),
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
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = iconTextColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = metadata?.artist?.toString().orEmpty(),
                                fontSize = 15.sp,
                                color = iconTextColor.copy(alpha = 0.60f),
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
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        TrackInfoCard(
                            metadata = metadata,
                            color = iconTextColor,
                            onGoToAlbum = onGoToAlbum,
                            modifier = Modifier.padding(start = 40.dp, end = 40.dp, bottom = 132.dp)
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

        if (queueOpen)
            TvQueueSheet(
                mediaController = mediaController,
                onDismiss = { queueOpen = false }
            )

        if (moreOpen)
            TvMoreSheet(
                canShowLyrics = canShowLyrics,
                lyricsOpen = lyricsOpen,
                albumId = sheetAlbumId,
                songId = sheetSongId,
                onGoToAlbum = { moreOpen = false; onGoToAlbum() },
                onShare = { moreOpen = false; shareOpen = true },
                onSleepTimer = { moreOpen = false; sleepTimerOpen = true },
                onToggleLyrics = { moreOpen = false; toggleLyrics() },
                onDismiss = { moreOpen = false }
            )

        if (shareOpen)
            TvShareSheet(
                songId = sheetSongId,
                title = metadata?.title?.toString().orEmpty(),
                onDismiss = { shareOpen = false }
            )

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
                onOpenQueue = { queueOpen = true },
                onOpenMore = { moreOpen = true },
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
    onOpenQueue: () -> Unit,
    onOpenMore: () -> Unit,
    onInteraction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { onBarFocusChanged(it.hasFocus) }
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    onInteraction()
                    if (keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_UP ||
                        keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_BACK
                    ) {
                        onDismiss()
                        return@onKeyEvent true
                    }
                }
                false
            },
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (!isRadio)
            TvPlaybackProgress(mediaController = mediaController, color = color)

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
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                colors = ButtonDefaults.colors(
                    containerColor = Color.White.copy(alpha = 0.18f),
                    disabledContainerColor = Color.White.copy(alpha = 0.18f),
                    contentColor = Color.White,
                    disabledContentColor = Color.White.copy(alpha = 0.3f),
                    focusedContainerColor = Color.White,
                    focusedContentColor = Color.Black
                )
            ) {
                Text(text = "Info", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.weight(1f))

            BarCircleButton(
                icon = R.drawable.rounded_queue_music_24,
                contentDescription = "Queue",
                active = false,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .focusProperties {
                        down = pageFocus
                        up = FocusRequester.Cancel
                    },
                onClick = onOpenQueue
            )

            BarCircleButton(
                icon = R.drawable.rounded_more_horiz_24,
                contentDescription = "More",
                active = false,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .focusProperties {
                        down = pageFocus
                        up = FocusRequester.Cancel
                    },
                onClick = onOpenMore
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
        modifier = modifier.size(38.dp),
        shape = ButtonDefaults.shape(shape = CircleShape),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.colors(
            containerColor = Color.White.copy(alpha = 0.14f),
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
            modifier = Modifier.size(18.dp)
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
    val size = if (largeText) 28.sp else 22.sp

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

/** Apple's queue: everything after the current song, with the current row marked. */
@Composable
private fun TvQueueSheet(
    mediaController: MediaController?,
    onDismiss: () -> Unit
) {
    val count = mediaController?.mediaItemCount ?: 0
    val current = mediaController?.currentMediaItemIndex ?: 0
    val currentFocus = remember { FocusRequester() }

    LaunchedEffect(count) { runCatching { currentFocus.requestFocus() } }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(480.dp)
                .heightIn(max = 396.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1C1C1E))
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Up Next",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 10.dp, bottom = 4.dp)
            ) {
                for (index in 0 until count) {
                    val item = mediaController?.getMediaItemAt(index) ?: continue
                    val isCurrent = index == current

                    Button(
                        onClick = {
                            mediaController.seekToDefaultPosition(index)
                            mediaController.play()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                            .then(if (isCurrent) Modifier.focusRequester(currentFocus) else Modifier),
                        shape = ButtonDefaults.shape(shape = RoundedCornerShape(12.dp)),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                        colors = ButtonDefaults.colors(
                            containerColor = Color.White.copy(alpha = if (isCurrent) 0.16f else 0.04f),
                            focusedContainerColor = Color.White,
                            contentColor = Color.White,
                            focusedContentColor = Color.Black
                        )
                    ) {
                        Column(Modifier.fillMaxWidth()) {
                            Text(
                                text = item.mediaMetadata.title?.toString().orEmpty(),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.mediaMetadata.artist?.toString().orEmpty(),
                                fontSize = 13.sp,
                                color = androidx.compose.material3.LocalContentColor.current.copy(alpha = 0.66f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

/** The ••• sheet: the actions the bar itself no longer carries. */
@Composable
private fun TvMoreSheet(
    canShowLyrics: Boolean,
    lyricsOpen: Boolean,
    albumId: String,
    songId: String,
    onGoToAlbum: () -> Unit,
    onShare: () -> Unit,
    onSleepTimer: () -> Unit,
    onToggleLyrics: () -> Unit,
    onDismiss: () -> Unit
) {
    val first = remember { FocusRequester() }

    val actions = buildList {
        if (songId.isNotBlank()) add("Share Song" to onShare)
        if (albumId.isNotBlank()) add("Go to Album" to onGoToAlbum)
        if (canShowLyrics) add((if (lyricsOpen) "Hide Lyrics" else "Lyrics") to onToggleLyrics)
        add("Sleep Timer" to onSleepTimer)
    }

    LaunchedEffect(Unit) { runCatching { first.requestFocus() } }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(320.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF1C1C1E))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            actions.forEachIndexed { index, action ->
                MoreSheetRow(
                    label = action.first,
                    modifier = if (index == 0) Modifier.focusRequester(first) else Modifier,
                    onClick = action.second
                )
            }
        }
    }
}

/** Share a song: a temporary public link, drawn as a code the phone can scan. */
@Composable
private fun TvShareSheet(
    songId: String,
    title: String,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val first = remember { FocusRequester() }

    var link by remember { mutableStateOf<String?>(null) }
    var lifetime by remember { mutableStateOf("") }
    var pending by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { runCatching { first.requestFocus() } }

    val durations = listOf("15 minutes" to 15L, "1 hour" to 60L, "24 hours" to 1440L)

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(540.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1C1C1E))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (link == null) "Share Song" else title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (link == null) {
                Text(
                    text = "Anyone with this link can listen until it expires.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                durations.forEachIndexed { index, duration ->
                    MoreSheetRow(
                        label = duration.first,
                        modifier = if (index == 0) Modifier.focusRequester(first) else Modifier,
                        onClick = {
                            pending = true
                            failed = false
                            scope.launch {
                                val expiresAt = System.currentTimeMillis() + duration.second * 60_000L
                                val url = runCatching {
                                    MediaProviderManager.currentProvider.value
                                        ?.createShareLink(songId, expiresAt)
                                }.getOrNull()
                                pending = false
                                if (url.isNullOrBlank()) {
                                    failed = true
                                } else {
                                    link = url
                                    lifetime = duration.first
                                }
                            }
                        }
                    )
                }

                if (pending)
                    Text(
                        text = "Creating link…",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 13.sp
                    )

                if (failed)
                    Text(
                        text = "This server cannot create share links.",
                        color = Color(0xFFFF8A80),
                        fontSize = 13.sp
                    )
            } else {
                ShareQr(
                    link = link.orEmpty(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(248.dp)
                )

                Text(
                    text = link.orEmpty(),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Link expires in $lifetime",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                MoreSheetRow(
                    label = "Done",
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .focusRequester(first),
                    onClick = onDismiss
                )
            }
        }
    }
}

@Composable
private fun ShareQr(link: String, modifier: Modifier = Modifier) {
    val matrix = remember(link) { QrCode.encode(link, QrCode.Level.MEDIUM) }

    if (matrix == null) {
        Text(
            text = "This link is too long for a code.",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 13.sp
        )
        return
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val quiet = 4
            val count = matrix.size + quiet * 2
            val module = size.minDimension / count
            for (row in matrix.indices) {
                for (column in matrix.indices) {
                    if (!matrix[row][column]) continue
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset((column + quiet) * module, (row + quiet) * module),
                        size = Size(module, module)
                    )
                }
            }
        }
    }
}

@Composable
private fun MoreSheetRow(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(12.dp)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        colors = ButtonDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.White,
            contentColor = Color.White,
            focusedContentColor = Color.Black
        )
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
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
    modifier: Modifier = Modifier
) {
    val ctaFocus = remember { FocusRequester() }
    val albumId = metadata?.extras?.getString("albumId").orEmpty()
    val durationMs = metadata?.durationMs ?: 0L
    val minutes = durationMs / 60_000L
    val seconds = (durationMs % 60_000L) / 1000L
    val format = metadata?.extras?.getString("format")?.uppercase()
    val bitrate = metadata?.extras?.getLong("bitrate")?.takeIf { it > 0 }
    val lossless = format?.contains("FLAC") == true ||
            format?.contains("ALAC") == true ||
            (bitrate ?: 0) >= 700

    LaunchedEffect(albumId) {
        if (albumId.isNotBlank()) runCatching { ctaFocus.requestFocus() }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black.copy(alpha = 0.34f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(metadata?.artworkUri)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            fallback = painterResource(R.drawable.placeholder),
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(6.dp))
        )

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = metadata?.title?.toString().orEmpty(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = listOfNotNull(
                    metadata?.artist?.toString()?.takeIf { it.isNotBlank() },
                    metadata?.albumTitle?.toString()?.takeIf { it.isNotBlank() }
                ).joinToString(" – "),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.88f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (durationMs > 0)
                Text(
                    text = "$minutes min, $seconds secs",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )

            if (lossless)
                Text(
                    text = "◐  Lossless",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 3.dp)
                )
        }

        Spacer(Modifier.width(20.dp))

        if (albumId.isNotBlank())
            Button(
                onClick = onGoToAlbum,
                modifier = Modifier
                    .focusRequester(ctaFocus)
                    .focusProperties { up = FocusRequester.Cancel },
                shape = ButtonDefaults.shape(shape = RoundedCornerShape(50)),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 6.dp),
                colors = ButtonDefaults.colors(
                    containerColor = Color(0xFFC8DDFF),
                    disabledContainerColor = Color(0xFFC8DDFF),
                    contentColor = Color.Black,
                    disabledContentColor = Color.Black.copy(alpha = 0.4f),
                    focusedContainerColor = Color.White,
                    focusedContentColor = Color.Black
                )
            ) {
                Text(text = "Go to Album", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
    }
}

/**
 * The player background is a colour field computed on the GPU from the artwork's
 * palette: value noise is domain-warped by itself a couple of times, so the colours
 * bleed into each other in long liquid bands with no shape, edge or tiling to see.
 * Drawing gradients or circles into the canvas always left visible geometry; a
 * shader has no geometry at all.
 */
@Composable
private fun PaletteWash(palette: List<Color>) {
    val colors = remember(palette) { washColors(palette) }

    val transition = rememberInfiniteTransition(label = "palette wash")
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 4000f,
        animationSpec = infiniteRepeatable(tween(180_000, easing = LinearEasing)),
        label = "wash time"
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val shader = remember { android.graphics.RuntimeShader(WASH_SHADER) }
        val paint = remember { android.graphics.Paint().apply { isAntiAlias = false } }

        Canvas(Modifier.fillMaxSize()) {
            shader.setFloatUniform("uResolution", size.width, size.height)
            shader.setFloatUniform("uTime", time)
            shader.setFloatUniform("uColorA", colors[0].red, colors[0].green, colors[0].blue)
            shader.setFloatUniform("uColorB", colors[1].red, colors[1].green, colors[1].blue)
            shader.setFloatUniform("uColorC", colors[2].red, colors[2].green, colors[2].blue)
            shader.setFloatUniform("uColorD", colors[3].red, colors[3].green, colors[3].blue)

            paint.shader = shader
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawRect(0f, 0f, size.width, size.height, paint)
            }
        }
    } else {
        // Pre-Android 12: no runtime shaders, so two soft bands of the same colours
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(colors[0], colors[1], colors[2], colors[3]),
                        start = Offset.Zero,
                        end = Offset(1400f, 900f)
                    )
                )
        )
    }
}

private const val WASH_SHADER = """
uniform float2 uResolution;
uniform float uTime;
uniform float3 uColorA;
uniform float3 uColorB;
uniform float3 uColorC;
uniform float3 uColorD;

float hash21(float2 p) {
    p = fract(p * float2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float valueNoise(float2 p) {
    float2 i = floor(p);
    float2 f = fract(p);
    float2 u = f * f * (3.0 - 2.0 * f);
    float a = hash21(i);
    float b = hash21(i + float2(1.0, 0.0));
    float c = hash21(i + float2(0.0, 1.0));
    float d = hash21(i + float2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(float2 p) {
    float total = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 4; i++) {
        total += amplitude * valueNoise(p);
        p *= 2.03;
        amplitude *= 0.5;
    }
    return total;
}

half4 main(float2 fragCoord) {
    float2 uv = fragCoord / uResolution;
    float2 p = uv * 2.6;

    float t = uTime * 0.012;
    float2 warpA = float2(fbm(p + float2(0.0, t)), fbm(p + float2(4.7, -t)));
    float2 warpB = float2(
        fbm(p + 2.2 * warpA + float2(1.7, 9.2) + t * 0.55),
        fbm(p + 2.2 * warpA + float2(8.3, 2.8) - t * 0.45)
    );
    float field = fbm(p + 1.8 * warpB);

    float3 color = mix(uColorA, uColorB, smoothstep(0.16, 0.84, field));
    color = mix(color, uColorC, smoothstep(0.28, 0.92, warpB.y));
    color = mix(color, uColorD, smoothstep(0.42, 0.96, warpA.x));

    float2 centered = uv - 0.5;
    float vignette = 1.0 - dot(centered, centered) * 0.9;
    color *= clamp(vignette, 0.52, 1.0);

    return half4(color.r, color.g, color.b, 1.0);
}
"""

private fun washColors(palette: List<Color>): List<Color> {
    val usable = palette.filter { it.alpha > 0.5f }
    val source = if (usable.size >= 2) usable else listOf(
        Color(0xFF8A3524),
        Color(0xFF43245C),
        Color(0xFF16394F),
        Color(0xFF5C2338)
    )

    fun boost(color: Color): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(color.toArgb(), hsv)
        hsv[1] = (hsv[1] * 1.7f).coerceIn(0f, 1f)
        hsv[2] = (hsv[2] * 0.82f).coerceIn(0.14f, 0.72f)
        return Color(android.graphics.Color.HSVToColor(hsv))
    }

    val boosted = source.map { boost(it) }
    return List(4) { index -> boosted[index % boosted.size] }
}
