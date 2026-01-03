package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import com.patrykandpatrick.liftapp.ui.component.TextComponent.BULLET_SEPARATOR
import com.patrykandpatrick.liftapp.ui.component.TextComponent.COMPLETED_ICON
import com.patrykandpatrick.liftapp.ui.component.TextComponent.separatorBullet
import com.patrykandpatrick.liftapp.ui.icons.BadgeCheck
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun LiftAppText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    autoSize: TextAutoSize? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = LiftAppTextDefaults.inlineContent,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    Text(
        text,
        modifier,
        color,
        autoSize,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        minLines,
        inlineContent,
        onTextLayout,
        style,
    )
}

@Composable
fun LiftAppText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    autoSize: TextAutoSize? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    Text(
        text,
        modifier,
        color,
        autoSize,
        fontSize,
        fontStyle,
        fontWeight,
        fontFamily,
        letterSpacing,
        textDecoration,
        textAlign,
        lineHeight,
        overflow,
        softWrap,
        maxLines,
        minLines,
        onTextLayout,
        style,
    )
}

object LiftAppTextDefaults {
    val inlineContent =
        mapOf(
            BULLET_SEPARATOR to
                InlineTextContent(
                    placeholder =
                        Placeholder(
                            width =
                                (separatorBullet.width.value + separatorBullet.padding.value * 2)
                                    .em,
                            height = separatorBullet.height,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                        )
                ) {
                    BulletSeparator()
                },
            COMPLETED_ICON to
                InlineTextContent(
                    Placeholder(
                        width = 2.em,
                        height = 1.2.em,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                    )
                ) {
                    CompletedIcon()
                },
        )
}

@Composable
private fun BulletSeparator(modifier: Modifier = Modifier) {
    val contentColor = LocalContentColor.current
    Canvas(modifier.fillMaxSize()) {
        val x = (size.width - size.height) / 2
        translate(left = x.coerceAtLeast(0f)) {
            drawOutline(
                outline =
                    separatorBullet.shape.createOutline(
                        size.copy(width = size.width.coerceAtMost(size.height)),
                        layoutDirection,
                        this,
                    ),
                color = contentColor,
            )
        }
    }
}

@Composable
private fun CompletedIcon(modifier: Modifier = Modifier) {
    val backgroundColor = colorScheme.secondary
    Icon(
        imageVector = LiftAppIcons.BadgeCheck,
        contentDescription = null,
        tint = colorScheme.secondary,
        modifier =
            modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val size = minOf(placeable.width, placeable.height)
                layout(placeable.width, placeable.height) {
                    placeable.place(
                        (constraints.maxWidth - size) / 2,
                        (constraints.maxHeight - size) / 2,
                    )
                }
            },
    )
}
