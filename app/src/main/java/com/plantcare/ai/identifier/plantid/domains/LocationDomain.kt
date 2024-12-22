package com.plantcare.ai.identifier.plantid.domains

data class LocationDomain(
    var cityName: String = "HaNoi",
    var countriesCode: String = "VN",
    var lat: Double = 0.0,
    var long: Double = 0.0,
)