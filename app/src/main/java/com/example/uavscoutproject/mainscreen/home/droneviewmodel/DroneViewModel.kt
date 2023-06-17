package com.example.uavscoutproject.mainscreen.home.droneviewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uavscoutproject.UAVScoutApp
import com.example.uavscoutproject.mainscreen.home.data.Dronedata
import com.example.uavscoutproject.mainscreen.home.data.LastRoute
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem
import com.example.uavscoutproject.mainscreen.location.data.Position
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


/**
 * The DroneViewModel class is responsible for managing drone data and interacting with the database and cloud storage.
 *
 * @property application The application instance.
 */
@Suppress("UNCHECKED_CAST", "SENSELESS_COMPARISON")
class DroneViewModel(application: Application) : AndroidViewModel(application) {
    private val database = (application as UAVScoutApp).database
    private val droneDao = database.personalDroneDao()
    private val routeDao = database.lastDroneDao()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    /**
     * Enum class representing local mode options.
     */
    enum class LocalMode {
        SAVE, UPDATE, DELETE
    }

    /**
     * Initializes the Firestore settings.
     */
    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    /**
     * Adds a new drone to the drone list using the DroneMaker object.
     *
     * @param newDrone The new drone to add.
     */
    fun addDrone(newDrone: Dronedata) {
        DroneMaker.addDrone(newDrone)
    }

    /**
     * Edits the drone at the specified index with the edited drone data using the DroneMaker object.
     *
     * @param index The index of the drone to edit.
     * @param editedDrone The edited drone data.
     */
    fun editDrone(index: Int, editedDrone: Dronedata) {
        DroneMaker.editDrone(index, editedDrone)
    }

    /**
     * Deletes the drone at the specified index using the DroneMaker object.
     *
     * @param index The index of the drone to delete.
     */
    fun deleteDrone(index: Int) {
        DroneMaker.deleteDrone(index)
    }

    /**
     * Retrieves the drone at the specified index from the DroneMaker object.
     *
     * @param index The index of the drone to retrieve.
     * @return The drone at the specified index.
     */
    fun getItem(index: Int): Dronedata {
        return DroneMaker.getItem(index)
    }

    /**
     * Retrieves the list of drones from the DroneMaker object.
     *
     * @return The list of drones.
     */
    fun getList(): List<Dronedata> {
        return DroneMaker.getList()
    }

    /**
     * Retrieves the list of drones from the DroneMaker object.
     *
     * @return The list of drones.
     */
    fun getDBList(): List<Dronedata> {
        return DroneMaker.getDBList()
    }

    /**
     * Retrieves the drone data from Firestore and updates the drone list in the DroneMaker object.
     */
    fun getDroneData() {
        val document = firestore
            .collection("dronesdata")
            .document("comertial_drones")

        val handle = document.get()
        handle.addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val data = documentSnapshot.data
                if (data != null) {
                    val jsonArray = JSONArray()
                    for (item in data.values) {
                        if (item is Map<*, *>) {
                            val jsonObject = JSONObject(item)
                            jsonArray.put(jsonObject)
                        }
                    }
                    val droneList = convertJsonToDronedataList(jsonArray)
                    DroneMaker.setDBList(droneList)
                }
            } else {
                // The document doesn't exist
            }
        }
        handle.addOnFailureListener {
            Log.d("Firebase", "Get failed $it ")
        }
    }

    /**
     * Retrieves the personal drone data from Firestore or local database, based on the cloudSave parameter.
     *
     * @param cloudSave Indicates whether to retrieve data from Firestore (true) or local database (false).
     */
    suspend fun getPersonalDroneData(cloudSave: Boolean = true) {
        if (cloudSave) {
            val document = firestore
                .collection("dronesdata")
                .document("personal_drones_${user?.uid}")

            val handle = document.get()
            handle.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data
                    if (data != null) {
                        val jsonArray = JSONArray()
                        for (item in data.values) {
                            if (item is Map<*, *>) {
                                val jsonObject = JSONObject(item)
                                jsonArray.put(jsonObject)
                            }
                        }
                        val droneList = convertJsonToDronedataList(jsonArray)
                        DroneMaker.setList(droneList)
                    }
                } else {
                    // The document doesn't exist
                }
            }
            handle.addOnFailureListener {
                Log.d("Firebase", "Get failed $it ")
            }
        } else {
            viewModelScope.launch {
                val drones = droneDao.getAll()
                if (drones.isNotEmpty()) {
                    DroneMaker.setList(drones)
                }
            }
        }
    }

    /**
     * Saves the local personal drone data in the local database.
     *
     * @param drone The drone data to update.
     */
    private suspend fun saveLocalPersonalDroneData(drone: Dronedata) {
        droneDao.insert(drone)
        Log.d("SQLite", "Data saved")
    }

    /**
     * Updates the local personal drone data in the local database.
     *
     * @param drone The drone data to update.
     */
    private suspend fun updateLocalPersonalDroneData(drone: Dronedata) {
        droneDao.update(drone)
        Log.d("SQLite", "Data updated")
    }

    /**
     * Deletes the local personal drone data from the local database.
     *
     * @param drone The drone data to delete.
     */
    private suspend fun deleteLocalPersonalDroneData(drone: Dronedata) {
        droneDao.delete(drone)
        Log.d("SQLite", "Data deleted")
    }

    /**
     * Saves the personal drone data locally or in Firestore, based on the cloudSave parameter and localMode.
     *
     * @param cloudSave Indicates whether to save data in Firestore (true) or locally (false).
     * @param localMode The local mode for saving data.
     * @param localIndex The local index for saving data.
     */
    fun savePersonalDroneData(
        cloudSave: Boolean = true,
        localMode: LocalMode = LocalMode.SAVE,
        localIndex: Dronedata = Dronedata()
    ) {
        if (cloudSave) {
            val droneData = DroneMaker.getList()
            val document =
                firestore.collection("dronesdata").document("personal_drones_${user?.uid}")
            val locationDataMap = droneData.mapIndexed { index, droneItem ->
                "item$index" to droneItem
            }.toMap()

            val handle = document.set(locationDataMap)
            handle.addOnSuccessListener {
                Log.d("Firebase", "Document saved")
            }
            handle.addOnFailureListener {
                Log.d("Firebase", "Save failed $it ")
            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                when (localMode) {
                    LocalMode.SAVE -> saveLocalPersonalDroneData(localIndex)
                    LocalMode.UPDATE -> updateLocalPersonalDroneData(localIndex)
                    LocalMode.DELETE -> deleteLocalPersonalDroneData(localIndex)
                }
            }
        }
    }

    /**
     * Converts a JSON array to a list of drone data objects.
     *
     * @param jsonArray The JSON array to convert.
     * @return The list of drone data objects.
     */
    private fun convertJsonToDronedataList(jsonArray: JSONArray): List<Dronedata> {
        val dronedataList = mutableListOf<Dronedata>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val namej = jsonObject.optString("name", "")
            val vehiclej = jsonObject.optString("vehicle", "")
            val providerj = jsonObject.optString("provider", "")
            val colorj = jsonObject.optString("color", "")
            val speedj = jsonObject.optString("speed", "")
            val weightj = jsonObject.optString("weight", "")
            val batteryj = jsonObject.optString("battery", "")
            val energyj = jsonObject.optString("energy", "")
            val capacityj = jsonObject.optString("capacity", "")
            val imgUrij = jsonObject.optString("imgUri", "")

            val droneData = Dronedata().apply {
                name = namej
                vehicle = vehiclej
                provider = providerj
                color = colorj
                speed = speedj
                weight = weightj
                battery = batteryj
                energy = energyj
                capacity = capacityj
                operator = ""
                telephone = ""
                serial = ""
                imgUri = imgUrij
            }
            dronedataList.add(droneData)
        }
        return dronedataList
    }

    /**
     * Saves the local route data to the local database.
     *
     * @param route The route data to save.
     */
    private suspend fun saveLocalRouteData(route: LastRoute) {
        routeDao.insert(route)
        Log.d("SQLite", "Data saved")
    }

    /**
     * Updates the local route data in the local database.
     *
     * @param route The route data to update.
     */
    private suspend fun updateLocalRouteData(route: LastRoute) {
        routeDao.update(route)
        Log.d("SQLite", "Data updated")
    }

    /**
     * Saves the route data locally or in Firestore, based on the cloudSave parameter and localMode.
     *
     * @param cloudSave Indicates whether to save data in Firestore (true) or locally (false).
     * @param localMode The local mode for saving data.
     * @param route The route data to save.
     */
    fun saveRouteData(
        cloudSave: Boolean = true,
        localMode: LocalMode = LocalMode.SAVE,
        route: LastRoute = LastRoute()
    ) {
        if (cloudSave) {
            val document =
                firestore.collection("lastroute").document("last_route_${user?.uid}")

            val routeDataMap = RouteMaker.getRoute().mapIndexed { index, geocodeItem ->
                "item$index" to geocodeItem
            }.toMap()

            val data = hashMapOf(
                "routeData" to routeDataMap,
                "distance" to RouteMaker.getDistance(),
                "time" to RouteMaker.getTime(),
                "weather" to RouteMaker.getWeather()
            )

            document.set(data)
                .addOnSuccessListener {
                    Log.d("Firebase", "Document saved")
                }
                .addOnFailureListener {
                    Log.d("Firebase", "Save failed $it")
                }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                when (localMode) {
                    LocalMode.SAVE -> saveLocalRouteData(route)
                    LocalMode.UPDATE -> updateLocalRouteData(route)
                    LocalMode.DELETE -> {
                        // Not implemented
                    }
                }
            }
        }
    }
    /**
     * Retrieves the route data from Firestore or local database, based on the cloudSave parameter.
     *
     * @param cloudSave Indicates whether to retrieve data from Firestore (true) or local database (false).
     */
    @SuppressLint("SuspiciousIndentation")
    suspend fun getRouteData(cloudSave:Boolean = true) {
        if (cloudSave) {
            val document = firestore
                .collection("lastroute")
                .document("last_route_${user?.uid}")

            val handle = document.get()
            handle.addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val distance = documentSnapshot.getDouble("distance")
                    val routeData = documentSnapshot.get("routeData") as Map<String, Any>?
                    val time = documentSnapshot.getLong("time")
                    val weather = documentSnapshot.getString("weather")
                    val geocodeItemList = convertJsonToGeocodeItemList(routeData)

                        RouteMaker.setRoute(geocodeItemList)
                        RouteMaker.setDistance(distance!!)
                        RouteMaker.setTime(time!!.toInt())
                        RouteMaker.setWeather(weather!!)
                    }
            }
            handle.addOnFailureListener {
                Log.d("Firebase", "Get failed $it ")
            }
        }
        else{
            viewModelScope.launch {
                val route = routeDao.get()
                if (route != null){
                    RouteMaker.setRoute(route.route)
                    RouteMaker.setDistance(route.distance)
                    RouteMaker.setTime(route.time)
                    RouteMaker.setWeather(route.weather)
                }
            }
        }
    }
    /**
     * Converts route data in JSON format to a list of [GeocodeItem] objects.
     *
     * @param routeData The route data in JSON format as a [Map] of key-value pairs.
     * @return A list of [GeocodeItem] objects representing the converted route data.
     */
    private fun convertJsonToGeocodeItemList(routeData: Map<String, Any>?): List<GeocodeItem> {
        val geocodeItemList = mutableListOf<GeocodeItem>()

        // Check if routeData is not null
        if (routeData != null) {
            for (item in routeData) {
                val attrs = item.value as Map<String, Any>?
                val positions = attrs?.get("position") as Map<String, Any>?

                // Extract the necessary attributes from the JSON data
                val title = attrs?.get("title") as String
                val latitude = positions?.get("lat") as Double
                val longitude = positions["lng"] as Double
                val elevation = attrs["elevation"] as Double
                val distance = attrs["distance"].toString().toInt()

                // Create a GeocodeItem object using the extracted attributes
                val geocodeItem = GeocodeItem(
                    title,
                    Position(latitude, longitude),
                    elevation,
                    distance
                )

                // Add the GeocodeItem to the list
                geocodeItemList.add(geocodeItem)
            }
        }

        return geocodeItemList
    }

}