package com.example.uavscoutproject.mainscreen.location.data

import android.util.Log
import com.mapbox.mapboxsdk.geometry.LatLng
import org.osmdroid.util.GeoPoint
import java.text.DecimalFormat
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType


data class AirSpaceResponse(
    val status: String,
    val data: List<AirSpace>
)

data class AirSpace(
    val id: String,
    val name: String,
    val type: String,
    val country: String,
    val state: String,
    val city: String,
    val geometry: AirSpaceGeometry,
    val ruleset_id: String
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "type" to type,
            "country" to country,
            "state" to state,
            "city" to city,
            "geometry" to geometry.coordinates.toPolygon().mapIndexed{ index, coordinates ->
                "item$index" to coordinates
            }.toMap(),
            "ruleset_id" to ruleset_id
        )
    }
}

data class AirSpaceGeometry(
    val coordinates: List<List<List<Double>>> = emptyList(),
    val polygons : List<GeoPoint>
)
fun List<List<List<Double>>>.toPolygon(): MutableList<GeoPoint> {
    val polygons: MutableList<GeoPoint> = mutableListOf()

    for (coordinateList in this) {
        for (coordinate in coordinateList) {
            val longitude = coordinate[0]
            val latitude = coordinate[1]
            polygons.add(GeoPoint(latitude, longitude))
        }
    }

    return polygons
}