@file:OptIn(UnstableApi::class)

package com.craftworks.music.ui.playing.tv

import com.craftworks.music.ui.elements.tv.glassRim
import com.craftworks.music.ui.elements.tv.liquidGlass
import com.craftworks.music.ui.theme.TvType
import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.snap
import com.craftworks.music.ui.playing.ArtworkColours
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
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
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
import androidx.compose.ui.graphics.luminance
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
import com.craftworks.music.data.model.SyncedWord
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
    colours: ArtworkColours = ArtworkColours.default(true),
    onGoToAlbum: () -> Unit = {},
    onGoToArtist: () -> Unit = {},
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
    val sheetArtistId = metadata?.extras?.getString("artistId").orEmpty()
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
    var focusBarOnShow by remember { mutableStateOf(false) }
    val railFocus = remember { FocusRequester() }

    // Whether the page itself, rather than one of its buttons, is holding the remote.
    // The page only takes arrows while this is true, so a bar button always wins.
    var pageFocused by remember { mutableStateOf(true) }

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
    LaunchedEffect(barVisible, showAppleBar, focusBarOnShow) {
        if (barVisible && focusBarOnShow) {
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
            repeat(10) {
                if (pageFocus.requestFocusSafely()) return@LaunchedEffect
                delay(40)
            }
        }
    }

    // A sheet takes the remote with it when it closes, leaving nothing focused and a
    // page that has to keep working. Hand the remote back to whatever is still on
    // screen: the bar if it is up, the page otherwise.
    LaunchedEffect(infoOpen, queueOpen, moreOpen, shareOpen, sleepTimerOpen) {
        if (infoOpen || queueOpen || moreOpen || shareOpen || sleepTimerOpen) return@LaunchedEffect
        delay(200)
        if (barVisible) {
            repeat(10) {
                if (barFocus.requestFocusSafely()) return@LaunchedEffect
                delay(40)
            }
        } else {
            repeat(10) {
                if (pageFocus.requestFocusSafely()) return@LaunchedEffect
                delay(40)
            }
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

    fun revealBar(focusBar: Boolean = false) {
        lastInteraction = System.nanoTime()
        focusBarOnShow = focusBar
        barVisible = true
    }

    fun hideBar() {
        barVisible = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // The plain, static and animated styles are drawn by NowPlaying_Background;
            // only the drifting style paints here, from the artwork's own base colour so
            // the first frame never flashes black.
            .background(
                if (backgroundStyle == NowPlayingBackground.DRIFTING_BLUR) colours.base
                else Color.Transparent
            )
            .focusRequester(pageFocus)
            .focusable(true)
            .onFocusChanged { pageFocused = it.isFocused }
            .onKeyEvent { keyEvent ->
                if (keyEvent.type != KeyEventType.KeyDown) return@onKeyEvent false
                val keyCode = keyEvent.nativeKeyEvent.keyCode

                // A bar or rail button holds the remote: the D-Pad belongs to it. The
                // page used to keep handling arrows after the bar appeared, which is
                // what left the remote stuck on the bar instead of moving through it.
                // When nothing with buttons is showing the page takes the keys anyway,
                // so losing focus can never leave the player deaf to the remote.
                val controlsShowing = barVisible || infoOpen || queueOpen || moreOpen ||
                    shareOpen || sleepTimerOpen
                if (!pageFocused && controlsShowing) return@onKeyEvent false

                // The info card replaces the bar, so Back belongs to it first
                if (keyCode == KeyEvent.KEYCODE_BACK && infoOpen) {
                    infoOpen = false
                    barVisible = true
                    barFocusTicket++
                    return@onKeyEvent true
                }

                when {
                    keyCode == KeyEvent.KEYCODE_BACK && lyricsOpen -> {
                        lyricsOpen = false
                        true
                    }

                    keyCode == KeyEvent.KEYCODE_BACK && barVisible -> {
                        barVisible = false
                        pageFocus.requestFocusSafely()
                        true
                    }

                    // Nothing on screen yet: the D-Pad itself drives playback.
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
                            revealBar(focusBar = false)
                            true
                        }

                        // UP is the way into the bar: it comes up already selected
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            revealBar(focusBar = true)
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_MENU -> {
                            revealBar(focusBar = true)
                            true
                        }

                        else -> false
                    }

                    // The bar is up but the page still holds the remote.
                    else -> when (keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            lastInteraction = System.nanoTime()
                            barFocusTicket++
                            true
                        }

                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            lastInteraction = System.nanoTime()
                            barVisible = false
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
                            revealBar(focusBar = false)
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
                        if (appleLyrics) minOf(maxWidth * 0.30f, maxHeight * 0.58f)
                        else minOf(maxWidth * 0.26f, maxHeight * 0.46f)
                    } else {
                        minOf(maxWidth * 0.28f, maxHeight * 0.50f)
                    }

                    // A wash built from the cover's own colours, drifting slowly
                    if (backgroundStyle == NowPlayingBackground.DRIFTING_BLUR)
                        if (lyricsOpen && appleLyrics) {
                            // Over lyrics the blurred cover is wanted, exactly as the
                            // shared background does it elsewhere; the colour wash is
                            // for the player's own screen.
                            LyricsBackdrop(metadata)
                        } else {
                            ArtworkWash(colours)
                        }

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

                                // With the Apple lyric pane open the artwork stands on its
                                // own: a title, artist and indicator next to a screen full of
                                // lyric lines is just competing noise.
                                if (!appleLyrics) {
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
                                            fontSize = TvType.title3.fontSize,
                                            fontWeight = FontWeight.SemiBold,
                                            color = iconTextColor,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        text = metadata?.artist?.toString().orEmpty(),
                                        fontSize = TvType.body.fontSize,
                                        color = iconTextColor.copy(alpha = 0.74f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
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
                                fontSize = TvType.body.fontSize,
                                fontWeight = FontWeight.Medium,
                                color = iconTextColor.copy(alpha = 0.74f),
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
                                    fontSize = TvType.title3.fontSize,
                                    fontWeight = FontWeight.SemiBold,
                                    color = iconTextColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = metadata?.artist?.toString().orEmpty(),
                                fontSize = TvType.body.fontSize,
                                color = iconTextColor.copy(alpha = 0.74f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(Modifier.weight(1f))
                        }
                    }

                    AnimatedVisibility(
                        visible = infoOpen,
                        enter = fadeIn(tween(200)) + slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(240, easing = FastOutSlowInEasing)
                        ),
                        exit = fadeOut(tween(180)) + slideOutVertically(
                            targetOffsetY = { it / 2 },
                            animationSpec = tween(200)
                        ),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        // The card takes the bar's place, so the bar is not shown at all
                        // while it is open and Back brings the bar back.
                        TrackInfoCard(
                            metadata = metadata,
                            color = iconTextColor,
                            onGoToAlbum = onGoToAlbum,
                            onGoToArtist = { onGoToArtist() },
                            onDismiss = {
                                infoOpen = false
                                barVisible = true
                                barFocusTicket++
                            },
                            modifier = Modifier.padding(start = 40.dp, end = 40.dp, bottom = 24.dp)
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
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.5f to Color.Black.copy(alpha = 0.18f),
                            1f to Color.Black.copy(alpha = 0.45f)
                        )
                    )
            )
        }

        if (queueOpen)
            TvQueueRail(
                mediaController = mediaController,
                colours = colours,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 128.dp),
                onDismiss = {
                    queueOpen = false
                    barFocusTicket++
                }
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

        // The options: a column of glass circles down the right edge, above the bar.
        AnimatedVisibility(
            visible = barVisible && showAppleBar && !infoOpen,
            enter = fadeIn(tween(durationMillis = 220)),
            exit = fadeOut(tween(durationMillis = 200)),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 40.dp, bottom = 150.dp)
        ) {
            TvOptionsRail(
                colour = iconTextColor,
                railFocus = railFocus,
                hasAlbum = sheetAlbumId.isNotBlank(),
                hasArtist = sheetArtistId.isNotBlank(),
                onGoToAlbum = onGoToAlbum,
                onGoToArtist = onGoToArtist,
                onOpenQueue = { queueOpen = true },
                onShare = { shareOpen = true },
                onSleepTimer = { sleepTimerOpen = true },
                onOpenMore = { moreOpen = true },
                onDismiss = {
                    barVisible = false
                    pageFocus.requestFocusSafely()
                },
                onInteraction = { lastInteraction = System.nanoTime() }
            )
        }

        // The player bar: Info, the transport and Lyrics on one line along the bottom,
        // the way the tvOS player reads from left to right.
        AnimatedVisibility(
            visible = barVisible && showAppleBar && !infoOpen,
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
                playFocus = barFocus,
                railFocus = railFocus,
                onDismiss = {
                    barVisible = false
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
 * The tvOS player bar: Info on the left, the transport in the middle, Lyrics on the
 * right, on one line along the bottom edge.
 *
 * The scrubber above it is a readout rather than a focus target, so the remote can
 * never end up parked on it, and Up leaves the row for the options rail on the right
 * instead of wandering off into nothing.
 */
@Composable
private fun ApplePlayerBar(
    mediaController: MediaController?,
    color: Color,
    isRadio: Boolean,
    canShowLyrics: Boolean,
    lyricsOpen: Boolean,
    playFocus: FocusRequester,
    railFocus: FocusRequester,
    onDismiss: () -> Unit,
    onToggleLyrics: () -> Unit,
    onToggleInfo: () -> Unit,
    onInteraction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    onInteraction()
                    // Back and Down both leave the bar; everything else belongs to its
                    // buttons.
                    val code = keyEvent.nativeKeyEvent.keyCode
                    if (code == KeyEvent.KEYCODE_BACK || code == KeyEvent.KEYCODE_DPAD_DOWN) {
                        onDismiss()
                        return@onKeyEvent true
                    }
                }
                false
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isRadio)
            TvPlaybackProgress(mediaController = mediaController, color = color)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Equal side slots keep the transport exactly centred whatever sits in them.
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                val pillShade = if (color.luminance() > 0.5f) Color.White else Color.Black
                var pillFocused by remember { mutableStateOf(false) }

                Button(
                    onClick = onToggleInfo,
                    modifier = Modifier
                        .onFocusChanged { pillFocused = it.hasFocus }
                        .glassRim(CircleShape, focused = pillFocused, strength = 0.85f)
                        .focusProperties { up = railFocus },
                    shape = ButtonDefaults.shape(shape = CircleShape),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                    colors = ButtonDefaults.colors(
                        containerColor = pillShade.copy(alpha = 0.14f),
                        disabledContainerColor = pillShade.copy(alpha = 0.10f),
                        contentColor = color,
                        disabledContentColor = color.copy(alpha = 0.3f),
                        focusedContainerColor = pillShade.copy(alpha = 0.38f),
                        focusedContentColor = color
                    )
                ) {
                    Text(text = "Info", fontSize = TvType.callout.fontSize, fontWeight = FontWeight.Medium)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                BarCircleButton(
                    icon = R.drawable.rounded_skip_previous_24,
                    contentDescription = "Previous",
                    active = false,
                    tint = color,
                    size = 44.dp,
                    modifier = Modifier.focusProperties { up = railFocus },
                    onClick = {
                        mediaController?.seekToPreviousMediaItem()
                        onInteraction()
                    }
                )

                Spacer(Modifier.width(12.dp))

                TransportPlayButton(
                    mediaController = mediaController,
                    color = color,
                    focus = playFocus,
                    railFocus = railFocus,
                    onInteraction = onInteraction
                )

                Spacer(Modifier.width(12.dp))

                BarCircleButton(
                    icon = R.drawable.rounded_skip_next_24,
                    contentDescription = "Next",
                    active = false,
                    tint = color,
                    size = 44.dp,
                    modifier = Modifier.focusProperties { up = railFocus },
                    onClick = {
                        mediaController?.seekToNextMediaItem()
                        onInteraction()
                    }
                )
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                // The slot is kept when a track has no lyrics so the transport never
                // shifts sideways between songs.
                if (canShowLyrics) {
                    BarCircleButton(
                        icon = if (lyricsOpen) R.drawable.lyrics_active else R.drawable.lyrics_inactive,
                        contentDescription = "Lyrics",
                        active = lyricsOpen,
                        tint = color,
                        size = 44.dp,
                        modifier = Modifier.focusProperties { up = railFocus },
                        onClick = onToggleLyrics
                    )
                } else {
                    Spacer(Modifier.size(44.dp))
                }
            }
        }
    }
}

/** Play and pause as one button, drawn from the player's own state. */
@Composable
private fun TransportPlayButton(
    mediaController: MediaController?,
    color: Color,
    focus: FocusRequester,
    railFocus: FocusRequester,
    onInteraction: () -> Unit
) {
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

    BarCircleButton(
        icon = if (playing) R.drawable.rounded_pause_24 else R.drawable.rounded_play_24,
        contentDescription = if (playing) "Pause" else "Play",
        active = false,
        tint = color,
        size = 52.dp,
        iconSize = 24.dp,
        modifier = Modifier
            .focusRequester(focus)
            .focusProperties { up = railFocus },
        onClick = {
            mediaController.togglePlayback()
            onInteraction()
        }
    )
}

/**
 * The options rail: everything that is not the transport, as a column of circles down
 * the right edge. Push Up from the bar to reach it, Down or Back to leave.
 */
@Composable
private fun TvOptionsRail(
    colour: Color,
    railFocus: FocusRequester,
    hasAlbum: Boolean,
    hasArtist: Boolean,
    onGoToAlbum: () -> Unit,
    onGoToArtist: () -> Unit,
    onOpenQueue: () -> Unit,
    onShare: () -> Unit,
    onSleepTimer: () -> Unit,
    onOpenMore: () -> Unit,
    onDismiss: () -> Unit,
    onInteraction: () -> Unit
) {
    val items = buildList {
        if (hasAlbum) add(Triple(R.drawable.rounded_library_music_24, "Go to Album", onGoToAlbum))
        if (hasArtist) add(Triple(R.drawable.rounded_artist_24, "Go to Artist", onGoToArtist))
        add(Triple(R.drawable.rounded_queue_music_24, "Queue", onOpenQueue))
        add(Triple(R.drawable.rounded_share_24, "Share", onShare))
        add(Triple(R.drawable.rounded_timer_24, "Sleep timer", onSleepTimer))
        add(Triple(R.drawable.rounded_more_horiz_24, "More", onOpenMore))
    }

    Column(
        modifier = Modifier
            .focusGroup()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    onInteraction()
                    if (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_BACK) {
                        onDismiss()
                        return@onKeyEvent true
                    }
                }
                false
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEachIndexed { index, item ->
            BarCircleButton(
                icon = item.first,
                contentDescription = item.second,
                active = false,
                tint = colour,
                size = 44.dp,
                modifier = if (index == 0) Modifier.focusRequester(railFocus) else Modifier,
                onClick = {
                    onInteraction()
                    item.third()
                }
            )
        }
    }
}

@Composable
private fun BarCircleButton(
    icon: Int,
    contentDescription: String,
    active: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    iconSize: Dp = 18.dp,
    onClick: () -> Unit
) {
    // Liquid glass: a translucent shade of the player's own ink inside a hairline
    // outline, brightening rather than inverting when the remote lands on it.
    val shade = if (tint.luminance() > 0.5f) Color.White else Color.Black
    var focused by remember { mutableStateOf(false) }

    Button(
        onClick = onClick,
        modifier = modifier
            .size(size)
            .onFocusChanged { focused = it.hasFocus }
            .glassRim(CircleShape, focused = focused, strength = 0.9f),
        shape = ButtonDefaults.shape(shape = CircleShape),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.colors(
            containerColor = shade.copy(alpha = 0.14f),
            disabledContainerColor = shade.copy(alpha = 0.10f),
            contentColor = tint,
            disabledContentColor = tint.copy(alpha = 0.3f),
            focusedContainerColor = shade.copy(alpha = 0.38f),
            focusedContentColor = tint
        )
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize)
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
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    onDown: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    var focused by remember { mutableStateOf(false) }
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

    Column(
        modifier
            .fillMaxWidth()
            .then(
                if (focusRequester != null) Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged { focused = it.isFocused }
                    .focusable()
                    .onKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                        when (event.nativeKeyEvent.keyCode) {
                            KeyEvent.KEYCODE_DPAD_DOWN -> { onDown?.invoke(); true }
                            KeyEvent.KEYCODE_BACK -> { onDismiss?.invoke(); true }
                            else -> false
                        }
                    }
                else Modifier
            )
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(if (focused) 5.dp else 3.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = if (focused) 0.38f else 0.22f))
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
                fontSize = TvType.callout.fontSize,
                fontWeight = FontWeight.Light,
                color = color.copy(alpha = 0.55f)
            )
            Text(
                text = "-" + StringUtils.formatSeconds((remaining / 1000L).toInt()),
                fontSize = TvType.callout.fontSize,
                fontWeight = FontWeight.Light,
                color = color.copy(alpha = 0.55f)
            )
        }
    }
}

/** One lyric line, with its words when the provider ships word timings. */
private data class LyricRow(
    val startMs: Int,
    val text: String,
    val words: List<SyncedWord>? = null
) {
    /**
     * The line as it should be drawn now: where the provider ships word timings the
     * sung words are lit and the rest stay back, otherwise the whole line is one run.
     */
    fun annotated(positionMs: Int, base: Color, lit: Color): AnnotatedString {
        val timed = words
        if (timed.isNullOrEmpty()) return AnnotatedString(text)

        return buildAnnotatedString {
            timed.forEachIndexed { index, word ->
                withStyle(
                    SpanStyle(color = if (positionMs >= word.startMs) lit else base)
                ) {
                    append(word.text)
                    if (index < timed.lastIndex) append(' ')
                }
            }
        }
    }
}

/**
 * tvOS-style lyrics: the lines themselves are the interface, big and rounded over the
 * drifting artwork, the sung line fully lit and the rest faded back. Every line is a
 * seek target, the focused one shows its timestamp, and word-synced lyrics light up
 * word by word as they are sung.
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

    var positionMs by remember { mutableIntStateOf(mediaController?.currentPosition?.toInt() ?: 0) }
    var durationMs by remember {
        mutableLongStateOf(mediaController?.duration?.coerceAtLeast(0L) ?: 0L)
    }

    LaunchedEffect(mediaController) {
        val controller = mediaController ?: return@LaunchedEffect
        while (true) {
            positionMs = controller.currentPosition.toInt()
            durationMs = controller.duration.coerceAtLeast(0L)
            delay(250)
        }
    }

    val rows = remember(lyrics, durationMs) {
        val raw = lyrics.lines
            .map { line ->
                LyricRow(
                    startMs = line.startMs,
                    text = line.lines.joinToString(" ") { it.text }.trim(),
                    words = line.lines.flatMap { it.words.orEmpty() }.takeIf { it.isNotEmpty() }
                )
            }
            .filter { it.text.isNotBlank() }

        // Plain lyrics carry no timestamps at all, so every line would sit on 0 and the
        // pane would never move. Spread them over the track instead: the last line lands
        // at the end of the song and the reader advances with playback. startMs is an
        // Int, so the spread stays an Int too.
        if (raw.any { it.startMs > 0 } || durationMs <= 0L || raw.isEmpty()) {
            raw
        } else {
            raw.mapIndexed { index, row ->
                row.copy(startMs = (durationMs * index / raw.size).toInt())
            }
        }
    }

    if (rows.isEmpty()) return

    val activeIndex = remember(positionMs, rows) {
        rows.indexOfLast { it.startMs <= positionMs }.coerceAtLeast(0)
    }
    var focusedIndex by remember { mutableIntStateOf(-1) }

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
            modifier = Modifier
                .fillMaxSize()
                .focusGroup(),
            contentPadding = PaddingValues(vertical = maxHeight * 0.36f),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            itemsIndexed(rows) { index, row ->
                val emphasis by animateFloatAsState(
                    targetValue = if (index == activeIndex) 1f else 0f,
                    animationSpec = emphasisSpec,
                    label = "line emphasis"
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = row.annotated(
                            positionMs = positionMs,
                            base = tint.copy(alpha = 0.45f),
                            lit = tint
                        ),
                        fontSize = size * (0.86f + 0.14f * emphasis),
                        fontWeight = if (index == activeIndex) FontWeight.Bold else FontWeight.Medium,
                        fontFamily = FontFamily.SansSerif,
                        lineHeight = size * 1.16f,
                        textAlign = textAlign,
                        color = tint.copy(alpha = 0.40f + 0.60f * emphasis),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            // A line is a seek target: press it and the song jumps there.
                            .clickable { mediaController?.seekTo(row.startMs.toLong()) }
                            .onFocusChanged { if (it.isFocused) focusedIndex = index }
                            .padding(vertical = 2.dp)
                    )

                    if (focusedIndex == index) {
                        Text(
                            text = StringUtils.formatSeconds(row.startMs / 1000),
                            fontSize = TvType.callout.fontSize,
                            fontWeight = FontWeight.SemiBold,
                            color = tint,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.Black.copy(alpha = 0.35f))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * The centred Up Next rail: the next covers in a row above the bar, the way the tvOS
 * player shows what is coming instead of taking over the screen with a list.
 */
@Composable
private fun TvQueueRail(
    mediaController: MediaController?,
    colours: ArtworkColours,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    val count = mediaController?.mediaItemCount ?: 0
    val current = mediaController?.currentMediaItemIndex ?: 0
    val first = remember { FocusRequester() }

    val upcoming = remember(mediaController, count, current) {
        (current + 1 until minOf(count, current + 11)).mapNotNull { index ->
            mediaController?.getMediaItemAt(index)?.let { index to it }
        }
    }

    LaunchedEffect(upcoming.firstOrNull()?.first) {
        runCatching { first.requestFocus() }
    }

    if (upcoming.isEmpty()) return

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .liquidGlass(RoundedCornerShape(20.dp), base = colours.base.copy(alpha = 0.92f))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        upcoming.forEachIndexed { position, entry ->
            val (index, item) = entry

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(104.dp)
            ) {
                Button(
                    onClick = {
                        mediaController?.seekToDefaultPosition(index)
                        mediaController?.play()
                        onDismiss()
                    },
                    modifier = Modifier
                        .then(if (position == 0) Modifier.focusRequester(first) else Modifier)
                        .size(104.dp),
                    shape = ButtonDefaults.shape(shape = RoundedCornerShape(8.dp)),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.colors(
                        containerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        contentColor = Color.White,
                        focusedContentColor = Color.White
                    )
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.mediaMetadata.artworkUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        fallback = painterResource(R.drawable.placeholder),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                Text(
                    text = item.mediaMetadata.title?.toString().orEmpty(),
                    fontSize = TvType.footnote.fontSize,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp)
                )
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
                .liquidGlass(RoundedCornerShape(20.dp))
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
                .liquidGlass(RoundedCornerShape(24.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (link == null) "Share Song" else title,
                color = Color.White,
                fontSize = TvType.headline.fontSize,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (link == null) {
                Text(
                    text = "Anyone with this link can listen until it expires.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = TvType.callout.fontSize,
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
                        fontSize = TvType.callout.fontSize
                    )

                if (failed)
                    Text(
                        text = "This server cannot create share links.",
                        color = Color(0xFFFF8A80),
                        fontSize = TvType.callout.fontSize
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
                    fontSize = TvType.callout.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Link expires in $lifetime",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = TvType.callout.fontSize,
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
            fontSize = TvType.callout.fontSize
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
            fontSize = TvType.body.fontSize,
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
                .liquidGlass(RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Sleep timer",
                color = Color.White,
                fontSize = TvType.headline.fontSize,
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
                    Text(text = "$minutes minutes", fontSize = TvType.body.fontSize)
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
                Text(text = "Turn off", fontSize = TvType.body.fontSize)
            }
        }
    }
}

@Composable
private fun TrackInfoCard(
    metadata: MediaMetadata?,
    color: Color,
    onGoToAlbum: () -> Unit = {},
    onGoToArtist: () -> Unit = {},
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val ctaFocus = remember { FocusRequester() }
    val albumId = metadata?.extras?.getString("albumId").orEmpty()
    val artistId = metadata?.extras?.getString("artistId").orEmpty()

    // The card is opaque, so it takes the theme's surface colour and writes on it in
    // the opposite ink instead of sitting on a translucent black slab.
    val lightSurface = color.luminance() > 0.5f
    val surface = if (lightSurface) Color.White else Color.Black
    val content = if (lightSurface) Color.Black else Color.White
    val durationMs = metadata?.durationMs ?: 0L
    val minutes = durationMs / 60_000L
    val seconds = (durationMs % 60_000L) / 1000L
    val format = metadata?.extras?.getString("format")?.uppercase()
    val bitrate = metadata?.extras?.getLong("bitrate")?.takeIf { it > 0 }
    val lossless = format?.contains("FLAC") == true ||
            format?.contains("ALAC") == true ||
            (bitrate ?: 0) >= 700

    LaunchedEffect(albumId, artistId) {
        if (albumId.isNotBlank() || artistId.isNotBlank()) {
            runCatching { ctaFocus.requestFocus() }
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .liquidGlass(RoundedCornerShape(18.dp), base = surface)
            .focusGroup()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown &&
                    event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_BACK
                ) {
                    onDismiss()
                    true
                } else {
                    false
                }
            }
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
                fontSize = TvType.body.fontSize,
                fontWeight = FontWeight.Bold,
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = listOfNotNull(
                    metadata?.artist?.toString()?.takeIf { it.isNotBlank() },
                    metadata?.albumTitle?.toString()?.takeIf { it.isNotBlank() }
                ).joinToString(" – "),
                fontSize = TvType.body.fontSize,
                color = content.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (durationMs > 0)
                Text(
                    text = "$minutes min, $seconds secs",
                    fontSize = TvType.callout.fontSize,
                    color = content.copy(alpha = 0.7f)
                )

            if (lossless)
                Text(
                    text = "◐  Lossless",
                    fontSize = TvType.callout.fontSize,
                    fontWeight = FontWeight.Medium,
                    color = content,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, content.copy(alpha = 0.45f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 3.dp)
                )
        }

        Spacer(Modifier.width(20.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            if (albumId.isNotBlank())
                Button(
                    onClick = onGoToAlbum,
                    modifier = Modifier
                        .focusRequester(ctaFocus)
                        .focusProperties { up = FocusRequester.Cancel },
                    shape = ButtonDefaults.shape(shape = RoundedCornerShape(10.dp)),
                    contentPadding = PaddingValues(horizontal = 22.dp, vertical = 6.dp),
                    colors = ButtonDefaults.colors(
                        containerColor = Color(0xFFC8DDFF),
                        disabledContainerColor = Color(0xFFC8DDFF),
                        contentColor = Color.Black,
                        disabledContentColor = Color.Black.copy(alpha = 0.4f),
                        focusedContainerColor = Color.White,
                        focusedContentColor = Color.Black
                    )
                ) {
                    Text(text = "Go to Album", fontSize = TvType.body.fontSize, fontWeight = FontWeight.Bold)
                }

            if (artistId.isNotBlank())
                Button(
                    onClick = onGoToArtist,
                    shape = ButtonDefaults.shape(shape = RoundedCornerShape(10.dp)),
                    contentPadding = PaddingValues(horizontal = 22.dp, vertical = 6.dp),
                    colors = ButtonDefaults.colors(
                        containerColor = content.copy(alpha = 0.10f),
                        disabledContainerColor = content.copy(alpha = 0.10f),
                        contentColor = content,
                        disabledContentColor = content.copy(alpha = 0.4f),
                        focusedContainerColor = content,
                        focusedContentColor = surface
                    )
                ) {
                    Text(text = "Go to Artist", fontSize = TvType.body.fontSize, fontWeight = FontWeight.Medium)
                }
        }
    }
}

/** The blurred cover behind the lyrics, so the pane frosts over the artwork. */
@Composable
private fun LyricsBackdrop(metadata: MediaMetadata?) {
    val scale = rememberInfiniteTransition(label = "lyrics backdrop")
    val drift by scale.animateFloat(
        initialValue = 1.16f,
        targetValue = 1.30f,
        animationSpec = infiniteRepeatable(tween(30_000, easing = LinearEasing), RepeatMode.Reverse),
        label = "lyrics backdrop scale"
    )

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(metadata?.artworkUri)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            fallback = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { scaleX = drift; scaleY = drift }
                .blur(70.dp)
        )

        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.42f))
        )
    }
}

/**
 * tvOS 27's now playing background is a field of colour taken from the artwork, not the
 * artwork itself: a few large, soft blobs of the cover's own hues drifting while each
 * one morphs into the next. Since 27.2 the motion is quicker than it used to be.
 *
 * The base is the cover's dominant hue at a mid-dark OKLCH lightness, so the field is
 * colourful instead of black, and every blob is brighter than that base, so a pastel
 * cover can no longer produce a pastel screen.
 */
@Composable
private fun ArtworkWash(colours: ArtworkColours) {
    val transition = rememberInfiniteTransition(label = "artwork wash")
    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14_000, easing = LinearEasing)),
        label = "wash drift"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(16_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "wash breathe"
    )

    Canvas(Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val reach = maxOf(width, height)

        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    colours.base,
                    lerp(colours.base, colours.accentB, 0.45f),
                    colours.base
                ),
                start = Offset(0f, 0f),
                end = Offset(width, height)
            )
        )

        // Every blob is larger than the screen, so colour reaches the corners instead of
        // fading out over the base and leaving the frame dark.
        val blobs = listOf(
            WashBlob(colours.accentA, colours.highlight, orbitX = 0.22f, orbitY = 0.11f, phase = 0f, radius = 0.92f, speed = 1f),
            WashBlob(colours.accentB, colours.accentA, orbitX = 0.28f, orbitY = 0.15f, phase = 0.37f, radius = 0.78f, speed = 0.62f),
            WashBlob(colours.highlight, colours.base, orbitX = 0.32f, orbitY = 0.17f, phase = 0.71f, radius = 0.66f, speed = 0.45f)
        )

        blobs.forEach { blob ->
            val cycle = (drift * blob.speed + blob.phase) % 1f
            val angle = cycle * 2f * Math.PI.toFloat()

            val center = Offset(
                x = width * (0.5f + blob.orbitX * cos(angle)),
                y = height * (0.5f + blob.orbitY * sin(angle))
            )
            val radius = reach * blob.radius * breathe

            val mix = 0.5f + 0.5f * sin(angle + blob.phase)
            val color = lerp(blob.from, blob.to, mix)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.62f),
                        color.copy(alpha = 0.34f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius
                ),
                radius = radius,
                center = center
            )
        }

        // Only the bottom needs help, because the bar sits on it. The labels at the top
        // are white on a mid-lightness base and need no scrim at all.
        drawRect(
            brush = Brush.verticalGradient(
                0.55f to Color.Transparent,
                0.82f to Color.Black.copy(alpha = 0.18f),
                1f to Color.Black.copy(alpha = 0.40f)
            )
        )
    }
}

private class WashBlob(
    val from: Color,
    val to: Color,
    val orbitX: Float,
    val orbitY: Float,
    val phase: Float,
    val radius: Float,
    val speed: Float
)
