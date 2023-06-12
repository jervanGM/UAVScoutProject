package com.example.uavscoutproject.mainscreen.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.uavscoutproject.mainscreen.home.dronedb.GeocodeItemListConverter
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem

@Entity(tableName = "last_route")
@TypeConverters(GeocodeItemListConverter::class)
class LastRoute {
    @PrimaryKey(autoGenerate = true) private var _id: Int = 0
    @ColumnInfo(name = "route") private var _route : List<GeocodeItem> = emptyList() //Coordenadas de ruta
    @ColumnInfo(name = "totalDistance") private var _distance : Double = 0.0 //Distancia de vuelo
    @ColumnInfo(name = "totalTime") private var _time : Int = 0      //Tiempo de vuelo total
    @ColumnInfo(name = "weatherState") private var _weather = ""     //Estado del clima

    var id: Int
        get() = _id
        set(value) {
            _id = value
        }

    var route: List<GeocodeItem>
        get() = _route
        set(value) {
            _route = value
        }

    var distance: Double
        get() = _distance
        set(value) {
            _distance = value
        }

    var time: Int
        get() = _time
        set(value) {
            _time = value
        }

    var weather: String
        get() = _weather
        set(value) {
            _weather = value
        }

}