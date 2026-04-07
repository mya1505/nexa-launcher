package com.nexa.launcher.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexa.launcher.viewmodel.AppUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerSheet(
    apps: List<AppUiModel>,
    query: String,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onLaunchApp: (AppUiModel) -> Unit,
    onToggleFavorite: (AppUiModel) -> Unit,
    onHide: (AppUiModel) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Search apps") }
            )

            if (apps.isEmpty()) {
                Text(
                    text = "No apps found",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(apps, key = { "${it.app.packageName}:${it.app.activityName}" }) { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLaunchApp(app) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = app.app.label,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onToggleFavorite(app) }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = if (app.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "favorite"
                            )
                        }
                        IconButton(onClick = { onHide(app) }, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "hide"
                            )
                        }
                    }
                }
            }
        }
    }
}
