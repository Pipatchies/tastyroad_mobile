package fr.tastyroad.repository

import fr.tastyroad.domain.LoginRequest
import fr.tastyroad.domain.TokenResponse
import fr.tastyroad.domain.User
import fr.tastyroad.network.AppClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthRepository {
    private val client = AppClient.instance

    suspend fun login(credentials: LoginRequest): TokenResponse {
        return client.post("/token/") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }.body()
    }

    suspend fun register(newUser: User): User {
        return client.post("/users/register/") {
            contentType(ContentType.Application.Json)
            setBody(newUser)
        }.body()
    }
}