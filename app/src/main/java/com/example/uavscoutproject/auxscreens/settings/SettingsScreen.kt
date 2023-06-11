package com.example.uavscoutproject.auxscreens.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.uavscoutproject.NavAppbar
import com.example.uavscoutproject.R
import com.example.uavscoutproject.navigation.AppScreens
import com.example.uavscoutproject.navigation.bottomBar

@Composable
fun SettingsScreen(navController: NavHostController){
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
        bottomBar = { bottomBar() }
    ){ paddingValues ->
        Settings(padding = paddingValues)
    }
}

@Composable
fun Settings(padding: PaddingValues) {

}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview(){
    val navController = rememberNavController()
    SettingsScreen(navController)
}

