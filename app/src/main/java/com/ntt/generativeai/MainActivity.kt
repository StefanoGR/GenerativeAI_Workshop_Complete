package com.ntt.generativeai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ntt.generativeai.summary.SummaryRoute
import com.ntt.generativeai.ui.AnalyzeScreen
import com.ntt.generativeai.ui.CameraScreen
import com.ntt.generativeai.ui.theme.GenerativeAITheme
import java.io.File

const val LOADING_SCREEN = "loading_screen"
const val CAMERA_SCREEN = "camera_screen"
const val ANALYZE_SCREEN = "analyze_screen"
const val SUMMARY_SCREEN = "summary_screen"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GenerativeAITheme {
                val scanned = remember { mutableStateListOf<File>() }
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
                            startDestination = CAMERA_SCREEN
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

                            composable(ANALYZE_SCREEN) {
                                AnalyzeScreen(Modifier, scanned)
                            }

                            composable(CAMERA_SCREEN) {
                                CameraScreen(Modifier, scanned) {
                                    navController.navigate(ANALYZE_SCREEN)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}