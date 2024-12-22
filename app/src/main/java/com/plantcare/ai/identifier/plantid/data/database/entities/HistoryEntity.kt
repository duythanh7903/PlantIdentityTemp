package com.plantcare.ai.identifier.plantid.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HISTORY_SEARCH")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var scientificName: String = "",
    var genus: String = "",
    var family: String = "",
    var commonNames: List<String> = mutableListOf(),
    var images: List<String> = mutableListOf(),
    var createdAt: Long = System.currentTimeMillis()
) {
}