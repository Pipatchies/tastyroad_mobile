package fr.tastyroad.domain

import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    val id: Int? = null,
    val user: Int? = null,
    val itinerary: Int,
)
