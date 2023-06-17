package com.example.uavscoutproject.navigation

import androidx.compose.ui.graphics.painter.Painter

/**
 * Data class representing a menu item.
 *
 * @property id The unique identifier of the menu item.
 * @property title The title of the menu item.
 * @property description The description of the menu item.
 * @property icon The icon of the menu item as a [Painter].
 */
data class MenuItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: Painter
)

