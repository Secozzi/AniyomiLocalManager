package xyz.secozzi.aniyomilocalmanager.ui.preferences

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.anggrayudi.storage.file.getBasePath
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.database.domain.AnimeTrackerRepository
import xyz.secozzi.aniyomilocalmanager.database.domain.MangaTrackerRepository
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.preferences.DataPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.collectAsState
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.settings.ConfirmDialog
import xyz.secozzi.aniyomilocalmanager.presentation.settings.SettingsListItem
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

@Serializable
data object DataPreferencesRoute : NavKey

@Composable
fun DataPreferencesScreen() {
    val context = LocalContext.current
    val resources = LocalResources.current
    val backstack = LocalBackStack.current

    val preferences = koinInject<DataPreferences>()
    val storageManager = koinInject<StorageManager>()
    val viewModel = koinViewModel<DataPreferencesScreenModel>()

    val dialog by viewModel.dialog.collectAsState()
    val animeLocation by preferences.animeStorageLocation.collectAsState()
    val animeLocationName by remember {
        derivedStateOf { storageManager.getFile(animeLocation)!!.getBasePath(context) }
    }
    val mangaLocation by preferences.mangaStorageLocation.collectAsState()
    val mangaLocationName by remember {
        derivedStateOf { storageManager.getFile(mangaLocation)!!.getBasePath(context) }
    }

    DataPreferencesScreenContent(
        onBack = { backstack.removeLastOrNull() },
        onClickClearIds = { viewModel.setConfirmDialog() },
        animeLocation = animeLocationName,
        onAnimeLocationClicked = {
            preferences.animeStorageLocation.delete()
            Toast.makeText(
                context,
                resources.getString(R.string.pref_data_localanime_cleared),
                Toast.LENGTH_SHORT,
            ).show()
        },
        mangaLocation = mangaLocationName,
        onMangaLocationClicked = {
            preferences.mangaStorageLocation.delete()
            Toast.makeText(
                context,
                resources.getString(R.string.pref_data_localmanga_cleared),
                Toast.LENGTH_SHORT,
            ).show()
        },
    )

    when (dialog) {
        DataPreferencesScreenModel.Dialog.ConfirmDeleteIds -> {
            ConfirmDialog(
                onConfirm = {
                    viewModel.deleteTrackerData()
                    viewModel.dismissDialog()
                },
                onDismiss = { viewModel.dismissDialog() },
            )
        }
        null -> {}
    }
}

class DataPreferencesScreenModel(
    private val mangaTrackerRepository: MangaTrackerRepository,
    private val animeTrackerRepository: AnimeTrackerRepository,
) : ViewModel() {
    private val _dialog = MutableStateFlow<Dialog?>(null)
    val dialog = _dialog.asStateFlow()

    sealed interface Dialog {
        data object ConfirmDeleteIds : Dialog
    }

    fun deleteTrackerData() {
        viewModelScope.launch {
            mangaTrackerRepository.clearTrackerIds()
            animeTrackerRepository.clearTrackerIds()
        }
    }

    fun setConfirmDialog() {
        _dialog.update { _ -> Dialog.ConfirmDeleteIds }
    }

    fun dismissDialog() {
        _dialog.update { _ -> null }
    }
}

@Composable
private fun DataPreferencesScreenContent(
    onBack: () -> Unit,
    onClickClearIds: () -> Unit,
    animeLocation: String,
    onAnimeLocationClicked: () -> Unit,
    mangaLocation: String,
    onMangaLocationClicked: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.pref_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_data_tracker_ids),
                    icon = ImageVector.vectorResource(R.drawable.database_off_24px),
                    itemSize = 3,
                    index = 0,
                    onClick = onClickClearIds,
                )
            }

            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_data_localanime),
                    icon = Icons.Default.VideoLibrary,
                    itemSize = 3,
                    index = 1,
                    supportingContent = { if (animeLocation.isNotBlank()) Text(animeLocation) },
                    onClick = onAnimeLocationClicked,
                )
            }

            item {
                SettingsListItem(
                    title = stringResource(R.string.pref_data_localmanga),
                    icon = Icons.Default.PermMedia,
                    itemSize = 3,
                    index = 2,
                    supportingContent = { if (mangaLocation.isNotBlank()) Text(mangaLocation) },
                    onClick = onMangaLocationClicked,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun DataPreferencesScreenPreview() {
    PreviewContent {
        DataPreferencesScreenContent(
            onBack = { },
            onClickClearIds = {},
            animeLocation = "",
            onAnimeLocationClicked = {},
            mangaLocation = "",
            onMangaLocationClicked = {},
        )
    }
}
