package com.nexa.launcher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.nexa.launcher.domain.model.ThemeMode

private val LightColors = lightColorScheme(
    primary = NexaBlue,
    secondary = NexaSky,
    background = NexaSurfaceLight,
    surface = NexaCardLight,
    surfaceVariant = NexaBlueSoft,
    onPrimary = Color.White,
    onBackground = NexaTextDark,
    onSurface = NexaTextDark
)

private val DarkColors = darkColorScheme(
    primary = NexaBlue,
    secondary = NexaSky,
    background = NexaSurfaceDark,
    surface = NexaCardDark,
    surfaceVariant = Color(0xFF22324D),
    onPrimary = Color.White,
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
