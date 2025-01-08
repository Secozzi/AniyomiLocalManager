package xyz.secozzi.aniyomilocalmanager.ui.entry.anime.cover

import android.net.Uri
import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.k1rakishou.fsaf.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import xyz.secozzi.aniyomilocalmanager.data.cover.CoverData
import xyz.secozzi.aniyomilocalmanager.data.cover.CoverRepository
import xyz.secozzi.aniyomilocalmanager.database.ALMDatabase
import xyz.secozzi.aniyomilocalmanager.database.entities.TrackerIdEntity
import xyz.secozzi.aniyomilocalmanager.domain.trackerid.TrackerIdRepository
import xyz.secozzi.aniyomilocalmanager.presentation.util.RequestState
import xyz.secozzi.aniyomilocalmanager.ui.home.tabs.ANIME_DIRECTORY_NAME
import xyz.secozzi.aniyomilocalmanager.utils.GET
import xyz.secozzi.aniyomilocalmanager.utils.getDirectoryName

class AnimeCoverScreenModel(
    private val path: String,
    private val anilistId: Long?,
    private val coverRepo: CoverRepository,
    private val fileManager: FileManager,
    private val client: OkHttpClient,
    private val trackerIdRepository: TrackerIdRepository,
) : StateScreenModel<RequestState<List<CoverData>>>(RequestState.Idle) {
    var selectedCover = MutableStateFlow<CoverData?>(null)

    fun updateSelected(selected: CoverData) {
        selectedCover.update { _ -> selected }
    }

    init {
        if (anilistId != null) {
            getCovers(anilistId)
        }
    }

    fun updateAniList(anilistId: Long) {
        screenModelScope.launch(Dispatchers.IO) {
            trackerIdRepository.updateAniListId(
                path = "$ANIME_DIRECTORY_NAME/${path.getDirectoryName()}",
                anilistId = anilistId,
            )
        }
    }

    fun getCovers(anilistId: Long) {
        mutableState.update { _ -> RequestState.Loading }
        screenModelScope.launch(Dispatchers.IO) {
            mutableState.update { _ ->
                try {
                    RequestState.Success(coverRepo.getAnimeCovers(anilistId))
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        }
    }

    fun downloadCover(): Boolean {
        val fileExt = selectedCover.value?.coverUrl?.split(".")?.last() ?: "jpg"

        val dir = fileManager.fromUri(Uri.parse(path)) ?: return false
        val cover = fileManager.createFile(dir, "cover.$fileExt") ?: return false

        screenModelScope.launch(Dispatchers.IO) {
            fileManager.getOutputStream(cover).use {
                val resp = client.newCall(
                    GET(selectedCover.value!!.coverUrl)
                ).execute()

                it?.write(resp.body.bytes())
            }
        }

        return true
    }
}