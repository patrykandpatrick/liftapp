package com.patrykandpatrick.liftapp.feature.bodymeasurementdetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.chart.DateIntervalController
import com.patrykandpatrick.liftapp.core.chart.bodyMeasurementLegend
import com.patrykandpatrick.liftapp.core.chart.bottom
import com.patrykandpatrick.liftapp.core.chart.rememberAdaptiveCartesianLayerRangeProvider
import com.patrykandpatrick.liftapp.core.chart.rememberCartesianMarker
import com.patrykandpatrick.liftapp.core.chart.rememberCartesianMarkerValueFormatter
import com.patrykandpatrick.liftapp.core.chart.rememberExtraStoreCartesianLayerRangeProvider
import com.patrykandpatrick.liftapp.core.chart.rememberLine
import com.patrykandpatrick.liftapp.core.chart.rememberStartAxisValueFormatter
import com.patrykandpatrick.liftapp.core.chart.start
import com.patrykandpatrick.liftapp.core.date.name
import com.patrykandpatrick.liftapp.core.extension.plus
import com.patrykandpatrick.liftapp.core.extension.toPaddingValues
import com.patrykandpatrick.liftapp.core.format.format
import com.patrykandpatrick.liftapp.core.isCompactWidth
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.model.valueOrNull
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.text.parseMarkup
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatrick.liftapp.core.ui.DropdownMenu
import com.patrykandpatrick.liftapp.core.ui.LiftAppModalBottomSheetWithTopAppBar
import com.patrykandpatrick.liftapp.core.ui.ListItem
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitle
import com.patrykandpatrick.liftapp.domain.date.DateInterval
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.feature.bodymeasurementdetails.model.Action
import com.patrykandpatrick.liftapp.feature.bodymeasurementdetails.model.ScreenState
import com.patrykandpatrick.liftapp.ui.component.EmptyState
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppChip
import com.patrykandpatrick.liftapp.ui.component.LiftAppDestructiveActionDialog
import com.patrykandpatrick.liftapp.ui.component.LiftAppFAB
import com.patrykandpatrick.liftapp.ui.component.LiftAppFilterChipDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.ChevronDown
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.Edit
import com.patrykandpatrick.liftapp.ui.icons.History
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plus
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import java.time.DayOfWeek
import java.time.LocalDate
import kotlinx.coroutines.runBlocking

@Composable
fun BodyMeasurementDetailScreen(modifier: Modifier = Modifier) {
    val viewModel: BodyMeasurementDetailViewModel = hiltViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()

    BodyMeasurementDetailScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun BodyMeasurementDetailScreen(
    state: Loadable<ScreenState>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val fabHeight = 24.dp + dimens.fab.verticalPadding * 2
    var entryIDToDelete by rememberSaveable { mutableStateOf<Long?>(null) }
    // The scaffold leaves 16 dp below the FAB; use the standard screen padding above it.
    val scrollableContentBottomPadding = fabHeight + 16.dp + dimens.screen.verticalPadding

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            CompactTopAppBar(
                scrollBehavior = topAppBarScrollBehavior,
                title = { Text(state.valueOrNull()?.name.orEmpty()) },
                navigationIcon = {
                    CompactTopAppBarDefaults.BackIcon { onAction(Action.PopBackStack) }
                },
            )
        },
        floatingActionButton = {
            LiftAppFAB(
                content = {
                    Icon(imageVector = LiftAppIcons.Plus, contentDescription = null)

                    Text(text = stringResource(id = R.string.action_new_entry))
                },
                onClick = { onAction(Action.AddBodyMeasurement) },
                modifier = Modifier.navigationBarsPadding(),
            )
        },
        contentWindowInsets = WindowInsets.statusBars.union(WindowInsets.displayCutout),
    ) { paddingValues ->
        state.Unfold { state ->
            if (isCompactWidth) {
                CompactContent(
                    state = state,
                    onAction = onAction,
                    onDeleteRequest = { entryIDToDelete = it },
                    bottomPadding = scrollableContentBottomPadding,
                    modifier = Modifier.padding(paddingValues),
                )
            } else {
                LargeContent(
                    state = state,
                    onAction = onAction,
                    onDeleteRequest = { entryIDToDelete = it },
                    bottomPadding = scrollableContentBottomPadding,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }

    entryIDToDelete?.let { entryID ->
        LiftAppDestructiveActionDialog(
            title = stringResource(R.string.body_measurement_entry_delete_title),
            text = stringResource(R.string.body_measurement_entry_delete_message),
            confirmText = stringResource(R.string.action_delete),
            dismissText = stringResource(android.R.string.cancel),
            onDismissRequest = { entryIDToDelete = null },
            onConfirm = {
                entryIDToDelete = null
                onAction(Action.DeleteBodyMeasurementEntry(entryID))
            },
        )
    }
}

@Composable
private fun CompactContent(
    state: ScreenState,
    onAction: (Action) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    if (state.entries.isEmpty()) {
        EmptyMeasurementContent(state = state, onAction = onAction, modifier = modifier)
        return
    }

    val horizontalPadding = dimens.screen.horizontalPadding
    LazyColumn(
        modifier = modifier,
        contentPadding =
            PaddingValues(
                top = dimens.screen.verticalPadding,
                bottom = bottomPadding,
            ) + WindowInsets.navigationBars.toPaddingValues(),
    ) {
        item {
            ChartControls(
                state,
                onAction,
                alignToScreenEdges = true,
            )
        }
        item {
            Chart(
                state.modelProducer,
                state.valueUnit,
                Modifier.padding(horizontal = dimens.screen.horizontalPadding),
            )
        }
        journalItems(
            entries = state.entries,
            onAction = onAction,
            onDeleteRequest = onDeleteRequest,
            paddingValues = PaddingValues(horizontalPadding, 12.dp),
        )
    }
}

@Composable
private fun LargeContent(
    state: ScreenState,
    onAction: (Action) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    if (state.entries.isEmpty()) {
        EmptyMeasurementContent(state = state, onAction = onAction, modifier = modifier)
        return
    }

    Row(
        modifier = modifier.fillMaxSize().padding(horizontal = dimens.screen.horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            Modifier.weight(1f)
                .padding(vertical = dimens.screen.verticalPadding)
                .navigationBarsPadding()
        ) {
            ChartControls(state, onAction)
            Chart(state.modelProducer, state.valueUnit, modifier = Modifier.fillMaxSize())
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding =
                PaddingValues(
                    top = dimens.screen.verticalPadding,
                    bottom = bottomPadding,
                ) + WindowInsets.navigationBars.toPaddingValues(),
        ) {
            journalItems(
                entries = state.entries,
                onAction = onAction,
                onDeleteRequest = onDeleteRequest,
                paddingValues = PaddingValues(vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun EmptyMeasurementContent(
    state: ScreenState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(vertical = dimens.screen.verticalPadding)) {
        ChartControls(state, onAction, alignToScreenEdges = true)
        EmptyState(
            icon = LiftAppIcons.History,
            message = stringResource(R.string.state_no_measurements),
            modifier =
                Modifier.weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = dimens.screen.horizontalPadding),
        )
    }
}

private fun LazyListScope.journalItems(
    entries: List<ScreenState.Entry>,
    onAction: (Action) -> Unit,
    onDeleteRequest: (Long) -> Unit,
    paddingValues: PaddingValues,
) {
    if (entries.isNotEmpty()) {
        item {
            ListSectionTitle(
                title = stringResource(id = R.string.generic_journal),
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }

    items(items = entries, key = { it.id }) { entry ->
        val (modalVisible, setModalVisible) = remember { mutableStateOf(false) }
        ListItem(
            title = { Text(parseMarkup(entry.value)) },
            modifier = Modifier.animateItem(),
            description = { Text(entry.date.format(Formatter.DateFormat.WeekdayDayMonth)) },
            paddingValues = paddingValues,
            onClick = { setModalVisible(true) },
        )

        if (modalVisible) {
            OptionsBottomSheet(
                onDismissRequest = { setModalVisible(false) },
                onEdit = { onAction(Action.EditBodyMeasurement(entry.id)) },
                onDelete = { onDeleteRequest(entry.id) },
            )
        }
    }
}

@Composable
private fun OptionsBottomSheet(
    onDismissRequest: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LiftAppModalBottomSheetWithTopAppBar(onDismissRequest, modifier) { dismiss ->
        ListItem(
            title = { Text(stringResource(id = R.string.action_edit)) },
            icon = { Icon(imageVector = LiftAppIcons.Edit, contentDescription = null) },
            onClick = {
                onEdit()
                dismiss()
            },
        )

        ListItem(
            title = { Text(text = stringResource(id = R.string.action_delete)) },
            icon = { Icon(LiftAppIcons.Delete, null) },
            onClick = {
                onDelete()
                dismiss()
            },
        )

        Spacer(modifier = Modifier.height(dimens.screen.verticalPadding))
    }
}

@Composable
private fun ChartControls(
    state: ScreenState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
    alignToScreenEdges: Boolean = false,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier =
                if (alignToScreenEdges) {
                    Modifier.padding(horizontal = dimens.screen.horizontalPadding)
                } else {
                    Modifier
                }
        ) {
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
            modifier =
                if (alignToScreenEdges) {
                    Modifier.padding(horizontal = 4.dp)
                } else {
                    Modifier
                },
        )
    }
}

@Composable
private fun Chart(
    modelProducer: CartesianChartModelProducer,
    valueUnit: String,
    modifier: Modifier = Modifier,
) {
    CartesianChartHost(
        modifier = modifier,
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider =
                        LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(colorScheme.chartColors[0]),
                            LineCartesianLayer.rememberLine(colorScheme.chartColors[1]),
                        ),
                    rangeProvider =
                        rememberAdaptiveCartesianLayerRangeProvider(
                            xAxisCartesianLayerRangeProvider =
                                rememberExtraStoreCartesianLayerRangeProvider()
                        ),
                ),
                startAxis = VerticalAxis.start(rememberStartAxisValueFormatter(valueUnit)),
                bottomAxis = HorizontalAxis.bottom(),
                marker = rememberCartesianMarker(rememberCartesianMarkerValueFormatter(valueUnit)),
                getXStep = { _, _, _ -> 1.0 },
                legend = bodyMeasurementLegend(),
            ),
        modelProducer = modelProducer,
        scrollState =
            rememberVicoScrollState(autoScrollCondition = AutoScrollCondition.OnModelGrowth),
        zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
    )
}

@MultiDevicePreview
@Composable
private fun BodyMeasurementDetailScreenPreview() {
    PreviewTheme {
        val modelProducer = remember { CartesianChartModelProducer() }

        val weights = listOf(70f, 71f, 70.5f, 70.7f, 71.3f, 72f)

        runBlocking { modelProducer.runTransaction { lineModel { series(weights) } } }

        BodyMeasurementDetailScreen(
            state =
                Loadable.Success(
                    ScreenState(
                        bodyMeasurementID = 1L,
                        name = "Weight",
                        entries =
                            weights
                                .mapIndexed { index, weight ->
                                    ScreenState.Entry(
                                        index.toLong(),
                                        "$weight kg",
                                        LocalDate.now().withDayOfMonth(index + 1).atStartOfDay(),
                                    )
                                }
                                .reversed(),
                        modelProducer = modelProducer,
                        valueUnit = "kg",
                        dateInterval =
                            DateInterval.bodyMeasurementOptions(DayOfWeek.MONDAY).first(),
                        dateIntervalOptions = DateInterval.bodyMeasurementOptions(DayOfWeek.MONDAY),
                    )
                ),
            onAction = {},
        )
    }
}
