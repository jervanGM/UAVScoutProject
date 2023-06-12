package com.example.uavscoutproject.mainscreen.home.dronedb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uavscoutproject.mainscreen.home.data.LastRoute

@Dao
interface LastRouteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(route: LastRoute)

    @Query("SELECT * FROM last_route")
    suspend fun get(): LastRoute

    @Update
    suspend fun update(route: LastRoute)

}