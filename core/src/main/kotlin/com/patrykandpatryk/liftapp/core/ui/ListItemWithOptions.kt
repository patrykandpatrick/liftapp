package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme

@Composable
fun ListItemWithOptions(
    mainContent: @Composable () -> Unit,
    isExpanded: Boolean,
    setExpanded: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    optionItems: List<OptionItem> = emptyList(),
) {

    val dimens = LocalDimens.current

    val elevation by animateDpAsState(
        targetValue = if (isExpanded) dimens.card.smallElevation else 0.dp,
    )

    val margins by animateDpAsState(
        targetValue = if (isExpanded) dimens.padding.contentHorizontalSmall else 0.dp,
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (isExpanded) dimens.card.smallCornerRadius else 0.dp,
    )

    val shape = RoundedCornerShape(cornerRadius)

    Column(
        modifier = modifier
            .padding(horizontal = margins)
            .background(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    elevation = elevation,
                ),
                shape = shape,
            )
            .clip(shape)
            .clickable { setExpanded(!isExpanded) },
    ) {

        mainContent()

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandHorizontally(),
            exit = fadeOut() + shrinkHorizontally(),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Divider()
        }

        AnimatedVisibility(visible = isExpanded) {

            Row {

                optionItems.forEach { (iconPainter, label, onClick) ->

                    Option(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(onClick = onClick)
                            .padding(vertical = MaterialTheme.dimens.padding.contentVerticalSmall),
                        iconPainter = iconPainter,
                        label = label,
                    )
                }
            }
        }
    }
}

@Composable
fun Option(
    modifier: Modifier = Modifier,
    iconPainter: Painter?,
    label: String? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.supportingTextVertical),
    ) {

        if (iconPainter != null) {
            Icon(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                painter = iconPainter,
                contentDescription = label,
            )
        }

        if (label != null) {

            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = label,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Stable
data class OptionItem(
    val iconPainter: Painter,
    val label: String,
    val onClick: () -> Unit,
)

@LightAndDarkThemePreview
@Composable
fun ListItemWithOptionsPreview() {
    LiftAppTheme {

        val (isExpanded, setExpanded) = remember { mutableStateOf(true) }

        Surface {
            ListItemWithOptions(
                modifier = Modifier.padding(vertical = 8.dp),
                mainContent = {
                    ListItem(
                        title = "This is a title",
                        description = "This is a description",
                    )
                },
                isExpanded = isExpanded,
                setExpanded = setExpanded,
                optionItems = listOf(
                    OptionItem(
                        iconPainter = painterResource(id = R.drawable.ic_settings),
                        label = stringResource(id = R.string.route_settings),
                        onClick = {},
                    ),
                    OptionItem(
                        iconPainter = painterResource(id = R.drawable.ic_edit),
                        label = stringResource(id = R.string.action_edit),
                        onClick = {},
                    ),
                    OptionItem(
                        iconPainter = painterResource(id = R.drawable.ic_delete),
                        label = stringResource(id = R.string.action_delete),
                        onClick = {},
                    ),
                ),
            )
        }
    }
}
