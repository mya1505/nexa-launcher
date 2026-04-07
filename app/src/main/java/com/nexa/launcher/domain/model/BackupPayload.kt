package com.nexa.launcher.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BackupPayload(
    val settings: BackupSettings,
    val appPreferences: List<BackupAppPreference>
)

@Serializable
data class BackupSettings(
    val gridColumns: Int,
    val gridRows: Int,
    val iconSizeDp: Float,
    val showLabels: Boolean,
    val enableSwipeUpDrawer: Boolean,
    val enableDoubleTapLock: Boolean,
    val themeMode: String,
    val widgetId: Int?
)

@Serializable
data class BackupAppPreference(
    val packageName: String,
    val isFavorite: Boolean,
    val isHidden: Boolean,
    val launchCount: Int,
    val lastLaunchedAt: Long
)
