package com.plantcare.ai.identifier.plantid.data.network.dto

import androidx.annotation.Keep
import com.squareup.moshi.JsonClass
@Keep
@JsonClass(generateAdapter = true)
data class ResponseWeatherDto(
    val code: String,
    val `data`: Data,
    val message: String,
    val path: String,
    val statusCode: Int,
    val timestamp: String
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Data(
        val alerts: List<Alert>,
        val current: Current,
        val forecast: List<Forecast>,
        val time: String
    ) {
        @Keep
        @JsonClass(generateAdapter = true)
        data class Alert(
            val areas: String? = null,
            val category: String? = null,
            val certainty: String? = null,
            val desc: String? = null,
            val effective: String? = null,
            val event: String? = null,
            val expires: String? = null,
            val headline: String? = null,
            val instruction: String? = null,
            val msgtype: String? = null,
            val note: String? = null,
            val severity: String? = null,
            val urgency: String? = null
        )
        @Keep
        @JsonClass(generateAdapter = true)
        data class Current(
            val airQualityIndex: Double? = null,
            val airQualityText: String? = null,
            val cloud: Double? = null,
            val conditionCode: Double? = null,
            val dewpointC: Double? = null,
            val dewpointF: Double? = null,
            val feelslikeC: Double? = null,
            val feelslikeF: Double? = null,
            val humidity: Double? = null,
            val isDay: Int? = null,
            val precipMm: Double? = null,
            val pressureHPa: Double? = null,
            val tempC: Double? = null,
            val tempF: Double? = null,
            val uv: Double? = null,
            val visKm: Double? = null,
            val windDegree: Double? = null,
            val windDir: String? = null,
            val windMps: Double? = null
        )
        @Keep
        @JsonClass(generateAdapter = true)
        data class Forecast(
            val date: String? = null,
            val dateEpoch: Double? = null,
            val day: Day? = null,
            val hour: List<Hour>? = null
        ) {
            @Keep
            @JsonClass(generateAdapter = true)
            data class Hour(
                val airQualityIndex: Double? = null,
                val airQualityText: String? = null,
                val chanceOfRain: Double? = null,
                val chanceOfSnow: Double? = null,
                val cloud: Double? = null,
                val conditionCode: Double? = null,
                val dewpointC: Double? = null,
                val dewpointF: Double? = null,
                val humidity: Double? = null,
                val isDay: Double? = null,
                val pressureHPa: Double? = null,
                val snowCm: Double? = null,
                val tempC: Double? = null,
                val tempF: Double? = null,
                val time: String? = null,
                val timeEpoch: Double? = null,
                val uv: Double? = null,
                val visKm: Double? = null,
                val willItRain: Double? = null,
                val willItSnow: Double? = null,
                val windDegree: Double? = null,
                val windDir: String? = null,
                val windMps: Double? = null
            )
            @Keep
            @JsonClass(generateAdapter = true)
            data class Day(
                val airQualityIndex: Double? = null,
                val airQualityText: String? = null,
                val avghumidity: Double? = null,
                val avgtempC: Double? = null,
                val avgtempF: Double? = null,
                val avgvisKm: Double? = null,
                val conditionCode: Double? = null,
                val dailyChanceOfRain: Double? = null,
                val dailyChanceOfSnow: Double? = null,
                val dailyWillItRain: Double? = null,
                val dailyWillItSnow: Double? = null,
                val maxtempC: Double? = null,
                val maxtempF: Double? = null,
                val maxwindMps: Double? = null,
                val mintempC: Double? = null,
                val mintempF: Double? = null,
                val sunrise: String? = null,
                val sunset: String? = null,
                val totalprecipMm: Double? = null,
                val totalsnowCm: Double? = null,
                val uv: Double? = null
            )
        }
    }
}