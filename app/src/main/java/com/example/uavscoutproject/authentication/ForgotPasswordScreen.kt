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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.uavscoutproject.R
import com.example.uavscoutproject.navigation.AppScreens

/**
 * Composable function for the Forgot Password screen.
 *
 * @param navController The navigation controller for navigating between screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    ForgotPassword(navController)
}

/**
 * Composable function for the Forgot Password UI.
 *
 * @param navController The navigation controller for navigating between screens.
 * @param viewModel The view model for the Authentication Screen.
 */
@ExperimentalMaterial3Api
@Composable
fun ForgotPassword(
    navController: NavHostController,
    viewModel: AuthenticationScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val email = remember { mutableStateOf("") }
    val fontSize = 17.sp
    val context = LocalContext.current
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
                    modifier = Modifier.padding(top = 85.dp)
                ) {
                    Text(
                        "Le enviaremos las instrucciones en el siguiente email:",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
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
                    label = { Text("Email de recuperación") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    textStyle = TextStyle(color = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)

                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {

                // Sign in button
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
                    border = BorderStroke(2.dp, Color.LightGray),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)

                ) {
                    Text(text = "Cancelar")
                }
                // Sign up button
                Button(
                    onClick = {
                        viewModel.resetPassword(email.value, context)
                        email.value = ""
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
                    Text(text = "Enviar")
                }
            }
        }

    }
}

/**
 * Preview function for the Forgot Password screen.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    val navController = rememberNavController()
    ForgotPasswordScreen(navController)
}
