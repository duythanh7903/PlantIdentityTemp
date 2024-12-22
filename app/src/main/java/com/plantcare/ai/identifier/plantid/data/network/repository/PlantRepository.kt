package com.plantcare.ai.identifier.plantid.data.network.repository

import com.plantcare.ai.identifier.plantid.data.network.service.PlantService
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRepository @Inject constructor (
    private val plantService: PlantService
) {

    suspend fun fetchInformationPlant(
        packageName : String,
        versionCode : String,
        internalKey: String,
        secretKey: String,
        image: MultipartBody.Part
    ) = plantService.fetchInformationPlant(
        packageName,versionCode,internalKey, secretKey, image
    )

}