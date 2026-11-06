package fr.tastyroad.domain

import kotlinx.serialization.Serializable

@Serializable
data class GeoJsonFeatureCollection<T>(
    val type: String,
    val features: List<GeoJsonFeature<T>>
)

@Serializable
data class GeoJsonFeature<T>(
    val type: String,
    val properties: T, // T sera remplacé par Itinerary, Step, ou FoodPlace
    val geometry: kotlinx.serialization.json.JsonObject? = null
)