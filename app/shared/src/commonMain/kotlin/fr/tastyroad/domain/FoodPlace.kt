package fr.tastyroad.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class FoodPlace(
    val id: Int,
    val name: String,
    val type: String? = null,
    val description: String? = null,
    val location: JsonObject? = null
)