package com.craftworks.music.ui.elements.tv

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
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
import kotlinx.coroutines.delay

/** Width of the floating sidebar card, sized like the reference panel on a 960dp canvas. */
private val SidebarWidth = 224.dp

/** Scrim drawn over the screen while the sidebar is open. */
private val ScrimColor = Color.Black.copy(alpha = 0.30f)

/** The red tile Apple Music uses for its channel row, reused for Chora. */
private val ChannelTileColor = Color(0xFFFA233B)

/**
 * TV chrome modelled on Apple Music on Apple TV: a floating, rounded card that
 * carries the account row, the destinations as pill rows and a channel tile at
 * the bottom. It opens from the remote's options/menu button and closes with back.
 *
 * The panel is an overlay, so screens keep the full canvas while it is closed. It is
 * not offered on the player route, which keeps the whole screen.
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
    var isSidebarOpen by rememberSaveable { mutableStateOf(false) }
    val firstItemFocusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown || isNowPlaying) return@onPreviewKeyEvent false

                when {
                    event.key == Key.Menu -> {
                        isSidebarOpen = !isSidebarOpen
                        true
                    }

                    isSidebarOpen && (event.key == Key.Back || event.key == Key.Escape) -> {
                        isSidebarOpen = false
                        true
                    }

                    else -> false
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusRestorer()
                .focusGroup()
        ) {
            content()
        }

        if (isSidebarOpen && !isNowPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ScrimColor)
            )

            TvSidebarPanel(
                navItems = navItems,
                backStackEntry = backStackEntry,
                isPlayingVisible = isPlayingVisible,
                firstItemFocusRequester = firstItemFocusRequester,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = 20.dp)
            ) { screen ->
                if (!backStackEntry.isOnRoute(screen)) navController.navigate(screen) {
                    launchSingleTop = true
                    restoreState = true
                }
                isSidebarOpen = false
            }
        }
    }
}

@Composable
private fun TvSidebarPanel(
    navItems: List<BottomNavItem>,
    backStackEntry: NavBackStackEntry?,
    isPlayingVisible: Boolean,
    firstItemFocusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val username by AppearanceSettingsManager(context).usernameFlow.collectAsState("Username")

    var clock by remember { mutableStateOf(currentClock()) }
    LaunchedEffect(Unit) {
        while (true) {
            clock = currentClock()
            delay(20_000L)
        }
    }

    LaunchedEffect(Unit) {
        firstItemFocusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .width(SidebarWidth)
            .wrapContentHeight()
            .clip(RoundedCornerShape(26.dp))
            .background(Color.Black.copy(alpha = 0.62f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(26.dp))
            .focusGroup()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Account row: avatar, name, time — the first thing the reference panel shows
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.round_music_note_24),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.width(10.dp))

            Text(
                text = username,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = clock,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.45f),
                maxLines = 1
            )
        }

        TvSidebarItem(
            label = stringResource(R.string.nav_search),
            icon = Icons.Rounded.Search,
            selected = backStackEntry.isOnRoute(Screen.Search),
            modifier = Modifier.focusRequester(firstItemFocusRequester)
        ) {
            onNavigate(Screen.Search)
        }

        navItems.filter { it.enabled }.forEach { item ->
            TvSidebarItem(
                label = item.title,
                icon = ImageVector.vectorResource(item.icon),
                selected = backStackEntry.isOnRoute(item.screenRoute)
            ) {
                onNavigate(item.screenRoute)
            }
        }

        if (isPlayingVisible) {
            TvSidebarItem(
                label = stringResource(R.string.nav_playing),
                icon = ImageVector.vectorResource(R.drawable.s_m_playback),
                selected = backStackEntry.isOnRoute(Screen.NowPlayingLandscape)
            ) {
                onNavigate(Screen.NowPlayingLandscape)
            }
        }

        Spacer(Modifier.height(14.dp))

        Text(
            text = "More",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.45f),
            modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)
        )

        TvSidebarChannelItem(
            label = stringResource(R.string.home_settings),
            selected = backStackEntry.isOnRoute(Screen.Settings)
        ) {
            onNavigate(Screen.Settings)
        }
    }
}

@Composable
private fun TvSidebarItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = when {
            focused -> Color.White
            selected -> Color.White.copy(alpha = 0.12f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 140),
        label = "sidebar container"
    )
    val contentColor by animateColorAsState(
        targetValue = when {
            focused -> Color.Black
            selected -> Color.White
            else -> Color.White.copy(alpha = 0.82f)
        },
        animationSpec = tween(durationMillis = 140),
        label = "sidebar content"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(CircleShape)
            .background(containerColor)
            .onFocusChanged { focused = it.isFocused }
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
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

@Composable
private fun TvSidebarChannelItem(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = when {
            focused -> Color.White.copy(alpha = 0.12f)
            selected -> Color.White.copy(alpha = 0.06f)
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 140),
        label = "channel container"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .onFocusChanged { focused = it.isFocused }
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(ChannelTileColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.round_music_note_24),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun currentClock(): String =
    java.time.LocalTime.now().format(
        java.time.format.DateTimeFormatter.ofPattern(
            "h:mm a",
            java.util.Locale.getDefault()
        )
    )

private fun NavBackStackEntry?.isOnRoute(route: Screen): Boolean =
    this?.destination?.hierarchy?.any { it.hasRoute(route::class) } == true
