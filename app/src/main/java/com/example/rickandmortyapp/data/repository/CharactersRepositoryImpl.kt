package com.example.rickandmortyapp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.rickandmortyapp.data.CharactersFilters
import com.example.rickandmortyapp.data.api.RickAndMortyApi
import com.example.rickandmortyapp.data.mapper.toDomain
import com.example.rickandmortyapp.domain.model.Character
import com.example.rickandmortyapp.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CharactersRepositoryImpl(
    private val api: RickAndMortyApi
) : CharactersRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getCharacters(filters: CharactersFilters): Flow<PagingData<Character>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 4,
                initialLoadSize = 20
            )
        ) {
            SimpleCharactersPagingSource(api, filters)
        }.flow
    }

    override fun getCharactersFromCache(filters: CharactersFilters): List<Character> {
        // Просто берем всех персонажей из кэша и фильтруем
        val allCachedCharacters = CharactersCache.pagesCache.values.flatten()

        return allCachedCharacters.filter { character ->
            // Фильтрация по имени
            filters.name?.let { name ->
                name.isNotBlank() && character.name.contains(name, ignoreCase = true)
            } ?: true &&
                    // Фильтрация по статусу
                    filters.status?.let { status ->
                        status.isNotBlank() && character.status.equals(status, ignoreCase = true)
                    } ?: true &&
                    // Фильтрация по виду
                    filters.species?.let { species ->
                        species.isNotBlank() && character.species.contains(species, ignoreCase = true)
                    } ?: true &&
                    // Фильтрация по полу
                    filters.gender?.let { gender ->
                        gender.isNotBlank() && character.gender.equals(gender, ignoreCase = true)
                    } ?: true
        }
    }

    override fun hasCachedDataForFilters(filters: CharactersFilters): Boolean {
        // Проверяем, есть ли хоть какие-то данные в кэше
        return CharactersCache.pagesCache.isNotEmpty()
    }

    override fun observeCharacter(id: Int): Flow<Character?> {
        return flow {
            // Используем кэш из CharactersCache
            val cachedCharacter = CharactersCache.getCharacterFromCache(id)
            if (cachedCharacter != null) {
                emit(cachedCharacter)
            } else {
                // Если нет в кэше, пытаемся загрузить
                try {
                    val character = refreshCharacterInternal(id)
                    emit(character)
                } catch (e: Exception) {
                    // Если загрузка не удалась, эмитим null
                    emit(null)
                }
            }
        }
    }

    override suspend fun refreshCharacter(id: Int) {
        refreshCharacterInternal(id)
    }

    override fun clearCacheForFilters(filters: CharactersFilters) {
        CharactersCache.clearCacheForFilters(filters)
    }

    override fun clearAllCache() {
        CharactersCache.clearAllCache()
    }

    private suspend fun refreshCharacterInternal(id: Int): Character {
        try {
            val characterDto = api.getCharacter(id)
            val character = characterDto.toDomain()
            // Сохраняем в общий кэш
            CharactersCache.saveCharacterToCache(character)
            return character
        } catch (e: Exception) {
            throw e
        }
    }
}