package fr.tastyroad.ui.components.header

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

class CurvedBottomShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val curveOffset = with(density) { 24.dp.toPx() }
        val path =
            Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height - curveOffset)

                quadraticBezierTo(
                    x1 = size.width / 2f,
                    y1 = size.height,
                    x2 = 0f,
                    y2 = size.height - curveOffset,
                )

                close()
            }
        return Outline.Generic(path)
    }
}
