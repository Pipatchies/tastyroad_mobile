package fr.tastyroad.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Step(
    val id: Int,
    val name: String,
    val description: String? = null,
    val picture: String? = null,
    @SerialName("step_order") val stepOrder: Int,
    val location: JsonObject? = null,
    val itinerary: Int,
    val foodplace: Int? = null
)