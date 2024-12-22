package com.plantcare.ai.identifier.plantid.domains

import java.io.Serializable

data class PlantDomain(
    val id: Long = 0L,
    var scientificName: String = "",
    var genus: String = "",
    var family: String = "",
    var commonNames: List<String> = mutableListOf(),
    var images: List<String> = mutableListOf()
): Serializable {
}