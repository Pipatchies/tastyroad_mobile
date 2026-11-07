package fr.tastyroad.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Itinerary(
    val id: Int? = null,
    val title: String,
    val type: String? = null,
    val zone: String? = null,
    val distance: Double? = null,
    val duration: String? = null,
    val difficulty: String? = null,
    val diet: String? = null,
    val speciality: String? = null,
    val facts: String? = null,
    val path: JsonObject? = null,
    val author: Int,
    val favorites: List<Int> = emptyList(),
    val steps: GeoJsonFeatureCollection<Step>? = null,
    val comments: List<Comment> = emptyList(),
)
