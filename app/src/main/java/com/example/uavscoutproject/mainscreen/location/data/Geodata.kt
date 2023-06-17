package com.example.uavscoutproject.mainscreen.location.data

/**
 * Represents the response received from the Geocode API.
 * @property items The list of geocode items returned in the response.
 */
data class GeocodeResponse(
    val items: List<GeocodeItem>
)


/**
 * Represents a geocode item containing information about a location.
 * @param title The title of the location.
 * @param position The position (latitude and longitude) of the location.
 * @param elevation The elevation of the location.
 * @param distance The distance of the location.
 */
data class GeocodeItem(
    val title: String,
    val position: Position,
    var elevation: Double = 0.0,
    var distance: Int = 0
)

/**
 * Represents the latitude and longitude of a location.
 * @param lat The latitude coordinate.
 * @param lng The longitude coordinate.
 */
data class Position(
    val lat: Double?,
    val lng: Double?
)
