package com.nexa.launcher.utils

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

object LockScreenHelper {
    fun lockNow(context: Context): Boolean {
        val adminComponent = ComponentName(context, LauncherDeviceAdminReceiver::class.java)
        val manager = context.getSystemService(DevicePolicyManager::class.java)
        val isAdminEnabled = manager.isAdminActive(adminComponent)

        return if (isAdminEnabled) {
            manager.lockNow()
            true
        } else {
            false
        }
    }

    fun adminIntent(context: Context): Intent {
        val adminComponent = ComponentName(context, LauncherDeviceAdminReceiver::class.java)
        return Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
            .putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            .putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Enable device admin so double tap can lock the screen instantly."
            )
    }
}
