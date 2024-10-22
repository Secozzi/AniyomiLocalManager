package xyz.secozzi.aniyomilocalmanager.ui.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SliderPreference
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.preferences.AniListPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.TitleLangs
import xyz.secozzi.aniyomilocalmanager.preferences.preference.collectAsState
import xyz.secozzi.aniyomilocalmanager.presentation.Screen

object AniListPreferencesScreen : Screen() {
    private fun readResolve(): Any = AniListPreferencesScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val preferences = koinInject<AniListPreferences>()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.pref_anilist_title))
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    },
                )
            },
        ) { paddingValues ->
            ProvidePreferenceLocals {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val titlePref by preferences.titleLang.collectAsState()
                    ListPreference(
                        value = titlePref,
                        onValueChange = { preferences.titleLang.set(it) },
                        values = TitleLangs.entries,
                        valueToText = { AnnotatedString((it.name)) },
                        title = { Text(text = stringResource(R.string.pref_anilist_pref_title)) },
                        summary = { Text(text = titlePref.name) }
                    )

                    val studioCount by preferences.studioCount.collectAsState()
                    SliderPreference(
                        value = studioCount.toFloat(),
                        onValueChange = { preferences.studioCount.set(it.toInt()) },
                        title = { Text(text = stringResource(R.string.pref_anilist_studio)) },
                        valueRange = 1f..10f,
                        valueSteps = 8,
                        summary = { Text(studioCount.toString()) },
                        onSliderValueChange = { preferences.studioCount.set(it.toInt()) },
                        sliderValue = studioCount.toFloat(),
                    )
                }
            }
        }
    }
}
