package com.craftworks.music.ui.screens.tv

import com.craftworks.music.ui.theme.TvType
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media.utils.MediaConstants.METADATA_KEY_IS_EXPLICIT
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.craftworks.music.R
import com.craftworks.music.data.model.LibraryType
import com.craftworks.music.data.model.artists
import com.craftworks.music.data.model.Screen
import com.craftworks.music.data.model.favorite
import com.craftworks.music.data.model.id
import com.craftworks.music.managers.MediaProviderManager
import com.craftworks.music.managers.settings.AppearanceSettingsManager
import com.craftworks.music.player.SongHelper
import com.craftworks.music.ui.elements.dialogs.tv.SongDialog
import com.craftworks.music.ui.viewmodels.AlbumDetailsViewModel
import com.craftworks.music.utils.StringUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * The album page follows Apple Music's proportions on a 960x540dp television:
 * a ~400dp cover on the left and a ~450dp column of text, pills and rows on the
 * right, which leaves four track rows and the release footer in view.
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvAlbumDetails(
    selectedAlbumId: String = "",
    selectedAlbumImage: Uri = Uri.EMPTY,
    mediaController: MediaController? = null,
    navHostController: NavHostController = rememberNavController(),
    viewModel: AlbumDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val currentProvider by MediaProviderManager.currentProvider.collectAsStateWithLifecycle()
    val currentAlbum = viewModel.songsInAlbum.collectAsStateWithLifecycle().value
    val showTrackNumbers by AppearanceSettingsManager(context)
        .showTrackNumbersFlow.collectAsStateWithLifecycle(false)

    var selectedSong by remember { mutableStateOf(MediaItem.EMPTY) }
    var showSongDialog by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }

    var currentMediaId by remember { mutableStateOf<String?>(null) }
    var playerPlaying by remember { mutableStateOf(false) }

    LaunchedEffect(selectedAlbumId) {
        showLoading = false

        viewModel.loadAlbumDetails(selectedAlbumId)

        delay(500.milliseconds)
        showLoading = true
    }

    DisposableEffect(mediaController) {
        val listener = object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                currentMediaId = mediaItem?.mediaId
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                playerPlaying = isPlaying
            }
        }

        mediaController?.addListener(listener)
        currentMediaId = mediaController?.currentMediaItem?.mediaId
        playerPlaying = mediaController?.isPlaying == true

        onDispose { mediaController?.removeListener(listener) }
    }

    AnimatedVisibility(
        visible = currentAlbum.isEmpty() && showLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 6.dp
            )
        }
    }

    AnimatedVisibility(
        visible = currentAlbum.isNotEmpty(),
        enter = fadeIn()
    ) {
        val coroutineScope = rememberCoroutineScope()
        val playRequester = remember { FocusRequester() }

        var isStarred by remember {
            mutableStateOf(
                currentAlbum[0].mediaMetadata.favorite ?: false
            )
        }
        var descriptionExpanded by remember { mutableStateOf(false) }
        var showOptions by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) { playRequester.requestFocus() }

        val artwork = remember(selectedAlbumId, currentProvider) {
            ImageRequest.Builder(context)
                .data(
                    currentProvider?.getImageUrl(
                        id = selectedAlbumId,
                        itemType = LibraryType.ALBUM,
                        size = 1080
                    )
                )
                .size(1080)
                .memoryCacheKey("$selectedAlbumId@1080")
                .placeholderMemoryCacheKey("$selectedAlbumId@1080")
                .crossfade(true)
                .build()
        }

        val songs = currentAlbum.subList(1, currentAlbum.size)
        val groupedSongs = songs.groupBy { it.mediaMetadata.discNumber }
        val album = currentAlbum[0].mediaMetadata

        val comment = album.extras?.getString("comment")?.takeIf { it.isNotBlank() }
        val releaseDate = formatReleaseDate(album.extras?.getString("releaseDate"))
        val recordLabels = album.extras?.getString("recordLabels")?.takeIf { it.isNotBlank() }
        val totalMillis = songs.sumOf { it.mediaMetadata.durationMs ?: 0L }
        val totalMinutes = (totalMillis / 60_000L).toInt()

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            // 398dp in the reference, and never wider than the left column allows
            val coverSize = minOf(maxHeight * 0.66f, maxWidth * 0.38f)

            // The artwork, blown up and blurred, is the page's only background
            AsyncImage(
                model = artwork,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = 1.15f
                        scaleY = 1.15f
                        alpha = 0.92f
                    }
                    .blur(72.dp)
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.26f),
                            0.45f to Color.Black.copy(alpha = 0.46f),
                            1f to Color.Black.copy(alpha = 0.78f)
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 34.dp, end = 30.dp, top = 40.dp, bottom = 22.dp),
                horizontalArrangement = Arrangement.spacedBy(36.dp)
            ) {
                AsyncImage(
                    model = artwork,
                    fallback = painterResource(R.drawable.placeholder),
                    contentScale = ContentScale.Crop,
                    contentDescription = album.title?.toString(),
                    modifier = Modifier
                        .size(coverSize)
                        .clip(RoundedCornerShape(8.dp))
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .focusRestorer()
                        .focusGroup()
                ) {
                    Text(
                        text = album.title?.toString() ?: "",
                        fontSize = TvType.title1.fontSize,
                        lineHeight = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(2.dp))

                    Text(
                        text = album.artist?.toString() ?: "",
                        fontSize = TvType.headline.fontSize,
                        color = Color.White.copy(alpha = 0.78f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = buildList {
                            album.genre?.toString()?.takeIf { it.isNotBlank() }?.let(::add)
                            album.recordingYear?.takeIf { it > 0 }?.let { add(it.toString()) }
                            album.extras?.getString("format")?.takeIf { it.isNotBlank() }?.let(::add)
                        }.joinToString(" · "),
                        fontSize = TvType.body.fontSize,
                        color = Color.White.copy(alpha = 0.5f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (comment != null) {
                        Spacer(Modifier.height(14.dp))

                        var descriptionOverflows by remember(comment) { mutableStateOf(false) }

                        Text(
                            text = comment,
                            fontSize = TvType.body.fontSize,
                            lineHeight = 19.sp,
                            color = Color.White.copy(alpha = 0.72f),
                            maxLines = if (descriptionExpanded) 6 else 2,
                            overflow = TextOverflow.Ellipsis,
                            onTextLayout = {
                                if (!descriptionExpanded) descriptionOverflows = it.hasVisualOverflow
                            }
                        )

                        if (descriptionOverflows || descriptionExpanded) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = { descriptionExpanded = !descriptionExpanded },
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                    colors = ButtonDefaults.colors(
                                        containerColor = Color.Transparent,
                                        contentColor = Color.White,
                                        disabledContainerColor = Color.Transparent,
                                        disabledContentColor = Color.White,
                                        focusedContainerColor = Color.White,
                                        focusedContentColor = Color.Black
                                    )
                                ) {
                                    Text(
                                        text = if (descriptionExpanded) "LESS" else "MORE",
                                        fontSize = TvType.callout.fontSize,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    SongHelper.play(songs, 0, mediaController)
                                    navHostController.navigate(Screen.NowPlayingLandscape) {
                                        launchSingleTop = true
                                    }
                                }
                            },
                            modifier = Modifier
                                // min, not fixed: the label grows with the text size
                                .heightIn(min = 40.dp)
                                .focusRequester(playRequester),
                            shape = ButtonDefaults.shape(shape = CircleShape),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                            colors = ButtonDefaults.colors(
                                containerColor = Color.White.copy(alpha = 0.14f),
                                contentColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedContentColor = Color.Black
                            )
                        ) {
                            Icon(
                                Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.action_play),
                                fontSize = TvType.body.fontSize,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                mediaController?.shuffleModeEnabled = true
                                coroutineScope.launch {
                                    val random = songs.indices.random()
                                    SongHelper.play(songs, random, mediaController)
                                    navHostController.navigate(Screen.NowPlayingLandscape) {
                                        launchSingleTop = true
                                    }
                                }
                            },
                            modifier = Modifier.size(40.dp),
                            shape = ButtonDefaults.shape(shape = CircleShape),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.colors(
                                containerColor = Color.White.copy(alpha = 0.14f),
                                contentColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedContentColor = Color.Black
                            )
                        ) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.round_shuffle_28),
                                contentDescription = stringResource(R.string.action_shuffle),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Button(
                            onClick = { showOptions = true },
                            modifier = Modifier.size(40.dp),
                            shape = ButtonDefaults.shape(shape = CircleShape),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.colors(
                                containerColor = Color.White.copy(alpha = 0.14f),
                                contentColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedContentColor = Color.Black
                            )
                        ) {
                            MoreDots()
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .focusGroup(),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (groupedSongs.size > 1) {
                            groupedSongs.forEach { (discNumber, disc) ->
                                item {
                                    Text(
                                        text = stringResource(R.string.album_details_disc, discNumber.toString()),
                                        fontSize = TvType.callout.fontSize,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(
                                            start = 14.dp,
                                            top = 8.dp,
                                            bottom = 4.dp
                                        )
                                    )
                                }
                                items(disc) { song ->
                                    TvAlbumTrackRow(
                                        song = song,
                                        isCurrent = song.mediaId == currentMediaId,
                                        showTrackNumber = showTrackNumbers,
                                        onClick = {
                                            coroutineScope.launch {
                                                SongHelper.play(songs, songs.indexOf(song), mediaController)
                                                navHostController.navigate(Screen.NowPlayingLandscape) {
                                                    launchSingleTop = true
                                                }
                                            }
                                        },
                                        onLongClick = {
                                            selectedSong = song
                                            showSongDialog = true
                                        }
                                    )
                                }
                            }
                        } else {
                            items(songs) { song ->
                                TvAlbumTrackRow(
                                    song = song,
                                    isCurrent = song.mediaId == currentMediaId,
                                    showTrackNumber = showTrackNumbers,
                                    onClick = {
                                        coroutineScope.launch {
                                            SongHelper.play(songs, songs.indexOf(song), mediaController)
                                            navHostController.navigate(Screen.NowPlayingLandscape) {
                                                launchSingleTop = true
                                            }
                                        }
                                    },
                                    onLongClick = {
                                        selectedSong = song
                                        showSongDialog = true
                                    }
                                )
                            }
                        }
                    }

                    // Release footer: date, length, then the rights line when the provider has one
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        releaseDate?.let {
                            Text(
                                text = it,
                                fontSize = TvType.callout.fontSize,
                                color = Color.White.copy(alpha = 0.45f),
                                maxLines = 1
                            )
                        }
                        Text(
                            text = buildString {
                                append(songs.size)
                                append(if (songs.size == 1) " song, " else " songs, ")
                                append(totalMinutes)
                                append(if (totalMinutes == 1) " minute" else " minutes")
                            },
                            fontSize = TvType.callout.fontSize,
                            color = Color.White.copy(alpha = 0.45f),
                            maxLines = 1
                        )
                        recordLabels?.let { labels ->
                            Text(
                                text = "© " + buildString {
                                    album.recordingYear?.takeIf { it > 0 }?.let {
                                        append(it)
                                        append(' ')
                                    }
                                    append(labels)
                                },
                                fontSize = TvType.callout.fontSize,
                                color = Color.White.copy(alpha = 0.45f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            if (showOptions) {
                val artist = album.artists?.firstOrNull()
                AlbumOptionsDialog(
                    title = album.title?.toString() ?: "",
                    artistName = album.artist?.toString() ?: "",
                    canOpenArtist = artist != null,
                    isStarred = isStarred,
                    onGoToArtist = {
                        showOptions = false
                        artist?.let {
                            navHostController.navigate(
                                Screen.ArtistDetails(
                                    it.id,
                                    it.imageUrl ?: it.imageId?.let { imageId ->
                                        currentProvider?.getImageUrl(imageId, LibraryType.ARTIST, 600)
                                    }
                                )
                            ) { launchSingleTop = true }
                        }
                    },
                    onPlayNext = {
                        showOptions = false
                        SongHelper.playNext(songs, mediaController)
                    },
                    onAddToQueue = {
                        showOptions = false
                        SongHelper.enqueue(songs, mediaController)
                    },
                    onDownload = {
                        showOptions = false
                        viewModel.downloadAlbum(songs)
                    },
                    onToggleLibrary = {
                        showOptions = false
                        if (isStarred) viewModel.unstarAlbum(selectedAlbumId)
                        else viewModel.starAlbum(selectedAlbumId)
                        isStarred = !isStarred
                    },
                    onDismiss = { showOptions = false }
                )
            }
        }
    }

    if (showSongDialog)
        SongDialog(
            song = selectedSong,
            onSetRating = { rating ->
                viewModel.setSongRating(
                    songId = selectedSong.mediaMetadata.id ?: "",
                    rating = rating
                )
            },
            onDownload = {
                viewModel.downloadSong(it)
            },
            setShowDialog = { showSongDialog = it }
        )
}

@Composable
private fun MoreDots() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) {
            Box(
                Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
            )
        }
    }
}

@Composable
private fun AlbumOptionsDialog(
    title: String,
    artistName: String,
    canOpenArtist: Boolean,
    isStarred: Boolean,
    onGoToArtist: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onDownload: () -> Unit,
    onToggleLibrary: () -> Unit,
    onDismiss: () -> Unit
) {
    val firstItem = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        runCatching { firstItem.requestFocus() }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                // The art stays visible behind the sheet, as on tvOS
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(horizontal = 56.dp, vertical = 48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 460.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = TvType.title3.fontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = artistName,
                    fontSize = TvType.body.fontSize,
                    color = Color.White.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(12.dp))

                if (canOpenArtist)
                    AlbumOptionItem(
                        text = "Go to artist",
                        modifier = Modifier.focusRequester(firstItem),
                        onClick = onGoToArtist
                    )

                AlbumOptionItem(
                    text = "Play next",
                    modifier = if (canOpenArtist) Modifier else Modifier.focusRequester(firstItem),
                    onClick = onPlayNext
                )

                AlbumOptionItem(text = "Add to queue", onClick = onAddToQueue)
                AlbumOptionItem(text = "Download album", onClick = onDownload)
                AlbumOptionItem(
                    text = if (isStarred) "Remove from library" else "Add to library",
                    onClick = onToggleLibrary
                )
            }
        }
    }
}

@Composable
private fun AlbumOptionItem(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ListItem(
        selected = false,
        headlineContent = { Text(text = text) },
        modifier = modifier,
        onClick = onClick
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TvAlbumTrackRow(
    song: MediaItem,
    isCurrent: Boolean,
    showTrackNumber: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val metadata: MediaMetadata = song.mediaMetadata
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    val background by animateColorAsState(
        targetValue = when {
            focused -> Color.White
            isCurrent -> Color.White.copy(alpha = 0.78f)
            else -> Color.Transparent
        },
        animationSpec = tween(120),
        label = "Track row background"
    )
    val contentColor = if (focused || isCurrent) Color.Black else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 46.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showTrackNumber) {
            Box(
                modifier = Modifier.width(30.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (isCurrent)
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Now playing",
                        tint = contentColor,
                        modifier = Modifier.size(16.dp)
                    )
                else
                    Text(
                        text = (metadata.trackNumber ?: 0).toString(),
                        fontSize = TvType.body.fontSize,
                        color = contentColor.copy(alpha = if (focused) 0.7f else 0.5f)
                    )
            }
        }

        if (metadata.favorite == true) {
            Text(
                text = "★",
                fontSize = TvType.subhead.fontSize,
                color = contentColor.copy(alpha = 0.55f),
                modifier = Modifier.padding(end = 6.dp)
            )
        }

        Text(
            text = metadata.title?.toString() ?: "",
            fontSize = TvType.body.fontSize,
            fontWeight = if (isCurrent) FontWeight.Medium else FontWeight.Normal,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        if (metadata.extras?.getBoolean(METADATA_KEY_IS_EXPLICIT) == true)
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.rounded_explicit_24),
                contentDescription = "Explicit",
                tint = contentColor.copy(alpha = 0.7f),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(14.dp)
            )

        Text(
            text = StringUtils.formatSeconds(
                metadata.durationMs?.div(1000)?.toInt() ?: 0
            ),
            fontSize = TvType.body.fontSize,
            color = contentColor.copy(alpha = if (focused || isCurrent) 0.75f else 0.5f),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

/** "2026-08-28" reads as "August 28, 2026" under the track list, as on tvOS. */
private fun formatReleaseDate(raw: String?): String? {
    val value = raw?.trim()?.takeIf { it.isNotBlank() } ?: return null
    if (value.length < 10) return value
    return try {
        java.time.LocalDate.parse(value.take(10)).format(
            java.time.format.DateTimeFormatter.ofPattern(
                "MMMM d, yyyy",
                java.util.Locale.getDefault()
            )
        )
    } catch (_: Exception) {
        value
    }
}
