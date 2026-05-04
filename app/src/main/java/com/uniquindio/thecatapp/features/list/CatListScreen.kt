package com.uniquindio.thecatapp.features.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uniquindio.thecatapp.domain.model.Cat
import com.uniquindio.thecatapp.domain.model.CatBreed
import com.uniquindio.thecatapp.domain.model.CatCategory

@Composable
fun CatListScreen(
    modifier: Modifier = Modifier,
    onNavigateToDetail: (String) -> Unit = {}
) {
    val viewModel: CatListViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }.collect { lastVisibleIndex ->
            val totalItems = listState.layoutInfo.totalItemsCount

            if (totalItems > 0 && lastVisibleIndex != null && lastVisibleIndex >= totalItems - 3) {
                viewModel.loadCats()
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (!uiState.isOnline) {
            Text(
                text = "Sin conexión",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        val errorMessage = uiState.errorMessage
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        BreedFilters(
            breeds = uiState.breeds,
            selectedBreedId = uiState.selectedBreedId,
            onSelect = viewModel::onBreedSelected
        )

        CategoryFilters(
            categories = uiState.categories,
            selectedCategoryId = uiState.selectedCategoryId,
            onSelect = viewModel::onCategorySelected
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(uiState.cats, key = { it.id }) { cat ->
                CatCard(
                    cat = cat,
                    onClick = { onNavigateToDetail(cat.id) }
                )
            }

            item {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BreedFilters(
    breeds: List<CatBreed>,
    selectedBreedId: String?,
    onSelect: (String?) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Filtrar por raza",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            item {
                FilterChip(
                    selected = selectedBreedId == null,
                    onClick = { onSelect(null) },
                    label = { Text("Todas") },
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                )
            }

            items(breeds, key = { it.id }) { breed ->
                FilterChip(
                    selected = selectedBreedId == breed.id,
                    onClick = { onSelect(breed.id) },
                    label = { Text(breed.name) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryFilters(
    categories: List<CatCategory>,
    selectedCategoryId: Int?,
    onSelect: (Int?) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Filtrar por categoría",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            item {
                FilterChip(
                    selected = selectedCategoryId == null,
                    onClick = { onSelect(null) },
                    label = { Text("Todas") },
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                )
            }

            items(categories, key = { it.id }) { category ->
                FilterChip(
                    selected = selectedCategoryId == category.id,
                    onClick = { onSelect(category.id) },
                    label = { Text(category.name) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
    }
}

@Composable
fun CatCard(
    cat: Cat,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Cat ID: ${cat.id}")
            Text(text = cat.url)
        }
    }
}
