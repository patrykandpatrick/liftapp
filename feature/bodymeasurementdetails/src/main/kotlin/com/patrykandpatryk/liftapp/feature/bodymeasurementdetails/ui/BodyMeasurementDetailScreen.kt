package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsets.Companion
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.ui.component.LiftAppFAB
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.core.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.chart.rememberAdaptiveCartesianLayerRangeProvider
import com.patrykandpatryk.liftapp.core.chart.rememberCartesianMarker
import com.patrykandpatryk.liftapp.core.chart.rememberCartesianMarkerValueFormatter
import com.patrykandpatryk.liftapp.core.chart.rememberEpochDayCartesianValueFormatter
import com.patrykandpatryk.liftapp.core.chart.rememberLine
import com.patrykandpatryk.liftapp.core.chart.rememberStartAxisValueFormatter
import com.patrykandpatryk.liftapp.core.chart.rememberTextComponent
import com.patrykandpatryk.liftapp.core.extension.toPaddingValues
import com.patrykandpatryk.liftapp.core.format.format
import com.patrykandpatryk.liftapp.core.isCompactWidth
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.model.valueOrNull
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
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
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = null,
                    )

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
    LazyColumn(modifier = modifier, contentPadding = Companion.navigationBars.toPaddingValues()) {
        item { Chart(state.modelProducer, state.valueUnit) }
        journalItems(state.entries, onAction)
    }
}

@Composable
private fun LargeContent(
    state: ScreenState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier.fillMaxSize()) {
        Chart(
            state.modelProducer,
            state.valueUnit,
            modifier = Modifier.weight(1f).fillMaxHeight().navigationBarsPadding(),
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = WindowInsets.navigationBars.toPaddingValues(),
        ) {
            journalItems(state.entries, onAction)
        }
    }
}

private fun LazyListScope.journalItems(
    entries: List<ScreenState.Entry>,
    onAction: (Action) -> Unit,
) {
    item { ListSectionTitle(stringResource(id = R.string.generic_journal)) }

    items(items = entries, key = { it.id }) { entry ->
        ListItem(
            title = { Text(entry.value) },
            modifier = Modifier.animateItem(),
            description = { Text(entry.date.format(Formatter.DateFormat.WeekdayDayMonth)) },
            paddingValues =
                PaddingValues(
                    horizontal = dimens.padding.contentHorizontal,
                    vertical = dimens.padding.itemVerticalMedium,
                ),
            actions = {
                LiftAppIconButton(onClick = { onAction(Action.EditBodyMeasurement(entry.id)) }) {
                    Icon(
                        painterResource(id = R.drawable.ic_edit),
                        stringResource(id = R.string.action_edit),
                    )
                }

                LiftAppIconButton(
                    onClick = { onAction(Action.DeleteBodyMeasurementEntry(entry.id)) }
                ) {
                    Icon(LiftAppIcons.Delete, stringResource(id = R.string.action_delete))
                }
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
        modifier =
            modifier.padding(
                top = dimens.padding.itemVertical,
                bottom = dimens.padding.itemVertical,
                start = dimens.padding.contentHorizontal,
            ),
        chart =
            rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider =
                        LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(colorScheme.primary),
                            LineCartesianLayer.rememberLine(colorScheme.secondary),
                        ),
                    rangeProvider = rememberAdaptiveCartesianLayerRangeProvider(),
                ),
                startAxis =
                    VerticalAxis.rememberStart(
                        label =
                            rememberTextComponent(
                                textStyle = MaterialTheme.typography.titleSmall,
                                margins = insets(end = 4.dp),
                            ),
                        valueFormatter = rememberStartAxisValueFormatter(valueUnit),
                    ),
                bottomAxis =
                    HorizontalAxis.rememberBottom(
                        valueFormatter = rememberEpochDayCartesianValueFormatter(),
                        label =
                            rememberTextComponent(
                                textStyle = MaterialTheme.typography.titleSmall,
                                margins = insets(top = 2.dp, start = 4.dp, end = 4.dp),
                            ),
                    ),
                marker = rememberCartesianMarker(rememberCartesianMarkerValueFormatter(valueUnit)),
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
                    )
                ),
            onAction = {},
        )
    }
}
