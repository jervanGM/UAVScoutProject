package com.example.uavscoutproject.mainscreen.location.locationapi

import com.example.uavscoutproject.mainscreen.location.data.AirMapRulesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface AirSpaceRulesApiService {
    @GET("rules/v1/{id}")
    suspend fun getAirMapRules(
        @Path("id") id: String,
        @Header("Accept") accept: String,
        @Header("X-API-KEY") apiKey: String
    ): AirMapRulesResponse
}