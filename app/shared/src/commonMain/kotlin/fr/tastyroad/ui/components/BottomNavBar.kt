package fr.tastyroad.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import fr.tastyroad.ui.navigation.Screen
import androidx.compose.material3.MaterialTheme

private data class NavItem(
    val screen: Screen,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val label: String,
)

@Composable
fun BottomNavBar(navController: NavController) {
    val backgroundColor = MaterialTheme.colorScheme.secondary
    val iconColor = MaterialTheme.colorScheme.onSecondary
    val transparent = Color.Transparent

    val tabs =
        listOf(
            NavItem(Screen.Home, Icons.Filled.Home, Icons.Outlined.Home, "Accueil"),
            NavItem(Screen.Community, Icons.Filled.Groups, Icons.Outlined.Groups, "Communauté"),
            NavItem(Screen.Favorites, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, "Favoris"),
            NavItem(Screen.Profile, Icons.Filled.Person, Icons.Outlined.Person, "Profil"),
        )

    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route ?: ""

    NavigationBar(
        containerColor = backgroundColor,
        contentColor = iconColor,
    ) {
        tabs.forEach { tab ->
            val screenName = tab.screen::class.simpleName ?: ""
            val isSelected = currentRoute.contains(screenName)

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(tab.screen) {
                        popUpTo<Screen.Home> { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tab.filledIcon else tab.outlinedIcon,
                        contentDescription = tab.label,
                        modifier = Modifier.size(46.dp),
                    )
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = iconColor,
                        unselectedIconColor = iconColor,
                        indicatorColor = transparent,
                    ),
            )
        }
    }
}
