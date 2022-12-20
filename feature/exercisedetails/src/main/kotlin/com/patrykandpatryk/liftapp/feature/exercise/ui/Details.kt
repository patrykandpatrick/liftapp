package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.feature.exercise.model.ScreenState

@Composable
internal fun Details(
    modifier: Modifier = Modifier,
) {

    val viewModel: ExerciseViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()

    Details(
        modifier = modifier,
        state = state,
    )
}

@Composable
private fun Details(
    modifier: Modifier = Modifier,
    state: ScreenState,
) {

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {

        AsyncImage(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .padding(LocalDimens.current.padding.contentHorizontal),
            model = state.imagePath,
            contentDescription = null,
        )
    }
}
