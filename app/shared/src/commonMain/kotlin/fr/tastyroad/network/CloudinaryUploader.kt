package fr.tastyroad.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class CloudinaryResponse(
    @SerialName("secure_url") val secureUrl: String? = null,
    val url: String? = null,
)

object CloudinaryUploader {
    private val client =
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

    suspend fun uploadImage(
        bytes: ByteArray,
        cloudName: String = Config.CLOUD_NAME,
        uploadPreset: String = Config.UPLOAD_PRESET,
    ): String? {
        if (bytes.isEmpty()) return null
        val uploadUrl = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"

        return try {
            val response: CloudinaryResponse =
                client.submitFormWithBinaryData(
                    url = uploadUrl,
                    formData =
                        formData {
                            append("upload_preset", uploadPreset)
                            append(
                                key = "file",
                                value = bytes,
                                headers =
                                    Headers.build {
                                        append(HttpHeaders.ContentType, "image/jpeg")
                                        append(HttpHeaders.ContentDisposition, "filename=\"photo.jpg\"")
                                    },
                            )
                        },
                ).body()

            response.secureUrl ?: response.url
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
