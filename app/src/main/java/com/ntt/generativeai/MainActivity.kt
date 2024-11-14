package com.ntt.generativeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ntt.generativeai.summary.SummaryRoute
import com.ntt.generativeai.ui.theme.GenerativeAITheme

const val LOADING_SCREEN = "loading_screen"
const val CAMERA_SCREEN = "camera_screen"
const val SUMMARY_SCREEN = "summary_screen"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GenerativeAITheme {
                Scaffold() { innerPadding ->
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = Color.White,
                    ) {
                        val navController = rememberNavController()

                        NavHost(
                            navController = navController,
                            startDestination = LOADING_SCREEN
                        ) {
                            composable(LOADING_SCREEN) {
                                LoadingRoute(
                                    onModelLoaded = {
                                        navController.navigate(SUMMARY_SCREEN) {
                                            popUpTo(LOADING_SCREEN) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }

                            composable(SUMMARY_SCREEN) {
                                SummaryRoute()
                            }
                        }
                    }
                }
            }
        }
    }
}