package com.example.uavscoutproject.mainscreen.datanalyzer.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.uavscoutproject.mainscreen.datanalyzer.data.HourlyData

/**
 * Data manager class for handling weather data.
 */
object DataMaker {
    private var weatherdata: MutableLiveData<HourlyData> = MutableLiveData(HourlyData())

    /**
     * Sets the new weather data.
     *
     * @param newData The new weather data to be set.
     */
    fun setWeatherData(newData: HourlyData) {
        weatherdata = MutableLiveData(newData)
    }

    /**
     * Retrieves the current weather data.
     *
     * @return The current weather data.
     */
    fun getData(): HourlyData {
        return weatherdata.value!!
    }
}
