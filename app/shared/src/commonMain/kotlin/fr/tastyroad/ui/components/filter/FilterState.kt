package fr.tastyroad.ui.components.filter

import fr.tastyroad.domain.Itinerary
import fr.tastyroad.domain.TopItineraryDTO

data class FilterState(
    val selectedType: String? = null,
    val selectedZone: String? = null,
    val selectedDistance: String? = null,
    val selectedGastronomy: String? = null,
    val selectedDifficulty: String? = null,
) {
    val isActive: Boolean
        get() =
            selectedType != null ||
                selectedZone != null ||
                selectedDistance != null ||
                selectedGastronomy != null ||
                selectedDifficulty != null
}

fun Itinerary.matchesFilter(filterState: FilterState): Boolean {
    if (!filterState.isActive) return true

    if (filterState.selectedType != null) {
        if (type?.lowercase() != filterState.selectedType.lowercase()) return false
    }

    if (filterState.selectedZone != null) {
        val itineraryZone = zone?.lowercase() ?: ""
        val filterZone = filterState.selectedZone.lowercase()
        if (!itineraryZone.contains(filterZone)) return false
    }

    if (filterState.selectedDistance != null && distance != null) {
        val dist = distance
        val matchesDistance =
            when (filterState.selectedDistance) {
                "< 10 km" -> dist < 10.0
                "10 km - 20 km", "10 km - 20km" -> dist in 10.0..20.0
                "20 km - 50 km" -> dist in 20.0..50.0
                "50 km - 100 km" -> dist in 50.0..100.0
                "100 km >" -> dist > 100.0
                else -> true
            }
        if (!matchesDistance) return false
    }

    if (filterState.selectedGastronomy != null) {
        val target = filterState.selectedGastronomy.lowercase()
        val itineraryDiet = diet?.lowercase() ?: ""
        val itinerarySpeciality = speciality?.lowercase() ?: ""
        val itineraryTitle = title.lowercase()
        val itineraryFacts = facts?.lowercase() ?: ""
        val matchesGastro =
            itineraryDiet.contains(target) ||
                itinerarySpeciality.contains(target) ||
                itineraryTitle.contains(target) ||
                itineraryFacts.contains(target)
        if (!matchesGastro) return false
    }

    if (filterState.selectedDifficulty != null) {
        val target = filterState.selectedDifficulty.lowercase()
        val itDiff = difficulty?.lowercase() ?: ""
        val matchesDiff =
            when (target) {
                "facile" -> itDiff.contains("facile")
                "moyenne", "moyen" -> itDiff.contains("moyen")
                "difficile" -> itDiff.contains("difficile")
                else -> itDiff.contains(target)
            }
        if (!matchesDiff) return false
    }

    return true
}

fun TopItineraryDTO.matchesFilter(filterState: FilterState): Boolean {
    if (!filterState.isActive) return true

    if (filterState.selectedType != null) {
        if (type?.lowercase() != filterState.selectedType.lowercase()) return false
    }

    if (filterState.selectedZone != null) {
        val itineraryZone = zone?.lowercase() ?: ""
        val filterZone = filterState.selectedZone.lowercase()
        if (!itineraryZone.contains(filterZone)) return false
    }

    if (filterState.selectedDistance != null && distance != null) {
        val dist = distance
        val matchesDistance =
            when (filterState.selectedDistance) {
                "< 10 km" -> dist < 10.0
                "10 km - 20 km", "10 km - 20km" -> dist in 10.0..20.0
                "20 km - 50 km" -> dist in 20.0..50.0
                "50 km - 100 km" -> dist in 50.0..100.0
                "100 km >" -> dist > 100.0
                else -> true
            }
        if (!matchesDistance) return false
    }

    if (filterState.selectedGastronomy != null) {
        val target = filterState.selectedGastronomy.lowercase()
        val itineraryDiet = diet?.lowercase() ?: ""
        val itineraryTitle = title.lowercase()
        val matchesGastro = itineraryDiet.contains(target) || itineraryTitle.contains(target)
        if (!matchesGastro) return false
    }

    if (filterState.selectedDifficulty != null) {
        val target = filterState.selectedDifficulty.lowercase()
        val itDiff = difficulty?.lowercase() ?: ""
        val matchesDiff =
            when (target) {
                "facile" -> itDiff.contains("facile")
                "moyenne", "moyen" -> itDiff.contains("moyen")
                "difficile" -> itDiff.contains("difficile")
                else -> itDiff.contains(target)
            }
        if (!matchesDiff) return false
    }

    return true
}
