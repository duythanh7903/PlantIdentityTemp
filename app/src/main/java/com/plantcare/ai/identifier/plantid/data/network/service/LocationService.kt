package com.plantcare.ai.identifier.plantid.data.network.service

import com.plantcare.ai.identifier.plantid.data.network.dto.ResponseLocationDto
import com.plantcare.ai.identifier.plantid.utils.network_adapter_factory.ResultWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationService {

    @GET("/data/reverse-geocode-client")
    suspend fun fetchDataLocation(
        @Query("latitude") lat: Double = 0.0,
        @Query("longitude") lon: Double = 0.0,
        @Query("localityLanguage") localityLang: String = "en",
    ): ResultWrapper<ResponseLocationDto>

}