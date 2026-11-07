package fr.tastyroad.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val username: String,
    val email: String? = null,
    val age: Int? = null,
    val avatar: String? = null,
)
