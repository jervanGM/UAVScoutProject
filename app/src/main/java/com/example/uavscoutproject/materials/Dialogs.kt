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
                                // La variable `imagePainter` es un ImagePainter
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
                                // La variable `imagePainter` es un Bitmap
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
                        // La variable `imagePainter` no es ni un ImagePainter ni un Bitmap
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

@Composable
fun WeatherDialog(
    lastlocation: String,
    firstlocation: String,
    index: Int,
    data: HourlyData,
    onDismiss: () -> Unit,
){
    val weatherAttribute = listOf("Humedad", "Lluvia",
        "Presión","Visibilidad",
        "Viento", "Dirección")
    val tablefontSize = 14.sp
    val grayColorSemiTransparent = Color.LightGray.copy(0.6f)
    val evaluation = data.evaluateFlightPossibility(index)
    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .scale(0.9f),
            shape = RoundedCornerShape(70.dp),
            color = Color(evaluation.first)
        ) {
            Column(modifier = Modifier.padding(16.dp),
                   horizontalAlignment = Alignment.CenterHorizontally) {

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(top = 16.dp)){
                    Icon(
                        painter = painterResource(data.getWeatherIcon(index)),
                        modifier = Modifier
                            .size(60.dp),
                        contentDescription = null
                    )
                    Text(text = data.time[index],
                         fontWeight = FontWeight.Bold,
                         fontSize = 36.sp)
                    Text(text = "${data.temperature_2m[index].toInt()}°",
                         fontWeight = FontWeight.Bold,
                         fontSize = 36.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(vertical = 16.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_route),
                        modifier = Modifier
                            .size(36.dp),
                        contentDescription = null
                    )
                    Text(text = "$firstlocation \n" +
                                    "--- \n" +
                                    lastlocation,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 8.dp),
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(
                    Modifier
                        .fillMaxWidth(0.92f)
                        .background(
                            grayColorSemiTransparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        TableCell(text = weatherAttribute[0],tablefontSize,true, weight = 2f)
                        TableCell(text = "${data.relativehumidity_2m[index]}%",tablefontSize, weight = 2f)
                        TableCell(text = weatherAttribute[1],tablefontSize,true, weight = 2f)
                        TableCell(text = "${data.precipitation_probability[index]}%",tablefontSize, weight = 2f)
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        TableCell(text = weatherAttribute[2],tablefontSize, true,weight = 2f)
                        TableCell(text = "${data.surface_pressure[index]} bar",tablefontSize, weight = 2f)
                        TableCell(text = weatherAttribute[3],tablefontSize,true, weight = 2f)
                        TableCell(text = "${data.visibility[index]} Km",tablefontSize, weight = 2f)
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        TableCell(text = weatherAttribute[4],tablefontSize,true, weight = 2f)
                        TableCell(text = "${data.windspeed_10m[index]} Km/h",tablefontSize, weight = 2f)
                        TableCell(text = weatherAttribute[5],tablefontSize,true, weight = 2f)
                        TableCell(text = data.getWindDirection(index),tablefontSize, weight = 2f)
                    }
                }
                Row(horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)) {
                    Icon(
                        painter = painterResource(evaluation.second),
                        modifier = Modifier
                            .size(24.dp),
                        contentDescription = null
                    )
                }

                Text(text = evaluation.third,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 50.dp)
                        .background(grayColorSemiTransparent, shape = RoundedCornerShape(16.dp)),
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    color = Color(android.graphics.Color.parseColor("#F24726"))
                )
            }
        }
    }

}

@Composable
fun AdvisorDialog(
    airSpaceData: SnapshotStateList<AirSpace>,
    id:String,
    onDismiss: () -> Unit,
){
    val context = LocalContext.current
    val locationViewModel = LocationViewModel()
    val airSpace = airSpaceData.firstOrNull { it.id == id }!!
    locationViewModel.getAirMapRules(context,airSpace.ruleset_id)
    val airSpaceRules = locationViewModel.AirSpaceRule.observeAsState().value
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
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color.Cyan
                            )
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
                        /*Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Nombre del conjunto de avisos : \n${airSpaceRules?.name}")
                        Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Descripción del conjunto : \n${airSpaceRules?.description}")
                        Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Tipos de zonas que aplica : \n${airSpaceRules?.airspace_types}")
                        Divider(color = Color.Gray, thickness = 1.dp)
                        AirSpaceText(text = "Avisos de la zona")
                    }
                    airSpaceRules?.rules?.size?.let { it ->
                        items(it){itemIt->
                            AirSpaceText(text = airSpaceRules.rules[itemIt].description)
                        }*/
                    }
                }
            }
        }
    }
}

@Composable
fun AirSpaceText(text:String){
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth(),
        fontSize = 14.sp,
        textAlign = TextAlign.Justify,
        color = Color.Black
    )
}


/*@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DialogPreview(){
    AdvisorDialog(airSpace,onDismiss = {})
}*/