package com.patrykandpatrick.liftapp.core.ui.error

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.TriangleAlert
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

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
    CompositionLocalProvider(LocalContentColor provides colorScheme.foreground) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize(),
        ) {
            Icon(
                imageVector = LiftAppIcons.TriangleAlert,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.headlineSmall)

            if (message != null) {
                Spacer(Modifier.height(4.dp))
                Text(text = message, style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(Modifier.height(16.dp))
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
