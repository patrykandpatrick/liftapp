package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.thenIfNotNull
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens

@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    iconPainter: Painter? = null,
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

        if (iconPainter != null) {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 24.dp),
            )
        }

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
@Composable
fun PreviewTitleItem() {
    ListItem(title = "This is a title")
}

@Preview
@Composable
fun PreviewTitleWithDescItem() {
    ListItem(
        title = "This is a title",
        description = "This is a description",
    )
}

@Preview
@Composable
fun PreviewTitleWithDescAndIconItem() {
    ListItem(
        title = "This is a title",
        description = "This is a description",
        iconPainter = painterResource(id = R.drawable.ic_distance),
    )
}

@Preview
@Composable
fun PreviewTitleWithLongDescAndIconItem() {
    ListItem(
        title = "This is a title",
        description = "This is a description \nwith two lines",
        iconPainter = painterResource(id = R.drawable.ic_distance),
    )
}

@Preview
@Composable
fun PreviewTitleWithIconItem() {
    ListItem(
        title = "This is a title",
        iconPainter = painterResource(id = R.drawable.ic_distance),
    )
}
