package com.patrykandpatryk.liftapp.core.ui.error

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R

@Composable
fun Error(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.generic_error_title),
    message: String? = null,
) {
    val viewModel: ErrorViewModel = hiltViewModel()
    Error(
        modifier = modifier,
        title = title,
        onCloseClick = { viewModel.onAction(Action.PopBackStack) },
        message = message,
    )
}

@Composable
fun Error(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.generic_error_title),
    onCloseClick: () -> Unit,
    message: String? = null,
) {
    CompositionLocalProvider(LocalContentColor provides colorScheme.onSurface) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize(),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_error),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
            Spacer(Modifier.height(LocalDimens.current.padding.itemVerticalSmall))
            Text(text = title, style = MaterialTheme.typography.headlineSmall)

            if (message != null) {
                Spacer(Modifier.height(4.dp))
                Text(text = message, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(Modifier.height(LocalDimens.current.padding.itemVertical))
            LiftAppButton(onCloseClick) { Text(text = stringResource(R.string.action_close)) }
        }
    }
}

@Composable
@LightAndDarkThemePreview
private fun ErrorPreview() {
    LiftAppTheme {
        Error(
            message = "An error occurred",
            onCloseClick = {},
            modifier = Modifier.background(colorScheme.background),
        )
    }
}
