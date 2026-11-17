package fr.tastyroad.ui.components.filter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FilterChipDropdown(
    filterName: String,
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val isSelected = selectedOption != null

    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
    val borderStroke = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)

    val displayText = if (selectedOption != null) "$filterName: $selectedOption" else filterName

    Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
        Surface(
            onClick = { expanded = !expanded },
            shape = RoundedCornerShape(16.dp),
            color = containerColor,
            border = borderStroke,
            modifier = Modifier.clickable { expanded = !expanded },
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = contentColor,
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Fermer" else "Ouvrir",
                    modifier = Modifier.padding(start = 4.dp),
                    tint = contentColor,
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            border = borderStroke,
        ) {
            if (isSelected) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Tous ($filterName)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    },
                    onClick = {
                        onOptionSelected(null)
                        expanded = false
                    },
                )
            }

            options.forEach { option ->
                val optionIsSelected = option == selectedOption
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (optionIsSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (optionIsSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f),
                            )
                            if (optionIsSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Sélectionné",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 8.dp),
                                )
                            }
                        }
                    },
                    onClick = {
                        if (optionIsSelected) {
                            onOptionSelected(null)
                        } else {
                            onOptionSelected(option)
                        }
                        expanded = false
                    },
                )
            }
        }
    }
}
