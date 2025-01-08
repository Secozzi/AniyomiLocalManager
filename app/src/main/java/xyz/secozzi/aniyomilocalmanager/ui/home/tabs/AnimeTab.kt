package xyz.secozzi.aniyomilocalmanager.ui.home.tabs

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.preferences.GeneralPreferences
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.PathText
import xyz.secozzi.aniyomilocalmanager.presentation.directorylist.DirectoryList
import xyz.secozzi.aniyomilocalmanager.presentation.directorylist.SelectStorage
import xyz.secozzi.aniyomilocalmanager.presentation.util.Tab
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.AnimeEntryScreen
import xyz.secozzi.aniyomilocalmanager.ui.preferences.PreferencesScreen

object AnimeTab : Tab {
    private fun readResolve(): Any = AnimeTab

    override val options: TabOptions
        @Composable
        get() {
            val image = rememberVectorPainter(Icons.Outlined.PlayCircleOutline)
            return TabOptions(
                index = 0u,
                title = stringResource(R.string.anime_tab),
                icon = image,
            )
        }

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val preferences = koinInject<GeneralPreferences>()

        val screenModel = rememberScreenModel { AnimeTabScreenModel(preferences) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        PathText(screenModel.storageLocation)
                    },
                    actions = {
                        IconButton(onClick = {
                            navigator.push(PreferencesScreen)
                        }) {
                            Icon(Icons.Default.Settings, null)
                        }
                    },
                )
            },
        ) { paddingValues ->
            val paddingModifier = Modifier.padding(paddingValues)

            if (screenModel.storageLocation.isBlank()) {
                SelectStorage(
                    label = stringResource(R.string.select_localanime_directory),
                    validateName = { it.equals(ANIME_DIRECTORY_NAME, true) },
                    onInvalid = {
                        val message = context.resources.getString(R.string.select_invalid_location)
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    },
                    onSelected = screenModel::updateStorageLocation,
                    modifier = paddingModifier,
                )
            } else {
                DirectoryList(
                    storagePath = screenModel.storageLocation,
                    isAnime = true,
                    modifier = paddingModifier,
                    onClick = { navigator.push(AnimeEntryScreen(it)) },
                    onError = {
                        SelectStorage(
                            label = stringResource(R.string.select_localanime_directory),
                            validateName = { it.equals(ANIME_DIRECTORY_NAME, true) },
                            onInvalid = {
                                val message = context.resources.getString(R.string.select_invalid_location)
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            },
                            onSelected = screenModel::updateStorageLocation,
                            modifier = paddingModifier,
                        )
                    },
                )
            }
        }
    }
}

const val ANIME_DIRECTORY_NAME = "localanime"
