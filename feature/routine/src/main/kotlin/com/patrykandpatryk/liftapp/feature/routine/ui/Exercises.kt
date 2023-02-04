package com.patrykandpatryk.liftapp.feature.routine.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.thenIf
import com.patrykandpatryk.liftapp.core.gestures.onItemYRange
import com.patrykandpatryk.liftapp.core.gestures.rememberItemRanges
import com.patrykandpatryk.liftapp.core.gestures.reorderable
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.provider.navigator
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.ui.resource.iconRes
import com.patrykandpatryk.liftapp.domain.extension.addOrSet
import com.patrykandpatryk.liftapp.feature.routine.model.Intent
import com.patrykandpatryk.liftapp.feature.routine.model.ScreenState
import kotlin.math.roundToInt

@Composable
internal fun Exercises(
    modifier: Modifier = Modifier,
) {

    val viewModel: RoutineViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()

    Exercises(
        state = state,
        onIntent = viewModel::handleIntent,
        modifier = modifier,
    )
}

@Composable
private fun Exercises(
    state: ScreenState,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier,
) {

    val navigator = navigator

    val dragElevation = MaterialTheme.dimens.elevation.dragElevation

    val itemRanges = rememberItemRanges()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {

        itemsIndexed(
            items = state.exercises,
            key = { _, exercise -> exercise.id },
        ) { index, exercise ->

            val lastDragDelta = remember { mutableStateOf(0f) }
            var isDragging by remember { mutableStateOf(false) }
            var yOffset by remember(index) { mutableStateOf(0f + lastDragDelta.value) }

            val elevation by animateDpAsState(targetValue = if (isDragging) dragElevation else 0.dp)

            ListItem(
                modifier = Modifier
                    .thenIf(isDragging.not()) { animateItemPlacement() }
                    .offset { IntOffset(x = 0, y = yOffset.roundToInt()) }
                    .shadow(elevation)
                    .zIndex(if (isDragging) 1f else 0f)
                    .background(color = MaterialTheme.colorScheme.surface)
                    .onItemYRange { yRange -> itemRanges.addOrSet(index, yRange) },
                iconPainter = painterResource(id = exercise.type.iconRes),
                title = exercise.name,
                description = exercise.prettyGoal + "\n" + exercise.muscles,
                actions = {
                    Icon(
                        modifier = Modifier
                            .reorderable(
                                itemIndex = index,
                                itemYOffset = yOffset,
                                itemRanges = itemRanges,
                                onIsDragging = { isDragging = it },
                                onDelta = { delta ->
                                    yOffset += delta
                                    lastDragDelta.value = delta
                                },
                                onItemReordered = { from, to -> onIntent(Intent.Reorder(from = from, to = to)) },
                            ),
                        painter = painterResource(id = R.drawable.ic_drag_handle),
                        contentDescription = null,
                    )
                },
            ) {
                navigator.navigate(Routes.Exercise.create(exerciseId = exercise.id))
            }
        }
    }
}
