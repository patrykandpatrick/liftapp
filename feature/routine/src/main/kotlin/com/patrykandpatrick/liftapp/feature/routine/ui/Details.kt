package com.patrykandpatrick.liftapp.feature.routine.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.ui.ListItem
import com.patrykandpatrick.liftapp.core.ui.image.MuscleImage
import com.patrykandpatrick.liftapp.core.ui.resource.color
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.feature.routine.model.ScreenState
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens

@Composable
internal fun Details(
    loadableState: Loadable<ScreenState>,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    loadableState.Unfold { state ->
        Details(state = state, bottomPadding = bottomPadding, modifier = modifier)
    }
}

@Composable
private fun Details(state: ScreenState, bottomPadding: Dp, modifier: Modifier = Modifier) {
    val dimens = LocalDimens.current
    val muscleDimens = dimens.muscle

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(minSize = muscleDimens.gridCellMinSize),
        contentPadding =
            PaddingValues(
                start = dimens.screen.horizontalPadding,
                top = dimens.screen.verticalPadding,
                end = dimens.screen.horizontalPadding,
                bottom = dimens.screen.verticalPadding + bottomPadding,
            ),
        horizontalArrangement = Arrangement.spacedBy(muscleDimens.listItemHorizontalMargin),
    ) {
        item(key = "image", span = { GridItemSpan(maxLineSpan) }) {
            MuscleImage(
                model = state,
                modifier = Modifier.padding(vertical = dimens.screen.verticalPadding),
            )
        }

        items(items = state.muscles, key = { it.muscle }) { muscleModel ->
            ListItem(
                title = { Text(stringResource(id = muscleModel.nameRes)) },
                description = { Text(stringResource(id = muscleModel.type.nameRes)) },
                icon = {
                    Box(
                        modifier =
                            Modifier.size(muscleDimens.tileSize)
                                .background(
                                    color = muscleModel.type.color,
                                    shape = RoundedCornerShape(muscleDimens.tileCornerSize),
                                )
                    )
                },
                paddingValues = PaddingValues(vertical = 16.dp, horizontal = 0.dp),
            )
        }
    }
}
