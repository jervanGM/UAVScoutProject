package com.example.uavscoutproject.mainscreen.location.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uavscoutproject.R
import com.example.uavscoutproject.mainscreen.location.data.AirSpace
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem
import com.example.uavscoutproject.mainscreen.location.data.Position
import com.example.uavscoutproject.mainscreen.location.locationapi.AirSpaceApiService
import com.example.uavscoutproject.mainscreen.location.locationapi.GeocodeApiService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocationViewModel : ViewModel() {
        val locationDataList = mutableStateListOf<GeocodeItem>()
        val alterLocationList = mutableStateListOf<GeocodeItem>(GeocodeItem("", Position(0.0,0.0)))
        val airSpaceData = mutableStateListOf<AirSpace>()
        val addressSuggestions = mutableStateListOf<GeocodeItem>()
        val locationretrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://geocode.search.hereapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        private val airspaceretrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.airmap.com/")
            .addConverterFactory(GsonConverterFactory.create()) // Si deseas usar Gson como convertidor
            .build()

        private val airMapApi = airspaceretrofit.create(AirSpaceApiService::class.java)
        private val geocodeApiService = locationretrofit.create(GeocodeApiService::class.java)

        private lateinit var firestore: FirebaseFirestore

        init {
            firestore = FirebaseFirestore.getInstance()
            firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
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
    suspend fun requestAirSpace(context: Context): Boolean {
        val geometry = "{\"type\":\"Point\",\"coordinates\":[-118.6578369140625,34.11180455556899]}"
        val buffer = 1
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
            airSpaceData.addAll(response.data)
            return true
        } catch (e: Exception) {
            // Maneja cualquier error en la llamada a la API
            e.printStackTrace()
        }
        return false
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
    }
