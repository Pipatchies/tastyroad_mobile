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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.tastyroad.domain.ItineraryDisplayItem
import fr.tastyroad.domain.MockData
import fr.tastyroad.ui.components.buttons.TastyRoadButton
import fr.tastyroad.ui.components.filter.FilterSection
import fr.tastyroad.ui.components.header.Header
import fr.tastyroad.ui.components.map.InteractiveMapView
import fr.tastyroad.ui.components.map.MapClusterData

@Composable
fun MapScreen(
    onCloseClick: () -> Unit,
    onCreateItineraryClick: () -> Unit,
    itineraries: List<ItineraryDisplayItem> = MockData.sampleItineraries,
) {
    val countByZone =
        itineraries.groupBy { it.itinerary.zone ?: "" }
            .mapValues { it.value.size }

    val clusters =
        listOf(
            MapClusterData("Occitanie, France", countByZone["Occitanie, France"] ?: 10, 43.6047, 1.4442),
            MapClusterData("PACA, France", countByZone["PACA, France"] ?: 17, 43.2965, 5.3698),
            MapClusterData("Nord, France", countByZone["Nord, France"] ?: 10, 50.6292, 3.0573),
            MapClusterData("Ile-de-France", 12, 48.8566, 2.3522),
            MapClusterData("Bretagne", 8, 48.1173, -1.6778),
            MapClusterData("Auvergne-Rhône-Alpes", 15, 45.7640, 4.8357),
        )

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
        ) {
            InteractiveMapView(
                clusters = clusters,
                modifier = Modifier.fillMaxSize(),
            )

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
