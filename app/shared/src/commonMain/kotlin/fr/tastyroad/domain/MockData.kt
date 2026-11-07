package fr.tastyroad.domain

data class ItineraryDisplayItem(
    val itinerary: Itinerary,
    val rating: Float,
    val imageUrl: String,
    val stepsCount: Int
)

object MockData {
    val sampleItineraries = listOf(
        ItineraryDisplayItem(
            itinerary = Itinerary(
                id = 1,
                title = "Saveurs d'Occitanie",
                type = "Vélo",
                zone = "Occitanie, France",
                distance = 45.0,
                duration = "8 h",
                difficulty = "Facile",
                diet = "Viande",
                author = 1
            ),
            rating = 4.8f,
            imageUrl = "https://images.unsplash.com/photo-1555396273-367ea4eb4db5?q=80&w=800&auto=format&fit=crop",
            stepsCount = 10
        ),
        ItineraryDisplayItem(
            itinerary = Itinerary(
                id = 2,
                title = "Sentiers méditerranéens",
                type = "Moto",
                zone = "PACA, France",
                distance = 170.0,
                duration = "3 h",
                difficulty = "Moyen",
                diet = "Poisson",
                author = 1
            ),
            rating = 4.4f,
            imageUrl = "https://images.unsplash.com/photo-1516483638261-f4dbaf036963?q=80&w=800&auto=format&fit=crop",
            stepsCount = 6
        ),
        ItineraryDisplayItem(
            itinerary = Itinerary(
                id = 3,
                title = "Route des bières",
                type = "Rando",
                zone = "Nord, France",
                distance = 25.0,
                duration = "5 h",
                difficulty = "Facile",
                diet = "Bière",
                author = 1
            ),
            rating = 4.7f,
            imageUrl = "https://images.unsplash.com/photo-1510812431401-41d2bd2722f3?q=80&w=800&auto=format&fit=crop",
            stepsCount = 5
        )
    )
}
