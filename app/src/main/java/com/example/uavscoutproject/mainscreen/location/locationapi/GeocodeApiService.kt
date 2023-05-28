package com.example.uavscoutproject.mainscreen.location.locationapi

import com.example.uavscoutproject.mainscreen.location.data.GeocodeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeApiService {
    @GET("geocode")
    suspend fun geocode(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): GeocodeResponse
}
