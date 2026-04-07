package com.nexa.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nexa.launcher.viewmodel.AppUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerSheet(
    apps: List<AppUiModel>,
    query: String,
    iconSize: Dp,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onLaunchApp: (AppUiModel) -> Unit,
    onToggleFavorite: (AppUiModel) -> Unit,
    onHide: (AppUiModel) -> Unit
) {
    val suggestedApps = apps.take(8)
    val letters = apps.asSequence()
        .map { it.app.label.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "#" }
        .distinct()
        .toList()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 34.dp, topEnd = 34.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Apps screen",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                shape = RoundedCornerShape(18.dp),
                label = { Text("Search apps") }
            )

            if (letters.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(letters) { letter ->
                        AssistChip(
                            onClick = { onQueryChange(letter) },
                            label = { Text(letter) }
                        )
                    }
                }
            }

            if (suggestedApps.isNotEmpty() && query.isBlank()) {
                Text(
                    text = "Suggested",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(4) { index ->
                        val app = suggestedApps.getOrNull(index)
                        if (app == null) {
                            Box(modifier = Modifier.size(iconSize + 20.dp))
                        } else {
                            AppIconItem(
                                app = app,
                                iconSize = iconSize,
                                showLabel = true,
                                editMode = false,
                                onClick = { onLaunchApp(app) },
                                onLongClick = { onToggleFavorite(app) },
                                onToggleFavorite = { onToggleFavorite(app) },
                                onToggleHidden = { onHide(app) }
                            )
                        }
                    }
                }
            }

            if (apps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "No apps found",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text(
                    text = "All apps",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(apps, key = { "${it.app.packageName}:${it.app.activityName}" }) { app ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLaunchApp(app) },
                            contentAlignment = Alignment.TopCenter
                        ) {
                            AppIconItem(
                                app = app,
                                iconSize = iconSize,
                                showLabel = true,
                                editMode = false,
                                onClick = { onLaunchApp(app) },
                                onLongClick = { onToggleFavorite(app) },
                                onToggleFavorite = { onToggleFavorite(app) },
                                onToggleHidden = { onHide(app) }
                            )
                        }
                    }
                }
            }
        }
    }
}
