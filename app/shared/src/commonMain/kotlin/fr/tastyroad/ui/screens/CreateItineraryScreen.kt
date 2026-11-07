package fr.tastyroad.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.tastyroad.ui.components.buttons.ButtonVariant
import fr.tastyroad.ui.components.buttons.TastyRoadButton
import fr.tastyroad.ui.components.map.CreationMapView
import fr.tastyroad.ui.components.metrics.MetricsBox

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateItineraryScreen(
    onCloseClick: () -> Unit,
    onSaveSuccess: () -> Unit,
) {
    var isFormExpanded by remember { mutableStateOf(false) }
    var showStepError by remember { mutableStateOf(false) }
    var showFormErrors by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var foodPlaceSearchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Rando") }
    var selectedZone by remember { mutableStateOf("Occitanie, France") }
    var selectedDifficulty by remember { mutableStateOf("Facile") }
    var selectedGastronomy by remember { mutableStateOf("Viande") }
    var description by remember { mutableStateOf("") }

    var distanceKm by remember { mutableDoubleStateOf(0.0) }
    var durationMinutes by remember { mutableIntStateOf(0) }
    var stepCount by remember { mutableIntStateOf(0) }

    val isStepsValid = stepCount >= 2
    val isTitleValid = title.isNotBlank()
    val isDescriptionValid = description.isNotBlank()
    val isFormValid = isStepsValid && isTitleValid && isDescriptionValid

    val formattedDuration =
        run {
            val hours = durationMinutes / 60
            val mins = durationMinutes % 60
            if (hours > 0) "${hours}h ${mins.toString().padStart(2, '0')}" else "$mins min"
        }

    val types = listOf("Rando", "Vélo", "Moto")
    val zones = listOf("Occitanie, France", "PACA, France", "Nord, France", "Île-de-France", "Bretagne")
    val difficulties = listOf("Facile", "Moyen", "Difficile")
    val gastronomies = listOf("Viande", "Poisson", "Fromage", "Vignoble", "Végétarien")

    if (!isFormExpanded) {
        // --- PHASE 1 : MODE TRACÉ SUR CARTE  ---
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        ) {
            CreationMapView(
                transportType = selectedType,
                onMetricsUpdated = { dist, dur, steps ->
                    distanceKm = dist
                    durationMinutes = dur
                    stepCount = steps
                    if (steps >= 2) showStepError = false
                },
                modifier = Modifier.fillMaxSize(),
            )

            Surface(
                onClick = onCloseClick,
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(1.dp, Color.Black),
                modifier =
                    Modifier
                        .padding(16.dp)
                        .size(36.dp)
                        .align(Alignment.TopStart),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            TextField(
                value = foodPlaceSearchQuery,
                onValueChange = { foodPlaceSearchQuery = it },
                placeholder = { Text("Chercher un lieu gourmand (ex: Fromagerie)...", style = MaterialTheme.typography.bodySmall) },
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Rechercher", tint = Color.Gray) },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 64.dp, end = 16.dp, top = 12.dp)
                        .align(Alignment.TopCenter),
                shape = RoundedCornerShape(16.dp),
                colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                singleLine = true,
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MetricsBox(
                    metric1Value = "${(distanceKm * 100).toInt() / 100.0}",
                    metric1Label = "km",
                    metric2Value = formattedDuration,
                    metric2Label = "durée",
                    metric3Value = "$stepCount",
                    metric3Label = "étapes",
                )

                if (showStepError && !isStepsValid) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ Veuillez placer au moins 2 étapes sur la carte (un point de départ et une arrivée)",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TastyRoadButton(
                    text = "Valider le tracé et remplir les infos",
                    onClick = {
                        if (isStepsValid) {
                            showStepError = false
                            isFormExpanded = true
                        } else {
                            showStepError = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    } else {
        // --- PHASE 2 : MODE FORMULAIRE ---
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
            ) {
                CreationMapView(
                    transportType = selectedType,
                    onMetricsUpdated = { dist, dur, steps ->
                        distanceKm = dist
                        durationMinutes = dur
                        stepCount = steps
                    },
                    modifier = Modifier.fillMaxSize(),
                )

                Surface(
                    onClick = onCloseClick,
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(1.dp, Color.Black),
                    modifier =
                        Modifier
                            .padding(16.dp)
                            .size(36.dp)
                            .align(Alignment.TopStart),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
            ) {
                TastyRoadButton(
                    text = "← Modifier le tracé sur la carte",
                    onClick = { isFormExpanded = false },
                    variant = ButtonVariant.SECONDARY,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Titre de l'itinéraire",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    isError = showFormErrors && !isTitleValid,
                    placeholder = { Text(text = "Ex: La Route des Fromages d'Auvergne", color = Color.Gray, fontStyle = FontStyle.Italic) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            errorContainerColor = Color.White,
                        ),
                    singleLine = true,
                )

                if (showFormErrors && !isTitleValid) {
                    Text(
                        text = "Le titre de l'itinéraire est obligatoire",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                MetricsBox(
                    metric1Value = "${(distanceKm * 100).toInt() / 100.0}",
                    metric1Label = "km",
                    metric2Value = formattedDuration,
                    metric2Label = "durée",
                    metric3Value = "$stepCount",
                    metric3Label = "étapes",
                )

                if (showFormErrors && !isStepsValid) {
                    Text(
                        text = "⚠️ L'itinéraire doit comporter au moins 2 étapes (placez des points sur la carte)",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                FormSectionLabel("Type de transport")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    types.forEach { type ->
                        val isSelected = selectedType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedType = type },
                            label = { Text(type) },
                            shape = RoundedCornerShape(16.dp),
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = Color(0xFFF7EBD3),
                                    labelColor = MaterialTheme.colorScheme.onBackground,
                                ),
                            border =
                                FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = MaterialTheme.colorScheme.primary,
                                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                                    borderWidth = 1.dp,
                                ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                FormSectionLabel("Région / Zone")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    zones.forEach { zone ->
                        val isSelected = selectedZone == zone
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedZone = zone },
                            label = { Text(zone) },
                            shape = RoundedCornerShape(16.dp),
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = Color(0xFFF7EBD3),
                                    labelColor = MaterialTheme.colorScheme.onBackground,
                                ),
                            border =
                                FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = MaterialTheme.colorScheme.primary,
                                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                                    borderWidth = 1.dp,
                                ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                FormSectionLabel("Difficulté")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    difficulties.forEach { diff ->
                        val isSelected = selectedDifficulty == diff
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedDifficulty = diff },
                            label = { Text(diff) },
                            shape = RoundedCornerShape(16.dp),
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = Color(0xFFF7EBD3),
                                    labelColor = MaterialTheme.colorScheme.onBackground,
                                ),
                            border =
                                FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = MaterialTheme.colorScheme.primary,
                                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                                    borderWidth = 1.dp,
                                ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                FormSectionLabel("Spécialité Gastronomique")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    gastronomies.forEach { gastro ->
                        val isSelected = selectedGastronomy == gastro
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedGastronomy = gastro },
                            label = { Text(gastro) },
                            shape = RoundedCornerShape(16.dp),
                            colors =
                                FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    containerColor = Color(0xFFF7EBD3),
                                    labelColor = MaterialTheme.colorScheme.onBackground,
                                ),
                            border =
                                FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = MaterialTheme.colorScheme.primary,
                                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                                    borderWidth = 1.dp,
                                ),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                FormSectionLabel("Description")
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    isError = showFormErrors && !isDescriptionValid,
                    placeholder = {
                        Text(
                            text = "Décrivez les lieux incontournables et spécialités...",
                            color = Color.Gray,
                            fontStyle = FontStyle.Italic,
                        )
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            errorContainerColor = Color.White,
                        ),
                )

                if (showFormErrors && !isDescriptionValid) {
                    Text(
                        text = "La description est obligatoire",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp),
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                TastyRoadButton(
                    text = "Enregistrer l'itinéraire",
                    onClick = {
                        showFormErrors = true
                        if (isFormValid) {
                            onSaveSuccess()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun FormSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 6.dp),
    )
}
