package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme

@Composable
fun Error(
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.generic_error_title),
    message: String? = null,
    onCloseClick: (() -> Unit)? = null,
) {
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

        if (onCloseClick != null) {
            Spacer(Modifier.height(LocalDimens.current.padding.itemVertical))
            Button(onCloseClick) { Text(text = stringResource(R.string.action_close)) }
        }
    }
}

@Composable
@LightAndDarkThemePreview
private fun ErrorPreview() {
    LiftAppTheme { Surface { Error(message = "An error occurred", onCloseClick = {}) } }
}
