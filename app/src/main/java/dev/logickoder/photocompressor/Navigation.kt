package dev.logickoder.photocompressor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.logickoder.photocompressor.ui.screens.CompressedScreen
import dev.logickoder.photocompressor.ui.screens.HomeScreen

sealed class Navigation(
    val title: String,
    val icon: ImageVector,
    val route: String,
) {
    object Home : Navigation("Home", Icons.Default.Home, "/home")
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
        homeGraph()
        compressedGraph()
    }
}

fun NavGraphBuilder.homeGraph() {
    composable(Navigation.Home.route) {
        HomeScreen()
    }
}

fun NavGraphBuilder.compressedGraph() {
    composable(Navigation.Compressed.route) {
        CompressedScreen()
    }
}