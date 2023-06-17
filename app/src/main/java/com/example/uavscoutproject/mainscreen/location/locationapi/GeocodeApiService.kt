package com.example.uavscoutproject.mainscreen.location.locationapi

import com.example.uavscoutproject.mainscreen.location.data.GeocodeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeApiService {
    /**
     * Performs geocoding based on the provided query.
     * @param query The query string for geocoding.
     * @param apiKey The API key for authentication.
     * @return The response containing geocoding data.
     */
    @GET("geocode")
    suspend fun geocode(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String
    ): GeocodeResponse
}

