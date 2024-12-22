package com.plantcare.ai.identifier.plantid.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.plantcare.ai.identifier.plantid.data.database.entities.HistoryEntity

@Dao
interface HistoryPlantDao {

    @Insert
    fun saveHistory(history: HistoryEntity)

    @Query("SELECT * FROM HISTORY_SEARCH ORDER BY createdAt DESC")
    fun getAllHistory(): LiveData<List<HistoryEntity>>

    @Delete
    fun deleteListRecordRemind(histories: MutableList<HistoryEntity>)

}