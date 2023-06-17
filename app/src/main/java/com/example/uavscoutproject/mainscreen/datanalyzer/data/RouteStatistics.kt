package com.example.uavscoutproject.mainscreen.datanalyzer.data


/**
 * Represents the statistics of a flight route.
 *
 * @property totalDistance The total distance of the flight route in kilometers.
 * @property totalConsumption The total fuel consumption of the flight route in liters.
 * @property averageSpeed The average speed of the flight route in kilometers per hour.
 * @property flightDuration The duration of the flight route in minutes.
 * @property minAltitude The minimum altitude reached during the flight route in meters.
 * @property maxAltitude The maximum altitude reached during the flight route in meters.
 * @property routeEvaluation A triple containing the color, warning icon, and evaluation message for the flight route.
 */
data class RouteStatistics(
    val totalDistance: Double,
    val totalConsumption: Int,
    val averageSpeed: Int,
    val flightDuration: Int,
    val minAltitude: Double,
    val maxAltitude: Double,
    val routeEvaluation: Triple<Int, Int, String>
)
