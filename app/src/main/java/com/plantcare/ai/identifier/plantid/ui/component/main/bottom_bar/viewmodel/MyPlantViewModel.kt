package com.plantcare.ai.identifier.plantid.ui.component.main.bottom_bar.viewmodel

import androidx.lifecycle.viewModelScope
import com.plantcare.ai.identifier.plantid.data.database.entities.PlantEntity
import com.plantcare.ai.identifier.plantid.data.database.repository.PlantRepository
import com.plantcare.ai.identifier.plantid.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPlantViewModel @Inject constructor(
    private val plantRepo: PlantRepository
): BaseViewModel() {

    val records = plantRepo.getAllRecord()

    fun deletePlantEntity(plantEntity: PlantEntity) =
        bgScope.launch {
            plantRepo.deleteOneRecordPlant(plantEntity)
        }

}