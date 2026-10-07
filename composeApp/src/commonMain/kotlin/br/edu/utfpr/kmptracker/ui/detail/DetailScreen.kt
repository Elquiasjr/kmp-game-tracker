package br.edu.utfpr.kmptracker.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.utfpr.kmptracker.data.formatRating
import br.edu.utfpr.kmptracker.data.toBrazilianDate
import br.edu.utfpr.kmptracker.domain.GameStatus
import br.edu.utfpr.kmptracker.ui.components.RawgAttribution
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    gameId: Int,
    onBack: () -> Unit,
    viewModel: DetailViewModel = koinViewModel { parametersOf(gameId) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val haptics = LocalHapticFeedback.current // vibração no celular; sem efeito no desktop

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.game?.name ?: "Detalhes", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { TextButton(onClick = onBack) { Text("← Voltar") } },
            )
        },
    ) { padding ->
        val game = state.game
        when {
            game == null && state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                CircularProgressIndicator()
            }

            game == null -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) {
                Text(state.error ?: "Jogo não encontrado.")
            }

            else -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 900.dp) // no desktop o conteúdo não estica demais
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    AsyncImage(
                        model = game.imageUrl,
                        contentDescription = game.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).clip(RoundedCornerShape(12.dp)),
                    )
                    Text(game.name, style = MaterialTheme.typography.headlineSmall)
                    Text("Lançamento: ${game.released?.toBrazilianDate() ?: "não informado"}")
                    Text(
                        "Nota RAWG: ⭐ ${game.rating.formatRating()}" +
                            (game.metacritic?.let { " · Metacritic: $it" } ?: ""),
                    )
                    if (game.genres.isNotEmpty()) Text("Gêneros: ${game.genres.joinToString()}")
                    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                    Text("Minha biblioteca", style = MaterialTheme.typography.titleMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        GameStatus.entries.forEach { status ->
                            FilterChip(
                                selected = state.status == status,
                                onClick = {
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    viewModel.onStatusClick(status)
                                },
                                label = { Text(status.label) },
                            )
                        }
                    }

                    Text("Descrição", style = MaterialTheme.typography.titleMedium)
                    Text(
                        game.description?.takeIf { it.isNotBlank() }
                            ?: if (state.isLoading) "Carregando descrição…" else "Descrição indisponível offline.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    RawgAttribution()
                }
            }
        }
    }
}
