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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nexa.launcher.ui.components.AppDrawerSheet
import com.nexa.launcher.ui.components.AppIconItem
import com.nexa.launcher.ui.components.WidgetHostCard
import com.nexa.launcher.viewmodel.AppUiModel
import com.nexa.launcher.viewmodel.LauncherUiState
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay
import kotlin.math.max

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
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.background
        )
    )
    val iconSize = state.settings.iconSizeDp.dp
    val clockText by rememberClock("HH:mm")
    val dateText by rememberClock("EEEE, d MMMM")

    val dockApps = state.homeApps.takeLast(minOf(5, state.homeApps.size))
    val gridApps = if (state.homeApps.size > dockApps.size) {
        state.homeApps.dropLast(dockApps.size)
    } else {
        state.homeApps
    }

    val rowSlots = max(1, state.settings.gridRows - 1)
    val pageCapacity = max(1, state.settings.gridColumns * rowSlots)
    val pagedApps = if (gridApps.isEmpty()) {
        listOf(emptyList())
    } else {
        gridApps.chunked(pageCapacity)
    }
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pagedApps.size })

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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = clockText,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.76f)
                        )
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }

            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Text(
                                text = "Finder",
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        FilledTonalButton(onClick = onOpenQuickSearch) {
                            Text("Open")
                        }
                    }
                }
            }

            item {
                AnimatedVisibility(visible = state.editMode) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Edit mode: long press app to manage favorite/hide, double tap to lock screen",
                            modifier = Modifier.padding(14.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                        Text(text = "Add widget", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }

            if (state.recentApps.isNotEmpty()) {
                item {
                    Text(
                        text = "Suggested apps",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
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
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height((iconSize + 40.dp) * rowSlots),
                    pageSpacing = 12.dp
                ) { pageIndex ->
                    val pageRows = pagedApps[pageIndex].chunked(state.settings.gridColumns)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(rowSlots) { rowIndex ->
                            val rowApps = pageRows.getOrNull(rowIndex).orEmpty()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                repeat(state.settings.gridColumns) { colIndex ->
                                    val app = rowApps.getOrNull(colIndex)
                                    if (app == null) {
                                        Box(modifier = Modifier.size(iconSize + 20.dp))
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
                    }
                }
            }

            if (pagedApps.size > 1) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(pagedApps.size) { index ->
                            val isActive = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .size(if (isActive) 10.dp else 8.dp)
                                    .background(
                                        color = if (isActive) {
                                            MaterialTheme.colorScheme.onBackground
                                        } else {
                                            MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                                        },
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }

            item { Box(modifier = Modifier.height(24.dp)) }
        }

        if (dockApps.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 18.dp),
                shape = RoundedCornerShape(34.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                tonalElevation = 6.dp,
                shadowElevation = 18.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    dockApps.forEach { app ->
                        AppIconItem(
                            app = app,
                            iconSize = iconSize,
                            showLabel = false,
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

        if (state.quickSearchOpen) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 18.dp),
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
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
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        label = { Text("Search app") }
                    )
                    state.drawerApps.take(8).forEach { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
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
            iconSize = (iconSize * 0.88f),
            onQueryChange = onSetQuery,
            onDismiss = onCloseDrawer,
            onLaunchApp = onLaunchApp,
            onToggleFavorite = onToggleFavorite,
            onHide = onToggleHidden
        )
    }
}

@Composable
private fun rememberClock(pattern: String): androidx.compose.runtime.State<String> {
    return produceState(initialValue = formattedTime(pattern)) {
        while (true) {
            value = formattedTime(pattern)
            delay(1000L)
        }
    }
}

private fun formattedTime(pattern: String): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))
}
