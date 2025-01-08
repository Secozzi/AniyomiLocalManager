package xyz.secozzi.aniyomilocalmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.android.ext.android.inject
import xyz.secozzi.aniyomilocalmanager.preferences.AppearancePreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.collectAsState
import xyz.secozzi.aniyomilocalmanager.ui.home.HomeScreen
import xyz.secozzi.aniyomilocalmanager.ui.theme.AniyomiLocalManagerTheme
import xyz.secozzi.aniyomilocalmanager.ui.theme.DarkMode

class MainActivity : ComponentActivity() {
    private val appearancePreferences by inject<AppearancePreferences>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val dark by appearancePreferences.darkMode.collectAsState()
            val isSystemInDarkTheme = isSystemInDarkTheme()
            enableEdgeToEdge(
                SystemBarStyle.auto(
                    lightScrim = Color.White.toArgb(),
                    darkScrim = Color.White.toArgb(),
                ) { dark == DarkMode.Dark || (dark == DarkMode.System && isSystemInDarkTheme) },
            )

            AniyomiLocalManagerTheme {
                Navigator(screen = HomeScreen) {
                    SlideTransition(navigator = it)
                }
            }
        }
    }
}
