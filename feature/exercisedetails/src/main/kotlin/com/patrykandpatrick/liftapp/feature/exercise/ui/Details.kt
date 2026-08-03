package com.patrykandpatrick.liftapp.feature.exercise.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.ui.image.MuscleImage
import com.patrykandpatrick.liftapp.core.ui.image.MuscleLegendItem
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.feature.exercise.model.ScreenState
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens

@Composable
internal fun Details(modifier: Modifier = Modifier) {
    val viewModel: ExerciseDetailsViewModel = hiltViewModel()

    val state by viewModel.screenState.collectAsState()

    Details(modifier = modifier, loadableScreenState = state)
}

@Composable
private fun Details(modifier: Modifier = Modifier, loadableScreenState: Loadable<ScreenState>) {
    val dimens = LocalDimens.current
    val muscleDimens = dimens.muscle

    loadableScreenState.Unfold { state ->
        LazyVerticalGrid(
            modifier = modifier.fillMaxSize(),
            columns = GridCells.Adaptive(minSize = muscleDimens.gridCellMinSize),
            contentPadding =
                PaddingValues(
                    start = dimens.screen.padding,
                    top = dimens.screen.padding,
                    end = dimens.screen.padding,
                    bottom = dimens.screen.padding,
                ),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "image", span = { GridItemSpan(maxLineSpan) }) {
                MuscleImage(
                    model = state,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }

            items(items = state.muscles, key = { it.muscle }) { muscleModel ->
                MuscleLegendItem(
                    muscleModel = muscleModel,
                    modifier = Modifier.padding(top = 24.dp),
                )
            }
        }
    }
}
