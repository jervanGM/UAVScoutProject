package com.example.uavscoutproject.mainscreen.datanalyzer.weatherapi

import com.example.uavscoutproject.mainscreen.datanalyzer.data.WeatherResponse
import com.example.uavscoutproject.mainscreen.location.data.GeocodeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    suspend fun weather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String,
        @Query("forecast_days") forecastDays: Int
    ): WeatherResponse
}
