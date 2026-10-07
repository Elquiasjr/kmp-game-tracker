package br.edu.utfpr.kmptracker.domain

/** Modelo usado pela UI: não conhece JSON nem SQL. */
data class Game(
    val id: Int,
    val name: String,
    val released: String?,
    val imageUrl: String?,
    val rating: Double,
    val metacritic: Int?,
    val genres: List<String>,
    val description: String?,
    val website: String?,
) {
    val releaseYear: String? get() = released?.take(4)
}

enum class GameStatus(val label: String) {
    WANT_TO_PLAY("Quero jogar"),
    PLAYING("Jogando"),
    FINISHED("Zerado"),
    DROPPED("Abandonado"),
}

data class LibraryGame(val game: Game, val status: GameStatus)
