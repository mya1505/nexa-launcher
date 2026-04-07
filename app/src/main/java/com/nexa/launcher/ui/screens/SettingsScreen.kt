package com.nexa.launcher.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexa.launcher.domain.model.ThemeMode
import com.nexa.launcher.viewmodel.AppUiModel
import com.nexa.launcher.viewmodel.LauncherUiState

@Composable
fun SettingsScreen(
    state: LauncherUiState,
    onBack: () -> Unit,
    onGridChange: (Int, Int) -> Unit,
    onIconSizeChange: (Float) -> Unit,
    onShowLabelChange: (Boolean) -> Unit,
    onSwipeUpChange: (Boolean) -> Unit,
    onDoubleTapLockChange: (Boolean) -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    onExportBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    onUnhide: (AppUiModel) -> Unit
) {
    val gridPresets = listOf(4 to 4, 4 to 5, 5 to 5, 5 to 6)

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Settings", style = MaterialTheme.typography.headlineSmall)
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Grid Size", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        gridPresets.forEach { preset ->
                            AssistChip(
                                onClick = { onGridChange(preset.first, preset.second) },
                                label = { Text("${preset.first}x${preset.second}") }
                            )
                        }
                    }
                    Text(
                        text = "Current: ${state.settings.gridColumns}x${state.settings.gridRows}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Icon Size", style = MaterialTheme.typography.titleMedium)
                    Slider(
                        value = state.settings.iconSizeDp,
                        valueRange = 40f..80f,
                        onValueChange = onIconSizeChange
                    )
                    Text(text = "${state.settings.iconSizeDp.toInt()} dp")
                }
            }
        }

        item {
            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SettingSwitchRow(
                        title = "Show app labels",
                        checked = state.settings.showLabels,
                        onCheckedChange = onShowLabelChange
                    )
                    SettingSwitchRow(
                        title = "Swipe up opens app drawer",
                        checked = state.settings.enableSwipeUpDrawer,
                        onCheckedChange = onSwipeUpChange
                    )
                    SettingSwitchRow(
                        title = "Double tap locks screen",
                        checked = state.settings.enableDoubleTapLock,
                        onCheckedChange = onDoubleTapLockChange,
                        icon = Icons.Default.Lock
                    )
                }
            }
        }

        item {
            Card {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Theme", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ThemeMode.entries.forEach { mode ->
                            AssistChip(
                                onClick = { onThemeChange(mode) },
                                label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onExportBackup, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.SettingsBackupRestore, contentDescription = null)
                    Text(text = "Backup", modifier = Modifier.padding(start = 8.dp))
                }
                OutlinedButton(onClick = onRestoreBackup, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.SettingsBackupRestore, contentDescription = null)
                    Text(text = "Restore", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }

        if (state.hiddenApps.isNotEmpty()) {
            item {
                Text(text = "Hidden Apps", style = MaterialTheme.typography.titleMedium)
            }
            items(state.hiddenApps, key = { it.app.packageName + it.app.activityName }) { app ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = app.app.label, modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = { onUnhide(app) }) {
                        Icon(Icons.Default.VisibilityOff, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text(text = "Unhide", modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (icon != null) {
                Icon(icon, contentDescription = null)
            }
            Text(text = title)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
