package com.example.uavscoutproject

import android.app.Application
import androidx.room.Room
import com.example.uavscoutproject.mainscreen.home.dronedb.AppDatabase

/**
 * Application class for the UAVScout app.
 */
class UAVScoutApp : Application() {
    /**
     * Lazy instance of the application's database.
     *
     * The database is built using Room.databaseBuilder with the provided applicationContext
     * and the [AppDatabase] class. The name of the database is set to "drone-database".
     *
     * @return The built instance of the application's database.
     */
    val database by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "drone-database")
            .build()
    }
}
