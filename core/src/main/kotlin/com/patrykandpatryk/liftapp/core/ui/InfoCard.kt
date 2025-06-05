package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R

@Composable
fun InfoCard(
    text: String,
    modifier: Modifier = Modifier,
    buttons: (@Composable RowScope.() -> Unit)? = null,
) {
    InfoCard(AnnotatedString(text), modifier, buttons)
}

@Composable
fun InfoCard(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    buttons: (@Composable RowScope.() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(12.dp)
    val colorScheme = colorScheme
    Column(
        verticalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.itemVerticalSmall),
        modifier =
            modifier
                .fillMaxWidth()
                .drawBehind() {
                    drawOutline(
                        outline = shape.createOutline(size, layoutDirection, this),
                        brush =
                            Brush.radialGradient(
                                colors =
                                    listOf(
                                        colorScheme.primary.copy(alpha = .1f),
                                        Color.Transparent,
                                    ),
                                center = Offset(size.width * .1f, size.height * -1f),
                                radius = (size.width * 1f).coerceAtLeast(1f),
                            ),
                    )
                    drawOutline(
                        outline = shape.createOutline(size, layoutDirection, this),
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        colorScheme.primary.copy(alpha = .05f),
                                        Color.Transparent,
                                    )
                            ),
                    )
                    drawOutline(
                        outline = shape.createOutline(size, layoutDirection, this),
                        brush =
                            Brush.radialGradient(
                                colors = listOf(colorScheme.primary, colorScheme.outline),
                                center = Offset(size.width * .1f, 0f),
                                radius = (size.width * .5f).coerceAtLeast(1f),
                            ),
                        style = Stroke(width = 1.dp.toPx()),
                    )
                    drawOutline(
                        outline = shape.createOutline(size, layoutDirection, this),
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        colorScheme.primary.copy(alpha = .4f),
                                        Color.Transparent,
                                    ),
                                endY = size.height / 2f,
                            ),
                        style = Stroke(width = 1.dp.toPx()),
                    )
                }
                .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 8.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(space = 16.dp)) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = colorScheme.onSurface,
            )

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterVertically),
            )
        }

        if (buttons != null) {
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(LocalDimens.current.padding.itemHorizontalSmall),
                modifier = Modifier.align(Alignment.End),
                content = buttons,
            )
        } else {
            Spacer(Modifier)
        }
    }
}

object InfoDefaults {
    @Composable
    fun DismissButton(onDismiss: () -> Unit) {
        PlainLiftAppButton(onDismiss) { Text(text = stringResource(id = R.string.action_dismiss)) }
    }
}

@Composable
@LightAndDarkThemePreview
fun InfoPreview() {
    LiftAppTheme {
        Surface {
            InfoCard(
                text = stringResource(id = R.string.one_rep_max_description),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
@LightAndDarkThemePreview
fun InfoWithButtonPreview() {
    LiftAppTheme {
        Surface {
            InfoCard(
                text = stringResource(id = R.string.one_rep_max_description),
                modifier = Modifier.padding(16.dp),
                buttons = { InfoDefaults.DismissButton {} },
            )
        }
    }
}
