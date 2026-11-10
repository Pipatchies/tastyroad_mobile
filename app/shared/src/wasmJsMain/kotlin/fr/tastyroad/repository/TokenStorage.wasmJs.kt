package fr.tastyroad.repository

actual class TokenStorage {
    actual fun saveToken(token: String) {}

    actual fun getToken(): String? = null

    actual fun clearToken() {}

    actual fun saveRefreshToken(token: String) {}

    actual fun getRefreshToken(): String? = null

    actual fun clearRefreshToken() {}
}

actual fun provideTokenStorage(): TokenStorage = TokenStorage()
