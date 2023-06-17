package com.example.uavscoutproject.mainscreen.location.data

import org.osmdroid.util.GeoPoint


/**
 * Represents the response received from the AirSpace API.
 * @property status The status of the response.
 * @property data The list of airspaces returned in the response.
 */
data class AirSpaceResponse(
    val status: String,
    val data: List<AirSpace>
)

/**
 * Represents an airspace.
 * @property id The ID of the airspace.
 * @property name The name of the airspace.
 * @property type The type of the airspace.
 * @property country The country of the airspace.
 * @property state The state of the airspace.
 * @property city The city of the airspace.
 * @property geometry The geometry of the airspace.
 * @property ruleset_id The ID of the ruleset.
 */
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
    /**
     * Converts the AirSpace object to a map representation.
     * @return The map representation of the AirSpace object.
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "name" to name,
            "type" to type,
            "country" to country,
            "state" to state,
            "city" to city,
            "geometry" to geometry.coordinates.toPolygon().mapIndexed { index, coordinates ->
                "item$index" to coordinates
            }.toMap(),
            "ruleset_id" to ruleset_id
        )
    }
}

/**
 * Represents the geometry of an airspace.
 * @property coordinates The coordinates of the airspace geometry.
 * @property polygons The list of polygons representing the airspace geometry.
 */
data class AirSpaceGeometry(
    val coordinates: List<List<List<Double>>> = emptyList(),
    val polygons: List<GeoPoint>
)

/**
 * Extension function to convert a list of lists of doubles to a list of GeoPoint objects representing a polygon.
 * @return The list of GeoPoint objects representing the polygon.
 */
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
