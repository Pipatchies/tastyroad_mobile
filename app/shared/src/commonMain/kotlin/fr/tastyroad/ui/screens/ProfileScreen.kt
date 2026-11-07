package fr.tastyroad.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import fr.tastyroad.domain.MockData
import fr.tastyroad.repository.AuthSession
import fr.tastyroad.repository.UserRepository
import fr.tastyroad.ui.components.buttons.ButtonVariant
import fr.tastyroad.ui.components.buttons.TastyRoadButton
import fr.tastyroad.ui.components.cards.ItineraryCard
import fr.tastyroad.ui.components.cards.PhotoCard
import fr.tastyroad.ui.components.cards.ReviewCard
import fr.tastyroad.ui.components.header.Header
import fr.tastyroad.ui.components.metrics.MetricsBox
import fr.tastyroad.ui.theme.TastyRoadTheme
import kotlinx.coroutines.launch

private const val DEFAULT_AVATAR =
    "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?q=80&w=300&auto=format&fit=crop"

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit,
    onMapClick: () -> Unit = {},
    initialTabIndex: Int = 0,
    userRepository: UserRepository = remember { UserRepository() },
) {
    val user = AuthSession.currentUser
    var selectedTabIndex by remember { mutableIntStateOf(initialTabIndex) }
    val tabTitles = listOf("Mes itinéraires", "Mes favoris", "Mes avis", "Mes photos")

    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Header {
            Text(
                text = "Bonjour ${user?.username ?: "User1458"}",
                style = MaterialTheme.typography.displayLarge,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSecondary,
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF7EBD3),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(80.dp),
                ) {
                    AsyncImage(
                        model = user?.avatar ?: DEFAULT_AVATAR,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column {
                    Text(
                        text = user?.username ?: "Katsu BERTAUD",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = user?.age?.let { "$it ans" } ?: "1 an",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user?.email ?: "kastuber@gmail.com",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }

            HorizontalDivider(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                thickness = 1.dp,
            )

            MetricsBox(
                metric1Value = "1",
                metric1Label = "itinéraire",
                metric2Value = "40",
                metric2Label = "km",
                metric3Value = "8",
                metric3Label = "étapes",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        val isSelected = selectedTabIndex == index
                        val color =
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = color,
                            modifier =
                                Modifier
                                    .clickable { selectedTabIndex = index }
                                    .padding(bottom = 8.dp),
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    thickness = 1.dp,
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedTabIndex) {
                0 -> {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(MockData.sampleItineraries) { item ->
                            ItineraryCard(
                                item = item,
                                modifier = Modifier.width(320.dp),
                            )
                        }
                    }
                }
                1 -> {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(MockData.sampleItineraries.reversed()) { item ->
                            ItineraryCard(
                                item = item,
                                modifier = Modifier.width(320.dp),
                            )
                        }
                    }
                }
                2 -> {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(MockData.sampleReviews) { review ->
                            ReviewCard(
                                review = review,
                                modifier = Modifier.width(320.dp),
                            )
                        }
                    }
                }
                3 -> {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(MockData.samplePhotos) { photoUrl ->
                            PhotoCard(imageUrl = photoUrl)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TastyRoadButton(
                text = "Se déconnecter",
                onClick = {
                    AuthSession.logout()
                    onLogoutClick()
                },
                variant = ButtonVariant.SECONDARY,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            TastyRoadButton(
                text = "Supprimer mon compte (Droit à l'oubli RGPD)",
                onClick = {
                    showDeleteAccountDialog = true
                },
                variant = ButtonVariant.SECONDARY,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text(
                    text = "Suppression définitive du compte",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            },
            text = {
                Text(
                    text =
                        "Conformément au RGPD, cette action supprimera définitivement votre compte, " +
                            "l'intégralité de vos itinéraires et toutes vos coordonnées GPS en base de données. " +
                            "Cette action est irréversible.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val token = AuthSession.token
                        if (token != null) {
                            coroutineScope.launch {
                                try {
                                    userRepository.deleteMyAccount(token)
                                } catch (_: Exception) {
                                }
                                AuthSession.logout()
                                showDeleteAccountDialog = false
                                onLogoutClick()
                            }
                        } else {
                            AuthSession.logout()
                            showDeleteAccountDialog = false
                            onLogoutClick()
                        }
                    },
                ) {
                    Text(
                        text = "Supprimer définitivement",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Annuler", color = Color.Gray)
                }
            },
        )
    }
}

@Preview
@Composable
fun ProfileScreenTab1Preview() {
    TastyRoadTheme {
        ProfileScreen(onLogoutClick = {}, initialTabIndex = 0)
    }
}

@Preview
@Composable
fun ProfileScreenTab2Preview() {
    TastyRoadTheme {
        ProfileScreen(onLogoutClick = {}, initialTabIndex = 1)
    }
}

@Preview
@Composable
fun ProfileScreenTab3Preview() {
    TastyRoadTheme {
        ProfileScreen(onLogoutClick = {}, initialTabIndex = 2)
    }
}

@Preview
@Composable
fun ProfileScreenTab4Preview() {
    TastyRoadTheme {
        ProfileScreen(onLogoutClick = {}, initialTabIndex = 3)
    }
}
