package xyz.secozzi.aniyomilocalmanager.domain.search.repository

import xyz.secozzi.aniyomilocalmanager.domain.search.models.SearchResultItem

interface SearchRepository {
    suspend fun search(query: String): List<SearchResultItem>
}
