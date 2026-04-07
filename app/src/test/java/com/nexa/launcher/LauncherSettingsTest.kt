package com.nexa.launcher

import com.nexa.launcher.domain.model.LauncherSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LauncherSettingsTest {
    @Test
    fun defaultSettingsAreWithinExpectedBounds() {
        val settings = LauncherSettings()

        assertEquals(4, settings.gridColumns)
        assertEquals(5, settings.gridRows)
        assertTrue(settings.iconSizeDp in 40f..80f)
        assertTrue(settings.enableSwipeUpDrawer)
    }
}
