package com.plantcare.ai.identifier.plantid.ui.component.diagnose.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlantDiseaseModel(
    var imageList: List<Int> = emptyList(),
    var diseaseName: Int = 0,
    var diseaseSymptoms : Int = 0,
    var diseaseCause  : Int = 0,
    var diseaseManagement  : Int = 0,
): Parcelable {
}