package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens

@Composable
fun TopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        title = { Text(text = title) },
        actions = actions,
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {

                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = stringResource(id = R.string.action_close),
                    )
                }
            }
        },
    )
}

@Composable
fun DialogTopBar(
    title: String,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Row(modifier = modifier) {

        Text(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
                .padding(horizontal = LocalDimens.current.padding.contentHorizontal),
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )

        IconButton(
            modifier = Modifier
                .padding(end = LocalDimens.current.padding.contentHorizontalSmall),
            onClick = onCloseClick,
        ) {

            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                imageVector = Icons.Outlined.Close,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = stringResource(id = R.string.action_close),
            )
        }
    }
}

@Preview
@Composable
fun PreviewDialogTopBar() {
    Surface {
        DialogTopBar(
            title = "Title",
            onCloseClick = {},
        )
    }
}
