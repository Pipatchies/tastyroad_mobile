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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import fr.tastyroad.network.CloudinaryUploader
import fr.tastyroad.ui.components.buttons.ButtonVariant
import fr.tastyroad.ui.components.buttons.TastyRoadButton
import fr.tastyroad.ui.components.map.CreationMapView
import fr.tastyroad.ui.components.metrics.MetricsBox
import fr.tastyroad.ui.components.picker.ImagePickerButton
import fr.tastyroad.ui.viewmodels.CreateItineraryViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CreateItineraryScreen(
    onCloseClick: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: CreateItineraryViewModel = viewModel { CreateItineraryViewModel() },
) {
    val uiState by viewModel.uiState.collectAsState()

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
    var pathGeoJson by remember { mutableStateOf("{}") }
    var stepsGeoJson by remember { mutableStateOf("{}") }

    val coroutineScope = rememberCoroutineScope()

    var showFoodPlaceSheet by remember { mutableStateOf(false) }
    var activeStepForFoodPlace by remember { mutableStateOf<Int?>(null) }
    var newFoodPlaceName by remember { mutableStateOf("") }
    var newFoodPlaceType by remember { mutableStateOf("") }
    var newFoodPlaceDescription by remember { mutableStateOf("") }
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)

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

    val mapContent =
        remember {
            androidx.compose.runtime.movableContentOf<String, Modifier> { type, modifier ->
                CreationMapView(
                    transportType = type,
                    onMetricsUpdated = { dist, dur, steps, path, stepsJson ->
                        distanceKm = dist
                        durationMinutes = dur
                        stepCount = steps
                        pathGeoJson = path

                        viewModel.updateStepsFromGeoJson(stepsJson)

                        if (steps >= 2) showStepError = false
                    },
                    modifier = modifier,
                )
            }
        }

    if (uiState.isSuccess) {
        onSaveSuccess()
    }

    if (!isFormExpanded) {
        // --- PHASE 1 : MODE TRACÉ SUR CARTE ---
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        ) {
            mapContent(selectedType, Modifier.fillMaxSize())

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
                mapContent(selectedType, Modifier.fillMaxSize())

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

                FilterChipGroup("Type de transport", types, selectedType) { selectedType = it }
                FilterChipGroup("Région / Zone", zones, selectedZone) { selectedZone = it }
                FilterChipGroup("Difficulté", difficulties, selectedDifficulty) { selectedDifficulty = it }
                FilterChipGroup("Spécialité Gastronomique", gastronomies, selectedGastronomy) { selectedGastronomy = it }

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

                Spacer(modifier = Modifier.height(32.dp))

                // --- SECTION ÉTAPES ---
                Text(
                    text = "Vos Étapes",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                if (uiState.stepsDrafts.isEmpty()) {
                    Text(
                        text = "Aucune étape définie. Placez au moins deux points sur la carte.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                } else {
                    uiState.stepsDrafts.forEach { stepDraft ->
                        key(stepDraft.order) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF7EBD3),
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    val defaultTitle = if (stepDraft.order == 0) "Départ" else "Étape ${stepDraft.order}"
                                    Text(
                                        text = defaultTitle,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.padding(bottom = 8.dp),
                                    )

                                    OutlinedTextField(
                                        value = stepDraft.name,
                                        onValueChange = { viewModel.updateStepName(stepDraft.order, it) },
                                        label = { Text("Titre de l'étape (Optionnel)", color = Color.Gray, fontStyle = FontStyle.Italic) },
                                        placeholder = { Text(defaultTitle) },
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors =
                                            TextFieldDefaults.colors(
                                                focusedContainerColor = Color.White,
                                                unfocusedContainerColor = Color.White,
                                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                                unfocusedIndicatorColor = Color.Transparent,
                                            ),
                                    )

                                    OutlinedTextField(
                                        value = stepDraft.description,
                                        onValueChange = { viewModel.updateStepDescription(stepDraft.order, it) },
                                        label = {
                                            Text(
                                                "Description de l'étape (Optionnel)",
                                                color = Color.Gray,
                                                fontStyle = FontStyle.Italic,
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth().height(100.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors =
                                            TextFieldDefaults.colors(
                                                focusedContainerColor = Color.White,
                                                unfocusedContainerColor = Color.White,
                                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                                unfocusedIndicatorColor = Color.Transparent,
                                            ),
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    ExposedDropdownMenuBox(
                                        expanded = stepDraft.isDropdownExpanded,
                                        onExpandedChange = { viewModel.setStepDropdownExpanded(stepDraft.order, it) },
                                    ) {
                                        OutlinedTextField(
                                            value = stepDraft.foodPlaceSearchQuery,
                                            onValueChange = {
                                                viewModel.updateStepFoodPlaceSearchQuery(stepDraft.order, it)
                                            },
                                            label = {
                                                Text(
                                                    "Lieu gourmand associé (Optionnel)",
                                                    color = Color.Gray,
                                                    fontStyle = FontStyle.Italic,
                                                )
                                            },
                                            placeholder = { Text("Rechercher ou créer un lieu...") },
                                            modifier =
                                                Modifier.fillMaxWidth().menuAnchor(
                                                    androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryEditable,
                                                    true,
                                                ),
                                            shape = RoundedCornerShape(12.dp),
                                            singleLine = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = stepDraft.isDropdownExpanded,
                                                )
                                            },
                                            colors =
                                                ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                                    focusedContainerColor = Color.White,
                                                    unfocusedContainerColor = Color.White,
                                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                                ),
                                        )

                                        if (stepDraft.isDropdownExpanded && stepDraft.foodPlaceSearchQuery.isNotBlank()) {
                                            val filteredLocalPlaces =
                                                try {
                                                    uiState.foodPlaces.filter {
                                                        it.name.contains(stepDraft.foodPlaceSearchQuery, ignoreCase = true)
                                                    }
                                                } catch (e: Exception) {
                                                    emptyList()
                                                }

                                            val filteredOsmPlaces =
                                                try {
                                                    uiState.osmFoodPlaces.filter { osmPlace ->
                                                        uiState.foodPlaces.none { localPlace ->
                                                            localPlace.name.equals(osmPlace.name, ignoreCase = true)
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    emptyList()
                                                }

                                            ExposedDropdownMenu(
                                                expanded = stepDraft.isDropdownExpanded,
                                                onDismissRequest = { viewModel.setStepDropdownExpanded(stepDraft.order, false) },
                                            ) {
                                                val query = stepDraft.foodPlaceSearchQuery
                                                if (filteredLocalPlaces.isEmpty() && filteredOsmPlaces.isEmpty()) {
                                                    DropdownMenuItem(
                                                        text = {
                                                            if (stepDraft.isCreatingFoodPlace) {
                                                                CircularProgressIndicator(
                                                                    modifier = Modifier.size(24.dp),
                                                                    strokeWidth = 2.dp,
                                                                )
                                                            } else {
                                                                Text(
                                                                    text = "Aucun résultat. Créer manuellement : \"$query\"",
                                                                    color = MaterialTheme.colorScheme.primary,
                                                                    fontWeight = FontWeight.Bold,
                                                                )
                                                            }
                                                        },
                                                        onClick = {
                                                            if (!stepDraft.isCreatingFoodPlace) {
                                                                newFoodPlaceName = query
                                                                newFoodPlaceType = ""
                                                                newFoodPlaceDescription = ""
                                                                activeStepForFoodPlace = stepDraft.order
                                                                showFoodPlaceSheet = true
                                                                viewModel.setStepDropdownExpanded(stepDraft.order, false)
                                                            }
                                                        },
                                                    )
                                                } else {
                                                    // 1. Afficher les résultats locaux (déjà en base de données TastyRoad)
                                                    if (filteredLocalPlaces.isNotEmpty()) {
                                                        DropdownMenuItem(
                                                            text = {
                                                                Text(
                                                                    text = "Lieux enregistrés",
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color.Gray,
                                                                )
                                                            },
                                                            onClick = {},
                                                        )
                                                        filteredLocalPlaces.forEach { place ->
                                                            DropdownMenuItem(
                                                                text = { Text("📍 ${place.name}") },
                                                                onClick = {
                                                                    viewModel.selectFoodPlaceForStep(
                                                                        order = stepDraft.order,
                                                                        foodPlaceId = place.id,
                                                                        foodPlaceName = place.name,
                                                                    )
                                                                },
                                                            )
                                                        }
                                                    }

                                                    // 2. Afficher les résultats du monde entier (OpenStreetMap)
                                                    if (filteredOsmPlaces.isNotEmpty()) {
                                                        DropdownMenuItem(
                                                            text = {
                                                                Text(
                                                                    text = "Résultats OpenStreetMap",
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = Color.Gray,
                                                                )
                                                            },
                                                            onClick = {},
                                                        )
                                                        filteredOsmPlaces.forEach { place ->
                                                            DropdownMenuItem(
                                                                text = {
                                                                    Column {
                                                                        Text("🌍 ${place.name}")
                                                                        Text(
                                                                            text = place.type ?: "Lieu",
                                                                            style = MaterialTheme.typography.bodySmall,
                                                                            color = Color.Gray,
                                                                        )
                                                                    }
                                                                },
                                                                onClick = {
                                                                    if (!stepDraft.isCreatingFoodPlace) {
                                                                        viewModel.createAndAssignFoodPlace(
                                                                            order = stepDraft.order,
                                                                            foodPlaceName = place.name,
                                                                            foodPlaceType = place.type ?: "",
                                                                            foodPlaceDesc = place.description ?: "",
                                                                            locationGeometry = place.location,
                                                                        )
                                                                        viewModel.setStepDropdownExpanded(stepDraft.order, false)
                                                                    }
                                                                },
                                                            )
                                                        }
                                                    }

                                                    // 3. Option manuelle si le nom ne correspond exactement à rien
                                                    val noLocalExact =
                                                        filteredLocalPlaces.none {
                                                            it.name.equals(query, ignoreCase = true)
                                                        }
                                                    val noOsmExact =
                                                        filteredOsmPlaces.none {
                                                            it.name.equals(query, ignoreCase = true)
                                                        }
                                                    if (noLocalExact && noOsmExact) {
                                                        DropdownMenuItem(
                                                            text = {
                                                                if (stepDraft.isCreatingFoodPlace) {
                                                                    CircularProgressIndicator(
                                                                        modifier = Modifier.size(24.dp),
                                                                        strokeWidth = 2.dp,
                                                                    )
                                                                } else {
                                                                    Text(
                                                                        text = "Créer le lieu manuellement : \"$query\"",
                                                                        color = MaterialTheme.colorScheme.primary,
                                                                    )
                                                                }
                                                            },
                                                            onClick = {
                                                                if (!stepDraft.isCreatingFoodPlace) {
                                                                    newFoodPlaceName = query
                                                                    newFoodPlaceType = ""
                                                                    newFoodPlaceDescription = ""
                                                                    activeStepForFoodPlace = stepDraft.order
                                                                    showFoodPlaceSheet = true
                                                                    viewModel.setStepDropdownExpanded(stepDraft.order, false)
                                                                }
                                                            },
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    if (stepDraft.pictureUrl.isNotBlank()) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(140.dp)
                                                    .clip(RoundedCornerShape(12.dp)),
                                        ) {
                                            AsyncImage(
                                                model = stepDraft.pictureUrl,
                                                contentDescription = "Photo d'étape",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize(),
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }

                                    var isUploadingStepPhoto by remember { mutableStateOf(false) }

                                    ImagePickerButton(
                                        buttonText =
                                            if (isUploadingStepPhoto) {
                                                "Téléversement..."
                                            } else if (stepDraft.pictureUrl.isBlank()) {
                                                "Ajouter une photo de l'étape"
                                            } else {
                                                "Changer la photo de l'étape"
                                            },
                                        onImagePicked = { bytes ->
                                            coroutineScope.launch {
                                                isUploadingStepPhoto = true
                                                val uploadedUrl = CloudinaryUploader.uploadImage(bytes)
                                                if (uploadedUrl != null) {
                                                    viewModel.updateStepPicture(stepDraft.order, uploadedUrl)
                                                }
                                                isUploadingStepPhoto = false
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                            }
                        }
                    }
                }

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
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
                        text = "Enregistrer l'itinéraire",
                        onClick = {
                            showFormErrors = true
                            if (isFormValid) {
                                viewModel.createItinerary(
                                    title = title,
                                    type = selectedType,
                                    zone = selectedZone,
                                    difficulty = selectedDifficulty,
                                    gastronomy = selectedGastronomy,
                                    description = description,
                                    distanceKm = distanceKm,
                                    durationMinutes = durationMinutes,
                                    pathGeoJson = pathGeoJson,
                                    stepsGeoJson = stepsGeoJson,
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showFoodPlaceSheet && activeStepForFoodPlace != null) {
        androidx.compose.material3.ModalBottomSheet(
            onDismissRequest = { showFoodPlaceSheet = false },
            sheetState = sheetState,
            containerColor = Color.White,
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("Créer un nouveau lieu gourmand", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = newFoodPlaceName,
                    onValueChange = { newFoodPlaceName = it },
                    label = { Text("Nom du lieu") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = newFoodPlaceType,
                    onValueChange = { newFoodPlaceType = it },
                    label = { Text("Type (ex: Restaurant, Fromagerie...)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = newFoodPlaceDescription,
                    onValueChange = { newFoodPlaceDescription = it },
                    label = { Text("Description") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                )
                Spacer(modifier = Modifier.height(24.dp))
                TastyRoadButton(
                    text = "Créer et associer",
                    onClick = {
                        viewModel.createAndAssignFoodPlace(
                            order = activeStepForFoodPlace!!,
                            foodPlaceName = newFoodPlaceName,
                            foodPlaceType = newFoodPlaceType,
                            foodPlaceDesc = newFoodPlaceDescription,
                        )
                        showFoodPlaceSheet = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(32.dp))
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

@Composable
fun FilterChipGroup(
    label: String,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
) {
    FormSectionLabel(label)
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items.forEach { item ->
            val isSelected = selectedItem == item
            FilterChip(
                selected = isSelected,
                onClick = { onItemSelected(item) },
                label = { Text(item) },
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
}
