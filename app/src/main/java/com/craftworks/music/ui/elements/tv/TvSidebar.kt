package com.craftworks.music.ui.elements.tv

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
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

/**
 * TV chrome modelled on Apple Music on Apple TV: a floating, frosted card that
 * carries the account row, the destinations as pill rows and a channel tile at
 * the bottom. It slides in from the remote's options/menu button, and back on a
 * root screen opens it before it closes the app.
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
    val backdrop = rememberGraphicsLayer()
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

                    // Back on a root screen summons the panel before it leaves the app
                    (event.key == Key.Back || event.key == Key.Escape) &&
                            backStackEntry.isOnRoute(Screen.Home) -> {
                        isSidebarOpen = true
                        true
                    }

                    else -> false
                }
            }
    ) {
        // The page is recorded once per frame so the panel can sit on a blurred copy
        // of it; that is what makes the glass read as glass rather than a dark slab.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusRestorer()
                .focusGroup()
                .drawWithContent {
                    backdrop.record { this@drawWithContent.drawContent() }
                    drawLayer(backdrop)
                }
        ) {
            content()
        }

        if (isSidebarOpen && !isNowPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.14f))
            )
        }

        AnimatedVisibility(
            visible = isSidebarOpen && !isNowPlaying,
            enter = fadeIn(tween(200)) + slideInHorizontally(
                animationSpec = tween(280),
                initialOffsetX = { -it / 4 }
            ) + scaleIn(
                initialScale = 0.94f,
                transformOrigin = TransformOrigin(0f, 0f),
                animationSpec = tween(280)
            ),
            exit = fadeOut(tween(140)) + slideOutHorizontally(
                animationSpec = tween(200),
                targetOffsetX = { -it / 4 }
            ) + scaleOut(
                targetScale = 0.94f,
                transformOrigin = TransformOrigin(0f, 0f),
                animationSpec = tween(200)
            ),
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Box(modifier = Modifier.padding(start = 20.dp, top = 20.dp)) {
                // Only the patch of page the card covers is blurred: the recording is
                // full-screen, so it is shifted back by the card's own inset and
                // clipped to the card's outline.
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            shape = RoundedCornerShape(26.dp)
                            clip = true
                            renderEffect = BlurEffect(56f, 56f, TileMode.Clamp)
                        }
                        .drawWithContent {
                            translate(left = -20.dp.toPx(), top = -20.dp.toPx()) {
                                drawLayer(backdrop)
                            }
                        }
                )

                TvSidebarPanel(
                    navItems = navItems,
                    backStackEntry = backStackEntry,
                    isPlayingVisible = isPlayingVisible,
                    firstItemFocusRequester = firstItemFocusRequester
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
            // A frost, not a page blur: layered translucency keeps the labels readable
            // over whatever artwork sits behind the panel.
            .background(
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.14f), Color.Black.copy(alpha = 0.26f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(26.dp))
            .focusGroup()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
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
                    .background(Color.White.copy(alpha = 0.16f)),
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
                color = Color.White.copy(alpha = 0.50f),
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
            color = Color.White.copy(alpha = 0.50f),
            modifier = Modifier.padding(start = 6.dp, bottom = 4.dp)
        )

        TvSidebarItem(
            label = stringResource(R.string.home_settings),
            icon = ImageVector.vectorResource(R.drawable.rounded_settings_24),
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
            selected -> Color.White
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 150),
        label = "sidebar container"
    )
    val scale by animateFloatAsState(
        targetValue = if (focused) 1.06f else 1f,
        animationSpec = tween(durationMillis = 170),
        label = "sidebar scale"
    )
    val contentColor = if (focused || selected) Color.Black else Color.White.copy(alpha = 0.88f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
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

private fun currentClock(): String =
    java.time.LocalTime.now().format(
        java.time.format.DateTimeFormatter.ofPattern(
            "h:mm a",
            java.util.Locale.getDefault()
        )
    )

private fun NavBackStackEntry?.isOnRoute(route: Screen): Boolean =
    this?.destination?.hierarchy?.any { it.hasRoute(route::class) } == true
