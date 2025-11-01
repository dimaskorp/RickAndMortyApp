package com.example.rickandmortyapp.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.rickandmortyapp.data.CharactersFilters
import com.example.rickandmortyapp.domain.model.Character
import com.example.rickandmortyapp.domain.repository.CharactersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CharactersRepository
) : ViewModel() {

    private val _filters = MutableStateFlow(CharactersFilters())
    private val _forceRefresh = MutableStateFlow(false)
    private val _instantSearchResults = MutableStateFlow<List<Character>?>(null)

    val pagingData: Flow<PagingData<Character>> = _filters
        .debounce(500)
        .distinctUntilChanged()
        .combine(_forceRefresh) { filters, _ -> filters }
        .flatMapLatest { filters ->
            repository.getCharacters(filters)
                .catch { e ->
                    emit(PagingData.empty())
                }
        }
        .cachedIn(viewModelScope)

    val instantSearchResults: StateFlow<List<Character>?> = _instantSearchResults.asStateFlow()

    fun updateFilters(newFilters: CharactersFilters) {
        if (newFilters != _filters.value) {
            // При любом изменении фильтров показываем мгновенные результаты из кэша
            showInstantResults(newFilters)
            _filters.value = newFilters
        }
    }

    fun refreshData() {
        repository.clearCacheForFilters(_filters.value)
        _forceRefresh.value = !_forceRefresh.value
        _instantSearchResults.value = null
    }

    fun getCurrentFilters(): CharactersFilters = _filters.value

    fun showInstantResults(filters: CharactersFilters) {
        val cachedResults = repository.getCharactersFromCache(filters)
        if (cachedResults.isNotEmpty()) {
            _instantSearchResults.value = cachedResults
        } else {
            _instantSearchResults.value = null
        }
    }
}


