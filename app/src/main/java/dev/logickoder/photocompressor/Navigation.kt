package dev.logickoder.photocompressor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import dev.logickoder.photocompressor.ui.screens.home.HomeScreen

sealed class Navigation(
    val title: String,
    val icon: ImageVector,
    val route: String,
) {
    object Home : Navigation("Home", Icons.Default.Home, "/home") {
        object Index : Navigation("Home", icon, "$route/")
    }

    object Compressed : Navigation("Compressed", Icons.Default.AccountBox, "/compressed")
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

fun NavGraphBuilder.homeGraph(navController: NavController) {
    navigation(
        startDestination = Navigation.Home.Index.route,
        route = Navigation.Home.route,
    ) {
        composable(Navigation.Home.Index.route) {
            HomeScreen()
        }
    }
}

fun NavGraphBuilder.compressedGraph(navController: NavController) {
    composable(Navigation.Compressed.route) {

    }
}