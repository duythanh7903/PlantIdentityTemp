package com.plantcare.ai.identifier.plantid.ui.component.ring

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.plantcare.ai.identifier.plantid.data.database.entities.AlarmEntity
import com.plantcare.ai.identifier.plantid.data.database.repository.AlarmRepository
import com.plantcare.ai.identifier.plantid.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RingViewModel @Inject constructor(
    private val alarmRepo: AlarmRepository
): BaseViewModel() {

    private val _alarmEntity = MutableLiveData<AlarmEntity>()
    val alarmEntity: LiveData<AlarmEntity> = _alarmEntity

    private val _canFinishScreen = MutableLiveData<Boolean>()
    val canFinishScreen: LiveData<Boolean> = _canFinishScreen

    fun saveRecordReminder(record: AlarmEntity, onEventOnMainThread: () -> Unit) = bgScope.launch {
        alarmRepo.saveRecordAlarm(record)

        withContext(Dispatchers.IO) {
            onEventOnMainThread.invoke()
        }
    }

    fun searchAlarmEntity(id: Long) = bgScope.launch {
        _alarmEntity.postValue(
            alarmRepo.searchAlarmById(id)
        )
    }

    fun countDown1MinuteAndFinishScreenIfNotAction() = bgScope.launch {
        delay(60 * 1000)
        _canFinishScreen.postValue(true)
    }

    fun updateRecord(record: AlarmEntity, onEventInMainThread: () -> Unit) = bgScope.launch {
        alarmRepo.updateRecord(record)
        withContext(Dispatchers.IO) {
            onEventInMainThread.invoke()
        }
    }

}