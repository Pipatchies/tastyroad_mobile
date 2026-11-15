package fr.tastyroad.ui.components.picker

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import fr.tastyroad.ui.components.buttons.ButtonVariant
import fr.tastyroad.ui.components.buttons.TastyRoadButton

@Composable
actual fun ImagePickerButton(
    buttonText: String,
    onImagePicked: (ByteArray) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri ->
            if (uri != null) {
                try {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    if (bytes != null && bytes.isNotEmpty()) {
                        onImagePicked(bytes)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    TastyRoadButton(
        text = buttonText,
        onClick = { launcher.launch("image/*") },
        variant = ButtonVariant.SECONDARY,
        modifier = modifier,
    )
}
