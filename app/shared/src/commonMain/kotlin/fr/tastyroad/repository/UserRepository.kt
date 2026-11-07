package fr.tastyroad.repository

import fr.tastyroad.domain.User
import fr.tastyroad.network.AppClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class UserRepository {
    private val client = AppClient.instance

    suspend fun getUserProfile(userId: Int): User {
        return client.get("/users/$userId/").body()
    }

    suspend fun getMyProfile(token: String): User {
        return client.get("/users/me/") {
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun updateMyProfile(
        token: String,
        updatedUser: User,
    ): User {
        return client.put("/users/me/") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(updatedUser)
        }.body()
    }
}
