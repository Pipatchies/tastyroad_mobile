package fr.tastyroad.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import fr.tastyroad.ui.components.badges.PillBadge
import fr.tastyroad.ui.components.buttons.TastyRoadButton
import fr.tastyroad.ui.components.map.DetailMapView
import fr.tastyroad.ui.components.metrics.MetricsBox
import fr.tastyroad.ui.components.timeline.StepTimelineItem
import fr.tastyroad.ui.components.timeline.TimelineEndBadge
import fr.tastyroad.ui.viewmodels.ItineraryDetailViewModel
import kotlinx.serialization.json.Json

@Composable
fun ItineraryDetailScreen(
    itineraryId: Int,
    onBackClick: () -> Unit,
    onDeleteSuccess: () -> Unit = {},
    viewModel: ItineraryDetailViewModel = viewModel { ItineraryDetailViewModel() },
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(itineraryId) {
        viewModel.loadItinerary(itineraryId)
    }

    if (uiState.isDeleteSuccess) {
        LaunchedEffect(Unit) {
            onDeleteSuccess()
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary,
            )
        } else if (uiState.errorMessage != null && uiState.itinerary == null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = uiState.errorMessage ?: "Erreur",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                TastyRoadButton(text = "Retour", onClick = onBackClick)
            }
        } else {
            val itinerary = uiState.itinerary!!
            val scrollState = rememberScrollState()

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
            ) {
                // --- 1. HEADER IMAGE DE FOND ET BOUTON RETOUR ---
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(bottomStart = 80.dp, bottomEnd = 80.dp)),
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=800&auto=format&fit=crop",
                        contentDescription = itinerary.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )

                    Surface(
                        onClick = onBackClick,
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier =
                            Modifier
                                .padding(16.dp)
                                .size(36.dp)
                                .align(Alignment.TopStart),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }

                    Row(
                        modifier =
                            Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        repeat(4) { index ->
                            Box(
                                modifier =
                                    Modifier
                                        .size(if (index == 0) 10.dp else 8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == 0) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.primary.copy(
                                                    alpha = 0.4f,
                                                )
                                            },
                                        ),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- 2. TITRE & BADGES ---
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = itinerary.title,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        itinerary.type?.let { PillBadge(it) }
                        itinerary.zone?.let { PillBadge(it) }
                        itinerary.difficulty?.let { PillBadge(it) }
                        itinerary.diet?.let { PillBadge(it) }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- 3. DESCRIPTION CARD VERT FONCÉ ---
                    val factsText =
                        itinerary.facts?.ifBlank { null }
                            ?: "Une superbe balade gourmande à travers les plus beaux paysages du terroir."

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = factsText,
                            color = MaterialTheme.colorScheme.onSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(20.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 4. MÉTROLOGIE (km / heures / étapes) ---
                    val stepsList = itinerary.steps?.features?.map { it.properties } ?: emptyList()
                    val stepCount = stepsList.size

                    val hoursStr = extractHours(itinerary.duration)

                    MetricsBox(
                        metric1Value = "${itinerary.distance?.toInt() ?: 0}",
                        metric1Label = "km",
                        metric2Value = hoursStr,
                        metric2Label = if (hoursStr.toIntOrNull() != null && hoursStr.toInt() > 1) "heures" else "heure",
                        metric3Value = "$stepCount",
                        metric3Label = if (stepCount > 1) "étapes" else "étape",
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- 5. LA CARTE ET LE BOUTON "AJOUTER UNE ÉTAPE" ---
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(20.dp)),
                    ) {
                        val json = Json { ignoreUnknownKeys = true }
                        val pathStr = itinerary.path?.let { json.encodeToString(it) } ?: "{}"
                        val stepsStr = itinerary.steps?.let { json.encodeToString(it) } ?: "{}"

                        DetailMapView(
                            pathGeoJson = pathStr,
                            stepsGeoJson = stepsStr,
                            modifier = Modifier.fillMaxSize(),
                        )

                        TastyRoadButton(
                            text = "Ajouter une étape",
                            onClick = { /* Action optionnelle */ },
                            modifier =
                                Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 12.dp),
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    // --- 6. TIMELINE DES ÉTAPES ---
                    if (stepsList.isNotEmpty()) {
                        stepsList.forEachIndexed { index, step ->
                            StepTimelineItem(
                                stepNumber = index + 1,
                                step = step,
                            )
                        }

                        TimelineEndBadge()
                    } else {
                        Text(
                            text = "Aucune étape détaillée pour cet itinéraire.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    // --- 7. BOUTONS BAS DE PAGE (Commentaires, Spécialités, Supprimer) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    ) {
                        DetailActionPill(text = "Commentaires", onClick = {})
                        DetailActionPill(text = "Spécialités", onClick = {})
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    if (uiState.isDeleting) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.error)
                    } else {
                        TextButton(onClick = { viewModel.deleteItinerary() }) {
                            Text(
                                text = "Supprimer cet itinéraire",
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                fontSize = 14.sp,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailActionPill(
    text: String,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.background,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp),
        )
    }
}

private fun extractHours(durationStr: String?): String {
    if (durationStr.isNullOrBlank()) return "0"
    val parts = durationStr.split(":")
    return if (parts.isNotEmpty()) parts[0].toIntOrNull()?.toString() ?: "0" else "0"
}
