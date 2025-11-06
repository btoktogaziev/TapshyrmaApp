package com.example.tapshyrmaapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tapshyrmaapp.presentation.ui.screens.Screens
import com.example.tapshyrmaapp.presentation.ui.screens.detail.DetailTaskScreen
import com.example.tapshyrmaapp.presentation.ui.screens.home.HomeScreen
import com.example.tapshyrmaapp.presentation.ui.theme.TapshyrmaAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TapshyrmaAppTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    TapshyrmaApp()
                }
            }
        }
    }

    @Composable
    private fun TapshyrmaApp() {
        val navController = rememberNavController()
        NavGraph(navController)
    }

    @Composable
    private fun NavGraph(
        navController: NavHostController,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = Screens.HomeScreen.route
            ) {
                composable(
                    Screens.HomeScreen.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(150)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(150)
                        )
                    }) {
                    HomeScreen(
                        toDetailScreen = { id ->
                            navController.navigate("${Screens.DetailTaskScreen.route}/$id")
                        },
                        onFloatingButtonClick = {
                            navController.navigate("${Screens.DetailTaskScreen.route}/-1")
                        },
                    )
                }
                composable(
                    "${Screens.DetailTaskScreen.route}/{id}",
                    arguments = listOf(navArgument("id") {
                        type = NavType.IntType
                    })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id") ?: -1
                    DetailTaskScreen(
                        id = id,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

