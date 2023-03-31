package com.patrykandpatryk.liftapp.core.ui.indicator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.LocalContentColor
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.shape.FlowerShape
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.theme.PillShape

@Composable
fun RoundBackgroundWithBadge(
    content: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    badgePainter: Painter = painterResource(id = R.drawable.ic_check),
    outlined: Boolean = false,
    showBadge: Boolean = true,
) {

    Box(
        modifier = modifier,
    ) {

        val shape = FlowerShape(
            waveHeight = 1.dp,
            waveCount = 10,
        )

        Box(
            modifier = Modifier
                .padding(all = 2.dp)
                .size(MaterialTheme.dimens.list.itemIconBackgroundSize)
                .then(
                    if (outlined) {
                        Modifier
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline,
                                shape = shape,
                            )
                    } else {
                        Modifier.background(
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(elevation = 4.dp),
                            shape = shape,
                        )
                    }
                ),
        ) {
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurface,
                LocalTextStyle provides MaterialTheme.typography.labelLarge,
            ) {
                content()
            }
        }

        AnimatedVisibility(
            visible = showBadge,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(2.dp),
        ) {
            Icon(
                painter = badgePainter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .size(16.dp)
                    .shadow(elevation = 2.dp, shape = PillShape)
                    .background(
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = PillShape
                    )
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun FilledRoundBackgroundWithBadgePreview() {
    LiftAppTheme {
        Surface {
            RoundBackgroundWithBadge(
                content = {
                    Text(
                        text = "1",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun OutlinedRoundBackgroundWithBadgePreview() {
    LiftAppTheme {
        Surface {
            RoundBackgroundWithBadge(
                content = {
                    Text(
                        text = "1",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.align(Alignment.Center),
                    )
                },
                outlined = true,
            )
        }
    }
}
