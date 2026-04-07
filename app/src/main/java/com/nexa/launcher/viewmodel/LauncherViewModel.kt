package com.nexa.launcher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexa.launcher.data.local.AppPreferenceEntity
import com.nexa.launcher.data.repository.BackupRepository
import com.nexa.launcher.data.repository.LauncherRepository
import com.nexa.launcher.domain.model.AppEntry
import com.nexa.launcher.domain.model.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val repository: LauncherRepository,
    private val backupRepository: BackupRepository
) : ViewModel() {

    private val installedApps = MutableStateFlow<List<AppEntry>>(emptyList())
    private val drawerQuery = MutableStateFlow("")
    private val drawerOpen = MutableStateFlow(false)
    private val quickSearchOpen = MutableStateFlow(false)
    private val editMode = MutableStateFlow(false)
    private val currentScreen = MutableStateFlow(LauncherScreen.HOME)
    private val loading = MutableStateFlow(true)

    private val _messages = MutableSharedFlow<String>()
    val messages = _messages.asSharedFlow()

    private val drawerFlags = combine(
        drawerQuery,
        drawerOpen,
        quickSearchOpen
    ) { query, drawer, quickSearch ->
        Triple(query, drawer, quickSearch)
    }

    private val uiFlags = combine(
        drawerFlags,
        editMode,
        currentScreen,
        loading
    ) { drawerState, isEditMode, screen, isLoading ->
        UiFlags(
            query = drawerState.first,
            drawerOpen = drawerState.second,
            quickSearchOpen = drawerState.third,
            editMode = isEditMode,
            screen = screen,
            isLoading = isLoading
        )
    }

    val uiState: StateFlow<LauncherUiState> = combine(
        installedApps,
        repository.appPreferencesFlow,
        repository.settingsFlow,
        uiFlags
    ) { apps, preferences, settings, flags ->
        val decoratedApps = decorate(apps, preferences)

        val visibleApps = decoratedApps.filterNot { it.isHidden }
        val sortedByUsage = visibleApps.sortedWith(
            compareByDescending<AppUiModel> { it.isFavorite }
                .thenByDescending { it.launchCount }
                .thenBy { it.app.label.lowercase() }
        )
        val maxHomeItems = settings.gridColumns * settings.gridRows
        val homeApps = sortedByUsage.take(maxHomeItems)

        val baseDrawerApps = visibleApps.sortedBy { it.app.label.lowercase() }
        val filteredDrawerApps = if (flags.query.isBlank()) {
            baseDrawerApps
        } else {
            baseDrawerApps.filter { it.app.label.contains(flags.query, ignoreCase = true) }
        }
        val drawerApps = filteredDrawerApps.sortedWith(
            compareByDescending<AppUiModel> { it.isFavorite }
                .thenBy { it.app.label.lowercase() }
        )

        val recentApps = visibleApps.filter { it.launchCount > 0 }
            .sortedByDescending { it.lastLaunchedAt }
            .take(8)

        LauncherUiState(
            isLoading = flags.isLoading,
            homeApps = homeApps,
            drawerApps = drawerApps,
            recentApps = recentApps,
            hiddenApps = decoratedApps.filter { it.isHidden },
            drawerQuery = flags.query,
            drawerOpen = flags.drawerOpen,
            quickSearchOpen = flags.quickSearchOpen,
            editMode = flags.editMode,
            currentScreen = flags.screen,
            settings = settings
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), LauncherUiState())

    init {
        reloadApps()
    }

    fun reloadApps() {
        viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            installedApps.value = repository.loadLaunchableApps()
            loading.value = false
        }
    }

    fun openSettings() {
        currentScreen.value = LauncherScreen.SETTINGS
    }

    fun openHome() {
        currentScreen.value = LauncherScreen.HOME
    }

    fun toggleDrawer(open: Boolean) {
        drawerOpen.value = open
        if (!open) {
            drawerQuery.value = ""
        }
    }

    fun toggleQuickSearch(open: Boolean) {
        quickSearchOpen.value = open
        if (!open) {
            drawerQuery.value = ""
        }
    }

    fun setDrawerQuery(query: String) {
        drawerQuery.value = query
    }

    fun setEditMode(enabled: Boolean) {
        editMode.value = enabled
    }

    fun toggleEditMode() {
        editMode.update { !it }
    }

    fun setFavorite(packageName: String, favorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setFavorite(packageName, favorite)
        }
    }

    fun setHidden(packageName: String, hidden: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setHidden(packageName, hidden)
        }
    }

    fun recordLaunch(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.recordLaunch(packageName)
        }
    }

    fun updateGrid(columns: Int, rows: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateGrid(columns, rows)
        }
    }

    fun updateIconSize(iconSizeDp: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIconSize(iconSizeDp)
        }
    }

    fun updateShowLabels(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateShowLabels(enabled)
        }
    }

    fun updateSwipeUp(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSwipeUp(enabled)
        }
    }

    fun updateDoubleTapLock(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateDoubleTapLock(enabled)
        }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateThemeMode(themeMode)
        }
    }

    fun updateWidgetId(widgetId: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateWidgetId(widgetId)
        }
    }

    fun exportBackup() {
        viewModelScope.launch(Dispatchers.IO) {
            val message = backupRepository.exportToJson()
                .fold(
                    onSuccess = { path -> "Backup saved: $path" },
                    onFailure = { error -> "Backup failed: ${error.message}" }
                )
            _messages.emit(message)
        }
    }

    fun restoreBackup() {
        viewModelScope.launch(Dispatchers.IO) {
            val message = backupRepository.restoreFromJson()
                .fold(
                    onSuccess = { path -> "Restore success from: $path" },
                    onFailure = { error -> "Restore failed: ${error.message}" }
                )
            _messages.emit(message)
            reloadApps()
        }
    }

    private fun decorate(
        apps: List<AppEntry>,
        preferences: Map<String, AppPreferenceEntity>
    ): List<AppUiModel> {
        return apps.map { app ->
            val pref = preferences[app.packageName]
            AppUiModel(
                app = app,
                isFavorite = pref?.isFavorite ?: false,
                isHidden = pref?.isHidden ?: false,
                launchCount = pref?.launchCount ?: 0,
                lastLaunchedAt = pref?.lastLaunchedAt ?: 0L
            )
        }
    }

    private data class UiFlags(
        val query: String,
        val drawerOpen: Boolean,
        val quickSearchOpen: Boolean,
        val editMode: Boolean,
        val screen: LauncherScreen,
        val isLoading: Boolean
    )
}
