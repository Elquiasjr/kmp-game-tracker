package br.edu.utfpr.kmptracker.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Resposta paginada de GET /games. */
@Serializable
data class GamesResponseDto(
    val count: Int = 0,
    val next: String? = null,
    val results: List<GameDto> = emptyList(),
)

/** Usado tanto na lista (GET /games) quanto nos detalhes (GET /games/{id}). */
@Serializable
data class GameDto(
    val id: Int,
    val name: String,
    val released: String? = null,
    @SerialName("background_image") val backgroundImage: String? = null,
    val rating: Double = 0.0,
    val metacritic: Int? = null,
    val genres: List<NamedDto> = emptyList(),
    // Só vem no endpoint de detalhes; texto puro (o campo "description" vem em HTML).
    @SerialName("description_raw") val descriptionRaw: String? = null,
    val website: String? = null,
)

@Serializable
data class NamedDto(val id: Int, val name: String)
