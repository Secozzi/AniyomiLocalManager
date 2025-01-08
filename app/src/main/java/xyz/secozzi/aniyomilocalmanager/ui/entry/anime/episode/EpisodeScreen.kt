package xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.k1rakishou.fsaf.FileManager
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.data.anidb.episode.EpisodeRepository
import xyz.secozzi.aniyomilocalmanager.data.anidb.episode.dto.EpisodeModel
import xyz.secozzi.aniyomilocalmanager.data.anidb.search.dto.ADBAnime
import xyz.secozzi.aniyomilocalmanager.data.search.SearchRepositoryManager
import xyz.secozzi.aniyomilocalmanager.database.ALMDatabase
import xyz.secozzi.aniyomilocalmanager.domain.model.EpisodeType
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository
import xyz.secozzi.aniyomilocalmanager.preferences.AniDBPreferences
import xyz.secozzi.aniyomilocalmanager.presentation.Screen
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.NotSearchedContent
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.ProgressContent
import xyz.secozzi.aniyomilocalmanager.presentation.compontents.SimpleDropdown
import xyz.secozzi.aniyomilocalmanager.presentation.search.SearchScreen
import xyz.secozzi.aniyomilocalmanager.presentation.util.RequestState
import xyz.secozzi.aniyomilocalmanager.presentation.util.clearResults
import xyz.secozzi.aniyomilocalmanager.presentation.util.getResult
import xyz.secozzi.aniyomilocalmanager.presentation.util.setScreenResult
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.components.OutlinedNumericChooser
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.components.PreviewEpisodeCard
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.preview.PreviewEpisodeScreen
import xyz.secozzi.aniyomilocalmanager.ui.preferences.AniDBPreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName

class EpisodeScreen(val path: String, val aniDBId: Long?) : Screen() {
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val clipboardManager = LocalClipboardManager.current

        val episodeRepo = koinInject<EpisodeRepository>()
        val fileManager = koinInject<FileManager>()
        val trackerIdRepository = koinInject<TrackerIdRepository>()
        val preferences = koinInject<AniDBPreferences>()

        val screenModel = rememberScreenModel {
            EpisodeScreenModel(path, aniDBId, episodeRepo, fileManager, trackerIdRepository, preferences)
        }

        val state by screenModel.state.collectAsState()
        val availableTypes by screenModel.availableTypes.collectAsState()
        val offset by screenModel.offset.collectAsState()
        val start by screenModel.start.collectAsState()
        val end by screenModel.end.collectAsState()
        val isValid by screenModel.isValid.collectAsState()
        val selectedType by screenModel.selectedType.collectAsState()
        val startPreview by screenModel.startPreview.collectAsState()
        val endPreview by screenModel.endPreview.collectAsState()

        val result = getResult().value as? ADBAnime
        if (result != null) {
            screenModel.updateAniDB(result.remoteId)
            screenModel.getEpisodes(result.remoteId)
            navigator.clearResults()
        }

        LaunchedEffect(Unit) {
            if (state.isSuccess()) {
                screenModel.updateStartPreview()
                screenModel.updateEndPreview()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.episode_title))
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navigator.push(AniDBPreferencesScreen) }) {
                            Icon(Icons.Default.Settings, null)
                        }

                        IconButton(
                            onClick = {
                                navigator.setScreenResult(TYPES_KEY, availableTypes)
                                navigator.setScreenResult(EPISODES_KEY, state.getSuccessData().mapValues { (_, values) ->
                                    values.map { screenModel.toEpisodeInfo(it) }
                                })
                                navigator.push(PreviewEpisodeScreen(path, aniDBId))
                            },
                            enabled = state.isSuccess(),
                        ) {
                            Icon(Icons.Outlined.RemoveRedEye, null)
                        }
                        IconButton(
                            onClick = {
                                navigator.push(
                                    SearchScreen(
                                        searchQuery = path.getDirectoryName(),
                                        searchRepositoryId = SearchRepositoryManager.ANIDB,
                                    )
                                )
                            }
                        ) {
                            Icon(Icons.Default.Search, null)
                        }
                    }
                )
            },
            bottomBar = {
                if (state is RequestState.Success) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
                        modifier = Modifier.windowInsetsPadding(NavigationBarDefaults.windowInsets)
                            .padding(
                                start = MaterialTheme.spacing.medium,
                                end = MaterialTheme.spacing.medium,
                                bottom = MaterialTheme.spacing.smaller,
                            )
                    ) {
                        Button(
                            onClick = {
                                val generateResult = screenModel.generateEpisodesJson()

                                val message = if (generateResult) {
                                    context.resources.getString(R.string.episode_generate_details_success)
                                } else {
                                    context.resources.getString(R.string.episode_generate_details_failure)
                                }

                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f),
                            enabled = isValid,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(Icons.Default.FileDownload, null)
                                Text(text = stringResource(R.string.episode_generate_json))
                            }
                        }

                        FilledIconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(screenModel.generateJsonString()))
                            },
                            enabled = isValid,
                        ) {
                            Icon(Icons.Default.ContentCopy, null)
                        }
                    }
                }
            }
        ) { paddingValues ->
            val paddingModifier = Modifier.padding(paddingValues)

            state.DisplayResult(
                onIdle = {
                    if (aniDBId == null) {
                        NotSearchedContent(
                            title = stringResource(R.string.episode_not_searched),
                            modifier = paddingModifier,
                            onSearch = {
                                navigator.push(
                                    SearchScreen(
                                        searchQuery = path.getDirectoryName(),
                                        searchRepositoryId = SearchRepositoryManager.ANIDB,
                                    )
                                )
                            }
                        )
                    } else {
                        ProgressContent(modifier = paddingModifier)
                    }
                },
                onLoading = { ProgressContent(modifier = paddingModifier) },
                onError = { ErrorContent(it, modifier = paddingModifier) },
                onSuccess = { values ->
                    Column(
                        modifier = paddingModifier
                            .verticalScroll(rememberScrollState())
                            .padding(
                                start = MaterialTheme.spacing.medium,
                                end = MaterialTheme.spacing.medium,
                            ),
                    ) {
                        SimpleDropdown(
                            label = "Type",
                            selectedItem = selectedType,
                            items = availableTypes,
                            modifier = Modifier.fillMaxWidth(),
                            onSelected = screenModel::onSelectedType,
                        )

                        OutlinedNumericChooser(
                            value = start,
                            onChange = screenModel::updateStart,
                            max = selectedType.extraData!!,
                            step = 1,
                            min = 1,
                            label = { Text(text = stringResource(R.string.episode_start_label)) },
                            isStart = true,
                            isCrossing = start > end,
                        )

                        OutlinedNumericChooser(
                            value = end,
                            onChange = screenModel::updateEnd,
                            max = selectedType.extraData!!,
                            step = 1,
                            min = 1,
                            label = { Text(text = stringResource(R.string.episode_end_label)) },
                            isStart = false,
                            isCrossing = start > end
                        )

                        OutlinedNumericChooser(
                            value = offset,
                            onChange = screenModel::updateOffset,
                            max = Int.MAX_VALUE,
                            step = 1,
                            min = Int.MIN_VALUE,
                            label = { Text(text = stringResource(R.string.episode_offset_label)) },
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                        ) {
                            HorizontalDivider()

                            Row {
                                Icon(
                                    Icons.Outlined.Numbers, null,
                                    modifier = Modifier.padding(start = 14.dp)
                                )
                                Spacer(modifier = Modifier.width(MaterialTheme.spacing.smaller))
                                Text(text = "Video count")
                                Spacer(modifier = Modifier.weight(1.0f))
                                Text(
                                    text = screenModel.getVideoCount().toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(end = 14.dp)
                                )
                            }

                            val previewModifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .padding(MaterialTheme.spacing.medium)

                            if (startPreview != null) {
                                PreviewEpisodeCard(
                                    title = startPreview!!.name,
                                    episodeNumber = startPreview!!.episodeNumber,
                                    originalEpisodeNumber = startPreview!!.episodeNumber - offset,
                                    extraInfo = listOf(
                                        startPreview!!.date ?: "",
                                        startPreview!!.scanlator ?: "",
                                    ),
                                    modifier = previewModifier,
                                )
                            }

                            if (endPreview != null) {
                                PreviewEpisodeCard(
                                    title = endPreview!!.name,
                                    episodeNumber = endPreview!!.episodeNumber,
                                    originalEpisodeNumber = endPreview!!.episodeNumber - offset,
                                    extraInfo = listOf(
                                        endPreview!!.date ?: "",
                                        endPreview!!.scanlator ?: "",
                                    ),
                                    modifier = previewModifier,
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

const val TYPES_KEY = "available_types"
typealias TYPES_RESULT = List<EpisodeType>
const val EPISODES_KEY = "episode_list"
typealias EPISODES_RESULT = Map<Int, List<EpisodeInfo>>
