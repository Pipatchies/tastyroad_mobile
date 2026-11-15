package fr.tastyroad.repository

import fr.tastyroad.domain.FoodPlace
import fr.tastyroad.domain.GeoJsonFeature
import fr.tastyroad.domain.GeoJsonFeatureCollection
import fr.tastyroad.domain.PhotonResponse
import fr.tastyroad.network.AppClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class FoodPlaceRepository {
    private val client = AppClient.instance

    private val osmClient =
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

    suspend fun getFoodPlaces(): List<FoodPlace> {
        return try {
            val response: GeoJsonFeatureCollection<FoodPlace> = client.get("foodplaces/").body()
            response.features.map { it.properties.copy(id = it.id ?: it.properties.id) }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                client.get("foodplaces/").body()
            } catch (e2: Exception) {
                e2.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun searchFoodPlacesOSM(query: String): List<FoodPlace> {
        if (query.isBlank()) return emptyList()
        println("--> [FoodPlaceRepository] Searching OSM Photon for: '$query'")
        return try {
            val responseText =
                osmClient.get("https://photon.komoot.io/api/") {
                    parameter("q", query)
                    parameter("limit", "5")
                    parameter("lang", "fr")
                }.bodyAsText()

            println("<-- [FoodPlaceRepository] Photon response length: ${responseText.length}")
            val json =
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                }
            val parsed = json.decodeFromString<PhotonResponse>(responseText)

            parsed.features.mapNotNull { feature ->
                val name = feature.properties.name ?: return@mapNotNull null
                val type = feature.properties.osm_value ?: "Lieu"
                val city = feature.properties.city ?: ""
                val desc = if (city.isNotBlank()) "$name ($city)" else name

                FoodPlace(
                    id = null,
                    name = name,
                    type = type,
                    description = desc,
                    location = feature.geometry,
                )
            }.also {
                println("[FoodPlaceRepository] Found ${it.size} OSM food places.")
            }
        } catch (e: Exception) {
            println("x-- [FoodPlaceRepository] Error searching OSM: ${e::class.simpleName} - ${e.message}")
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getFoodPlaceById(id: Int): FoodPlace {
        return try {
            val feature: GeoJsonFeature<FoodPlace> = client.get("foodplaces/$id/").body()
            feature.properties.copy(id = feature.id ?: feature.properties.id, location = feature.geometry ?: feature.properties.location)
        } catch (e: Exception) {
            client.get("foodplaces/$id/").body()
        }
    }

    suspend fun createFoodPlace(
        token: String,
        newFoodPlace: FoodPlace,
    ): FoodPlace {
        return try {
            val feature: GeoJsonFeature<FoodPlace> =
                client.post("foodplaces/") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(newFoodPlace)
                }.body()
            feature.properties.copy(
                id = feature.id ?: feature.properties.id,
                location = feature.geometry ?: feature.properties.location,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            client.post("foodplaces/") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(newFoodPlace)
            }.body()
        }
    }

    suspend fun updateFoodPlace(
        token: String,
        foodPlaceId: Int,
        updatedFoodPlace: FoodPlace,
    ): FoodPlace {
        return try {
            val feature: GeoJsonFeature<FoodPlace> =
                client.put("foodplaces/$foodPlaceId/") {
                    header("Authorization", "Bearer $token")
                    contentType(ContentType.Application.Json)
                    setBody(updatedFoodPlace)
                }.body()
            feature.properties.copy(
                id = feature.id ?: feature.properties.id,
                location = feature.geometry ?: feature.properties.location,
            )
        } catch (e: Exception) {
            client.put("foodplaces/$foodPlaceId/") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(updatedFoodPlace)
            }.body()
        }
    }

    suspend fun deleteFoodPlace(
        token: String,
        foodPlaceId: Int,
    ): Boolean {
        return client.delete("foodplaces/$foodPlaceId/") {
            header("Authorization", "Bearer $token")
        }.status.isSuccess()
    }
}
