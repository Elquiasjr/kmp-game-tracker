package br.edu.utfpr.kmptracker.data

import br.edu.utfpr.kmptracker.data.local.GameDao
import br.edu.utfpr.kmptracker.data.local.LibraryEntryEntity
import br.edu.utfpr.kmptracker.data.remote.GameDto
import br.edu.utfpr.kmptracker.data.remote.RawgApi
import br.edu.utfpr.kmptracker.domain.Game
import br.edu.utfpr.kmptracker.domain.GameStatus
import br.edu.utfpr.kmptracker.domain.LibraryGame
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Fonte única de verdade: a UI só observa o banco (Flows do Room);
 * a rede apenas atualiza o banco. Sem internet, o cache continua na tela.
 */
@OptIn(ExperimentalTime::class)
class GameRepository(
    private val api: RawgApi,
    private val dao: GameDao,
    private val now: () -> Long = { Clock.System.now().toEpochMilliseconds() },
) {

    // ① a UI observa SEMPRE o banco local
    fun searchCached(query: String): Flow<List<Game>> =
        dao.searchGames(query.trim())
            .map { rows -> rows.map { it.toDomain() } }

    // ② a rede apenas atualiza o banco
    suspend fun refreshSearch(query: String) = safeCall {
        val response = api.searchGames(query.trim())
        saveAll(response.results)
    }

    fun observeGame(id: Int): Flow<Game?> = dao.observeGame(id).map { it?.toDomain() }

    suspend fun refreshGame(id: Int) = safeCall {
        saveAll(listOf(api.gameDetails(id)))
    }

    fun observeStatus(gameId: Int): Flow<GameStatus?> =
        dao.observeStatus(gameId).map { raw -> GameStatus.entries.firstOrNull { it.name == raw } }

    suspend fun setStatus(gameId: Int, status: GameStatus?) {
        if (status == null) dao.removeFromLibrary(gameId)
        else dao.upsertLibraryEntry(LibraryEntryEntity(gameId, status.name, now()))
    }

    fun observeLibrary(): Flow<List<LibraryGame>> =
        dao.observeLibrary().map { items -> items.mapNotNull { it.toDomain() } }

    private suspend fun saveAll(dtos: List<GameDto>) {
        val timestamp = now()
        dao.upsertGames(dtos.map { it.toEntity(timestamp, dao.getGame(it.id)) })
    }

    /** Como runCatching, mas sem engolir o cancelamento de corrotinas. */
    private inline fun safeCall(block: () -> Unit): Result<Unit> =
        try {
            block()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}
