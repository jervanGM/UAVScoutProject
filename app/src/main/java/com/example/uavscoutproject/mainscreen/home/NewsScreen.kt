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
import com.example.uavscoutproject.navigation.bottomBar

@Composable
fun NewsScreen(navController: NavHostController, url: String = "") {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {NavAppbar(
                    onNavigationIconClick = {
                        navController.navigate(AppScreens.MainScreen.route)
                    },
                    id = R.drawable.ic_back,
                    buttonColor = R.color.back_button_color
                )},
        bottomBar = {bottomBar()},
        backgroundColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ){ paddingValues ->
        WebContent(url,padding = paddingValues)
    }

}

@Preview(showBackground = true,  showSystemUi = true)
@Composable
fun NewsScreenPreview(){
    val navController = rememberNavController()
    NewsScreen(navController)
}


@Composable
fun WebContent(url: String, padding: PaddingValues){
    Column( // (2)
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