package xyz.secozzi.aniyomilocalmanager.ui.search

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.aniyomilocalmanager.data.search.SearchManager
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.utils.StateViewModel

class SearchScreenViewModel(
    private val searchRepositoryId: Long,
    private val searchManager: SearchManager,
) : StateViewModel<SearchScreenViewModel.State>(State.Idle) {

    private val _selected = MutableStateFlow<SearchResultItem?>(null)
    val selected = _selected.asStateFlow()

    fun updateSelected(selected: SearchResultItem) {
        _selected.update { s ->
            if (s == selected) null else selected
        }
    }

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableState.update { _ -> State.Loading }

            try {
                val result = searchManager.getSearchRepository(searchRepositoryId).search(query)
                mutableState.update { _ -> State.Success(result) }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                mutableState.update { _ -> State.Error(e) }
            }
        }
    }

    @Immutable
    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data object Loading : State

        @Immutable
        data class Error(val exception: Throwable) : State

        @Immutable
        data class Success(
            val items: List<SearchResultItem>,
        ) : State
    }
}
