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
import com.example.uavscoutproject.mainscreen.datanalyzer.weatherapi.WeatherApiService
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
    private lateinit var firestore: FirebaseFirestore

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }


    fun saveWeatherData() {
        val weatherdata = DataMaker.getData()
        val collection = firestore.collection("weatherdata")
        val document = if (weatherdata.id.isNullOrEmpty()) {
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


    fun saveSensorData(){
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
        handle.addOnFailureListener{
            Log.d("Firebase", "Save failed $it ")
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
        val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

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

            when (event.sensor.type) {
                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    _temperaturaState.value = event.values[0]
                }
                Sensor.TYPE_RELATIVE_HUMIDITY -> {
                    _humedadState.value = event.values[0]
                }
                Sensor.TYPE_PRESSURE -> {
                    _presionState.value = (event.values[0]/1000)
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
}




