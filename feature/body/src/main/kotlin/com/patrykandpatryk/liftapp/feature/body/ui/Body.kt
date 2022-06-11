package com.patrykandpatryk.liftapp.feature.body.ui

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R

@Composable
fun Body(modifier: Modifier) {
    Text(
        modifier = modifier.statusBarsPadding(),
        text = stringResource(id = R.string.route_body),
        style = MaterialTheme.typography.displayMedium,
    )
}
