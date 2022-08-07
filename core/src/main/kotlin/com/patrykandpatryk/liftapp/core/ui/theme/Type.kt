package com.patrykandpatryk.liftapp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle

private val platformTextStyle = PlatformTextStyle(
    includeFontPadding = false,
)

val Typography = Typography().run {
    copy(
     displayLarge = displayLarge.copy(platformStyle = platformTextStyle),
     displayMedium = displayMedium.copy(platformStyle = platformTextStyle),
     displaySmall = displaySmall.copy(platformStyle = platformTextStyle),
     headlineLarge = headlineLarge.copy(platformStyle = platformTextStyle),
     headlineMedium = headlineMedium.copy(platformStyle = platformTextStyle),
     headlineSmall = headlineSmall.copy(platformStyle = platformTextStyle),
     titleLarge = titleLarge.copy(platformStyle = platformTextStyle),
     titleMedium = titleMedium.copy(platformStyle = platformTextStyle),
    )
}
