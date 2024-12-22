package com.plantcare.ai.identifier.plantid.ui.component.history

import com.plantcare.ai.identifier.plantid.data.database.entities.HistoryEntity
import com.plantcare.ai.identifier.plantid.data.database.repository.HistoryRepository
import com.plantcare.ai.identifier.plantid.ui.bases.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepo: HistoryRepository
): BaseViewModel() {

    val records = historyRepo.getAllHistory()

    fun deleteListHistoryEntity(entities: MutableList<HistoryEntity>) =
        bgScope.launch {
            historyRepo.deleteListRecordRemind(entities)
        }

}