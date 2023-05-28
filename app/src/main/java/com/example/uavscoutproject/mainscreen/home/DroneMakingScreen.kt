package com.example.uavscoutproject.mainscreen.home

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImagePainter
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.uavscoutproject.NavAppbar
import com.example.uavscoutproject.R
import com.example.uavscoutproject.mainscreen.home.data.Dronedata
import com.example.uavscoutproject.mainscreen.home.droneviewmodel.DroneViewModel
import com.example.uavscoutproject.mainscreen.location.data.GeocodeItem
import com.example.uavscoutproject.mainscreen.location.data.Position
import com.example.uavscoutproject.navigation.AppScreens
import com.example.uavscoutproject.navigation.bottomBar
import kotlinx.coroutines.launch

@Composable
fun DroneMakingScreen(navController: NavHostController, edit: Boolean?, index: Int?,
                      droneViewModel: DroneViewModel = viewModel()) {

    val context = LocalContext.current
    val drone = if (edit == true && index != null) droneViewModel.getItem(index) else Dronedata()
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            NavAppbar(
            onNavigationIconClick = {
                navController.navigate(AppScreens.MainScreen.route)
            },
            id = R.drawable.ic_back,
            buttonColor = R.color.back_button_color
        )
        },
        bottomBar = {bottomBar()}
    ){ paddingValues ->
        DroneMaking(padding = paddingValues,context, drone,navController,edit,index)
    }




}

@Preview(showBackground = true,  heightDp = 1500)
@Composable
fun DroneMakingScreenPreview(){
    val navController = rememberNavController()
    DroneMakingScreen(navController, false,0)
}

@Composable
fun DroneMaking(
    padding: PaddingValues,
    context: Context,
    drone: Dronedata,
    navController: NavHostController,
    edit: Boolean?,
    index: Int?,
    droneViewModel: DroneViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var expanded by remember { mutableStateOf(false) }
    val fields = listOf("Nombre", "Aeronave", "Proveedor") //campos editables
    val fieldscar = listOf("Color", "Velocidad", "Peso neto",
        "Batería", "Energía", "Capacidad") //campos editables
    val fieldsc = listOf("Operadora", "Teléfono", "N/S") //campos editables
    val valuesinfo: MutableState<List<String>> = remember { mutableStateOf(emptyList()) }
    val valueschar: MutableState<List<String>> = remember { mutableStateOf(emptyList()) }
    val valuescha: MutableState<List<String>> = remember { mutableStateOf(emptyList()) }
    val items = droneViewModel.getDBList()
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val scrollState = rememberLazyListState()
    with(drone) {
        valuesinfo.value = mutableListOf(
            name, vehicle, provider
        )
        valueschar.value = mutableListOf(
            color, speed, weight, battery, energy, capacity
        )
        valuescha.value = mutableListOf(
            operator, telephone, serial
        )
    }
    if(drone.imgUri != ""){
        selectedImageUri = drone.imgUri.toUri()
    }
    val onImageSelected: (Uri) -> Unit = { uri ->
        selectedImageUri = uri
        // Realiza las acciones necesarias con la imagen seleccionada
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if(uri != null) {
            onImageSelected(uri)
        }
    }
    Column(Modifier.padding(bottom = padding.calculateBottomPadding())) {
        Box(
            Modifier
                .fillMaxSize()
                .weight(0.7f)) {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
                state = scrollState
            ) {
                item { DropDownMenu(
                    expanded,
                    onExpandedChange = { expanded = it },
                    items,
                    onDroneSelected = { newItem ->
                        with(newItem) {
                            valuesinfo.value = mutableListOf(
                                name, vehicle, provider
                            )
                            valueschar.value = mutableListOf(
                                color, speed, weight, battery, energy, capacity
                            )
                            valuescha.value = mutableListOf(
                                operator, telephone, serial
                            )
                        }
                        selectedImageUri = Uri.parse(newItem.imgUri)
                    }) }
                item { ImagePicker(selectedImageUri, launcher)}
                item { FieldsMaker("Información principal", fields, valuesinfo ) }
                item { FieldsMaker("Características de la aeronave", fieldscar,valueschar ) }
                item { FieldsMaker("Chapa de identificación", fieldsc,valuescha ) }
                val droneValues = (valuesinfo.value + valueschar.value + valuescha.value)
                item { EditCreateButtons(navController, droneValues, context, edit, index, selectedImageUri) }
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldsMaker(title: String,fields: List<String>, fieldval: MutableState<List<String>>){

    Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Column(Modifier.fillMaxWidth()) {
            fields.forEachIndexed { index,field ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = field,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    TextField(
                        value = fieldval.value[index],
                        onValueChange = { newValue ->
                            fieldval.value = fieldval.value
                                .toMutableList()
                                .also { it[index] = newValue } },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 11.sp
                        ),
                        placeholder = { Text("Enter your text") },
                        modifier = Modifier
                            .padding(8.dp)
                            .width(230.dp)
                            .heightIn(min = 18.dp)
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(4.dp)
                            ),
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            textColor = Color.Black,
                            cursorColor = Color.Black,
                        ),
                    )

                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EditCreateButtons(
    navController: NavHostController,
    droneValues: List<String>,
    context: Context,
    edit: Boolean?,
    index: Int?,
    selectedImageUri: Uri?,
    droneViewModel: DroneViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val droneData = Dronedata().apply {
        name = droneValues[0]
        vehicle = droneValues[1]
        provider = droneValues[2]
        color = droneValues[3]
        speed = droneValues[4]
        weight = droneValues[5]
        battery = droneValues[6]
        energy = droneValues[7]
        capacity = droneValues[8]
        operator = droneValues[9]
        telephone = droneValues[10]
        serial = droneValues[11]
        imgUri = if(selectedImageUri != null) selectedImageUri.toString() else ""
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {

        // Sign in button
        Button(
            onClick = { navController.navigate(AppScreens.MainScreen.route) },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor =
                Color(android.graphics.Color.parseColor("#414BB2"))
            ),
            border= BorderStroke(2.dp, Color.LightGray),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)

        ) {
            Text(text = "Cancelar", fontSize = 16.sp)
        }
        Button(
            onClick = {
                if (droneData.isNotBlank()) {
                    if (edit == true && index != null) {
                        droneData.id = droneViewModel.getItem(index).id
                        droneViewModel.editDrone(index, droneData)
                    } else {
                        droneViewModel.addDrone(droneData)
                    }
                    droneViewModel.savePersonalDroneData(
                        cloudSave = false,
                        localMode =
                        if(edit == true)
                            DroneViewModel.LocalMode.UPDATE
                        else DroneViewModel.LocalMode.SAVE,
                        localIndex = droneData
                    )
                    navController.navigate(AppScreens.MainScreen.route)
                } else {
                    showAlert(context)
                }

            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor =
                Color(android.graphics.Color.parseColor("#0CA789"))
            ),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(text = if(edit==true)"Editar aeronave" else "Crear aeronave",fontSize = 16.sp)
        }
    }
}


@Composable
fun ImagePicker(selectedImageUri: Uri?, launcher: ActivityResultLauncher<String>) {

    Box(
        modifier = Modifier
            .padding(horizontal = 50.dp, vertical = 16.dp)
            .clickable { launcher.launch("image/*") }
    ) {
        if (selectedImageUri != null) {
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
                        contentDescription = "Selected image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .size(150.dp, 250.dp)
                    )
                }

                is ImageBitmap -> {
                    // La variable `imagePainter` es un Bitmap
                    Image(
                        bitmap = imagePainter.asAndroidBitmap().asImageBitmap(),
                        contentDescription = "Selected image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .size(150.dp, 250.dp)
                    )
                }
            }
        }
        else{
                // La variable `imagePainter` no es ni un ImagePainter ni un Bitmap
                Image(
                    painter = painterResource(R.drawable.image_picker),
                    contentDescription = "Default image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .size(150.dp, 250.dp)
                )
        }
    }
}

fun loadBitmapFromUri(context: Context, uri: Uri): ImageBitmap {
    val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
    val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
    val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
    parcelFileDescriptor.close()
    return bitmap.asImageBitmap()
}

@Composable
fun DropDownMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<Dronedata>,
    onDroneSelected: (Dronedata) -> Unit,
) {
    var selectedItem by remember { mutableStateOf<String?>(null) }
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onExpandedChange(true) }
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(4.dp)
                )
                .height(50.dp),
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
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
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
                .fillMaxWidth()
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Row {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
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
                        selectedItem = item.name
                        onDroneSelected(item)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

fun showAlert(context: Context){
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Error")
    builder.setMessage("Se debe introducir al menos un parámetro para crear un drone")
    builder.setPositiveButton("Aceptar",null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}

