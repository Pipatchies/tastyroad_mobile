package fr.tastyroad.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.tastyroad.domain.Itinerary
import fr.tastyroad.repository.ItineraryRepository
import fr.tastyroad.session.AuthSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ItineraryDetailUiState(
    val isLoading: Boolean = true,
    val itinerary: Itinerary? = null,
    val isDeleting: Boolean = false,
    val isDeleteSuccess: Boolean = false,
    val errorMessage: String? = null,
)

class ItineraryDetailViewModel(
    private val itineraryRepository: ItineraryRepository = ItineraryRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(ItineraryDetailUiState())
    val uiState: StateFlow<ItineraryDetailUiState> = _uiState.asStateFlow()

    fun loadItinerary(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val item = itineraryRepository.getItineraryById(id)
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        itinerary = item,
                    )
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Impossible de charger l'itinéraire.",
                    )
            }
        }
    }

    fun deleteItinerary() {
        val token = AuthSession.token ?: return
        val currentId = _uiState.value.itinerary?.id ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, errorMessage = null)
            try {
                val success = itineraryRepository.deleteItinerary(token, currentId)
                if (success) {
                    _uiState.value = _uiState.value.copy(isDeleting = false, isDeleteSuccess = true)
                } else {
                    _uiState.value = _uiState.value.copy(isDeleting = false, errorMessage = "Erreur lors de la suppression.")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(isDeleting = false, errorMessage = "Impossible de supprimer l'itinéraire.")
            }
        }
    }
}
