package com.nexa.launcher.data.repository

import android.content.Context
import com.nexa.launcher.data.local.AppPreferenceEntity
import com.nexa.launcher.domain.model.BackupAppPreference
import com.nexa.launcher.domain.model.BackupPayload
import com.nexa.launcher.domain.model.BackupSettings
import com.nexa.launcher.domain.model.LauncherSettings
import com.nexa.launcher.domain.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

@Singleton
class BackupRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val launcherRepository: LauncherRepository
) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun exportToJson(): Result<String> = runCatching {
        withContext(Dispatchers.IO) {
            val settings = launcherRepository.settingsFlowSnapshot()
            val prefs = launcherRepository.getAllPreferences()
            val payload = BackupPayload(
                settings = settings.toBackupSettings(),
                appPreferences = prefs.map {
                    BackupAppPreference(
                        packageName = it.packageName,
                        isFavorite = it.isFavorite,
                        isHidden = it.isHidden,
                        launchCount = it.launchCount,
                        lastLaunchedAt = it.lastLaunchedAt
                    )
                }
            )
            val file = File(context.getExternalFilesDir(null), FILE_NAME)
            file.writeText(json.encodeToString(BackupPayload.serializer(), payload))
            file.absolutePath
        }
    }

    suspend fun restoreFromJson(): Result<String> = runCatching {
        withContext(Dispatchers.IO) {
            val file = File(context.getExternalFilesDir(null), FILE_NAME)
            require(file.exists()) { "Backup file not found" }
            val payload = json.decodeFromString(BackupPayload.serializer(), file.readText())

            launcherRepository.updateGrid(payload.settings.gridColumns, payload.settings.gridRows)
            launcherRepository.updateIconSize(payload.settings.iconSizeDp)
            launcherRepository.updateShowLabels(payload.settings.showLabels)
            launcherRepository.updateSwipeUp(payload.settings.enableSwipeUpDrawer)
            launcherRepository.updateDoubleTapLock(payload.settings.enableDoubleTapLock)
            launcherRepository.updateThemeMode(
                runCatching { ThemeMode.valueOf(payload.settings.themeMode) }.getOrDefault(ThemeMode.SYSTEM)
            )
            launcherRepository.updateWidgetId(payload.settings.widgetId)

            launcherRepository.replaceAllPreferences(
                payload.appPreferences.map {
                    AppPreferenceEntity(
                        packageName = it.packageName,
                        isFavorite = it.isFavorite,
                        isHidden = it.isHidden,
                        launchCount = it.launchCount,
                        lastLaunchedAt = it.lastLaunchedAt
                    )
                }
            )
            file.absolutePath
        }
    }

    private suspend fun LauncherRepository.settingsFlowSnapshot(): LauncherSettings {
        return settingsFlow.first()
    }

    private fun LauncherSettings.toBackupSettings(): BackupSettings = BackupSettings(
        gridColumns = gridColumns,
        gridRows = gridRows,
        iconSizeDp = iconSizeDp,
        showLabels = showLabels,
        enableSwipeUpDrawer = enableSwipeUpDrawer,
        enableDoubleTapLock = enableDoubleTapLock,
        themeMode = themeMode.name,
        widgetId = widgetId
    )

    companion object {
        private const val FILE_NAME = "launcher-config.json"
    }
}
