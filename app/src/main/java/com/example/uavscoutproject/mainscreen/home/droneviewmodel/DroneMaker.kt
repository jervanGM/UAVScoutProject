package com.example.uavscoutproject.mainscreen.home.droneviewmodel

import androidx.compose.runtime.mutableStateListOf
import com.example.uavscoutproject.mainscreen.home.data.Dronedata

/**
 * The DroneMaker object is responsible for managing drone-related data.
 */
object DroneMaker {
    private val droneList = mutableStateListOf<Dronedata>()
    private val droneDBList = mutableStateListOf<Dronedata>()

    /**
     * Adds a new drone to the list of drones.
     *
     * @param newDrone The drone to be added.
     */
    fun addDrone(newDrone: Dronedata) {
        droneList.add(newDrone)
    }

    /**
     * Edits the drone at the specified index with the edited drone data.
     *
     * @param index The index of the drone to be edited.
     * @param editedDrone The edited drone data.
     */
    fun editDrone(index: Int, editedDrone: Dronedata) {
        droneList[index] = editedDrone
    }

    /**
     * Deletes the drone at the specified index.
     *
     * @param index The index of the drone to be deleted.
     */
    fun deleteDrone(index: Int) {
        droneList.removeAt(index)
    }

    /**
     * Returns the drone at the specified index.
     *
     * @param index The index of the drone to retrieve.
     * @return The drone at the specified index.
     */
    fun getItem(index: Int): Dronedata {
        return droneList[index]
    }

    /**
     * Sets the list of personal drones.
     *
     * @param personalDrones The list of personal drones.
     */
    fun setList(personalDrones: List<Dronedata>) {
        droneList.clear()
        droneList.addAll(personalDrones)
    }

    /**
     * Returns the list of personal drones.
     *
     * @return The list of personal drones.
     */
    fun getList(): List<Dronedata> {
        return droneList
    }

    /**
     * Sets the list of drones from the database.
     *
     * @param DBList The list of drones from the database.
     */
    fun setDBList(DBList: List<Dronedata>) {
        droneDBList.clear()
        droneDBList.addAll(DBList)
    }

    /**
     * Returns the list of drones from the database.
     *
     * @return The list of drones from the database.
     */
    fun getDBList(): List<Dronedata> {
        return droneDBList
    }
}


