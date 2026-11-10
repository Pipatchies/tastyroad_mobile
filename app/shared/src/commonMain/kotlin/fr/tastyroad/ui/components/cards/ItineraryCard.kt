package fr.tastyroad.ui.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import fr.tastyroad.domain.ItineraryDisplayItem

@Composable
fun ItineraryCard(
    item: ItineraryDisplayItem,
    modifier: Modifier = Modifier,
) {
    val itinerary = item.itinerary

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(horizontal = 24.dp, vertical = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = itinerary.title,
                contentScale = ContentScale.Crop,
                alpha = 0.85f,
                modifier = Modifier.fillMaxSize(),
            )

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(50),
                modifier =
                    Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart),
            ) {
                Text(
                    text = itinerary.difficulty ?: "Inconnu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(50),
                modifier =
                    Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Note",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.rating.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(8.dp),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = itinerary.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            itinerary.type?.let { BadgeItem(it) }
                            itinerary.diet?.let { BadgeItem(it) }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Lieu",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = itinerary.zone ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        StatItem(value = "${itinerary.distance?.toInt() ?: 0}", label = "km")

                        val durationStr = itinerary.duration ?: "00:00:00"
                        val formattedDuration =
                            if (durationStr.contains("H") || durationStr.contains("M")) {
                                val hoursMatch = Regex("""(\d+)H""").find(durationStr)
                                val minsMatch = Regex("""(\d+)M""").find(durationStr)
                                val hours = hoursMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
                                val mins = minsMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
                                "${hours.toString().padStart(2, '0')}:${mins.toString().padStart(2, '0')}"
                            } else {
                                val parts = durationStr.split(":")
                                if (parts.size >= 2) {
                                    "${parts[0].padStart(2, '0')}:${parts[1].padStart(2, '0')}"
                                } else {
                                    "00:00"
                                }
                            }

                        StatItem(value = formattedDuration, label = "durée")
                        StatItem(value = "${item.stepsCount}", label = "étapes")
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeItem(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(50),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
        )
    }
}
