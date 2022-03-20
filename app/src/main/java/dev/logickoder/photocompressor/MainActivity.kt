package dev.logickoder.photocompressor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
                        topBar = {
                            TopAppBar(
                                title = { Text(text = stringResource(id = R.string.app_name)) }
                            )
                        },
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
