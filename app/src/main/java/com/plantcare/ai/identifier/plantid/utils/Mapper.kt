package com.plantcare.ai.identifier.plantid.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity
import com.plantcare.ai.identifier.plantid.data.database.entities.HistoryEntity
import com.plantcare.ai.identifier.plantid.data.database.entities.PlantEntity
import com.plantcare.ai.identifier.plantid.data.network.dto.ResponseLocationDto
import com.plantcare.ai.identifier.plantid.data.network.dto.ResponsePlantDto
import com.plantcare.ai.identifier.plantid.domains.AlarmDomain
import com.plantcare.ai.identifier.plantid.domains.AlarmDomainDelete
import com.plantcare.ai.identifier.plantid.domains.HistoryDomain
import com.plantcare.ai.identifier.plantid.domains.LocationDomain
import com.plantcare.ai.identifier.plantid.domains.PlantDomain
import com.plantcare.ai.identifier.plantid.domains.ReminderDomainDelete

fun ResponseLocationDto.toLocationDomain() =
    LocationDomain(
        cityName = this.city,
        countriesCode = this.countryCode,
        lat = this.latitude,
        long = this.longitude,
    )

fun ResponsePlantDto.toPlantDomain() =
    PlantDomain(
        scientificName = this.data?.scientificName ?: "",
        genus = this.data?.genus ?: "",
        family = this.data?.family ?: "",
        commonNames = this.data?.commonNames ?: mutableListOf(),
        images = this.data?.images ?: mutableListOf()
    )

fun ResponsePlantDto.toHistoryEntity() =
    HistoryEntity(
        id = 0L,
        scientificName = this.data?.scientificName ?: "",
        genus = this.data?.genus ?: "",
        family = this.data?.family ?: "",
        commonNames = this.data?.commonNames ?: mutableListOf(),
        images = this.data?.images ?: mutableListOf()
    )

fun PlantEntity.toPlantDomain() = PlantDomain(
    id = this.id,
    scientificName = this.scientificName,
    genus = this.genus,
    family = this.family,
    commonNames = this.commonNames,
    images = this.images,
)

fun PlantDomain.toPlantEntity() = PlantEntity(
    id = this.id,
    scientificName = this.scientificName,
    genus = this.genus,
    family = this.family,
    commonNames = this.commonNames,
    images = this.images,
)

fun HistoryEntity.toHistoryDomain() =
    HistoryDomain(
        id = this.id,
        scientificName = this.scientificName,
        genus = this.genus,
        family = this.family,
        commonNames = this.commonNames,
        images = this.images,
        createdAt = this.createdAt
    )

fun HistoryDomain.toHistoryEntity() =
    HistoryEntity(
        id = this.id,
        scientificName = this.scientificName,
        genus = this.genus,
        family = this.family,
        commonNames = this.commonNames,
        images = this.images,
        createdAt = this.createdAt
    )

fun List<HistoryEntity>.toListHistoryDomain() =
    this.map { it.toHistoryDomain() }

fun List<HistoryDomain>.toListHistoryEntity() =
    this.map { it.toHistoryEntity() }

fun List<PlantEntity>.toListPlantDomain() =
    this.map { it.toPlantDomain() }

fun AlarmEntity.toAlarmDomain() =
    AlarmDomain(
        id = this.id,
        time = this.time,
        label = this.label,
        description = this.description,
        allDays = this.days,
        soundRes  = this.soundRes,
        isEnabled = this.isEnabled,
        isVibration = this.isVibration
    )

fun List<AlarmEntity>.toListAlarmDomain() =
    this.map { it.toAlarmDomain() }

fun AlarmDomain.toAlarmEntity(): AlarmEntity {
    val temp = AlarmEntity()
    temp.id = this.id
    temp.time = this.time
    temp.label = this.label
    temp.description = this.description
    temp.setDay(AlarmEntity.MON, this.allDays[AlarmEntity.MON])
    temp.setDay(AlarmEntity.TUES, this.allDays[AlarmEntity.TUES])
    temp.setDay(AlarmEntity.WED, this.allDays[AlarmEntity.WED])
    temp.setDay(AlarmEntity.THURS, this.allDays[AlarmEntity.THURS])
    temp.setDay(AlarmEntity.FRI, this.allDays[AlarmEntity.FRI])
    temp.setDay(AlarmEntity.SAT, this.allDays[AlarmEntity.SAT])
    temp.setDay(AlarmEntity.SUN, this.allDays[AlarmEntity.SUN])
    temp.soundRes = this.soundRes
    temp.setIsEnabled(this.isEnabled)
    temp.setIsVibration(this.isVibration)

    return temp
}

fun AlarmEntity.toAlarmDeleteDomain() = AlarmDomainDelete(
    id = this.id,
    time = this.time,
    label = this.label,
    description = this.description,
    allDays = this.days,
    soundRes  = this.soundRes,
    isEnabled = this.isEnabled,
    isVibration = this.isVibration,
    isSelected = false
)

fun AlarmDomainDelete.toAlarmEntity(): AlarmEntity {
    val temp = AlarmEntity()
    temp.id = this.id
    temp.label = this.label
    temp.description = this.description
    temp.setDay(AlarmEntity.MON, this.allDays[AlarmEntity.MON])
    temp.setDay(AlarmEntity.TUES, this.allDays[AlarmEntity.TUES])
    temp.setDay(AlarmEntity.WED, this.allDays[AlarmEntity.WED])
    temp.setDay(AlarmEntity.THURS, this.allDays[AlarmEntity.THURS])
    temp.setDay(AlarmEntity.FRI, this.allDays[AlarmEntity.FRI])
    temp.setDay(AlarmEntity.SAT, this.allDays[AlarmEntity.SAT])
    temp.setDay(AlarmEntity.SUN, this.allDays[AlarmEntity.SUN])
    temp.soundRes = this.soundRes
    temp.setIsEnabled(this.isEnabled)
    temp.setIsVibration(this.isVibration)

    return temp
}

fun List<AlarmDomainDelete>.toListAlarmEntity() =
    this.map { it.toAlarmEntity() }

fun List<AlarmEntity>.toListAlarmDeleteDomain() =
    this.map { it.toAlarmDeleteDomain() }

fun RecyclerView.addItemDecoration(
    marginStart: Int,
    marginTop: Int,
    marginEnd: Int,
    marginBottom: Int
) {
    this.addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            if (parent.getChildAdapterPosition(view) >= 0) {
                outRect.left = marginStart
                outRect.top = marginTop
                outRect.right = marginEnd
                outRect.bottom = marginBottom
            }
        }
    })
}