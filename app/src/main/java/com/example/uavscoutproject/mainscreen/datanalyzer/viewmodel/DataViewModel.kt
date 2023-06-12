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


    fun saveWeatherData(cloudSave: Boolean) {
        if(cloudSave) {
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


    fun saveSensorData(cloudSave: Boolean){
        if(cloudSave) {
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

    fun fetchHourlyWeatherData(locations: List<GeocodeItem>) {
        val hourlyFields = "temperature_2m,relativehumidity_2m,precipitation_probability," +
                "weathercode,surface_pressure,visibility,windspeed_10m,winddirection_10m,is_day"
        val forecastDays = 3
        val hourlyDataList = mutableListOf<HourlyData>()
        if(locations.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    firstlocation = locations.first()
                    lastlocation = locations.last()
                    for (location in locations) {
                        val latitude = location.position.lat
                        val longitude = location.position.lng
                        val response =
                            weatherApiService.weather(
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

    private fun calculateAverage(hourlyDataList: List<HourlyData>, time: List<String>) {
        val df = DecimalFormat("#.##")

        val hourList = time.map { dateTime ->
            val dateTimeObj = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME)
            dateTimeObj.format(DateTimeFormatter.ofPattern("HH:mm"))
        }
        val averageTemperature = hourlyDataList.map { it.temperature_2m }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }
        val averageRelativeHumidity = hourlyDataList.map { it.relativehumidity_2m }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }
        val averagePrecipitationProbability = hourlyDataList.map { it.precipitation_probability }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }
        val averageSurfacePressure = hourlyDataList.map { it.surface_pressure }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }
        val averageVisibility = hourlyDataList.map { it.visibility }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }
        val averageWindSpeed = hourlyDataList.map { it.windspeed_10m }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }
        val averageWindDirection = hourlyDataList.map { it.winddirection_10m }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }
        val averageIsDay = hourlyDataList.map { it.is_day }
            .reduce { acc, list -> acc.zip(list) { a, b -> a + b } }

        val weatherdata = HourlyData(
            time = hourList,
            temperature_2m = averageTemperature
                .map { df.format(it/hourlyDataList.size).toDouble() }, //Celsius
            relativehumidity_2m = averageRelativeHumidity
                .map { it/hourlyDataList.size },  //%
            precipitation_probability = averagePrecipitationProbability
                .map { it/hourlyDataList.size },  //%
            weathercode = hourlyDataList[0].weathercode,  //WMO code
            surface_pressure = averageSurfacePressure
                .map { df.format(it/(hourlyDataList.size*1000)).toDouble() }, //bar
            visibility = averageVisibility
                .map { df.format(it/(hourlyDataList.size*1000)).toDouble() }, //Km
            windspeed_10m = averageWindSpeed
                .map { df.format(it/hourlyDataList.size).toDouble() }, //Km/h
            winddirection_10m = averageWindDirection
                .map { df.format(it/hourlyDataList.size).toDouble() }, //degrees
            is_day = averageIsDay
                .map { df.format(it/hourlyDataList.size).toDouble() } //day(1), night(0)
        )
        DataMaker.setWeatherData(weatherdata)

    }
    fun getWeatherValue() : HourlyData
    {
        return DataMaker.getData()
    }

    fun getTemperatureValue(): LiveData<Float?> {
        return _temperaturaState
    }

    fun getTemperatureColor(): String {
        return when {
            _temperaturaState.value!! < 5f ->  "#00BFFF"
            _temperaturaState.value!! < 15f -> "#1E90FF"
            _temperaturaState.value!! < 27f -> "#4169E1"
            _temperaturaState.value!! < 35f -> "#FF6347"
            else -> "#FF0000"
        }
    }

    fun getHumidityValue() : LiveData<Float>
    {
        return _humedadState
    }

    fun getHumidityColor(): String {
        return when {
            _humedadState.value!! < 5f ->  "#00BFFF"
            _humedadState.value!! < 25f -> "#1E90FF"
            _humedadState.value!! < 50f -> "#2E8B57"
            _humedadState.value!! < 75f -> "#00FF7F"
            else -> "#7CFC00"
        }
    }

    fun getPressureValue() : LiveData<Float>
    {
        return _presionState
    }

    fun getPressureColor(): String {
        return when {
            _presionState.value!! < 0.1f ->  "#00008B"
            _presionState.value!! < 0.8f -> "#0000FF"
            _presionState.value!! < 1.2f -> "#1E90FF"
            _presionState.value!! < 2f -> "#4169E1"
            else -> "#483D8B"
        }
    }
    fun getIlluminationValue() : LiveData<Float>
    {
        return _iluminacionState
    }
    fun getIlluminationIcon(): Int{
        return when {
            _iluminacionState.value!! < 10f ->  R.drawable.im_night
            _iluminacionState.value!! < 40f -> R.drawable.im_after
            else -> R.drawable.im_day
        }
    }

    fun getCurrentDate(): Map<String, String>{
        val currentDate = Calendar.getInstance()
        // Definir el formato ISO-8601
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Obtener la fecha en formato ISO-8601
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault()).format(currentDate.time)
        val dayOfMonthFormat = SimpleDateFormat("d", Locale.getDefault()).format(currentDate.time)
        return mapOf(
            "dayOfWeek" to dayOfWeekFormat,
            "dayOfMonth" to dayOfMonthFormat
        )
    }

    private val sensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // No se utiliza en este caso
        }

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
                    _presionState.value = decimalFormat
                        .format(event.values[0] / 1000)
                        .toFloat()
                }
                Sensor.TYPE_LIGHT -> {
                    _iluminacionState.value = event.values[0]/100
                }
            }

        }
    }

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

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }

    private fun stopListening() {
        sensorManager.unregisterListener(sensorListener)
    }

    fun calculateRouteStatistics(droneData: Dronedata, weatherData: HourlyData, route: List<GeocodeItem>): RouteStatistics {
        // Cálculos para obtener los parámetros deseados
        if(route.isNotEmpty()) {
            val totalDistance = calculateTotalDistance(route)
            val averageSpeed = calculateAverageSpeed(droneData, weatherData)
            val flightDuration = calculateFlighDuration(totalDistance, averageSpeed)
            val totalConsumption = calculateTotalConsumption(droneData, flightDuration)
            val minmaxAltitude = calculateMinMaxAltitude(route)
            val routeEvaluation = calculateRouteEvaluation(weatherData,droneData,totalConsumption)
            val distanceToKm = totalDistance/1000
            val timeToMin = (flightDuration * 60).toInt()
            RouteMaker.setRoute(route)
            RouteMaker.setDistance(distanceToKm)
            RouteMaker.setTime(timeToMin)
            RouteMaker.setWeather(when(routeEvaluation.second){
                R.drawable.ic_void -> "Estable"
                R.drawable.ic_warning -> "Moderado"
                R.drawable.ic_danger -> "Dificil"
                else -> "Desconocido"
            })
            return RouteStatistics(
                totalDistance = distanceToKm,
                totalConsumption = totalConsumption.toInt(),
                averageSpeed = averageSpeed.toInt(),
                flightDuration = timeToMin,
                minAltitude = (minmaxAltitude.first / 1000),
                maxAltitude = (minmaxAltitude.second / 1000),
                routeEvaluation = routeEvaluation
            )
        }
        else{
            return RouteStatistics(
                0.0, 0,
                0, 0,
                0.0, 0.0,
                Triple(android.graphics.Color.parseColor("#66FBB0"),
                    R.drawable.ic_void,"No hay condiciones para volar")
            )
        }
    }

    private fun calculateRouteEvaluation(
        weatherData: HourlyData,
        droneData: Dronedata,
        totalConsumption: Double
    ): Triple<Int, Int, String>  {
            var evaluator = 0.0
            val maxenergy = droneData.energy.filter { it.isDigit() }.toDoubleOrNull()
        if (maxenergy != null) {
            for (index in 0 until weatherData.temperature_2m.size){
                val evaluation = weatherData.evaluateFlightPossibility(index)
                evaluator += when (evaluation.second){
                    (R.drawable.ic_void) ->
                        if(maxenergy>totalConsumption) 1 else 3

                    R.drawable.ic_warning ->
                        if(maxenergy>totalConsumption) 2 else 3
                    R.drawable.ic_danger  -> 3
                    else -> 0
                }
            }
        }
        val finalEvaluation = (evaluator/weatherData.temperature_2m.size).roundToInt()
        return when(finalEvaluation){
            1 -> Triple(android.graphics.Color.parseColor("#66FBB0"),
                R.drawable.ic_void,"Las condiciones son optimas para volar")
            2 -> Triple(android.graphics.Color.parseColor("#FDAB48"),
                R.drawable.ic_warning,"Las condiciones actuales son poco aptas para el vuelo")
            3-> Triple(android.graphics.Color.parseColor("#FF6347"),
                R.drawable.ic_danger,"Las condiciones actuales no son aptas para el vuelo")
            else -> {
                Triple(android.graphics.Color.parseColor("#66FBB0"),
                R.drawable.ic_void,"No hay condiciones para volar")
            }
        }
    }

    private fun calculateMinMaxAltitude(route: List<GeocodeItem>): Pair<Double,Double> {
            val minAltitude = route.map { it.elevation }.min()
            val maxAltitude = route.map { it.elevation }.max()
        return Pair(minAltitude,maxAltitude)
    }

    private fun calculateFlighDuration(totalDistance: Double, averageSpeed: Double): Double {
            return (totalDistance /(1000*averageSpeed))
    }

    private fun calculateTotalDistance(route: List<GeocodeItem>): Double {
          return  route.map { it.distance.toDouble() }.sum()
    }

    private fun calculateTotalConsumption(droneData: Dronedata, flightDuration: Double): Double {
        val batteryConsumption = droneData.energy.filter { it.isDigit() }.toDoubleOrNull()
        var consumption = 0.0
        if(batteryConsumption !=null){
            consumption = batteryConsumption*flightDuration
        }
        return consumption
    }

    fun calculateAverageSpeed(droneData: Dronedata, weatherData: HourlyData): Double {
        val maxSpeed = droneData.speed.filter { it.isDigit() }.toDoubleOrNull()
        val weatherSpeed = calculateWeatherAdjustedSpeed(weatherData)
        var averageSpeed = 0.0
        // Calcular la velocidad media teniendo en cuenta la velocidad máxima y la velocidad ajustada por el clima
        if (maxSpeed != null) {
            averageSpeed = (maxSpeed + weatherSpeed) / 2
        } else {
            // La cadena no contiene un número válido
            // Maneja el caso de error según tus necesidades
        }
        return averageSpeed
    }

    private fun calculateWeatherAdjustedSpeed(weatherData: HourlyData): Double {
        // Lógica para ajustar la velocidad en base a las condiciones climáticas
        // Puedes utilizar los datos de weatherData para calcular el ajuste en función del clima

        // Ejemplo de ajuste simple:
        val precipitationProbability = weatherData.precipitation_probability.average()
        val weatherSpeedAdjustment = when {
            precipitationProbability > 50 -> -2.0
            weatherData.windspeed_10m.average() > 10.0 -> -1.0
            else -> 0.0
        }

        // Devolver la velocidad ajustada por el clima
        return weatherSpeedAdjustment
    }

}




