package com.plantcare.ai.identifier.plantid.data.database.repository

import com.plantcare.ai.identifier.plantid.data.database.daos.PlantDao
import com.plantcare.ai.identifier.plantid.data.database.entities.PlantEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRepository @Inject constructor(
    private val dao: PlantDao
) {
    suspend fun saveRecord(record: PlantEntity) = withContext(Dispatchers.IO){
        dao.saveRecord(record)
    }

    fun getAllRecord() = dao.getAllRecord()

    suspend fun deleteOneRecordPlant(plant: PlantEntity) = withContext(Dispatchers.IO){
        dao.deleteOneRecordPlant(plant)
    }

    suspend fun isPlantExists(scientificName: String) = withContext(Dispatchers.IO){
        dao.isPlantExists(scientificName)
    }

}