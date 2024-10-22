package xyz.secozzi.aniyomilocalmanager.presentation.compontents.cover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import xyz.secozzi.aniyomilocalmanager.data.cover.CoverData
import xyz.secozzi.aniyomilocalmanager.presentation.util.plus
import xyz.secozzi.aniyomilocalmanager.ui.theme.spacing

@Composable
fun CoverResultList(
    values: List<CoverData>,
    selectedCover: CoverData?,
    paddingValues: PaddingValues,
    gridSize: Int,
    onItemClick: (CoverData) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(gridSize),
        contentPadding = paddingValues + PaddingValues(
            vertical = MaterialTheme.spacing.medium,
            horizontal = MaterialTheme.spacing.small,
        ),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        items(
            items = values,
            key = { it.hashCode() }
        ) { cover ->
            CoverResultItem(
                coverData = cover,
                selected = selectedCover == cover,
                onClick = { onItemClick(cover) },
            )
        }
    }
}
