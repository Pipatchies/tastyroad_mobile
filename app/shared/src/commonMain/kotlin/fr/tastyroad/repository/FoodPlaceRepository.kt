package fr.tastyroad.repository

import fr.tastyroad.domain.FoodPlace
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

class FoodPlaceRepository {
    private val client = AppClient.instance

    suspend fun getFoodPlaces(): List<FoodPlace> {
        return client.get("/foodplaces/").body()
    }

    suspend fun getFoodPlaceById(id: Int): FoodPlace {
        return client.get("/foodplaces/$id/").body()
    }

    suspend fun createFoodPlace(token: String, newFoodPlace: FoodPlace): FoodPlace {
        return client.post("/foodplaces/") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(newFoodPlace)
        }.body()
    }

    suspend fun updateFoodPlace(token: String, foodPlaceId: Int, updatedFoodPlace: FoodPlace): FoodPlace {
        return client.put("/foodplaces/$foodPlaceId/") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(updatedFoodPlace)
        }.body()
    }

    suspend fun deleteFoodPlace(token: String, foodPlaceId: Int): Boolean {
        return client.delete("/foodplaces/$foodPlaceId/") {
            header("Authorization", "Bearer $token")
        }.status.isSuccess()
    }
}