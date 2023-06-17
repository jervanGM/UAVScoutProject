package com.example.uavscoutproject.authentication

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.uavscoutproject.preferences.MyPreferences
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * ViewModel class for the Authentication Screen.
 *
 * @property application The application context.
 */
class AuthenticationScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    private val myPreferences = MyPreferences(application)

    /**
     * Sets the logged-in status with the provided email and password.
     *
     * @param email The user's email.
     * @param password The user's password.
     */
    fun setLoggedIn(email: String, password: String) {
        myPreferences.setLoggedIn(email, password)
    }

    /**
     * Clears the logged-in status.
     */
    fun clearLogIn() {
        myPreferences.clearLoggedIn()
    }

    /**
     * Retrieves the stored email.
     *
     * @return The stored email.
     */
    fun getEmail(): String? {
        return myPreferences.getEmail()
    }

    /**
     * Retrieves the stored password.
     *
     * @return The stored password.
     */
    fun getPassword(): String? {
        return myPreferences.getPassword()
    }

    /**
     * Checks if the user is logged in.
     *
     * @return `true` if the user is logged in, `false` otherwise.
     */
    fun isLoggedIn(): Boolean {
        return myPreferences.isLoggedIn()
    }

    /**
     * Validates if the provided email is in a valid format.
     *
     * @param email The email to validate.
     * @return `true` if the email is valid, `false` otherwise.
     */
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Signs in the user using email and password.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @param context The context used for displaying Toast messages.
     * @param mainscreen A callback to be invoked when sign-in is successful.
     */
    fun signInEmailPassword(
        email: String,
        password: String,
        context: Context,
        mainscreen: () -> Unit
    ) = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("LogConcedido", "signInWithEmailAndPassword logueado!!!")
                        mainscreen()
                    } else {
                        Toast.makeText(
                            context,
                            "Usuario o contraseña incorrectos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } catch (ex: Exception) {
            Log.d("LogConcedido", "signInWithEmailAndPassword  ${ex.message}")
        }
    }

    /**
     * Signs in the user using Google authentication credentials.
     *
     * @param credential The Google authentication credential.
     * @param context The context used for displaying Toast messages.
     * @param mainscreen A callback to be invoked when sign-in is successful.
     */
    fun signInWithGoogle(
        credential: AuthCredential,
        context: Context,
        mainscreen: () -> Unit
    ) = viewModelScope.launch {

        try {
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("LogConcedido", "signInWithEmailAndPassword logueado!!!")
                        mainscreen()
                    } else {
                        showAlert(context)
                    }
                }
        } catch (ex: Exception) {
            Log.d("LogConcedido", "signInWithEmailAndPassword  ${ex.localizedMessage}")
        }
    }

    companion object {
        private const val TAG = "AuthenticationScreenViewModel"
        private const val RC_SIGN_IN = 9001
    }

    /**
     * Registers the user using email and password.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @param repeatpassword The repeated password for confirmation.
     * @param context The context used for displaying Toast messages.
     * @param mainscreen A callback to be invoked when registration is successful.
     */
    fun registerEmailPassword(
        email: String,
        password: String,
        repeatpassword: String,
        context: Context,
        mainscreen: () -> Unit
    ) {
        try {
            if (password == repeatpassword && isValidEmail(email) && password.isNotEmpty()) {
                if (_loading.value == false) {
                    _loading.value = true
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Firebase.auth.currentUser?.sendEmailVerification()
                                mainscreen()
                                Toast.makeText(
                                    context,
                                    "Se ha enviado un correo de verificación a su bandeja de entrada",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                showAlert(context)
                            }
                            _loading.value = false
                        }
                }
            } else if (!isValidEmail(email) || password.isEmpty()) {
                Toast.makeText(
                    context,
                    "Email y contraseña deben rellenarse correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password != repeatpassword) {
                Toast.makeText(
                    context,
                    "Los dos campos de contraseña no coinciden",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                /**Programación defensiva **/
            }
        } catch (ex: Exception) {
            Log.d("LogConcedido", "registerInWithEmailAndPassword  ${ex.message}")
        }
    }

    /**
     * Sends a password reset email to the provided email address.
     *
     * @param email The user's email address.
     * @param context The context used for displaying Toast messages.
     */
    fun resetPassword(email: String, context: Context) {

        if (email.isEmpty()) {
            Toast.makeText(context, "Rellene un email para enviar", Toast.LENGTH_SHORT).show()
        } else {

            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Email enviado", Toast.LENGTH_SHORT).show()
                    } else {
                        showAlert(context)
                    }
                }
        }
    }

    /**
     * Shows an alert dialog with an error message.
     *
     * @param context The context used for displaying the dialog.
     */
    private fun showAlert(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario.Revise el email y contraseña o credenciales")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}
