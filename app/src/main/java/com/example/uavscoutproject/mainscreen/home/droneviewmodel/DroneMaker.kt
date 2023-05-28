package com.example.uavscoutproject.mainscreen.home.droneviewmodel

import androidx.compose.runtime.mutableStateListOf
import com.example.uavscoutproject.mainscreen.home.data.Dronedata

object DroneMaker {
    private val droneList = mutableStateListOf<Dronedata>()
    private val droneDBList = mutableStateListOf<Dronedata>()
    fun addDrone(newDrone: Dronedata) {
        droneList.add(newDrone)
    }

    fun editDrone(index: Int, editedDrone: Dronedata) {
        droneList[index] = editedDrone
    }

    fun deleteDrone(index: Int) {
        droneList.removeAt(index)
    }

    fun getItem(index: Int): Dronedata {
        return droneList[index]
    }

    fun setList(personalDrones : List<Dronedata>){
        droneList.clear()
        droneList.addAll(personalDrones)
    }

    fun getList(): List<Dronedata> {
        return droneList
    }

    fun setDBList(DBList :List<Dronedata> ){
        droneDBList.clear()
        droneDBList.addAll(DBList)
    }
    fun getDBList(): List<Dronedata> {
        return droneDBList
    }
}

