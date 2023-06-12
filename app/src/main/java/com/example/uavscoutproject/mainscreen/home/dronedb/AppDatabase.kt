package com.example.uavscoutproject.mainscreen.home.dronedb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.uavscoutproject.mainscreen.home.data.Dronedata
import com.example.uavscoutproject.mainscreen.home.data.LastRoute

@Database(entities = [Dronedata::class, LastRoute::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personalDroneDao(): PersonalDroneDao

    abstract fun lastDroneDao(): LastRouteDao

}
