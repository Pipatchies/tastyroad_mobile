package fr.tastyroad.repository

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual class TokenStorage(private val context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_tastyroad_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    actual fun saveToken(token: String) {
        sharedPreferences.edit().putString("jwt_token", token).apply()
    }

    actual fun getToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    actual fun clearToken() {
        sharedPreferences.edit().remove("jwt_token").apply()
    }

    actual fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString("jwt_refresh_token", token).apply()
    }

    actual fun getRefreshToken(): String? {
        return sharedPreferences.getString("jwt_refresh_token", null)
    }

    actual fun clearRefreshToken() {
        sharedPreferences.edit().remove("jwt_refresh_token").apply()
    }
}

private var appContext: Context? = null

fun initTokenStorage(context: Context) {
    appContext = context.applicationContext
}

actual fun provideTokenStorage(): TokenStorage {
    val ctx =
        appContext
            ?: throw IllegalStateException(
                "TokenStorage not initialized. Call initTokenStorage(context) in MainActivity.",
            )
    return TokenStorage(ctx)
}
