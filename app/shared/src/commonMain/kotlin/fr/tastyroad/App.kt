package fr.tastyroad

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import fr.tastyroad.ui.components.footer.BottomNavBar
import fr.tastyroad.ui.navigation.AppNavigation
import fr.tastyroad.ui.theme.TastyRoadTheme

@Preview
@Composable
fun App() {
    val navController = rememberNavController()

    TastyRoadTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    BottomNavBar(navController = navController)
                },
            ) { innerPadding ->
                AppNavigation(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                )
            }
        }
    }
}
