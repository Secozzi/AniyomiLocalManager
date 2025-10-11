package xyz.secozzi.aniyomilocalmanager.ui.home.manga

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anggrayudi.storage.file.fullName
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.storage.StorageManager
import xyz.secozzi.aniyomilocalmanager.presentation.home.manga.MangaScreenContent
import xyz.secozzi.aniyomilocalmanager.ui.preferences.PreferencesRoute
import xyz.secozzi.aniyomilocalmanager.ui.utils.LocalBackStack

@Composable
fun MangaScreen(
    bottomPadding: Dp,
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val backstack = LocalBackStack.current

    val storageManager = koinInject<StorageManager>()
    val viewModel = koinViewModel<MangaScreenViewModel>()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val locationPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        context.contentResolver.takePersistableUriPermission(uri, flags)

        val dir = storageManager.getFile(uri)!!

        if (dir.fullName.equals(MANGA_DIRECTORY_NAME, true)) {
            viewModel.setStorageLocation(uri.toString())
        } else {
            Toast.makeText(
                context,
                resources.getString(R.string.select_invalid_location),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    MangaScreenContent(
        state = state,
        isLoading = isLoading,
        bottomPadding = bottomPadding,
        onClickSelectLocation = { locationPicker.launch(null) },
        onClickSettings = { backstack.add(PreferencesRoute) },
        onClickItem = { item ->
            // TODO
        },
    )
}
