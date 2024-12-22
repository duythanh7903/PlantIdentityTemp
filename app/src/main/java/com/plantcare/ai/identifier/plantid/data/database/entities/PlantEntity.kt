package com.plantcare.ai.identifier.plantid.data.database.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
@Entity(tableName = "Plant")
data class PlantEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var scientificName: String = "",
    var genus: String = "",
    var family: String = "",
    var commonNames: List<String> = mutableListOf(),
    var images: List<String> = mutableListOf(),
    var createdAt: Long = System.currentTimeMillis()
): Parcelable