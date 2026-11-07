package fr.tastyroad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.tastyroad.domain.LoginRequest
import fr.tastyroad.domain.User
import fr.tastyroad.repository.AuthRepository
import fr.tastyroad.repository.AuthSession
import fr.tastyroad.ui.components.buttons.ButtonVariant
import fr.tastyroad.ui.components.buttons.TastyRoadButton
import fr.tastyroad.ui.theme.TastyRoadTheme
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    authRepository: AuthRepository = remember { AuthRepository() },
) {
    var isLoginMode by remember { mutableStateOf(true) } // true = Connexion, false = Inscription
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var ageText by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // État pour le Dialog "Mot de passe oublié"
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmailInput by remember { mutableStateOf("") }
    var resetSuccessMessage by remember { mutableStateOf<String?>(null) }
    var resetErrorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Validation stricte du mot de passe pour l'inscription
    fun validatePasswordSecurity(pass: String): String? {
        if (pass.length < 8) return "Le mot de passe doit contenir au moins 8 caractères."
        if (!pass.any { it.isUpperCase() }) return "Le mot de passe doit contenir au moins une lettre majuscule (A-Z)."
        if (!pass.any { it.isLowerCase() }) return "Le mot de passe doit contenir au moins une lettre minuscule (a-z)."
        if (!pass.any { it.isDigit() }) return "Le mot de passe doit contenir au moins un chiffre (0-9)."
        if (!pass.any { !it.isLetterOrDigit() }) return "Le mot de passe doit contenir au moins un caractère spécial (!@#$%...)."
        return null
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = if (isLoginMode) "Bienvenue sur TastyRoad" else "Créer un compte",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Login vs Register
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                TastyRoadButton(
                    text = "Connexion",
                    onClick = {
                        isLoginMode = true
                        errorMessage = null
                    },
                    variant = if (isLoginMode) ButtonVariant.PRIMARY else ButtonVariant.SECONDARY,
                    modifier = Modifier.weight(1f).padding(end = 2.dp),
                )
                TastyRoadButton(
                    text = "Inscription",
                    onClick = {
                        isLoginMode = false
                        errorMessage = null
                    },
                    variant = if (!isLoginMode) ButtonVariant.PRIMARY else ButtonVariant.SECONDARY,
                    modifier = Modifier.weight(1f).padding(start = 2.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Username (Login and register)
            FormInputLabel("Identifiant")
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Votre nom d'utilisateur", color = Color.Gray, fontStyle = FontStyle.Italic) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    ),
                singleLine = true,
            )

            // Email (register)
            if (!isLoginMode) {
                Spacer(modifier = Modifier.height(16.dp))
                FormInputLabel("Adresse e-mail")
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("exemple@domaine.com", color = Color.Gray, fontStyle = FontStyle.Italic) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        ),
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password (Login and register)
            FormInputLabel("Mot de passe")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Mot de passe", color = Color.Gray, fontStyle = FontStyle.Italic) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Afficher/Masquer mot de passe",
                            tint = Color.Gray,
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    ),
                singleLine = true,
            )

            // "Mot de passe oublié ?"
            if (isLoginMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Text(
                        text = "Mot de passe oublié ?",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier =
                            Modifier.clickable {
                                resetEmailInput = email
                                resetSuccessMessage = null
                                resetErrorMessage = null
                                showForgotPasswordDialog = true
                            },
                    )
                }
            }

            // Age (register)
            if (!isLoginMode) {
                Spacer(modifier = Modifier.height(16.dp))
                FormInputLabel("Âge")
                OutlinedTextField(
                    value = ageText,
                    onValueChange = { ageText = it },
                    placeholder = { Text("Votre âge (ex: 25)", color = Color.Gray, fontStyle = FontStyle.Italic) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        ),
                    singleLine = true,
                )
            }

            // Error
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Validation button
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                TastyRoadButton(
                    text = if (isLoginMode) "Se connecter" else "S'inscrire",
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            errorMessage = "Veuillez remplir votre identifiant et mot de passe."
                            return@TastyRoadButton
                        }

                        if (!isLoginMode) {
                            if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
                                errorMessage = "Format d'e-mail incorrect."
                                return@TastyRoadButton
                            }

                            val passwordSecurityError = validatePasswordSecurity(password)
                            if (passwordSecurityError != null) {
                                errorMessage = passwordSecurityError
                                return@TastyRoadButton
                            }
                        }

                        errorMessage = null
                        isLoading = true

                        coroutineScope.launch {
                            try {
                                if (isLoginMode) {
                                    // Connexion
                                    val tokenResp =
                                        authRepository.login(
                                            LoginRequest(username = username, password = password),
                                        )
                                    AuthSession.token = tokenResp.access
                                    AuthSession.currentUser =
                                        User(
                                            username = username,
                                            email = email.ifBlank { null },
                                            age = ageText.toIntOrNull(),
                                        )
                                    isLoading = false
                                    onAuthSuccess()
                                } else {
                                    // Inscription
                                    val createdUser =
                                        authRepository.register(
                                            User(
                                                username = username,
                                                email = email,
                                                age = ageText.toIntOrNull(),
                                            ),
                                        )
                                    AuthSession.currentUser = createdUser
                                    isLoading = false
                                    onAuthSuccess()
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage =
                                    if (isLoginMode) {
                                        "E-mail/Identifiant ou mot de passe incorrect."
                                    } else {
                                        "Cet identifiant ou cette adresse e-mail est déjà utilisé."
                                    }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    // Forgott password dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = {
                Text(
                    text = "Réinitialiser le mot de passe",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column {
                    Text(
                        text = "Saisissez votre e-mail enregistré pour recevoir un lien de réinitialisation.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = resetEmailInput,
                        onValueChange = { resetEmailInput = it },
                        placeholder = { Text("exemple@domaine.com") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )
                    if (resetErrorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = resetErrorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    if (resetSuccessMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = resetSuccessMessage ?: "",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!resetEmailInput.contains("@") || !resetEmailInput.contains(".")) {
                            resetSuccessMessage = null
                            resetErrorMessage = "Format d'e-mail incorrect."
                            return@TextButton
                        }

                        resetErrorMessage = null
                        coroutineScope.launch {
                            try {
                                authRepository.requestPasswordReset(resetEmailInput)
                                resetErrorMessage = null
                                resetSuccessMessage = "Un e-mail de réinitialisation a été envoyé à $resetEmailInput."
                            } catch (_: Exception) {
                                resetSuccessMessage = null
                                resetErrorMessage = "Adresse e-mail inconnue ou incorrecte."
                            }
                        }
                    },
                ) {
                    Text("Envoyer", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Fermer", color = Color.Gray)
                }
            },
        )
    }
}

@Composable
private fun FormInputLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
    )
}

@Preview
@Composable
fun AuthScreenPreview() {
    TastyRoadTheme {
        AuthScreen(onAuthSuccess = {})
    }
}
