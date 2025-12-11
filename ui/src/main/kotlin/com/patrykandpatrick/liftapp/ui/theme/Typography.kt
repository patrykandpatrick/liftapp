package com.patrykandpatrick.liftapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.liftapp.ui.R

private val lexendFontFamily =
    FontFamily(
        Font(R.font.lexend_regular, FontWeight.Normal),
        Font(R.font.lexend_medium, FontWeight.Medium),
        Font(R.font.lexend_semibold, FontWeight.SemiBold),
        Font(R.font.lexend_bold, FontWeight.Bold),
    )

private val manropeFontFamily =
    FontFamily(
        Font(R.font.manrope_regular, FontWeight.Normal),
        Font(R.font.manrope_medium, FontWeight.Medium),
        Font(R.font.manrope_semibold, FontWeight.SemiBold),
        Font(R.font.manrope_bold, FontWeight.Bold),
    )

private val martianMonoFamily =
    FontFamily(
        Font(R.font.martian_mono_regular, FontWeight.Normal),
        Font(R.font.martian_mono_semi_bold, FontWeight.SemiBold),
        Font(R.font.martian_mono_bold, FontWeight.Bold),
    )

val LiftAppTypography =
    Typography().run {
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
            bodyLarge =
                bodyLarge.copy(fontFamily = manropeFontFamily, fontWeight = FontWeight.Medium),
            bodyMedium =
                bodyMedium.copy(fontFamily = manropeFontFamily, fontWeight = FontWeight.SemiBold),
            bodySmall =
                bodySmall.copy(fontFamily = manropeFontFamily, fontWeight = FontWeight.Bold),
            labelLarge =
                labelLarge.copy(
                    fontFamily = manropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = .2.sp,
                ),
            labelMedium =
                labelMedium.copy(
                    fontFamily = manropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = .2.sp,
                ),
            labelSmall =
                labelSmall.copy(
                    fontFamily = manropeFontFamily,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = .2.sp,
                ),
        )
    }

object Typography {
    val titleLargeMono =
        TextStyle(
            fontFamily = martianMonoFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
        )

    val titleMediumMono =
        TextStyle(
            fontFamily = martianMonoFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )

    val titleSmallMono =
        TextStyle(fontFamily = martianMonoFamily, fontWeight = FontWeight.Bold, fontSize = 14.sp)

    val bodySmallMono =
        TextStyle(
            fontFamily = martianMonoFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        )
}
