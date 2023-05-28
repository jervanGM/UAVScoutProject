package com.example.uavscoutproject.preferences

import android.content.Context
import android.provider.Settings.Secure.getString
import com.example.uavscoutproject.R

class MyPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(R.string.prefs_file.toString(), Context.MODE_PRIVATE)

    fun setLoggedIn( email: String, password: String) {
        sharedPreferences.edit().putString("email", email).apply()
        sharedPreferences.edit().putString("password", password).apply()
    }
    fun clearLoggedIn(){
        sharedPreferences.edit().putString("email", null).apply()
        sharedPreferences.edit().putString("password", null).apply()
    }
    fun isLoggedIn(): Boolean {
        val email = getEmail()
        val password = getPassword()
        if(email != null && password !=null){
            return true
        }
        return false
    }
    fun getEmail(): String? {
        return sharedPreferences.getString("email", null)
    }
    fun getPassword(): String? {
        return sharedPreferences.getString("password", null)
    }
}
