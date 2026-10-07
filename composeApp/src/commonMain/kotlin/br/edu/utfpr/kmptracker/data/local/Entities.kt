package br.edu.utfpr.kmptracker.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Cache do catálogo remoto: pode ser apagado e baixado de novo. */
@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val released: String?,
    val backgroundImage: String?,
    val rating: Double,
    val metacritic: Int?,
    val genres: String, // nomes separados por "|"
    val description: String?,
    val website: String?,
    val cachedAt: Long,
)

/** Dados do usuário: existem só no aparelho e nunca são sobrescritos pela API. */
@Entity(tableName = "library")
data class LibraryEntryEntity(
    @PrimaryKey val gameId: Int,
    val status: String,
    val updatedAt: Long,
)

/** Resultado do JOIN entre biblioteca e cache. */
data class LibraryItem(
    @Embedded val game: GameEntity,
    val status: String,
)
