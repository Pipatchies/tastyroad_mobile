package fr.tastyroad.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.tastyroad.domain.ItineraryDisplayItem
import fr.tastyroad.repository.CommentRepository
import fr.tastyroad.repository.ItineraryRepository
import fr.tastyroad.session.AuthSession
import fr.tastyroad.ui.components.cards.ReviewItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val myItineraries: List<ItineraryDisplayItem> = emptyList(),
    val myFavorites: List<ItineraryDisplayItem> = emptyList(),
    val myReviews: List<ReviewItem> = emptyList(),
    val errorMessage: String? = null,
)

class ProfileViewModel(
    private val itineraryRepository: ItineraryRepository = ItineraryRepository(),
    private val commentRepository: CommentRepository = CommentRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchMyData()
    }

    fun fetchMyData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // 1. On s'assure d'avoir chargé les infos de l'utilisateur avec son ID
            if (AuthSession.currentUser == null) {
                AuthSession.fetchCurrentUserProfile(fr.tastyroad.repository.AuthRepository())
            }

            val currentUser = AuthSession.currentUser
            if (currentUser == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, 
                    errorMessage = "Impossible de récupérer votre profil."
                )
                return@launch
            }

            try {
                val allItineraries = itineraryRepository.getItineraries()

                val myApiItineraries = allItineraries.filter { it.author == currentUser.id }
                val myApiFavorites =
                    allItineraries.filter { itinerary ->
                        currentUser.id != null && itinerary.favorites.contains(currentUser.id)
                    }

                val myComments =
                    try {
                        currentUser.id?.let { commentRepository.getCommentsByUser(it) } ?: emptyList()
                    } catch (_: Exception) {
                        emptyList()
                    }

                val myDisplayItems =
                    myApiItineraries.map { itinerary ->
                        ItineraryDisplayItem(
                            itinerary = itinerary,
                            rating = 4.5f,
                            imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=800&auto=format&fit=crop",
                            stepsCount = itinerary.steps?.features?.size ?: 0,
                        )
                    }

                val myFavoritesDisplayItems =
                    myApiFavorites.map { itinerary ->
                        ItineraryDisplayItem(
                            itinerary = itinerary,
                            rating = 4.5f,
                            imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=800&auto=format&fit=crop",
                            stepsCount = itinerary.steps?.features?.size ?: 0,
                        )
                    }

                val reviewItems =
                    myComments.map { comment ->
                        ReviewItem(
                            id = comment.id,
                            title = "Itinéraire n° ${comment.itinerary}",
                            rating = comment.review,
                            comment = comment.content,
                            imageUrl = "https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?q=80&w=300&auto=format&fit=crop",
                        )
                    }

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        myItineraries = myDisplayItems,
                        myFavorites = myFavoritesDisplayItems,
                        myReviews = reviewItems,
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
