package com.patrykandpatrick.liftapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.liftapp.ui.R

@OptIn(ExperimentalTextApi::class)
private fun variableFont(resourceId: Int, weight: FontWeight) =
    Font(
        resId = resourceId,
        weight = weight,
        variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
    )

private val googleSansFlexFontFamily =
    FontFamily(
        variableFont(R.font.google_sans_flex_variable, FontWeight.Normal),
        variableFont(R.font.google_sans_flex_variable, FontWeight.Medium),
        variableFont(R.font.google_sans_flex_variable, FontWeight.SemiBold),
        variableFont(R.font.google_sans_flex_variable, FontWeight.Bold),
        variableFont(R.font.google_sans_flex_variable, FontWeight.ExtraBold),
    )

private val interFontFamily =
    FontFamily(
        variableFont(R.font.inter_variable, FontWeight.Normal),
        variableFont(R.font.inter_variable, FontWeight.Medium),
        variableFont(R.font.inter_variable, FontWeight.SemiBold),
        variableFont(R.font.inter_variable, FontWeight.Bold),
        variableFont(R.font.inter_variable, FontWeight.ExtraBold),
    )

val LiftAppTypography =
    Typography().run {
        copy(
            headlineLarge =
                headlineLarge.copy(
                    fontFamily = googleSansFlexFontFamily,
                    fontWeight = FontWeight.Medium,
                ),
            headlineMedium =
                headlineMedium.copy(
                    fontFamily = googleSansFlexFontFamily,
                    fontWeight = FontWeight.Medium,
                ),
            headlineSmall =
                headlineSmall.copy(
                    fontFamily = googleSansFlexFontFamily,
                    fontWeight = FontWeight.Medium,
                ),
            displayLarge =
                displayLarge.copy(
                    fontFamily = googleSansFlexFontFamily,
                    fontWeight = FontWeight.Medium,
                ),
            displayMedium =
                displayMedium.copy(
                    fontFamily = googleSansFlexFontFamily,
                    fontWeight = FontWeight.Medium,
                ),
            displaySmall =
                displaySmall.copy(
                    fontFamily = googleSansFlexFontFamily,
                    fontWeight = FontWeight.Medium,
                ),
            titleLarge =
                titleLarge.copy(
                    fontFamily = googleSansFlexFontFamily,
                    fontWeight = FontWeight.Medium,
                ),
            titleMedium =
                titleMedium.copy(
                    fontFamily = googleSansFlexFontFamily,
                    fontWeight = FontWeight.Medium,
                ),
            titleSmall =
                titleSmall.copy(
                    fontFamily = googleSansFlexFontFamily,
                    fontWeight = FontWeight.Medium,
                ),
            bodyLarge =
                bodyLarge.copy(fontFamily = interFontFamily, fontWeight = FontWeight.Normal),
            bodyMedium =
                bodyMedium.copy(fontFamily = interFontFamily, fontWeight = FontWeight.Medium),
            bodySmall =
                bodySmall.copy(fontFamily = interFontFamily, fontWeight = FontWeight.SemiBold),
            labelLarge =
                labelLarge.copy(
                    fontFamily = interFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = .2.sp,
                ),
            labelMedium =
                labelMedium.copy(
                    fontFamily = interFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = .2.sp,
                ),
            labelSmall =
                labelSmall.copy(
                    fontFamily = interFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = .2.sp,
                ),
        )
    }
