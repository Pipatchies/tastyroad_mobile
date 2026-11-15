package fr.tastyroad.ui.components.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ImagePickerButton(
    buttonText: String = "Choisir une photo",
    onImagePicked: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
)
