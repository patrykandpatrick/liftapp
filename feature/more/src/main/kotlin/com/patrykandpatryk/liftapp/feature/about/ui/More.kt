package com.patrykandpatryk.liftapp.feature.about.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.navigation.Routes

@Composable
fun More(
    modifier: Modifier,
    navigate: (String) -> Unit,
) {
    Column {
        Text(
            modifier = modifier
                .statusBarsPadding(),
            text = stringResource(id = R.string.route_more),
            style = MaterialTheme.typography.displayMedium,
        )
        Button(onClick = { navigate(Routes.Settings.value) }) {
            Text(text = stringResource(id = R.string.route_settings))
        }
        Button(onClick = { navigate(Routes.About.value) }) {
            Text(text = stringResource(id = R.string.route_about))
        }
        Button(onClick = { navigate(Routes.OneRepMax.value) }) {
            Text(text = stringResource(id = R.string.route_one_rep_max))
        }
    }
}
