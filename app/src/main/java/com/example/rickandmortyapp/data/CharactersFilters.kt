package com.example.rickandmortyapp.data

data class CharactersFilters(
    val name: String? = null,
    val status: String? = null,
    val species: String? = null,
    val gender: String? = null
) {
    fun toQueryKey(): String {
        return "name=${name ?: ""}_status=${status ?: ""}_species=${species ?: ""}_gender=${gender ?: ""}"
    }

    fun hasActiveFilters(): Boolean {
        return name?.isNotBlank() == true ||
                status?.isNotBlank() == true ||
                species?.isNotBlank() == true ||
                gender?.isNotBlank() == true
    }
}


