package com.example.rickandmortyapp.data.mapper

import com.example.rickandmortyapp.data.dto.CharacterDto
import com.example.rickandmortyapp.domain.model.Character

fun CharacterDto.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    originName = origin.name,
    originUrl = origin.url,
    locationName = location.name,
    locationUrl = location.url,
    image = image,
    created = created,
)


