package br.edu.utfpr.kmptracker.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    // ---- cache do catálogo ----
    @Upsert
    suspend fun upsertGames(games: List<GameEntity>)

    @Query("SELECT * FROM games WHERE name LIKE '%' || :query || '%' ORDER BY rating DESC LIMIT 60")
    fun searchGames(query: String): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :id")
    fun observeGame(id: Int): Flow<GameEntity?>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGame(id: Int): GameEntity?

    // ---- biblioteca do usuário ----
    @Upsert
    suspend fun upsertLibraryEntry(entry: LibraryEntryEntity)

    @Query("DELETE FROM library WHERE gameId = :gameId")
    suspend fun removeFromLibrary(gameId: Int)

    @Query("SELECT status FROM library WHERE gameId = :gameId")
    fun observeStatus(gameId: Int): Flow<String?>

    @Query(
        """
        SELECT games.*, library.status AS status
        FROM library INNER JOIN games ON games.id = library.gameId
        ORDER BY library.updatedAt DESC
        """
    )
    fun observeLibrary(): Flow<List<LibraryItem>>
}
