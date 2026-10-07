package br.edu.utfpr.kmptracker.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.kmptracker.data.GameRepository
import br.edu.utfpr.kmptracker.domain.Game
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DiscoverUiState(
    val query: String = "",
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val offlineMessage: String? = null,
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class DiscoverViewModel(private val repository: GameRepository) : ViewModel() {

    private val query = MutableStateFlow("")
    private val isLoading = MutableStateFlow(false)
    private val offlineMessage = MutableStateFlow<String?>(null)

    // ③ debounce: espera 400 ms sem digitação antes de buscar
    private val debouncedQuery = query.debounce(400).distinctUntilChanged()

    val uiState: StateFlow<DiscoverUiState> = combine(
        query,
        debouncedQuery.flatMapLatest { repository.searchCached(it) },
        isLoading,
        offlineMessage,
    ) { q, games, loading, offline -> DiscoverUiState(q, games, loading, offline) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DiscoverUiState())

    init {
        // Cada nova busca (após o debounce) atualiza o cache pela rede.
        viewModelScope.launch { debouncedQuery.collectLatest { refresh(it) } }
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun retry() {
        viewModelScope.launch { refresh(query.value) }
    }

    private suspend fun refresh(q: String) {
        isLoading.value = true
        val result = repository.refreshSearch(q)
        offlineMessage.value = if (result.isFailure) "Sem conexão com a RAWG: exibindo jogos salvos" else null
        isLoading.value = false
    }
}
