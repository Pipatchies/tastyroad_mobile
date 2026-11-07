package fr.tastyroad.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fr.tastyroad.domain.User

object AuthSession {
    var token by mutableStateOf<String?>(null)
    var currentUser by mutableStateOf<User?>(null)

    val isLoggedIn: Boolean
        get() = currentUser != null || token != null

    fun logout() {
        token = null
        currentUser = null
    }
}
