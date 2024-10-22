package xyz.secozzi.aniyomilocalmanager.ui.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import me.zhanghai.compose.preference.PreferenceCategory
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.SliderPreference
import me.zhanghai.compose.preference.SwitchPreference
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.preferences.CoverPreferences
import xyz.secozzi.aniyomilocalmanager.preferences.preference.collectAsState
import xyz.secozzi.aniyomilocalmanager.presentation.Screen

object CoverPreferencesScreen : Screen() {
    private fun readResolve(): Any = CoverPreferencesScreen

    @Composable
    override fun Content() {
        val preferences = koinInject<CoverPreferences>()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.pref_cover_title)) },
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
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues),
                ) {
                    PreferenceCategory(
                        title = { Text(text = stringResource(id = R.string.pref_cover_anime)) },
                    )

                    val animeAnilist by preferences.animeCoverAnilist.collectAsState()
                    SwitchPreference(
                        value = animeAnilist,
                        onValueChange = { preferences.animeCoverAnilist.set(it) },
                        title = { Text(text = stringResource(R.string.pref_cover_anilist)) }
                    )

                    val animeMAL by preferences.animeCoverMAL.collectAsState()
                    SwitchPreference(
                        value = animeMAL,
                        onValueChange = { preferences.animeCoverMAL.set(it) },
                        title = { Text(text = stringResource(R.string.pref_cover_mal)) }
                    )

                    val animeFanart by preferences.animeCoverFanart.collectAsState()
                    SwitchPreference(
                        value = animeFanart,
                        onValueChange = { preferences.animeCoverFanart.set(it) },
                        title = { Text(text = stringResource(R.string.pref_cover_fanart)) }
                    )

                    PreferenceCategory(
                        title = { Text(text = stringResource(id = R.string.pref_cover_manga)) },
                    )

                    val mangaAnilist by preferences.mangaCoverAnilist.collectAsState()
                    SwitchPreference(
                        value = mangaAnilist,
                        onValueChange = { preferences.mangaCoverAnilist.set(it) },
                        title = { Text(text = stringResource(R.string.pref_cover_anilist)) }
                    )

                    val mangaMAL by preferences.mangaCoverMAL.collectAsState()
                    SwitchPreference(
                        value = mangaMAL,
                        onValueChange = { preferences.mangaCoverMAL.set(it) },
                        title = { Text(text = stringResource(R.string.pref_cover_mal)) }
                    )

                    val mangaMD by preferences.mangaCoverMD.collectAsState()
                    SwitchPreference(
                        value = mangaMD,
                        onValueChange = { preferences.mangaCoverMD.set(it) },
                        title = { Text(text = stringResource(R.string.pref_cover_md)) }
                    )

                    PreferenceCategory(
                        title = { Text(text = stringResource(id = R.string.pref_cover_display)) },
                    )

                    val gridSize by preferences.gridSize.collectAsState()
                    SliderPreference(
                        value = gridSize.toFloat(),
                        onValueChange = { preferences.gridSize.set(it.toInt()) },
                        title = { Text(text = stringResource(R.string.pref_cover_columns)) },
                        valueRange = 1f..10f,
                        valueSteps = 8,
                        summary = {
                            Text(text = stringResource(R.string.pref_cover_columns_per_row, gridSize))
                        },
                        onSliderValueChange = { preferences.gridSize.set(it.toInt()) },
                        sliderValue = gridSize.toFloat(),
                    )
                }
            }
        }
    }
}
