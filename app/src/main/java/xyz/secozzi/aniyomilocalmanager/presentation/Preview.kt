package xyz.secozzi.aniyomilocalmanager.presentation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.ui.theme.AniyomiLocalManagerPreviewTheme
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

@Serializable
internal data object DummyRoute : NavKey

@Composable
fun PreviewContent(content: @Composable () -> Unit) {
    val backstack = rememberNavBackStack(DummyRoute)

    CompositionLocalProvider(
        LocalBackStack provides backstack,
    ) {
        AniyomiLocalManagerPreviewTheme {
            Surface {
                content()
            }
        }
    }
}
