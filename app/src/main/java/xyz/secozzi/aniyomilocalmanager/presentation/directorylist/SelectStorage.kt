package xyz.secozzi.aniyomilocalmanager.presentation.directorylist

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.k1rakishou.fsaf.FileManager
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R

@Composable
fun SelectStorage(
    label: String,
    validateName: (String) -> Boolean,
    onInvalid: () -> Unit,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val fileManager = koinInject<FileManager>()

    val locationPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        val flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        context.contentResolver.takePersistableUriPermission(uri, flags)

        val dir = fileManager.fromUri(uri)!!

        val isValid = validateName(fileManager.getName(dir))
        if (isValid) {
            onSelected(dir.getFullPath())
        } else {
            onInvalid()
        }
    }

    Surface {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.headlineSmall,
            )
            Button(
                onClick = { locationPicker.launch(null) }
            ) {
                Text(text = stringResource(R.string.select_action))
            }
        }
    }
}
