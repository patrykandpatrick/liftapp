package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppChip
import com.patrykandpatrick.liftapp.ui.component.LiftAppChipRow
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Dropdown
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.ChartTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.grouped
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.chart.ExtraStoreCartesianLayerRangeProvider
import com.patrykandpatryk.liftapp.core.chart.OnModelChange
import com.patrykandpatryk.liftapp.core.chart.rememberBottom
import com.patrykandpatryk.liftapp.core.chart.rememberCartesianMarker
import com.patrykandpatryk.liftapp.core.chart.rememberStart
import com.patrykandpatryk.liftapp.core.chart.rememberValueUnitCartesianMarkerValueFormatter
import com.patrykandpatryk.liftapp.core.chart.rememberValueUnitCartesianValueFormatter
import com.patrykandpatryk.liftapp.core.date.displayDateInterval
import com.patrykandpatryk.liftapp.core.date.name
import com.patrykandpatryk.liftapp.core.exercise.prettyString
import com.patrykandpatryk.liftapp.core.exerciseset.name
import com.patrykandpatryk.liftapp.core.format.format
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.ui.DropdownMenu
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.feature.exercise.model.Action
import com.patrykandpatryk.liftapp.feature.exercise.model.ScreenState

@Composable
fun Statistics(state: ScreenState, modifier: Modifier = Modifier, onAction: (Action) -> Unit) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = dimens.padding.contentVertical),
        verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVerticalSmall),
    ) {
        item(key = "date_interval") { StatisticsControls(state, onAction) }

        item(key = "chart") { Chart(state.cartesianChartModelProducer) }

        if (state.exerciseSetGroups.isNotEmpty()) {
            item(key = "journal") {
                ListSectionTitle(
                    title = stringResource(R.string.generic_journal),
                    modifier = Modifier.animateItem(),
                )
            }
        }

        items(items = state.exerciseSetGroups, key = { it.workoutStartDate }) { exerciseSetGroup ->
            ExerciseSetGroupItem(
                exerciseSetGroup = exerciseSetGroup,
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun StatisticsControls(
    state: ScreenState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = dimens.padding.contentHorizontal),
        verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVerticalSmall),
    ) {
        LiftAppChipRow {
            DropdownMenu(
                selectedItems = listOf(state.dateInterval),
                items = state.dateIntervalOptions,
                getItemText = { it.name() },
                onClick = { onAction(Action.SetDateInterval(it)) },
                isMultiSelect = false,
            ) { expanded, setExpanded ->
                LiftAppChip(
                    onClick = { setExpanded(true) },
                    colors = LiftAppButtonDefaults.outlinedButtonColors,
                    trailingIcon = {
                        Icon(imageVector = LiftAppIcons.Dropdown, contentDescription = null)
                    },
                    label = { Text(text = state.dateInterval.name()) },
                )
            }

            if (state.summaryTypeOptions.size > 1) {
                DropdownMenu(
                    selectedItems = listOf(state.summaryType),
                    items = state.summaryTypeOptions,
                    getItemText = { it.name() },
                    onClick = { onAction(Action.SetSummaryType(it)) },
                    isMultiSelect = false,
                ) { expanded, setExpanded ->
                    LiftAppChip(
                        onClick = { setExpanded(true) },
                        colors = LiftAppButtonDefaults.outlinedButtonColors,
                        trailingIcon = {
                            Icon(imageVector = LiftAppIcons.Dropdown, contentDescription = null)
                        },
                        label = { Text(text = state.summaryType.name()) },
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            LiftAppIconButton(onClick = { onAction(Action.DecrementDateInterval) }) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
            }

            Text(
                text = state.dateInterval.displayDateInterval(),
                style = MaterialTheme.typography.titleSmall,
                color = colorScheme.onSurfaceVariant,
            )

            LiftAppIconButton(
                onClick = { onAction(Action.IncrementDateInterval) },
                enabled = state.dateInterval.isIncrementable,
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowForward, null)
            }
        }
    }
}

@Composable
private fun Chart(producer: CartesianChartModelProducer, modifier: Modifier = Modifier) {
    CartesianChartHost(
        modifier = modifier.padding(horizontal = dimens.padding.contentHorizontal),
        chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    mergeMode = { ColumnCartesianLayer.MergeMode.grouped(4.dp) },
                    columnProvider =
                        ColumnCartesianLayer.ColumnProvider.series(
                            ChartTheme.columnCartesianLayerColors.map { color ->
                                rememberLineComponent(
                                    fill = fill(color),
                                    shape = CorneredShape.rounded(topLeft = 4.dp, topRight = 4.dp),
                                    thickness = 8.dp,
                                )
                            }
                        ),
                    rangeProvider = ExtraStoreCartesianLayerRangeProvider(),
                ),
                startAxis = VerticalAxis.rememberStart(rememberValueUnitCartesianValueFormatter()),
                bottomAxis = HorizontalAxis.rememberBottom(),
                marker = rememberCartesianMarker(rememberValueUnitCartesianMarkerValueFormatter()),
                getXStep = { 1.0 },
            ),
        modelProducer = producer,
        zoomState = rememberVicoZoomState(minZoom = Zoom.fixed(1f)),
        scrollState =
            rememberVicoScrollState(
                initialScroll = Scroll.Absolute.End,
                autoScrollCondition = AutoScrollCondition.OnModelChange,
            ),
    )
}

@Composable
private fun ExerciseSetGroupItem(
    exerciseSetGroup: ExerciseSetGroup,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVerticalSmall),
        modifier =
            modifier.padding(
                vertical = dimens.padding.itemVerticalSmall,
                horizontal = dimens.padding.contentHorizontal,
            ),
    ) {
        Text(
            text =
                buildString {
                    append(exerciseSetGroup.workoutName)
                    append(" ${stringResource(R.string.point_separator)} ")
                    append(
                        exerciseSetGroup.workoutStartDate.format(
                            Formatter.DateFormat.WeekdayDayMonth
                        )
                    )
                },
            style = MaterialTheme.typography.titleSmall,
            color = colorScheme.onSurfaceVariant,
        )

        exerciseSetGroup.sets.forEachIndexed { setIndex, set ->
            Text(
                text =
                    LocalMarkupProcessor.current.toAnnotatedString(
                        stringResource(
                            R.string.workout_exercise_set_info,
                            setIndex + 1,
                            set.prettyString(),
                        )
                    ),
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurface,
            )
        }
    }
}

@Composable
@MultiDevicePreview
private fun StatisticsPreview() {
    PreviewTheme { Statistics(state = getScreenStateForPreview(), onAction = {}) }
}
