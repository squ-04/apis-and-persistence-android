package com.uniquindio.thecatapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uniquindio.thecatapp.core.navigation.AppRoutes
import com.uniquindio.thecatapp.features.detail.CatDetailScreen
import com.uniquindio.thecatapp.features.list.CatListScreen
import com.uniquindio.thecatapp.ui.theme.ThecatappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ThecatappTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppRoutes.LIST,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(route = AppRoutes.LIST) {
                            CatListScreen(
                                onNavigateToDetail = { catId ->
                                    navController.navigate(AppRoutes.detail(Uri.encode(catId)))
                                }
                            )
                        }

                        composable(
                            route = AppRoutes.DETAIL,
                            arguments = listOf(navArgument(AppRoutes.CAT_ID_ARG) { type = NavType.StringType })
                        ) {
                            CatDetailScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

