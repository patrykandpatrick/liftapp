package com.patrykandpatryk.liftapp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.patrykandpatryk.liftapp.core.R

private val lexendFontFamily = FontFamily(
    Font(R.font.lexend_regular, FontWeight.Normal),
    Font(R.font.lexend_medium, FontWeight.Medium),
    Font(R.font.lexend_semibold, FontWeight.SemiBold),
    Font(R.font.lexend_bold, FontWeight.Bold),
)

private val manropeFontFamily = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
)

val LiftAppTypography = Typography().run {
    copy(
        headlineLarge = headlineLarge.copy(fontFamily = lexendFontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = lexendFontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = lexendFontFamily),
        displayLarge = displayLarge.copy(fontFamily = lexendFontFamily),
        displayMedium = displayMedium.copy(fontFamily = lexendFontFamily),
        displaySmall = displaySmall.copy(fontFamily = lexendFontFamily),
        titleLarge = titleLarge.copy(fontFamily = lexendFontFamily),
        titleMedium = titleMedium.copy(fontFamily = lexendFontFamily),
        titleSmall = titleSmall.copy(fontFamily = lexendFontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = manropeFontFamily, fontWeight = FontWeight.Medium),
        bodyMedium = bodyMedium.copy(fontFamily = manropeFontFamily, fontWeight = FontWeight.Medium),
        bodySmall = bodySmall.copy(fontFamily = manropeFontFamily, fontWeight = FontWeight.Medium),
        labelLarge = labelLarge.copy(fontFamily = manropeFontFamily, fontWeight = FontWeight.Bold),
        labelMedium = labelMedium.copy(fontFamily = manropeFontFamily, fontWeight = FontWeight.Bold),
        labelSmall = labelSmall.copy(fontFamily = manropeFontFamily, fontWeight = FontWeight.Bold),
    )
}
