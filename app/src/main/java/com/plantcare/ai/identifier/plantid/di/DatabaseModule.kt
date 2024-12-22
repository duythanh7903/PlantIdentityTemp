package com.plantcare.ai.identifier.plantid.di

import android.content.Context
import androidx.room.Room
import com.plantcare.ai.identifier.plantid.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "AppDatabase.db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideHistoryDao(appDb: AppDatabase) = appDb.historyDao()

    @Provides
    fun providePlantDao(appDb: AppDatabase) = appDb.plantDao()

    @Provides
    fun provideAlarmDao(appDb: AppDatabase) = appDb.alarmDao()
}