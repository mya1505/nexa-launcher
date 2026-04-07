package com.nexa.launcher.main

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nexa.launcher.ui.screens.HomeScreen
import com.nexa.launcher.ui.screens.SettingsScreen
import com.nexa.launcher.ui.theme.NexaLauncherTheme
import com.nexa.launcher.utils.LockScreenHelper
import com.nexa.launcher.viewmodel.LauncherScreen
import com.nexa.launcher.viewmodel.LauncherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: LauncherViewModel by viewModels()

    private lateinit var appWidgetHost: AppWidgetHost
    private lateinit var appWidgetManager: AppWidgetManager

    private var pendingWidgetId: Int? = null

    private val configureWidgetLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val widgetId = pendingWidgetId ?: return@registerForActivityResult
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.updateWidgetId(widgetId)
            } else {
                appWidgetHost.deleteAppWidgetId(widgetId)
            }
            pendingWidgetId = null
        }

    private val pickWidgetLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val widgetId = result.data?.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

            if (result.resultCode != Activity.RESULT_OK || widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    appWidgetHost.deleteAppWidgetId(widgetId)
                }
                return@registerForActivityResult
            }

            val info = appWidgetManager.getAppWidgetInfo(widgetId)
            if (info?.configure != null) {
                pendingWidgetId = widgetId
                val configureIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                    component = info.configure
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                }
                configureWidgetLauncher.launch(configureIntent)
            } else {
                viewModel.updateWidgetId(widgetId)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, APP_WIDGET_HOST_ID)

        setContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                viewModel.messages.collect { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }

            NexaLauncherTheme(themeMode = state.settings.themeMode) {
                when (state.currentScreen) {
                    LauncherScreen.HOME -> {
                        HomeScreen(
                            state = state,
                            appWidgetHost = appWidgetHost,
                            appWidgetManager = appWidgetManager,
                            onOpenSettings = viewModel::openSettings,
                            onLaunchApp = { app ->
                                runCatching {
                                    val intent = Intent().apply {
                                        component = ComponentName(app.app.packageName, app.app.activityName)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    startActivity(intent)
                                    viewModel.recordLaunch(app.app.packageName)
                                    viewModel.toggleDrawer(false)
                                    viewModel.toggleQuickSearch(false)
                                }.onFailure {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Unable to launch ${app.app.label}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onToggleFavorite = { app ->
                                viewModel.setFavorite(app.app.packageName, !app.isFavorite)
                            },
                            onToggleHidden = { app ->
                                viewModel.setHidden(app.app.packageName, !app.isHidden)
                            },
                            onOpenDrawer = { viewModel.toggleDrawer(true) },
                            onCloseDrawer = { viewModel.toggleDrawer(false) },
                            onOpenQuickSearch = { viewModel.toggleQuickSearch(true) },
                            onCloseQuickSearch = { viewModel.toggleQuickSearch(false) },
                            onSetQuery = viewModel::setDrawerQuery,
                            onToggleEditMode = viewModel::toggleEditMode,
                            onAddWidget = ::pickWidget,
                            onRemoveWidget = { widgetId ->
                                appWidgetHost.deleteAppWidgetId(widgetId)
                                viewModel.updateWidgetId(null)
                            },
                            onDoubleTapLock = {
                                if (state.settings.enableDoubleTapLock) {
                                    val locked = LockScreenHelper.lockNow(this@MainActivity)
                                    if (!locked) {
                                        startActivity(LockScreenHelper.adminIntent(this@MainActivity))
                                    }
                                }
                            }
                        )
                    }

                    LauncherScreen.SETTINGS -> {
                        SettingsScreen(
                            state = state,
                            onBack = viewModel::openHome,
                            onGridChange = viewModel::updateGrid,
                            onIconSizeChange = viewModel::updateIconSize,
                            onShowLabelChange = viewModel::updateShowLabels,
                            onSwipeUpChange = viewModel::updateSwipeUp,
                            onDoubleTapLockChange = viewModel::updateDoubleTapLock,
                            onThemeChange = viewModel::updateThemeMode,
                            onExportBackup = viewModel::exportBackup,
                            onRestoreBackup = viewModel::restoreBackup,
                            onUnhide = { app -> viewModel.setHidden(app.app.packageName, false) }
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        appWidgetHost.startListening()
    }

    override fun onStop() {
        super.onStop()
        appWidgetHost.stopListening()
    }

    private fun pickWidget() {
        val widgetId = appWidgetHost.allocateAppWidgetId()
        val pickIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        pickWidgetLauncher.launch(pickIntent)
    }

    companion object {
        private const val APP_WIDGET_HOST_ID = 0x4E5841
    }
}
