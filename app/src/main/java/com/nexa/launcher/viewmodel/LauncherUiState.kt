package com.nexa.launcher.viewmodel

import com.nexa.launcher.domain.model.AppEntry
import com.nexa.launcher.domain.model.LauncherSettings

enum class LauncherScreen {
    HOME,
    SETTINGS
}

data class AppUiModel(
    val app: AppEntry,
    val isFavorite: Boolean,
    val isHidden: Boolean,
    val launchCount: Int,
    val lastLaunchedAt: Long
)

data class LauncherUiState(
    val isLoading: Boolean = true,
    val homeApps: List<AppUiModel> = emptyList(),
    val drawerApps: List<AppUiModel> = emptyList(),
    val recentApps: List<AppUiModel> = emptyList(),
    val hiddenApps: List<AppUiModel> = emptyList(),
    val drawerQuery: String = "",
    val drawerOpen: Boolean = false,
    val quickSearchOpen: Boolean = false,
    val editMode: Boolean = false,
    val currentScreen: LauncherScreen = LauncherScreen.HOME,
    val settings: LauncherSettings = LauncherSettings()
)
