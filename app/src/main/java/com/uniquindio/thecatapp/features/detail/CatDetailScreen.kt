package com.uniquindio.thecatapp.features.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CatDetailScreen(
	modifier: Modifier = Modifier,
	onBack: () -> Unit = {}
) {
	val viewModel: CatDetailViewModel = hiltViewModel()
	val uiState by viewModel.uiState.collectAsState()

	Column(
		modifier = modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		OutlinedButton(onClick = onBack) {
			Text(text = "Volver")
		}

		if (!uiState.isOnline) {
			Text(
				text = "Sin conexión: mostrando datos en caché si están disponibles",
				color = MaterialTheme.colorScheme.error,
				modifier = Modifier.padding(top = 12.dp)
			)
		}

		when {
			uiState.isLoading -> {
				CircularProgressIndicator(modifier = Modifier.padding(top = 24.dp))
			}

			uiState.errorMessage != null -> {
				val errorMessage = uiState.errorMessage ?: "No se pudo cargar el detalle"
				Text(
					text = errorMessage,
					color = MaterialTheme.colorScheme.error,
					modifier = Modifier.padding(top = 16.dp)
				)
				Button(
					onClick = viewModel::retry,
					modifier = Modifier.padding(top = 12.dp)
				) {
					Text(text = "Reintentar")
				}
			}

			uiState.detail != null -> {
				uiState.detail?.let { detail ->
					Text(
						text = "ID: ${detail.id}",
						style = MaterialTheme.typography.titleMedium,
						modifier = Modifier.padding(top = 16.dp)
					)
					Text(text = detail.url, modifier = Modifier.padding(top = 8.dp))
					Text(text = "Mime: ${detail.mimeType ?: "No disponible"}", modifier = Modifier.padding(top = 8.dp))
					Text(text = "Tamaño: ${detail.width ?: "?"} x ${detail.height ?: "?"}", modifier = Modifier.padding(top = 8.dp))
					Text(text = "Raza: ${detail.breedName ?: "No disponible"}", modifier = Modifier.padding(top = 8.dp))
					Text(text = "Categoria: ${detail.categoryName ?: "No disponible"}", modifier = Modifier.padding(top = 8.dp))
				}
			}

			else -> {
				Text(
					text = "No hay información para mostrar",
					modifier = Modifier.padding(top = 16.dp)
				)
			}
		}
	}
}
