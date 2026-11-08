package fr.tastyroad.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
}

@Composable
fun TastyRoadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    icon: ImageVector? = null,
) {
    val shape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    when (variant) {
        ButtonVariant.PRIMARY -> {
            // Au clic (isPressed), on inverse les couleurs (le contraire)
            val containerCol = if (isPressed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
            val contentCol = if (isPressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary

            Button(
                onClick = onClick,
                shape = shape,
                interactionSource = interactionSource,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = containerCol,
                        contentColor = contentCol,
                    ),
                border = if (isPressed) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                modifier = modifier,
            ) {
                ButtonContent(text = text, icon = icon, contentColor = contentCol)
            }
        }
        ButtonVariant.SECONDARY -> {
            // Au clic (isPressed), on inverse les couleurs (le contraire)
            val containerCol = if (isPressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val contentCol = if (isPressed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground

            OutlinedButton(
                onClick = onClick,
                shape = shape,
                interactionSource = interactionSource,
                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = containerCol,
                        contentColor = contentCol,
                    ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                modifier = modifier,
            ) {
                ButtonContent(text = text, icon = icon, contentColor = contentCol)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    icon: ImageVector?,
    contentColor: Color,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
