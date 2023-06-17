package com.example.uavscoutproject.mainscreen.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.uavscoutproject.R

/**
 * Entity class that represents personal drone data.
 *
 * @property id The unique identifier of the drone data.
 * @property name The name of the drone vehicle.
 * @property vehicle The type of aircraft.
 * @property provider The provider of the aircraft.
 * @property color The color of the vehicle.
 * @property speed The speed of the vehicle.
 * @property weight The weight of the vehicle.
 * @property battery The type of battery of the vehicle.
 * @property energy The power of the battery in Wh.
 * @property capacity The capacity of the battery in mAh.
 * @property operator The operator of the identification plate.
 * @property telephone The operator's phone number.
 * @property serial The vehicle's serial number.
 * @property imgUri The link to the vehicle's image.
 */
@Entity(tableName = "personal_drones")
class Dronedata {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private var _id: Int = 0

    @ColumnInfo(name = "name")
    private var _name: String = ""

    @ColumnInfo(name = "vehicle")
    private var _vehicle: String = ""

    @ColumnInfo(name = "provider")
    private var _provider: String = ""

    @ColumnInfo(name = "color")
    private var _color: String = ""

    @ColumnInfo(name = "speed")
    private var _speed: String = ""

    @ColumnInfo(name = "weight")
    private var _weight: String = ""

    @ColumnInfo(name = "battery")
    private var _battery: String = ""

    @ColumnInfo(name = "energy")
    private var _energy: String = ""

    @ColumnInfo(name = "capacity")
    private var _capacity: String = ""

    @ColumnInfo(name = "operator")
    private var _operator: String = ""

    @ColumnInfo(name = "telephone")
    private var _telephone: String = ""

    @ColumnInfo(name = "serial")
    private var _serial: String = ""

    @ColumnInfo(name = "imgUri")
    private var _imgUri: String = ""

    /**
     * The unique identifier of the drone data.
     */
    var id: Int
        get() = _id
        set(value) {
            _id = value
        }

    /**
     * The name of the drone vehicle.
     */
    var name: String
        get() = _name
        set(value) {
            _name = value
        }

    /**
     * The type of aircraft.
     */
    var vehicle: String
        get() = _vehicle
        set(value) {
            _vehicle = value
        }

    /**
     * The provider of the aircraft.
     */
    var provider: String
        get() = _provider
        set(value) {
            _provider = value
        }

    /**
     * The color of the vehicle.
     */
    var color: String
        get() = _color
        set(value) {
            _color = value
        }

    /**
     * The speed of the vehicle.
     */
    var speed: String
        get() = _speed
        set(value) {
            _speed = value
        }

    /**
     * The weight of the vehicle.
     */
    var weight: String
        get() = _weight
        set(value) {
            _weight = value
        }

    /**
     * The type of battery of the vehicle.
     */
    var battery: String
        get() = _battery
        set(value) {
            _battery = value
        }

    /**
     * The power of the battery in Wh.
     */
    var energy: String
        get() = _energy
        set(value) {
            _energy = value
        }

    /**
     * The capacity of the battery in mAh.
     */
    var capacity: String
        get() = _capacity
        set(value) {
            _capacity = value
        }

    /**
     * The operator of the identification plate.
     */
    var operator: String
        get() = _operator
        set(value) {
            _operator = value
        }

    /**
     * The operator's phone number.
     */
    var telephone: String
        get() = _telephone
        set(value) {
            _telephone = value
        }

    /**
     * The vehicle's serial number.
     */
    var serial: String
        get() = _serial
        set(value) {
            _serial = value
        }

    /**
     * The link to the vehicle's image.
     */
    var imgUri: String
        get() = _imgUri
        set(value) {
            _imgUri = value
        }

    /**
     * The icon representing the drone vehicle based on its type.
     */
    val icon: Int
        get() = when (vehicle) {
            "UAV Multirrotor" -> R.drawable.ic_drone
            "Helicóptero" -> R.drawable.ic_helicopter
            "Aeromodelo" -> R.drawable.ic_plane
            "Ala" -> R.drawable.ic_wing
            else -> R.drawable.ic_arrow_down
        }

    /**
     * Checks if any of the drone data properties is not blank.
     */
    fun isNotBlank(): Boolean {
        return name.isNotBlank() ||
                vehicle.isNotBlank() ||
                provider.isNotBlank() ||
                color.isNotBlank() ||
                speed.isNotBlank() ||
                weight.isNotBlank() ||
                battery.isNotBlank() ||
                energy.isNotBlank() ||
                capacity.isNotBlank() ||
                operator.isNotBlank() ||
                telephone.isNotBlank() ||
                serial.isNotBlank() ||
                imgUri.isNotBlank()
    }
}

