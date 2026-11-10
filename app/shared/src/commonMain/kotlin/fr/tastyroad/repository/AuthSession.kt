package fr.tastyroad.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.tastyroad.domain.User

object AuthSession {
    private val tokenStorage: TokenStorage? =
        try {
            provideTokenStorage()
        } catch (_: Exception) {
            null
        }

    private var _token by mutableStateOf<String?>(
        try {
            tokenStorage?.getToken()
        } catch (_: Exception) {
            null
        },
    )

    var token: String?
        get() = _token
        set(value) {
            _token = value
            try {
                if (value != null) {
                    tokenStorage?.saveToken(value)
                } else {
                    tokenStorage?.clearToken()
                }
            } catch (_: Exception) {
            }
        }

    var currentUser by mutableStateOf<User?>(null)

    val isLoggedIn: Boolean
        get() = currentUser != null || !_token.isNullOrBlank()

    fun logout() {
        _token = null
        currentUser = null
        try {
            tokenStorage?.clearToken()
        } catch (_: Exception) {
        }
    }
}
