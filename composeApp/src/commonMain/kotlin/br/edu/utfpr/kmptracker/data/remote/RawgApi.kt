package br.edu.utfpr.kmptracker.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Cria o HttpClient. O engine é recebido de fora:
 * OkHttp (Android/Desktop), Darwin (iOS) ou MockEngine (testes).
 */
fun createHttpClient(engine: HttpClientEngine, apiKey: String): HttpClient =
    HttpClient(engine) {
        expectSuccess = true // respostas 4xx/5xx viram exceção
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true }) // a RAWG devolve muitos campos que não usamos
        }
        install(HttpTimeout) { requestTimeoutMillis = 15_000 }
        defaultRequest {
            url("https://api.rawg.io/api/")
            url.parameters.append("key", apiKey) // a chave vai em TODA requisição
        }
    }

class RawgApi(private val client: HttpClient) {

    suspend fun searchGames(query: String, page: Int = 1, pageSize: Int = 20): GamesResponseDto =
        client.get("games") {
            parameter("page", page)
            parameter("page_size", pageSize)
            if (query.isNotBlank()) parameter("search", query) else parameter("ordering", "-added")
        }.body()

    suspend fun gameDetails(id: Int): GameDto = client.get("games/$id").body()
}
