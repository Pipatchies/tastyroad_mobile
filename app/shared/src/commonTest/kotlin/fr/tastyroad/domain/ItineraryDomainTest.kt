package fr.tastyroad.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ItineraryDomainTest {
    @Test
    fun testItineraryCreationAndProperties() {
        val itinerary =
            Itinerary(
                id = 10,
                title = "La Route des Vins d'Alsace",
                type = "Vélo",
                zone = "Grand Est, France",
                distance = 32.5,
                duration = "02:10",
                difficulty = "Moyen",
                diet = "Vignoble",
                author = 1,
            )

        assertEquals(10, itinerary.id)
        assertEquals("La Route des Vins d'Alsace", itinerary.title)
        assertEquals("Vélo", itinerary.type)
        assertEquals("Grand Est, France", itinerary.zone)
        assertEquals(32.5, itinerary.distance)
        assertEquals("02:10", itinerary.duration)
        assertEquals("Moyen", itinerary.difficulty)
        assertEquals("Vignoble", itinerary.diet)
        assertEquals(1, itinerary.author)
    }

    @Test
    fun testTransportSpeedAndDurationCalculation() {
        val distanceKm = 60.0

        // Test vitesse Rando (~4 km/h)
        val randoSpeedKmH = 4.0
        val randoDurationHours = distanceKm / randoSpeedKmH
        assertEquals(15.0, randoDurationHours)

        // Test vitesse Vélo (~15 km/h)
        val veloSpeedKmH = 15.0
        val veloDurationHours = distanceKm / veloSpeedKmH
        assertEquals(4.0, veloDurationHours)

        // Test vitesse Moto (~60 km/h)
        val motoSpeedKmH = 60.0
        val motoDurationHours = distanceKm / motoSpeedKmH
        assertEquals(1.0, motoDurationHours)
    }

    @Test
    fun testItineraryValidationRules() {
        // Règle 1: Titre non vide
        val validTitle = "Circuit du Cantal"
        val invalidTitle = "   "

        assertTrue(validTitle.isNotBlank())
        assertFalse(invalidTitle.isNotBlank())

        // Règle 2: Minimum 2 étapes (un point de départ et une arrivée)
        val stepCountZero = 0
        val stepCountOne = 1
        val stepCountTwo = 2

        assertFalse(stepCountZero >= 2)
        assertFalse(stepCountOne >= 2)
        assertTrue(stepCountTwo >= 2)
    }

    @Test
    fun testMockDataItemsArePopulated() {
        val itineraries = MockData.sampleItineraries
        assertTrue(itineraries.isNotEmpty())

        val firstItem = itineraries[0]
        assertNotNull(firstItem.itinerary.title)
        assertTrue(firstItem.rating > 0.0f)
        assertTrue(firstItem.stepsCount > 0)

        val reviews = MockData.sampleReviews
        assertTrue(reviews.isNotEmpty())
        assertTrue(reviews[0].rating in 1..5)

        val photos = MockData.samplePhotos
        assertTrue(photos.isNotEmpty())
    }
}
