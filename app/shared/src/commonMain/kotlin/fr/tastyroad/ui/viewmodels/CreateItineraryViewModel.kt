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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.SerialName

@Serializable
data class JsStepProperties(
    val name: String,
    @SerialName("step_order") val stepOrder: Int
)

// Classe représentant l'état d'édition d'une étape dans le formulaire
data class StepDraft(
    val order: Int,
    val defaultName: String,
    val name: String = "",
    val description: String = "",
    val foodPlaceId: Int? = null,
    val geometry: JsonObject? = null
)

data class CreateItineraryUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val foodPlaces: List<FoodPlace> = emptyList(),
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
            } catch (_: Exception) {
            }
        }
    }

    fun updateStepsFromGeoJson(stepsGeoJson: String) {
        try {
            val stepsCollection = json.decodeFromString<GeoJsonFeatureCollection<JsStepProperties>>(stepsGeoJson)
            val drafts = stepsCollection.features.mapIndexed { index, feature ->
                val defaultName = feature.properties.name
                val oldDraft = _uiState.value.stepsDrafts.find { it.order == index }

                StepDraft(
                    order = index,
                    defaultName = defaultName,
                    name = oldDraft?.name ?: "",
                    description = oldDraft?.description ?: "",
                    foodPlaceId = oldDraft?.foodPlaceId,
                    geometry = feature.geometry
                )
            }
            _uiState.value = _uiState.value.copy(stepsDrafts = drafts)
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.value = _uiState.value.copy(stepsDrafts = emptyList())
        }
    }

    fun updateStepName(order: Int, newName: String) {
        val updatedDrafts = _uiState.value.stepsDrafts.map {
            if (it.order == order) it.copy(name = newName) else it
        }
        _uiState.value = _uiState.value.copy(stepsDrafts = updatedDrafts)
    }

    fun updateStepDescription(order: Int, newDesc: String) {
        val updatedDrafts = _uiState.value.stepsDrafts.map {
            if (it.order == order) it.copy(description = newDesc) else it
        }
        _uiState.value = _uiState.value.copy(stepsDrafts = updatedDrafts)
    }

    fun updateStepFoodPlace(order: Int, foodPlaceId: Int?) {
        val updatedDrafts = _uiState.value.stepsDrafts.map {
            if (it.order == order) it.copy(foodPlaceId = foodPlaceId) else it
        }
        _uiState.value = _uiState.value.copy(stepsDrafts = updatedDrafts)
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

                val finalFeatures = _uiState.value.stepsDrafts.map { draft ->
                    fr.tastyroad.domain.GeoJsonFeature<Step>(
                        type = "Feature",
                        geometry = draft.geometry,
                        properties = Step(
                            name = draft.name.ifBlank { draft.defaultName },
                            description = draft.description.ifBlank { null },
                            stepOrder = draft.order,
                            itinerary = 0, // Sera set par le backend
                            foodplace = draft.foodPlaceId
                        )
                    )
                }
                val finalStepsCollection = GeoJsonFeatureCollection("FeatureCollection", finalFeatures)

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

                itineraryRepository.createItinerary(token, newItinerary)

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                    )
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = e.message?.lowercase() ?: ""
                val error =
                    if (msg.contains("connect") || msg.contains("timeout") || msg.contains("host") || msg.contains("500")) {
                        "Erreur serveur. Veuillez réessayer plus tard."
                    } else {
                        "Une erreur est survenue lors de la création de l'itinéraire."
                    }

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error,
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
