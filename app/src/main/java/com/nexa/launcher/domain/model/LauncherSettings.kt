package com.nexa.launcher.domain.model

data class LauncherSettings(
    val gridColumns: Int = 4,
    val gridRows: Int = 5,
    val iconSizeDp: Float = 56f,
    val showLabels: Boolean = true,
    val enableSwipeUpDrawer: Boolean = true,
    val enableDoubleTapLock: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val widgetId: Int? = null
)
