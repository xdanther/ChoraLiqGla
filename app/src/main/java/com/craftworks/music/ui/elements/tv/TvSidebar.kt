package com.craftworks.music.ui.elements.tv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.session.MediaController
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.craftworks.music.R
import com.craftworks.music.data.BottomNavItem
import com.craftworks.music.data.model.Screen
import com.craftworks.music.managers.settings.AppearanceSettingsManager

/** Width of the labelled sidebar, measured against the 960dp TV canvas. */
private val SidebarWidth = 208.dp

/**
 * TV chrome modelled on Apple Music on Apple TV: a permanently visible, labelled
 * sidebar on the leading edge with the destination beside it. Items are text + icon,
 * dimmed when idle, brighter when selected and inverted (white pill, black text) when
 * focused.
 *
 * The player route opts out so Now Playing keeps the whole screen.
 */
@Composable
fun TvSidebarScaffold(
    navController: NavHostController,
    mediaController: MediaController?,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val backStackEntry by navController.currentBackStackEntryAsState()

    val navItems = AppearanceSettingsManager(context).bottomNavItemsFlow.collectAsState(
        initial = listOf(
            BottomNavItem(
                stringResource(R.string.nav_home), R.drawable.rounded_home_24, Screen.Home
            ),
            BottomNavItem(
                stringResource(R.string.nav_albums), R.drawable.rounded_library_music_24, Screen.Albums
            ),
            BottomNavItem(
                stringResource(R.string.nav_songs), R.drawable.round_music_note_24, Screen.Songs, false
            ),
            BottomNavItem(
                stringResource(R.string.nav_artists), R.drawable.rounded_artist_24, Screen.Artists
            ),
            BottomNavItem(
                stringResource(R.string.nav_radios), R.drawable.rounded_radio, Screen.Radios
            ),
            BottomNavItem(
                stringResource(R.string.nav_playlists), R.drawable.placeholder, Screen.Playlists
            ),
        )
    ).value

    var isPlayingVisible by remember { mutableStateOf(mediaController?.currentMediaItem != null) }
    LaunchedEffect(mediaController?.mediaMetadata) {
        isPlayingVisible = mediaController?.currentMediaItem != null
    }

    val isNowPlaying = backStackEntry.isOnRoute(Screen.NowPlayingLandscape)

    Row(modifier = Modifier.fillMaxSize()) {
        if (!isNowPlaying) {
            Column(
                modifier = Modifier
                    .width(SidebarWidth)
                    .fillMaxHeight()
                    .focusGroup()
                    .padding(start = 24.dp, end = 8.dp, top = 32.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                TvSidebarItem(
                    label = stringResource(R.string.nav_search),
                    icon = Icons.Rounded.Search,
                    selected = backStackEntry.isOnRoute(Screen.Search)
                ) {
                    if (!backStackEntry.isOnRoute(Screen.Search)) navController.navigate(Screen.Search) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }

                Spacer(Modifier.height(10.dp))

                navItems.forEach { item ->
                    if (!item.enabled) return@forEach

                    TvSidebarItem(
                        label = item.title,
                        icon = ImageVector.vectorResource(item.icon),
                        selected = backStackEntry.isOnRoute(item.screenRoute)
                    ) {
                        if (!backStackEntry.isOnRoute(item.screenRoute)) navController.navigate(item.screenRoute) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }

                if (isPlayingVisible) {
                    TvSidebarItem(
                        label = stringResource(R.string.nav_playing),
                        icon = ImageVector.vectorResource(R.drawable.s_m_playback),
                        selected = isNowPlaying
                    ) {
                        if (!isNowPlaying) navController.navigate(Screen.NowPlayingLandscape) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                TvSidebarItem(
                    label = stringResource(R.string.home_settings),
                    icon = ImageVector.vectorResource(R.drawable.rounded_settings_24),
                    selected = backStackEntry.isOnRoute(Screen.Settings)
                ) {
                    if (!backStackEntry.isOnRoute(Screen.Settings)) navController.navigate(Screen.Settings) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .focusRestorer()
                .focusGroup()
        ) {
            content()
        }
    }
}

@Composable
private fun TvSidebarItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = when {
            focused -> Color.White
            selected -> Color.White.copy(alpha = 0.14f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 150),
        label = "sidebar container"
    )
    val contentColor by animateColorAsState(
        targetValue = when {
            focused -> Color.Black
            selected -> Color.White
            else -> Color.White.copy(alpha = 0.55f)
        },
        animationSpec = tween(durationMillis = 150),
        label = "sidebar content"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .onFocusChanged { focused = it.isFocused }
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = label,
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun NavBackStackEntry?.isOnRoute(route: Screen): Boolean =
    this?.destination?.hierarchy?.any { it.hasRoute(route::class) } == true
