package com.plantcare.ai.identifier.plantid.data.network.dto

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@Keep
@JsonClass(generateAdapter = true)
data class ResponsePlantDto(
    @Json(name = "statusCode")
    var statusCode: Int = 0,
    @Json(name = "code")
    var code: String = "",
    @Json(name = "message")
    var message: String = "",
    @Json(name = "path")
    var path: String = "",
    @Json(name = "timestamp")
    var timestamp: String = "",
    @Json(name = "data")
    var data: DataResponsePlantDto? = DataResponsePlantDto()
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class DataResponsePlantDto(
        @Json(name = "scientificName")
        var scientificName: String = "",
        @Json(name = "genus")
        var genus: String = "",
        @Json(name = "family")
        var family: String = "",
        @Json(name = "commonNames")
        var commonNames: List<String> = mutableListOf(),
        @Json(name = "images")
        var images: List<String> = mutableListOf()
    )

}