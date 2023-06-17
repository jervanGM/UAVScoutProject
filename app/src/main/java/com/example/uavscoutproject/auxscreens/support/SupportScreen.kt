package com.example.uavscoutproject.auxscreens.support

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
import com.example.uavscoutproject.navigation.BottomBar

/**
 * Composable function that represents the Support screen(TBD).
 *
 * @param navController The [NavHostController] used for navigation.
 */
@Composable
fun SupportScreen(navController: NavHostController) {
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
        bottomBar = { BottomBar() },
        backgroundColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Support(padding = paddingValues)
    }
}

/**
 * Composable function that represents the content of the Support screen(TBD).
 *
 * @param padding The padding values to apply to the content.
 */
@Composable
fun Support(padding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(bottom = padding.calculateBottomPadding()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Support Screen: TBD")
    }
}

/**
 * Preview function for the Support screen(TBD).
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SupportScreenPreview() {
    val navController = rememberNavController()
    SupportScreen(navController)
}
