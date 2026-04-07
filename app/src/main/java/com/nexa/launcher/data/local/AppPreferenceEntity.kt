package com.nexa.launcher.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_preferences")
data class AppPreferenceEntity(
    @PrimaryKey val packageName: String,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val launchCount: Int = 0,
    val lastLaunchedAt: Long = 0L
)
