package br.edu.utfpr.kmptracker

import br.edu.utfpr.kmptracker.data.remote.RawgApi
import br.edu.utfpr.kmptracker.data.remote.createHttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

/** Testa a camada de rede sem internet e sem gastar a cota da API. */
class RawgApiTest {

    private val requests = mutableListOf<Url>()

    private fun apiReturning(body: String, status: HttpStatusCode = HttpStatusCode.OK): RawgApi {
        val engine = MockEngine { request ->
            requests += request.url
            respond(body, status, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        return RawgApi(createHttpClient(engine, apiKey = "chave-de-teste"))
    }

    @Test
    fun searchSendsApiKeyAndQuery() = runTest {
        apiReturning(SEARCH_JSON).searchGames("witcher")

        val url = requests.single()
        assertTrue(url.encodedPath.endsWith("/api/games"))
        assertEquals("chave-de-teste", url.parameters["key"])
        assertEquals("witcher", url.parameters["search"])
    }

    @Test
    fun blankQueryAsksForPopularGames() = runTest {
        apiReturning(SEARCH_JSON).searchGames("")

        val url = requests.single()
        assertNull(url.parameters["search"])
        assertEquals("-added", url.parameters["ordering"])
    }

    @Test
    fun parsesResultsIgnoringUnknownFields() = runTest {
        val response = apiReturning(SEARCH_JSON).searchGames("witcher")

        val game = response.results.single()
        assertEquals(3328, game.id)
        assertEquals("The Witcher 3: Wild Hunt", game.name)
        assertEquals(listOf("Action", "RPG"), game.genres.map { it.name })
        assertNull(game.descriptionRaw) // a lista não traz descrição
    }

    @Test
    fun httpErrorBecomesException() = runTest {
        val api = apiReturning("""{"error":"invalid key"}""", HttpStatusCode.Unauthorized)
        assertFailsWith<Exception> { api.searchGames("witcher") }
    }

    private companion object {
        // Trecho real da resposta, com campos extras que o app ignora.
        const val SEARCH_JSON = """
        {
          "count": 1,
          "next": null,
          "results": [
            {
              "id": 3328,
              "slug": "the-witcher-3-wild-hunt",
              "name": "The Witcher 3: Wild Hunt",
              "released": "2015-05-18",
              "background_image": "https://media.rawg.io/media/games/618/618c2031a07bbff6b4f611f10b6bcdbc.jpg",
              "rating": 4.65,
              "metacritic": 92,
              "playtime": 46,
              "genres": [ { "id": 4, "name": "Action", "slug": "action" },
                          { "id": 5, "name": "RPG", "slug": "role-playing-games-rpg" } ],
              "platforms": [ { "platform": { "id": 4, "name": "PC" } } ]
            }
          ]
        }
        """
    }
}
