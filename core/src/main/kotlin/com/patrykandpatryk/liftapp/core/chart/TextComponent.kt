package com.patrykandpatryk.liftapp.core.chart

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.component.Component
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent

@Composable
fun rememberTextComponent(
    textStyle: TextStyle,
    textAlign: TextAlign = TextAlign.Start,
    lineCount: Int = 1,
    margins: Insets = Insets.Zero,
    padding: Insets = Insets.Zero,
    background: Component? = null,
    minWidth: TextComponent.MinWidth = TextComponent.MinWidth.fixed(),
): TextComponent {
    val fontFamilyResolver = LocalFontFamilyResolver.current

    val resolvedTypeface =
        remember(textStyle) { fontFamilyResolver.resolve(textStyle.fontFamily) }.value

    return rememberTextComponent(
        style =
            TextStyle(
                color = if (textStyle.color.isSpecified) textStyle.color else colorScheme.onSurface,
                fontFamily = FontFamily(resolvedTypeface as? Typeface ?: Typeface.DEFAULT),
                fontSize = textStyle.fontSize,
                textAlign = textAlign,
                lineHeight = textStyle.lineHeight,
            ),
        lineCount = lineCount,
        margins = margins,
        padding = padding,
        background = background,
        minWidth = minWidth,
    )
}
