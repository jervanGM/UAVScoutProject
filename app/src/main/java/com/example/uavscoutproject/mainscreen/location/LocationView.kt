package com.example.uavscoutproject.mainscreen.location

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uavscoutproject.R
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem
import com.example.uavscoutproject.mainscreen.location.data.Position
import com.example.uavscoutproject.mainscreen.location.viewmodel.LocationViewModel
import com.example.uavscoutproject.materials.MapView

enum class MarkerState {
    NO_MARK,
    MARK,
    STABLISH_MARK
}
/**
 * Composable function for displaying the LocationView.
 *
 * @param locationViewModel The view model for handling location-related data and operations.
 */
@Composable
fun LocationView(locationViewModel: LocationViewModel = viewModel()) {
    // Retrieve location items list from the view model
    val itemsList = locationViewModel.alterLocationList

    // Create scroll state for the LazyColumn
    val scrollState = rememberLazyListState()

    // Get the current context
    val context = LocalContext.current

    // Define mutable state for marker state and delete location
    val markerState = remember { mutableStateOf(MarkerState.NO_MARK) }
    val deleteLocationIndex = remember { mutableStateOf(0) }
    val deleteLocation = remember { mutableStateOf(false) }

    Column {
        // MapView composable wrapped in a Box
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1.5f)
        ) {
            MapView(
                modifier = Modifier,
                deleteLocation = deleteLocation,
                deleteLocationIndex = deleteLocationIndex,
                isMarkerSet = markerState,
                viewModel = locationViewModel
            )
        }

        // Box containing the rest of the content
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp)
        ) {
            Column {
                // GeobuttonsRow composable
                GeobuttonsRow(locationViewModel, onMarkerChange = {
                    when (markerState.value) {
                        MarkerState.NO_MARK -> {
                            markerState.value = MarkerState.MARK
                        }
                        MarkerState.MARK -> {
                            markerState.value = MarkerState.STABLISH_MARK
                        }
                        MarkerState.STABLISH_MARK -> {
                            markerState.value = MarkerState.NO_MARK
                        }
                    }
                })

                // Box containing text labels for establishing route and distance
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                ) {
                    Text(
                        text = "Establecer ruta",
                        fontSize = 14.sp,
                        color = Color(android.graphics.Color.parseColor("#808080"))
                    )
                    Text(
                        text = "Distancia",
                        modifier = Modifier.padding(start = 273.dp),
                        fontSize = 14.sp,
                        color = Color(android.graphics.Color.parseColor("#808080"))
                    )
                }

                // LazyColumn for displaying location items
                LazyColumn(state = scrollState) {
                    items(itemsList.size) { index ->
                        var success: Boolean

                        LocationItem(
                            index = index,
                            item = itemsList.get(index),
                            onItemEdited = { i, s ->
                                itemsList[i] = s
                                Log.d("LOCATION", "${locationViewModel.alterLocationList.size}")
                            },
                            onItemChanged = { i, s ->
                                success = locationViewModel.updatePositionAndCheckDistance(
                                    context,
                                    i,
                                    s.title,
                                    s.position
                                )
                                if (success) locationViewModel.requestAirSpace(context, s)
                            },
                            onItemRemoved = { i ->
                                itemsList.removeAt(i)
                                deleteLocation.value = true
                                deleteLocationIndex.value = i
                            },
                            onItemAdded = { itemsList.add(GeocodeItem("Init", Position(null, null))) },
                            canDelete = (itemsList.size > 1),
                            context,
                            locationViewModel = locationViewModel
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable function for previewing the LocationView.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LocationPreview() {
    LocationView()
}

/**
 * Composable function for rendering the geobuttons row.
 *
 * @param locationViewModel The view model for handling location-related data and operations.
 * @param onMarkerChange Callback function for handling marker changes.
 */
@Composable
fun GeobuttonsRow(
    locationViewModel: LocationViewModel,
    onMarkerChange: () -> Unit
) {
    val context = LocalContext.current

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(top = 8.dp)) {
        Box(
            modifier = Modifier
                .weight(0.3f)
                .clickable(onClick = { onMarkerChange() }),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(48.dp)
                    .border(2.dp, Color.LightGray, shape = RoundedCornerShape(4.dp))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_location),
                    contentDescription = "Añadir item",
                    modifier = Modifier
                        .size(28.dp)
                        .padding(start = 8.dp)
                )
                Text(
                    "Marcador",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(0.4f)
                .clickable(onClick = {
                    locationViewModel.establishRoute()
                    locationViewModel.saveRouteData()
                }),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(48.dp)
                    .border(2.dp, Color.LightGray, shape = RoundedCornerShape(4.dp))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_route),
                    contentDescription = "Añadir item",
                    modifier = Modifier
                        .size(28.dp)
                        .padding(start = 8.dp)
                )
                Text(
                    "Establecer ruta",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(0.3f)
                .clickable(onClick = { locationViewModel.setGPSCoordinates(context = context) }),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(48.dp)
                    .border(2.dp, Color.LightGray, shape = RoundedCornerShape(4.dp))
                    .wrapContentSize()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_personloc),
                    contentDescription = "Añadir item",
                    modifier = Modifier
                        .size(28.dp)
                        .padding(start = 8.dp)
                )
                Text(
                    "Usar GPS",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

/**
 * Composable function for rendering a location item.
 *
 * @param index The index of the item.
 * @param item The GeocodeItem representing the location.
 * @param onItemEdited Callback function for handling item edit.
 * @param onItemChanged Callback function for handling item change.
 * @param onItemRemoved Callback function for handling item removal.
 * @param onItemAdded Callback function for handling item addition.
 * @param canDelete Flag indicating whether the item can be deleted.
 * @param context The Android Context.
 * @param locationViewModel The view model for handling location-related data and operations.
 */
@Composable
fun LocationItem(
    index: Int,
    item: GeocodeItem,
    onItemEdited: (Int, GeocodeItem) -> Unit,
    onItemChanged: (Int, GeocodeItem) -> Unit,
    onItemRemoved: (Int) -> Unit,
    onItemAdded: () -> Unit,
    canDelete: Boolean,
    context: Context,
    locationViewModel: LocationViewModel = viewModel()
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(4.dp))
        ) {
            IconButton(
                onClick = {
                    if (locationViewModel.alterLocationList.last().title != "Init") {
                        onItemAdded()
                    } else {
                        AlertDialog.Builder(context)
                            .setTitle("Localización previa incorrecta")
                            .setMessage("Se debe incluir una posición previamente")
                            .setPositiveButton("Aceptar", null)
                            .create().show()
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Añadir item",
                    modifier = Modifier.size(24.dp)
                )
            }

            LocationAutocomplete(
                searchText = if (item.title == "Init") "" else item.title,
                onSearchTextChanged = { newValue ->
                    onItemEdited(index, GeocodeItem(newValue, Position(null, null)))
                },
                onAddressSelected = { suggestion ->
                    onItemChanged(
                        index,
                        GeocodeItem(
                            suggestion.title,
                            Position(suggestion.position.lat, suggestion.position.lng)
                        )
                    )
                },
                modifier = Modifier.weight(1.5f),
                context = context,
                locationViewModel = locationViewModel
            )

            Box(
                Modifier
                    .width(68.dp)
                    .height(40.dp)
                    .border(2.dp, Color.LightGray, shape = RoundedCornerShape(4.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                val distance = item.distance.let {
                    if (it > 999) {
                        "${"%.2f".format(it.toDouble() / 1000)} Km"
                    } else {
                        "$it m"
                    }
                }
                Text(text = distance, fontSize = 14.sp, softWrap = false)
            }

            IconButton(
                onClick = {
                    if (canDelete) {
                        onItemRemoved(index)
                    }
                },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bin),
                    contentDescription = "Eliminar item",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * Composable function for rendering the location autocomplete field.
 *
 * @param searchText The current search text.
 * @param onSearchTextChanged Callback function for handling search text changes.
 * @param onAddressSelected Callback function for handling address selection.
 * @param modifier The modifier for the composable.
 * @param context The Android Context.
 * @param locationViewModel The view model for handling location-related data and operations.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LocationAutocomplete(
    searchText: String,
    onSearchTextChanged: (String) -> Unit,
    onAddressSelected: (GeocodeItem) -> Unit,
    modifier: Modifier,
    context: Context,
    locationViewModel: LocationViewModel = viewModel(),
) {
    val isDropdownOpen = remember { mutableStateOf(false) }

    Box(modifier) {
        AnimatedVisibility(
            visible = isDropdownOpen.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val addressSuggestions = locationViewModel.addressSuggestions
            DropdownMenu(
                expanded = true,
                onDismissRequest = {
                    isDropdownOpen.value = false
                },
                properties = PopupProperties(
                    focusable = false,
                    clippingEnabled = false
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 50.dp, max = 150.dp)
                    .background(Color.White)
                    .align(Alignment.Center)
            ) {
                addressSuggestions.forEach { suggestion ->
                    DropdownMenuItem(
                        onClick = {
                            onAddressSelected(suggestion)
                            isDropdownOpen.value = false // Cierra el menú desplegable después de seleccionar una sugerencia
                        },
                        text = {
                            Text(text = suggestion.title)
                        },
                        leadingIcon = { Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = "location address",
                            modifier = Modifier.size(24.dp)
                        )}
                    )
                }
            }
        }
        TextField(
            value = searchText,
            onValueChange = {
                onSearchTextChanged(it)
                locationViewModel.setLocationSuggestion(context, it)
                isDropdownOpen.value = it.isNotEmpty() // Cambia el estado del menú desplegable
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                textColor = Color.Black,
                cursorColor = Color.Black,
            ),
            placeholder = { Text("Introduce localización",
                overflow = TextOverflow.Visible,
                softWrap = false) },
            singleLine = true,
            modifier = Modifier
        )
    }
}










