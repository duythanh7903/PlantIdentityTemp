package com.plantcare.ai.identifier.plantid.ui.component.remind

import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity
import com.plantcare.ai.identifier.plantid.data.database.repository.AlarmRepository
import com.plantcare.ai.identifier.plantid.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RemindViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : BaseViewModel() {

    val records = alarmRepository.getAllRecordAlarm()

    fun saveRecord(entity: AlarmEntity, onUpdateOnMainThread: () -> Unit) = bgScope.launch {
        alarmRepository.saveRecordAlarm(entity)
        withContext(Dispatchers.Main) {
            onUpdateOnMainThread.invoke()
        }
    }

    fun updateRecord(record: AlarmEntity, onUpdateOnMainThread: () -> Unit) = bgScope.launch {
        alarmRepository.updateRecord(record)
        withContext(Dispatchers.Main) {
            onUpdateOnMainThread.invoke()
        }
    }

    fun deleteListRecordRemind(entities: MutableList<AlarmEntity>) = bgScope.launch {
        alarmRepository.deleteListRecordRemind(entities)
    }

}