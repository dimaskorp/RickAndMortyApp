package com.example.rickandmortyapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rickandmortyapp.presentation.characters.HomeScreen
import com.example.rickandmortyapp.presentation.details.DetailsScreen

object Routes {
    const val HOME = "home"
    const val DETAILS = "details/{id}"
}

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onOpenDetails = { id -> navController.navigate("details/$id") }
            )
        }
        composable(Routes.DETAILS) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
            DetailsScreen(id = id, onBack = { navController.popBackStack() })
        }
    }
}


