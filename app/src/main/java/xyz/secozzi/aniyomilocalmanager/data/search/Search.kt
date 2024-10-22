package xyz.secozzi.aniyomilocalmanager.data.search

import xyz.secozzi.aniyomilocalmanager.presentation.util.NavigatorResult

interface SearchDataItem : NavigatorResult {
    fun toSearchItem(): SearchResultItem
}

interface SearchRepository {
    val id: Long

    suspend fun search(query: String): List<SearchDataItem>
}
