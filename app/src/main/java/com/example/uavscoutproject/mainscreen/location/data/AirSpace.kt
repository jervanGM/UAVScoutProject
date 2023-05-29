package com.example.uavscoutproject.mainscreen.location.data

import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType


data class AirSpaceResponse(
    val status: String,
    val data: List<AirSpace>
)

data class AirSpace(
    val id: String,
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
    val coordinates: List<List<List<Double>>>
)

data class AirSpacePosition(
    val lat: Double,
    val lng: Double
)
fun List<List<List<Double>>>.toPositionList(): List<AirSpacePosition> {
    val positionList = mutableListOf<AirSpacePosition>()

    for (coordinateList in this) {
        for (coordinate in coordinateList) {
            val longitude = coordinate[0]
            val latitude = coordinate[1]

            val position = AirSpacePosition(latitude, longitude)
            positionList.add(position)
        }
    }

    return positionList
}