package com.example.uavscoutproject.mainscreen.home.droneviewmodel

import com.example.uavscoutproject.mainscreen.home.data.LastRoute
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem


/**
 * The RouteMaker object is responsible for managing route-related information.
 */
object RouteMaker {
    private val lastRoute = LastRoute()

    /**
     * Sets the route based on the given list of geocode items.
     *
     * @param lastroute The list of geocode items representing the route.
     */
    fun setRoute(lastroute: List<GeocodeItem>) {
        lastRoute.route = lastroute
    }

    /**
     * Returns the route as a list of geocode items.
     *
     * @return The list of geocode items representing the route.
     */
    fun getRoute(): List<GeocodeItem> {
        return lastRoute.route
    }

    /**
     * Sets the distance of the route.
     *
     * @param lastdistance The distance of the route.
     */
    fun setDistance(lastdistance: Double) {
        lastRoute.distance = lastdistance
    }

    /**
     * Returns the distance of the route.
     *
     * @return The distance of the route.
     */
    fun getDistance(): Double {
        return lastRoute.distance
    }

    /**
     * Sets the time of the route.
     *
     * @param lastime The time of the route.
     */
    fun setTime(lastime: Int) {
        lastRoute.time = lastime
    }

    /**
     * Returns the time of the route.
     *
     * @return The time of the route.
     */
    fun getTime(): Int {
        return lastRoute.time
    }

    /**
     * Sets the weather information for the route.
     *
     * @param lastweather The weather information for the route.
     */
    fun setWeather(lastweather: String) {
        lastRoute.weather = lastweather
    }

    /**
     * Returns the weather information for the route.
     *
     * @return The weather information for the route.
     */
    fun getWeather(): String {
        return lastRoute.weather
    }
}
