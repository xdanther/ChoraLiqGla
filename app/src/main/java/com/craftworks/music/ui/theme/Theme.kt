package com.craftworks.music.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.core.view.WindowCompat
import androidx.tv.material3.MaterialTheme
import com.craftworks.music.managers.settings.AppearanceSettingsManager
import com.craftworks.music.managers.settings.TvTextSize

/**
 * Music-on-TV palette: a black canvas lit by artwork, white type, and a single red
 * accent reserved for playback state (progress, stars) the way Apple Music does it.
 */
private val TvDarkColorScheme = androidx.tv.material3.darkColorScheme(
    primary = Color(0xFFFA233B),
    onPrimary = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF0B0B0B),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1B1B1B),
    onSurfaceVariant = Color(0xFFB3B3B3),
)

@Composable
fun MusicPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    val colorScheme = when {
        darkTheme -> TvDarkColorScheme
        else -> androidx.tv.material3.lightColorScheme()
    }

    // Dynamic Type. Android TV exposes no system font size, so the app carries its
    // own scale; only sp is scaled, so covers, spacing and focus rings stay put.
    val settings = remember(context) { AppearanceSettingsManager(context) }
    val textSize by settings.tvTextSizeFlow.collectAsState(TvTextSize.DEFAULT)
    val density = LocalDensity.current

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TvTypography
    ) {
        CompositionLocalProvider(
            LocalDensity provides Density(
                density = density.density,
                fontScale = density.fontScale * textSize.scale
            )
        ) {
            content()
        }
    }
}
