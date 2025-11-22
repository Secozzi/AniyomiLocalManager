package xyz.secozzi.aniyomilocalmanager.presentation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.ui.theme.AniyomiLocalManagerPreviewTheme
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

@Serializable
internal data object DummyRoute : NavKey

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PreviewContent(content: @Composable () -> Unit) {
    val backstack = rememberNavBackStack(DummyRoute)

    val previewHandler = remember {
        AsyncImagePreviewHandler {
            ColorImage(Color.Red.toArgb())
        }
    }

    val navEvent = remember {
        object : NavigationEventDispatcherOwner {
            override val navigationEventDispatcher = NavigationEventDispatcher()
        }
    }

    CompositionLocalProvider(
        LocalBackStack provides backstack,
        LocalAsyncImagePreviewHandler provides previewHandler,
        LocalNavigationEventDispatcherOwner provides navEvent,
    ) {
        AniyomiLocalManagerPreviewTheme {
            Surface {
                content()
            }
        }
    }
}
