package com.nexa.launcher.ui.components

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WidgetHostCard(
    widgetId: Int,
    appWidgetHost: AppWidgetHost,
    appWidgetManager: AppWidgetManager,
    modifier: Modifier = Modifier
) {
    val widgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)

    Surface(modifier = modifier, tonalElevation = 2.dp, shape = MaterialTheme.shapes.large) {
        if (widgetInfo == null) {
            Text(
                text = "Widget unavailable",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )
        } else {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                factory = { context ->
                    appWidgetHost.createView(context, widgetId, widgetInfo).apply {
                        setAppWidget(widgetId, widgetInfo)
                    }
                },
                update = { hostView ->
                    hostView.setAppWidget(widgetId, widgetInfo)
                }
            )
        }
    }
}
