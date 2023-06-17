package com.example.uavscoutproject.materials

import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.uavscoutproject.R
import com.example.uavscoutproject.mainscreen.datanalyzer.data.HourlyData
import com.example.uavscoutproject.mainscreen.home.data.Dronedata
import com.example.uavscoutproject.mainscreen.home.TableCell
import com.example.uavscoutproject.mainscreen.home.loadBitmapFromUri
import com.example.uavscoutproject.mainscreen.location.data.AirSpace
import com.example.uavscoutproject.mainscreen.location.viewmodel.LocationViewModel

/**
 * Displays a dialog with drone data.
 *
 * @param dronedata The data of the drone.
 * @param onDismiss Callback function when the dialog is dismissed.
 */
@Composable
fun DroneDialog(dronedata: Dronedata, onDismiss: () -> Unit){
    val droneAttribute = listOf("Color", "Batería",
        "Velocidad","Energía",
        "Peso neto", "Capacidad")
    val droneData = listOf(
        dronedata.color,
        dronedata.battery,
        dronedata.speed.filter { it.isDigit() }.toIntOrNull(),
        dronedata.energy.filter { it.isDigit() }.toIntOrNull(),
        dronedata.weight.filter { it.isDigit() }.toIntOrNull(),
        dronedata.capacity.filter { it.isDigit() }.toIntOrNull()
    )
    val tablefontSize = 14.sp

    //Displays the drone dialog.
    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(60.dp)
                ),
            shape = RoundedCornerShape(60.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = dronedata.name,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically){

                    if (dronedata.imgUri != null && dronedata.imgUri != "") {
                        val selectedImageUri = dronedata.imgUri.toUri()
                        val context = LocalContext.current
                        val imagePainter: Any =
                            if (selectedImageUri.scheme == "http" || selectedImageUri.scheme == "https") {
                                val imageRequest = ImageRequest.Builder(context)
                                    .data(selectedImageUri)
                                    .build()
                                rememberAsyncImagePainter(model = imageRequest)
                            } else {
                                loadBitmapFromUri(context, selectedImageUri)
                            }
                        when (imagePainter) {
                            is AsyncImagePainter -> {
                                // The `imagePainter` variable is an ImagePainter
                                Image(
                                    painter = imagePainter,
                                    contentDescription = "Default image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .padding(
                                            start = 8.dp,
                                            end = 16.dp,
                                            top = 8.dp,
                                            bottom = 8.dp
                                        )
                                        .clip(shape = RectangleShape)
                                        .border(
                                            width = 2.dp,
                                            color = Color(android.graphics.Color.parseColor("#12CDD4")),
                                            shape = RectangleShape
                                        )
                                )
                            }

                            is ImageBitmap -> {
                                // The `imagePainter` variable is a Bitmap
                                Image(
                                    bitmap = imagePainter.asAndroidBitmap().asImageBitmap(),
                                    contentDescription = "Default image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(120.dp)
                                        .padding(
                                            start = 8.dp,
                                            end = 16.dp,
                                            top = 8.dp,
                                            bottom = 8.dp
                                        )
                                        .clip(shape = RectangleShape)
                                        .border(
                                            width = 2.dp,
                                            color = Color(android.graphics.Color.parseColor("#12CDD4")),
                                            shape = RectangleShape
                                        )
                                )
                            }
                        }
                    }
                    else{
                        // The `imagePainter` variable is neither an ImagePainter nor a Bitmap
                        Image(
                            painter = painterResource(R.drawable.no_image),
                            contentDescription = "Default image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                        )
                    }
                    Column(
                        Modifier.weight(1f)) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            TableCell(text = "Aeronave",tablefontSize,true, weight = 2f)
                            TableCell(text = dronedata.vehicle,tablefontSize, weight = 3f)
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            TableCell(text = "Proveedor",tablefontSize,true, weight = 2f)
                            TableCell(text = dronedata.provider,tablefontSize, weight = 3f)
                        }
                        Text(text = "Chapa de identificación", fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp))
                        Text(text = "Operadora: ${dronedata.operator}", fontSize = 12.sp,
                            color = Color(android.graphics.Color.parseColor("#808080")),
                            modifier = Modifier.padding(start = 8.dp))
                        Text(text = "Tlf: ${dronedata.telephone}", fontSize = 12.sp,
                            color = Color(android.graphics.Color.parseColor("#808080")),
                            modifier = Modifier.padding(start = 8.dp))
                        Text(text = "N/S: ${dronedata.serial}", fontSize = 12.sp,
                            color = Color(android.graphics.Color.parseColor("#808080")),
                            modifier = Modifier.padding(start = 8.dp))
                    }
                }
                Column(
                    Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        TableCell(text = droneAttribute[0],tablefontSize,true, weight = 2f)
                        TableCell(text = droneData[0] as String,tablefontSize, weight = 2f)
                        TableCell(text = droneAttribute[1],tablefontSize,true, weight = 2f)
                        TableCell(text = droneData[1] as String,tablefontSize, weight = 2f)
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        TableCell(text = droneAttribute[2],tablefontSize, true,weight = 2f)
                        TableCell(text = "${droneData[2]} Km/h",tablefontSize, weight = 2f)
                        TableCell(text = droneAttribute[3],tablefontSize,true, weight = 2f)
                        TableCell(text = "${droneData[3]} Wh",tablefontSize, weight = 2f)
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        TableCell(text = droneAttribute[4],tablefontSize,true, weight = 2f)
                        TableCell(text = "${droneData[4]} g",tablefontSize, weight = 2f)
                        TableCell(text = droneAttribute[5],tablefontSize,true, weight = 2f)
                        TableCell(text = "${droneData[5]} mAh",tablefontSize, weight = 2f)
                    }
                }
                Text(text = "Información adicional",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 17.sp)
                Text(text = "Este es el cuadro de información adicional de un drone",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    fontSize = 14.sp)
            }
        }
    }
}

/**
 * Displays a weather dialog with the given parameters.
 *
 * @param lastlocation The last location value.
 * @param firstlocation The first location value.
 * @param index The index value.
 * @param data The hourly data.
 * @param onDismiss Callback when the dialog is dismissed.
 */
@Composable
fun WeatherDialog(
    lastlocation: String,
    firstlocation: String,
    index: Int,
    data: HourlyData,
    onDismiss: () -> Unit,
){
    // Define weather attributes
    val weatherAttribute = listOf("Humedad", "Lluvia", "Presión", "Visibilidad", "Viento", "Dirección")

    // Define table font size
    val tablefontSize = 14.sp

    // Define semi-transparent gray color
    val grayColorSemiTransparent = Color.LightGray.copy(0.6f)

    // Evaluate flight possibility based on data and index
    val evaluation = data.evaluateFlightPossibility(index)

    // Display the weather dialog
    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false), onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .scale(0.9f),
            shape = RoundedCornerShape(70.dp),
            color = Color(evaluation.first)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Display weather icon, time, and temperature
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(top = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(data.getWeatherIcon(index)),
                        modifier = Modifier.size(60.dp),
                        contentDescription = null
                    )
                    Text(
                        text = data.time[index],
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                    Text(
                        text = "${data.temperature_2m[index].toInt()}°",
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp
                    )
                }

                // Display location information
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_route),
                        modifier = Modifier.size(36.dp),
                        contentDescription = null
                    )
                    Text(
                        text = "$firstlocation \n--- \n$lastlocation",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Display weather attributes in a table format
                Column(
                    Modifier
                        .fillMaxWidth(0.92f)
                        .background(
                            grayColorSemiTransparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        TableCell(
                            text = weatherAttribute[0],
                            fontSize = tablefontSize,
                            bold = true,
                            weight = 2f
                        )
                        TableCell(
                            text = "${data.relativehumidity_2m[index]}%",
                            fontSize = tablefontSize,
                            weight = 2f
                        )
                        TableCell(
                            text = weatherAttribute[1],
                            fontSize = tablefontSize,
                            bold = true,
                            weight = 2f
                        )
                        TableCell(
                            text = "${data.precipitation_probability[index]}%",
                            fontSize = tablefontSize,
                            weight = 2f
                        )
                    }

                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        TableCell(
                            text = weatherAttribute[2],
                            fontSize = tablefontSize,
                            bold = true,
                            weight = 2f
                        )
                        TableCell(
                            text = "${data.surface_pressure[index]} bar",
                            fontSize = tablefontSize,
                            weight = 2f
                        )
                        TableCell(
                            text = weatherAttribute[3],
                            fontSize = tablefontSize,
                            bold = true,
                            weight = 2f
                        )
                        TableCell(
                            text = "${data.visibility[index]} Km",
                            fontSize = tablefontSize,
                            weight = 2f
                        )
                    }

                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        TableCell(
                            text = weatherAttribute[4],
                            fontSize = tablefontSize,
                            bold = true,
                            weight = 2f
                        )
                        TableCell(
                            text = "${data.windspeed_10m[index]} Km/h",
                            fontSize = tablefontSize,
                            weight = 2f
                        )
                        TableCell(
                            text = weatherAttribute[5],
                            fontSize = tablefontSize,
                            bold = true,
                            weight = 2f
                        )
                        TableCell(
                            text = data.getWindDirection(index),
                            fontSize = tablefontSize,
                            weight = 2f
                        )
                    }
                }

                // Display flight evaluation icon and text
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Icon(
                        painter = painterResource(evaluation.second),
                        modifier = Modifier.size(24.dp),
                        contentDescription = null
                    )
                }

                Text(
                    text = evaluation.third,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp)
                        .background(
                            grayColorSemiTransparent,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    color = Color(android.graphics.Color.parseColor("#F24726"))
                )
            }
        }
    }
}

/**
 * Composable function that displays the advisor dialog for a specific airspace.
 *
 * @param airSpaceData The list of airspace data.
 * @param id The ID of the airspace for which to display the dialog.
 * @param onDismiss The callback function to be invoked when the dialog is dismissed.
 */
@Composable
fun AdvisorDialog(
    airSpaceData: SnapshotStateList<AirSpace>,
    id: String,
    onDismiss: () -> Unit,
) {
    // Get the current context
    val context = LocalContext.current

    // Create an instance of LocationViewModel
    val locationViewModel = LocationViewModel()

    // Find the matching airspace based on the provided ID
    val airSpace = airSpaceData.firstOrNull { it.id == id }!!

    // Fetch the AirMap rules for the airspace
    locationViewModel.getAirMapRules(context, airSpace.ruleset_id)

    // Observe the airspace rules from the LocationViewModel
    val airSpaceRules = locationViewModel.AirSpaceRule.observeAsState().value

    // Display the advisor dialog
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = true),
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .scale(0.9f),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Column() {
                // Display the header with a danger icon and title
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Cyan)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_danger),
                        modifier = Modifier
                            .size(40.dp)
                            .padding(vertical = 8.dp),
                        contentDescription = null
                    )
                    Text(
                        text = "ZONA DE RESTRINCIÓN DE VUELO",
                        modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(android.graphics.Color.parseColor("#F24726"))
                    )
                }

                // Display the airspace details in a LazyColumn
                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Nombre de la zona : \n${airSpace.name}")
                        Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Tipo de aviso : ${airSpace.type}")
                        Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Pais : ${airSpace.country}")
                        Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Región/Provincia : ${airSpace.state}")
                        Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Ciudad : ${airSpace.city}")
                        // Uncomment the following lines to display airspace rules
                        /*Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Nombre del conjunto de avisos : \n${airSpaceRules?.name}")
                        Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Descripción del conjunto : \n${airSpaceRules?.description}")
                        Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Tipos de zonas que aplica : \n${airSpaceRules?.airspace_types}")
                        Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Avisos de la zona")
                        */
                    }
                }
            }
        }
    }
}


/**
 * Composable function that displays text for an airspace.
 *
 * @param text The text to be displayed.
 */
@Composable
fun AirSpaceText(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth(),
        fontSize = 14.sp,
        textAlign = TextAlign.Justify,
        color = Color.Black
    )
}

/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DialogPreview() {
    AdvisorDialog(airSpace, onDismiss = {})
}
*/
