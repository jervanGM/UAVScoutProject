package com.example.uavscoutproject.mainscreen.datanalyzer.weatherapi

import com.example.uavscoutproject.mainscreen.datanalyzer.data.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for accessing weather data from the API.
 */
interface WeatherApiService {
    /**
     * Retrieves weather data for a specific location.
     *
     * @param latitude The latitude of the location.
     * @param longitude The longitude of the location.
     * @param hourly Indicates whether to retrieve hourly weather data.
     * @param forecastDays The number of forecast days to retrieve.
     * @return The weather response containing the retrieved data.
     */
    @GET("forecast")
    suspend fun weather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String,
        @Query("forecast_days") forecastDays: Int
    ): WeatherResponse
}

