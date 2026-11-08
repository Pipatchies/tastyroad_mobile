package fr.tastyroad.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.tastyroad.domain.FoodPlace
import fr.tastyroad.domain.GeoJsonFeatureCollection
import fr.tastyroad.domain.Itinerary
import fr.tastyroad.domain.Step
import fr.tastyroad.repository.AuthSession
import fr.tastyroad.repository.FoodPlaceRepository
import fr.tastyroad.repository.ItineraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

data class CreateItineraryUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val foodPlaces: List<FoodPlace> = emptyList(),
    val errorMessage: String? = null,
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
                // Ignore failure on foodplaces fetch for offline/mock fallback
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
        val authorId = AuthSession.currentUser?.id

        if (token == null || authorId == null) {
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

                val stepsCollection =
                    try {
                        json.decodeFromString<GeoJsonFeatureCollection<Step>>(stepsGeoJson)
                    } catch (_: Exception) {
                        null
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
                        author = authorId,
                        path = pathObject,
                        steps = stepsCollection,
                    )

                itineraryRepository.createItinerary(token, newItinerary)

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                    )
            } catch (e: Exception) {
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
