package com.example.uavscoutproject.mainscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.uavscoutproject.mainscreen.home.HomeView

@Composable
fun UsersView(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("This is Tab 4 content")
            Spacer(modifier = Modifier.height(16.dp))
            Text("This is some additional content for Tab 4")
            Spacer(modifier = Modifier.height(16.dp))
            Checkbox(
                checked = true,
                onCheckedChange = { /* Do something */ },
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UsersPreview(){
    val navController = rememberNavController()
    UsersView(navController)
}