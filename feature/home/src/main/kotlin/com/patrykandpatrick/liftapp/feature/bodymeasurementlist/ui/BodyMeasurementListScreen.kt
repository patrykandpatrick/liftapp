package com.patrykandpatrick.liftapp.feature.bodymeasurementlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.chart.runSparklineTransaction
import com.patrykandpatrick.liftapp.core.extension.increaseBy
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitle
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitleDefaults
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValueDisplay
import com.patrykandpatrick.liftapp.feature.bodymeasurementlist.model.Action
import com.patrykandpatrick.liftapp.feature.bodymeasurementlist.model.ScreenState
import com.patrykandpatrick.liftapp.ui.VerticalGrid
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import kotlinx.coroutines.runBlocking

@Composable
fun BodyMeasurementListScreen(modifier: Modifier = Modifier) {
    val viewModel: BodyMeasurementListViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    BodyMeasurementListScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun BodyMeasurementListScreen(
    state: ScreenState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val compositionTitle = stringResource(R.string.body_measurement_section_composition)
    val measurementsTitle = stringResource(R.string.body_measurement_section_circumference)

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            CompactTopAppBar(
                title = { Text(stringResource(id = R.string.route_body)) },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.statusBars,
    ) { paddingValues ->
        // The list leaves the gap that belongs between neighbors within a section, and each section
        // adds the rest of the wider gap above itself — the scheme the rest of the app follows.
        //
        // Only the vertical inset is the list's. Each element insets itself horizontally, so that
        // the list scrolls and overscrolls the full width it was given rather than within a margin,
        // and so that an element wanting to reach the edge can.
        LazyColumn(
            contentPadding = paddingValues.increaseBy(vertical = dimens.screen.verticalPadding),
            verticalArrangement =
                Arrangement.spacedBy(ListSectionTitleDefaults.withinSectionSpacing),
        ) {
            state.featured?.let { featured ->
                item(key = featured.id) {
                    BodyMeasurementHeroCard(
                        item = featured,
                        onClick = { onAction(Action.OpenDetails(featured.id)) },
                        onAddEntryClick = { onAction(Action.AddEntry(featured.id)) },
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(horizontal = dimens.screen.horizontalPadding),
                    )
                }
            }

            tileSection(
                key = "composition",
                title = compositionTitle,
                items = state.composition,
                isFirstSection = state.featured == null,
                onAction = onAction,
            )

            tileSection(
                key = "measurements",
                title = measurementsTitle,
                items = state.measurements,
                isFirstSection = state.featured == null && state.composition.isEmpty(),
                onAction = onAction,
            )
        }
    }
}

/**
 * A titled grid of tiles. The grid goes in one lazy item because it lays its own rows out; the
 * section counts are small and fixed, so nothing is lost by composing it whole.
 *
 * [key] identifies the section to the list and is not [title]: a heading is translated, and a list
 * key has to be unique and to survive the reader changing their language.
 */
private fun LazyListScope.tileSection(
    key: String,
    title: String,
    items: List<BodyMeasurementListItem>,
    isFirstSection: Boolean,
    onAction: (Action) -> Unit,
) {
    if (items.isEmpty()) return

    item(key = "$key-title") {
        // The same inset the tiles below take, so the heading sits on the edge they sit on.
        ListSectionTitle(
            title = title,
            paddingValues =
                PaddingValues(
                    start = dimens.screen.horizontalPadding,
                    top = ListSectionTitleDefaults.topPadding(isFirstSection),
                    end = dimens.screen.horizontalPadding,
                    bottom = ListSectionTitleDefaults.bottomPadding,
                ),
        )
    }

    item(key = "$key-grid") {
        VerticalGrid(
            horizontalArrangement = Arrangement.spacedBy(TileSpacing),
            verticalArrangement = Arrangement.spacedBy(TileSpacing),
            modifier = Modifier.padding(horizontal = dimens.screen.horizontalPadding),
        ) {
            items.forEach { item ->
                BodyMeasurementTile(
                    item = item,
                    onClick = { onAction(Action.OpenDetails(item.id)) },
                    onAddEntryClick = { onAction(Action.AddEntry(item.id)) },
                )
            }
        }
    }
}

private val TileSpacing = 8.dp

@MultiDevicePreview
@Composable
private fun BodyMeasurementListScreenPreview() {
    // Built on first composition rather than held in a top-level property: filling a producer
    // blocks, and a preview is the only thing that should ever pay for it.
    val state = remember { previewState() }

    PreviewTheme { BodyMeasurementListScreen(state = state, onAction = {}) }
}

private fun previewItem(
    id: Long,
    name: String,
    type: BodyMeasurementType,
    primary: String? = null,
    secondary: String? = null,
    unit: String = "cm",
    delta: Pair<String, BodyMeasurementValueDisplay.Direction>? = null,
    trend: List<Number> = emptyList(),
    progress: Float? = null,
    sideBalance: Float? = null,
) =
    BodyMeasurementListItem(
        id = id,
        name = name,
        type = type,
        value =
            primary?.let {
                BodyMeasurementValueDisplay(
                    primary = primary,
                    secondary = secondary,
                    unit = unit,
                    delta =
                        delta?.let { (label, direction) ->
                            BodyMeasurementValueDisplay.Delta(label, direction)
                        },
                )
            },
        trend =
            trend
                .takeIf { readings -> readings.size > 1 }
                ?.let { readings ->
                    CartesianChartModelProducer().also { producer ->
                        runBlocking { producer.runSparklineTransaction(readings) }
                    }
                },
        progress = progress,
        sideBalance = sideBalance,
    )

private fun previewState() =
    ScreenState(
        featured =
            previewItem(
                id = 1,
                name = "Weight",
                type = BodyMeasurementType.Weight,
                primary = "78.4",
                unit = "kg",
                delta = "-1.8" to BodyMeasurementValueDisplay.Direction.Down,
                trend = listOf(80.2f, 80.4f, 79.8f, 80.1f, 79.3f, 79.5f, 78.6f, 78.9f, 78.4f),
            ),
        composition =
            listOf(
                previewItem(
                    id = 2,
                    name = "Fat",
                    type = BodyMeasurementType.Percentage,
                    primary = "16.2",
                    unit = "%",
                    delta = "-0.8" to BodyMeasurementValueDisplay.Direction.Down,
                    progress = .162f,
                ),
                previewItem(
                    id = 3,
                    name = "Muscle",
                    type = BodyMeasurementType.Percentage,
                    primary = "42.1",
                    unit = "%",
                    delta = "+0.4" to BodyMeasurementValueDisplay.Direction.Up,
                    progress = .421f,
                ),
            ),
        measurements =
            listOf(
                previewItem(
                    id = 4,
                    name = "Chest",
                    type = BodyMeasurementType.Length,
                    primary = "104",
                    delta = "+1.2" to BodyMeasurementValueDisplay.Direction.Up,
                    trend = listOf(101.5f, 102f, 101.8f, 102.8f, 103.4f, 104f),
                ),
                previewItem(
                    id = 5,
                    name = "Abdomen",
                    type = BodyMeasurementType.Length,
                    primary = "82.5",
                    delta = "-1.1" to BodyMeasurementValueDisplay.Direction.Down,
                    trend = listOf(85f, 84.6f, 84.8f, 83.9f, 83.2f, 82.5f),
                ),
                previewItem(
                    id = 6,
                    name = "Bicep",
                    type = BodyMeasurementType.LengthTwoSides,
                    primary = "38.2",
                    secondary = "38.6",
                    sideBalance = .4974f,
                ),
                previewItem(id = 7, name = "Calf", type = BodyMeasurementType.Length),
            ),
    )
