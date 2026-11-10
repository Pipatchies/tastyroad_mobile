package fr.tastyroad.repository

import fr.tastyroad.domain.LoginRequest
import fr.tastyroad.domain.TokenResponse
import fr.tastyroad.domain.User
import fr.tastyroad.network.AppClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthRepository {
    private val client = AppClient.instance

    suspend fun login(credentials: LoginRequest): TokenResponse {
        return client.post("token/") {
            contentType(ContentType.Application.Json)
            setBody(credentials)
        }.body()
    }

    suspend fun register(request: fr.tastyroad.domain.RegisterRequest): User {
        return client.post("users/register/") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun requestPasswordReset(email: String) {
        client.post("users/password-reset/") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("email" to email))
        }
    }

    suspend fun getMyProfile(): User {
        return client.get("users/me/") {
            fr.tastyroad.session.AuthSession.token?.let {
                header("Authorization", "Bearer $it")
            }
        }.body()
    }
}
