package fr.tastyroad.repository

import fr.tastyroad.domain.GeoJsonFeature
import fr.tastyroad.domain.Step
import fr.tastyroad.network.AppClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class StepRepository {
    private val client = AppClient.instance

    suspend fun createStep(
        token: String,
        newStep: Step,
    ): Step {
        val response: GeoJsonFeature<Step> =
            client.post("steps/") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(newStep)
            }.body()
        return response.properties
    }

    suspend fun updateStep(
        token: String,
        stepId: Int,
        updatedStep: Step,
    ): Step {
        val response: GeoJsonFeature<Step> =
            client.put("steps/$stepId/") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(updatedStep)
            }.body()
        return response.properties
    }

    suspend fun deleteStep(
        token: String,
        stepId: Int,
    ): Boolean {
        return client.delete("steps/$stepId/") {
            header("Authorization", "Bearer $token")
        }.status.isSuccess()
    }
}
