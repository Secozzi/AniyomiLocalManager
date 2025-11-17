package xyz.secozzi.aniyomilocalmanager.presentation.anime.episodes.fetch

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.launch
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeDetails
import xyz.secozzi.aniyomilocalmanager.domain.entry.anime.model.EpisodeType
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.ErrorContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.GenerateBottomBar
import xyz.secozzi.aniyomilocalmanager.presentation.components.InfoContent
import xyz.secozzi.aniyomilocalmanager.presentation.components.TopLoadingIndicator
import xyz.secozzi.aniyomilocalmanager.presentation.components.details.SearchIcon
import xyz.secozzi.aniyomilocalmanager.ui.anime.episode.fetch.AnimeFetchEpisodesScreenViewModel

@Composable
fun AnimeFetchEpisodesScreenContent(
    state: AnimeFetchEpisodesScreenViewModel.State,
    selectedSearch: SearchIds?,
    name: String,
    isLoading: Boolean,
    episodesMap: ImmutableMap<EpisodeType, ImmutableList<EpisodeDetails>>,
    onBack: () -> Unit,
    onClickSearch: () -> Unit,
    onClickSearchId: (SearchIds) -> Unit,
    onGenerate: () -> Unit,
    onCopy: () -> Unit,

    videoCount: Int?,
    selectedType: EpisodeType?,
    onSelectedTypeChange: (EpisodeType) -> Unit,
    start: Int,
    onStartChange: (Int) -> Unit,
    end: Int,
    onEndChange: (Int) -> Unit,
    offset: Int,
    onOffsetChange: (Int) -> Unit,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val successState = state as? AnimeFetchEpisodesScreenViewModel.State.Success
    var isMenuExpanded by remember { mutableStateOf(false) }

    val dropdownItems = remember(successState?.searchIds) {
        successState?.searchIds?.keys?.map {
            it to it.stringRes
        } ?: emptyList()
    }

    val isValid = remember(selectedType, episodesMap, start, end) {
        val current = episodesMap.entries.firstOrNull { (type, _) ->
            type == selectedType
        }

        current != null && start <= end && start >= 1 && end <= current.value.size
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                    }
                },
                actions = {
                    if (selectedSearch != null) {
                        SearchIcon(selectedSearch)

                        IconButton(onClick = { isMenuExpanded = !isMenuExpanded }) {
                            Icon(if (isMenuExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown, null)
                        }

                        DropdownMenu(
                            expanded = isMenuExpanded,
                            onDismissRequest = { isMenuExpanded = false },
                        ) {
                            dropdownItems.forEach { (id, stringRes) ->
                                DropdownMenuItem(
                                    text = { Text(text = stringResource(stringRes)) },
                                    onClick = {
                                        if (id != selectedSearch) {
                                            onClickSearchId(id)
                                        }

                                        isMenuExpanded = false
                                    },
                                    leadingIcon = { SearchIcon(id) },
                                )
                            }
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (state is AnimeFetchEpisodesScreenViewModel.State.Success && selectedTab == 0) {
                GenerateBottomBar(
                    label = stringResource(R.string.episode_generate_details),
                    enabled = isValid,
                    onGenerate = onGenerate,
                    onCopy = onCopy,
                )
            }
        },
    ) { contentPadding ->
        TopLoadingIndicator(
            isLoading = isLoading,
            contentPadding = contentPadding,
        )

        when (state) {
            AnimeFetchEpisodesScreenViewModel.State.Idle -> { }
            is AnimeFetchEpisodesScreenViewModel.State.Error -> {
                ErrorContent(
                    throwable = state.exception,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            AnimeFetchEpisodesScreenViewModel.State.NoID -> {
                InfoContent(
                    onClick = onClickSearch,
                    icon = Icons.Outlined.Search,
                    subtitle = stringResource(R.string.entry_no_id_set),
                    buttonText = stringResource(R.string.generic_search),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                )
            }
            is AnimeFetchEpisodesScreenViewModel.State.Success -> {
                SuccessContent(
                    episodesMap = episodesMap,
                    selectedTab = selectedTab,
                    onSelectedTabChange = { selectedTab = it },
                    selectedType = selectedType,
                    videoCount = videoCount,
                    onSelectedTypeChange = onSelectedTypeChange,
                    start = start,
                    onStartChange = onStartChange,
                    end = end,
                    onEndChange = onEndChange,
                    offset = offset,
                    onOffsetChange = onOffsetChange,
                    contentPadding = contentPadding,
                )
            }
        }
    }
}

@Composable
private fun SuccessContent(
    episodesMap: ImmutableMap<EpisodeType, ImmutableList<EpisodeDetails>>,
    selectedTab: Int,
    onSelectedTabChange: (Int) -> Unit,

    videoCount: Int?,
    selectedType: EpisodeType?,
    onSelectedTypeChange: (EpisodeType) -> Unit,
    start: Int,
    onStartChange: (Int) -> Unit,
    end: Int,
    onEndChange: (Int) -> Unit,
    offset: Int,
    onOffsetChange: (Int) -> Unit,

    contentPadding: PaddingValues,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 2 }

    LaunchedEffect(pagerState.currentPage) {
        onSelectedTabChange(pagerState.currentPage)
    }

    Column(
        modifier = Modifier.padding(
            top = contentPadding.calculateTopPadding(),
            start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
            end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
        ),
    ) {
        PrimaryTabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { scope.launch { pagerState.animateScrollToPage(0) } },
                text = { Text("Fetch") },
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { scope.launch { pagerState.animateScrollToPage(1) } },
                text = { Text("Preview") },
            )
        }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
        ) { page ->
            when (page) {
                0 -> {
                    EpisodeFetchDetails(
                        episodesMap = episodesMap,
                        videoCount = videoCount,
                        selectedType = selectedType,
                        onSelectedTypeChange = onSelectedTypeChange,
                        start = start,
                        onStartChange = onStartChange,
                        end = end,
                        onEndChange = onEndChange,
                        offset = offset,
                        onOffsetChange = onOffsetChange,
                        bottomPadding = contentPadding.calculateBottomPadding(),
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                1 -> {
                    EpisodeListPreview(
                        episodesMap = episodesMap,
                        modifier = Modifier.fillMaxSize().padding(bottom = contentPadding.calculateBottomPadding()),
                    )
                }
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun AnimeFetchEpisodesScreenContentPreview() {
    PreviewContent {
        val episodes = persistentListOf(
            EpisodeDetails(
                episodeNumber = 1,
                name = "Ep. 1 - What? Moon over the Ruined Castle?",
                dateUpload = "2024-01-08",
                fillermark = false,
                scanlator = null,
                summary = """Mikoto and Shiki are making their way to Rotsgard Academy in hopes of opening a new store. Meanwhile, back in the demiplane, everyone is helping Mio practice her cooking skills by taste testing her dishes.

Source: crunchyroll""",
                previewUrl = null,
            ),
            EpisodeDetails(
                episodeNumber = 2,
                name = "Ep. 2 - The Heroes Are a Couple of Beauties",
                dateUpload = "2024-01-15",
                fillermark = false,
                scanlator = null,
                summary = """We meet one of Misumi Makoto's classmates back on earth, who also has a curious encounter with a certain goddess who presides over a certain isekai. She and one other are sent there as heroes to help the hyumans defeat the demons.

Source: Crunchyroll""",
                previewUrl = null,
            ),
        )
        val credit = persistentListOf(
            EpisodeDetails(
                episodeNumber = 1,
                name = "Utopia (1-8, 10-12)",
                dateUpload = "2024-01-08",
                fillermark = false,
                scanlator = null,
                summary = null,
                previewUrl = null,
            ),
            EpisodeDetails(
                episodeNumber = 1,
                name = "Reversal (13-25)",
                dateUpload = "2024-04-01",
                fillermark = false,
                scanlator = null,
                summary = null,
                previewUrl = null,
            ),
        )
        val data = persistentMapOf(
            EpisodeType.Regular to episodes,
            EpisodeType.Credit to credit,
        )

        val successState = AnimeFetchEpisodesScreenViewModel.State.Success(
            persistentMapOf(),
        )

        AnimeFetchEpisodesScreenContent(
            state = successState,
            selectedSearch = SearchIds.AniDB,
            name = "Boku no Hero Academia",
            isLoading = false,
            episodesMap = data,
            onBack = { },
            onClickSearch = { },
            onClickSearchId = { },
            onGenerate = { },
            onCopy = { },
            videoCount = null,
            selectedType = EpisodeType.Regular,
            onSelectedTypeChange = { },
            start = 1,
            onStartChange = { },
            end = 2,
            onEndChange = { },
            offset = 0,
            onOffsetChange = { },
        )
    }
}
