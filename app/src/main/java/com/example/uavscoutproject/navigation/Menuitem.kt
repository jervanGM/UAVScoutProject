package com.example.uavscoutproject.navigation

import androidx.compose.ui.graphics.painter.Painter

data class Menuitem(
    val id: String,
    val title: String,
    val description : String,
    val icon: Painter
)
