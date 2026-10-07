package br.edu.utfpr.kmptracker.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.edu.utfpr.kmptracker.ui.components.RawgAttribution
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryScreen(
    onGameClick: (Int) -> Unit,
    viewModel: LibraryViewModel = koinViewModel(),
) {
    val items by viewModel.items.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize()) {
        Text("Minha biblioteca", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
        Box(Modifier.weight(1f)) {
            if (items.isEmpty()) {
                Text(
                    "Sua biblioteca está vazia. Abra um jogo e escolha um status.",
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
            }
            LazyColumn(Modifier.fillMaxSize()) {
                items(items, key = { it.game.id }) { item ->
                    ListItem(
                        headlineContent = { Text(item.game.name) },
                        supportingContent = { Text(item.status.label) },
                        leadingContent = {
                            AsyncImage(
                                model = item.game.imageUrl,
                                contentDescription = item.game.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                            )
                        },
                        modifier = Modifier.clickable { onGameClick(item.game.id) },
                    )
                    HorizontalDivider()
                }
            }
        }
        RawgAttribution(Modifier.align(Alignment.CenterHorizontally))
    }
}
