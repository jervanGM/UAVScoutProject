@file:Suppress("DEPRECATION")

package com.example.uavscoutproject.materials

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.uavscoutproject.R
import com.mapbox.android.gestures.BuildConfig
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.io.File

/**
 * Creates a [MapView] composable that follows the lifecycle of the current composable.
 *
 * @return The created [MapView].
 */
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
    Configuration.getInstance().osmdroidBasePath = File(context.filesDir, "osmdroid/base")
    Configuration.getInstance().osmdroidTileCache = File(context.filesDir, "/osmdroid/cache")
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
            setTileSource(TileSourceFactory.OpenTopo)
            clipToOutline = true
            controller.setZoom(10)
            setMultiTouchControls(true)
            val startPoint = GeoPoint(40.416775, -3.703790)
            setExpectedCenter(startPoint)
        }
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

/**
 * Creates a [LifecycleEventObserver] that maps the lifecycle events to corresponding MapView actions.
 *
 * @param mapView The MapView to observe.
 * @return The created [LifecycleEventObserver].
 */
@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
    }




