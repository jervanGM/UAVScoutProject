package com.example.uavscoutproject.mainscreen.location.locationapi

import com.example.uavscoutproject.mainscreen.location.data.AirSpaceResponse
import okhttp3.Call
import okhttp3.ResponseBody

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface AirSpaceApiService {
    @GET("airspace/v2/search")
    suspend fun searchAirSpace(
        @Query("geometry") geometry: String,
        @Query("buffer") buffer: Int,
        @Query("types") types: String,
        @Query("full") full: Boolean,
        @Query("geometry_format") geometryFormat: String,
        @Header("X-API-Key") apiKey: String
    ): AirSpaceResponse
}