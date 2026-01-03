package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppChip
import com.patrykandpatrick.liftapp.ui.component.LiftAppFAB
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.Dropdown
import com.patrykandpatrick.liftapp.ui.icons.Edit
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plus
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.chart.DateIntervalController
import com.patrykandpatryk.liftapp.core.chart.bodyMeasurementLegend
import com.patrykandpatryk.liftapp.core.chart.rememberAdaptiveCartesianLayerRangeProvider
import com.patrykandpatryk.liftapp.core.chart.rememberBottom
import com.patrykandpatryk.liftapp.core.chart.rememberCartesianMarker
import com.patrykandpatryk.liftapp.core.chart.rememberCartesianMarkerValueFormatter
import com.patrykandpatryk.liftapp.core.chart.rememberExtraStoreCartesianLayerRangeProvider
import com.patrykandpatryk.liftapp.core.chart.rememberLine
import com.patrykandpatryk.liftapp.core.chart.rememberStart
import com.patrykandpatryk.liftapp.core.chart.rememberStartAxisValueFormatter
import com.patrykandpatryk.liftapp.core.date.name
import com.patrykandpatryk.liftapp.core.extension.plus
import com.patrykandpatryk.liftapp.core.extension.toPaddingValues
import com.patrykandpatryk.liftapp.core.format.format
import com.patrykandpatryk.liftapp.core.isCompactWidth
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.model.valueOrNull
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.text.parseMarkup
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.core.ui.DropdownMenu
import com.patrykandpatryk.liftapp.core.ui.LiftAppModalBottomSheetWithTopAppBar
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.domain.date.DateInterval
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.model.Action
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.model.ScreenState
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
    LiftAppScaffold(
        modifier = modifier,
        topBar = {
            CompactTopAppBar(
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
                    modifier = Modifier.padding(paddingValues),
                )
            } else {
                LargeContent(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier.padding(paddingValues),
                )
            }
        }
    }
}

@Composable
private fun CompactContent(
    state: ScreenState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val padding = dimens.padding
    LazyColumn(
        modifier = modifier,
        contentPadding =
            PaddingValues(vertical = padding.contentVertical) +
                WindowInsets.navigationBars.toPaddingValues(),
    ) {
        item {
            ChartControls(state, onAction, Modifier.padding(horizontal = padding.contentHorizontal))
        }
        item {
            Chart(
                state.modelProducer,
                state.valueUnit,
                Modifier.padding(horizontal = padding.contentHorizontal),
            )
        }
        journalItems(
            entries = state.entries,
            onAction = onAction,
            paddingValues = PaddingValues(padding.contentHorizontal, padding.itemVerticalMedium),
        )
    }
}

@Composable
private fun LargeContent(
    state: ScreenState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val padding = dimens.padding

    Row(
        modifier = modifier.fillMaxSize().padding(horizontal = padding.contentHorizontal),
        horizontalArrangement = Arrangement.spacedBy(padding.itemHorizontal),
    ) {
        Column(
            Modifier.weight(1f).padding(vertical = padding.contentVertical).navigationBarsPadding()
        ) {
            ChartControls(state, onAction)
            Chart(state.modelProducer, state.valueUnit, modifier = Modifier.fillMaxSize())
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding =
                PaddingValues(vertical = padding.contentVertical) +
                    WindowInsets.navigationBars.toPaddingValues(),
        ) {
            journalItems(
                entries = state.entries,
                onAction = onAction,
                paddingValues = PaddingValues(vertical = padding.itemVerticalMedium),
            )
        }
    }
}

private fun LazyListScope.journalItems(
    entries: List<ScreenState.Entry>,
    onAction: (Action) -> Unit,
    paddingValues: PaddingValues,
) {
    if (entries.isNotEmpty()) {
        item {
            ListSectionTitle(
                title = stringResource(id = R.string.generic_journal),
                modifier = Modifier.padding(top = dimens.padding.itemVertical),
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
                onDelete = { onAction(Action.DeleteBodyMeasurementEntry(entry.id)) },
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

        Spacer(modifier = Modifier.height(dimens.padding.contentVertical))
    }
}

@Composable
private fun ChartControls(
    state: ScreenState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVerticalSmall),
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
                    Icon(imageVector = LiftAppIcons.Dropdown, contentDescription = null)
                },
                label = { Text(text = state.dateInterval.name()) },
            )
        }

        DateIntervalController(
            dateInterval = state.dateInterval,
            incrementDateInterval = { onAction(Action.IncrementDateInterval) },
            decrementDateInterval = { onAction(Action.DecrementDateInterval) },
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
                startAxis = VerticalAxis.rememberStart(rememberStartAxisValueFormatter(valueUnit)),
                bottomAxis = HorizontalAxis.rememberBottom(),
                marker = rememberCartesianMarker(rememberCartesianMarkerValueFormatter(valueUnit)),
                getXStep = { 1.0 },
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

        runBlocking { modelProducer.runTransaction { lineSeries { series(weights) } } }

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
                        dateInterval = DateInterval.bodyMeasurementOptions.first(),
                        dateIntervalOptions = DateInterval.bodyMeasurementOptions,
                    )
                ),
            onAction = {},
        )
    }
}
