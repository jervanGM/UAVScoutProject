package com.example.uavscoutproject.mainscreen.home.dronedb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.uavscoutproject.mainscreen.home.data.LastRoute

/**
 * Data Access Object (DAO) interface for accessing the last route data from the database.
 */
@Dao
interface LastRouteDao {
    /**
     * Inserts a new last route data into the database.
     *
     * @param route The last route data to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(route: LastRoute)

    /**
     * Retrieves the last route data from the database.
     *
     * @return The last route data stored in the database.
     */
    @Query("SELECT * FROM last_route")
    suspend fun get(): LastRoute

    /**
     * Updates the specified last route data in the database.
     *
     * @param route The updated last route data to be stored in the database.
     */
    @Update
    suspend fun update(route: LastRoute)
}
