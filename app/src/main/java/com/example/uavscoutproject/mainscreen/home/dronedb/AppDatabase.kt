package com.example.uavscoutproject.mainscreen.home.dronedb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.uavscoutproject.mainscreen.home.data.Dronedata
import com.example.uavscoutproject.mainscreen.home.data.LastRoute

/**
 * Room database class that defines the database configuration and serves as the main access point
 * for interacting with the app's data.
 *
 * @property personalDroneDao The Data Access Object (DAO) for accessing and manipulating personal drone data.
 * @property lastDroneDao The Data Access Object (DAO) for accessing and manipulating last drone data.
 */
@Database(entities = [Dronedata::class, LastRoute::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Returns the Data Access Object (DAO) for personal drone data.
     *
     * @return The PersonalDroneDao object.
     */
    abstract fun personalDroneDao(): PersonalDroneDao

    /**
     * Returns the Data Access Object (DAO) for last drone data.
     *
     * @return The LastRouteDao object.
     */
    abstract fun lastDroneDao(): LastRouteDao
}
