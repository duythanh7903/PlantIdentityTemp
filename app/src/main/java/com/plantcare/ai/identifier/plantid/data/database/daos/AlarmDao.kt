package com.plantcare.ai.identifier.plantid.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity.TYPE_ALARM_NORMAL

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRecordAlarm(entity: AlarmEntity)

    @Query("SELECT * FROM Record_Alarm WHERE type == $TYPE_ALARM_NORMAL ORDER BY id ASC")
    fun getAllRecordAlarm(): LiveData<List<AlarmEntity>>

    @Query("SELECT * FROM Record_Alarm WHERE isEnabled == 1 AND type == $TYPE_ALARM_NORMAL ORDER BY id ASC")
    fun getAllRecordAlarmTypeList(): List<AlarmEntity>

    @Query("SELECT * FROM Record_Alarm WHERE id == :id")
    fun searchAlarmById(id: Long) : AlarmEntity

    @Update
    fun updateRecord(entity: AlarmEntity)

    @Delete
    fun deleteListRecordRemind(records: MutableList<AlarmEntity>)

}