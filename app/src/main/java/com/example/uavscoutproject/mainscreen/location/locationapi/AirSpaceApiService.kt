package com.example.uavscoutproject.mainscreen.location.locationapi

import com.example.uavscoutproject.mainscreen.location.data.AirSpaceResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface AirSpaceApiService {
    /**
     * Searches for airspace based on the provided parameters.
     * @param geometry The geometry parameter specifying the search area.
     * @param buffer The buffer parameter specifying the distance around the geometry.
     * @param types The types parameter specifying the airspace types to search for.
     * @param full The full parameter indicating whether to include detailed information in the response.
     * @param geometryFormat The geometry format parameter specifying the format of the geometry parameter.
     * @param apiKey The API key for authentication.
     * @return The response containing the airspace search results.
     */
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
