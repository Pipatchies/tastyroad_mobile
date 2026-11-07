package fr.tastyroad.api

import fr.tastyroad.domain.Itinerary
import fr.tastyroad.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApiClientTest {
    @Test
    fun fetchItinerariesReturnsValidListFromMockBackend() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = ByteReadChannel(
                    """
                    [
                        {
                            "id": 1,
                            "title": "Parcours Toulouse Centre",
                            "type": "Balade",
                            "zone": "Occitanie",
                            "distance": 5.2,
                            "duration": "01:30:00",
                            "difficulty": "Facile",
                            "diet": "Omnivore",
                            "speciality": "Tapas",
                            "facts": "Un super parcours pour découvrir le capitole.",
                            "path": {
                                "type": "LineString",
                                "coordinates": [
                                    [1.444209, 43.604652],
                                    [1.441990, 43.600678]
                                ]
                            },
                            "author": 42,
                            "favorites": [10, 15, 42],
                            "steps": [],
                            "comments": []
                        }
                    ]
                    """.trimIndent(),
                ),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val fakeClient = ApiClient(
            baseUrl = "http://localhost:8000/api",
            engine = mockEngine,
        ).client

        val response: List<Itinerary> = fakeClient.get("/itineraries/").body()

        val itinerary = response[0]

        assertEquals(1, response.size)
        assertEquals(1, itinerary.id)
        assertEquals("Parcours Toulouse Centre", itinerary.title)
        assertEquals("Balade", itinerary.type)
        assertEquals(5.2, itinerary.distance)
        assertEquals("01:30:00", itinerary.duration)
        assertEquals(42, itinerary.author)

        assertEquals(3, itinerary.favorites.size)
        assertEquals(15, itinerary.favorites[1])

        assertNotNull(itinerary.path)
        assertEquals("LineString", itinerary.path["type"]?.toString()?.replace("\"", ""))
    }

    @Test
    fun testRealNetworkConnection() = runTest {
        val repo = fr.tastyroad.repository.ItineraryRepository()
        
        try {
            val response: List<Itinerary> = repo.getItineraries()
            
            println("✅ CONNEXION RÉUSSIE ! Voici le premier itinéraire reçu :")
            if (response.isNotEmpty()) {
                println("Titre: ${response[0].title}")
                println("Auteur ID: ${response[0].author}")
            } else {
                println("L'API a répondu, mais la liste d'itinéraires est vide.")
            }

            assertTrue(true)
        } catch (e: Exception) {
            println("❌ ÉCHEC DE LA CONNEXION : ${e.message}")
            // Fait échouer le test
            throw e
        }
    }
}
