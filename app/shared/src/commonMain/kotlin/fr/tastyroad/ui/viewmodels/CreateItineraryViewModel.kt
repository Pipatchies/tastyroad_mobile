package fr.tastyroad.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.tastyroad.domain.FoodPlace
import fr.tastyroad.domain.GeoJsonFeatureCollection
import fr.tastyroad.domain.Itinerary
import fr.tastyroad.domain.Step
import fr.tastyroad.repository.FoodPlaceRepository
import fr.tastyroad.repository.ItineraryRepository
import fr.tastyroad.session.AuthSession
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Serializable
data class JsStepProperties(
    val name: String,
    @SerialName("step_order") val stepOrder: Int,
)

data class StepDraft(
    val order: Int,
    val defaultName: String,
    val name: String = "",
    val description: String = "",
    val foodPlaceId: Int? = null,
    val foodPlaceSearchQuery: String = "",
    val isDropdownExpanded: Boolean = false,
    val isCreatingFoodPlace: Boolean = false,
    val geometry: JsonObject? = null,
)

data class CreateItineraryUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val foodPlaces: List<FoodPlace> = emptyList(),
    val osmFoodPlaces: List<FoodPlace> = emptyList(),
    val errorMessage: String? = null,
    val stepsDrafts: List<StepDraft> = emptyList(),
)

class CreateItineraryViewModel(
    private val itineraryRepository: ItineraryRepository = ItineraryRepository(),
    private val foodPlaceRepository: FoodPlaceRepository = FoodPlaceRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateItineraryUiState())
    val uiState: StateFlow<CreateItineraryUiState> = _uiState.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        fetchFoodPlaces()
    }

    private fun fetchFoodPlaces() {
        viewModelScope.launch {
            try {
                val places = foodPlaceRepository.getFoodPlaces()
                _uiState.value = _uiState.value.copy(foodPlaces = places)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateStepsFromGeoJson(stepsGeoJson: String) {
        if (stepsGeoJson == "{}" || stepsGeoJson.isBlank()) return
        try {
            val stepsCollection = json.decodeFromString<GeoJsonFeatureCollection<JsStepProperties>>(stepsGeoJson)
            val drafts =
                stepsCollection.features.mapIndexed { index, feature ->
                    val defaultName = feature.properties.name
                    val oldDraft = _uiState.value.stepsDrafts.find { it.order == index }

                    StepDraft(
                        order = index,
                        defaultName = defaultName,
                        name = oldDraft?.name ?: "",
                        description = oldDraft?.description ?: "",
                        foodPlaceId = oldDraft?.foodPlaceId,
                        geometry = feature.geometry,
                    )
                }
            _uiState.value = _uiState.value.copy(stepsDrafts = drafts)
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.value = _uiState.value.copy(stepsDrafts = emptyList())
        }
    }

    fun updateStepName(
        order: Int,
        newName: String,
    ) {
        val updatedDrafts =
            _uiState.value.stepsDrafts.map {
                if (it.order == order) it.copy(name = newName) else it
            }
        _uiState.value = _uiState.value.copy(stepsDrafts = updatedDrafts)
    }

    fun updateStepDescription(
        order: Int,
        newDesc: String,
    ) {
        val updatedDrafts =
            _uiState.value.stepsDrafts.map {
                if (it.order == order) it.copy(description = newDesc) else it
            }
        _uiState.value = _uiState.value.copy(stepsDrafts = updatedDrafts)
    }

    fun selectFoodPlaceForStep(
        order: Int,
        foodPlaceId: Int?,
        foodPlaceName: String,
    ) {
        searchJob?.cancel()
        val updatedDrafts =
            _uiState.value.stepsDrafts.map {
                if (it.order == order) {
                    it.copy(
                        foodPlaceId = foodPlaceId,
                        foodPlaceSearchQuery = foodPlaceName,
                        isDropdownExpanded = false,
                    )
                } else {
                    it
                }
            }
        _uiState.value = _uiState.value.copy(stepsDrafts = updatedDrafts, osmFoodPlaces = emptyList())
    }

    fun setStepDropdownExpanded(
        order: Int,
        expanded: Boolean,
    ) {
        val updatedDrafts =
            _uiState.value.stepsDrafts.map {
                if (it.order == order) it.copy(isDropdownExpanded = expanded) else it
            }
        _uiState.value = _uiState.value.copy(stepsDrafts = updatedDrafts)
    }

    private var searchJob: kotlinx.coroutines.Job? = null

    fun updateStepFoodPlaceSearchQuery(
        order: Int,
        query: String,
    ) {
        val updatedDrafts =
            _uiState.value.stepsDrafts.map {
                if (it.order == order) it.copy(foodPlaceSearchQuery = query, isDropdownExpanded = query.isNotBlank()) else it
            }
        _uiState.value = _uiState.value.copy(stepsDrafts = updatedDrafts)

        searchJob?.cancel()
        if (query.length >= 2) {
            searchJob =
                viewModelScope.launch {
                    kotlinx.coroutines.delay(250)
                    try {
                        val osmResults = foodPlaceRepository.searchFoodPlacesOSM(query)
                        _uiState.value = _uiState.value.copy(osmFoodPlaces = osmResults)
                    } catch (_: Exception) {
                    }
                }
        } else {
            _uiState.value = _uiState.value.copy(osmFoodPlaces = emptyList())
        }
    }

    fun createAndAssignFoodPlace(
        order: Int,
        foodPlaceName: String,
        foodPlaceType: String,
        foodPlaceDesc: String,
        locationGeometry: JsonObject? = null,
    ) {
        val token = AuthSession.token ?: return
        val draft = _uiState.value.stepsDrafts.find { it.order == order } ?: return

        viewModelScope.launch {
            val loadingDrafts =
                _uiState.value.stepsDrafts.map {
                    if (it.order == order) it.copy(isCreatingFoodPlace = true) else it
                }
            _uiState.value = _uiState.value.copy(stepsDrafts = loadingDrafts)

            try {
                val newPlace =
                    FoodPlace(
                        name = foodPlaceName,
                        type = foodPlaceType.ifBlank { null },
                        description = foodPlaceDesc.ifBlank { null },
                        location = locationGeometry ?: draft.geometry,
                    )
                val createdPlace = foodPlaceRepository.createFoodPlace(token, newPlace)

                val updatedPlaces = _uiState.value.foodPlaces + createdPlace
                val finishedDrafts =
                    _uiState.value.stepsDrafts.map {
                        if (it.order == order) {
                            it.copy(
                                foodPlaceId = createdPlace.id,
                                foodPlaceSearchQuery = createdPlace.name,
                                isCreatingFoodPlace = false,
                                isDropdownExpanded = false,
                            )
                        } else {
                            it
                        }
                    }

                _uiState.value =
                    _uiState.value.copy(
                        foodPlaces = updatedPlaces,
                        stepsDrafts = finishedDrafts,
                    )
            } catch (e: Exception) {
                e.printStackTrace()
                val errorDrafts =
                    _uiState.value.stepsDrafts.map {
                        if (it.order == order) it.copy(isCreatingFoodPlace = false, isDropdownExpanded = false) else it
                    }
                _uiState.value = _uiState.value.copy(stepsDrafts = errorDrafts)
            }
        }
    }

    fun createItinerary(
        title: String,
        type: String,
        zone: String,
        difficulty: String,
        gastronomy: String,
        description: String,
        distanceKm: Double,
        durationMinutes: Int,
        pathGeoJson: String,
        stepsGeoJson: String,
    ) {
        val token = AuthSession.token

        if (token == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "Vous devez être connecté pour créer un itinéraire.")
            return
        }

        val durationFormatted = formatDuration(durationMinutes)

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val pathObject =
                    try {
                        json.decodeFromString<JsonObject>(pathGeoJson)
                    } catch (_: Exception) {
                        null
                    }

                val finalFeatures =
                    _uiState.value.stepsDrafts.map { draft ->
                        fr.tastyroad.domain.GeoJsonFeature<Step>(
                            type = "Feature",
                            geometry = draft.geometry,
                            properties =
                                Step(
                                    name = draft.name.ifBlank { draft.defaultName.ifBlank { "Étape ${draft.order + 1}" } },
                                    description = draft.description.ifBlank { null },
                                    stepOrder = draft.order,
                                    itinerary = null,
                                    foodplace = draft.foodPlaceId,
                                ),
                        )
                    }

                val finalStepsCollection =
                    if (finalFeatures.isEmpty()) {
                        null
                    } else {
                        GeoJsonFeatureCollection(
                            "FeatureCollection",
                            finalFeatures,
                        )
                    }

                val newItinerary =
                    Itinerary(
                        title = title,
                        type = type,
                        zone = zone,
                        difficulty = difficulty,
                        diet = gastronomy,
                        facts = description,
                        distance = distanceKm,
                        duration = durationFormatted,
                        path = pathObject,
                        steps = finalStepsCollection,
                    )

                println("[CreateItineraryViewModel] Envoi de l'itinéraire : $newItinerary")
                itineraryRepository.createItinerary(token, newItinerary)

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                    )
            } catch (e: Exception) {
                e.printStackTrace()
                println("[CreateItineraryViewModel] Exception lors de la création d'itinéraire : ${e::class.simpleName} - ${e.message}")

                var detailedError = e.message ?: e.toString()
                if (e is io.ktor.client.plugins.ResponseException) {
                    try {
                        val bodyText = e.response.bodyAsText()
                        println("[CreateItineraryViewModel] Status HTTP: ${e.response.status}, Réponse: $bodyText")
                        if (bodyText.isNotBlank()) {
                            detailedError = "HTTP ${e.response.status.value}: $bodyText"
                        }
                    } catch (ex: Exception) {
                        println("[CreateItineraryViewModel] Échec de lecture du body : ${ex.message}")
                        detailedError = "HTTP ${e.response.status.value}: ${e.message}"
                    }
                }

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erreur ($detailedError)",
                    )
            }
        }
    }

    private fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return "${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}:00"
    }
}
