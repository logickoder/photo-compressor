package dev.logickoder.photocompressor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dev.logickoder.photocompressor.ui.shared.components.BottomNavigation
import dev.logickoder.photocompressor.ui.theme.PhotoCompressionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PhotoCompressionTheme {
                val navController = rememberNavController()
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        bottomBar = {
                            BottomNavigation(
                                navController = navController,
                                items = listOf(Navigation.Home, Navigation.Compressed)
                            )
                        }
                    ) {
                        NavGraph(navController = navController, modifier = Modifier.padding(it))
                    }
                }
            }
        }
    }
}
