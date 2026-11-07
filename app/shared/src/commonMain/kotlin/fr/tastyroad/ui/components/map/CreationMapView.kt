package fr.tastyroad.ui.components.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CreationMapView(
    transportType: String,
    onMetricsUpdated: (distanceKm: Double, durationMinutes: Int, stepCount: Int) -> Unit,
    modifier: Modifier = Modifier,
)
