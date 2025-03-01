package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme

@Composable
fun Info(
    text: String,
    modifier: Modifier = Modifier,
    buttons: (@Composable RowScope.() -> Unit)? = null,
) {
    val shape = RoundedCornerShape(12.dp)
    var size by remember { mutableStateOf(IntSize.Zero) }
    Column(
        verticalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.itemVerticalSmall),
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    brush =
                        Brush.radialGradient(
                            colors =
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = .1f),
                                    Color.Transparent,
                                ),
                            center = Offset(size.width * .1f, size.height * -1f),
                            radius = (size.width * .8f).coerceAtLeast(1f),
                        ),
                    shape = shape,
                )
                .border(
                    width = 1.dp,
                    brush =
                        Brush.radialGradient(
                            colors =
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.surfaceVariant,
                                ),
                            center = Offset(size.width * .1f, 0f),
                            radius = (size.width * .5f).coerceAtLeast(1f),
                        ),
                    shape = shape,
                )
                .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 8.dp)
                .onGloballyPositioned { layoutCoordinates -> size = layoutCoordinates.size },
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(space = 16.dp)) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        TextButton(onDismiss) { Text(text = stringResource(id = R.string.action_dismiss)) }
    }
}

@Composable
@LightAndDarkThemePreview
fun InfoPreview() {
    LiftAppTheme {
        Surface {
            Info(
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
            Info(
                text = stringResource(id = R.string.one_rep_max_description),
                modifier = Modifier.padding(16.dp),
                buttons = { InfoDefaults.DismissButton {} },
            )
        }
    }
}
