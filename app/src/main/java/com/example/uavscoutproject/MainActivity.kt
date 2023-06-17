package com.example.uavscoutproject

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.uavscoutproject.navigation.AppNavigation
import com.example.uavscoutproject.preferences.MyPreferences
import com.example.uavscoutproject.ui.theme.UAVScoutProjectTheme
import com.google.firebase.FirebaseApp

/**
 * The main activity of the UAVScout app.
 */
class MainActivity : ComponentActivity() {
    private lateinit var context: Context

    /**
     * Called when the activity is starting.
     *
     * Sets the default night mode to follow the system's night mode. Initializes the [context]
     * variable with the application's context. Sets the content of the activity using the
     * UAVScoutProjectTheme, which includes the dark mode setting obtained from [MyPreferences].
     * The theme's background color is used as the background color for the activity. Initializes
     * the FirebaseApp with the [context]. Displays the [AppNavigation] composable as the content
     * of the activity.
     *
     * @param savedInstanceState The saved instance state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        context = applicationContext
        setContent {
            UAVScoutProjectTheme(
                darkTheme = MyPreferences(context).getBooleanSetting("darkMode"),
                dynamicColor = false
            ) {
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

