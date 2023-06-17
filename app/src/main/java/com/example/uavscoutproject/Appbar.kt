package com.example.uavscoutproject


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composable function that displays a customized navigation app bar.
 *
 * @param onNavigationIconClick Callback triggered when the navigation icon is clicked.
 * @param id The resource ID of the navigation icon.
 * @param buttonColor The color resource ID of the button tint.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavAppbar(
    onNavigationIconClick: () -> Unit,
    id: Int,
    buttonColor: Int = R.color.nav_button_color
) {
    // Display the top app bar
    TopAppBar(
        title = {
            // Center-aligned title
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Display "UAV" text
                    Text(
                        "UAV",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = colorResource(id = R.color.text_blue_Color)
                    )
                    // Display "Scout" text
                    Text(
                        "Scout",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.Black
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(android.graphics.Color.parseColor("#559AB6"))
        ),
        navigationIcon = {
            // Display navigation icon
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                IconButton(
                    onClick = { onNavigationIconClick.invoke() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(id),
                        tint = colorResource(id = buttonColor),
                        modifier = Modifier.size(32.dp),
                        contentDescription = null
                    )
                }
            }
        },
        actions = {
            Box(
                contentAlignment = Alignment.CenterEnd,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 16.dp)
            ) {
                // Display logo image
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.size(45.dp)
                )
            }
        }
    )
}


