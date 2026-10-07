package br.edu.utfpr.kmptracker.data

import br.edu.utfpr.kmptracker.data.local.GameEntity
import br.edu.utfpr.kmptracker.data.local.LibraryItem
import br.edu.utfpr.kmptracker.data.remote.GameDto
import br.edu.utfpr.kmptracker.domain.Game
import br.edu.utfpr.kmptracker.domain.GameStatus
import br.edu.utfpr.kmptracker.domain.LibraryGame

private const val GENRE_SEPARATOR = "|"

/**
 * Converte a resposta da API em linha do banco.
 * A lista de jogos não traz descrição: por isso reaproveitamos a que já
 * estava salva (`existing`), em vez de apagá-la com null.
 */
fun GameDto.toEntity(now: Long, existing: GameEntity?): GameEntity = GameEntity(
    id = id,
    name = name,
    released = released,
    backgroundImage = backgroundImage,
    rating = rating,
    metacritic = metacritic,
    genres = genres.joinToString(GENRE_SEPARATOR) { it.name },
    description = descriptionRaw ?: existing?.description,
    website = website ?: existing?.website,
    cachedAt = now,
)

fun GameEntity.toDomain(): Game = Game(
    id = id,
    name = name,
    released = released,
    imageUrl = backgroundImage,
    rating = rating,
    metacritic = metacritic,
    genres = genres.split(GENRE_SEPARATOR).filter { it.isNotBlank() },
    description = description,
    website = website,
)

fun LibraryItem.toDomain(): LibraryGame? {
    val parsed = GameStatus.entries.firstOrNull { it.name == status } ?: return null
    return LibraryGame(game.toDomain(), parsed)
}

/** "2015-05-18" -> "18/05/2015". Mantém o texto original se o formato for outro. */
fun String.toBrazilianDate(): String {
    val parts = split("-")
    return if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else this
}

/** 4.4666 -> "4.5" sem depender de String.format (indisponível no código comum). */
fun Double.formatRating(): String {
    val tenths = kotlin.math.round(this * 10).toInt()
    return "${tenths / 10}.${tenths % 10}"
}
