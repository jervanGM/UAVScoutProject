package com.example.uavscoutproject.mainscreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uavscoutproject.R
import com.example.uavscoutproject.mainscreen.datanalyzer.data.HourlyData
import com.example.uavscoutproject.mainscreen.datanalyzer.data.RouteStatistics
import com.example.uavscoutproject.mainscreen.datanalyzer.viewmodel.DataViewModel
import com.example.uavscoutproject.mainscreen.home.TableCell
import com.example.uavscoutproject.mainscreen.home.data.Dronedata
import com.example.uavscoutproject.mainscreen.home.droneviewmodel.DroneViewModel
import com.example.uavscoutproject.mainscreen.location.viewmodel.LocationViewModel
import com.example.uavscoutproject.materials.WeatherDialog
import com.example.uavscoutproject.preferences.MyPreferences
import kotlinx.coroutines.delay


/**
 * Composable function that displays the data view.
 *
 * @param dataViewModel The data view model to use for retrieving data.
 * @param locationViewModel The location view model to use for retrieving location data.
 * @param droneViewModel The drone view model to use for retrieving drone data.
 */
@Composable
fun DataView(
    dataViewModel: DataViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel(),
    droneViewModel: DroneViewModel = viewModel()
) {
    var show by remember { mutableStateOf(false) }
    var showindex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf("Hoy") }
    var expanded by remember { mutableStateOf(false) }
    val items = droneViewModel.getList()
    val droneAttribute = listOf("Distancia", "Consumo", "Velocidad", "Duración", "Altitud")
    var droneData = RouteStatistics(
        0.0, 0,
        0, 0,
        0.0, 0.0,
        Triple(android.graphics.Color.parseColor("#66FBB0"), R.drawable.ic_void, "No hay condiciones para volar")
    )
    val tablefontSize = 14.sp
    val fistLocation = dataViewModel.firstlocation.title
    val lastLocation = dataViewModel.lastlocation.title
    val hourlyData = dataViewModel.getWeatherValue()
    val context = LocalContext.current

    // Start listening to data updates
    LaunchedEffect(Unit) {
        dataViewModel.startListening()

        // Continuously save sensor data at intervals
        while (true) {
            dataViewModel.saveSensorData(!MyPreferences(context).getBooleanSetting("isLocal"))
            delay(60000) // Delay of 60 seconds
        }
    }

    // Fetch hourly weather data and save it
    dataViewModel.fetchHourlyWeatherData(locationViewModel.locationDataList)
    dataViewModel.saveWeatherData(!MyPreferences(context).getBooleanSetting("isLocal"))

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            Text(
                text = "Condiciones climáticas de ruta",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 14.sp
            )
            Image(
                painter = painterResource(id = dataViewModel.getIlluminationIcon()),
                contentDescription = "Illumintaion at $${dataViewModel.getIlluminationValue().observeAsState().value} percent",
                modifier = Modifier.size(70.dp)
            )
            val dateinfo = dataViewModel.getCurrentDate()
            Text(
                text = "${dateinfo["dayOfWeek"]},${dateinfo["dayOfMonth"]}",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 2.dp),
                fontSize = 18.sp
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SensorBox(
                    color = dataViewModel.getHumidityColor(),
                    title = "Humedad",
                    value = "${dataViewModel.getHumidityValue().observeAsState().value}%"
                )
                SensorBox(
                    color = dataViewModel.getTemperatureColor(),
                    title = "Temperatura",
                    value = "${dataViewModel.getTemperatureValue().observeAsState().value}°",
                    horizontalpadding = 18
                )
                SensorBox(
                    color = dataViewModel.getPressureColor(),
                    title = "Presión(bar)",
                    value = "${dataViewModel.getPressureValue().observeAsState().value}",
                    horizontalpadding = 18
                )
            }
            TabRow(
                options = listOf("Hoy", "Mañana", "Día"),
                selectedOption = selectedOption,
                onOptionSelected = { option ->
                    selectedOption = option
                }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    modifier = Modifier.size(14.dp),
                    contentDescription = null
                )
                Box(
                    modifier = Modifier.width(360.dp).padding(vertical = 12.dp),
                ) {
                    LazyRow() {
                        itemsIndexed(hourlyData.time) { index, _ ->
                            val indexes = hourlyData.getDayWeatherData(selectedOption)
                            if (index in indexes.first..indexes.second) {
                                if (show) {
                                    WeatherDialog(
                                        firstlocation = fistLocation,
                                        lastlocation = lastLocation,
                                        data = hourlyData,
                                        index = showindex,
                                        onDismiss = { show = false })
                                }
                                WeatherBox(
                                    data = hourlyData,
                                    horizontalpadding = 18,
                                    icon = hourlyData.getWeatherIcon(index),
                                    index = index
                                ) { indexed ->
                                    show = true
                                    showindex = indexed
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                    }
                }
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_right),
                    modifier = Modifier.size(14.dp),
                    contentDescription = null
                )
            }
            Text(
                text = "Información estimada de vuelo",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp),
                fontSize = 14.sp
            )
            DropDownMenuData(
                expanded,
                onExpandedChange = { expanded = it },
                ondroneSelected = { drone ->
                    droneData = dataViewModel.calculateRouteStatistics(
                        droneData = drone,
                        weatherData = hourlyData,
                        route = locationViewModel.locationDataList
                    )
                    droneViewModel.saveRouteData(!MyPreferences(context).getBooleanSetting("isLocal"))
                },
                items
            )
            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    TableCell(text = droneAttribute[0], tablefontSize, true, weight = 2f)
                    TableCell(text = "${droneData.totalDistance} Km", tablefontSize, weight = 2f)
                    TableCell(text = droneAttribute[1], tablefontSize, true, weight = 2f)
                    TableCell(text = "${droneData.totalConsumption} W", tablefontSize, weight = 2f)
                }
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    TableCell(text = droneAttribute[2], tablefontSize, true, weight = 2f)
                    TableCell(text = "${droneData.averageSpeed} Km/h", tablefontSize, weight = 2f)
                    TableCell(text = droneAttribute[3], tablefontSize, true, weight = 2f)
                    TableCell(text = "${droneData.flightDuration} min", tablefontSize, weight = 2f)
                }
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    TableCell(text = droneAttribute[4], tablefontSize, true, weight = 1.6f)
                    TableCell(
                        text = "${droneData.minAltitude} Km - ${droneData.maxAltitude} Km",
                        tablefontSize,
                        weight = 5f
                    )
                }
            }
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                val redColor = Color(droneData.routeEvaluation.first)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(32.dp)
                        .fillMaxWidth()
                        .border(2.dp, redColor, shape = RoundedCornerShape(4.dp))
                ) {
                    Icon(
                        painter = painterResource(id = droneData.routeEvaluation.second),
                        contentDescription = "Añadir item",
                        tint = redColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        droneData.routeEvaluation.third,
                        fontSize = 10.sp,
                        color = redColor,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}


/**
 * Function used for previewing the [DataView] composable.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DataPreview() {
    DataView()
}



/**
 * Composable function that represents a sensor box.
 *
 * @param color The color of the sensor box.
 * @param title The title of the sensor box.
 * @param value The value of the sensor box.
 * @param horizontalpadding The horizontal padding value.
 */
@Composable
fun SensorBox(color: String, title: String, value: String, horizontalpadding: Int = 24) {
    val animcolor = animateColorAsState(Color(android.graphics.Color.parseColor(color)))

    Box(
        modifier = Modifier
            .background(animcolor.value, shape = RoundedCornerShape(24.dp))
            .padding(vertical = 12.dp, horizontal = horizontalpadding.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp),
                fontSize = 14.sp
            )
            Text(
                text = value,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 28.sp
            )
        }
    }
}

/**
 * Composable function that represents a weather box.
 *
 * @param data The hourly weather data.
 * @param horizontalpadding The horizontal padding value.
 * @param index The index of the weather data.
 * @param icon The icon resource ID for the weather.
 * @param onClick The click listener for the weather box.
 */
@Composable
fun WeatherBox(
    data: HourlyData,
    horizontalpadding: Int = 24,
    index: Int,
    icon: Int,
    onClick: (Int) -> Unit,
) {
    val evaluation = data.evaluateFlightPossibility(index)

    Box(
        modifier = Modifier
            .background(Color(evaluation.first), shape = RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = Color(android.graphics.Color.parseColor("#808080")),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(vertical = 16.dp, horizontal = horizontalpadding.dp)
            .clickable { onClick(index) }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(icon),
                modifier = Modifier
                    .size(42.dp),
                contentDescription = null
            )
            Text(
                text = "${data.temperature_2m[index].toInt()}°",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                fontSize = 28.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 18.dp)) {
                Text(
                    text = data.time[index],
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 14.sp
                )
                Icon(
                    painter = painterResource(evaluation.second),
                    modifier = Modifier
                        .size(24.dp),
                    contentDescription = null
                )
            }
        }
    }
}



/**
 * Composable function that represents a drop-down menu for selecting drone data.
 *
 * @param expanded Whether the drop-down menu is expanded or not.
 * @param onExpandedChange Callback function to notify changes in the expanded state.
 * @param ondroneSelected Callback function to handle the selection of a drone item.
 * @param items The list of drone data items to display in the drop-down menu.
 */
@Composable
fun DropDownMenuData(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    ondroneSelected: (Dronedata) -> Unit,
    items: List<Dronedata>
) {
    var selectedItem by remember { mutableStateOf<String?>(null) }
    var selectedIconItem by remember { mutableStateOf(R.drawable.ic_arrow_down) }

    Column(Modifier.padding(horizontal = 22.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onExpandedChange(true) }
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(4.dp)
                )
                .height(40.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .weight(0.9f)
                    .padding(start = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(selectedIconItem),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        selectedItem ?: "Lista de aeromodelos",
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .weight(0.1f)
                    .align(Alignment.CenterVertically)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Row() {
                            Icon(
                                painter = painterResource(item.icon),
                                modifier = Modifier.size(24.dp),
                                contentDescription = null,
                            )
                            Text(
                                text = item.name,
                                fontSize = 19.sp,
                                textAlign = TextAlign.Center,
                            )
                        }
                    },
                    onClick = {
                        ondroneSelected(item)
                        selectedItem = item.name
                        selectedIconItem = item.icon
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}



/**
 * Composable function that represents a row of tabs to select the day of the weather list.
 *
 * @param options The list of tab options.
 * @param selectedOption The currently selected tab option.
 * @param onOptionSelected Callback function to handle the selection of a tab option.
 */
@Composable
fun TabRow(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .padding(top = 16.dp, start = 78.dp, end = 78.dp)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(5.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            val backgroundColor = if (isSelected) Color(android.graphics.Color.parseColor("#12CDD4"))
            else Color.Transparent
            val borderColor = if (isSelected) Color.Black else Color.Transparent

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp)
                    .padding(1.dp) // Added padding to cover the border of the Row
                    .background(
                        backgroundColor,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onOptionSelected(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    fontSize = 14.sp,
                    color = if (isSelected) Color.White else Color.Black
                )
            }
        }
    }
}




