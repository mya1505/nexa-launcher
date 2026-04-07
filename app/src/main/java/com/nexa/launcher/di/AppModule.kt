package com.nexa.launcher.di

import android.content.Context
import androidx.room.Room
import com.nexa.launcher.data.local.AppPreferenceDao
import com.nexa.launcher.data.local.LauncherDatabase
import com.nexa.launcher.data.preferences.SettingsDataStore
import com.nexa.launcher.data.repository.LauncherRepository
import com.nexa.launcher.data.repository.LauncherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindLauncherRepository(impl: LauncherRepositoryImpl): LauncherRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): LauncherDatabase {
            return Room.databaseBuilder(context, LauncherDatabase::class.java, "launcher.db").build()
        }

        @Provides
        fun provideAppPreferenceDao(database: LauncherDatabase): AppPreferenceDao = database.appPreferenceDao()

        @Provides
        @Singleton
        fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore =
            SettingsDataStore(context)
    }
}
