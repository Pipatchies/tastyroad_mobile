package fr.tastyroad.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.tastyroad.domain.ItineraryDisplayItem
import fr.tastyroad.repository.AuthSession
import fr.tastyroad.repository.ItineraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val myItineraries: List<ItineraryDisplayItem> = emptyList(),
    val myFavorites: List<ItineraryDisplayItem> = emptyList(),
    val errorMessage: String? = null,
)

class ProfileViewModel(
    private val itineraryRepository: ItineraryRepository = ItineraryRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchMyData()
    }

    fun fetchMyData() {
        val currentUser = AuthSession.currentUser ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val allItineraries = itineraryRepository.getItineraries()

                val myApiItineraries = allItineraries.filter { it.author == currentUser.id }
                // On récupère les itinéraires que l'utilisateur a mis en favoris (son ID est dans la liste 'favorites' de l'itinéraire)
                val myApiFavorites =
                    allItineraries.filter { itinerary ->
                        currentUser.id != null && itinerary.favorites.contains(currentUser.id)
                    }

                val myDisplayItems =
                    myApiItineraries.map { itinerary ->
                        ItineraryDisplayItem(
                            itinerary = itinerary,
                            rating = 0.0f,
                            imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=800&auto=format&fit=crop",
                            stepsCount = itinerary.steps?.features?.size ?: 0,
                        )
                    }

                val myFavoritesDisplayItems =
                    myApiFavorites.map { itinerary ->
                        ItineraryDisplayItem(
                            itinerary = itinerary,
                            rating = 0.0f,
                            imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=800&auto=format&fit=crop",
                            stepsCount = itinerary.steps?.features?.size ?: 0,
                        )
                    }

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        myItineraries = myDisplayItems,
                        myFavorites = myFavoritesDisplayItems,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erreur lors du chargement de votre profil.",
                    )
            }
        }
    }
}
