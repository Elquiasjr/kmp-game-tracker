package br.edu.utfpr.kmptracker

import br.edu.utfpr.kmptracker.data.formatRating
import br.edu.utfpr.kmptracker.data.local.GameEntity
import br.edu.utfpr.kmptracker.data.toBrazilianDate
import br.edu.utfpr.kmptracker.data.toDomain
import br.edu.utfpr.kmptracker.data.toEntity
import br.edu.utfpr.kmptracker.data.remote.GameDto
import br.edu.utfpr.kmptracker.data.remote.NamedDto
import kotlin.test.Test
import kotlin.test.assertEquals

class MappersTest {

    private val listDto = GameDto(
        id = 1,
        name = "Hades",
        released = "2020-09-17",
        rating = 4.46,
        genres = listOf(NamedDto(1, "Action"), NamedDto(2, "Indie")),
    )

    @Test
    fun keepsCachedDescriptionWhenListHasNone() {
        val cached = listDto.copy(descriptionRaw = "Roguelike no submundo grego").toEntity(now = 0, existing = null)

        val updated = listDto.toEntity(now = 10, existing = cached)

        assertEquals("Roguelike no submundo grego", updated.description)
        assertEquals(10L, updated.cachedAt)
    }

    @Test
    fun genresRoundTripThroughDatabase() {
        val entity: GameEntity = listDto.toEntity(now = 0, existing = null)
        assertEquals("Action|Indie", entity.genres)
        assertEquals(listOf("Action", "Indie"), entity.toDomain().genres)
    }

    @Test
    fun formatsValuesForBrazilianUsers() {
        assertEquals("17/09/2020", "2020-09-17".toBrazilianDate())
        assertEquals("4.5", 4.46.formatRating())
        assertEquals("4.0", 4.0.formatRating())
    }
}
