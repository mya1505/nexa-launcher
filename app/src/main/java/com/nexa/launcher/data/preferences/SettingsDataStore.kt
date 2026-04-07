package com.nexa.launcher.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nexa.launcher.domain.model.LauncherSettings
import com.nexa.launcher.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "launcher_settings")

class SettingsDataStore(private val context: Context) {

    val settingsFlow: Flow<LauncherSettings> = context.settingsDataStore.data.map { pref ->
        LauncherSettings(
            gridColumns = pref[GRID_COLUMNS] ?: 4,
            gridRows = pref[GRID_ROWS] ?: 5,
            iconSizeDp = pref[ICON_SIZE_DP] ?: 56f,
            showLabels = pref[SHOW_LABELS] ?: true,
            enableSwipeUpDrawer = pref[ENABLE_SWIPE_UP] ?: true,
            enableDoubleTapLock = pref[ENABLE_DOUBLE_TAP_LOCK] ?: false,
            themeMode = pref[THEME_MODE]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() } ?: ThemeMode.SYSTEM,
            widgetId = pref[WIDGET_ID]
        )
    }

    suspend fun updateGrid(columns: Int, rows: Int) {
        context.settingsDataStore.edit { pref ->
            pref[GRID_COLUMNS] = columns
            pref[GRID_ROWS] = rows
        }
    }

    suspend fun updateIconSize(iconSizeDp: Float) {
        context.settingsDataStore.edit { pref ->
            pref[ICON_SIZE_DP] = iconSizeDp
        }
    }

    suspend fun updateShowLabels(enabled: Boolean) {
        context.settingsDataStore.edit { pref ->
            pref[SHOW_LABELS] = enabled
        }
    }

    suspend fun updateSwipeUp(enabled: Boolean) {
        context.settingsDataStore.edit { pref ->
            pref[ENABLE_SWIPE_UP] = enabled
        }
    }

    suspend fun updateDoubleTapLock(enabled: Boolean) {
        context.settingsDataStore.edit { pref ->
            pref[ENABLE_DOUBLE_TAP_LOCK] = enabled
        }
    }

    suspend fun updateThemeMode(themeMode: ThemeMode) {
        context.settingsDataStore.edit { pref ->
            pref[THEME_MODE] = themeMode.name
        }
    }

    suspend fun updateWidgetId(widgetId: Int?) {
        context.settingsDataStore.edit { pref ->
            if (widgetId == null) {
                pref.remove(WIDGET_ID)
            } else {
                pref[WIDGET_ID] = widgetId
            }
        }
    }

    companion object {
        private val GRID_COLUMNS = intPreferencesKey("grid_columns")
        private val GRID_ROWS = intPreferencesKey("grid_rows")
        private val ICON_SIZE_DP = floatPreferencesKey("icon_size_dp")
        private val SHOW_LABELS = booleanPreferencesKey("show_labels")
        private val ENABLE_SWIPE_UP = booleanPreferencesKey("enable_swipe_up")
        private val ENABLE_DOUBLE_TAP_LOCK = booleanPreferencesKey("enable_double_tap_lock")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val WIDGET_ID = intPreferencesKey("widget_id")
    }
}
