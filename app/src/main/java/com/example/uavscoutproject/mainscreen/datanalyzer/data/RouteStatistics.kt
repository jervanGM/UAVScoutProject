package com.example.uavscoutproject.mainscreen.datanalyzer.data

data class RouteStatistics(
    val totalDistance: Double,
    val totalConsumption: Int,
    val averageSpeed: Int,
    val flightDuration: Int,
    val minAltitude: Double,
    val maxAltitude: Double,
    val routeEvaluation: Triple<Int, Int, String>
)