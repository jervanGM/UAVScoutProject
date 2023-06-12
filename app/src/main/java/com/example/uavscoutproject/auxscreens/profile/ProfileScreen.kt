package com.example.uavscoutproject.auxscreens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.uavscoutproject.NavAppbar
import com.example.uavscoutproject.R
import com.example.uavscoutproject.navigation.AppScreens
import com.example.uavscoutproject.navigation.bottomBar

@Composable
fun ProfileScreen(navController: NavHostController){
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
        bottomBar = { bottomBar() },
        backgroundColor = MaterialTheme.colorScheme.background
    ){ paddingValues ->
        Profile(padding = paddingValues)
    }
}

@Composable
fun Profile(padding: PaddingValues) {
    Column(modifier = Modifier.fillMaxSize()
        .padding(bottom = padding.calculateBottomPadding()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        Text("Profile Screen: TBD")
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview(){
    val navController = rememberNavController()
    ProfileScreen(navController)
}