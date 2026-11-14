package fr.tastyroad.domain

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class PhotonFeatureProperties(
    val name: String? = null,
    val city: String? = null,
    val country: String? = null,
    val osm_key: String? = null,
    val osm_value: String? = null
)

@Serializable
data class PhotonFeature(
    val geometry: JsonObject? = null,
    val properties: PhotonFeatureProperties
)

@Serializable
data class PhotonResponse(
    val features: List<PhotonFeature> = emptyList()
)

@Serializable
data class FoodPlace(
    val id: Int? = null,
    val name: String = "Lieu Inconnu",
    val type: String? = null,
    val description: String? = null,
    val location: JsonObject? = null,
)
