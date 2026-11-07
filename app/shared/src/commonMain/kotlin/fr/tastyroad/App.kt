package fr.tastyroad

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import fr.tastyroad.ui.navigation.AppNavigation

@Preview
@Composable
fun App() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            // Footer à implémenter plus tard
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
