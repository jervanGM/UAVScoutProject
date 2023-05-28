package com.example.uavscoutproject.mainscreen.home.dronedb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uavscoutproject.mainscreen.home.data.Dronedata

@Dao
interface PersonalDroneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drone: Dronedata)

    @Query("SELECT * FROM personal_drones")
    suspend fun getAll(): List<Dronedata>

    @Update
    suspend fun update(drone: Dronedata)

    @Delete
    suspend fun delete(drone: Dronedata)

}
