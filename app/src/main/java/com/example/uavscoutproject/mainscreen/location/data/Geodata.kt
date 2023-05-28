package com.example.uavscoutproject.mainscreen.location.data

data class GeocodeResponse(
    val items: List<GeocodeItem>
)

data class GeocodeItem(
    val title: String,
    val position: Position
)

data class Position(
    val lat: Double,
    val lng: Double
)