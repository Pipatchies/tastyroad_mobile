package fr.tastyroad.repository

import fr.tastyroad.domain.CreateItineraryRequest
import fr.tastyroad.domain.GeoJsonFeature
import fr.tastyroad.domain.GeoJsonFeatureCollection
import fr.tastyroad.domain.Itinerary
import fr.tastyroad.domain.RawItinerariesResponse
import fr.tastyroad.domain.Step
import fr.tastyroad.domain.TopItineraryDTO
import fr.tastyroad.network.AppClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json

class ItineraryRepository {
    private val client = AppClient.instance
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

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
        val response: GeoJsonFeature<Itinerary> = client.get("itineraries/$id/").body()
        return response.properties
    }

    suspend fun createItinerary(
        token: String,
        newItinerary: Itinerary,
    ): Itinerary {
        val stepsList: List<Step>? = newItinerary.steps?.features?.map { feature ->
            feature.properties.copy(
                location = feature.geometry ?: feature.properties.location
            )
        }

        val requestPayload = CreateItineraryRequest(
            title = newItinerary.title,
            type = newItinerary.type,
            zone = newItinerary.zone,
            distance = newItinerary.distance,
            duration = newItinerary.duration,
            difficulty = newItinerary.difficulty,
            diet = newItinerary.diet,
            speciality = newItinerary.speciality,
            facts = newItinerary.facts,
            path = newItinerary.path,
            steps = stepsList,
        )

        println("--> [ItineraryRepository] POST itineraries/ | Token: ${token.take(10)}... | Payload: $requestPayload")
        val httpResponse = client.post("itineraries/") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(requestPayload)
        }
        val responseText = httpResponse.bodyAsText()
        println("<-- [ItineraryRepository] Raw response status: ${httpResponse.status}, body text: $responseText")

        if (!httpResponse.status.isSuccess()) {
            throw Exception("HTTP ${httpResponse.status.value}: $responseText")
        }

        return try {
            val feature = json.decodeFromString<GeoJsonFeature<Itinerary>>(responseText)
            feature.properties.copy(id = feature.id ?: feature.properties.id)
        } catch (e: Exception) {
            println("x-- [ItineraryRepository] Failed decoding as GeoJsonFeature: ${e::class.simpleName} - ${e.message}")
            try {
                json.decodeFromString<Itinerary>(responseText)
            } catch (e2: Exception) {
                println("x-- [ItineraryRepository] Failed decoding as Itinerary: ${e2::class.simpleName} - ${e2.message}")
                newItinerary.copy(id = 1)
            }
        }
    }

    suspend fun updateItinerary(
        token: String,
        itineraryId: Int,
        updatedItinerary: Itinerary,
    ): Itinerary {
        val response: GeoJsonFeature<Itinerary> =
            client.put("itineraries/$itineraryId/") {
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
            client.delete("itineraries/$itineraryId/") {
                header("Authorization", "Bearer $token")
            }
        return response.status.isSuccess()
    }
}
