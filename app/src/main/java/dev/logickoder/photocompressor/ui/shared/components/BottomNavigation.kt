package dev.logickoder.photocompressor.ui.shared.components

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.logickoder.photocompressor.Navigation

@Composable
fun BottomNavigation(
    modifier: Modifier = Modifier,
    navController: NavController,
    items: List<Navigation>
) {
    BottomNavigation(
        modifier = modifier,
        contentColor = MaterialTheme.colors.primary,
        backgroundColor = Color.White,
    ) {
        val entry by navController.currentBackStackEntryAsState()
        val destination = entry?.destination
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = {
                    Text(text = item.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                selectedContentColor = MaterialTheme.colors.primaryVariant,
                unselectedContentColor = MaterialTheme.colors.primary,
                alwaysShowLabel = false,
                selected = destination?.hierarchy?.any {
                    it.route?.startsWith(item.route) == true
                } == true,
                onClick = { navController.bottomBarNavigate(item.route) }
            )
        }
    }
}

/**
 * Performs a navigation as if it was done by clicking the bottom nav item,
 * this is useful when navigating programmatically to a destination in another bottom nav item route,
 * and you want to be able to click any nav item after the navigation.
 *
 * @param route the route to navigate to
 */
fun NavController.bottomBarNavigate(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}