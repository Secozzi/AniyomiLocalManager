package xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.preview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.github.k1rakishou.fsaf.FileManager
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.koinInject
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.model.EpisodeType
import xyz.secozzi.aniyomilocalmanager.presentation.Screen
import xyz.secozzi.aniyomilocalmanager.presentation.search.SearchScreen
import xyz.secozzi.aniyomilocalmanager.presentation.util.getScreenResult
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.EPISODES_KEY
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.EPISODES_RESULT
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.EpisodeInfo
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.EpisodeScreenModel
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.TYPES_KEY
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.TYPES_RESULT
import xyz.secozzi.aniyomilocalmanager.ui.entry.anime.episode.components.PreviewEpisodeCard
import xyz.secozzi.aniyomilocalmanager.ui.preferences.AniDBPreferencesScreen
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing
import xyz.secozzi.aniyomilocalmanager.utils.Constants.disabledAlpha
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName

class PreviewEpisodeScreen(
    val path: String,
    val aniDBId: Long?,
) : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val availableTypes = navigator.getScreenResult<TYPES_RESULT>(TYPES_KEY)!!
        val episodeList = navigator.getScreenResult<EPISODES_RESULT>(EPISODES_KEY)!!

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.episode_list_preview_title))
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                        }
                    },
                )
            }
        ) { paddingValues ->
            val expandedState = remember(availableTypes) { availableTypes.map { false }.toMutableStateList() }

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
            ) {
                availableTypes.forEachIndexed { i, episodeType ->
                    val expanded = expandedState[i]
                    EpisodeTypeCard(
                        episodeType = episodeType,
                        episodes = episodeList[episodeType.id]!!.toImmutableList(),
                        expanded = expanded,
                        onExpand = { expandedState[i] = !expanded }
                    )
                }
            }
        }
    }

    @Composable
    fun EpisodeTypeCard(
        episodeType: EpisodeType,
        episodes: ImmutableList<EpisodeInfo>,
        expanded: Boolean,
        onExpand: () -> Unit,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(horizontal = MaterialTheme.spacing.small)
                .clip(RoundedCornerShape(16.dp))
                .background(color = episodeType.id.getColor())
                .padding(MaterialTheme.spacing.medium)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.combinedClickable(onClick = onExpand),
                ) {
                    Text(
                        text = episodeType.displayName,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    episodeType.extraData?.let {
                        Text(
                            text = "($it)",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.alpha(disabledAlpha),
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (expanded) {
                        Icon(Icons.Default.KeyboardArrowUp, null)
                    } else {
                        Icon(Icons.Default.KeyboardArrowDown, null)
                    }
                }


                if (expanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 600.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.smaller),
                        ) {
                            items(episodes) { ep ->
                                PreviewEpisodeCard(
                                    title = ep.name,
                                    episodeNumber = ep.episodeNumber,
                                    extraInfo = listOf(
                                        ep.date ?: "",
                                        ep.scanlator ?: "",
                                    ),
                                    modifier = Modifier
                                        .border(
                                            width = 2.dp,
                                            color = episodeType.id.getColor(),
                                            shape = RoundedCornerShape(16.dp),
                                        )
                                        .padding(MaterialTheme.spacing.small),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // "500" colors from https://m2.material.io/design/color/the-color-system.html#tools-for-picking-colors
    private fun Int.getColor(): Color {
        return when (this) {
            1 -> Color(0x402196F3)
            2 -> Color(0x404CAF50)
            3 -> Color(0x40FFEB3B)
            4 -> Color(0x40F44336)
            5 -> Color(0x40E91E63)
            else -> Color(0x40673AB7)
        }
    }
}
