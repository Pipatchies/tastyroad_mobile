package fr.tastyroad.domain

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: Int,
    val content: String,
    val review: Int,
    val author: Int,
    val itinerary: Int
)