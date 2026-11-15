package fr.tastyroad.ui.components.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.tastyroad.ui.components.buttons.ButtonVariant
import fr.tastyroad.ui.components.buttons.TastyRoadButton

@Composable
actual fun ImagePickerButton(
    buttonText: String,
    onImagePicked: (ByteArray) -> Unit,
    modifier: Modifier,
) {
    TastyRoadButton(
        text = buttonText,
        onClick = {},
        variant = ButtonVariant.SECONDARY,
        modifier = modifier,
    )
}
