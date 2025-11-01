package com.example.rickandmortyapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rickandmortyapp.data.CharactersFilters
import com.example.rickandmortyapp.data.mapper.toDomain
import com.example.rickandmortyapp.data.api.RickAndMortyApi
import com.example.rickandmortyapp.data.repository.CharactersCache.charactersCache
import com.example.rickandmortyapp.data.repository.CharactersCache.pagesCache
import com.example.rickandmortyapp.domain.model.Character

class SimpleCharactersPagingSource(
    private val api: RickAndMortyApi,
    private val filters: CharactersFilters
) : PagingSource<Int, Character>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: 1
        val cacheKey = "${filters.toQueryKey()}_page_$page"

        return try {
            // Сначала проверяем кэш страниц
            val cachedData = pagesCache[cacheKey]
            if (cachedData != null) {
                // Сохраняем персонажей в общий кэш
                cachedData.forEach { character ->
                    charactersCache[character.id] = character
                }
                return LoadResult.Page(
                    data = cachedData,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = page + 1
                )
            }

            // Если нет в кэше, загружаем из сети
            val response = api.getCharacters(
                page = page,
                name = filters.name,
                status = filters.status,
                species = filters.species,
                gender = filters.gender
            )

            val characters = response.results.map { it.toDomain() }

            // Сохраняем в кэш страниц
            pagesCache[cacheKey] = characters

            // Сохраняем персонажей в общий кэш
            characters.forEach { character ->
                charactersCache[character.id] = character
            }

            LoadResult.Page(
                data = characters,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.info.next == null) null else page + 1
            )
        } catch (e: Exception) {
            // При ошибке сети проверяем кэш еще раз
            val cachedData = pagesCache[cacheKey]
            if (cachedData != null) {
                LoadResult.Page(
                    data = cachedData,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = null
                )
            } else {
                LoadResult.Error(e)
            }
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}