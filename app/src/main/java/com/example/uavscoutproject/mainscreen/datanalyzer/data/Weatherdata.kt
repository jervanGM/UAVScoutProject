package com.example.uavscoutproject.mainscreen.datanalyzer.data

import com.example.uavscoutproject.R

/**
 * Represents the weather response.
 *
 * @property elevation The elevation.
 * @property hourly The hourly data.
 */
data class WeatherResponse(
    val elevation: Double,
    val hourly: HourlyData
)

/**
 * Represents the hourly weather data.
 *
 * @property id The ID.
 * @property time The list of time values.
 * @property temperature_2m The list of 2-meter temperatures.
 * @property relativehumidity_2m The list of relative humidity values at 2 meters.
 * @property precipitation_probability The list of precipitation probability values.
 * @property weathercode The list of weather code values.
 * @property surface_pressure The list of surface pressure values.
 * @property visibility The list of visibility values.
 * @property windspeed_10m The list of 10-meter wind speed values.
 * @property winddirection_10m The list of 10-meter wind direction values.
 * @property is_day The list of is_day values.
 */
data class HourlyData(
    var id: String = "",
    val time: List<String> = listOf("0"),
    val temperature_2m: List<Double> = listOf(0.0),
    val relativehumidity_2m: List<Int> = listOf(0),
    val precipitation_probability: List<Int> = listOf(0),
    val weathercode: List<Int> = listOf(0),
    val surface_pressure: List<Double> = listOf(0.0),
    val visibility: List<Double> = listOf(0.0),
    val windspeed_10m: List<Double> = listOf(0.0),
    val winddirection_10m: List<Double> = listOf(0.0),
    val is_day: List<Double> = listOf(0.0)
) {
    /**
     * Returns the weather icon for the given index.
     *
     * @param index The index of the weather data.
     * @return The resource ID of the weather icon.
     */
    fun getWeatherIcon(index: Int): Int {
        val weatherCode = weathercode[index]
        val isDay = is_day[index] == 1.0
        val weatherIcon = when {
            weatherCode == 0 ->
                if (isDay) R.drawable.ic_sunny
                else R.drawable.ic_night
            (weatherCode in setOf(1, 2, 3)) ->
                if (isDay) R.drawable.ic_cloudy
                else R.drawable.ic_night_cloudy
            (weatherCode in setOf(45, 48)) -> {
                if (isDay) R.drawable.ic_fog
                else R.drawable.ic_fog_night
            }
            (weatherCode in setOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82)) ->
                R.drawable.ic_rainy
            (weatherCode in setOf(77, 85, 86)) -> R.drawable.ic_snow
            (weatherCode in setOf(95, 96, 99)) -> R.drawable.ic_thunderstorm
            else -> R.drawable.ic_wind
        }
        return weatherIcon
    }

    /**
     * Returns the wind direction for the given index.
     *
     * @param index The index of the weather data.
     * @return The wind direction.
     */
    fun getWindDirection(index: Int): String {
        val windDegrees = winddirection_10m[index]
        val range = 22.5
        val rangeVar = 22.49
        val direction = when {
            (windDegrees in (360 - rangeVar)..(0 + range)) -> "E"
            (windDegrees in (45 - rangeVar)..(45 + range)) -> "NE"
            (windDegrees in (90 - rangeVar)..(90 + range)) -> "N"
            (windDegrees in (135 - rangeVar)..(135 + range)) -> "NW"
            (windDegrees in (180 - rangeVar)..(180 + range)) -> "W"
            (windDegrees in (225 - rangeVar)..(225 + range)) -> "SW"
            (windDegrees in (270 - rangeVar)..(270 + range)) -> "S"
            (windDegrees in (315 - rangeVar)..(315 + range)) -> "SE"
            else -> "ERROR"
        }
        return direction
    }

    /**
     * Evaluates the flight possibility based on the weather conditions at the given index.
     *
     * @param index The index of the weather data.
     * @return A triple containing the color, warning icon, and flight possibility message.
     */
    fun evaluateFlightPossibility(index: Int): Triple<Int, Int, String> {
        // Acceptable ranges for each weather condition
        val temperatureRange = 5.0..35.0
        val relativeHumidityRange = 20..60
        val precipitationProbabilityRange = 0..50
        val surfacePressureRange = 0.9..1.1
        val visibilityRange = 5.0..Double.MAX_VALUE
        val windSpeedRange = 0.0..30.0
        // Initial score
        var totalScore = 0

        // Evaluate temperature
        if (temperature_2m[index] in temperatureRange) {
            totalScore += 2
        } else {
            //Is not adding points
        }

        // Evaluate relative humidity
        if (relativehumidity_2m[index] in relativeHumidityRange) {
            totalScore += 2
        } else {
            totalScore -= 1
        }

        // Evaluate precipitation probability
        if (precipitation_probability[index] in precipitationProbabilityRange) {
            totalScore += 2
        } else {
            totalScore -= 1
        }

        // Evaluate weather code
        totalScore += when (weathercode[index]) {
            in listOf(0, 1, 2, 3) -> 2
            in listOf(45, 48, 51, 53, 55, 56, 57) -> 1
            else -> 0
        }

        // Evaluate surface pressure
        if (surface_pressure[index] in surfacePressureRange) {
            totalScore += 2
        } else {
            totalScore += 0
        }

        // Evaluate visibility
        if (visibility[index] >= visibilityRange.start) {
            totalScore += 2
        } else {
            totalScore += 0
        }

        // Evaluate wind speed
        if (windspeed_10m[index] in windSpeedRange) {
            totalScore += 2
        } else {
            totalScore += 0
        }

        // Final evaluation of the total score
        return when {
            totalScore >= 8 -> Triple(
                android.graphics.Color.parseColor("#66FBB0"),
                R.drawable.ic_void,
                "Clima estable.\nSe puede volar respetando la normativa vigente."
            )
            totalScore in 4..7 -> Triple(
                android.graphics.Color.parseColor("#FDAB48"),
                R.drawable.ic_warning,
                "Clima moderadamente inestable.\nSe recomienda volar con precaución y en altitudes bajas."
            )
            else -> Triple(
                android.graphics.Color.parseColor("#FF6347"),
                R.drawable.ic_danger,
                "Clima inestable.\nNo se recomienda volar en la zona bajo ningún concepto."
            )
        }
    }

    /**
     * Returns the start and end index of the weather data based on the selected option.
     *
     * @param option The selected option ("Hoy", "Mañana", or "Otros").
     * @return A pair containing the start and end index of the weather data.
     */
    fun getDayWeatherData(option: String): Pair<Int, Int> {
        val startIndex = when {
            option == "Hoy" -> 0
            option == "Mañana" -> 24
            else -> 48
        }
        val endIndex = when {
            option == "Hoy" -> 23
            option == "Mañana" -> 47
            else -> 71
        }
        return Pair(startIndex, endIndex)
    }
}


