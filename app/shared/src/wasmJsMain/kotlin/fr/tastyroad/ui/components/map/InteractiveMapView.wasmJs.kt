package fr.tastyroad.ui.components.map

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun InteractiveMapView(
    clusters: List<MapClusterData>,
    modifier: Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Carte Interactive (Web Wasm)")
    }
}
