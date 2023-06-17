package com.example.uavscoutproject.mainscreen.location.data

/**
 * Represents the response received from the AirMap Rules API.
 * @property status The status of the response.
 * @property data The data returned in the response.
 */
data class AirMapRulesResponse(
    val status: String,
    val data: AirMapRulesData
)

/**
 * Represents the data received from the AirMap Rules API.
 * @property name The name of the rules data.
 * @property description The description of the rules data.
 * @property airspace_types The list of airspace types associated with the rules data.
 * @property rules The list of rules associated with the rules data.
 */
data class AirMapRulesData(
    val name: String,
    val description: String,
    val airspace_types: List<String>,
    val rules: List<Rule>
)

/**
 * Represents a rule.
 * @property description The description of the rule.
 */
data class Rule(
    val description: String
)
