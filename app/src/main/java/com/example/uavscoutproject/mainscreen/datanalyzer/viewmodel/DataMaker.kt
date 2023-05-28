package com.example.uavscoutproject.mainscreen.datanalyzer.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.uavscoutproject.mainscreen.datanalyzer.data.HourlyData

object DataMaker {
    private var weatherdata: MutableLiveData<HourlyData> = MutableLiveData(HourlyData())

    fun setWeatherData(newData: HourlyData) {
        weatherdata = MutableLiveData(newData)
    }

    fun deleteData() {
        weatherdata.value = HourlyData()
    }

    fun getData(): HourlyData {
        return weatherdata.value!!
    }

}