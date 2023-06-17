package com.example.uavscoutproject.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.uavscoutproject.R

/**
 * Composable function representing the back navigation bar.
 *
 * @param navController The [NavHostController] used for navigation.
 */
@Composable
fun BackNavBar(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(android.graphics.Color.parseColor("#559AB6")))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            // Back button
            IconButton(
                onClick = { navController.navigate(AppScreens.MainScreen.route) },
                modifier = Modifier
                    .size(32.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    tint = Color(android.graphics.Color.parseColor("#305471")),
                    modifier = Modifier
                        .size(32.dp),
                    contentDescription = null
                )
            }

            // Title
            Box(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "UAV",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(android.graphics.Color.parseColor(R.color.text_blue_Color.toString()))
                    )
                    Text(
                        "Scout",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.Black
                    )
                }
            }

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(45.dp)
            )
        }
    }
}

/**
 * Composable function representing the bottom navigation bar.
 */
@Composable
fun BottomBar() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Color(android.graphics.Color.parseColor("#559AB6")))
            .padding(top = 32.dp)
    )
}
