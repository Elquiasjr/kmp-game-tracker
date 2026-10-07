package br.edu.utfpr.kmptracker.navigation

import kotlinx.serialization.Serializable

// Rotas type-safe do Navigation Compose: os argumentos viajam como propriedades.
@Serializable data object DiscoverRoute
@Serializable data object LibraryRoute
@Serializable data class DetailRoute(val gameId: Int)
