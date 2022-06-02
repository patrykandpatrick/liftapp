package com.patrykandpatryk.liftapp.feature.about.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.patrykandpatryk.liftapp.core.R

@Composable
fun About(
    parentNavController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize(),
        text = stringResource(id = R.string.route_about),
        style = MaterialTheme.typography.displayMedium,
    )
}
