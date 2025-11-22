package xyz.secozzi.aniyomilocalmanager.presentation.components.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import xyz.secozzi.aniyomilocalmanager.R
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds

@Composable
fun SearchIcon(searchIds: SearchIds?) {
    val imageVector = when (searchIds) {
        SearchIds.AniDB -> ImageVector.vectorResource(R.drawable.anidb_icon)
        SearchIds.MangaBaka -> Icons.Default.Book
        SearchIds.AnilistAnime, SearchIds.AnilistManga -> ImageVector.vectorResource(R.drawable.anilist_icon)
        SearchIds.MalAnime, SearchIds.MalManga -> ImageVector.vectorResource(R.drawable.mal_icon)
        null -> Icons.Default.Folder
    }

    Icon(imageVector, null)
}
