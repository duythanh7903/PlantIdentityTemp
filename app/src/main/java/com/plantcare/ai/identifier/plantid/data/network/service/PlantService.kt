package com.plantcare.ai.identifier.plantid.data.network.service

import com.plantcare.ai.identifier.plantid.data.network.dto.ResponsePlantDto
import com.plantcare.ai.identifier.plantid.utils.network_adapter_factory.ResultWrapper
import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PlantService {

    @Multipart
    @POST("/api/v1/plants")
    suspend fun fetchInformationPlant(
        @Header("x-package-name") packageName: String,
        @Header("x-version-code") versionCode: String,
        @Header("x-internal-key") internalKey: String,
        @Header("x-secret-key") secretKey: String,
        @Part file: MultipartBody.Part
    ): ResultWrapper<ResponsePlantDto>

}