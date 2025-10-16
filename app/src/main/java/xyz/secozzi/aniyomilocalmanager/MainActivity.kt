package xyz.secozzi.aniyomilocalmanager

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import org.koin.androidx.viewmodel.ext.android.viewModel
import xyz.secozzi.aniyomilocalmanager.navigation.Navigator
import xyz.secozzi.aniyomilocalmanager.ui.home.anime.AnimeScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.home.manga.MangaScreenViewModel
import xyz.secozzi.aniyomilocalmanager.ui.theme.AniyomiLocalManagerTheme

class MainActivity : ComponentActivity() {
    private val mangaScreenViewModel by viewModel<MangaScreenViewModel>()
    private val animeScreenViewModel by viewModel<AnimeScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delaySplashScreen()

        setContent {
            enableEdgeToEdge()

            AniyomiLocalManagerTheme {
                Surface {
                    Navigator()
                }
            }
        }
    }

    private fun delaySplashScreen() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (animeScreenViewModel.isLoaded || mangaScreenViewModel.isLoaded) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            },
        )
    }
}
