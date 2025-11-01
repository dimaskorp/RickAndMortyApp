package com.example.rickandmortyapp.domain.repository

import androidx.paging.PagingData
import com.example.rickandmortyapp.data.CharactersFilters
import com.example.rickandmortyapp.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {
    fun getCharacters(filters: CharactersFilters): Flow<PagingData<Character>>
    fun getCharactersFromCache(filters: CharactersFilters): List<Character>
    fun hasCachedDataForFilters(filters: CharactersFilters): Boolean
    fun observeCharacter(id: Int): Flow<Character?>
    fun clearCacheForFilters(filters: CharactersFilters)
    fun clearAllCache()
    suspend fun refreshCharacter(id: Int)
}


