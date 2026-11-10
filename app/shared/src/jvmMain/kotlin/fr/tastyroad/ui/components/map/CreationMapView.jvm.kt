package fr.tastyroad.ui.components.map

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
actual fun CreationMapView(
    transportType: String,
    onMetricsUpdated: (Double, Int, Int, String, String) -> Unit,
    modifier: Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Carte de Création (Desktop)")
    }
}
