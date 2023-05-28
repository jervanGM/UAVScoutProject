@file:Suppress("DEPRECATION")

package com.example.uavscoutproject.materials

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.ColorUtils
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.annotations.PolygonOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap


/**
 * A composable Google Map.
 * @author Arnau Mora
 * @since 20211230
 * @param modifier Modifiers to apply to the map.
 * @param onLoad This will get called once the map has been loaded.
 */
@Composable
fun MapView() {
    var show by remember { mutableStateOf(false) }
    val polygon: MutableList<LatLng> = ArrayList()
    polygon.add(LatLng(45.522585, -122.685699))
    polygon.add(LatLng(45.534611, -122.708873))
    polygon.add(LatLng(45.530883, -122.678833))
    polygon.add(LatLng(45.547115, -122.667503))
    polygon.add(LatLng(45.530643, -122.660121))
    polygon.add(LatLng(45.533529, -122.636260))
    polygon.add(LatLng(45.521743, -122.659091))
    polygon.add(LatLng(45.510677, -122.648792))
    polygon.add(LatLng(45.515008, -122.664070))
    polygon.add(LatLng(45.502496, -122.669048))
    polygon.add(LatLng(45.515369, -122.678489))
    polygon.add(LatLng(45.506346, -122.702007))
    polygon.add(LatLng(45.522585, -122.685699))
    AndroidView(
        factory = { context ->
            Mapbox.getInstance(context)
            val mapView = MapView(context)
            val styleUrl = "https://api.maptiler.com/maps/hybrid/style.json?key=Z5cLOuGEyJEN8mBYXKu8";
            //val styleUrl = "https://cdn.airmap.com/static/map-styles/0.8.5/standard.json";

            mapView.onCreate(null)
            mapView.getMapAsync { map ->
                // Set the style after mapView was loaded
                map.setStyle(styleUrl) {
                    map.uiSettings.setAttributionMargins(15, 0, 0, 15)
                    // Set the map view center
                    map.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(45.522585, -122.685699))
                        .zoom(10.0)
                        .bearing(2.0)
                        .build()
                    map.addMarker(
                        MarkerOptions()
                            .position(LatLng(28.679079, 77.069710))
                            .setTitle("Hola mundo")
                            .setSnippet("Pelotudo")
                    )
                    var polygonl =map.addPolygon(
                        PolygonOptions()
                        .addAll(polygon)
                        .fillColor(ColorUtils.setAlphaComponent(
                            Color.parseColor("#F27E7E"),
                            (0.5f * 255).toInt())))

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
    if(show){
        AdvisorDialog( onDismiss = {show = false})
    }
}