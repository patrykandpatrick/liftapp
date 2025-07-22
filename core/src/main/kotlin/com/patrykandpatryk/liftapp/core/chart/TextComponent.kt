package com.patrykandpatryk.liftapp.core.chart

import android.graphics.Typeface
import android.text.Layout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.component.Component
import com.patrykandpatrick.vico.core.common.component.TextComponent

@Composable
fun rememberTextComponent(
    textStyle: TextStyle,
    textAlignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL,
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
        color = if (textStyle.color.isSpecified) textStyle.color else colorScheme.onSurface,
        typeface = resolvedTypeface as? Typeface ?: Typeface.DEFAULT,
        textSize = textStyle.fontSize,
        textAlignment = textAlignment,
        lineHeight = textStyle.lineHeight,
        lineCount = lineCount,
        margins = margins,
        padding = padding,
        background = background,
        minWidth = minWidth,
    )
}
