package br.edu.utfpr.kmptracker.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.kmptracker.data.GameRepository
import br.edu.utfpr.kmptracker.domain.Game
import br.edu.utfpr.kmptracker.domain.GameStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetailUiState(
    val game: Game? = null,
    val status: GameStatus? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

class DetailViewModel(
    private val gameId: Int,
    private val repository: GameRepository,
) : ViewModel() {

    private val isLoading = MutableStateFlow(true)
    private val error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DetailUiState> = combine(
        repository.observeGame(gameId),
        repository.observeStatus(gameId),
        isLoading,
        error,
    ) { game, status, loading, err -> DetailUiState(game, status, loading, err) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailUiState())

    init {
        // Mostra o que já está no cache e busca a descrição completa em paralelo.
        viewModelScope.launch {
            val result = repository.refreshGame(gameId)
            error.value = if (result.isFailure) "Não foi possível atualizar os detalhes (sem conexão?)" else null
            isLoading.value = false
        }
    }

    /** Tocar no status já selecionado remove o jogo da biblioteca. */
    fun onStatusClick(clicked: GameStatus) {
        val newStatus = if (uiState.value.status == clicked) null else clicked
        viewModelScope.launch { repository.setStatus(gameId, newStatus) }
    }
}
