package com.example.rickandmortyapp.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyapp.domain.model.Character
import com.example.rickandmortyapp.domain.repository.CharactersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: CharactersRepository
) : ViewModel() {

    private val _characterState = MutableStateFlow<CharacterState>(CharacterState.Loading)
    val characterState: StateFlow<CharacterState> = _characterState.asStateFlow()

    private var currentCharacterId: Int? = null

    fun loadCharacter(id: Int) {
        if (currentCharacterId == id && _characterState.value is CharacterState.Success) {
            return
        }

        currentCharacterId = id
        _characterState.value = CharacterState.Loading

        viewModelScope.launch {
            repository.observeCharacter(id)
                .catch { e ->
                    _characterState.value = CharacterState.Error(
                        e.message ?: "Failed to load character"
                    )
                }
                .collect { character ->
                    when {
                        character != null -> {
                            _characterState.value = CharacterState.Success(character)
                        }
                        _characterState.value is CharacterState.Loading -> {
                            // Если персонажа нет в кэше и мы еще в состоянии загрузки,
                            // пытаемся загрузить принудительно
                            loadCharacterFromNetwork(id)
                        }
                    }
                }
        }
    }

    private suspend fun loadCharacterFromNetwork(id: Int) {
        try {
            repository.refreshCharacter(id)
            // После успешной загрузки observeCharacter автоматически получит данные
        } catch (e: Exception) {
            _characterState.value = CharacterState.Error(
                "Network error: ${e.message ?: "Check your connection"}"
            )
        }
    }
}

sealed class CharacterState {
    object Loading : CharacterState()
    data class Success(val character: Character) : CharacterState()
    data class Error(val message: String) : CharacterState()
}