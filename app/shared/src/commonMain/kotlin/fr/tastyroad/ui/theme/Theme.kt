package fr.tastyroad.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val TastyRoadLightColorScheme =
    lightColorScheme(
        primary = OrangeFonce,
        secondary = VertSapin,
        background = BlancCasse,
        surface = BlancCasse,
        onPrimary = BlancCasse,
        onSecondary = BlancCasse,
        onBackground = GrisAnthracite,
        onSurface = GrisAnthracite,
    )

@Composable
fun TastyRoadTheme(content: @Composable () -> Unit) {
    val typography = getTastyRoadTypography()

    MaterialTheme(
        colorScheme = TastyRoadLightColorScheme,
        typography = typography,
        content = content,
    )
}
