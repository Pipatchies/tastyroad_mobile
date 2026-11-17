package fr.tastyroad.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import fr.tastyroad.session.AuthSession
import fr.tastyroad.ui.screens.AuthScreen
import fr.tastyroad.ui.screens.ComingSoonScreen
import fr.tastyroad.ui.screens.CreateItineraryScreen
import fr.tastyroad.ui.screens.HomeScreen
import fr.tastyroad.ui.screens.ItineraryDetailScreen
import fr.tastyroad.ui.screens.MapScreen
import fr.tastyroad.ui.screens.NotFoundScreen
import fr.tastyroad.ui.screens.ProfileScreen

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
                    navController.navigate(Screen.Map())
                },
                onSearchToMap = { query ->
                    navController.navigate(Screen.Map(searchQuery = query))
                },
                onItineraryClick = { id ->
                    navController.navigate(Screen.ItineraryDetail(id))
                },
            )
        }

        composable<Screen.Community> {
            ComingSoonScreen(
                title = "Communauté",
                subtitle = "Échangez avec les passionnés de terroir",
                description =
                    "L'espace Communauté arrive très prochainement ! " +
                        "Vous pourrez y partager vos coups de cœur gastronomiques " +
                        "et échanger avec les membres TastyRoad.",
                icon = Icons.Outlined.Groups,
                onBackToHomeClick = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
            )
        }

        composable<Screen.Favorites> {
            ComingSoonScreen(
                title = "Mes Favoris",
                subtitle = "Vos parcours préférés à portée de main",
                description =
                    "La gestion de vos favoris arrive très bientôt ! " +
                        "Vous pourrez y retrouver et organiser tous vos itinéraires enregistrés.",
                icon = Icons.Outlined.FavoriteBorder,
                onBackToHomeClick = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
            )
        }

        composable<Screen.Profile> {
            if (AuthSession.isLoggedIn) {
                ProfileScreen(
                    onLogoutClick = {
                    },
                    onItineraryClick = { id ->
                        navController.navigate(Screen.ItineraryDetail(id))
                    },
                )
            } else {
                AuthScreen(
                    onAuthSuccess = {
                        // Connexion / Inscription réussie !
                    },
                )
            }
        }

        composable<Screen.Map> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.Map>()
            MapScreen(
                initialSearchQuery = route.searchQuery,
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
            CreateItineraryScreen(
                onCloseClick = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
                onSaveSuccess = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
            )
        }

        composable<Screen.ItineraryDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ItineraryDetail>()
            val itineraryId = route.itineraryId
            ItineraryDetailScreen(
                itineraryId = itineraryId,
                onBackClick = {
                    navController.popBackStack()
                },
                onDeleteSuccess = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
            )
        }

        composable<Screen.NotFound> {
            NotFoundScreen(
                onBackToHomeClick = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Home> { inclusive = true }
                    }
                },
            )
        }
    }
}
