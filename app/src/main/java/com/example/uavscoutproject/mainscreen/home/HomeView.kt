package com.example.uavscoutproject.mainscreen.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.uavscoutproject.R
import com.example.uavscoutproject.mainscreen.location.viewmodel.LocationViewModel
import com.example.uavscoutproject.mainscreen.home.data.Article
import com.example.uavscoutproject.mainscreen.home.data.Dronedata
import com.example.uavscoutproject.mainscreen.home.droneviewmodel.DroneViewModel
import com.example.uavscoutproject.mainscreen.home.newsapi.ArticleComposer
import com.example.uavscoutproject.materials.DroneDialog
import com.example.uavscoutproject.navigation.AppScreens
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeView(navController: NavHostController,
             locationViewModel: LocationViewModel = viewModel(),
             droneViewModel: DroneViewModel = viewModel()) {
    val scrollState = rememberLazyListState()
    val droneList by remember { mutableStateOf(droneViewModel.getList()) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, bottom = 16.dp),
            state = scrollState
        ) {
            item { DronesSection(navController,droneList, droneViewModel) }
            item { LastFlightSection(locationViewModel) }
            item { NewsSection(navController) }
        }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePreview(){
    val navController = rememberNavController()
    HomeView(navController)
}

@Composable
fun DronesSection(
    navController: NavHostController,
    droneList: List<Dronedata>,
    droneViewModel: DroneViewModel = viewModel()
) {
    val EDIT = true
    val NOTEDIT = false
    val FALSEINDEX = 0
    Column(Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Drones propietarios",
                Modifier.align(Alignment.CenterVertically),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = { navController.navigate(AppScreens.DroneMakingScreen.route +
                        "?edit=$NOTEDIT&index=$FALSEINDEX")},
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    modifier = Modifier.size(32.dp),
                    contentDescription = null
                )
            }
        }
        droneList.forEachIndexed { index, drone ->
            DroneItem(drone, index,
                onEdit = { editedIndex ->
                    navController.navigate(AppScreens.DroneMakingScreen.route +
                    "?edit=$EDIT&index=$editedIndex")
                },
                onItemRemoved = { removedIndex ->
                    droneViewModel.savePersonalDroneData(
                        cloudSave = false,
                        localMode = DroneViewModel.LocalMode.DELETE,
                        localIndex = droneViewModel.getItem(removedIndex))
                    droneViewModel.deleteDrone(removedIndex)
                }
            )
        }

    }
}



@Composable
fun DroneItem(dronedata: Dronedata,
              index: Int,
              onEdit: (Int) -> Unit,
              onItemRemoved: (Int) -> Unit) {
    var show by remember { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(onClick = { show = true }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(show){
            DroneDialog(dronedata, onDismiss = {show = false})
        }

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
                            .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                            .clip(shape = RectangleShape)
                            .border(
                                width = 2.dp,
                                color = Color(android.graphics.Color.parseColor("#12CDD4")),
                                shape = RectangleShape)
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
                            .padding(start = 8.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                            .clip(shape = RectangleShape)
                            .border(
                                width = 2.dp,
                                color = Color(android.graphics.Color.parseColor("#12CDD4")),
                                shape = RectangleShape)
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
        Column(Modifier.weight(1f),
               verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Aeronave : ${dronedata.vehicle}",
                fontSize = 14.sp,
                color = Color(android.graphics.Color.parseColor("#808080"))
            )
            Text(
                text = dronedata.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(text = "Sample text", fontSize = 14.sp)
            Text(text = "ID: ${dronedata.serial}", fontSize = 14.sp)
        }
        Column(modifier = Modifier.padding(horizontal = 12.dp),
               verticalArrangement = Arrangement.spacedBy(24.dp)) {
            IconButton(
                onClick = { onItemRemoved(index) },
                modifier = Modifier
                    .size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_bin),
                    modifier = Modifier
                        .size(32.dp),
                    contentDescription = null
                )
            }
            IconButton(
                onClick = { onEdit(index)},
                modifier = Modifier
                    .size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    modifier = Modifier
                        .size(32.dp),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun LastFlightSection(locationViewModel: LocationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val flightAttribute = listOf("Ruta", "Distancia recorrida",
        "Tiempo de vuelo", "Clima")

    val locationlist = locationViewModel.locationDataList
    val firstlocation = if(locationlist.isNotEmpty()) locationlist.first().title else " "
    val lastlocation = if(locationlist.isNotEmpty()) locationlist.last().title else " "
    val flightData = listOf("${firstlocation} - ${lastlocation}", "13,2 Km",
        "26 min", "Estable") // clase de datos
    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)

        ) {
            Text(
                text = "Último vuelo",
                Modifier.align(Alignment.CenterVertically),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { /* Perform sign in action */ },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                    Color(android.graphics.Color.parseColor("#2D9BF0"))
                ),
                modifier = Modifier
                    .heightIn(min = 18.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fly),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Volar", fontSize = 12.sp)
            }
        }
        flightAttribute.forEachIndexed { index, attribute ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .topBottomBorder(1.dp, Color.Gray)
                    .padding(vertical = 8.dp)) {
                TableCell(text = attribute, fontSize = 17.sp, weight = 3f)
                TableCell(text = flightData[index],fontSize = 17.sp, weight = 2f, center = true)
            }
        }
    }
}

@Composable
fun RowScope.TableCell(text: String, fontSize: TextUnit, bold:Boolean = false, weight: Float, center: Boolean = false) {
    Text(
        text = text,
        Modifier
            .weight(weight)
            .padding(horizontal = 8.dp),
        fontSize = fontSize,
        softWrap = false,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        textAlign = if (center) TextAlign.Center else TextAlign.Start
    )
}

fun Modifier.topBottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height

            drawLine(
                color = color,
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = width , y = 0f),
                strokeWidth = strokeWidthPx
            )

            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width , y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

@Composable
fun NewsSection(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val articles = ArticleComposer.getList()
    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Text(
                text = "Noticias destacadas",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        Column(Modifier.fillMaxWidth()) {
            articles.take(20).chunked(2).forEach { fila ->
                Row(Modifier.fillMaxWidth()) {
                    fila.forEach { noticia ->
                        NoticiaDestacada(Modifier.weight(1f),
                            onClick = {
                                navController.navigate(AppScreens.NewsScreen.route +
                                        "?url=${noticia.url}")//Moverse a la pantalla de noticias
                            },
                            article = noticia)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
fun NoticiaDestacada(modifier: Modifier = Modifier, onClick: () -> Unit, article: Article) {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val publishedAt = LocalDateTime.parse(article.publishedAt, formatter)
    val now = LocalDateTime.now()
    val duration = Duration.between(publishedAt, now)
    val days = duration.toDays()
    val words = article.title.split(" ")
    val truncatedTitle = if (words.size > 10) {
        val truncatedWords = words.take(10)
        val truncatedText = truncatedWords.joinToString(" ")
        "$truncatedText..."
    } else {
        article.title
    }
    Column(
        modifier
            .padding(8.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(article.urlToImage)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.aspectRatio(16f/9f)
        )
        Row( modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
            Text(
                text = truncatedTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${days}d",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}