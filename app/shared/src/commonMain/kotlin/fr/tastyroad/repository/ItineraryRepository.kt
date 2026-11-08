package fr.tastyroad.repository

import fr.tastyroad.domain.GeoJsonFeature
import fr.tastyroad.domain.GeoJsonFeatureCollection
import fr.tastyroad.domain.Itinerary
import fr.tastyroad.domain.RawItinerariesResponse
import fr.tastyroad.domain.TopItineraryDTO
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
        val response: GeoJsonFeatureCollection<Itinerary> = client.get("itineraries/").body()
        return response.features.map { it.properties }
    }

    suspend fun getTopItineraries(limit: Int = 5): List<TopItineraryDTO> {
        val response: RawItinerariesResponse =
            client.get("raw/itineraries/") {
                url { parameters.append("limit", limit.toString()) }
            }.body()
        return response.data
    }

    suspend fun getItineraryById(id: Int): Itinerary {
        val response: GeoJsonFeature<Itinerary> = client.get("/itineraries/$id/").body()
        return response.properties
    }

    suspend fun createItinerary(
        token: String,
        newItinerary: Itinerary,
    ): Itinerary {
        val response: GeoJsonFeature<Itinerary> =
            client.post("/itineraries/") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(newItinerary)
            }.body()
        return response.properties
    }

    suspend fun updateItinerary(
        token: String,
        itineraryId: Int,
        updatedItinerary: Itinerary,
    ): Itinerary {
        val response: GeoJsonFeature<Itinerary> =
            client.put("/itineraries/$itineraryId/") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(updatedItinerary)
            }.body()
        return response.properties
    }

    suspend fun deleteItinerary(
        token: String,
        itineraryId: Int,
    ): Boolean {
        val response =
            client.delete("/itineraries/$itineraryId/") {
                header("Authorization", "Bearer $token")
            }
        return response.status.isSuccess()
    }
}
