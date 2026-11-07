package fr.tastyroad.ui.components.header

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class CurvedBottomShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height - 50f)

            quadraticBezierTo(
                x1 = size.width / 2f, y1 = size.height,
                x2 = 0f, y2 = size.height - 50f
            )

            close()
        }
        return Outline.Generic(path)
    }
}