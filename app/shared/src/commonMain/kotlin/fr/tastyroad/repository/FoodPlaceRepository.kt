package fr.tastyroad.repository

import fr.tastyroad.domain.FoodPlace
import fr.tastyroad.network.AppClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header

class FoodPlaceRepository {
    private val client = AppClient.instance

    suspend fun getFoodPlaces(): List<FoodPlace> {
        return client.get("/foodplaces/").body()
    }

    suspend fun getFoodPlacesAuthenticated(token: String): List<FoodPlace> {
        return client.get("/foodplaces/") {
            header("Authorization", "Bearer $token")
        }.body()
    }
}