package com.plantcare.ai.identifier.plantid.data.network.service

import com.plantcare.ai.identifier.plantid.data.network.dto.ResponseWeatherDto
import com.plantcare.ai.identifier.plantid.utils.network_adapter_factory.ResultWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("api/weathers?")
    suspend fun fetchDataWeather(
        @Query("cityCode") cityCode: String,
        @Query("lat") lat: Double,
        @Query("long") long: Double,
    ): ResultWrapper<ResponseWeatherDto>

}