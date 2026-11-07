package fr.tastyroad.ui.components.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class Filter(
    val name: String,
    val options: List<String>
)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterSection() {
    val filters = listOf(
        Filter("Type", listOf("Rando", "Vélo", "Moto")),
        Filter("Zone", listOf("Nord", "Occitanie", "PACA")),
        Filter(
            "Distance",
            listOf("< 10 km", "10 km - 20km", "20 km - 50 km", "50 km - 100 km", "100 km >")
        ),
        Filter(
            "Gastronomie",
            listOf("Viande", "Poisson", "Fromage", "Vignoble", "Végétarien")
        ),
        Filter("Difficulté", listOf("Facile", "Moyenne", "Difficile"))
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        items(filters) { filter ->
            FilterChipDropdown(
                filterName = filter.name,
                options = filter.options,
                onOptionSelected = { selectedOption ->
                    println("Filtre choisi: '$selectedOption' dans '${filter.name}'")
                }
            )
        }
    }
}