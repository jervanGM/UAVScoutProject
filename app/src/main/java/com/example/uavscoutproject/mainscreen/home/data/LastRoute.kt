package com.example.uavscoutproject.mainscreen.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.uavscoutproject.mainscreen.home.dronedb.GeocodeItemListConverter
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem

/**
 * Entity class that represents the last drone route data.
 *
 * @property id The unique identifier of the last route data.
 * @property route The list of geocode items representing the coordinates of the route.
 * @property distance The total distance of the flight.
 * @property time The total time of the flight.
 * @property weather The state of the weather during the flight.
 */
@Entity(tableName = "last_route")
@TypeConverters(GeocodeItemListConverter::class)
class LastRoute {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private var _id: Int = 0

    @ColumnInfo(name = "route")
    private var _route: List<GeocodeItem> = emptyList()

    @ColumnInfo(name = "totalDistance")
    private var _distance: Double = 0.0

    @ColumnInfo(name = "totalTime")
    private var _time: Int = 0

    @ColumnInfo(name = "weatherState")
    private var _weather: String = ""

    /**
     * The unique identifier of the last route data.
     */
    var id: Int
        get() = _id
        set(value) {
            _id = value
        }

    /**
     * The list of geocode items representing the coordinates of the route.
     */
    var route: List<GeocodeItem>
        get() = _route
        set(value) {
            _route = value
        }

    /**
     * The total distance of the flight.
     */
    var distance: Double
        get() = _distance
        set(value) {
            _distance = value
        }

    /**
     * The total time of the flight.
     */
    var time: Int
        get() = _time
        set(value) {
            _time = value
        }

    /**
     * The state of the weather during the flight.
     */
    var weather: String
        get() = _weather
        set(value) {
            _weather = value
        }
}