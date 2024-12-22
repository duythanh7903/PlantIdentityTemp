package com.plantcare.ai.identifier.plantid.data.network.dto

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
@Keep
@JsonClass(generateAdapter = true)
data class ResponseLocationDto(
    val latitude: Double,
    val longitude: Double,
    val lookupSource: String,
    val localityLanguageRequested: String,
    val continent: String,
    val continentCode: String,
    val countryName: String,
    val countryCode: String,
    val principalSubdivision: String,
    val principalSubdivisionCode: String,
    val city: String,
    val locality: String,
    val postcode: String?,
    val plusCode: String,
    val localityInfo: LocalityInfo
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class LocalityInfo(
        val administrative: List<Administrative>,
        val informative: List<Informative>
    )
    @Keep
    @JsonClass(generateAdapter = true)
    data class Administrative(
        val name: String,
        val description: String?,
        val isoName: String?,
        val order: Int,
        val adminLevel: Int,
        val isoCode: String?,
        val wikidataId: String?,
        val geonameId: Int?
    )
    @Keep
    @JsonClass(generateAdapter = true)
    data class Informative(
        val name: String,
        val description: String?,
        val isoName: String?,
        val order: Int,
        val isoCode: String?,
        val wikidataId: String?,
        val geonameId: Int?
    )

}