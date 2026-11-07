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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.tastyroad.domain.MockData
import fr.tastyroad.ui.components.buttons.TastyRoadButton
import fr.tastyroad.ui.components.cards.ItineraryCard
import fr.tastyroad.ui.components.filter.FilterSection
import fr.tastyroad.ui.components.header.Header

@Composable
fun HomeScreen(onMapClick: () -> Unit = {}) {
    val scrollState = rememberScrollState()

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
            ) {
                FilterSection()

                Text(
                    text = "A LA UNE",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                )

                MockData.sampleItineraries.forEach { item ->
                    ItineraryCard(item = item)
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
