package fr.tastyroad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.tastyroad.ui.components.buttons.TastyRoadButton
import fr.tastyroad.ui.components.cards.ItineraryCard
import fr.tastyroad.ui.components.filter.FilterSection
import fr.tastyroad.ui.components.header.Header
import fr.tastyroad.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    onMapClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel { HomeViewModel() },
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
        ) {
            Header()

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FilterSection()

                Text(
                    text = "A LA UNE",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp).align(Alignment.Start),
                )

                if (uiState.isLoading) {
                    Spacer(modifier = Modifier.height(32.dp))
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                } else if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = uiState.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                } else if (uiState.itineraries.isEmpty()) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Aucun itinéraire trouvé.",
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                } else {
                    uiState.itineraries.forEach { item ->
                        ItineraryCard(item = item)
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        TastyRoadButton(
            text = "Carte",
            onClick = onMapClick,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
        )
    }
}
