package fr.tastyroad.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fr.tastyroad.ui.screens.HomeScreen
import fr.tastyroad.ui.screens.MapScreen

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
            HomeScreen(
                onMapClick = {
                    navController.navigate(Screen.Map)
                },
            )
        }

        composable<Screen.Community> {
            Text("Écran de la Communauté")
        }

        composable<Screen.Favorites> {
            Text("Écran des Favoris")
        }

        composable<Screen.Profile> {
            Text("Écran du Profil Utilisateur")
        }

        composable<Screen.Map> {
            MapScreen(
                onCloseClick = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
                onCreateItineraryClick = {
                    navController.navigate(Screen.CreateItinerary)
                },
            )
        }

        composable<Screen.CreateItinerary> {
            Text("Écran de création d'itinéraire")
        }

        composable<Screen.ItineraryDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ItineraryDetail>()
            val itineraryId = route.itineraryId
            Text("Écran de Détail pour l'itinéraire n° $itineraryId")
        }
    }
}
