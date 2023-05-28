package com.example.uavscoutproject.mainscreen.home.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personal_drones")
class Dronedata {
    @PrimaryKey(autoGenerate = true) private var _id: Int = 0
    @ColumnInfo(name = "name") private var _name = ""          //Nombre de vehiculo
    @ColumnInfo(name = "vehicle") private var _vehicle = ""       //Tipo de aeronave
    @ColumnInfo(name = "provider") private var _provider = ""      //Proveedor de la aeronave
    @ColumnInfo(name = "color") private var _color = ""         //Color del vehiculo
    @ColumnInfo(name = "speed") private var _speed = ""         //Velocidad del vehiculo
    @ColumnInfo(name = "weight") private var _weight = ""        //Peso del vehiculo
    @ColumnInfo(name = "battery") private var _battery = ""       //Tipo de bateria del vehiculo (Lipo 3S,2S,1S, Li-ion ,etc)
    @ColumnInfo(name = "energy") private var _energy = ""        //Potencia de la bateria en Wh
    @ColumnInfo(name = "capacity") private var _capacity = ""      //Capacidad de la bateria en mAh
    @ColumnInfo(name = "operator") private var _operator = ""      //Operadora de la chapa de identificación
    @ColumnInfo(name = "telephone") private var _telephone = ""     //Teléfono de la operadora
    @ColumnInfo(name = "serial") private var _serial = ""        //Número de serie del vehiculo
    @ColumnInfo(name = "imgUri") private var _imgUri = ""        //Enlace a la imagen del vehiculo

    var id: Int
        get() = _id
        set(value) {
            _id = value
        }

    var name: String
        get() = _name
        set(value) {
            _name = value
        }

    var vehicle: String
        get() = _vehicle
        set(value) {
            _vehicle = value
        }

    var provider: String
        get() = _provider
        set(value) {
            _provider = value
        }

    var color: String
        get() = _color
        set(value) {
            _color = value
        }

    var speed: String
        get() = _speed
        set(value) {
            _speed = value
        }

    var weight: String
        get() = _weight
        set(value) {
            _weight = value
        }

    var battery: String
        get() = _battery
        set(value) {
            _battery = value
        }

    var energy: String
        get() = _energy
        set(value) {
            _energy = value
        }

    var capacity: String
        get() = _capacity
        set(value) {
            _capacity = value
        }

    var operator: String
        get() = _operator
        set(value) {
            _operator = value
        }

    var telephone: String
        get() = _telephone
        set(value) {
            _telephone = value
        }

    var serial: String
        get() = _serial
        set(value) {
            _serial = value
        }

    var imgUri: String
        get() = _imgUri
        set(value) {
            _imgUri = value
        }


    fun isNotBlank(): Boolean{
        return  name.isNotBlank() ||
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

