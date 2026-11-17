package fr.tastyroad.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.tastyroad.domain.Itinerary
import fr.tastyroad.repository.ItineraryRepository
import fr.tastyroad.ui.components.filter.FilterState
import fr.tastyroad.ui.components.filter.matchesFilter
import fr.tastyroad.ui.components.map.MapClusterData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MapUiState(
    val isLoading: Boolean = false,
    val allItineraries: List<Itinerary> = emptyList(),
    val clusters: List<MapClusterData> = emptyList(),
    val searchQuery: String = "",
    val centerLat: Double? = null,
    val centerLng: Double? = null,
    val zoomLevel: Int? = null,
    val filterState: FilterState = FilterState(),
    val errorMessage: String? = null,
)

class MapViewModel(
    private val itineraryRepository: ItineraryRepository = ItineraryRepository(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        fetchItineraries()
    }

    fun initializeSearch(initialQuery: String?) {
        if (!initialQuery.isNullOrBlank()) {
            performSearch(initialQuery)
        }
    }

    fun fetchItineraries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val itineraries = itineraryRepository.getItineraries()
                _uiState.update { it.copy(isLoading = false, allItineraries = itineraries) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            } finally {
                updateClusters()
                if (_uiState.value.searchQuery.isNotBlank()) {
                    val coords = resolveLocation(_uiState.value.searchQuery)
                    if (coords != null) {
                        _uiState.update {
                            it.copy(
                                centerLat = coords.first,
                                centerLng = coords.second,
                                zoomLevel = coords.third,
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun performSearch(query: String) {
        val targetCoords = resolveLocation(query)
        _uiState.update {
            it.copy(
                searchQuery = query,
                centerLat = targetCoords?.first ?: it.centerLat,
                centerLng = targetCoords?.second ?: it.centerLng,
                zoomLevel = targetCoords?.third ?: it.zoomLevel,
            )
        }
        updateClusters()
    }

    fun setFilter(filterState: FilterState) {
        _uiState.update { it.copy(filterState = filterState) }
        updateClusters()
    }

    private fun updateClusters() {
        _uiState.update { currentState ->
            val filteredList =
                currentState.allItineraries.filter { itinerary ->
                    val matchesFilters = itinerary.matchesFilter(currentState.filterState)
                    val matchesSearch =
                        if (currentState.searchQuery.isBlank()) {
                            true
                        } else {
                            val q = currentState.searchQuery.lowercase()
                            itinerary.title.lowercase().contains(q) ||
                                (itinerary.zone?.lowercase()?.contains(q) == true) ||
                                (itinerary.type?.lowercase()?.contains(q) == true) ||
                                (itinerary.diet?.lowercase()?.contains(q) == true)
                        }
                    matchesFilters && matchesSearch
                }

            val dynamicClusters =
                filteredList.mapNotNull { itinerary ->
                    var lat: Double? = null
                    var lng: Double? = null

                    val path = itinerary.path
                    if (path != null && path.containsKey("coordinates")) {
                        try {
                            val coords = path["coordinates"] as? kotlinx.serialization.json.JsonArray
                            if (coords != null && coords.isNotEmpty()) {
                                val firstPoint = coords[0] as? kotlinx.serialization.json.JsonArray
                                if (firstPoint != null && firstPoint.size >= 2) {
                                    lng = firstPoint[0].toString().toDoubleOrNull()
                                    lat = firstPoint[1].toString().toDoubleOrNull()
                                }
                            }
                        } catch (_: Exception) {
                        }
                    }

                    if (lat == null || lng == null) {
                        val zoneCoords = getCoordinatesForZone(itinerary.zone ?: "")
                        if (zoneCoords != null) {
                            lat = zoneCoords.first
                            lng = zoneCoords.second
                        }
                    }

                    if (lat != null && lng != null) {
                        val finalLat = if (itinerary.path == null) lat + (kotlin.random.Random.nextDouble(-0.05, 0.05)) else lat
                        val finalLng = if (itinerary.path == null) lng + (kotlin.random.Random.nextDouble(-0.05, 0.05)) else lng

                        MapClusterData(
                            name = itinerary.title,
                            count = 1,
                            latitude = finalLat,
                            longitude = finalLng,
                        )
                    } else {
                        null
                    }
                }

            currentState.copy(clusters = dynamicClusters)
        }
    }

    private fun resolveLocation(query: String): Triple<Double, Double, Int>? {
        if (query.isBlank()) return null
        val q = query.lowercase().trim()

        val staticCoords =
            when {
                q.contains("paris") || q.contains("ile-de-france") || q.contains("île-de-france") -> Triple(48.8566, 2.3522, 11)
                q.contains("toulouse") || q.contains("occitanie") -> Triple(43.6047, 1.4442, 10)
                q.contains("marseille") || q.contains("paca") || q.contains("provence") -> Triple(43.2965, 5.3698, 11)
                q.contains("lyon") || q.contains("rhone") || q.contains("rhône") || q.contains("auvergne") -> Triple(45.7640, 4.8357, 11)
                q.contains("lille") || q.contains("nord") || q.contains("hauts-de-france") -> Triple(50.6292, 3.0573, 11)
                q.contains("rennes") || q.contains("bretagne") -> Triple(48.1173, -1.6778, 10)
                q.contains("bordeaux") || q.contains("aquitaine") || q.contains("nouvelle-aquitaine") -> Triple(44.8378, -0.5792, 11)
                q.contains("strasbourg") || q.contains("alsace") || q.contains("grand est") -> Triple(48.5734, 7.7521, 11)
                q.contains("dijon") || q.contains("bourgogne") -> Triple(47.3220, 5.0415, 11)
                q.contains("ajaccio") || q.contains("corse") -> Triple(41.9272, 8.7346, 9)
                q.contains("nantes") -> Triple(47.2184, -1.5536, 11)
                q.contains("nice") -> Triple(43.7102, 7.2620, 11)
                q.contains("montpellier") -> Triple(43.6108, 3.8767, 11)
                q.contains("grenoble") -> Triple(45.1885, 5.7245, 11)
                q.contains("rouen") || q.contains("normandie") -> Triple(49.4431, 1.0993, 11)
                q.contains("reims") -> Triple(49.2583, 4.0317, 11)
                q.contains("clermont") -> Triple(45.7772, 3.0870, 11)
                q.contains("tours") -> Triple(47.3941, 0.6848, 11)
                q.contains("limoges") -> Triple(45.8336, 1.2611, 11)
                q.contains("cantal") -> Triple(45.0333, 2.6667, 10)
                else -> null
            }

        if (staticCoords != null) return staticCoords

        val matchingItinerary =
            _uiState.value.allItineraries.firstOrNull {
                it.title.lowercase().contains(q) || (it.zone?.lowercase()?.contains(q) == true)
            }

        if (matchingItinerary != null) {
            val zoneCoords = getCoordinatesForZone(matchingItinerary.zone ?: "")
            if (zoneCoords != null) {
                return Triple(zoneCoords.first, zoneCoords.second, 11)
            }
        }

        return null
    }

    private fun getCoordinatesForZone(zone: String): Pair<Double, Double>? {
        val lowerZone = zone.lowercase()
        return when {
            lowerZone.contains("occitanie") -> Pair(43.6047, 1.4442)
            lowerZone.contains("paca") || lowerZone.contains("provence") -> Pair(43.2965, 5.3698)
            lowerZone.contains("nord") || lowerZone.contains("hauts-de-france") -> Pair(50.6292, 3.0573)
            lowerZone.contains("ile-de-france") || lowerZone.contains("paris") -> Pair(48.8566, 2.3522)
            lowerZone.contains("bretagne") -> Pair(48.1173, -1.6778)
            lowerZone.contains("auvergne") || lowerZone.contains("rhône") || lowerZone.contains("rhone") -> Pair(45.7640, 4.8357)
            lowerZone.contains("nouvelle-aquitaine") || lowerZone.contains("aquitaine") -> Pair(44.8378, -0.5792)
            lowerZone.contains("grand est") || lowerZone.contains("alsace") -> Pair(48.5734, 7.7521)
            lowerZone.contains("bourgogne") -> Pair(47.3220, 5.0415)
            lowerZone.contains("corse") -> Pair(41.9272, 8.7346)
            else -> null
        }
    }
}
