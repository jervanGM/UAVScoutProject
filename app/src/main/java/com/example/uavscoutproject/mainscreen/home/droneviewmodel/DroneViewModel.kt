package com.example.uavscoutproject.mainscreen.home.droneviewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uavscoutproject.MainActivity
import com.example.uavscoutproject.UAVScoutApp
import com.example.uavscoutproject.mainscreen.home.data.Dronedata
import com.example.uavscoutproject.mainscreen.home.dronedb.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class DroneViewModel(application: Application) : AndroidViewModel(application) {
    private val database = (application as UAVScoutApp).database
    private val droneDao = database.personalDroneDao()
    private lateinit var firestore: FirebaseFirestore
    private val user = FirebaseAuth.getInstance().currentUser

    enum class LocalMode {
        SAVE, UPDATE, DELETE
    }

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun addDrone(newDrone: Dronedata) {
        DroneMaker.addDrone(newDrone)
    }

    fun editDrone(index: Int, editedDrone: Dronedata) {
        DroneMaker.editDrone(index,editedDrone)
    }

    fun deleteDrone(index: Int) {
        DroneMaker.deleteDrone(index)
    }

    fun getItem(index: Int): Dronedata {
        return DroneMaker.getItem(index)
    }

    fun getList(): List<Dronedata> {
        return DroneMaker.getList()
    }

    fun getDBList(): List<Dronedata>{
        return DroneMaker.getDBList()
    }

    fun getDroneData(){
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
                            val jsonObject = JSONObject(item as Map<*, *>)
                            jsonArray.put(jsonObject)
                        }
                    }
                    val droneList = convertJsonToDronedataList(jsonArray)
                    DroneMaker.setDBList(droneList)
                }
            } else {
                // El documento no existe
            }
        }
        handle.addOnFailureListener {
            Log.d("Firebase", "Get failed $it ")
        }
    }


    suspend fun getPersonalDroneData(cloudSave:Boolean = true) {
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
                                val jsonObject = JSONObject(item as Map<*, *>)
                                jsonArray.put(jsonObject)
                            }
                        }
                        val droneList = convertJsonToDronedataList(jsonArray)
                        DroneMaker.setList(droneList)
                    }
                } else {
                    // El documento no existe
                }
            }
            handle.addOnFailureListener {
                Log.d("Firebase", "Get failed $it ")
            }
        }
        else{
            viewModelScope.launch {
                val drones = droneDao.getAll()
                if (drones.isNotEmpty()){
                    DroneMaker.setList(drones)
                }
            }
        }
    }

    private suspend fun saveLocalPersonalDroneData(drone: Dronedata){

        droneDao.insert(drone)
        Log.d("SQLite", "Data saved")
    }

    private suspend fun updateLocalPersonalDroneData(drone: Dronedata) {
        droneDao.update(drone)
        Log.d("SQLite", "Data updated")
    }

    private suspend fun deleteLocalPersonalDroneData(drone: Dronedata) {
        droneDao.delete(drone)
        Log.d("SQLite", "Data deleted")
    }

    fun savePersonalDroneData(
        cloudSave:Boolean = true,
        localMode : LocalMode = LocalMode.SAVE,
        localIndex : Dronedata = Dronedata()
    ){
        if(cloudSave) {
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
        }
        else{
            viewModelScope.launch(Dispatchers.IO) {
                when(localMode){
                    LocalMode.SAVE -> saveLocalPersonalDroneData(localIndex)
                    LocalMode.UPDATE -> updateLocalPersonalDroneData(localIndex)
                    LocalMode.DELETE -> deleteLocalPersonalDroneData(localIndex)
                    else -> Log.d("SQLite", "SQLite DB mode error")
                }
            }
        }
    }


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
}