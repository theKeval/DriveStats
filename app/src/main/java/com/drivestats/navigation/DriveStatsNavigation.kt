package com.drivestats.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.drivestats.feature.insights.InsightsScreen
import com.drivestats.feature.onboarding.OnboardingScreen
import com.drivestats.feature.permissions.PermissionsScreen
import com.drivestats.feature.settings.SettingsScreen
import com.drivestats.feature.trips.TripDetailScreen
import com.drivestats.feature.trips.TripListScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Permissions : Screen("permissions")
    object TripList : Screen("trips")
    object TripDetail : Screen("trips/{tripId}") {
        fun createRoute(tripId: Long) = "trips/$tripId"
    }
    object Insights : Screen("insights")
    object Settings : Screen("settings")
}

@Composable
fun DriveStatsNavHost(
    navController: NavHostController,
    startDestination: String = Screen.TripList.route,
) {
    NavHost(navController = navController, startDestination = startDestination) {

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
            )
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
            SettingsScreen(onBack = { navController.navigateUp() })
        }
    }
}
