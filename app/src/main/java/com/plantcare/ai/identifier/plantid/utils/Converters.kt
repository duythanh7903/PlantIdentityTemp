package com.plantcare.ai.identifier.plantid.utils

import android.util.SparseBooleanArray
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.google.gson.Gson
import org.json.JSONArray

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson<List<String>?>(value, listType).toList()
    }

    @TypeConverter
    fun fromSparseBooleanArray(sparseBooleanArray: SparseBooleanArray?): String? {
        if (sparseBooleanArray == null) return null
        val jsonArray = JSONArray()
        for (i in 0 until sparseBooleanArray.size()) {
            val key = sparseBooleanArray.keyAt(i)
            val value = sparseBooleanArray[key]
            jsonArray.put(JSONArray().put(key).put(if (value) 1 else 0))
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toSparseBooleanArray(json: String?): SparseBooleanArray? {
        if (json.isNullOrEmpty()) return null
        val sparseBooleanArray = SparseBooleanArray()
        val jsonArray = JSONArray(json)
        for (i in 0 until jsonArray.length()) {
            val item = jsonArray.getJSONArray(i)
            val key = item.getInt(0)
            val value = item.getInt(1) == 1
            sparseBooleanArray.put(key, value)
        }
        return sparseBooleanArray
    }
}