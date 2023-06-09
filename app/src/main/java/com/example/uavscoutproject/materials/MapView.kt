//@file:Suppress("DEPRECATION")

package com.example.uavscoutproject.materials

import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.uavscoutproject.R
import com.example.uavscoutproject.mainscreen.location.MarkerState
import com.example.uavscoutproject.mainscreen.location.data.Position
import com.example.uavscoutproject.mainscreen.location.viewmodel.LocationViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.Projection
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline


@Composable
fun MapView(
    modifier: Modifier = Modifier,
    isMarkerSet: MutableState<MarkerState>,
    viewModel: LocationViewModel,
    onLoad: ((map: MapView) -> Unit)? = null
) {
    val mapViewState = rememberMapViewWithLifecycle()
    var show = remember { mutableStateOf(false) }
    var polygonId = remember { mutableStateOf("") }
    val markerPoints = mutableListOf<GeoPoint>()
    val context = LocalContext.current
    val centerMarker = remember { mutableStateOf<Marker?>(null) }

    AndroidView(
        factory = { context ->
            mapViewState.apply {

            }
        },
        modifier = modifier
    ) {
            map -> onLoad?.invoke(map)
        val iconSizeFactor = 4 // Factor de escala para agrandar el icono

        val centerOverlay = object : Overlay(context) {
            override fun draw(canvas: Canvas?, mapProjection: Projection?) {
                if (isMarkerSet.value == MarkerState.MARK) {
                    val centerPoint = mapProjection!!.toPixels(map.mapCenter, null)
                    val markerSize = 40 // Tamaño original del marcador

                    val icon = ContextCompat.getDrawable(context, R.drawable.ic_location) // Carga el recurso de icono con el contexto

                    icon?.apply {
                        val scaledWidth = markerSize * iconSizeFactor // Ancho escalado del icono
                        val scaledHeight = markerSize * iconSizeFactor // Altura escalada del icono

                        setBounds(
                            centerPoint.x - scaledWidth / 2,
                            centerPoint.y - scaledHeight / 2,
                            centerPoint.x + scaledWidth / 2,
                            centerPoint.y + scaledHeight / 2
                        )
                        draw(canvas!!)
                    }
                }
            }
        }
        map.overlays.add(centerOverlay)
        if(isMarkerSet.value == MarkerState.STABLISH_MARK){
            val m = Marker(map)
            m.position = GeoPoint(map.mapCenter.latitude, map.mapCenter.longitude)
            m.title = "Test"
            m.snippet = "Posiciones de test"
            m.icon = ContextCompat.getDrawable(context, R.drawable.ic_location)
            map.overlays.add(m)

            markerPoints.add(m.position)
            isMarkerSet.value = MarkerState.NO_MARK
        }
        for (loc in viewModel.alterLocationList) {
            if(loc.position.lat != null && loc.position.lng != null) {
                val m = Marker(map)
                m.position = GeoPoint(loc.position.lat, loc.position.lng)
                m.title = "Test"
                m.snippet = "Posiciones de test"
                m.icon = ContextCompat.getDrawable(context, R.drawable.ic_location)
                map.overlays.add(m)

                markerPoints.add(m.position)
            }
        }
        Log.d("MARKERS", "${markerPoints}")
        val polyline = Polyline()
        polyline.setPoints(markerPoints)
        polyline.color = Color.BLUE
        polyline.width = 5f

        map.overlayManager.add(polyline)

        for (airspace in viewModel.airSpaceData) {
            val polygonalChain = mutableListOf<GeoPoint>()
            polygonalChain.addAll(airspace.geometry.polygons)

            val polygon =Polygon()
            polygon.points = polygonalChain
            polygon.fillColor = 0x88a42141.toInt()
            polygon.strokeWidth = 2f
            polygon.infoWindow
            polygon.id = airspace.id
            polygon.setOnClickListener { polygonal, mapal, eventPos ->
                polygonId.value = polygonal.id
                show.value = true
                true
            }

            map.overlayManager.add(polygon)
        }
        map.invalidate()
    }
    if (show.value) {
        AdvisorDialog(airSpaceData = viewModel.airSpaceData, id = polygonId.value) {
            show.value = false
        }
    }
}


