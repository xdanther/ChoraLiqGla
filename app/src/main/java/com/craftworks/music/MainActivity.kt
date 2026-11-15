package com.craftworks.music

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.media3.common.util.NotificationUtil.IMPORTANCE_LOW
import androidx.media3.common.util.NotificationUtil.createNotificationChannel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.compose.rememberNavController
import com.craftworks.music.data.model.Screen
import com.craftworks.music.managers.MediaProviderManager
import com.craftworks.music.managers.SearchRequestManager
import com.craftworks.music.managers.settings.AppTheme
import com.craftworks.music.managers.settings.AppearanceSettingsManager
import com.craftworks.music.player.rememberManagedMediaController
import com.craftworks.music.ui.elements.dialogs.tv.OnboardingDialog
import com.craftworks.music.ui.theme.MusicPlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var navController: androidx.navigation.NavHostController

    private val searchRequests = MutableStateFlow<String?>(null)
    private val openNowPlayingRequests = MutableStateFlow(false)

    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleIncomingIntent(intent)
        enableEdgeToEdge()

        setContent {
            val selectedThemeName by AppearanceSettingsManager(this).appTheme.collectAsState(
                AppTheme.SYSTEM.name
            )
            val darkTheme = when (selectedThemeName) {
                AppTheme.DARK.name -> true
                AppTheme.LIGHT.name -> false
                else -> isSystemInDarkTheme()
            }

            MusicPlayerTheme(darkTheme) {
                navController = rememberNavController()

                val pendingSearch by searchRequests.collectAsState()
                val pendingOpenNowPlaying by openNowPlayingRequests.collectAsState()

                // A TV launcher search or the system Now Playing card arrives while
                // this activity is already alive; route it once the graph exists.
                LaunchedEffect(pendingSearch) {
                    pendingSearch?.let { query ->
                        SearchRequestManager.request(query)
                        navController.navigate(Screen.Search) { launchSingleTop = true }
                        searchRequests.value = null
                    }
                }

                LaunchedEffect(pendingOpenNowPlaying) {
                    if (pendingOpenNowPlaying) {
                        navController.navigate(Screen.NowPlaying) {
                            launchSingleTop = true
                        }
                        openNowPlayingRequests.value = false
                    }
                }

                val mediaController by rememberManagedMediaController()

                window.decorView.setBackgroundColor(
                    androidx.tv.material3.MaterialTheme.colorScheme.background.toArgb()
                )

                SetupNavGraph(navController, mediaController)

                var showNoProvidersDialog by rememberSaveable { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    showNoProvidersDialog = MediaProviderManager.allProviders.value.isEmpty()
                }

                if (showNoProvidersDialog) {
                    OnboardingDialog { showNoProvidersDialog = it }
                }
            }
        }

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.entries.forEach { permission ->
                Log.d(
                    "PERMISSIONS",
                    "Is '${permission.key}' permission granted? ${permission.value}"
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN) {
            requestPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.READ_MEDIA_AUDIO,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                    android.Manifest.permission.ACCESS_LOCAL_NETWORK
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.READ_MEDIA_AUDIO,
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            )
        } else {
            requestPermissionLauncher.launch(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            )
        }

        createNotificationChannel(
            this,
            "download_channel",
            R.string.notification_download_name,
            R.string.notification_download_desc,
            IMPORTANCE_LOW
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        intent ?: return

        if (intent.action == Intent.ACTION_SEARCH) {
            intent.getStringExtra(SearchManager.QUERY)
                ?.takeIf { it.isNotBlank() }
                ?.let { searchRequests.value = it }
        }

        if (intent.getBooleanExtra(EXTRA_OPEN_NOW_PLAYING, false)) {
            openNowPlayingRequests.value = true
        }
    }

    companion object {
        const val EXTRA_OPEN_NOW_PLAYING = "openNowPlaying"
    }
}
