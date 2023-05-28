package com.example.uavscoutproject

//import com.airmap.airmapsdk.networking.services.AirMap
//import com.airmap.airmapsdk.util.AirMapConfig
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.room.Room
import com.example.uavscoutproject.mainscreen.home.dronedb.AppDatabase
import com.example.uavscoutproject.navigation.AppNavigation
import com.example.uavscoutproject.ui.theme.UAVScoutProjectTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        setContent {
            UAVScoutProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FirebaseApp.initializeApp(context)
                    AppNavigation()

                }
            }
        }
    }
}