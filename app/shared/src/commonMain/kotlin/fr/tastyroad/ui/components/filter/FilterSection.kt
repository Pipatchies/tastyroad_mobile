package fr.tastyroad.ui.components.filter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class FilterItem(
    val name: String,
    val options: List<String>,
    val selectedOption: String?,
    val onSelected: (String?) -> Unit,
)

@Composable
fun FilterSection(
    modifier: Modifier = Modifier,
    filterState: FilterState = FilterState(),
    onFilterChanged: (FilterState) -> Unit = {},
) {
    val filters =
        listOf(
            FilterItem(
                name = "Type",
                options = listOf("Rando", "Vélo", "Moto"),
                selectedOption = filterState.selectedType,
                onSelected = { opt -> onFilterChanged(filterState.copy(selectedType = opt)) },
            ),
            FilterItem(
                name = "Zone",
                options =
                    listOf(
                        "Nord",
                        "Occitanie",
                        "PACA",
                        "Île-de-France",
                        "Bretagne",
                        "Rhône-Alpes",
                        "Nouvelle-Aquitaine",
                        "Grand Est",
                        "Bourgogne",
                        "Corse",
                    ),
                selectedOption = filterState.selectedZone,
                onSelected = { opt -> onFilterChanged(filterState.copy(selectedZone = opt)) },
            ),
            FilterItem(
                name = "Distance",
                options = listOf("< 10 km", "10 km - 20 km", "20 km - 50 km", "50 km - 100 km", "100 km >"),
                selectedOption = filterState.selectedDistance,
                onSelected = { opt -> onFilterChanged(filterState.copy(selectedDistance = opt)) },
            ),
            FilterItem(
                name = "Gastronomie",
                options = listOf("Viande", "Poisson", "Fromage", "Vignoble", "Végétarien"),
                selectedOption = filterState.selectedGastronomy,
                onSelected = { opt -> onFilterChanged(filterState.copy(selectedGastronomy = opt)) },
            ),
            FilterItem(
                name = "Difficulté",
                options = listOf("Facile", "Moyenne", "Difficile"),
                selectedOption = filterState.selectedDifficulty,
                onSelected = { opt -> onFilterChanged(filterState.copy(selectedDifficulty = opt)) },
            ),
        )

    LazyRow(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (filterState.isActive) {
            item {
                Surface(
                    onClick = { onFilterChanged(FilterState()) },
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Réinitialiser",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Effacer",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
        }

        items(filters) { filter ->
            FilterChipDropdown(
                filterName = filter.name,
                options = filter.options,
                selectedOption = filter.selectedOption,
                onOptionSelected = filter.onSelected,
            )
        }
    }
}
