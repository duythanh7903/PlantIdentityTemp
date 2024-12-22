package com.plantcare.ai.identifier.plantid.data.network.repository

import com.plantcare.ai.identifier.plantid.data.network.service.WeatherService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherService: WeatherService
) {

    suspend fun fetchDataWeather(
        cityCode: String,
        lat: Double,
        long: Double
    ) = weatherService.fetchDataWeather(
        cityCode = cityCode,
        lat = lat,
        long = long
    )

}