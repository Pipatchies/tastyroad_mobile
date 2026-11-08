package fr.tastyroad.ui.components.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CreationMapView(
    transportType: String,
    // On ajoute pathGeoJson et stepsGeoJson pour recevoir la géométrie de la carte Leaflet
    onMetricsUpdated: (distanceKm: Double, durationMinutes: Int, stepCount: Int, pathGeoJson: String, stepsGeoJson: String) -> Unit,
    modifier: Modifier = Modifier,
)
