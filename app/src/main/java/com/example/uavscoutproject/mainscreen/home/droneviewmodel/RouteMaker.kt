package com.example.uavscoutproject.mainscreen.home.droneviewmodel

import com.example.uavscoutproject.mainscreen.home.data.LastRoute
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem


object RouteMaker {
    private val lastRoute = LastRoute()
    fun setRoute(lastroute: List<GeocodeItem>) {
        lastRoute.route = lastroute
    }

    fun getRoute(): List<GeocodeItem> {
        return lastRoute.route
    }

    fun setDistance(lastdistance: Double) {
        lastRoute.distance = lastdistance
    }

    fun getDistance(): Double {
        return lastRoute.distance
    }

    fun setTime(lastime: Int) {
        lastRoute.time = lastime
    }

    fun getTime(): Int {
        return lastRoute.time
    }

    fun setWeather(lastweather: String) {
        lastRoute.weather = lastweather
    }

    fun getWeather(): String {
        return lastRoute.weather
    }
}