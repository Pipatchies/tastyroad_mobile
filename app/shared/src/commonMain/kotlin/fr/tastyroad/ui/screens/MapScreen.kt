package fr.tastyroad.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.tastyroad.ui.components.buttons.TastyRoadButton
import fr.tastyroad.ui.components.filter.FilterSection
import fr.tastyroad.ui.components.header.Header
import fr.tastyroad.ui.components.map.InteractiveMapView
import fr.tastyroad.ui.viewmodels.MapViewModel

@Composable
fun MapScreen(
    onCloseClick: () -> Unit,
    onCreateItineraryClick: () -> Unit,
    viewModel: MapViewModel = viewModel { MapViewModel() },
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Header()

        FilterSection()

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                InteractiveMapView(
                    clusters = uiState.clusters,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Surface(
                onClick = onCloseClick,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, Color.Black),
                modifier =
                    Modifier
                        .padding(12.dp)
                        .size(36.dp)
                        .align(Alignment.TopStart),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fermer la carte",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            TastyRoadButton(
                text = "Ajouter un itinéraire",
                onClick = onCreateItineraryClick,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
            )
        }
    }
}
