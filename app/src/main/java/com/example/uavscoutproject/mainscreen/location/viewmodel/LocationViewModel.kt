package com.example.uavscoutproject.mainscreen.location.viewmodel

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uavscoutproject.R
import com.example.uavscoutproject.mainscreen.location.data.AirMapRulesData
import com.example.uavscoutproject.mainscreen.location.data.AirSpace
import com.example.uavscoutproject.mainscreen.location.data.AirSpaceGeometry
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem
import com.example.uavscoutproject.mainscreen.location.data.Position
import com.example.uavscoutproject.mainscreen.location.locationapi.AirSpaceApiService
import com.example.uavscoutproject.mainscreen.location.locationapi.AirSpaceRulesApiService
import com.example.uavscoutproject.mainscreen.location.locationapi.GeocodeApiService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocationViewModel() : ViewModel() {
        val locationDataList = mutableStateListOf<GeocodeItem>()
        val alterLocationList = mutableStateListOf<GeocodeItem>(GeocodeItem("Init", Position(null,null)))
        val airSpaceData = mutableStateListOf<AirSpace>()
        val addressSuggestions = mutableStateListOf<GeocodeItem>()
        val AirSpaceRule  = MutableLiveData<AirMapRulesData>()
        val REQUEST_LOCATION_PERMISSION = 99
        val locationretrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://geocode.search.hereapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        private val airspaceretrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.airmap.com/")
            .addConverterFactory(GsonConverterFactory.create()) // Si deseas usar Gson como convertidor
            .build()

        private val airMapApi = airspaceretrofit.create(AirSpaceApiService::class.java)
        private val airMapRulesApi = airspaceretrofit.create(AirSpaceRulesApiService::class.java)
        private val geocodeApiService = locationretrofit.create(GeocodeApiService::class.java)

        private lateinit var firestore: FirebaseFirestore

        init {
            firestore = FirebaseFirestore.getInstance()
            firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        }

    fun updatePositionAndCheckDistance(context:Context,index:Int,newTitle: String,newPosition: Position): Boolean {
        if (alterLocationList.size != 1) {
            val previousPosition = alterLocationList[index-1].position
            val originPosition = alterLocationList.first().position
            val distanceToPrevious = calculateDistance(previousPosition, newPosition)
            if (calculateDistance(originPosition, newPosition) > 10000) {
                // Mostrar un cuadro de diálogo si la distancia es mayor a 10 km
                showDistanceAlertDialog(context)
                return false
            }
            else{
                alterLocationList[index] = GeocodeItem(newTitle,newPosition,distance = distanceToPrevious.toInt())
                for (loc in (1 until alterLocationList.size)){
                    alterLocationList[index].distance = calculateDistance(
                        alterLocationList[loc-1].position,
                        alterLocationList[loc].position).toInt()
                }
                return true
            }
        } else{
            alterLocationList[index] = GeocodeItem(newTitle,newPosition)
            return true
        }
    }
        fun addPositionAndCheckDistance(context:Context,newTitle: String,newPosition: Position): Boolean {
            if (alterLocationList.isNotEmpty()) {
                val firstPosition = alterLocationList.firstOrNull()
                if (firstPosition?.title == "Init" && firstPosition.position.lat == null && firstPosition.position.lng == null) {
                    alterLocationList.removeAt(0)
                }
            }
            if (alterLocationList.isNotEmpty()) {
                val previousPosition = alterLocationList.last().position
                val originPosition = alterLocationList.first().position
                val distanceToPrevious = calculateDistance(previousPosition, newPosition)
                if (calculateDistance(originPosition, newPosition) > 10000) {
                    // Mostrar un cuadro de diálogo si la distancia es mayor a 10 km
                    showDistanceAlertDialog(context)
                    return false
                }
                else{
                    alterLocationList.add(GeocodeItem(newTitle,newPosition,distance = distanceToPrevious.toInt()))
                    return true
                }
            } else{
                alterLocationList.add(GeocodeItem(newTitle,newPosition))
                return true
            }
        }

        private fun calculateDistance(start: Position, end: Position): Float {
            val results = FloatArray(1)
            Location.distanceBetween(
                start.lat!!, start.lng!!,
                end.lat!!, end.lng!!,
                results
            )
            return results[0]
        }

        private fun showDistanceAlertDialog(context: Context) {
            AlertDialog.Builder(context)
                .setTitle("Distancia excedida")
                .setMessage("No se puede establecer posiciones superiores a 10 Km desde el punto de origen.")
                .setPositiveButton("Aceptar", null)
                .create().show()
        }
        fun setGPSCoordinates(context: Context) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
            }
            else {
                if (!gpsEnabled) {
                    val dialogBuilder = AlertDialog.Builder(context)
                        .setTitle("Habilitar GPS")
                        .setMessage("Se requiere habilitar el GPS para obtener la ubicación actual.")
                        .setPositiveButton("Configuración") { _, _ ->
                            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            context.startActivity(settingsIntent)
                        }
                        .setNegativeButton("Cancelar", null)
                    val dialog = dialogBuilder.create()
                    dialog.show()
                }
                else{
                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    addPositionAndCheckDistance(
                        context,
                        "Posicion GPS",
                        Position(location?.latitude,location?.longitude))
                }
            }
        }

        fun saveRouteData(){
            val document =
                firestore.collection("routedata").document()
            val locationDataMap = locationDataList.mapIndexed { index, geocodeItem ->
                "item$index" to geocodeItem
            }.toMap()

            val handle = document.set(locationDataMap)
            handle.addOnSuccessListener {
                Log.d("Firebase", "Document saved")
            }
            handle.addOnFailureListener{
                Log.d("Firebase", "Save failed $it ")
            }
        }
        fun requestAirSpace(context: Context, suggestion: GeocodeItem){
            viewModelScope.launch {
                val lastlocation = suggestion.position
                val geometry =
                    "{\"type\":\"Point\",\"coordinates\":[${lastlocation.lng},${lastlocation.lat}]}"
                val buffer = 10000
                val types = "airport,controlled_airspace,tfr,wildfire"
                val full = true
                val geometryFormat = "geojson"
                val apiKey = context.getString(R.string.airmap_api_key)

                try {
                    val response = airMapApi.searchAirSpace(
                        geometry,
                        buffer,
                        types,
                        full,
                        geometryFormat,
                        apiKey
                    )
                    for (airspace in response.data){

                        saveAirspaceData(airspace)
                    }

                } catch (e: Exception) {
                    // Maneja cualquier error en la llamada a la API
                    e.printStackTrace()
                }
            }
        }
        private fun saveAirspaceData(airspace: AirSpace) {
            val collection = firestore.collection("airspacedata")
            val document = if (airspace.id.isNullOrEmpty()) {
                collection.document()
            } else {
                collection.document(airspace.id)
            }
            //weatherdata.id = document.id
            val handle = document.set(airspace.toMap())
            handle.addOnSuccessListener {
                Log.d("Firebase", "Document saved")
            }
            handle.addOnFailureListener {
                Log.d("Firebase", "Save failed $it ")
            }
        }

        fun getAirSpacesFromDB(){
            val collectionRef = firestore.collection("airspacedata")

            collectionRef.get()
                .addOnSuccessListener { querySnapshot ->
                    val airSpaceList = mutableListOf<AirSpace>()

                    for (documentSnapshot in querySnapshot) {
                        val id = documentSnapshot.getString("id") ?: ""
                        val name = documentSnapshot.getString("name") ?: ""
                        val type = documentSnapshot.getString("type") ?: ""
                        val country = documentSnapshot.getString("country") ?: ""
                        val state = documentSnapshot.getString("state") ?: ""
                        val city = documentSnapshot.getString("city") ?: ""
                        val ruleset_id = documentSnapshot.getString("ruleset_id") ?: ""

                        val geometryData = documentSnapshot.get("geometry") as? Map<String, Any>
                        val itemsData = geometryData?.filterKeys { it.startsWith("item") } ?: emptyMap()
                        val polygons = itemsData.values.mapNotNull { itemData ->
                            val latitude = (itemData as? Map<String, Any>)?.get("latitude") as? Double
                            val longitude = (itemData as? Map<String, Any>)?.get("longitude") as? Double

                            if (latitude != null && longitude != null) {
                                GeoPoint(latitude, longitude)
                            } else {
                                null
                            }
                        }

                        val geometry = AirSpaceGeometry(polygons = polygons)
                        val airspace = AirSpace(id, name, type, country, state, city, geometry, ruleset_id)

                        airSpaceList.add(airspace)
                    }
                    airSpaceData.addAll(airSpaceList)
                }
                .addOnFailureListener { exception ->
                    // Ocurrió un error al obtener los datos desde Firestore
                }
        }

        fun setLocationSuggestion(context : Context, query:String = "") {
            viewModelScope.launch {
                val apiKey = context.getString(R.string.here_api_key)
                try {
                    addressSuggestions.clear()
                    val response = geocodeApiService.geocode(query, apiKey)
                    addressSuggestions.addAll(response.items)
                } catch (e: Exception) {
                    // Maneja cualquier error en la llamada a la API
                    e.printStackTrace()
                }
            }
        }

        fun stablishRoute(){
            //locationDataList.clear()
            locationDataList.addAll(alterLocationList)
        }

        fun getAirMapRules(context: Context, id: String){
            viewModelScope.launch {
                val acceptHeader = "application/json"
                val apiKey = context.getString(R.string.airmap_api_key)
                try {
                    val response = airMapRulesApi.getAirMapRules(id, acceptHeader, apiKey)
                    AirSpaceRule.value = response.data
                } catch (e: Exception) {
                    // Maneja cualquier error en la llamada a la API
                    e.printStackTrace()
                }
            }
        }
}
