package com.example.uavscoutproject.mainscreen.datanalyzer.viewmodel

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.uavscoutproject.R
import com.example.uavscoutproject.mainscreen.datanalyzer.data.HourlyData
import com.example.uavscoutproject.mainscreen.datanalyzer.data.RouteStatistics
import com.example.uavscoutproject.mainscreen.datanalyzer.weatherapi.WeatherApiService
import com.example.uavscoutproject.mainscreen.home.data.Dronedata
import com.example.uavscoutproject.mainscreen.home.droneviewmodel.RouteMaker
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem
import com.example.uavscoutproject.mainscreen.location.data.Position
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

/**
 * ViewModel class for handling data related to weather and sensor readings.
 *
 * @param application The application context.
 */
class DataViewModel(application: Application) : AndroidViewModel(application) {
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var _temperaturaState: MutableLiveData<Float> = MutableLiveData(0f)
    private val _humedadState: MutableLiveData<Float> = MutableLiveData(0f)
    private val _presionState: MutableLiveData<Float> = MutableLiveData(0f)
    private val _iluminacionState: MutableLiveData<Float> = MutableLiveData(0f)

    var firstlocation = GeocodeItem("", Position(null,null))
    var lastlocation = GeocodeItem("", Position(null,null))
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val weatherApiService = retrofit.create(WeatherApiService::class.java)
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    /**
     * Saves weather data to Firestore.
     *
     * @param cloudSave Determines if the data should be saved to the cloud.
     */
    fun saveWeatherData(cloudSave: Boolean) {
        if (cloudSave) {
            val weatherdata = DataMaker.getData()
            val collection = firestore.collection("weatherdata")
            val document = if (weatherdata.id.isEmpty()) {
                collection.document()
            } else {
                collection.document(weatherdata.id)
            }
            weatherdata.id = document.id
            val handle = document.set(weatherdata)
            handle.addOnSuccessListener {
                Log.d("Firebase", "Document saved")
            }
            handle.addOnFailureListener {
                Log.d("Firebase", "Save failed $it ")
            }
        }
    }

    /**
     * Saves sensor data to Firestore.
     *
     * @param cloudSave Determines if the data should be saved to the cloud.
     */
    fun saveSensorData(cloudSave: Boolean) {
        if (cloudSave) {
            val sensorsList = mapOf(
                "temperaturaState" to _temperaturaState.value,
                "humedadState" to _humedadState.value,
                "presionState" to _presionState.value,
                "iluminacionState" to _iluminacionState.value
            )
            val document = firestore.collection("sensordata").document()

            val handle = document.set(sensorsList)
            handle.addOnSuccessListener {
                Log.d("Firebase", "Document saved")
            }
            handle.addOnFailureListener {
                Log.d("Firebase", "Save failed $it ")
            }
        }
    }

    /**
     * Fetches hourly weather data for the specified [locations].
     * @param locations The list of geocode items representing the locations for which to fetch weather data.
     */
    fun fetchHourlyWeatherData(locations: List<GeocodeItem>) {
        val hourlyFields = "temperature_2m,relativehumidity_2m,precipitation_probability," +
                "weathercode,surface_pressure,visibility,windspeed_10m,winddirection_10m,is_day"
        val forecastDays = 3
        val hourlyDataList = mutableListOf<HourlyData>()
        if (locations.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    firstlocation = locations.first()
                    lastlocation = locations.last()
                    for (location in locations) {
                        val latitude = location.position.lat
                        val longitude = location.position.lng
                        val response = weatherApiService.weather(
                            latitude!!,
                            longitude!!,
                            hourlyFields,
                            forecastDays
                        )
                        location.elevation = response.elevation
                        hourlyDataList.add(response.hourly)
                    }
                    calculateAverage(hourlyDataList, hourlyDataList[0].time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Calculates the average values of various weather parameters based on the given hourly data.
     *
     * @param hourlyDataList The list of hourly data.
     * @param time The list of time values corresponding to the hourly data.
     */
    private fun calculateAverage(hourlyDataList: List<HourlyData>, time: List<String>) {
        val df = DecimalFormat("#.##")

        // Convert the date-time strings to HH:mm format
        val hourList = time.map { dateTime ->
            val dateTimeObj = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
            dateTimeObj.format(DateTimeFormatter.ofPattern("HH:mm"))
        }

        // Calculate the average temperature
        val averageTemperature = hourlyDataList.map { it.temperature_2m }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }

        // Calculate the average relative humidity
        val averageRelativeHumidity = hourlyDataList.map { it.relativehumidity_2m }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }

        // Calculate the average precipitation probability
        val averagePrecipitationProbability = hourlyDataList.map { it.precipitation_probability }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }

        // Calculate the average surface pressure
        val averageSurfacePressure = hourlyDataList.map { it.surface_pressure }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }

        // Calculate the average visibility
        val averageVisibility = hourlyDataList.map { it.visibility }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }

        // Calculate the average wind speed
        val averageWindSpeed = hourlyDataList.map { it.windspeed_10m }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }

        // Calculate the average wind direction
        val averageWindDirection = hourlyDataList.map { it.winddirection_10m }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }

        // Calculate the average is_day value
        val averageIsDay = hourlyDataList.map { it.is_day }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }

        // Create a new HourlyData object with the average values
        val weatherData = HourlyData(
            time = hourList,
            temperature_2m = averageTemperature
                .map { df.format(it / hourlyDataList.size).toDouble() }, // Celsius
            relativehumidity_2m = averageRelativeHumidity
                .map { it / hourlyDataList.size },  // Percentage
            precipitation_probability = averagePrecipitationProbability
                .map { it / hourlyDataList.size },  // Percentage
            weathercode = hourlyDataList[0].weathercode,  // WMO code
            surface_pressure = averageSurfacePressure
                .map { df.format(it / (hourlyDataList.size * 1000)).toDouble() }, // bar
            visibility = averageVisibility
                .map { df.format(it / (hourlyDataList.size * 1000)).toDouble() }, // Km
            windspeed_10m = averageWindSpeed
                .map { df.format(it / hourlyDataList.size).toDouble() }, // Km/h
            winddirection_10m = averageWindDirection
                .map { df.format(it / hourlyDataList.size).toDouble() }, // degrees
            is_day = averageIsDay
                .map { df.format(it / hourlyDataList.size).toDouble() } // day(1), night(0)
        )

        DataMaker.setWeatherData(weatherData)
    }

    /**
     * Retrieves the weather value.
     *
     * @return The weather data.
     */
    fun getWeatherValue(): HourlyData {
        return DataMaker.getData()
    }

    /**
     * Retrieves the temperature value as a LiveData object.
     *
     * @return The temperature value.
     */
    fun getTemperatureValue(): LiveData<Float?> {
        return _temperaturaState
    }

    /**
     * Retrieves the color corresponding to the temperature value.
     *
     * @return The color code.
     */
    fun getTemperatureColor(): String {
        return when {
            _temperaturaState.value!! < 5f -> "#00BFFF"
            _temperaturaState.value!! < 15f -> "#1E90FF"
            _temperaturaState.value!! < 27f -> "#4169E1"
            _temperaturaState.value!! < 35f -> "#FF6347"
            else -> "#FF0000"
        }
    }

    /**
     * Retrieves the humidity value as a LiveData object.
     *
     * @return The humidity value.
     */
    fun getHumidityValue(): LiveData<Float> {
        return _humedadState
    }

    /**
     * Retrieves the color corresponding to the humidity value.
     *
     * @return The color code.
     */
    fun getHumidityColor(): String {
        return when {
            _humedadState.value!! < 5f -> "#00BFFF"
            _humedadState.value!! < 25f -> "#1E90FF"
            _humedadState.value!! < 50f -> "#2E8B57"
            _humedadState.value!! < 75f -> "#00FF7F"
            else -> "#7CFC00"
        }
    }

    /**
     * Retrieves the pressure value as a LiveData object.
     *
     * @return The pressure value.
     */
    fun getPressureValue(): LiveData<Float> {
        return _presionState
    }

    /**
     * Retrieves the color corresponding to the pressure value.
     *
     * @return The color code.
     */
    fun getPressureColor(): String {
        return when {
            _presionState.value!! < 0.1f -> "#00008B"
            _presionState.value!! < 0.8f -> "#0000FF"
            _presionState.value!! < 1.2f -> "#1E90FF"
            _presionState.value!! < 2f -> "#4169E1"
            else -> "#483D8B"
        }
    }

    /**
     * Retrieves the illumination value as a LiveData object.
     *
     * @return The illumination value.
     */
    fun getIlluminationValue(): LiveData<Float> {
        return _iluminacionState
    }

    /**
     * Retrieves the icon corresponding to the illumination value.
     *
     * @return The icon resource ID.
     */
    fun getIlluminationIcon(): Int {
        return when {
            _iluminacionState.value!! < 10f -> R.drawable.im_night
            _iluminacionState.value!! < 40f -> R.drawable.im_after
            else -> R.drawable.im_day
        }
    }

    /**
     * Retrieves the current date information.
     *
     * @return A map containing the day of the week and day of the month.
     */
    fun getCurrentDate(): Map<String, String> {
        val currentDate = Calendar.getInstance()
        // Define the ISO-8601 format
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Get the date in ISO-8601 format
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault()).format(currentDate.time)
        val dayOfMonthFormat = SimpleDateFormat("d", Locale.getDefault()).format(currentDate.time)

        return mapOf(
            "dayOfWeek" to dayOfWeekFormat,
            "dayOfMonth" to dayOfMonthFormat
        )
    }

    /**
     * Sensor listener for handling sensor events.
     */
    private val sensorListener = object : SensorEventListener {
        /**
         * Called when the accuracy of the sensor has changed.
         * @param sensor The sensor whose accuracy changed.
         * @param accuracy The new accuracy value.
         */
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Not used in this case
        }

        /**
         * Called when there is a new sensor event.
         * @param event The sensor event.
         */
        override fun onSensorChanged(event: SensorEvent) {
            val decimalFormat = DecimalFormat("#.##")
            when (event.sensor.type) {
                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    _temperaturaState.value = event.values[0]
                }
                Sensor.TYPE_RELATIVE_HUMIDITY -> {
                    _humedadState.value = event.values[0]
                }
                Sensor.TYPE_PRESSURE -> {
                    _presionState.value = decimalFormat.format(event.values[0] / 1000).toFloat()
                }
                Sensor.TYPE_LIGHT -> {
                    _iluminacionState.value = event.values[0] / 100
                }
            }
        }
    }

    /**
     * Starts listening for sensor updates.
     */
    fun startListening() {
        val temperaturaSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        val humedadSensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
        val presionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        val iluminacionSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        sensorManager.registerListener(
            sensorListener,
            temperaturaSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            sensorListener,
            humedadSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            sensorListener,
            presionSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            sensorListener,
            iluminacionSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    /**
     * Called when the ViewModel is being destroyed.
     * Stops listening for sensor updates.
     */
    override fun onCleared() {
        super.onCleared()
        stopListening()
    }

    /**
     * Stops listening for sensor updates.
     */
    private fun stopListening() {
        sensorManager.unregisterListener(sensorListener)
    }


    /**
     * Calculates the route statistics based on drone data, weather data, and the route.
     * @param droneData The drone data.
     * @param weatherData The weather data.
     * @param route The route.
     * @return The calculated route statistics.
     */
    fun calculateRouteStatistics(
        droneData: Dronedata,
        weatherData: HourlyData,
        route: List<GeocodeItem>
    ): RouteStatistics {
        // Calculations to obtain the desired parameters
        if (route.isNotEmpty()) {
            val totalDistance = calculateTotalDistance(route)
            val averageSpeed = calculateAverageSpeed(droneData, weatherData)
            val flightDuration = calculateFlighDuration(totalDistance, averageSpeed)
            val totalConsumption = calculateTotalConsumption(droneData, flightDuration)
            val minmaxAltitude = calculateMinMaxAltitude(route)
            val routeEvaluation = calculateRouteEvaluation(weatherData, droneData, totalConsumption)
            val distanceToKm = totalDistance / 1000
            val timeToMin = (flightDuration * 60).toInt()
            RouteMaker.setRoute(route)
            RouteMaker.setDistance(distanceToKm)
            RouteMaker.setTime(timeToMin)
            RouteMaker.setWeather(
                when (routeEvaluation.second) {
                    R.drawable.ic_void -> "Estable"
                    R.drawable.ic_warning -> "Moderado"
                    R.drawable.ic_danger -> "Dificil"
                    else -> "Desconocido"
                }
            )
            return RouteStatistics(
                totalDistance = distanceToKm,
                totalConsumption = totalConsumption.toInt(),
                averageSpeed = averageSpeed.toInt(),
                flightDuration = timeToMin,
                minAltitude = (minmaxAltitude.first / 1000),
                maxAltitude = (minmaxAltitude.second / 1000),
                routeEvaluation = routeEvaluation
            )
        } else {
            return RouteStatistics(
                0.0, 0,
                0, 0,
                0.0, 0.0,
                Triple(
                    android.graphics.Color.parseColor("#66FBB0"),
                    R.drawable.ic_void,
                    "No hay condiciones para volar"
                )
            )
        }
    }

    /**
     * Calculates the route evaluation based on weather data, drone data, and total consumption.
     * @param weatherData The weather data.
     * @param droneData The drone data.
     * @param totalConsumption The total consumption.
     * @return The calculated route evaluation.
     */
    private fun calculateRouteEvaluation(
        weatherData: HourlyData,
        droneData: Dronedata,
        totalConsumption: Double
    ): Triple<Int, Int, String> {
        var evaluator = 0.0
        val maxenergy = droneData.energy.filter { it.isDigit() }.toDoubleOrNull()
        if (maxenergy != null) {
            for (index in 0 until weatherData.temperature_2m.size) {
                val evaluation = weatherData.evaluateFlightPossibility(index)
                evaluator += when (evaluation.second) {
                    (R.drawable.ic_void) ->
                        if (maxenergy > totalConsumption) 1 else 3

                    R.drawable.ic_warning ->
                        if (maxenergy > totalConsumption) 2 else 3
                    R.drawable.ic_danger -> 3
                    else -> 0
                }
            }
        }
        val finalEvaluation = (evaluator / weatherData.temperature_2m.size).roundToInt()
        return when (finalEvaluation) {
            1 -> Triple(
                android.graphics.Color.parseColor("#66FBB0"),
                R.drawable.ic_void,
                "Las condiciones son optimas para volar"
            )
            2 -> Triple(
                android.graphics.Color.parseColor("#FDAB48"),
                R.drawable.ic_warning,
                "Las condiciones actuales son poco aptas para el vuelo"
            )
            3 -> Triple(
                android.graphics.Color.parseColor("#FF6347"),
                R.drawable.ic_danger,
                "Las condiciones actuales no son aptas para el vuelo"
            )
            else -> {
                Triple(
                    android.graphics.Color.parseColor("#66FBB0"),
                    R.drawable.ic_void,
                    "No hay condiciones para volar"
                )
            }
        }
    }

    /**
     * Calculates the minimum and maximum altitude from the route.
     * @param route The route.
     * @return The minimum and maximum altitude as a pair.
     */
    private fun calculateMinMaxAltitude(route: List<GeocodeItem>): Pair<Double, Double> {
        val minAltitude = route.map { it.elevation }.min()
        val maxAltitude = route.map { it.elevation }.max()
        return Pair(minAltitude, maxAltitude)
    }

    /**
     * Calculates the flight duration based on the total distance and average speed.
     * @param totalDistance The total distance.
     * @param averageSpeed The average speed.
     * @return The calculated flight duration.
     */
    private fun calculateFlighDuration(totalDistance: Double, averageSpeed: Double): Double {
        return (totalDistance / (1000 * averageSpeed))
    }

    /**
     * Calculates the total distance of the route.
     * @param route The route.
     * @return The calculated total distance.
     */
    private fun calculateTotalDistance(route: List<GeocodeItem>): Double {
        return route.map { it.distance.toDouble() }.sum()
    }

    /**
     * Calculates the total consumption based on drone data and flight duration.
     * @param droneData The drone data.
     * @param flightDuration The flight duration.
     * @return The calculated total consumption.
     */
    private fun calculateTotalConsumption(droneData: Dronedata, flightDuration: Double): Double {
        val batteryConsumption = droneData.energy.filter { it.isDigit() }.toDoubleOrNull()
        var consumption = 0.0
        if (batteryConsumption != null) {
            consumption = batteryConsumption * flightDuration
        }
        return consumption
    }

    /**
     * Calculates the average speed based on drone data and weather data.
     * @param droneData The drone data.
     * @param weatherData The weather data.
     * @return The calculated average speed.
     */
    fun calculateAverageSpeed(droneData: Dronedata, weatherData: HourlyData): Double {
        val maxSpeed = droneData.speed.filter { it.isDigit() }.toDoubleOrNull()
        val weatherSpeed = calculateWeatherAdjustedSpeed(weatherData)
        var averageSpeed = 0.0
        // Calculate the average speed considering the maximum speed and weather-adjusted speed
        if (maxSpeed != null) {
            averageSpeed = (maxSpeed + weatherSpeed) / 2
        } else {
            // The string does not contain a valid number
            // Handle the error case according to your needs
        }
        return averageSpeed
    }

    /**
     * Calculates the weather-adjusted speed based on weather data.
     * @param weatherData The weather data.
     * @return The calculated weather-adjusted speed.
     */
    private fun calculateWeatherAdjustedSpeed(weatherData: HourlyData): Double {
        // Logic to adjust the speed based on weather conditions
        // You can use the weatherData to calculate the adjustment based on the weather

        // Example of simple adjustment:
        val precipitationProbability = weatherData.precipitation_probability.average()
        val weatherSpeedAdjustment = when {
            precipitationProbability > 50 -> -2.0
            weatherData.windspeed_10m.average() > 10.0 -> -1.0
            else -> 0.0
        }

        // Return the weather-adjusted speed
        return weatherSpeedAdjustment
    }

}




