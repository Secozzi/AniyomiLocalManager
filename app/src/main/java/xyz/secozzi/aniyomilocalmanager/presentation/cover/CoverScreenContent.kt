package xyz.secozzi.aniyomilocalmanager.presentation.cover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import kotlinx.collections.immutable.ImmutableList
import xyz.secozzi.aniyomilocalmanager.domain.cover.model.CoverData
import xyz.secozzi.aniyomilocalmanager.presentation.utils.plus
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun CoverScreenContent(
    covers: ImmutableList<CoverData>,
    selectedCover: CoverData?,
    gridSize: Int,
    onClickCover: (CoverData) -> Unit,
    paddingValues: PaddingValues = PaddingValues(),
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridSize),
        contentPadding = paddingValues + PaddingValues(
            start = MaterialTheme.spacing.small,
            end = MaterialTheme.spacing.small,
            bottom = MaterialTheme.spacing.smaller,
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
    ) {
        items(
            items = covers,
            key = { it.coverUrl },
        ) { cover ->
            CoverListItem(
                coverData = cover,
                selected = cover == selectedCover,
                onClick = { onClickCover(cover) },
            )
        }
    }
}
