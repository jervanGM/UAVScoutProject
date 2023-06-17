package com.example.uavscoutproject.mainscreen.home.dronedb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uavscoutproject.mainscreen.home.data.Dronedata

/**
 * Data Access Object (DAO) interface for accessing personal drone data from the database.
 */
@Dao
interface PersonalDroneDao {
    /**
     * Inserts a new drone data into the database.
     *
     * @param drone The drone data to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drone: Dronedata)

    /**
     * Retrieves all personal drone data from the database.
     *
     * @return A list of all personal drone data stored in the database.
     */
    @Query("SELECT * FROM personal_drones")
    suspend fun getAll(): List<Dronedata>

    /**
     * Updates the specified drone data in the database.
     *
     * @param drone The updated drone data to be stored in the database.
     */
    @Update
    suspend fun update(drone: Dronedata)

    /**
     * Deletes the specified drone data from the database.
     *
     * @param drone The drone data to be deleted from the database.
     */
    @Delete
    suspend fun delete(drone: Dronedata)
}

