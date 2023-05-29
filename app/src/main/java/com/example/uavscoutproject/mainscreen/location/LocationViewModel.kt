package com.example.uavscoutproject.mainscreen.location

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uavscoutproject.R
import com.example.uavscoutproject.mainscreen.home.newsapi.ArticleComposer
import com.example.uavscoutproject.mainscreen.location.data.AirSpace
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem
import com.example.uavscoutproject.mainscreen.location.data.Position
import com.example.uavscoutproject.mainscreen.location.data.toPositionList
import com.example.uavscoutproject.mainscreen.location.locationapi.AirSpaceApiService
import com.example.uavscoutproject.mainscreen.location.locationapi.GeocodeApiService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocationViewModel : ViewModel() {
        val locationDataList = mutableStateListOf<GeocodeItem>()
        val alterLocationList = mutableStateListOf<GeocodeItem>(GeocodeItem("", Position(0.0,0.0)))
        val addressSuggestions = mutableStateListOf<GeocodeItem>()
        val airspaceDataList = mutableStateListOf<AirSpace>()
        val locationretrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://geocode.search.hereapi.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val airspaceretrofit = Retrofit.Builder()
            .baseUrl("https://api.airmap.com/")
            .addConverterFactory(GsonConverterFactory.create()) // Si deseas usar Gson como convertidor
            .build()

        val airMapApi = airspaceretrofit.create(AirSpaceApiService::class.java)
        val geocodeApiService = locationretrofit.create(GeocodeApiService::class.java)

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
        fun requestAirSpace(context: Context){
            val geometry = "{\"type\":\"Point\",\"coordinates\":[-118.6578369140625,34.11180455556899]}"
            val buffer = 10000
            val types = "airport,controlled_airspace,tfr,wildfire"
            val full = true
            val geometryFormat = "geojson"
            val apiKey = context.getString(R.string.airmap_api_key)
            viewModelScope.launch {
                try {
                    val response = airMapApi.searchAirSpace(
                        geometry,
                        buffer,
                        types,
                        full,
                        geometryFormat,
                        apiKey
                    )
                    if (response.isSuccessful) {
                        Log.d("AirSpace", "Ha entrado bien : ${response.body()?.status }")
                        airspaceDataList.addAll(response.body()?.data!!)

                    } else {
                        Log.d("API_ERROR", "Error: ${response.code()} + ${response.raw()}")
                    }
                } catch (e: Exception) {
                    // Maneja cualquier error en la llamada a la API
                    e.printStackTrace()
                }
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
    }
