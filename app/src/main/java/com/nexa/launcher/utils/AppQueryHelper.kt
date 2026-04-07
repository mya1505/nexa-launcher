package com.nexa.launcher.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.nexa.launcher.R
import com.nexa.launcher.domain.model.AppEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppQueryHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun queryLaunchableApps(): List<AppEntry> {
        val packageManager = context.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val fallbackIcon = requireNotNull(ContextCompat.getDrawable(context, R.drawable.ic_fallback_app))

        return packageManager.queryIntentActivities(launcherIntent, 0)
            .asSequence()
            .filter { it.activityInfo?.packageName != context.packageName }
            .mapNotNull { resolveInfo ->
                val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
                val label = resolveInfo.loadLabel(packageManager)?.toString().orEmpty().ifBlank {
                    activityInfo.packageName
                }
                val icon = runCatching { resolveInfo.loadIcon(packageManager) }.getOrElse { fallbackIcon }
                AppEntry(
                    packageName = activityInfo.packageName,
                    activityName = activityInfo.name,
                    label = label,
                    icon = icon
                )
            }
            .sortedBy { it.label.lowercase() }
            .toList()
    }
}
