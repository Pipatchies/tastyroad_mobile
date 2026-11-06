package fr.tastyroad.repository

import fr.tastyroad.domain.Favorite
import fr.tastyroad.network.AppClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class FavoriteRepository {
    private val client = AppClient.instance

    suspend fun getMyFavorites(token: String): List<Favorite> {
        return client.get("/favorites/") {
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun addFavorite(token: String, itineraryId: Int): Favorite {
        val newFavorite = Favorite(itinerary = itineraryId)

        return client.post("/favorites/") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(newFavorite)
        }.body()
    }

    suspend fun removeFavorite(token: String, favoriteId: Int): Boolean {

        return client.delete("/favorites/$favoriteId/") {
            header("Authorization", "Bearer $token")
        }.status.isSuccess()
    }
}