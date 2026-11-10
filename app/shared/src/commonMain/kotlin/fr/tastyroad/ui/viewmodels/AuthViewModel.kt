package fr.tastyroad.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.tastyroad.domain.LoginRequest
import fr.tastyroad.domain.User
import fr.tastyroad.repository.AuthRepository
import fr.tastyroad.session.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(
        username: String,
        password: String,
    ) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Veuillez remplir votre identifiant et mot de passe.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val tokenResp = authRepository.login(LoginRequest(username = username, password = password))
                AuthSession.token = tokenResp.access
                AuthSession.refreshToken = tokenResp.refresh
                
                // On récupère le "vrai" profil avec l'ID depuis le serveur
                try {
                    AuthSession.currentUser = authRepository.getMyProfile()
                } catch (_: Exception) {
                    // Fallback
                    AuthSession.currentUser = User(username = username)
                }

                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "E-mail/Identifiant ou mot de passe incorrect.",
                    )
            }
        }
    }

    fun register(
        username: String,
        email: String,
        age: Int?,
        password: String,
    ) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Veuillez remplir votre identifiant et mot de passe.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val createdUser =
                    authRepository.register(
                        fr.tastyroad.domain.RegisterRequest(
                            username = username,
                            email = email,
                            age = age,
                            password = password,
                        ),
                    )
                val tokenResp = authRepository.login(LoginRequest(username = username, password = password))
                AuthSession.token = tokenResp.access
                AuthSession.refreshToken = tokenResp.refresh
                
                try {
                    AuthSession.currentUser = authRepository.getMyProfile()
                } catch (_: Exception) {
                    AuthSession.currentUser = createdUser
                }

                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Cet identifiant ou cette adresse e-mail est déjà utilisé.",
                    )
            }
        }
    }

    fun requestPasswordReset(
        email: String,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            try {
                authRepository.requestPasswordReset(email)
                onSuccess()
            } catch (_: Exception) {
                onSuccess()
            }
        }
    }
}
