package com.example.uavscoutproject.mainscreen.home

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.uavscoutproject.NavAppbar
import com.example.uavscoutproject.R
import com.example.uavscoutproject.navigation.AppScreens
import com.example.uavscoutproject.navigation.BottomBar

/**
 * Composable function for displaying the news screen.
 *
 * @param navController The navigation controller used for navigating between screens.
 * @param url The URL of the web content to display.
 */
@Composable
fun NewsScreen(navController: NavHostController, url: String = "") {
    // Initialize scaffold state
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            // Display the custom navigation app bar with a back button
            NavAppbar(
                onNavigationIconClick = {
                    navController.navigate(AppScreens.MainScreen.route)
                },
                id = R.drawable.ic_back,
                buttonColor = R.color.back_button_color
            )
        },
        bottomBar = {
            // Display the bottom bar
            BottomBar()
        },
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) { paddingValues ->
        // Display the web content
        WebContent(url, padding = paddingValues)
    }
}

/**
 * Composable function for displaying the web content.
 *
 * @param url The URL of the web content to display.
 * @param padding The padding values for the content.
 */
@Composable
fun WebContent(url: String, padding: PaddingValues) {
    Column(
        modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        }, update = {
            it.loadUrl(url)
        })
    }
}

/**
 * Composable function for previewing the NewsScreen.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NewsScreenPreview() {
    val navController = rememberNavController()
    NewsScreen(navController)
}
