package xyz.secozzi.aniyomilocalmanager.ui.search

import androidx.compose.runtime.Immutable
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.aniyomilocalmanager.data.search.SearchManager
import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem
import xyz.secozzi.aniyomilocalmanager.domain.search.service.SearchIds
import xyz.secozzi.aniyomilocalmanager.utils.StateViewModel

class SearchScreenViewModel(
    private val searchRepositoryId: SearchIds,
    private val searchManager: SearchManager,
) : StateViewModel<SearchScreenViewModel.State>(State.Idle) {

    private val _selected = MutableStateFlow<SearchResultItem?>(null)
    val selected = _selected.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun updateSelected(selected: SearchResultItem) {
        _selected.update { s ->
            if (s == selected) null else selected
        }
    }

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (query.startsWith("id:")) {
                query.drop(3).toLongOrNull()?.let {
                    _uiEvent.emit(UiEvent.SearchId(it))
                    return@launch
                }
            }

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
    sealed interface UiEvent {
        @Immutable
        data class SearchId(val id: Long) : UiEvent
    }

    @Immutable
    sealed interface State {
        @Immutable
        data object Idle : State

        @Immutable
        data object Loading : State

        @Immutable
        data class Error(val throwable: Throwable) : State

        @Immutable
        data class Success(
            val items: List<SearchResultItem>,
        ) : State
    }
}
