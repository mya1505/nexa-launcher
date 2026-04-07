package com.nexa.launcher.ui.screens

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexa.launcher.ui.components.AppDrawerSheet
import com.nexa.launcher.ui.components.AppIconItem
import com.nexa.launcher.ui.components.WidgetHostCard
import com.nexa.launcher.viewmodel.AppUiModel
import com.nexa.launcher.viewmodel.LauncherUiState

@Composable
fun HomeScreen(
    state: LauncherUiState,
    appWidgetHost: AppWidgetHost,
    appWidgetManager: AppWidgetManager,
    onOpenSettings: () -> Unit,
    onLaunchApp: (AppUiModel) -> Unit,
    onToggleFavorite: (AppUiModel) -> Unit,
    onToggleHidden: (AppUiModel) -> Unit,
    onOpenDrawer: () -> Unit,
    onCloseDrawer: () -> Unit,
    onOpenQuickSearch: () -> Unit,
    onCloseQuickSearch: () -> Unit,
    onSetQuery: (String) -> Unit,
    onToggleEditMode: () -> Unit,
    onAddWidget: () -> Unit,
    onRemoveWidget: (Int) -> Unit,
    onDoubleTapLock: () -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .pointerInput(state.settings.enableSwipeUpDrawer) {
                var dragAmount = 0f
                detectVerticalDragGestures(
                    onVerticalDrag = { _, drag ->
                        dragAmount += drag
                    },
                    onDragEnd = {
                        if (dragAmount < -120f && state.settings.enableSwipeUpDrawer) {
                            onOpenDrawer()
                        }
                        if (dragAmount > 120f) {
                            onOpenQuickSearch()
                        }
                        dragAmount = 0f
                    }
                )
            }
            .pointerInput(state.editMode) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTapLock() },
                    onLongPress = { onToggleEditMode() }
                )
            }
    ) {
        val iconSize = state.settings.iconSizeDp.dp

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Nexa Launcher",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Swipe up for drawer • Swipe down for quick search",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }

            item {
                AnimatedVisibility(visible = state.editMode) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    ) {
                        Text(
                            text = "Edit mode active: tap star to favorite, eye to hide, and add/remove widget",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(onClick = onOpenDrawer, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(text = "App Drawer", modifier = Modifier.padding(start = 8.dp))
                    }
                    FilledTonalButton(onClick = onOpenQuickSearch, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(text = "Quick Search", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            state.settings.widgetId?.let { widgetId ->
                item {
                    WidgetHostCard(
                        widgetId = widgetId,
                        appWidgetHost = appWidgetHost,
                        appWidgetManager = appWidgetManager,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                if (state.editMode) {
                    item {
                        FilledTonalButton(
                            onClick = { onRemoveWidget(widgetId) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null)
                            Text(text = "Remove widget", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }

            if (state.editMode && state.settings.widgetId == null) {
                item {
                    FilledTonalButton(onClick = onAddWidget, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Widgets, contentDescription = null)
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(start = 4.dp))
                        Text(text = "Add widget", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            if (state.recentApps.isNotEmpty()) {
                item {
                    Text(text = "Recent Apps", style = MaterialTheme.typography.titleMedium)
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.recentApps, key = { it.app.packageName + it.app.activityName }) { app ->
                            AppIconItem(
                                app = app,
                                iconSize = iconSize,
                                showLabel = state.settings.showLabels,
                                editMode = false,
                                onClick = { onLaunchApp(app) },
                                onLongClick = onToggleEditMode,
                                onToggleFavorite = { onToggleFavorite(app) },
                                onToggleHidden = { onToggleHidden(app) }
                            )
                        }
                    }
                }
            }

            item {
                Text(text = "Home", style = MaterialTheme.typography.titleMedium)
            }

            val rows = state.homeApps.chunked(state.settings.gridColumns)
            items(rows.size) { rowIndex ->
                val rowApps = rows[rowIndex]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(state.settings.gridColumns) { colIndex ->
                        val app = rowApps.getOrNull(colIndex)
                        if (app == null) {
                            Box(modifier = Modifier.size(iconSize + 24.dp))
                        } else {
                            AppIconItem(
                                app = app,
                                iconSize = iconSize,
                                showLabel = state.settings.showLabels,
                                editMode = state.editMode,
                                onClick = { onLaunchApp(app) },
                                onLongClick = onToggleEditMode,
                                onToggleFavorite = { onToggleFavorite(app) },
                                onToggleHidden = { onToggleHidden(app) }
                            )
                        }
                    }
                }
            }
            item { Box(modifier = Modifier.height(56.dp)) }
        }

        if (state.quickSearchOpen) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 22.dp),
                shape = MaterialTheme.shapes.large,
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Quick Search", style = MaterialTheme.typography.titleMedium)
                        IconButton(onClick = onCloseQuickSearch) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    OutlinedTextField(
                        value = state.drawerQuery,
                        onValueChange = onSetQuery,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Search app") }
                    )
                    state.drawerApps.take(8).forEach { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = app.app.label,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            FilledTonalButton(onClick = { onLaunchApp(app) }) {
                                Text("Open")
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.drawerOpen) {
        AppDrawerSheet(
            apps = state.drawerApps,
            query = state.drawerQuery,
            onQueryChange = onSetQuery,
            onDismiss = onCloseDrawer,
            onLaunchApp = onLaunchApp,
            onToggleFavorite = onToggleFavorite,
            onHide = onToggleHidden
        )
    }
}
