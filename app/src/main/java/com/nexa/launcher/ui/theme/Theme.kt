package com.nexa.launcher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.nexa.launcher.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = NexaBlue,
    secondary = NexaPurple,
    background = NexaSurfaceLight,
    surface = androidx.compose.ui.graphics.Color.White,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = NexaTextDark,
    onSurface = NexaTextDark
)

private val DarkColors = darkColorScheme(
    primary = NexaBlue,
    secondary = NexaPurple,
    background = NexaSurfaceDark,
    surface = androidx.compose.ui.graphics.Color(0xFF172036),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = NexaTextLight,
    onSurface = NexaTextLight
)

@Composable
fun NexaLauncherTheme(
    themeMode: ThemeMode,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    MaterialTheme(
        colorScheme = if (isDark) DarkColors else LightColors,
        typography = NexaTypography,
        content = content
    )
}
