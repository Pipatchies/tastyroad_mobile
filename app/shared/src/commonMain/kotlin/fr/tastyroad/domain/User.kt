package fr.tastyroad.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val username: String,
    val age: Int? = null,
    val avatar: String? = null
)