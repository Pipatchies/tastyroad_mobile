package fr.tastyroad.ui

import fr.tastyroad.domain.Itinerary
import fr.tastyroad.ui.components.filter.FilterState
import fr.tastyroad.ui.components.filter.matchesFilter
import fr.tastyroad.ui.viewmodels.MapViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilterAndSearchTest {
    private val sampleItineraries =
        listOf(
            Itinerary(
                id = 1,
                title = "Circuit des Fromages d'Occitanie",
                type = "Rando",
                zone = "Occitanie, France",
                distance = 15.0,
                duration = "03:30",
                difficulty = "Facile",
                diet = "Fromage",
            ),
            Itinerary(
                id = 2,
                title = "Route des Vins en Alsace",
                type = "Vélo",
                zone = "Grand Est, France",
                distance = 45.0,
                duration = "02:45",
                difficulty = "Moyenne",
                diet = "Vignoble",
            ),
            Itinerary(
                id = 3,
                title = "Virée Provençale",
                type = "Moto",
                zone = "PACA, France",
                distance = 120.0,
                duration = "02:00",
                difficulty = "Difficile",
                diet = "Viande",
            ),
        )

    @Test
    fun testTypeFilter() {
        val randoFilter = FilterState(selectedType = "Rando")
        val randoMatches = sampleItineraries.filter { it.matchesFilter(randoFilter) }

        assertEquals(1, randoMatches.size)
        assertEquals("Circuit des Fromages d'Occitanie", randoMatches[0].title)
    }

    @Test
    fun testZoneFilter() {
        val occitanieFilter = FilterState(selectedZone = "Occitanie")
        val matches = sampleItineraries.filter { it.matchesFilter(occitanieFilter) }

        assertEquals(1, matches.size)
        assertEquals("Circuit des Fromages d'Occitanie", matches[0].title)
    }

    @Test
    fun testDistanceFilter() {
        val shortDistanceFilter = FilterState(selectedDistance = "10 km - 20 km")
        val shortMatches = sampleItineraries.filter { it.matchesFilter(shortDistanceFilter) }
        assertEquals(1, shortMatches.size)
        assertEquals(1, shortMatches[0].id)

        val longDistanceFilter = FilterState(selectedDistance = "100 km >")
        val longMatches = sampleItineraries.filter { it.matchesFilter(longDistanceFilter) }
        assertEquals(1, longMatches.size)
        assertEquals(3, longMatches[0].id)
    }

    @Test
    fun testGastronomyFilter() {
        val vignobleFilter = FilterState(selectedGastronomy = "Vignoble")
        val matches = sampleItineraries.filter { it.matchesFilter(vignobleFilter) }

        assertEquals(1, matches.size)
        assertEquals("Route des Vins en Alsace", matches[0].title)
    }

    @Test
    fun testMultipleFiltersCombined() {
        val combinedFilter =
            FilterState(
                selectedType = "Vélo",
                selectedGastronomy = "Vignoble",
                selectedDifficulty = "Moyenne",
            )
        val matches = sampleItineraries.filter { it.matchesFilter(combinedFilter) }

        assertEquals(1, matches.size)
        assertEquals("Route des Vins en Alsace", matches[0].title)

        val impossibleFilter =
            FilterState(
                selectedType = "Moto",
                selectedGastronomy = "Vignoble",
            )
        val noMatches = sampleItineraries.filter { it.matchesFilter(impossibleFilter) }
        assertTrue(noMatches.isEmpty())
    }

    @Test
    fun testFilterStateIsActive() {
        val emptyFilter = FilterState()
        assertFalse(emptyFilter.isActive)

        val activeFilter = FilterState(selectedZone = "PACA")
        assertTrue(activeFilter.isActive)
    }

    @Test
    fun testMapViewModelSearchAndFilter() {
        val mapViewModel = MapViewModel()
        mapViewModel.performSearch("Occitanie")
        val state = mapViewModel.uiState.value

        assertEquals("Occitanie", state.searchQuery)
        assertEquals(43.6047, state.centerLat)
        assertEquals(1.4442, state.centerLng)
        assertEquals(10, state.zoomLevel)
    }
}
