package com.plantcare.ai.identifier.plantid.data.database.repository

import com.plantcare.ai.identifier.plantid.data.database.daos.HistoryPlantDao
import com.plantcare.ai.identifier.plantid.data.database.entities.HistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(
    private val dao: HistoryPlantDao
) {

    suspend fun saveHistory(history: HistoryEntity) = withContext(Dispatchers.IO) {
        dao.saveHistory(history)
    }

    fun getAllHistory() = dao.getAllHistory()

    suspend fun deleteListRecordRemind(histories: MutableList<HistoryEntity>) =
        withContext(Dispatchers.IO) {
            dao.deleteListRecordRemind(histories)
        }

}