package com.patrykandpatrick.liftapp.feature.exercise.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.chart.DateIntervalController
import com.patrykandpatrick.liftapp.core.chart.DateIntervalControllerDefaults
import com.patrykandpatrick.liftapp.core.chart.OnModelChange
import com.patrykandpatrick.liftapp.core.chart.bottom
import com.patrykandpatrick.liftapp.core.chart.rememberCartesianMarker
import com.patrykandpatrick.liftapp.core.chart.rememberExtraStoreCartesianLayerRangeProvider
import com.patrykandpatrick.liftapp.core.chart.rememberValueUnitCartesianMarkerValueFormatter
import com.patrykandpatrick.liftapp.core.chart.rememberValueUnitCartesianValueFormatter
import com.patrykandpatrick.liftapp.core.chart.start
import com.patrykandpatrick.liftapp.core.date.name
import com.patrykandpatrick.liftapp.core.exercise.prettyString
import com.patrykandpatrick.liftapp.core.exerciseset.name
import com.patrykandpatrick.liftapp.core.format.LocalFormatter
import com.patrykandpatrick.liftapp.core.format.format
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatrick.liftapp.core.ui.DropdownMenu
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitle
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitleDefaults
import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseStatistics
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.feature.exercise.model.Action
import com.patrykandpatrick.liftapp.feature.exercise.model.ScreenState
import com.patrykandpatrick.liftapp.ui.component.EmptyState
import com.patrykandpatrick.liftapp.ui.component.LiftAppChip
import com.patrykandpatrick.liftapp.ui.component.LiftAppChipRow
import com.patrykandpatrick.liftapp.ui.component.LiftAppFilterChipDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.ChevronDown
import com.patrykandpatrick.liftapp.ui.icons.Dumbbell
import com.patrykandpatrick.liftapp.ui.icons.Feather
import com.patrykandpatrick.liftapp.ui.icons.History
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Repeat
import com.patrykandpatrick.liftapp.ui.icons.Ruler
import com.patrykandpatrick.liftapp.ui.icons.Timer
import com.patrykandpatrick.liftapp.ui.icons.TrendingDown
import com.patrykandpatrick.liftapp.ui.icons.TrendingUp
import com.patrykandpatrick.liftapp.ui.icons.Weight
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.Scroll
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent

@Composable
fun Statistics(
    state: ScreenState,
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    onAction: (Action) -> Unit,
) {
    val topContentPadding = 16.dp
    val resolvedBottomContentPadding = bottomContentPadding + dimens.screen.padding

    if (!state.hasExerciseHistory) {
        EmptyState(
            icon = LiftAppIcons.History,
            message = stringResource(R.string.state_no_exercise_history),
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(
                        start = dimens.screen.padding,
                        top = topContentPadding,
                        end = dimens.screen.padding,
                        bottom = resolvedBottomContentPadding,
                    ),
        )
        return
    }

    if (state.exerciseSetGroups.isEmpty()) {
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(
                        top = topContentPadding,
                        bottom = resolvedBottomContentPadding,
                    )
        ) {
            StatisticsControls(state, onAction)
            EmptyState(
                icon = LiftAppIcons.History,
                message = stringResource(R.string.state_no_data_for_period),
                modifier =
                    Modifier.weight(1f)
                        .fillMaxWidth()
                        .padding(
                            start = dimens.screen.padding,
                            top = 8.dp,
                            end = dimens.screen.padding,
                            bottom = 16.dp,
                        ),
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                top = topContentPadding,
                bottom = resolvedBottomContentPadding,
            ),
    ) {
        item(key = "date_interval") { StatisticsControls(state, onAction) }

        item(key = "chart") {
            Chart(
                producer = state.cartesianChartModelProducer,
                modifier =
                    Modifier.padding(
                        top = 16.dp - DateIntervalControllerDefaults.visibleContentVerticalInset
                    ),
            )
        }

        state.exerciseStatistics?.let { statistics ->
            item(key = "statistics_title") {
                ListSectionTitle(
                    title = stringResource(R.string.tab_stats),
                    modifier = Modifier.animateItem(),
                    inset = ListSectionTitleDefaults.Inset.ListItemContent,
                    spacing = ListSectionTitleDefaults.Spacing.Section,
                )
            }
            item(key = "statistics") {
                ExerciseStatisticsList(
                    statistics = statistics,
                    modifier =
                        Modifier.animateItem()
                            .padding(
                                start = dimens.screen.padding,
                                end = dimens.screen.padding,
                            ),
                )
            }
        }

        item(key = "journal") {
            ListSectionTitle(
                title = stringResource(R.string.generic_journal),
                modifier = Modifier.animateItem(),
                inset = ListSectionTitleDefaults.Inset.ListItemContent,
                spacing = ListSectionTitleDefaults.Spacing.Section,
            )
        }

        itemsIndexed(
            items = state.exerciseSetGroups,
            key = { _, item -> item.workoutStartDate },
        ) { index, exerciseSetGroup ->
            ExerciseSetGroupItem(
                exerciseSetGroup = exerciseSetGroup,
                position = LiftAppListItemPosition(index, state.exerciseSetGroups.size),
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun ExerciseStatisticsList(
    statistics: ExerciseStatistics,
    modifier: Modifier = Modifier,
) {
    val formatter = LocalFormatter.current
    val items =
        when (statistics) {
            is ExerciseStatistics.Weight ->
                listOf(
                    ExerciseStatistic(
                        icon = LiftAppIcons.Dumbbell,
                        value = formatter.formatWeight(statistics.totalVolume, statistics.massUnit),
                        label = stringResource(R.string.exercise_statistics_total_volume),
                    ),
                    ExerciseStatistic(
                        icon = LiftAppIcons.Weight,
                        value =
                            formatter.formatWeight(statistics.maximumWeight, statistics.massUnit),
                        label = stringResource(R.string.exercise_statistics_maximum_weight),
                    ),
                    ExerciseStatistic(
                        icon = LiftAppIcons.Feather,
                        value =
                            formatter.formatWeight(statistics.minimumWeight, statistics.massUnit),
                        label = stringResource(R.string.exercise_statistics_minimum_weight),
                    ),
                    ExerciseStatistic(
                        icon = LiftAppIcons.Repeat,
                        value =
                            formatter.formatNumber(
                                statistics.totalReps,
                                format = Formatter.NumberFormat.Integer,
                            ),
                        label = stringResource(R.string.exercise_statistics_total_reps),
                    ),
                )

            is ExerciseStatistics.Reps ->
                listOf(
                    ExerciseStatistic(
                        icon = LiftAppIcons.Repeat,
                        value =
                            formatter.formatNumber(
                                statistics.totalReps,
                                format = Formatter.NumberFormat.Integer,
                            ),
                        label = stringResource(R.string.exercise_statistics_total_reps),
                    ),
                    ExerciseStatistic(
                        icon = LiftAppIcons.TrendingUp,
                        value =
                            formatter.formatNumber(
                                statistics.maximumReps,
                                format = Formatter.NumberFormat.Integer,
                            ),
                        label = stringResource(R.string.exercise_statistics_maximum_reps),
                    ),
                    ExerciseStatistic(
                        icon = LiftAppIcons.TrendingDown,
                        value =
                            formatter.formatNumber(
                                statistics.minimumReps,
                                format = Formatter.NumberFormat.Integer,
                            ),
                        label = stringResource(R.string.exercise_statistics_minimum_reps),
                    ),
                )

            is ExerciseStatistics.Time ->
                durationStatistics(
                    totalDuration = formatter.formatDurationWithUnits(statistics.totalDuration),
                    minimumDuration = formatter.formatDurationWithUnits(statistics.minimumDuration),
                    maximumDuration = formatter.formatDurationWithUnits(statistics.maximumDuration),
                )

            is ExerciseStatistics.Cardio ->
                durationStatistics(
                    totalDuration = formatter.formatDurationWithUnits(statistics.totalDuration),
                    minimumDuration = formatter.formatDurationWithUnits(statistics.minimumDuration),
                    maximumDuration = formatter.formatDurationWithUnits(statistics.maximumDuration),
                ) +
                    ExerciseStatistic(
                        icon = LiftAppIcons.Ruler,
                        value =
                            formatter.formatValue(
                                statistics.totalDistance,
                                statistics.distanceUnit,
                            ),
                        label = stringResource(R.string.exercise_statistics_total_distance),
                    )
        }

    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->
            LiftAppListItem(
                title = item.label,
                imageVector = item.icon,
                description = item.value,
                position = LiftAppListItemPosition(index = index, count = items.size),
            )
        }
    }
}

private data class ExerciseStatistic(val icon: ImageVector, val value: String, val label: String)

@Composable
private fun durationStatistics(
    totalDuration: String,
    minimumDuration: String,
    maximumDuration: String,
) =
    listOf(
        ExerciseStatistic(
            icon = LiftAppIcons.Timer,
            value = totalDuration,
            label = stringResource(R.string.exercise_statistics_total_duration),
        ),
        ExerciseStatistic(
            icon = LiftAppIcons.TrendingDown,
            value = minimumDuration,
            label = stringResource(R.string.exercise_statistics_minimum_duration),
        ),
        ExerciseStatistic(
            icon = LiftAppIcons.TrendingUp,
            value = maximumDuration,
            label = stringResource(R.string.exercise_statistics_maximum_duration),
        ),
    )

@Composable
private fun StatisticsControls(
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
            ) { expanded, setExpanded ->
                LiftAppChip(
                    onClick = { setExpanded(true) },
                    trailingIcon = {
                        LiftAppFilterChipDefaults.Icon(vector = LiftAppIcons.ChevronDown)
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
                        trailingIcon = {
                            LiftAppFilterChipDefaults.Icon(vector = LiftAppIcons.ChevronDown)
                        },
                        label = { Text(text = state.summaryType.name()) },
                    )
                }
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

@Composable
private fun Chart(producer: CartesianChartModelProducer, modifier: Modifier = Modifier) {
    CartesianChartHost(
        modifier = modifier.padding(horizontal = dimens.screen.padding),
        chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    mergeMode = { ColumnCartesianLayer.MergeMode.Grouped(4.dp) },
                    columnProvider =
                        ColumnCartesianLayer.ColumnProvider.series(
                            colorScheme.chartColors.map { color ->
                                rememberLineComponent(
                                    fill = Fill(color),
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp),
                                    thickness = 8.dp,
                                )
                            }
                        ),
                    rangeProvider = rememberExtraStoreCartesianLayerRangeProvider(),
                ),
                startAxis = VerticalAxis.start(rememberValueUnitCartesianValueFormatter()),
                bottomAxis = HorizontalAxis.bottom(),
                marker = rememberCartesianMarker(rememberValueUnitCartesianMarkerValueFormatter()),
                getXStep = { _, _, _ -> 1.0 },
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
    position: LiftAppListItemPosition,
    modifier: Modifier = Modifier,
) {
    LiftAppListItem(
        position = position,
        modifier = modifier.padding(horizontal = dimens.screen.padding),
        contentPadding = PaddingValues(16.dp),
        title = {
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
                color = colorScheme.foregroundVariant,
            )
        },
        description = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp),
            ) {
                if (exerciseSetGroup.notes.isNotBlank()) {
                    JournalNote(notes = exerciseSetGroup.notes)
                }

                exerciseSetGroup.sets.forEachIndexed { setIndex, set ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                            color = colorScheme.foreground,
                        )

                        if (set.notes.isNotBlank()) {
                            JournalNote(notes = set.notes)
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun JournalNote(notes: String, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min),
    ) {
        Box(modifier = Modifier.fillMaxHeight().width(2.dp).background(color = colorScheme.primary))
        Text(
            text = notes.trim(),
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.foreground,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
@MultiDevicePreview
private fun StatisticsPreview() {
    PreviewTheme { Statistics(state = getScreenStateForPreview(), onAction = {}) }
}
