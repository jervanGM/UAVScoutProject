package com.example.uavscoutproject.mainscreen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.uavscoutproject.NavAppbar
import kotlinx.coroutines.launch
import com.example.uavscoutproject.R
import com.example.uavscoutproject.mainscreen.home.HomeView
import com.example.uavscoutproject.mainscreen.home.droneviewmodel.DroneViewModel
import com.example.uavscoutproject.mainscreen.location.LocationView
import com.example.uavscoutproject.mainscreen.location.viewmodel.LocationViewModel
import com.example.uavscoutproject.navigation.AppScreens
import com.example.uavscoutproject.navigation.DrawerBody
import com.example.uavscoutproject.navigation.DrawerHeader
import com.example.uavscoutproject.navigation.MenuItem

const val INDICATOR_WIDTH = 8


/**
 * Composable function for the main screen.
 *
 * @param navController The navigation controller for navigating between screens.
 * @param droneViewModel The view model for drone-related data.
 * @param locationViewModel The view model for location-related data.
 */
@Composable
fun MainScreen(
    navController: NavHostController,
    droneViewModel: DroneViewModel = viewModel(),
    locationViewModel: LocationViewModel = viewModel()
) {
    /**
     * List of titles for the tabs in the screen.
     */
    val tabTitles = listOf("Inicio", "Mapa", "Datos"/*, "Usuarios"*/)

    /**
     * State variables for managing the drawer and scaffold.
     */
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val isDrawerOpen = remember { mutableStateOf(false) }
    val drawerState = scaffoldState.drawerState

    /**
     * Update isDrawerOpen value when the drawer state changes.
     */
    LaunchedEffect(drawerState.isOpen) {
        isDrawerOpen.value = drawerState.isOpen
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            /**
             * Custom top app bar with navigation icon.
             */
            NavAppbar(
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                id = R.drawable.ic_menu
            )
        },
        drawerContent = {
            /**
             * Drawer content with header and body items.
             */
            DrawerHeader()
            DrawerBody(
                items = listOf(
                    // Menu items for navigation
                    MenuItem(
                        id = AppScreens.ProfileScreen.route,
                        title = "Perfil de piloto",
                        description = "Go to pilot profile screen",
                        icon = painterResource(id = R.drawable.ic_profile)
                    ),
                    MenuItem(
                        id = AppScreens.RuleSetInfoScreen.route,
                        title = "Reglamento",
                        description = "Go to ruleset screen",
                        icon = painterResource(id = R.drawable.ic_rules)
                    ),
                    MenuItem(
                        id = AppScreens.SettingsScreen.route,
                        title = "Ajustes",
                        description = "Go to settings screen",
                        icon = painterResource(id = R.drawable.ic_settings)
                    ),
                    MenuItem(
                        id = AppScreens.SupportScreen.route,
                        title = "Ayuda y soporte",
                        description = "Go to support screen",
                        icon = painterResource(id = R.drawable.ic_support)
                    )
                ),
                onItemClick = {
                    navController.navigate(it.id)
                },
                modifier = Modifier.padding(bottom = 16.dp),
                navController = navController
            )

        },
        drawerBackgroundColor = colorResource(id = R.color.colorBackground),
        drawerGesturesEnabled = isDrawerOpen.value,
        backgroundColor = MaterialTheme.colorScheme.background
    ){paddingValues ->
        /**
         * Composable function for the content of the scaffold.
         */
        ScaffoldContent(
            padding = paddingValues,
            tabTitles = tabTitles,
            navController = navController,
            locationViewModel,
            droneViewModel
        )
    }

}

/**
 * Modifier extension function to customize the tab indicator offset.
 *
 * @param currentTabPosition The current tab position.
 * @param tabWidth The width of the tab.
 * @return Modifier with custom tab indicator offset.
 */
fun Modifier.customTabIndicatorOffset(
    currentTabPosition: TabPosition,
    tabWidth: Dp
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "customTabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = tabWidth,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        targetValue = ((currentTabPosition.left + currentTabPosition.right - tabWidth) / 2),
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}

/**
 * Preview function for the MainScreen composable.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview(){
    val navController = rememberNavController()
    MainScreen(navController)
}

/**
 * Composable function for the content of the scaffold.
 *
 * @param padding The padding values for the content.
 * @param tabTitles The titles of the tabs.
 * @param navController The navigation controller.
 * @param locationViewModel The view model for location-related data.
 * @param droneViewModel The view model for drone-related data.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScaffoldContent(
    padding: PaddingValues,
    tabTitles: List<String>,
    navController: NavHostController,
    locationViewModel: LocationViewModel,
    droneViewModel: DroneViewModel
) {
    /**
     * Calculate the width of each tab indicator.
     */
    val tabWidth = tabTitles.map { it.length.dp * INDICATOR_WIDTH }

    /**
     * List of icons for the tabs.
     */
    val tabIcons = listOf(
        R.drawable.ic_home,
        R.drawable.ic_location,
        R.drawable.ic_data,
        //R.drawable.ic_social
    )

    /**
     * Pager state for managing tab content.
     */
    val pagerState = rememberPagerState()

    /**
     * Coroutine scope for launching animations.
     */
    val coroutineScope = rememberCoroutineScope()

    /**
     * Current selected tab index.
     */
    val selectedTabIndex = pagerState.currentPage

    Column(
        modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f)) {
            HorizontalPager(
                pageCount = tabTitles.size,
                state = pagerState,
                userScrollEnabled = false
            ) { page ->
                /**
                 * Content of each tab page.
                 */
                when (page) {
                    0 -> HomeView(navController, locationViewModel, droneViewModel)
                    1 -> LocationView(locationViewModel = locationViewModel)
                    2 -> DataView(
                        locationViewModel = locationViewModel,
                        droneViewModel = droneViewModel
                    )
                    //3 -> UsersView(navController)
                }
            }
        }

        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.9f)
                .padding(bottom = 18.dp),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    color = Color(android.graphics.Color.parseColor("#2D9BF0")),
                    height = 2.dp,
                    modifier = Modifier.customTabIndicatorOffset(
                        currentTabPosition = tabPositions[pagerState.currentPage],
                        tabWidth = tabWidth[selectedTabIndex]
                    )
                )
            },
            divider = { Divider(thickness = 0.dp, color = Color.Transparent) }
        ) {
            /**
             * Create a Tab composable for each tab title.
             */
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    selectedContentColor =
                    Color(android.graphics.Color.parseColor("#2D9BF0")),
                    unselectedContentColor = Color.Black,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = {  Text(
                        title,
                        fontSize = 13.sp,
                        onTextLayout = { textLayoutResult ->
                            textLayoutResult.size.width
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    ) },
                    icon = {
                        Icon(
                            painter = painterResource(id = tabIcons[index]),
                            modifier = Modifier.size(32.dp),
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}
