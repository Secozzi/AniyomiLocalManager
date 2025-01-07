package xyz.secozzi.aniyomilocalmanager.ui.preferences

import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository
import xyz.secozzi.aniyomilocalmanager.preferences.GeneralPreferences
import xyz.secozzi.aniyomilocalmanager.presentation.Screen
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.ConfirmDialog

object DataScreen : Screen() {
    private fun readResolve(): Any = DataScreen

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val preferences = koinInject<GeneralPreferences>()
        val trackerIdRepository = koinInject<TrackerIdRepository>()

        val screenModel = rememberScreenModel { DataScreenModel(preferences, trackerIdRepository) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.pref_data_title)) },
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
                    var isConfirmDialogShown by remember { mutableStateOf(false) }

                    Preference(
                        title = { Text(text = stringResource(R.string.pref_data_tracker_ids)) },
                        onClick = { isConfirmDialogShown = true }
                    )
                    if (isConfirmDialogShown) {
                        ConfirmDialog(
                            title = stringResource(R.string.pref_data_tracker_dialog_title),
                            subtitle = stringResource(R.string.pref_data_tracker_dialog_summary),
                            onConfirm = {
                                screenModel.clearTrackerIds()
                                isConfirmDialogShown = false
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.pref_data_tracker_cleared),
                                    Toast.LENGTH_SHORT,
                                ).show()
                            },
                            onCancel = { isConfirmDialogShown = false },
                        )
                    }

                    Preference(
                        title = { Text(text = stringResource(R.string.pref_data_localanime)) },
                        onClick = {
                            screenModel.clearLocalAnime()
                            Toast.makeText(
                                context,
                                context.getString(R.string.pref_data_localanime_cleared),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    )

                    Preference(
                        title = { Text(text = stringResource(R.string.pref_data_localmanga)) },
                        onClick = {
                            screenModel.clearLocalManga()
                            Toast.makeText(
                                context,
                                context.getString(R.string.pref_data_localmanga_cleared),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    )
                }
            }
        }
    }
}