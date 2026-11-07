package fr.tastyroad.api

import fr.tastyroad.domain.GeoJsonFeatureCollection
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApiClientTest {
    @Test
    fun fetchItinerariesReturnsValidListFromMockBackend() =
        runTest {
            val mockEngine =
                MockEngine { _ ->
                    respond(
                        content =
                            ByteReadChannel(
                                """
                                {
                                    "type": "FeatureCollection",
                                    "features": [
                                        {
                                            "id": 1,
                                            "type": "Feature",
                                            "geometry": {
                                                "type": "LineString",
                                                "coordinates": [
                                                    [1.444209, 43.604652],
                                                    [1.441990, 43.600678]
                                                ]
                                            },
                                            "properties": {
                                                "title": "Parcours Toulouse Centre",
                                                "type": "Balade",
                                                "zone": "Occitanie",
                                                "distance": 5.2,
                                                "duration": "01:30:00",
                                                "difficulty": "Facile",
                                                "diet": "Omnivore",
                                                "speciality": "Tapas",
                                                "facts": "Un super parcours pour découvrir le capitole.",
                                                "author": 42,
                                                "favorites": [10, 15, 42],
                                                "steps": {
                                                    "type": "FeatureCollection",
                                                    "features": []
                                                },
                                                "comments": []
                                            }
                                        }
                                    ]
                                }
                                """.trimIndent(),
                            ),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val fakeClient =
                ApiClient(
                    baseUrl = "http://localhost:8000/api/",
                    engine = mockEngine,
                ).client

            val geoJsonResponse: GeoJsonFeatureCollection<Itinerary> = fakeClient.get("itineraries/").body()
            val itineraryFeature = geoJsonResponse.features[0]
            val itinerary = itineraryFeature.properties

            assertEquals(1, itineraryFeature.id)

            assertEquals("Parcours Toulouse Centre", itinerary.title)
            assertEquals("Balade", itinerary.type)
            assertEquals(5.2, itinerary.distance)
            assertEquals("01:30:00", itinerary.duration)
            assertEquals(42, itinerary.author)

            assertEquals(3, itinerary.favorites.size)
            assertEquals(15, itinerary.favorites[1])

            assertEquals(0, itinerary.steps?.features?.size)

            assertNotNull(itineraryFeature.geometry)
            assertEquals("LineString", itineraryFeature.geometry?.get("type")?.toString()?.replace("\"", ""))
        }

    @Ignore
    @Test
    fun testRealNetworkConnection() =
        runTest(timeout = kotlin.time.Duration.parse("120s")) {
            val repo = fr.tastyroad.repository.ItineraryRepository()

            var success = false
            var attempts = 0
            val maxAttempts = 3
            var lastException: Exception? = null

            while (!success && attempts < maxAttempts) {
                attempts++
                try {
                    println("Tentative de connexion $attempts/$maxAttempts...")
                    val response: List<Itinerary> = repo.getItineraries()

                    println("✅ CONNEXION RÉUSSIE ! Voici le premier itinéraire reçu :")
                    if (response.isNotEmpty()) {
                        println("Titre: ${response[0].title}")
                        println("Auteur ID: ${response[0].author}")
                    } else {
                        println("L'API a répondu, mais la liste d'itinéraires est vide.")
                    }

                    success = true
                    assertTrue(true)
                } catch (e: io.ktor.client.plugins.HttpRequestTimeoutException) {
                    println("⏳ Timeout au réveil du serveur (tentative $attempts). Attente avant réessai...")
                    lastException = e
                    delay(5000)
                } catch (e: Exception) {
                    println("❌ ÉCHEC DE LA CONNEXION (Autre erreur) : ${e.message}")
                    throw e
                }
            }

            if (!success && lastException != null) {
                throw lastException
            }
        }
}
