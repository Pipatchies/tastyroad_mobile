package fr.tastyroad.repository

expect class TokenStorage {
    fun saveToken(token: String)

    fun getToken(): String?

    fun clearToken()

    fun saveRefreshToken(token: String)

    fun getRefreshToken(): String?

    fun clearRefreshToken()
}

expect fun provideTokenStorage(): TokenStorage
