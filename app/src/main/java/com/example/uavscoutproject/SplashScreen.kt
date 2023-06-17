package com.example.uavscoutproject

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.uavscoutproject.authentication.AuthenticationScreenViewModel
import com.example.uavscoutproject.mainscreen.home.droneviewmodel.DroneViewModel
import com.example.uavscoutproject.mainscreen.home.newsapi.ArticleComposer
import com.example.uavscoutproject.mainscreen.home.newsapi.RetrofitClient
import com.example.uavscoutproject.mainscreen.location.viewmodel.LocationViewModel
import com.example.uavscoutproject.navigation.AppScreens
import com.example.uavscoutproject.preferences.MyPreferences

/**
 * Composable function that displays the splash screen.
 *
 * @param navController The navigation controller used for navigating between screens.
 * @param authenticationviewModel The view model for the authentication screen.
 * @param droneViewModel The view model for drone data.
 * @param locationViewModel The view model for location data.
 */
@Composable
fun SplashScreen(
    navController: NavHostController,
    authenticationviewModel: AuthenticationScreenViewModel = viewModel(),
    droneViewModel: DroneViewModel = viewModel(),
    locationViewModel: LocationViewModel
) {
    val context = LocalContext.current

    // Perform necessary tasks in the background
    LaunchedEffect(key1 = true) {
        droneViewModel.getDroneData()
        locationViewModel.getAirSpacesFromDB()
        newsDroneApi(context)

        // Determine the next screen to navigate to based on the user's authentication status
        val navigate = if (authenticationviewModel.isLoggedIn()) {
            val cloudSave = !MyPreferences(context).getBooleanSetting("isLocal")
            droneViewModel.getRouteData(cloudSave = cloudSave)
            droneViewModel.getPersonalDroneData(cloudSave = cloudSave)
            AppScreens.MainScreen.route
        } else {
            AppScreens.AuthenticationScreen.route
        }

        // Navigate to the next screen
        navController.popBackStack()
        navController.navigate(navigate) {
            popUpTo(AppScreens.SplashScreen.route) {
                inclusive = true
            }
            launchSingleTop = true
            anim {
                exit = android.R.anim.fade_out
                popExit = android.R.anim.fade_out
            }
        }
    }

    // Display the splash screen UI
    Splash()
}

/**
 * Composable function that displays the UI for the splash screen.
 */
@Composable
fun Splash() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Display the UAVScout logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo UAVScout",
            modifier = Modifier
                .size(150.dp, 150.dp)
                .align(Alignment.Center)
        )

        // Display the "UAV" and "Scout" text below the logo
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(bottom = 50.dp)
            ) {
                val fontSize = 19

                Text(
                    "UAV",
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize.sp,
                    color = Color(android.graphics.Color.parseColor("#12CDD4"))
                )

                Text(
                    "Scout",
                    fontWeight = FontWeight.Bold,
                    fontSize = fontSize.sp,
                    color = Color.Black
                )
            }
        }
    }
}

/**
 * Performs an API call to retrieve drone articles and inserts them into the database.
 *
 * @param context The application context.
 */
suspend fun newsDroneApi(context: Context) {
    val response = RetrofitClient.newsApiService.getDroneArticles(context.getString(R.string.news_api_key))

    if (response.isSuccessful) {
        val articles = response.body()?.articles

        if (!articles.isNullOrEmpty()) {
            ArticleComposer.insertList(articles)
        }
    } else {
        Log.d("API_ERROR", "Error: ${response.code()}")
    }
}
