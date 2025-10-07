package xyz.secozzi.aniyomilocalmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import xyz.secozzi.aniyomilocalmanager.presentation.utils.predictiveHorizonal
import xyz.secozzi.aniyomilocalmanager.presentation.utils.slideHorizontal
import xyz.secozzi.aniyomilocalmanager.ui.home.HomeScreen
import xyz.secozzi.aniyomilocalmanager.ui.home.HomeRoute
import xyz.secozzi.aniyomilocalmanager.ui.theme.AniyomiLocalManagerTheme
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            enableEdgeToEdge()

            AniyomiLocalManagerTheme {
                Surface {
                    Navigator()
                }
            }
        }
    }

    @Composable
    private fun Navigator() {
        val backStack = rememberNavBackStack(HomeRoute)

        CompositionLocalProvider(
            LocalBackStack provides backStack,
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                transitionSpec = { slideHorizontal },
                popTransitionSpec = { slideHorizontal },
                predictivePopTransitionSpec = { predictiveHorizonal },
                entryProvider = entryProvider {
                    entry<HomeRoute> {
                        HomeScreen()
                    }
                },
            )
        }
    }
}
