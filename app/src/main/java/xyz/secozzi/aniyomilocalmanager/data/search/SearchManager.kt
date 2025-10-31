package xyz.secozzi.aniyomilocalmanager.data.search

import xyz.secozzi.aniyomilocalmanager.data.search.mangabaka.MangaBakaSearch
import xyz.secozzi.aniyomilocalmanager.domain.search.repository.SearchRepository
import xyz.secozzi.aniyomilocalmanager.domain.search.service.TrackerIds

class SearchManager(
    private val mangaBakaSearch: MangaBakaSearch,
) {
    fun getSearchRepository(id: Long): SearchRepository {
        return when (id) {
            TrackerIds.MangaBaka -> mangaBakaSearch
            else -> throw Exception("Invalid search repository")
        }
    }
}
