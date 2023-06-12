package com.example.uavscoutproject.auxscreens.settings

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.uavscoutproject.NavAppbar
import com.example.uavscoutproject.R
import com.example.uavscoutproject.navigation.AppScreens
import com.example.uavscoutproject.navigation.bottomBar
import com.example.uavscoutproject.preferences.MyPreferences

@Composable
fun SettingsScreen(navController: NavHostController){
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
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
        bottomBar = { bottomBar() },
        backgroundColor = MaterialTheme.colorScheme.background
    ){ paddingValues ->
        Settings(padding = paddingValues,context)
    }
}

@Composable
fun Settings(padding: PaddingValues, context:Context) {
    val settings = listOf(
        Pair("Guardado local", "isLocal"),
        Pair("Modo oscuro", "darkMode"),
        Pair("Alto contraste", "highContrast"),
        Pair("Activar notificaciones", "activeNotifications")
    )

    Column(
        modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text ="Ajustes",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
        LazyColumn {
            items(settings.size){
                SettingItem(settings[it],context)
            }
        }
    }
}

@Composable
fun SettingItem(setting: Pair<String, String>, context: Context) {
    val preferences = MyPreferences(LocalContext.current)
    val isChecked = remember { mutableStateOf(preferences.getBooleanSetting(setting.second)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = setting.first,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = isChecked.value,
            onCheckedChange = { newChecked ->
                isChecked.value = newChecked
                preferences.setBooleanSetting(setting.second, isChecked.value)
                Toast.makeText(
                    context,
                    "Reinicia la aplicación para aplicar los cambios",
                    Toast.LENGTH_LONG )
                    .show()
            },
            modifier = Modifier.padding(end = 16.dp)
        )
    }
    Divider()
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview(){
    val navController = rememberNavController()
    SettingsScreen(navController)
}

