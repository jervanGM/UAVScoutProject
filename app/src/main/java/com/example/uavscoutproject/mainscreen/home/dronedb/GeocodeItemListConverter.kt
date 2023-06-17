package com.example.uavscoutproject.mainscreen.home.dronedb

import androidx.room.TypeConverter
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

/**
 * Type converter class for converting a list of GeocodeItem objects to and from a JSON string.
 */
class GeocodeItemListConverter {
    /**
     * Converts a list of GeocodeItem objects to a JSON string.
     *
     * @param value The list of GeocodeItem objects to be converted.
     * @return A JSON string representation of the provided list of GeocodeItem objects.
     */
    @TypeConverter
    fun fromList(value: List<GeocodeItem>): String {
        val gson = Gson()
        return gson.toJson(value)
    }

    /**
     * Converts a JSON string to a list of GeocodeItem objects.
     *
     * @param value The JSON string to be converted.
     * @return A list of GeocodeItem objects parsed from the provided JSON string.
     */
    @TypeConverter
    fun toList(value: String): List<GeocodeItem> {
        val gson = Gson()
        val listType = object : TypeToken<List<GeocodeItem>>() {}.type
        return gson.fromJson(value, listType)
    }
}
