package com.drivestats

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.drivestats.navigation.DriveStatsNavHost
import com.drivestats.navigation.Screen
import com.drivestats.ui.theme.DriveStatsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs: SharedPreferences = getSharedPreferences("app_state", MODE_PRIVATE)
        val onboardingComplete = prefs.getBoolean("onboarding_complete", false)

        setContent {
            DriveStatsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    DriveStatsNavHost(
                        navController = navController,
                        startDestination = if (onboardingComplete) {
                            Screen.TripList.route
                        } else {
                            Screen.Onboarding.route
                        },
                    )
                }
            }
        }
    }
}
