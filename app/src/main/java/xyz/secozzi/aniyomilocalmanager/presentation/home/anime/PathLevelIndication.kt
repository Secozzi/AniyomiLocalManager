package xyz.secozzi.aniyomilocalmanager.presentation.home.anime

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.NavigateNext
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import xyz.secozzi.aniyomilocalmanager.presentation.PreviewContent

// From https://github.com/SkyD666/PodAura
@Composable
fun PathLevelIndication(
    pathList: ImmutableList<String>,
    onNavigateTo: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(scrollState.maxValue) {
        if (scrollState.canScrollForward) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        pathList.forEachIndexed { index, path ->
            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .clickable { onNavigateTo(index) }
                    .padding(horizontal = 6.dp),
                text = path,
            )

            if (index != pathList.lastIndex) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.NavigateNext,
                    contentDescription = null,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PathLevelIndicationPreview() {
    PreviewContent {
        PathLevelIndication(
            pathList = persistentListOf("/Aniyomi/localanime", "One Piece", "Season 1"),
            onNavigateTo = {},
        )
    }
}
