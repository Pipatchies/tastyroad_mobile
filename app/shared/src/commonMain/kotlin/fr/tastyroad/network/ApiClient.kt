package fr.tastyroad.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiClientTest(
    private val baseUrl: String = Config.BASE_URL,
    engine: HttpClientEngine? = null
) {
    val client = if (engine != null) HttpClient(engine) { configureKtor(baseUrl) }
    else HttpClient { configureKtor(baseUrl) }

    private fun HttpClientConfig<*>.configureKtor(baseUrl: String) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        defaultRequest {
            url(baseUrl)
        }
    }
}

// Un Singleton pour faciliter l'accès dans le vrai code de l'application (hors tests)
object AppClient {
    val instance = ApiClientTest().client
}