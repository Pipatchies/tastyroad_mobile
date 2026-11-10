package fr.tastyroad.network

import fr.tastyroad.domain.TokenRefreshRequest
import fr.tastyroad.domain.TokenResponse
import fr.tastyroad.session.AuthSession
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClient(
    private val baseUrl: String = Config.BASE_URL,
    engine: HttpClientEngine? = null,
) {
    val client =
        if (engine != null) {
            HttpClient(engine) { configureKtor(baseUrl) }
        } else {
            HttpClient { configureKtor(baseUrl) }
        }

    private fun HttpClientConfig<*>.configureKtor(baseUrl: String) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                },
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 60000L
            connectTimeoutMillis = 60000L
            socketTimeoutMillis = 60000L
        }

        defaultRequest {
            url(baseUrl)
        }

        install(Auth) {
            bearer {
                loadTokens {
                    AuthSession.token?.let { access ->
                        BearerTokens(access, AuthSession.refreshToken ?: "")
                    }
                }

                refreshTokens {
                    val refreshToken = oldTokens?.refreshToken
                    if (refreshToken.isNullOrBlank()) {
                        AuthSession.logout()
                        return@refreshTokens null
                    }

                    try {
                        val response: TokenResponse =
                            client.post("token/refresh/") {
                                markAsRefreshTokenRequest()
                                contentType(ContentType.Application.Json)
                                setBody(TokenRefreshRequest(refreshToken))
                            }.body()

                        AuthSession.token = response.access
                        if (response.refresh != null) {
                            AuthSession.refreshToken = response.refresh
                        }

                        BearerTokens(
                            accessToken = response.access,
                            refreshToken = response.refresh ?: refreshToken,
                        )
                    } catch (e: Exception) {
                        AuthSession.logout()
                        null
                    }
                }

                // Autorise l'envoi des requêtes aux routes publiques même sans token
                sendWithoutRequest { true }
            }
        }
    }
}

object AppClient {
    val instance = ApiClient().client
}
