package com.patrykandpatryk.liftapp.core.ui

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.thenIfNotNull
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme

@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    iconPainter: Painter? = null,
    onClick: (() -> Unit)? = null,
) {
    ListItem(
        title = title,
        modifier = modifier,
        description = description,
        icon = {
            if (iconPainter != null) {
                Icon(
                    painter = iconPainter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 24.dp),
                )
            }
        },
        onClick = onClick,
    )
}

@Composable
fun CheckableListItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ListItem(
        title = title,
        modifier = modifier,
        description = description,
        icon = {
            CompositionLocalProvider(LocalMinimumTouchTargetEnforcement provides false) {
                Checkbox(
                    modifier = Modifier
                        .padding(end = 24.dp),
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                )
            }
        },
        onClick = { onCheckedChange(checked.not()) },
    )
}

@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: @Composable RowScope.() -> Unit,
    onClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .thenIfNotNull(value = onClick) { clickable(onClick = it) }
            .padding(
                vertical = LocalDimens.current.padding.itemVertical,
                horizontal = LocalDimens.current.padding.contentHorizontal,
            ),
    ) {

        icon()

        Column {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTitleItem() {
    LiftAppTheme {
        Surface {
            ListItem(title = "This is a title")
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTitleWithDescItem() {
    LiftAppTheme {
        Surface {
            ListItem(
                title = "This is a title",
                description = "This is a description",
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTitleWithDescAndIconItem() {
    LiftAppTheme {
        Surface {
            ListItem(
                title = "This is a title",
                description = "This is a description",
                iconPainter = painterResource(id = R.drawable.ic_distance),
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTitleWithLongDescAndIconItem() {
    LiftAppTheme {
        Surface {
            ListItem(
                title = "This is a title",
                description = "This is a description \nwith two lines",
                iconPainter = painterResource(id = R.drawable.ic_distance),
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewTitleWithIconItem() {
    LiftAppTheme {
        Surface {
            ListItem(
                title = "This is a title",
                iconPainter = painterResource(id = R.drawable.ic_distance),
            )
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewCheckableListItem() {
    LiftAppTheme {
        Surface {
            val (checked, setChecked) = remember { mutableStateOf(true) }
            CheckableListItem(
                title = "This is a title",
                description = "This is a description",
                checked = checked,
                onCheckedChange = setChecked,
            )
        }
    }
}
