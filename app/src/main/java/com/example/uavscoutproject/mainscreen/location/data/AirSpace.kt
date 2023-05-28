package com.example.uavscoutproject.mainscreen.location.data


data class AirSpaceResponse(
    val status: String,
    val data: List<AirSpace>
)

data class AirSpace(
    val id: String,
    val airspace_uuid: String,
    val latitude: Double,
    val longitude: Double,
    val min_circle_radius: Double,
    val name: String,
    val type: String,
    val country: String,
    val state: String,
    val city: String,
    val last_updated: String,
    val properties: AirSpaceProperties,
    val geometry: AirSpaceGeometry,
    val ruleset_id: String
)

data class AirSpaceProperties(
    val url: String?,
    val icao: String?,
    val floor: Int,
    val faa_id: String?,
    val global_id: String
)

data class AirSpaceGeometry(
    val type: String,
    val coordinates: List<AirSpacePosition>
)

data class AirSpacePosition(
    val lat: Double,
    val lng: Double
)