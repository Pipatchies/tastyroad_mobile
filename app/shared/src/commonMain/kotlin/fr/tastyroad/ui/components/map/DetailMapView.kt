package fr.tastyroad.ui.components.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun DetailMapView(
    pathGeoJson: String,
    stepsGeoJson: String,
    modifier: Modifier = Modifier,
)
