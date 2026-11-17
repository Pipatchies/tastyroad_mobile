package fr.tastyroad.ui.components.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class MapClusterData(
    val name: String,
    val count: Int,
    val latitude: Double,
    val longitude: Double,
)

@Composable
expect fun InteractiveMapView(
    clusters: List<MapClusterData>,
    centerLat: Double? = null,
    centerLng: Double? = null,
    zoomLevel: Int? = null,
    modifier: Modifier = Modifier,
)
