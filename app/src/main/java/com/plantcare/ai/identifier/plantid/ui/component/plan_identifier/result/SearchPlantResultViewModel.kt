package com.plantcare.ai.identifier.plantid.ui.component.plan_identifier.result

import com.plantcare.ai.identifier.plantid.data.database.entities.PlantEntity
import com.plantcare.ai.identifier.plantid.data.database.repository.PlantRepository
import com.plantcare.ai.identifier.plantid.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchPlantResultViewModel @Inject constructor(
    private val plantRepo: PlantRepository
): BaseViewModel() {

    fun saveRecord(plant: PlantEntity) = bgScope.launch {
        plantRepo.saveRecord(plant)
    }

    fun deleteOneRecordPlant(plant: PlantEntity) = bgScope.launch {
        plantRepo.deleteOneRecordPlant(plant)
    }

    fun savePlantIfNotExists(plant: PlantEntity) = bgScope.launch{
        val exists = plantRepo.isPlantExists(plant.scientificName)
        if(exists == 0){
            saveRecord(plant)
        }
    }
}