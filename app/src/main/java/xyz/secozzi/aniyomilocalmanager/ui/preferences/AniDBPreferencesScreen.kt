package xyz.secozzi.aniyomilocalmanager.ui.preferences


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.TextFieldPreference
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.preferences.AniDBPreferences
import xyz.secozzi.aniyomilocalmanager.presentation.Screen
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import xyz.secozzi.aniyomilocalmanager.utils.Constants

object AniDBPreferencesScreen : Screen() {
    private fun readResolve(): Any = AniDBPreferencesScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val preferences = koinInject<AniDBPreferences>()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.pref_anidb_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                        }
                    }
                )
            }
        ) { paddingValues ->
            ProvidePreferenceLocals {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    var nameFormat by remember {
                        mutableStateOf(preferences.nameFormat.get())
                    }
                    ReplacingTextFieldPreference(
                        value = nameFormat,
                        onValueChange = { nameFormat = it },
                        title = stringResource(R.string.pref_anidb_episode),
                        textToValue = {
                            preferences.nameFormat.set(it)
                            it
                        }
                    )

                    var scanlatorFormat by remember {
                        mutableStateOf(preferences.scanlatorFormat.get())
                    }
                    ReplacingTextFieldPreference(
                        value = scanlatorFormat,
                        onValueChange = { scanlatorFormat = it },
                        title = stringResource(R.string.pref_anidb_scanlator),
                        textToValue = {
                            preferences.scanlatorFormat.set(it)
                            it
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun <T> ReplacingTextFieldPreference(
    value: T,
    onValueChange: (T) -> Unit,
    title: String,
    textToValue: (String) -> T?,
) {
    TextFieldPreference(
        value = value,
        onValueChange = onValueChange,
        title = { Text(text = title) },
        summary = { Text(text = value.toString()) },
        textField = { value, onValueChange, onOk ->
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
            ) {
                val desc = buildString {
                    append(stringResource(R.string.pref_replacements))
                    appendLine("\n")
                    Constants.replaceValues.forEachIndexed { index, s ->
                        append(s)
                        append(" - ")
                        append(Constants.replaceDescription[index])
                        appendLine()
                    }
                }

                Text(text = desc)

                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    maxLines = 1,
                    keyboardActions = KeyboardActions(onDone = { onOk() }),
                )
            }
        },
        textToValue = textToValue,
    )
}
