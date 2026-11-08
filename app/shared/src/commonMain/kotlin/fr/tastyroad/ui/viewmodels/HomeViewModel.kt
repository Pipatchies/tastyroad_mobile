package fr.tastyroad.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.tastyroad.domain.Itinerary
import fr.tastyroad.domain.ItineraryDisplayItem
import fr.tastyroad.repository.ItineraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val itineraries: List<ItineraryDisplayItem> = emptyList(),
    val errorMessage: String? = null,
)

class HomeViewModel(
    private val itineraryRepository: ItineraryRepository = ItineraryRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchTopItineraries()
    }

    fun fetchTopItineraries() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val topItineraries = itineraryRepository.getTopItineraries(limit = 5)

                val displayItems =
                    topItineraries.map { dto ->
                        ItineraryDisplayItem(
                            itinerary =
                                Itinerary(
                                    id = dto.id,
                                    title = dto.title,
                                    type = dto.type,
                                    zone = dto.zone,
                                    distance = dto.distance,
                                    duration = dto.duration,
                                    difficulty = dto.difficulty,
                                    diet = dto.diet,
                                    author = 0,
                                ),
                            rating = dto.averageRating.toFloat(),
                            imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=800&auto=format&fit=crop",
                            stepsCount = dto.stepsCount ?: 0,
                        )
                    }

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        itineraries = displayItems,
                    )
            } catch (e: Exception) {
                val msg = e.message?.lowercase() ?: ""
                val error =
                    if (msg.contains("connect") || msg.contains("host") || msg.contains("timeout")) {
                        "Impossible de joindre le serveur. Vérifiez votre connexion ou l'état du serveur."
                    } else {
                        "Une erreur est survenue lors de la récupération des itinéraires à la une."
                    }

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error,
                    )
            }
        }
    }
}
