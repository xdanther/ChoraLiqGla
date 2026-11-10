package com.craftworks.music.ui.screens.tv

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.craftworks.music.R
import com.craftworks.music.data.model.LibraryType
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

        LaunchedEffect(Unit) { playRequester.requestFocus() }

        val artwork = remember(selectedAlbumId, currentProvider) {
            ImageRequest.Builder(context)
                .data(
                    currentProvider?.getImageUrl(
                        id = selectedAlbumId,
                        itemType = LibraryType.ALBUM
                    )
                )
                .memoryCacheKey(selectedAlbumId)
                .placeholderMemoryCacheKey(selectedAlbumId)
                .crossfade(true)
                .build()
        }

        val songs = currentAlbum.subList(1, currentAlbum.size)
        val groupedSongs = songs.groupBy { it.mediaMetadata.discNumber }
        val album = currentAlbum[0].mediaMetadata

        Box(modifier = Modifier.fillMaxSize()) {
            // The artwork, blown up and blurred, is the page's only background
            AsyncImage(
                model = artwork,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = 1.3f
                        scaleY = 1.3f
                        alpha = 0.6f
                    }
                    .blur(60.dp)
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.5f),
                            0.45f to Color.Black.copy(alpha = 0.72f),
                            1f to Color.Black.copy(alpha = 0.92f)
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(44.dp)
            ) {
                AsyncImage(
                    model = artwork,
                    fallback = painterResource(R.drawable.placeholder),
                    contentScale = ContentScale.Crop,
                    contentDescription = album.title?.toString(),
                    modifier = Modifier
                        .size(288.dp)
                        .shadow(24.dp, RoundedCornerShape(12.dp), clip = true)
                        .clip(RoundedCornerShape(12.dp))
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
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = album.artist?.toString() ?: "",
                        fontSize = 22.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = buildList {
                            album.genre?.toString()?.takeIf { it.isNotBlank() }?.let(::add)
                            album.recordingYear?.takeIf { it > 0 }?.let { add(it.toString()) }
                            album.extras?.getString("format")?.takeIf { it.isNotBlank() }?.let(::add)
                        }.joinToString(" · "),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.55f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(20.dp))

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
                                .height(46.dp)
                                .focusRequester(playRequester),
                            contentPadding = PaddingValues(horizontal = 26.dp, vertical = 8.dp),
                            colors = ButtonDefaults.colors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            )
                        ) {
                            Icon(
                                Icons.Rounded.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.action_play),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
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
                            modifier = Modifier.height(46.dp),
                            contentPadding = PaddingValues(horizontal = 22.dp, vertical = 8.dp),
                            colors = ButtonDefaults.colors(
                                containerColor = Color.White.copy(alpha = 0.16f),
                                contentColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedContentColor = Color.Black
                            )
                        ) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.round_shuffle_28),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.action_shuffle),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Button(
                            onClick = {
                                if (isStarred) viewModel.unstarAlbum(selectedAlbumId)
                                else viewModel.starAlbum(selectedAlbumId)
                                isStarred = !isStarred
                            },
                            modifier = Modifier.size(46.dp),
                            shape = ButtonDefaults.shape(shape = CircleShape),
                            contentPadding = PaddingValues(0.dp),
                            colors = ButtonDefaults.colors(
                                containerColor = Color.White.copy(alpha = 0.16f),
                                contentColor = Color.White,
                                focusedContainerColor = Color.White,
                                focusedContentColor = Color.Black
                            )
                        ) {
                            Icon(
                                imageVector = if (isStarred) Icons.Rounded.Check else Icons.Rounded.Add,
                                contentDescription = if (isStarred) "Remove from library" else "Add to library",
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .focusGroup(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        if (groupedSongs.size > 1) {
                            groupedSongs.forEach { (discNumber, disc) ->
                                item {
                                    Text(
                                        text = stringResource(R.string.album_details_disc, discNumber.toString()),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(
                                            start = 16.dp,
                                            top = 12.dp,
                                            bottom = 6.dp
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
                }
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
            isCurrent -> Color.White.copy(alpha = 0.12f)
            else -> Color.Transparent
        },
        animationSpec = tween(120),
        label = "Track row background"
    )
    val contentColor = if (focused) Color.Black else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showTrackNumber) {
            Box(
                modifier = Modifier.width(34.dp),
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
                        fontSize = 15.sp,
                        color = contentColor.copy(alpha = 0.55f)
                    )
            }
        }

        Text(
            text = metadata.title?.toString() ?: "",
            fontSize = 16.sp,
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
            fontSize = 15.sp,
            color = contentColor.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
