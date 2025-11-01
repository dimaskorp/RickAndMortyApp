package com.example.rickandmortyapp.data.repository

import com.example.rickandmortyapp.data.CharactersFilters
import com.example.rickandmortyapp.domain.model.Character

object CharactersCache {
    // Общий кэш для всех страниц и отдельных персонажей
    val pagesCache = mutableMapOf<String, List<Character>>()
    val charactersCache = mutableMapOf<Int, Character>()

    // Очистка кэша страниц при изменении фильтров
    fun clearCacheForFilters(filters: CharactersFilters) {
        val keysToRemove = pagesCache.keys.filter { it.startsWith(filters.toQueryKey()) }
        keysToRemove.forEach { pagesCache.remove(it) }
    }

    fun clearAllCache() {
        pagesCache.clear()
        charactersCache.clear()
    }

    fun getCharacterFromCache(id: Int): Character? {
        return charactersCache[id]
    }

    fun saveCharacterToCache(character: Character) {
        charactersCache[character.id] = character
    }
}
