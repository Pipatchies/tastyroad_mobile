package fr.tastyroad.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.tastyroad.repository.ItineraryRepository
import fr.tastyroad.ui.components.map.MapClusterData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapUiState(
    val isLoading: Boolean = false,
    val clusters: List<MapClusterData> = emptyList(),
    val errorMessage: String? = null,
)

class MapViewModel(
    private val itineraryRepository: ItineraryRepository = ItineraryRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        fetchClusters()
    }

    private fun fetchClusters() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val allItineraries = itineraryRepository.getItineraries()

                val countByZone =
                    allItineraries.groupBy { it.zone ?: "Inconnu" }
                        .mapValues { it.value.size }

                val dynamicClusters =
                    countByZone.mapNotNull { (zoneName, count) ->
                        val (lat, lng) = getCoordinatesForZone(zoneName) ?: return@mapNotNull null
                        MapClusterData(
                            name = zoneName,
                            count = count,
                            latitude = lat,
                            longitude = lng,
                        )
                    }

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        clusters = dynamicClusters,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Erreur de chargement des clusters",
                    )
            }
        }
    }

    // Un mapping simple pour centrer les bulles oranges sur les vraies régions de France
    private fun getCoordinatesForZone(zone: String): Pair<Double, Double>? {
        val lowerZone = zone.lowercase()
        return when {
            lowerZone.contains("occitanie") -> Pair(43.6047, 1.4442) // Toulouse
            lowerZone.contains("paca") || lowerZone.contains("provence") -> Pair(43.2965, 5.3698) // Marseille
            lowerZone.contains("nord") || lowerZone.contains("hauts-de-france") -> Pair(50.6292, 3.0573) // Lille
            lowerZone.contains("ile-de-france") || lowerZone.contains("paris") -> Pair(48.8566, 2.3522) // Paris
            lowerZone.contains("bretagne") -> Pair(48.1173, -1.6778) // Rennes
            lowerZone.contains("auvergne") || lowerZone.contains("rhône") -> Pair(45.7640, 4.8357) // Lyon
            lowerZone.contains("nouvelle-aquitaine") -> Pair(44.8378, -0.5792) // Bordeaux
            lowerZone.contains("grand est") -> Pair(48.5734, 7.7521) // Strasbourg
            lowerZone.contains("bourgogne") -> Pair(47.3220, 5.0415) // Dijon
            lowerZone.contains("corse") -> Pair(41.9272, 8.7346) // Ajaccio
            else -> null // On ne place pas de bulle si la zone n'est pas mappée géographiquement
        }
    }
}
