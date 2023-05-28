package com.example.uavscoutproject.mainscreen.home.dronedb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.uavscoutproject.mainscreen.home.data.Dronedata

@Database(entities = [Dronedata::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personalDroneDao(): PersonalDroneDao
}
