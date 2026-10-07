package br.edu.utfpr.kmptracker.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.utfpr.kmptracker.data.GameRepository
import br.edu.utfpr.kmptracker.domain.LibraryGame
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class LibraryViewModel(repository: GameRepository) : ViewModel() {
    // Flow do Room: qualquer mudança no banco chega aqui automaticamente.
    val items: StateFlow<List<LibraryGame>> = repository.observeLibrary()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
