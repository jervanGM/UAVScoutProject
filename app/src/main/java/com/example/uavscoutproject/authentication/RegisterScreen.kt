package com.example.uavscoutproject.authentication


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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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

/**
 * Composable function for the Register screen.
 *
 * @param navController The navigation controller used to navigate between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController) {
    Register(navController)
}

/**
 * Composable function for the Register UI.
 *
 * @param navController The navigation controller used to navigate between screens.
 * @param viewModel The view model for the authentication screen.
 */
@ExperimentalMaterial3Api
@Composable
fun Register(
    navController: NavHostController,
    viewModel: AuthenticationScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordRepeat = remember { mutableStateOf("") }
    val fontSize = 17.sp
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
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
            // Logo Image
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
                // Title
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
                        "Registrarse",
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
                        .padding(bottom = 16.dp)

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
                        .padding(bottom = 16.dp)
                )
            }

            // Repeat Password field
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = passwordRepeat.value,
                    onValueChange = { passwordRepeat.value = it },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor =
                        Color(android.graphics.Color.parseColor("#414BB2")),
                        unfocusedIndicatorColor = Color.Gray,
                        focusedLabelColor =
                        Color(android.graphics.Color.parseColor("#414BB2"))
                    ),
                    label = { Text("Repeat Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    textStyle = TextStyle(color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth(fraction = 0.9f)
                        .padding(bottom = 16.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Cancel button
                Button(
                    onClick = {
                        navController.navigate(AppScreens.AuthenticationScreen.route) {
                            anim {
                                exit = android.R.anim.fade_out
                                popExit = android.R.anim.fade_out
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor =
                        Color(android.graphics.Color.parseColor("#414BB2"))
                    ),
                    border= BorderStroke(2.dp, Color.LightGray),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(text = "Cancelar")
                }

                // Sign up button
                Button(
                    onClick = {
                        viewModel.registerEmailPassword(email.value, password.value, passwordRepeat.value, context) {
                            navController.popBackStack()
                            navController.navigate(AppScreens.MainScreen.route) {
                                popUpTo(0)
                                launchSingleTop = true
                                anim {
                                    exit = android.R.anim.fade_out
                                    popExit = android.R.anim.fade_out
                                }
                            }
                            viewModel.setLoggedIn(email.value, password.value)
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                        Color(android.graphics.Color.parseColor("#0CA789"))
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(text = "Registrarse")
                }
            }
        }
    }
}

/**
 * Preview function for the Register screen.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview(){
    val navController = rememberNavController()
    RegisterScreen(navController)
}
