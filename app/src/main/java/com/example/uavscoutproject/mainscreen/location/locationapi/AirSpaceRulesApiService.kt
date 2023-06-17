package com.example.uavscoutproject.mainscreen.location.locationapi

import com.example.uavscoutproject.mainscreen.location.data.AirMapRulesResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface AirSpaceRulesApiService {
    /**
     * Retrieves airspace rules based on the provided ID.
     * @param id The ID of the airspace for which to retrieve the rules.
     * @param accept The value of the Accept header for specifying the response format.
     * @param apiKey The API key for authentication.
     * @return The response containing the airspace rules.
     */
    @GET("rules/v1/{id}")
    suspend fun getAirMapRules(
        @Path("id") id: String,
        @Header("Accept") accept: String,
        @Header("X-API-KEY") apiKey: String
    ): retrofit2.Response<AirMapRulesResponse>
}
