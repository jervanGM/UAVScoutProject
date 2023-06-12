package com.example.uavscoutproject.mainscreen.home.dronedb

import androidx.room.TypeConverter
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class GeocodeItemListConverter {
    @TypeConverter
    fun fromList(value: List<GeocodeItem>): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    @TypeConverter
    fun toList(value: String): List<GeocodeItem> {
        val gson = Gson()
        val listType = object : TypeToken<List<GeocodeItem>>() {}.type
        return gson.fromJson(value, listType)
    }
}
