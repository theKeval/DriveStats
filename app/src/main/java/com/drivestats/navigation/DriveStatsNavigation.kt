package com.drivestats.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import com.drivestats.R
import com.drivestats.feature.about.AboutScreen
import com.drivestats.feature.insights.InsightsScreen
import com.drivestats.feature.onboarding.OnboardingScreen
import com.drivestats.feature.permissions.PermissionsScreen
import com.drivestats.feature.settings.SettingsScreen
import com.drivestats.feature.stats.StatsScreen
import com.drivestats.feature.trips.TripDetailScreen
import com.drivestats.feature.trips.TripListScreen
import com.drivestats.feature.vehicles.VehiclesScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Permissions : Screen("permissions")
    object TripList : Screen("trips")
    object TripDetail : Screen("trips/{tripId}") {
        fun createRoute(tripId: Long) = "trips/$tripId"
    }
    object Stats : Screen("stats")
    object Vehicles : Screen("vehicles")
    object Insights : Screen("insights")
    object Settings : Screen("settings")
    object About : Screen("about")
}

@Composable
fun DriveStatsNavHost(
    navController: NavHostController,
    startDestination: String = Screen.TripList.route,
) {
    val topLevelRoutes = setOf(
        Screen.TripList.route,
        Screen.Stats.route,
        Screen.Vehicles.route,
        Screen.Settings.route,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in topLevelRoutes) {
                MainBottomNavigation(
                    currentRoute = currentRoute,
                    onTripsClick = {
                        navController.navigate(Screen.TripList.route) {
                            popUpTo(Screen.TripList.route)
                            launchSingleTop = true
                        }
                    },
                    onStatsClick = {
                        navController.navigate(Screen.Stats.route) {
                            popUpTo(Screen.TripList.route)
                            launchSingleTop = true
                        }
                    },
                    onVehiclesClick = {
                        navController.navigate(Screen.Vehicles.route) {
                            popUpTo(Screen.TripList.route)
                            launchSingleTop = true
                        }
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route) {
                            popUpTo(Screen.TripList.route)
                            launchSingleTop = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding),
        ) {

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinish = { navController.navigate(Screen.Permissions.route) },
                )
            }

            composable(Screen.Permissions.route) {
                PermissionsScreen(
                    onPermissionsGranted = {
                        navController.navigate(Screen.TripList.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(Screen.TripList.route) {
                TripListScreen(
                    onTripClick = { tripId ->
                        navController.navigate(Screen.TripDetail.createRoute(tripId))
                    },
                    onInsightsClick = { navController.navigate(Screen.Insights.route) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onAboutClick = { navController.navigate(Screen.About.route) },
                )
            }

            composable(Screen.Stats.route) {
                StatsScreen(onStartTripClick = { navController.navigate(Screen.TripList.route) })
            }

            composable(Screen.Vehicles.route) {
                VehiclesScreen()
            }

            composable(
                route = Screen.TripDetail.route,
                arguments = listOf(navArgument("tripId") { type = NavType.LongType }),
            ) { backStack ->
                val tripId = backStack.arguments!!.getLong("tripId")
                TripDetailScreen(
                    tripId = tripId,
                    onBack = { navController.navigateUp() },
                )
            }

            composable(Screen.Insights.route) {
                InsightsScreen(onBack = { navController.navigateUp() })
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.navigateUp() },
                    showBackButton = currentRoute !in topLevelRoutes,
                )
            }

            composable(Screen.About.route) {
                AboutScreen(onBack = { navController.navigateUp() })
            }
        }
    }
}

@Composable
private fun MainBottomNavigation(
    currentRoute: String?,
    onTripsClick: () -> Unit,
    onStatsClick: () -> Unit,
    onVehiclesClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == Screen.TripList.route,
            onClick = onTripsClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DirectionsCar,
                    contentDescription = stringResource(R.string.nav_trips_content_description),
                )
            },
            label = { Text(stringResource(R.string.nav_trips)) },
            colors = NavigationBarItemDefaults.colors(),
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Stats.route,
            onClick = onStatsClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.BarChart,
                    contentDescription = stringResource(R.string.nav_stats_content_description),
                )
            },
            label = { Text(stringResource(R.string.nav_stats)) },
            colors = NavigationBarItemDefaults.colors(),
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Vehicles.route,
            onClick = onVehiclesClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DirectionsBus,
                    contentDescription = stringResource(R.string.nav_vehicles_content_description),
                )
            },
            label = { Text(stringResource(R.string.nav_vehicles)) },
            colors = NavigationBarItemDefaults.colors(),
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Settings.route,
            onClick = onSettingsClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = stringResource(R.string.nav_settings_content_description),
                )
            },
            label = { Text(stringResource(R.string.nav_settings)) },
            colors = NavigationBarItemDefaults.colors(),
        )
    }
}
