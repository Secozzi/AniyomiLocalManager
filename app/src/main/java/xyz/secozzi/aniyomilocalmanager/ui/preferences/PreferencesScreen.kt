package xyz.secozzi.aniyomilocalmanager.ui.preferences

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.preference
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.presentation.Screen

object PreferencesScreen : Screen() {
    private fun readResolve(): Any = PreferencesScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.pref_settings_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    },
                )
            },
        ) { paddingValues ->
            ProvidePreferenceLocals {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                ) {
                    preference(
                        key = "appearance",
                        title = { Text(text = stringResource(R.string.pref_appearance_title)) },
                        summary = { Text(text = stringResource(R.string.pref_appearance_summary)) },
                        icon = { Icon(Icons.Outlined.Palette, null) },
                        onClick = { navigator.push(AppearancePreferencesScreen) },
                    )
                    preference(
                        key = "data",
                        title = { Text(text = stringResource(R.string.pref_data_title)) },
                        summary = { Text(text = stringResource(R.string.pref_data_summary)) },
                        icon = { Icon(ImageVector.vectorResource(R.drawable.database_24px), null) },
                        onClick = { navigator.push(DataScreen) },
                    )
                    preference(
                        key = "anilist",
                        title = { Text(text = stringResource(R.string.pref_anilist_title)) },
                        summary = { Text(text = stringResource(R.string.pref_anilist_summary)) },
                        icon = { Icon(ImageVector.vectorResource(R.drawable.anilist_icon), null) },
                        onClick = { navigator.push(AniListPreferencesScreen) },
                    )
                    preference(
                        key = "anidb",
                        title = { Text(text = stringResource(R.string.pref_anidb_title)) },
                        summary = { Text(text = stringResource(R.string.pref_anidb_summary)) },
                        icon = { Icon(ImageVector.vectorResource(R.drawable.anidb_icon), null) },
                        onClick = { navigator.push(AniDBPreferencesScreen) },
                    )
                    preference(
                        key = "cover",
                        title = { Text(text = stringResource(R.string.pref_cover_title)) },
                        summary = { Text(text = stringResource(R.string.pref_cover_summary)) },
                        icon = { Icon(Icons.Outlined.Image, null) },
                        onClick = { navigator.push(CoverPreferencesScreen) },
                    )
                }
            }
        }
    }
}
