package com.example.uavscoutproject.mainscreen.location.data

data class AirMapRulesResponse(
    val status: String,
    val data: AirMapRulesData
)

data class AirMapRulesData(
    val name: String,
    val description: String,
    val airspace_types: List<String>,
    val rules: List<Rule>
)

data class Rule(
    val description: String,
)