package xyz.secozzi.aniyomilocalmanager.ui.preferences

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ColoredListItem
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

private data class PreferenceItem(
    @param:StringRes val title: Int,
    @param:StringRes val subtitle: Int,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@Serializable
data object PreferencesRoute : NavKey

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PreferencesScreen() {
    val backstack = LocalBackStack.current

    val appItems = persistentListOf(
        PreferenceItem(
            title = R.string.pref_appearance_title,
            subtitle = R.string.pref_appearance_summary,
            icon = Icons.Outlined.Palette,
            onClick = { backstack.add(AppearancePreferencesRoute) },
        ),
        PreferenceItem(
            title = R.string.pref_data_title,
            subtitle = R.string.pref_data_summary,
            icon = ImageVector.vectorResource(R.drawable.database_24px),
            onClick = { backstack.add(DataPreferencesRoute) },
        ),
    )

    val entryItems = persistentListOf(
        PreferenceItem(
            title = R.string.pref_mangabaka_title,
            subtitle = R.string.pref_mangabaka_summary,
            icon = Icons.Outlined.Book,
            onClick = { backstack.add(MangaBakaPreferencesRoute) },
        ),
        PreferenceItem(
            title = R.string.pref_anilist_title,
            subtitle = R.string.pref_anilist_summary,
            icon = ImageVector.vectorResource(R.drawable.anilist_icon),
            onClick = { backstack.add(AnilistPreferencesRoute) },
        ),
        PreferenceItem(
            title = R.string.pref_anidb_title,
            subtitle = R.string.pref_anidb_summary,
            icon = ImageVector.vectorResource(R.drawable.anidb_icon),
            onClick = { backstack.add(AniDBPreferencesRoute) },
        ),
        PreferenceItem(
            title = R.string.pref_mal_title,
            subtitle = R.string.pref_mal_summary,
            icon = ImageVector.vectorResource(R.drawable.mal_icon),
            onClick = { backstack.add(MyAnimeListPreferencesRoute) },
        ),
        PreferenceItem(
            title = R.string.pref_cover_title,
            subtitle = R.string.pref_cover_summary,
            icon = Icons.Outlined.Image,
            onClick = { backstack.add(CoverPreferencesRoute) },
        ),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.pref_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { backstack.removeLastOrNull() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                },
            )
        },
    ) { contentPadding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = contentPadding,
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            itemsIndexed(appItems) { index, item ->
                ColoredListItem(
                    title = stringResource(item.title),
                    subtitle = stringResource(item.subtitle),
                    icon = item.icon,
                    itemSize = appItems.size,
                    index = index,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = item.onClick,
                )
            }

            item { Spacer(Modifier.height(MaterialTheme.spacing.smaller)) }

            itemsIndexed(entryItems) { index, item ->
                ColoredListItem(
                    title = stringResource(item.title),
                    subtitle = stringResource(item.subtitle),
                    icon = item.icon,
                    itemSize = entryItems.size,
                    index = index,
                    color = MaterialTheme.colorScheme.tertiary,
                    onClick = item.onClick,
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun PreferencesScreenPreview() {
    PreviewContent {
        PreferencesScreen()
    }
}
