package com.craftworks.music

import android.util.Log
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.craftworks.music.data.model.Screen
import com.craftworks.music.managers.settings.AppearanceSettingsManager
import com.craftworks.music.ui.elements.tv.TvSidebarScaffold
import com.craftworks.music.ui.playing.NowPlayingContent
import com.craftworks.music.ui.playing.NowPlayingViewModel
import com.craftworks.music.ui.screens.tv.TvAlbumDetails
import com.craftworks.music.ui.screens.tv.TvAlbumScreen
import com.craftworks.music.ui.screens.tv.TvArtistDetailsScreen
import com.craftworks.music.ui.screens.tv.TvArtistScreen
import com.craftworks.music.ui.screens.tv.TvHomeScreen
import com.craftworks.music.ui.screens.tv.TvPlaylistDetails
import com.craftworks.music.ui.screens.tv.TvPlaylistScreen
import com.craftworks.music.ui.screens.tv.TvRadioScreen
import com.craftworks.music.ui.screens.tv.TvSearchScreen
import com.craftworks.music.ui.screens.tv.TvSettingScreen
import com.craftworks.music.ui.screens.tv.TvSongsScreen
import com.craftworks.music.ui.screens.tv.settings.TvS_AppearanceScreen
import com.craftworks.music.ui.screens.tv.settings.TvS_LibraryScreen
import com.craftworks.music.ui.screens.tv.settings.TvS_PlaybackScreen
import com.craftworks.music.ui.screens.tv.settings.TvS_ProviderScreen
import com.craftworks.music.ui.viewmodels.AlbumScreenViewModel
import com.craftworks.music.ui.viewmodels.ArtistsScreenViewModel
import com.craftworks.music.ui.viewmodels.HomeScreenViewModel
import com.craftworks.music.ui.viewmodels.PlaylistScreenViewModel
import com.craftworks.music.ui.viewmodels.RadioScreenViewModel
import com.craftworks.music.ui.viewmodels.SongsScreenViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    mediaController: MediaController?
) {
    val animationSpec = MaterialTheme.LocalMaterialTheme.current.motionScheme.slowSpatialSpec<Float>()

    NavHost(
        navController = navController,
        startDestination = Screen.MainGraph,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { fadeIn(animationSpec) },
        exitTransition = { fadeOut(animationSpec) },
        popEnterTransition = { fadeIn(animationSpec) },
        popExitTransition = { fadeOut(animationSpec) },
        predictivePopEnterTransition = { fadeIn(animationSpec) },
        predictivePopExitTransition = { fadeOut(animationSpec) }
    ) {
        navigation<Screen.MainGraph>(startDestination = Screen.Home) {
            composable<Screen.Home> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<Screen.MainGraph>()
                }
                val viewModel: HomeScreenViewModel = hiltViewModel(parentEntry)
                TvSidebarScaffold(navController, mediaController) {
                    TvHomeScreen(navController, mediaController, viewModel)
                }
            }

            composable<Screen.Songs> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<Screen.MainGraph>()
                }
                val viewModel: SongsScreenViewModel = hiltViewModel(parentEntry)
                TvSidebarScaffold(navController, mediaController) {
                    TvSongsScreen(mediaController, navController, viewModel)
                }
            }

            composable<Screen.Radios> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<Screen.MainGraph>()
                }
                val viewModel: RadioScreenViewModel = hiltViewModel(parentEntry)
                TvSidebarScaffold(navController, mediaController) {
                    TvRadioScreen(mediaController, navController, viewModel)
                }
            }

            navigation<Screen.Albums>(startDestination = Screen.AlbumList) {
                composable<Screen.AlbumList> { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry<Screen.MainGraph>()
                    }
                    val viewModel: AlbumScreenViewModel = hiltViewModel(parentEntry)
                    TvSidebarScaffold(navController, mediaController) {
                        TvAlbumScreen(navController, viewModel)
                    }
                }
                composable<Screen.AlbumDetails> { backStackEntry ->
                    val albumId = backStackEntry.toRoute<Screen.AlbumDetails>().albumId
                    val imageUri = backStackEntry.toRoute<Screen.AlbumDetails>().imageUri
                    TvSidebarScaffold(navController, mediaController) {
                        TvAlbumDetails(
                            albumId,
                            imageUri.toUri(),
                            mediaController,
                            navController
                        )
                    }
                }
            }

            navigation<Screen.Artists>(startDestination = Screen.ArtistsList) {
                composable<Screen.ArtistsList> { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry<Screen.MainGraph>()
                    }
                    val viewModel: ArtistsScreenViewModel = hiltViewModel(parentEntry)
                    TvSidebarScaffold(navController, mediaController) {
                        TvArtistScreen(navController, viewModel)
                    }
                }
                composable<Screen.ArtistDetails> { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry<Screen.MainGraph>()
                    }
                    val artistId = backStackEntry.toRoute<Screen.ArtistDetails>().artistId
                    val imageUri = backStackEntry.toRoute<Screen.ArtistDetails>().imageUri
                    val viewModel: ArtistsScreenViewModel = hiltViewModel(parentEntry)
                    TvSidebarScaffold(navController, mediaController) {
                        TvArtistDetailsScreen(
                            artistId,
                            imageUri,
                            navController,
                            mediaController,
                            viewModel
                        )
                    }
                }
            }

            navigation<Screen.Playlists>(startDestination = Screen.PlaylistList) {
                composable<Screen.PlaylistList> { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry<Screen.MainGraph>()
                    }
                    val viewModel: PlaylistScreenViewModel = hiltViewModel(parentEntry)
                    TvSidebarScaffold(navController, mediaController) {
                        TvPlaylistScreen(navController, viewModel)
                    }
                }
                composable<Screen.PlaylistDetails> { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry<Screen.MainGraph>()
                    }
                    val playlistId = backStackEntry.toRoute<Screen.PlaylistDetails>().playlistId
                    val imageUri = backStackEntry.toRoute<Screen.PlaylistDetails>().imageUri
                    val viewModel: PlaylistScreenViewModel = hiltViewModel(parentEntry)
                    TvSidebarScaffold(navController, mediaController) {
                        TvPlaylistDetails(
                            playlistId,
                            imageUri,
                            navController,
                            mediaController,
                            viewModel
                        )
                    }
                }
            }

            navigation<Screen.Settings>(startDestination = Screen.SettingsList) {
                val enterSpec = tween<Float>(
                    durationMillis = 210,
                    delayMillis = 90,
                    easing = LinearOutSlowInEasing
                )
                val exitSpec = tween<Float>(durationMillis = 90, easing = FastOutLinearInEasing)
                val enterTransition = slideInHorizontally(
                    animationSpec = tween(durationMillis = 300)
                ) { fullWidth -> fullWidth / 4 } + fadeIn(enterSpec)
                val exitTransition = slideOutHorizontally(
                    animationSpec = tween(durationMillis = 300)
                ) { fullWidth -> fullWidth / 4 } + fadeOut(exitSpec)

                composable<Screen.SettingsList> {
                    TvSidebarScaffold(navController, mediaController) {
                        TvSettingScreen(navController)
                    }
                }
                composable<Screen.S_Appearance>(
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition }
                ) {
                    TvS_AppearanceScreen()
                }
                composable<Screen.S_Media_Providers>(
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition }
                ) {
                    TvS_ProviderScreen()
                }
                composable<Screen.S_Lyrics_Providers>(
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition }
                ) {
                    TvS_ProviderScreen()
                }
                composable<Screen.S_Playback>(
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition }
                ) {
                    TvS_PlaybackScreen()
                }
                composable<Screen.S_Library>(
                    enterTransition = { enterTransition },
                    exitTransition = { exitTransition }
                ) {
                    TvS_LibraryScreen()
                }
            }

            composable<Screen.NowPlaying> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<Screen.MainGraph>()
                }
                val viewModel: NowPlayingViewModel = hiltViewModel(parentEntry)
                var metadata by remember { mutableStateOf<MediaMetadata?>(null) }

                LaunchedEffect(mediaController) {
                    if (mediaController?.currentMediaItem != null) {
                        metadata = mediaController.currentMediaItem?.mediaMetadata
                    }
                }
                DisposableEffect(mediaController) {
                    val listener = object : Player.Listener {
                        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                            super.onMediaItemTransition(mediaItem, reason)
                            metadata = mediaController?.currentMediaItem?.mediaMetadata
                        }
                    }
                    mediaController?.addListener(listener)
                    onDispose { mediaController?.removeListener(listener) }
                }

                NowPlayingContent(
                    mediaController,
                    metadata,
                    navController,
                    viewModel
                )

                val currentView = LocalView.current
                val disableScreenStandy by AppearanceSettingsManager(LocalContext.current)
                    .disableScreenStandby.collectAsStateWithLifecycle(true)
                DisposableEffect(Unit) {
                    if (disableScreenStandy) {
                        currentView.keepScreenOn = true
                        Log.d("NOW-PLAYING", "KeepScreenOn: True")
                    }
                    onDispose {
                        currentView.keepScreenOn = false
                        Log.d("NOW-PLAYING", "KeepScreenOn: False")
                    }
                }
            }

            composable<Screen.Search> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<Screen.MainGraph>()
                }
                val albumViewModel: AlbumScreenViewModel = hiltViewModel(parentEntry)
                val songViewModel: SongsScreenViewModel = hiltViewModel(parentEntry)
                val artistViewModel: ArtistsScreenViewModel = hiltViewModel(parentEntry)

                TvSidebarScaffold(navController, mediaController) {
                    TvSearchScreen(
                        navController,
                        mediaController,
                        albumViewModel,
                        songViewModel,
                        artistViewModel
                    )
                }
            }
        }
    }
}
