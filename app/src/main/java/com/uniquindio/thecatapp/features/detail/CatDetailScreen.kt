package com.uniquindio.thecatapp.features.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.uniquindio.thecatapp.R
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun CatDetailScreen(
	modifier: Modifier = Modifier,
	onBack: () -> Unit = {}
) {
	val viewModel = hiltViewModel<CatDetailViewModel>()
	val uiState by viewModel.uiState.collectAsState()
	val scrollState = rememberScrollState()

	Box(
		modifier = modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.background)
	) {
		when {
			uiState.isLoading -> LoadingState()
			uiState.errorMessage != null -> ErrorState(message = uiState.errorMessage.orEmpty(), onRetry = viewModel::retry, onBack = onBack)
			uiState.detail != null -> {
					uiState.detail?.let { detail ->
					Column(
						modifier = Modifier
							.fillMaxSize()
							.verticalScroll(scrollState)
					) {
						HeroImage(
							detailUrl = detail.url,
							isFavorite = uiState.isFavorite,
							isFavLoading = uiState.isFavoriteLoading,
							onToggleFavorite = { viewModel.toggleFavorite() },
							onBack = onBack
						)
						DetailContent(detail = detail)
					}
				}
			}
			else -> EmptyState(onBack = onBack)
		}
	}
}

@Composable
private fun HeroImage(
	detailUrl: String,
	isFavorite: Boolean,
	isFavLoading: Boolean,
	onToggleFavorite: () -> Unit,
	onBack: () -> Unit
) {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.aspectRatio(0.92f)
	) {
		AsyncImage(
			model = ImageRequest.Builder(LocalContext.current)
				.data(detailUrl)
				.crossfade(true)
				.build(),
			contentDescription = "Imagen del gato",
			modifier = Modifier.fillMaxSize(),
			contentScale = ContentScale.Crop
		)

		Box(
			modifier = Modifier
				.fillMaxSize()
				.background(Color.Black.copy(alpha = 0.12f))
		)

		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 20.dp, vertical = 18.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.Top
		) {
			TopCircleButton(text = "←", onClick = onBack)
			Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
				val heartText = when {
					isFavLoading -> "…"
					isFavorite -> "♥"
					else -> "♡"
				}

				TopCircleButton(
					text = heartText,
					onClick = { if (!isFavLoading) onToggleFavorite() }
				)
			}
		}

		Row(
			modifier = Modifier
				.align(Alignment.BottomCenter)
				.padding(bottom = 16.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			IndicatorDot(active = true)
			IndicatorDot(active = false)
			IndicatorDot(active = false)
		}
	}
}

@Composable
private fun TopCircleButton(text: String, onClick: () -> Unit) {
	Surface(
		shape = RoundedCornerShape(999.dp),
		color = Color.White,
		tonalElevation = 2.dp,
		shadowElevation = 4.dp,
		onClick = onClick,
		modifier = Modifier.size(48.dp)
	) {
		Box(contentAlignment = Alignment.Center) {
			Text(text = text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
		}
	}
}

@Composable
private fun IndicatorDot(active: Boolean) {
	Box(
		modifier = Modifier
			.size(if (active) 10.dp else 8.dp)
			.clip(RoundedCornerShape(999.dp))
			.background(if (active) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.65f))
	)
}

@Composable
private fun DetailContent(detail: com.uniquindio.thecatapp.domain.model.CatImage) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(horizontal = 16.dp)
	) {
		DetailSheetHeader(detail = detail)
		Spacer(modifier = Modifier.height(12.dp))
		InformationCard(detail = detail)
		Spacer(modifier = Modifier.height(12.dp))
		TechnicalCard(detail = detail)
		Spacer(modifier = Modifier.height(12.dp))
		UrlCard(url = detail.url)
		Spacer(modifier = Modifier.height(20.dp))
		Text(
			text = stringResource(R.string.detail_sync_info),
			style = MaterialTheme.typography.bodySmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			modifier = Modifier.padding(bottom = 24.dp)
		)
	}
}

@Composable
private fun DetailSheetHeader(detail: com.uniquindio.thecatapp.domain.model.CatImage) {
	Card(
		shape = RoundedCornerShape(30.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
		elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(18.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Surface(
					shape = RoundedCornerShape(999.dp),
					color = MaterialTheme.colorScheme.primaryContainer
				) {
					Text(
						text = detail.breedName ?: "Gato",
						modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
						style = MaterialTheme.typography.labelLarge,
						fontWeight = FontWeight.Bold,
						color = MaterialTheme.colorScheme.onPrimaryContainer
					)
				}

				Surface(
					shape = RoundedCornerShape(999.dp),
					color = if (detail.categoryName != null) Color(0xFFDDF7E6) else MaterialTheme.colorScheme.surfaceVariant
				) {
					Text(
						text = if (detail.categoryName != null) stringResource(R.string.status_online) else stringResource(R.string.status_offline),
						modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
						style = MaterialTheme.typography.labelLarge,
						fontWeight = FontWeight.SemiBold,
						color = if (detail.categoryName != null) Color(0xFF0B7A2A) else MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}

			Spacer(modifier = Modifier.height(10.dp))

			Text(
				text = detail.breedName ?: "Gato disponible",
				style = MaterialTheme.typography.headlineSmall,
				fontWeight = FontWeight.ExtraBold,
				color = MaterialTheme.colorScheme.onSurface
			)
			Spacer(modifier = Modifier.height(6.dp))
			Text(
				text = "ID: ${detail.id}",
				style = MaterialTheme.typography.bodyMedium,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
	}
}

@Composable
private fun InformationCard(detail: com.uniquindio.thecatapp.domain.model.CatImage) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(18.dp)) {
			SectionTitle(text = stringResource(R.string.main_info))
			Spacer(modifier = Modifier.height(12.dp))

			InfoRow(label = stringResource(R.string.label_breed), value = detail.breedName ?: stringResource(R.string.not_available))
			DividerSpace()
			InfoRow(label = stringResource(R.string.label_category), value = detail.categoryName ?: stringResource(R.string.not_available))
			DividerSpace()
			InfoRow(label = "ID de raza", value = detail.breedId ?: stringResource(R.string.not_available))
			DividerSpace()
			InfoRow(label = "ID de categoría", value = detail.categoryId?.toString() ?: stringResource(R.string.not_available))
		}
	}
}

@Composable
private fun TechnicalCard(detail: com.uniquindio.thecatapp.domain.model.CatImage) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(18.dp)) {
			SectionTitle(text = stringResource(R.string.tech_data))
			Spacer(modifier = Modifier.height(12.dp))
			Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
				TechPill(label = stringResource(R.string.width), value = detail.width?.toString() ?: "—", modifier = Modifier.weight(1f))
				TechPill(label = stringResource(R.string.height), value = detail.height?.toString() ?: "—", modifier = Modifier.weight(1f))
			}
			Spacer(modifier = Modifier.height(12.dp))
			InfoRow(label = "MIME", value = detail.mimeType ?: stringResource(R.string.not_available))
		}
	}
}

@Composable
private fun UrlCard(url: String) {
	Card(
		shape = RoundedCornerShape(24.dp),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.padding(18.dp)) {
			SectionTitle(text = stringResource(R.string.image_url))
			Spacer(modifier = Modifier.height(10.dp))
			Surface(
				shape = RoundedCornerShape(18.dp),
				color = MaterialTheme.colorScheme.surfaceVariant
			) {
				Text(
					text = url,
					modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis
				)
			}
		}
	}
}

@Composable
private fun SectionTitle(text: String) {
	Text(
		text = text,
		style = MaterialTheme.typography.titleMedium,
		fontWeight = FontWeight.Bold,
		color = MaterialTheme.colorScheme.onSurface
	)
}

@Composable
private fun InfoRow(label: String, value: String) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically
	) {
		Column(modifier = Modifier.weight(1f)) {
			Text(
				text = label,
				style = MaterialTheme.typography.bodySmall,
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
			Text(
				text = value,
				style = MaterialTheme.typography.bodyLarge,
				fontWeight = FontWeight.SemiBold,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis
			)
		}
	}
}

@Composable
private fun TechPill(label: String, value: String, modifier: Modifier = Modifier) {
	Surface(
		shape = RoundedCornerShape(18.dp),
		color = MaterialTheme.colorScheme.secondaryContainer,
		modifier = modifier
	) {
		Column(modifier = Modifier.padding(14.dp)) {
			Text(
				text = label,
				style = MaterialTheme.typography.labelSmall,
				color = MaterialTheme.colorScheme.onSecondaryContainer
			)
			Text(
				text = value,
				style = MaterialTheme.typography.titleMedium,
				fontWeight = FontWeight.Bold
			)
		}
	}
}

@Composable
private fun DividerSpace() {
	Spacer(modifier = Modifier.height(12.dp))
}

@Composable
private fun LoadingState() {
	Box(
		modifier = Modifier.fillMaxSize(),
		contentAlignment = Alignment.Center
	) {
		CircularProgressIndicator()
	}
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit, onBack: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			text = message.ifBlank { stringResource(R.string.error_loading_detail) },
			color = MaterialTheme.colorScheme.error,
			style = MaterialTheme.typography.bodyLarge
		)
		Spacer(modifier = Modifier.height(16.dp))
		Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
			OutlinedButton(onClick = onBack) { Text(text = stringResource(R.string.back)) }
			Button(onClick = onRetry) { Text(text = stringResource(R.string.retry)) }
		}
	}
}

@Composable
private fun EmptyState(onBack: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
			verticalArrangement = Arrangement.Center,
			horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(text = stringResource(R.string.no_info), style = MaterialTheme.typography.titleLarge)
		Spacer(modifier = Modifier.height(16.dp))
		OutlinedButton(onClick = onBack) { Text(text = stringResource(R.string.back)) }
	}
}
