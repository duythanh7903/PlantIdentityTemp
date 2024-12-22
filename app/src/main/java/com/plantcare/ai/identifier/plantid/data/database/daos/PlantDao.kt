package com.plantcare.ai.identifier.plantid.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.plantcare.ai.identifier.plantid.data.database.entities.PlantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRecord(record: PlantEntity)

    @Query("SELECT * FROM PLANT order by createdAt DESC")
    fun getAllRecord(): Flow<List<PlantEntity>?>

    @Delete
    suspend fun deleteOneRecordPlant(plantEntity: PlantEntity)

    @Query("SELECT COUNT(*) FROM PLANT WHERE scientificName = :scientificName")
    suspend fun isPlantExists(scientificName: String): Int
}