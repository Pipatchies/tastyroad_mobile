package fr.tastyroad.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import tastyroad.app.shared.generated.resources.Res
import tastyroad.app.shared.generated.resources.libre_franklin_regular
import tastyroad.app.shared.generated.resources.nanum_myeongjo_regular
import tastyroad.app.shared.generated.resources.rubik_regular

@Composable
fun getTastyRoadTypography(): Typography {
    val titleFontFamily =
        FontFamily(
            Font(Res.font.nanum_myeongjo_regular, FontWeight.Normal),
        )

    val subtitleFontFamily =
        FontFamily(
            Font(Res.font.rubik_regular, FontWeight.Normal),
        )

    val bodyFontFamily =
        FontFamily(
            Font(Res.font.libre_franklin_regular, FontWeight.Normal),
        )

    return Typography(
        displayLarge = TextStyle(fontFamily = titleFontFamily, fontWeight = FontWeight.Normal, fontSize = 57.sp),
        headlineLarge = TextStyle(fontFamily = titleFontFamily, fontWeight = FontWeight.Normal, fontSize = 32.sp),
        titleLarge = TextStyle(fontFamily = titleFontFamily, fontWeight = FontWeight.Normal, fontSize = 22.sp),
        titleMedium = TextStyle(fontFamily = subtitleFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
        titleSmall = TextStyle(fontFamily = subtitleFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
        bodyLarge = TextStyle(fontFamily = bodyFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
        bodyMedium = TextStyle(fontFamily = bodyFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
        bodySmall = TextStyle(fontFamily = bodyFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
        labelLarge = TextStyle(fontFamily = bodyFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    )
}
