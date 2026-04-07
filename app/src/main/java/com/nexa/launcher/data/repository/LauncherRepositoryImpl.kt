package com.nexa.launcher.data.repository

import com.nexa.launcher.data.local.AppPreferenceDao
import com.nexa.launcher.data.local.AppPreferenceEntity
import com.nexa.launcher.data.preferences.SettingsDataStore
import com.nexa.launcher.domain.model.AppEntry
import com.nexa.launcher.domain.model.LauncherSettings
import com.nexa.launcher.domain.model.ThemeMode
import com.nexa.launcher.utils.AppQueryHelper
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class LauncherRepositoryImpl @Inject constructor(
    private val appPreferenceDao: AppPreferenceDao,
    private val settingsDataStore: SettingsDataStore,
    private val appQueryHelper: AppQueryHelper
) : LauncherRepository {

    override val settingsFlow: Flow<LauncherSettings> = settingsDataStore.settingsFlow

    override val appPreferencesFlow: Flow<Map<String, AppPreferenceEntity>> =
        appPreferenceDao.observeAll().map { list -> list.associateBy { it.packageName } }

    override suspend fun loadLaunchableApps(): List<AppEntry> = appQueryHelper.queryLaunchableApps()

    override suspend fun setFavorite(packageName: String, favorite: Boolean) {
        val existing = resolvePreference(packageName)
        appPreferenceDao.upsert(existing.copy(isFavorite = favorite))
    }

    override suspend fun setHidden(packageName: String, hidden: Boolean) {
        val existing = resolvePreference(packageName)
        appPreferenceDao.upsert(existing.copy(isHidden = hidden))
    }

    override suspend fun recordLaunch(packageName: String) {
        val existing = resolvePreference(packageName)
        appPreferenceDao.upsert(
            existing.copy(
                launchCount = existing.launchCount + 1,
                lastLaunchedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun updateGrid(columns: Int, rows: Int) {
        settingsDataStore.updateGrid(columns, rows)
    }

    override suspend fun updateIconSize(iconSizeDp: Float) {
        settingsDataStore.updateIconSize(iconSizeDp)
    }

    override suspend fun updateShowLabels(enabled: Boolean) {
        settingsDataStore.updateShowLabels(enabled)
    }

    override suspend fun updateSwipeUp(enabled: Boolean) {
        settingsDataStore.updateSwipeUp(enabled)
    }

    override suspend fun updateDoubleTapLock(enabled: Boolean) {
        settingsDataStore.updateDoubleTapLock(enabled)
    }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        settingsDataStore.updateThemeMode(themeMode)
    }

    override suspend fun updateWidgetId(widgetId: Int?) {
        settingsDataStore.updateWidgetId(widgetId)
    }

    override suspend fun getAllPreferences(): List<AppPreferenceEntity> = appPreferenceDao.getAll()

    override suspend fun replaceAllPreferences(preferences: List<AppPreferenceEntity>) {
        appPreferenceDao.clearAll()
        if (preferences.isNotEmpty()) {
            appPreferenceDao.upsertAll(preferences)
        }
    }

    private suspend fun resolvePreference(packageName: String): AppPreferenceEntity {
        return appPreferenceDao.getAll().firstOrNull { it.packageName == packageName }
            ?: AppPreferenceEntity(packageName = packageName)
    }
}
