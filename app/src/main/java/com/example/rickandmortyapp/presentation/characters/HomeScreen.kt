package com.example.rickandmortyapp.presentation.characters

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.rickandmortyapp.data.CharactersFilters
import com.example.rickandmortyapp.ui.theme.RickAndMortyAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenDetails: (Int) -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val paging = viewModel.pagingData.collectAsLazyPagingItems()

    val instantResults by viewModel.instantSearchResults.collectAsState()
    val currentInstantResults = instantResults

    val loadState = paging.loadState
    val showInitialLoading = (loadState.refresh is LoadState.Loading) && paging.itemCount == 0
    val showErrorState = (loadState.refresh is LoadState.Error) && paging.itemCount == 0
    val showEmptyState = paging.itemCount == 0 &&
            loadState.refresh !is LoadState.Loading &&
            loadState.refresh !is LoadState.Error

    var searchActive by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showFiltersSheet by remember { mutableStateOf(false) }
    val gridState = rememberLazyGridState()

    // Локальное состояние для управления обновлением
    var currentFilters by remember { mutableStateOf(CharactersFilters()) }
    var searchQuery by remember { mutableStateOf("") }

    // При pull-to-refresh обновляем только текущие данные
    val pullRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(currentFilters) {
        val oldFilters = viewModel.getCurrentFilters()
        if (currentFilters != oldFilters) {
            viewModel.updateFilters(currentFilters)
        }
    }

    LaunchedEffect(searchActive) {
        if (searchActive) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(paging.loadState.refresh) {
        isRefreshing = paging.loadState.refresh is LoadState.Loading
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                title = {
                    if (searchActive) {
                        TextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                currentFilters = currentFilters.copy(name = it.ifBlank { null })
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                            placeholder = { Text("Search characters") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            leadingIcon = {
                                IconButton(onClick = {
                                    searchActive = false
                                    searchQuery = ""
                                    currentFilters = currentFilters.copy(name = null)
                                }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                                }
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = {
                                        searchQuery = ""
                                        currentFilters = currentFilters.copy(name = null)
                                    }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear search"
                                        )
                                    }
                                }
                            }
                        )
                    } else {
                        Text("Characters")
                    }
                },
                actions = {
                    if (!searchActive) {
                        IconButton(
                            onClick = { searchActive = true },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search characters"
                            )
                        }

                        Box {
                            IconButton(
                                onClick = { showFiltersSheet = true },
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Filters"
                                )
                            }
                            if (currentFilters.hasActiveFilters()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(2.dp)
                                        .size(8.dp)
                                ) {
                                    Canvas(
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        drawCircle(color = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->

        if (showInitialLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Loading characters...", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else if (showErrorState) {
            val errorState = loadState.refresh as LoadState.Error
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Network error",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    errorState.error.message ?: "Unknown error occurred",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { paging.retry() }) {
                    Text("Try Again")
                }
            }
        } else {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    viewModel.refreshData()
                },
                state = pullRefreshState,
                indicator = {
                    PullToRefreshDefaults.IndicatorBox(
                        state = pullRefreshState,
                        isRefreshing = isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                        elevation = 0.dp,
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator()
                        } else {
                            CircularProgressIndicator(
                                progress = { pullRefreshState.distanceFraction },
                                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                            )
                        }
                    }
                },
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Box(Modifier.fillMaxSize()) {
                    if (showEmptyState) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = "No results",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No characters found",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                if (currentFilters.hasActiveFilters() || searchQuery.isNotEmpty()) {
                                    "Try changing search or filters"
                                } else {
                                    "No characters available"
                                },
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            if (currentFilters.hasActiveFilters() || searchQuery.isNotEmpty()) {
                                Button(onClick = {
                                    currentFilters = CharactersFilters()
                                    searchQuery = ""
                                }) {
                                    Text("Clear all filters")
                                }
                            } else {
                                Button(onClick = { paging.retry() }) {
                                    Text("Try Again")
                                }
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize(),
                            state = gridState
                        ) {
                            // Показываем результаты из кеша
                            if (!currentInstantResults.isNullOrEmpty()) {
                                items(
                                    count = currentInstantResults.size,
                                    key = { index -> currentInstantResults[index].id }
                                ) { index ->
                                    CharacterCard(currentInstantResults[index], onOpenDetails)
                                }
                            }
                            // Иначе показываем обычные paging данные
                            else {
                                items(
                                    count = paging.itemCount,
                                    key = { index -> paging[index]?.id ?: index }
                                ) { index ->
                                    val item = paging[index]
                                    if (item != null) {
                                        CharacterCard(item, onOpenDetails)
                                    } else {
                                        // Плейсхолдер загрузки
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .aspectRatio(0.75f)
                                        ) {
                                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                                        }
                                    }
                                }

                                // Индикатор загрузки следующей страницы
                                if (paging.loadState.append is LoadState.Loading) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        Row(
                                            Modifier.fillMaxWidth().padding(16.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showFiltersSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFiltersSheet = false },
                sheetState = sheetState,
                modifier = Modifier.fillMaxSize()
            ) {
                FiltersBottomSheetContent(
                    currentFilters = currentFilters,
                    onFiltersApplied = { newFilters ->
                        currentFilters = newFilters
                        showFiltersSheet = false
                    },
                    onFiltersReset = {
                        currentFilters = CharactersFilters()
                        searchQuery = ""
                        showFiltersSheet = false
                    },
                    onDismiss = { showFiltersSheet = false }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    RickAndMortyAppTheme {
        HomeScreen(
            onOpenDetails = {}
        )
    }
}