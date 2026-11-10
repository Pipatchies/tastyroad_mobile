package fr.tastyroad.domain

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
)

@Serializable
data class TokenResponse(
    val access: String,
    val refresh: String? = null,
)

@Serializable
data class TokenRefreshRequest(
    val refresh: String,
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val age: Int?,
    val password: String,
)
