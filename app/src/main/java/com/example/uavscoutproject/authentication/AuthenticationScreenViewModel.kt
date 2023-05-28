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

class AuthenticationScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth
    private val _loading = MutableLiveData(false)
    private val myPreferences = MyPreferences(application)

    fun setLoggedIn(email: String, password: String)
    {
        myPreferences.setLoggedIn(email,password)
    }
    fun clearLogIn(){
        myPreferences.clearLoggedIn()
    }
    fun getEmail(): String?{
        return myPreferences.getEmail()
    }
    fun getPassword(): String?{
        return myPreferences.getPassword()
    }
    fun isLoggedIn(): Boolean {
        return myPreferences.isLoggedIn()
    }
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun signInEmailPassword(
        email: String,
        password: String,
        context: Context,
        mainscreen: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("LogConcedido", "signInWithEmailAndPassword logueado!!!")
                            mainscreen()
                        }
                        else {
                            Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (ex: Exception) {
                Log.d("LogConcedido", "signInWithEmailAndPassword  ${ex.message}")
            }
        }
    fun signInWithGoogle(credential:AuthCredential,context: Context, mainscreen: () -> Unit) =
        viewModelScope.launch {

            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("LogConcedido", "signInWithEmailAndPassword logueado!!!")
                            mainscreen()
                        }
                        else {
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

    fun registerEmailPassword(email: String,
                              password: String,
                              repeatpassword: String,
                              context: Context,
                              mainscreen: () -> Unit){
        try {
        if(password == repeatpassword && isValidEmail(email) && password.isNotEmpty()) {
            if (_loading.value == false) {
                _loading.value = true
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Firebase.auth.currentUser?.sendEmailVerification()
                            mainscreen()
                            Toast.makeText(context, "Se ha enviado un correo de verificación a su bandeja de entrada",
                                Toast.LENGTH_SHORT).show()
                        } else {
                            showAlert(context)
                        }
                        _loading.value = false
                    }
            }
        }
        else if (!isValidEmail(email) || password.isEmpty()){
            Toast.makeText(context, "Email y contraseña deben rellenarse correctamente", Toast.LENGTH_SHORT).show()
        }
        else if(password != repeatpassword){
            Toast.makeText(context, "Los dos campos de contraseña no coinciden", Toast.LENGTH_SHORT).show()
        }
        else{
            /**Programación defensiva **/
        }
        } catch (ex: Exception) {
            Log.d("LogConcedido", "registerInWithEmailAndPassword  ${ex.message}")
        }
    }
    fun resetPassword(email: String, context: Context){

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
    private fun showAlert(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario.Revise el email y contraseña o credenciales")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}