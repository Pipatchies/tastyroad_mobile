package fr.tastyroad.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.compose.ui.Modifier

@Composable
fun AppNavigation(navController: NavHostController,  modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        modifier = modifier
    ) {

        composable<Screen.Home> {
            // TODO: Remplacer par le vrai composant Home
            Text("Écran d'Accueil")
        }

        composable<Screen.Map> {
            // TODO: Remplacer par le vrai composant Map
            Text("Écran de la Carte Interactive")
        }

        composable<Screen.CreateItinerary> {
            // TODO: Remplacer par le vrai composant de Création
            Text("Écran de Création d'Itinéraire")
        }

        composable<Screen.Profile> {
            // TODO: Remplacer par le vrai composant Profil
            Text("Écran du Profil Utilisateur")
        }

        composable<Screen.ItineraryDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ItineraryDetail>()
            val itineraryId = route.itineraryId
            // TODO: Remplacer par le vrai composant Detail
            Text("Écran de Détail pour l'itinéraire n° $itineraryId")
        }
    }
}