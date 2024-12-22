package com.plantcare.ai.identifier.plantid.data.network.repository

import com.plantcare.ai.identifier.plantid.data.network.service.LocationService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val locationService: LocationService
) {

    suspend fun fetchDataLocation(
        lat: Double = 0.0, lon: Double = 0.0
    ) = locationService.fetchDataLocation(
        lat = lat, lon = lon
    )

}