package com.plantcare.ai.identifier.plantid.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.plantcare.ai.identifier.plantid.data.database.daos.AlarmDao
import com.plantcare.ai.identifier.plantid.data.database.daos.HistoryPlantDao
import com.plantcare.ai.identifier.plantid.data.database.daos.PlantDao
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity
import com.plantcare.ai.identifier.plantid.data.database.entities.HistoryEntity
import com.plantcare.ai.identifier.plantid.data.database.entities.PlantEntity
import com.plantcare.ai.identifier.plantid.utils.Converters

@Database(
    entities = [HistoryEntity::class, PlantEntity::class, AlarmEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun historyDao(): HistoryPlantDao
    abstract fun plantDao(): PlantDao
    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "AppDatabase.db"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }

}