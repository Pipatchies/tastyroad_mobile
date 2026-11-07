package fr.tastyroad.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {

    @Serializable
    data object Home : Screen()

    @Serializable
    data object Map : Screen()

    @Serializable
    data class ItineraryDetail(val itineraryId: Int) : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object CreateItinerary : Screen()
}