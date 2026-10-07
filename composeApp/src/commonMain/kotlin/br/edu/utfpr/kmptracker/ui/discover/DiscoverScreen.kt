package br.edu.utfpr.kmptracker.ui.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.utfpr.kmptracker.platformName
import br.edu.utfpr.kmptracker.ui.components.GameCard
import br.edu.utfpr.kmptracker.ui.components.RawgAttribution
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DiscoverScreen(
    onGameClick: (Int) -> Unit,
    viewModel: DiscoverViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("KMP Game Tracker", style = MaterialTheme.typography.headlineSmall)
            // expect/actual em ação: o mesmo código mostra a plataforma atual
            Text(
                "Rodando em ${platformName()}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                label = { Text("Buscar jogos") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())

        state.offlineMessage?.let { message ->
            Surface(color = MaterialTheme.colorScheme.errorContainer, modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = viewModel::retry) { Text("Tentar de novo") }
                }
            }
        }

        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (state.games.isEmpty() && !state.isLoading) {
                Text(
                    "Nenhum jogo encontrado.",
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                )
            }
            // Grade adaptativa: 2 colunas no celular, várias no desktop.
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 180.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.games, key = { it.id }) { game ->
                    GameCard(game = game, onClick = { onGameClick(game.id) })
                }
            }
        }

        RawgAttribution(Modifier.align(Alignment.CenterHorizontally))
    }
}
