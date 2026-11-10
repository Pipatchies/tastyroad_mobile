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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.tastyroad.ui.components.buttons.ButtonVariant
import fr.tastyroad.ui.components.buttons.TastyRoadButton
import fr.tastyroad.ui.theme.TastyRoadTheme
import fr.tastyroad.ui.viewmodels.AuthViewModel

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel { AuthViewModel() },
) {
    val uiState by viewModel.uiState.collectAsState()

    var isLoginMode by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var ageText by remember { mutableStateOf("") }

    var hasAcceptedGdpr by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

    var localErrorMessage by remember { mutableStateOf<String?>(null) }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmailInput by remember { mutableStateOf("") }
    var resetSuccessMessage by remember { mutableStateOf<String?>(null) }
    var resetErrorMessage by remember { mutableStateOf<String?>(null) }

    if (uiState.isSuccess) {
        onAuthSuccess()
    }

    fun validatePasswordSecurity(pass: String): String? {
        val isLengthValid = pass.length >= 8
        val hasUpper = pass.any { it.isUpperCase() }
        val hasLower = pass.any { it.isLowerCase() }
        val hasDigit = pass.any { it.isDigit() }
        val hasSpecial = pass.any { !it.isLetterOrDigit() }

        if (!isLengthValid || !hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            return "Le mot de passe doit comporter au moins 8 caractères, " +
                "dont au moins une majuscule, une minuscule, un chiffre et un caractère spécial (!@#$%...)"
        }
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
                        localErrorMessage = null
                    },
                    variant = if (isLoginMode) ButtonVariant.PRIMARY else ButtonVariant.SECONDARY,
                    modifier = Modifier.weight(1f).padding(end = 2.dp),
                )
                TastyRoadButton(
                    text = "Inscription",
                    onClick = {
                        isLoginMode = false
                        localErrorMessage = null
                    },
                    variant = if (!isLoginMode) ButtonVariant.PRIMARY else ButtonVariant.SECONDARY,
                    modifier = Modifier.weight(1f).padding(start = 2.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FormInputLabel("Identifiant")
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Votre nom d'utilisateur", color = Color.Gray, fontStyle = FontStyle.Italic) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
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

            if (!isLoginMode) {
                Spacer(modifier = Modifier.height(16.dp))
                FormInputLabel("Adresse e-mail")
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("exemple@domaine.com", color = Color.Gray, fontStyle = FontStyle.Italic) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
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

            FormInputLabel("Mot de passe")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Mot de passe", color = Color.Gray, fontStyle = FontStyle.Italic) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector =
                                if (isPasswordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                            contentDescription = "Afficher/Masquer mot de passe",
                            tint = Color.Gray,
                        )
                    }
                },
                visualTransformation =
                    if (isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
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

                Spacer(modifier = Modifier.height(16.dp))

                val linkStyle =
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold,
                    )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = hasAcceptedGdpr,
                        onCheckedChange = { hasAcceptedGdpr = it },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text =
                            buildAnnotatedString {
                                append("J'accepte la ")
                                withStyle(style = linkStyle) {
                                    append("politique de confidentialité")
                                }
                                append(" et le traitement de mes données personnelles (RGPD).")
                            },
                        style = MaterialTheme.typography.bodySmall,
                        modifier =
                            Modifier.clickable {
                                showPrivacyPolicyDialog = true
                            },
                    )
                }
            }

            val displayError = localErrorMessage ?: uiState.errorMessage
            if (displayError != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = displayError,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                TastyRoadButton(
                    text = if (isLoginMode) "Se connecter" else "S'inscrire",
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            localErrorMessage = "Veuillez remplir votre identifiant et mot de passe."
                            return@TastyRoadButton
                        }

                        if (!isLoginMode) {
                            if (!hasAcceptedGdpr) {
                                localErrorMessage =
                                    "Vous devez accepter la politique de confidentialité (RGPD) pour créer un compte."
                                return@TastyRoadButton
                            }

                            if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
                                localErrorMessage = "Format d'e-mail incorrect."
                                return@TastyRoadButton
                            }

                            val passwordSecurityError = validatePasswordSecurity(password)
                            if (passwordSecurityError != null) {
                                localErrorMessage = passwordSecurityError
                                return@TastyRoadButton
                            }
                        }

                        localErrorMessage = null

                        if (isLoginMode) {
                            viewModel.login(username, password)
                        } else {
                            viewModel.register(username, email, ageText.toIntOrNull(), password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

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
                        viewModel.requestPasswordReset(resetEmailInput) {
                            resetSuccessMessage = "Un e-mail de réinitialisation a été envoyé à $resetEmailInput."
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

    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = {
                Text(
                    text = "Politique de Confidentialité (RGPD)",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("1. Données collectées :", fontWeight = FontWeight.Bold)
                    Text(
                        "Nous collectons uniquement votre nom d'utilisateur, adresse e-mail, âge " +
                            "et les coordonnées GPS des itinéraires et étapes que vous créez.",
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("2. Finalités du traitement :", fontWeight = FontWeight.Bold)
                    Text(
                        "Ces données sont utilisées exclusivement pour vous fournir le service " +
                            "de cartographie culinaire TastyRoad et vous permettre de partager vos trajets.",
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("3. Droit à l'oubli (Suppression) :", fontWeight = FontWeight.Bold)
                    Text(
                        "Conformément au RGPD, vous disposez d'un droit permanent de suppression " +
                            "définitive de votre compte et de toutes vos données depuis votre écran Profil.",
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyPolicyDialog = false }) {
                    Text("Compris", color = MaterialTheme.colorScheme.primary)
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
