package com.example.uavscoutproject.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.uavscoutproject.mainscreen.MainScreen
import com.example.uavscoutproject.SplashScreen
import com.example.uavscoutproject.authentication.AuthenticationScreen
import com.example.uavscoutproject.authentication.ForgotPasswordScreen
import com.example.uavscoutproject.authentication.RegisterScreen
import com.example.uavscoutproject.auxscreens.profile.ProfileScreen
import com.example.uavscoutproject.auxscreens.rulesetinfo.RuleSetInfoSreen
import com.example.uavscoutproject.auxscreens.settings.SettingsScreen
import com.example.uavscoutproject.auxscreens.support.SupportScreen
import com.example.uavscoutproject.mainscreen.home.DroneMakingScreen
import com.example.uavscoutproject.mainscreen.home.NewsScreen
import com.example.uavscoutproject.mainscreen.home.droneviewmodel.DroneViewModel
import com.example.uavscoutproject.mainscreen.location.viewmodel.LocationViewModel


@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val droneViewModel: DroneViewModel = viewModel()
    val locationViewModel: LocationViewModel = viewModel()
    NavHost(
            navController = navController,
            startDestination = AppScreens.SplashScreen.route){
        composable(AppScreens.SplashScreen.route){
            SplashScreen(navController, droneViewModel = droneViewModel, locationViewModel = locationViewModel)
        }
        composable(route = AppScreens.MainScreen.route){
            MainScreen(navController, droneViewModel,locationViewModel)
        }
        composable(AppScreens.AuthenticationScreen.route){
            AuthenticationScreen(navController)
        }
        composable(AppScreens.RegisterScreen.route){
            RegisterScreen(navController)
        }
        composable(AppScreens.ForgotPasswordScreen.route){
            ForgotPasswordScreen(navController)
        }
        composable(route = AppScreens.DroneMakingScreen.route +
                "?edit={edit}&index={index}",
        arguments = listOf(
            navArgument("edit"){type = NavType.BoolType},
            navArgument("index"){type = NavType.IntType}
        )){backStackEntry ->
            val edit = backStackEntry.arguments?.getBoolean("edit")
            val index = backStackEntry.arguments?.getInt("index")
            DroneMakingScreen(navController,edit,index, droneViewModel)
        }
        composable(AppScreens.NewsScreen.route +
                "?url={url}",
            arguments = listOf(
                navArgument("url"){type = NavType.StringType}
            )){backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")
            requireNotNull(url)
            NewsScreen(navController, url)

        }
        composable(AppScreens.ProfileScreen.route){
            ProfileScreen(navController)
        }
        composable(AppScreens.SettingsScreen.route){
            SettingsScreen(navController)
        }
        composable(AppScreens.RuleSetInfoScreen.route){
            RuleSetInfoSreen(navController)
        }
        composable(AppScreens.SupportScreen.route){
            SupportScreen(navController)
        }

    }
}