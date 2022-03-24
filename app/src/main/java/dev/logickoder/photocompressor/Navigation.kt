package dev.logickoder.photocompressor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.logickoder.photocompressor.ui.screens.CompressedScreen
import dev.logickoder.photocompressor.ui.screens.HomeScreen

sealed class Navigation(
    val route: String,
) {
    object Home : Navigation("/home")
    object Compressed : Navigation("/compressed")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController,
        startDestination = Navigation.Home.route,
        modifier = modifier
    ) {
        homeGraph(navController)
        compressedGraph(navController)
    }
}

fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    composable(Navigation.Home.route) {
        HomeScreen {
            navController.navigate(Navigation.Compressed.route)
        }
    }
}

fun NavGraphBuilder.compressedGraph(navController: NavHostController) {
    composable(Navigation.Compressed.route) {
        CompressedScreen {
            navController.popBackStack()
        }
    }
}