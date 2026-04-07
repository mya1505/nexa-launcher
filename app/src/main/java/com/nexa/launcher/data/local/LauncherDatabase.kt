package com.nexa.launcher.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AppPreferenceEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LauncherDatabase : RoomDatabase() {
    abstract fun appPreferenceDao(): AppPreferenceDao
}
