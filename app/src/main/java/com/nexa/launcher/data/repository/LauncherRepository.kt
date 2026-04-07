package com.nexa.launcher.data.repository

import com.nexa.launcher.data.local.AppPreferenceEntity
import com.nexa.launcher.domain.model.AppEntry
import com.nexa.launcher.domain.model.LauncherSettings
import com.nexa.launcher.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface LauncherRepository {
    val settingsFlow: Flow<LauncherSettings>
    val appPreferencesFlow: Flow<Map<String, AppPreferenceEntity>>

    suspend fun loadLaunchableApps(): List<AppEntry>

    suspend fun setFavorite(packageName: String, favorite: Boolean)
    suspend fun setHidden(packageName: String, hidden: Boolean)
    suspend fun recordLaunch(packageName: String)

    suspend fun updateGrid(columns: Int, rows: Int)
    suspend fun updateIconSize(iconSizeDp: Float)
    suspend fun updateShowLabels(enabled: Boolean)
    suspend fun updateSwipeUp(enabled: Boolean)
    suspend fun updateDoubleTapLock(enabled: Boolean)
    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updateWidgetId(widgetId: Int?)

    suspend fun getAllPreferences(): List<AppPreferenceEntity>
    suspend fun replaceAllPreferences(preferences: List<AppPreferenceEntity>)
}
