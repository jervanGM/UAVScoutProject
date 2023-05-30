@file:Suppress("DEPRECATION")

package com.example.uavscoutproject.materials

import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.ColorUtils
import com.example.uavscoutproject.mainscreen.location.data.toPolygon
import com.example.uavscoutproject.mainscreen.location.viewmodel.LocationViewModel
import com.mapbox.geojson.Polygon
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView


/**
 * A composable Google Map.
 * @author Arnau Mora
 * @since 20211230
 * @param modifier Modifiers to apply to the map.
 * @param onLoad This will get called once the map has been loaded.
 */
@Composable
fun MapView(viewmodel: LocationViewModel) {
    var show by remember { mutableStateOf(false) }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val success = viewmodel.requestAirSpace(context)
    }
    val polygonSize = mutableListOf<LatLng>()
    val polygonOptions = mutableListOf<PolygonOptions>()
    if(viewmodel.airSpaceData.size != 0){
        polygonSize.addAll(viewmodel.airSpaceData[0].geometry.coordinates.toPolygon())
    }
    Log.d("AIRSPACE", "$polygonSize")
    if (polygonSize.size != 0) {
        AndroidView(
            factory = { inContext ->
                Mapbox.getInstance(inContext)
                val mapView = MapView(inContext)
                val styleUrl =
                    "https://api.maptiler.com/maps/hybrid/style.json?key=Z5cLOuGEyJEN8mBYXKu8";
                //val styleUrl = "https://cdn.airmap.com/static/map-styles/0.8.5/standard.json";

                mapView.onCreate(null)
                mapView.getMapAsync { map ->
                    // Set the style after mapView was loaded
                    map.setStyle(styleUrl) {
                        map.uiSettings.setAttributionMargins(15, 0, 0, 15)
                        // Set the map view center
                        map.cameraPosition = CameraPosition.Builder()
                            .target(LatLng(34.15, -118.61))
                            .zoom(15.0)
                            .bearing(2.0)
                            .build()
                        map.addMarker(
                            MarkerOptions()
                                .position(LatLng(34.1668305103208, -118.61245940821))
                                .setTitle("Hola mundo")
                                .setSnippet("Pelotudo")
                        )

                        val polygonl = map.addPolygons(
                            PolygonOptions()
                                .addAll(polygonSize)
                                .fillColor(
                                    ColorUtils.setAlphaComponent(
                                        Color.parseColor("#F27E7E"),
                                        (0.5f * 255).toInt()
                                    )
                                )
                        )

                        map.setOnPolygonClickListener { clickedPolygon ->
                            if (clickedPolygon == polygonl) {
                                show = true
                            }
                        }

                    }
                }
                mapView
            }
        )
    }
    if(show){
        AdvisorDialog( onDismiss = {show = false})
    }
}