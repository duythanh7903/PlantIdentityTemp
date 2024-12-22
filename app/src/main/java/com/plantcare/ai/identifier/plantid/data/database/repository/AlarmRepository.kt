package com.plantcare.ai.identifier.plantid.data.database.repository

import com.plantcare.ai.identifier.plantid.data.database.daos.AlarmDao
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
    private val alarmDao: AlarmDao
) {

    suspend fun saveRecordAlarm(entity: AlarmEntity) = withContext(Dispatchers.IO) {
        alarmDao.saveRecordAlarm(entity)
    }

    fun getAllRecordAlarm() = alarmDao.getAllRecordAlarm()

    suspend fun deleteListRecordRemind(records: MutableList<AlarmEntity>) =
        withContext(Dispatchers.IO) {
            alarmDao.deleteListRecordRemind(records)
        }

    suspend fun updateRecord(record: AlarmEntity) = withContext(Dispatchers.IO) {
        alarmDao.updateRecord(record)
    }

    suspend fun searchAlarmById(id: Long) =
        withContext(Dispatchers.IO) {
            alarmDao.searchAlarmById(id)
        }

}