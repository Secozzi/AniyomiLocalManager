package xyz.secozzi.aniyomilocalmanager.presentation.search

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.aniyomilocalmanager.data.search.SearchDataItem
import xyz.secozzi.aniyomilocalmanager.data.search.SearchRepository
import xyz.secozzi.aniyomilocalmanager.presentation.util.RequestState

class SearchSreeenModel(private val searchRepo: SearchRepository) :
    ScreenModel {
    var selectedItem = MutableStateFlow<SearchDataItem?>(null)

    private val _searchItems = MutableStateFlow<RequestState<List<SearchDataItem>>>(RequestState.Idle)
    val searchItems = _searchItems.asStateFlow()

    fun updateSelected(selected: SearchDataItem) {
        selectedItem.update { _ -> selected }
    }

    fun search(searchQuery: String) {
        _searchItems.update { _ -> RequestState.Loading }
        screenModelScope.launch(Dispatchers.IO) {
            _searchItems.update { _ ->
                try {
                    RequestState.Success(searchRepo.search(searchQuery))
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        }
    }
}
