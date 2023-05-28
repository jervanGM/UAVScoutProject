package com.example.uavscoutproject

import android.app.Application
import androidx.room.Room
import com.example.uavscoutproject.mainscreen.home.dronedb.AppDatabase

class UAVScoutApp: Application(){
    val database by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "drone-database")
            .build()
    }
}