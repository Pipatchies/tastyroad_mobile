package fr.tastyroad.repository

import fr.tastyroad.domain.LoginRequest
import fr.tastyroad.domain.TokenResponse
import fr.tastyroad.domain.User
import fr.tastyroad.network.ApiClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ItineraryRepositoryIntegrationTest {
    @Test
    fun testLoginApiReturnsValidJwtToken() =
        runTest {
            val mockEngine =
                MockEngine { request ->
                    assertEquals("/api/token/", request.url.encodedPath)
                    respond(
                        content = ByteReadChannel("""{"access":"fake-jwt-access-token","refresh":"fake-refresh-token"}"""),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val testClient = ApiClient(baseUrl = "http://localhost:8000/api/", engine = mockEngine).client

            val response: TokenResponse =
                testClient.post("token/") {
                    contentType(ContentType.Application.Json)
                    setBody(LoginRequest("katsu", "Secret123!"))
                }.body()

            assertNotNull(response.access)
            assertEquals("fake-jwt-access-token", response.access)
        }

    @Test
    fun testRegisterApiCreatesNewUser() =
        runTest {
            val mockEngine =
                MockEngine { request ->
                    assertEquals("/api/users/register/", request.url.encodedPath)
                    respond(
                        content = ByteReadChannel("""{"id":42,"username":"katsu_bertaud","email":"kastuber@gmail.com","age":25}"""),
                        status = HttpStatusCode.Created,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val testClient = ApiClient(baseUrl = "http://localhost:8000/api/", engine = mockEngine).client

            val response: User =
                testClient.post("users/register/") {
                    contentType(ContentType.Application.Json)
                    setBody(User(username = "katsu_bertaud", email = "kastuber@gmail.com", age = 25))
                }.body()

            assertEquals(42, response.id)
            assertEquals("katsu_bertaud", response.username)
            assertEquals("kastuber@gmail.com", response.email)
            assertEquals(25, response.age)
        }

    @Test
    fun testDeleteAccountGdprEndpoint() =
        runTest {
            var authorizationHeaderValue: String? = null

            val mockEngine =
                MockEngine { request ->
                    assertEquals("/api/users/me/", request.url.encodedPath)
                    authorizationHeaderValue = request.headers[HttpHeaders.Authorization]

                    respond(
                        content = ByteReadChannel(""),
                        status = HttpStatusCode.NoContent,
                        headers = headersOf(HttpHeaders.ContentType, "application/json"),
                    )
                }

            val testClient = ApiClient(baseUrl = "http://localhost:8000/api/", engine = mockEngine).client

            val token = "test-jwt-token-123"
            val httpResponse =
                testClient.delete("users/me/") {
                    header(HttpHeaders.Authorization, "Bearer $token")
                }

            assertEquals(HttpStatusCode.NoContent, httpResponse.status)
            assertEquals("Bearer test-jwt-token-123", authorizationHeaderValue)
        }
}
