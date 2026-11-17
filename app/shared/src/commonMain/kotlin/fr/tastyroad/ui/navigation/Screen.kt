package fr.tastyroad.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    // --- Les 4 onglets de la NavBar ---
    @Serializable
    data object Home : Screen()

    @Serializable
    data object Community : Screen()

    @Serializable
    data object Favorites : Screen()

    @Serializable
    data object Profile : Screen()

    // --- Les autres écrans ---
    @Serializable
    data class Map(val searchQuery: String? = null) : Screen()

    @Serializable
    data class ItineraryDetail(val itineraryId: Int) : Screen()

    @Serializable
    data object CreateItinerary : Screen()

    @Serializable
    data object NotFound : Screen()
}
