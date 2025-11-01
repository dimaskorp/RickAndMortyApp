package com.example.rickandmortyapp.di

import com.example.rickandmortyapp.data.api.RickAndMortyApi
import com.example.rickandmortyapp.data.repository.CharactersRepositoryImpl
import com.example.rickandmortyapp.domain.repository.CharactersRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCharactersRepository(api: RickAndMortyApi): CharactersRepository =
        CharactersRepositoryImpl(api)
}


