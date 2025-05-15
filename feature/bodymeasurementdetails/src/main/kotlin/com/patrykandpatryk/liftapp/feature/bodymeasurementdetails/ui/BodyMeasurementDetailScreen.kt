package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui

import androidx.compose.foundation.layout.WindowInsets.Companion
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.scroll.AutoScrollCondition
import com.patrykandpatrick.vico.core.scroll.InitialScroll
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.toPaddingValues
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.model.valueOrNull
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListItemWithOptions
import com.patrykandpatryk.liftapp.core.ui.OptionItem
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.model.Action
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.model.ScreenState

@Composable
fun BodyMeasurementDetailScreen(modifier: Modifier = Modifier) {
    val viewModel: BodyMeasurementDetailViewModel = hiltViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()

    BodyMeasurementDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        modelProducer = viewModel.chartEntryModelProducer,
        modifier = modifier,
    )
}

@Composable
private fun BodyMeasurementDetailScreen(
    state: Loadable<ScreenState>,
    onAction: (Action) -> Unit,
    modelProducer: ChartEntryModelProducer,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = state.valueOrNull()?.name.orEmpty(),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = { onAction(Action.PopBackStack) },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                modifier = Modifier.navigationBarsPadding(),
                text = { Text(text = stringResource(id = R.string.action_new_entry)) },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = null,
                    )
                },
                onClick = { onAction(Action.AddBodyMeasurement) },
            )
        },
    ) { paddingValues ->
        state.Unfold { state ->
            ListContent(
                state = state,
                modelProducer = modelProducer,
                onAction = onAction,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun ListContent(
    state: ScreenState,
    modelProducer: ChartEntryModelProducer,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier, contentPadding = Companion.navigationBars.toPaddingValues()) {
        item {
            Chart(
                modifier =
                    Modifier.padding(
                        top = LocalDimens.current.padding.itemVertical,
                        bottom = LocalDimens.current.padding.itemVertical,
                        start = LocalDimens.current.padding.contentHorizontal,
                    ),
                chart =
                    lineChart(
                        axisValuesOverrider = AxisValuesOverrider.adaptiveYValues(yFraction = 1.1f)
                    ),
                chartModelProducer = modelProducer,
                startAxis =
                    rememberStartAxis(
                        itemPlacer = remember { AxisItemPlacer.Vertical.default(maxItemCount = 3) }
                    ),
                bottomAxis = rememberBottomAxis(),
                chartScrollSpec =
                    rememberChartScrollSpec(
                        initialScroll = InitialScroll.End,
                        autoScrollCondition = AutoScrollCondition.OnModelSizeIncreased,
                    ),
            )
        }

        item {
            Text(
                modifier =
                    Modifier.padding(
                        vertical = LocalDimens.current.padding.itemVertical,
                        horizontal = LocalDimens.current.padding.contentHorizontal,
                    ),
                text = stringResource(id = R.string.generic_journal),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        items(items = state.entries, key = { it.id }) { entry ->
            ListItemWithOptions(
                mainContent = {
                    ListItem(
                        title = { Text(entry.value) },
                        modifier = Modifier.animateItem(),
                        description = { Text(entry.date) },
                    )
                },
                optionItems =
                    listOf(
                        OptionItem(
                            iconPainter = painterResource(id = R.drawable.ic_edit),
                            label = stringResource(id = R.string.action_edit),
                            onClick = {
                                onAction(
                                    Action.EditBodyMeasurement(bodyEntryMeasurementId = entry.id)
                                )
                            },
                        ),
                        OptionItem(
                            iconPainter = painterResource(id = R.drawable.ic_delete),
                            label = stringResource(id = R.string.action_delete),
                            onClick = { onAction(Action.DeleteBodyMeasurementEntry(entry.id)) },
                        ),
                    ),
                isExpanded = entry.isExpanded,
                setExpanded = { onAction(Action.ExpandItem(id = entry.id)) },
            )
        }
    }
}
