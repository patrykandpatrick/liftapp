package com.patrykandpatrick.liftapp.feature.musclevolume.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.chart.DateIntervalController
import com.patrykandpatrick.liftapp.core.chart.DateIntervalControllerDefaults
import com.patrykandpatrick.liftapp.core.date.name
import com.patrykandpatrick.liftapp.core.extension.increaseBy
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.preview.PreviewMuscleVolume
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.ui.DropdownMenu
import com.patrykandpatrick.liftapp.core.ui.TopAppBar
import com.patrykandpatrick.liftapp.core.ui.resource.prettyList
import com.patrykandpatrick.liftapp.core.ui.volume.MuscleVolumeDefaults
import com.patrykandpatrick.liftapp.core.ui.volume.MuscleVolumeRow
import com.patrykandpatrick.liftapp.core.ui.volume.VolumeBandLegend
import com.patrykandpatrick.liftapp.core.ui.volume.VolumeScale
import com.patrykandpatrick.liftapp.domain.date.DAYS_IN_WEEK
import com.patrykandpatrick.liftapp.domain.date.DateInterval
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.feature.musclevolume.model.Action
import com.patrykandpatrick.liftapp.feature.musclevolume.model.ScreenState
import com.patrykandpatrick.liftapp.ui.component.EmptyState
import com.patrykandpatrick.liftapp.ui.component.LiftAppChip
import com.patrykandpatrick.liftapp.ui.component.LiftAppChipRow
import com.patrykandpatrick.liftapp.ui.component.LiftAppFilterChipDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.BicepsFlexed
import com.patrykandpatrick.liftapp.ui.icons.ChevronDown
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import java.time.DayOfWeek

@Composable
fun MuscleVolumeScreen(
    modifier: Modifier = Modifier,
    viewModel: MuscleVolumeViewModel = hiltViewModel(),
) {
    MuscleVolumeScreen(
        loadableState = viewModel.screenState.collectAsStateWithLifecycle().value,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun MuscleVolumeScreen(
    loadableState: Loadable<ScreenState>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.muscle_volume_title),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = { onAction(Action.PopBackStack) },
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
    ) { paddingValues ->
        loadableState.Unfold { state ->
            // Only the vertical inset belongs to the list, as on the dashboard: each row insets
            // itself horizontally so the bars can run the full width between the screen margins.
            Content(
                state = state,
                onAction = onAction,
                contentPadding = paddingValues.increaseBy(bottom = dimens.screen.padding),
            )
        }
    }
}

@Composable
private fun Content(
    state: ScreenState,
    onAction: (Action) -> Unit,
    contentPadding: PaddingValues,
) {
    val screenPadding = dimens.screen.padding
    // Held rather than recomputed: workedMuscles sorts on every read and hands LazyColumn a new
    // list identity each time, rebuilding the content for no reason.
    val muscles = remember(state.muscleVolume) { state.muscleVolume.workedMuscles }
    val untrained = remember(state.muscleVolume) { state.muscleVolume.untrainedMuscles }
    val colorStops = VolumeScale.rememberColorStops()

    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = contentPadding) {
        item(key = "controls") {
            Controls(state = state, onAction = onAction, modifier = Modifier.padding(top = 16.dp))
        }

        if (muscles.isEmpty()) {
            item(key = "empty") {
                EmptyState(
                    icon = LiftAppIcons.BicepsFlexed,
                    message = stringResource(R.string.state_no_data_for_period),
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(horizontal = screenPadding, vertical = 48.dp),
                )
            }
            return@LazyColumn
        }

        item(key = "caption") {
            LiftAppText(
                text =
                    stringResource(
                        if (state.isWeeklyAverage) {
                            R.string.muscle_volume_caption_weekly_average
                        } else {
                            R.string.muscle_volume_caption
                        }
                    ) +
                        state.plannedVolume
                            ?.let { " " + stringResource(R.string.muscle_volume_planned_caption) }
                            .orEmpty(),
                style = typography.bodySmall,
                color = colorScheme.foregroundVariant,
                modifier =
                    Modifier.padding(start = screenPadding, top = 20.dp, end = screenPadding),
            )
        }

        // Above the rows rather than below: the dots and colors are unreadable without it, and
        // a key that follows what it decodes is one the reader meets too late.
        item(key = "legend") {
            VolumeBandLegend(
                Modifier.padding(
                    start = screenPadding,
                    top = 12.dp,
                    end = screenPadding,
                    bottom = MuscleVolumeDefaults.rowSpacing,
                )
            )
        }

        items(items = muscles, key = { muscle -> muscle.name }) { muscle ->
            MuscleVolumeRow(
                muscle = muscle,
                volume = state.muscleVolume,
                colorStops = colorStops,
                plannedVolume = state.plannedVolume,
                modifier =
                    Modifier.animateItem()
                        .padding(horizontal = screenPadding)
                        .padding(bottom = MuscleVolumeDefaults.rowSpacing),
            )
        }

        if (untrained.isNotEmpty()) {
            item(key = "untrained") {
                UntrainedMuscles(
                    muscles = untrained,
                    modifier =
                        Modifier.animateItem()
                            .padding(horizontal = screenPadding)
                            .padding(top = 12.dp, bottom = 24.dp),
                )
            }
        }
    }
}

/**
 * The muscles that got nothing, named rather than drawn.
 *
 * A row apiece would be a dozen identical bars pinned at zero, which says no more than the names do
 * — but leaving them out entirely hides the gap the screen exists to show.
 */
@Composable
private fun UntrainedMuscles(muscles: List<Muscle>, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LiftAppText(
            text = stringResource(R.string.muscle_volume_untrained_title),
            style = typography.titleSmall,
        )

        LiftAppText(
            text = muscles.prettyList(),
            style = typography.bodyMedium,
            color = colorScheme.foregroundVariant,
        )
    }
}

@Composable
private fun Controls(
    state: ScreenState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement =
            Arrangement.spacedBy(
                20.dp - DateIntervalControllerDefaults.visibleContentVerticalInset
            ),
    ) {
        LiftAppChipRow(modifier = Modifier.padding(horizontal = dimens.screen.padding)) {
            DropdownMenu(
                selectedItems = listOf(state.dateInterval),
                items = state.dateIntervalOptions,
                getItemText = { it.name() },
                onClick = { onAction(Action.SetDateInterval(it)) },
                isMultiSelect = false,
            ) { _, setExpanded ->
                LiftAppChip(
                    onClick = { setExpanded(true) },
                    trailingIcon = {
                        LiftAppFilterChipDefaults.Icon(vector = LiftAppIcons.ChevronDown)
                    },
                    label = { Text(text = state.dateInterval.name()) },
                )
            }
        }

        DateIntervalController(
            dateInterval = state.dateInterval,
            incrementDateInterval = { onAction(Action.IncrementDateInterval) },
            decrementDateInterval = { onAction(Action.DecrementDateInterval) },
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}

@MultiDevicePreview
@Composable
private fun MuscleVolumeScreenPreview() {
    PreviewTheme {
        MuscleVolumeScreen(
            loadableState =
                Loadable.Success(
                    ScreenState(
                        muscleVolume = PreviewMuscleVolume,
                        plannedVolume = PreviewMuscleVolume,
                        dateInterval = DateInterval.RollingDays(DAYS_IN_WEEK),
                        dateIntervalOptions = DateInterval.volumeOptions(DayOfWeek.MONDAY),
                    )
                ),
            onAction = {},
        )
    }
}
