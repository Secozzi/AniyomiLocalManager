package xyz.secozzi.aniyomilocalmanager.ui.entry.anime.cover

import android.net.Uri
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.k1rakishou.fsaf.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import xyz.secozzi.aniyomilocalmanager.data.anilist.dto.ALAnime
import xyz.secozzi.aniyomilocalmanager.data.cover.CoverData
import xyz.secozzi.aniyomilocalmanager.data.cover.CoverRepository
import xyz.secozzi.aniyomilocalmanager.presentation.util.RequestState
import xyz.secozzi.aniyomilocalmanager.utils.GET

class AnimeCoverScreenModel(
    private val path: String,
    private val coverRepo: CoverRepository,
    private val fileManager: FileManager,
    private val client: OkHttpClient,
) : ScreenModel {
    var anilistId = MutableStateFlow(0L)
    var anilistCoverUrl = MutableStateFlow("")

    var selectedCover = MutableStateFlow<CoverData?>(null)

    private val _covers = MutableStateFlow<RequestState<List<CoverData>>>(RequestState.Idle)
    val covers = _covers.asStateFlow()

    fun updateSelected(selected: CoverData) {
        selectedCover.update { _ -> selected }
    }

    fun onSearched(item: ALAnime) {
        anilistId.update { _ -> item.remoteId }
        anilistCoverUrl.update { _ -> item.imageUrl }
        getCovers(item.remoteId)
    }

    fun getCovers(anilistId: Long) {
        _covers.update { _ -> RequestState.Loading }
        screenModelScope.launch(Dispatchers.IO) {
            _covers.update { _ ->
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