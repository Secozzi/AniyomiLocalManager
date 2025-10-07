package xyz.secozzi.aniyomilocalmanager.ui.home

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ChromeReaderMode
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.presentation.utils.TopLevelBackStack
import xyz.secozzi.aniyomilocalmanager.presentation.utils.predictiveHorizonal
import xyz.secozzi.aniyomilocalmanager.presentation.utils.slideHorizontal

@Serializable
data object HomeRoute : NavKey

private sealed class TopLevelRoute(val icon: ImageVector, @param:StringRes val stringRes: Int) {
    data object Manga : TopLevelRoute(Icons.AutoMirrored.Filled.ChromeReaderMode, R.string.label_manga)
    data object Anime : TopLevelRoute(Icons.Outlined.PlayCircleOutline, R.string.label_anime)
}

@Composable
fun HomeScreen() {
    val topLevelRoutes = listOf(TopLevelRoute.Manga, TopLevelRoute.Anime)
    val topLevelBackStack = remember { TopLevelBackStack<TopLevelRoute>(TopLevelRoute.Manga) }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        bottomBar = {
            NavigationBar {
                topLevelRoutes.forEach { topLevelRoute ->
                    val isSelected = topLevelRoute == topLevelBackStack.topLevelKey
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { topLevelBackStack.addTopLevel(topLevelRoute) },
                        label = {
                            Text(
                                text = stringResource(topLevelRoute.stringRes),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = topLevelRoute.icon,
                                contentDescription = null,
                            )
                        },
                    )
                }
            }
        },
    ) { _ ->
        NavDisplay(
            backStack = topLevelBackStack.backStack,
            onBack = { topLevelBackStack.removeLast() },
            transitionSpec = { slideHorizontal },
            popTransitionSpec = { slideHorizontal },
            predictivePopTransitionSpec = { predictiveHorizonal },
            entryProvider = entryProvider {
                entry<TopLevelRoute.Manga> {
                    Text("Manga Screen")
                }
                entry<TopLevelRoute.Anime> {
                    Text("Anime Screen")
                }
            },
        )
    }
}
