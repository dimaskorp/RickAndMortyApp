package com.example.rickandmortyapp.data.dto

data class CharactersPageDto(
    val info: PageInfoDto,
    val results: List<CharacterDto>
)

data class PageInfoDto(
    val count: Int?,
    val pages: Int?,
    val next: String?,
    val prev: String?
)

data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: LocationRefDto,
    val location: LocationRefDto,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String
)

data class LocationRefDto(
    val name: String,
    val url: String
)


