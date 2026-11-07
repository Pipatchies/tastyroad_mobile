package fr.tastyroad.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fr.tastyroad.ui.screens.HomeScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        modifier = modifier,
    ) {
        composable<Screen.Home> {
            HomeScreen()
        }

        composable<Screen.Community> {
            // TODO: Remplacer par le vrai composant Community
            Text("Écran de la Communauté")
        }

        composable<Screen.Favorites> {
            // TODO: Remplacer par le vrai composant Favoris
            Text("Écran des Favoris")
        }

        composable<Screen.Profile> {
            // TODO: Remplacer par le vrai composant Profil
            Text("Écran du Profil Utilisateur")
        }

        composable<Screen.Map> {
            // TODO: Remplacer par le vrai composant Map
            Text("Écran de la Carte Interactive")
        }

        composable<Screen.CreateItinerary> {
            // TODO: Remplacer par le vrai composant de Création
            Text("Écran de Création d'Itinéraire")
        }

        composable<Screen.ItineraryDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ItineraryDetail>()
            val itineraryId = route.itineraryId
            // TODO: Remplacer par le vrai composant Detail
            Text("Écran de Détail pour l'itinéraire n° $itineraryId")
        }
    }
}
