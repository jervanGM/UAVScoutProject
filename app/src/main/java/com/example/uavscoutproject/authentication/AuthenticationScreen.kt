package com.example.uavscoutproject.authentication


import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.uavscoutproject.R
import com.example.uavscoutproject.navigation.AppScreens
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthenticationScreen(navController: NavHostController) {
    //val activity = LocalContext.current as Activity

    // Fijar orientación a modo vertical. Habilitar en version final
    //activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    Authentication(navController)
}

@ExperimentalMaterial3Api
@Composable
fun Authentication(navController: NavHostController,
                   viewModel: AuthenticationScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val fontSize = 17.sp
    val iconSize = 19
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract =ActivityResultContracts.StartActivityForResult()
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try{
            val account = task.getResult(ApiException::class.java)
            email.value = account?.email.toString()
            viewModel.setLoggedIn(email.value,"GoogleSign")
            email.value = ""
            val credential = GoogleAuthProvider.getCredential(account.idToken,null)
            viewModel.signInWithGoogle(credential,context){
                navController.clearBackStack(AppScreens.MainScreen.route)
                navController.navigate(AppScreens.MainScreen.route) {
                    popUpTo(0)
                    launchSingleTop = true
                    anim {
                        exit = android.R.anim.fade_out
                        popExit = android.R.anim.fade_out
                    }
                }
            }
        }
        catch (ex:Exception){
            Log.d("FalloAuth","GoogleSignIn failure")
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.inicio_sesion),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo UAVScout",
                modifier = Modifier
                    .size(140.dp, 140.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(bottom = 40.dp)
                ) {
                    Text(
                        "UAV",
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSize,
                        color = Color(android.graphics.Color.parseColor("#12CDD4"))
                    )
                    Text(
                        "Scout",
                        fontWeight = FontWeight.Bold,
                        fontSize = fontSize,
                        color = Color.Black
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 30.dp)
                ) {
                    Text(
                        "Iniciar Sesión",
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        color = Color.White
                    )
                }
            }

                // Email field
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            focusedIndicatorColor =
                            Color(android.graphics.Color.parseColor("#414BB2")),
                            unfocusedIndicatorColor = Color.Gray,
                            focusedLabelColor =
                            Color(android.graphics.Color.parseColor("#414BB2"))
                        ),
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        textStyle = TextStyle(color = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.9f)
                            .padding(bottom = 16.dp),
                        singleLine = true

                    )
                }

                // Password field
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White,
                            focusedIndicatorColor =
                            Color(android.graphics.Color.parseColor("#414BB2")),
                            unfocusedIndicatorColor = Color.Gray,
                            focusedLabelColor =
                            Color(android.graphics.Color.parseColor("#414BB2"))
                        ),
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        textStyle = TextStyle(color = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.9f)
                            .padding(bottom = 16.dp),
                        singleLine = true
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Sign up button
                    Button(
                        onClick = { navController.navigate(AppScreens.RegisterScreen.route) {
                                        anim {
                                            exit = android.R.anim.fade_out
                                            popExit = android.R.anim.fade_out
                                        }
                                   } },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                            Color(android.graphics.Color.parseColor("#0CA789"))
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(text = "Registrarse")
                    }
                    // Sign in button
                    Button(
                        onClick = { viewModel.signInEmailPassword(email.value,password.value,context){
                            navController.popBackStack()
                            navController.navigate(AppScreens.MainScreen.route) {
                                popUpTo(0)
                                launchSingleTop = true
                                anim {
                                    exit = android.R.anim.fade_out
                                    popExit = android.R.anim.fade_out
                                }

                            }
                            viewModel.setLoggedIn(email.value,password.value)
                        } },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =Color.White,
                            contentColor =
                            Color(android.graphics.Color.parseColor("#414BB2"))
                        ),
                        border=BorderStroke(2.dp, Color.LightGray),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)

                    ) {
                        Text(text = "Acceder")
                    }
                }
            // Forgot password text
            ClickableText(
                text = AnnotatedString("¿Has olvidado la contraseña?"),
                style = MaterialTheme.typography.bodyMedium.merge(
                    TextStyle(
                        color=Color(android.graphics.Color.parseColor("#3F53D9")),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp),
                onClick = {
                    navController.navigate(AppScreens.ForgotPasswordScreen.route) {
                        anim {
                            exit = android.R.anim.fade_out
                            popExit = android.R.anim.fade_out
                        }
                    }
                }
            )

            // Sign in with Google button
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Button(
                    onClick = {
                                val opciones = GoogleSignInOptions.Builder(
                                    GoogleSignInOptions.DEFAULT_SIGN_IN
                                ).requestIdToken(context.getString(R.string.default_web_client_id))
                                 .requestEmail()
                                 .build()
                                val googleClient =  GoogleSignIn.getClient(context,opciones)
                                googleClient.signOut()
                                launcher.launch(googleClient.signInIntent)
                              },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor =
                        Color(android.graphics.Color.parseColor("#414BB2"))
                    ),
                    border = BorderStroke(2.dp, Color.LightGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Iniciar sesión con Google")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Iniciar sesión con Google",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(iconSize.dp)
                    )
                }
            }

        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AuthenticationSreenPreview(){
    val navController = rememberNavController()
    AuthenticationScreen(navController)
}
