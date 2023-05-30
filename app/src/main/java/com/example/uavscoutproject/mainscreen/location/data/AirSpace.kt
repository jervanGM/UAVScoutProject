package com.example.uavscoutproject.mainscreen.location.data

import android.util.Log
import com.mapbox.mapboxsdk.geometry.LatLng
import java.text.DecimalFormat
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
fun List<List<List<Double>>>.toPolygon(): MutableList<LatLng> {
    val polygons: MutableList<LatLng> = mutableListOf()

    for (coordinateList in this) {
        for (coordinate in coordinateList) {
            val longitude = coordinate[0]
            val latitude = coordinate[1]
            polygons.add(LatLng(latitude, longitude))
        }
    }

    return polygons
}