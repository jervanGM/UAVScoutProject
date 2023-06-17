package com.example.uavscoutproject.preferences

import android.content.Context
import com.example.uavscoutproject.R

/**
 * Helper class for managing user preferences using SharedPreferences.
 *
 * @param context The context used to access the SharedPreferences.
 */
class MyPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(
        R.string.prefs_file.toString(),
        Context.MODE_PRIVATE
    )

    /**
     * Sets the logged-in user email and password in SharedPreferences.
     *
     * @param email The user's email.
     * @param password The user's password.
     */
    fun setLoggedIn(email: String, password: String) {
        sharedPreferences.edit().putString("email", email).apply()
        sharedPreferences.edit().putString("password", password).apply()
    }

    /**
     * Clears the logged-in user's email and password from SharedPreferences.
     */
    fun clearLoggedIn() {
        sharedPreferences.edit().putString("email", null).apply()
        sharedPreferences.edit().putString("password", null).apply()
    }

    /**
     * Checks if a user is logged in by checking if email and password are set in SharedPreferences.
     *
     * @return `true` if a user is logged in, `false` otherwise.
     */
    fun isLoggedIn(): Boolean {
        val email = getEmail()
        val password = getPassword()
        return email != null && password != null
    }

    /**
     * Retrieves the logged-in user's email from SharedPreferences.
     *
     * @return The user's email, or `null` if not set.
     */
    fun getEmail(): String? {
        return sharedPreferences.getString("email", null)
    }

    /**
     * Retrieves the logged-in user's password from SharedPreferences.
     *
     * @return The user's password, or `null` if not set.
     */
    fun getPassword(): String? {
        return sharedPreferences.getString("password", null)
    }

    /**
     * Sets a boolean preference in SharedPreferences.
     *
     * @param preference The preference key.
     * @param settingState The state of the preference.
     */
    fun setBooleanSetting(preference: String, settingState: Boolean) {
        sharedPreferences.edit().putBoolean(preference, settingState).apply()
    }

    /**
     * Retrieves the value of a boolean preference from SharedPreferences.
     *
     * @param preference The preference key.
     * @return The value of the preference, or `false` if not set.
     */
    fun getBooleanSetting(preference: String): Boolean {
        return sharedPreferences.getBoolean(preference, false)
    }
}

