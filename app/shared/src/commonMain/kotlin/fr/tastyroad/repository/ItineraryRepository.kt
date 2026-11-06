package fr.tastyroad.repository

import fr.tastyroad.domain.Itinerary
import fr.tastyroad.network.AppClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class ItineraryRepository {
    private val client = AppClient.instance

    suspend fun getItineraries(): List<Itinerary> {
        return client.get("/itineraries/").body()
    }

    suspend fun createItinerary(
        token: String,
        newItinerary: Itinerary,
    ): Itinerary {
        return client.post("/itineraries/") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(newItinerary)
        }.body()
    }

    suspend fun updateItinerary(
        token: String,
        itineraryId: Int,
        updatedItinerary: Itinerary,
    ): Itinerary {
        return client.put("/itineraries/$itineraryId/") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(updatedItinerary)
        }.body()
    }

    suspend fun deleteItinerary(
        token: String,
        itineraryId: Int,
    ): Boolean {
        // L'URL inclut l'ID de l'itinéraire à supprimer
        val response =
            client.delete("/itineraries/$itineraryId/") {
                header("Authorization", "Bearer $token")
            }
        return response.status.isSuccess()
    }
}
