package com.patrykandpatrick.liftapp.feature.plan.configurator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.text.IntTextFieldState
import com.patrykandpatrick.liftapp.core.text.LocalDateTextFieldState
import com.patrykandpatrick.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatrick.liftapp.core.text.rememberDefaultMarkupProcessor
import com.patrykandpatrick.liftapp.core.text.updateValueBy
import com.patrykandpatrick.liftapp.core.ui.BottomAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatrick.liftapp.core.ui.InfoCard
import com.patrykandpatrick.liftapp.core.ui.SegmentedButton
import com.patrykandpatrick.liftapp.core.ui.SegmentedButtonContainer
import com.patrykandpatrick.liftapp.core.ui.input.DateInput
import com.patrykandpatrick.liftapp.core.ui.input.DateInputDefaults
import com.patrykandpatrick.liftapp.core.ui.input.NumberInput
import com.patrykandpatrick.liftapp.core.ui.resource.prettyName
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.target.TrainingIntent
import com.patrykandpatrick.liftapp.feature.plan.configurator.model.Action
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PlanConfiguratorScreen(viewModel: PlanConfiguratorViewModel = hiltViewModel()) {
    val loadableState = viewModel.screenState.collectAsStateWithLifecycle().value

    loadableState.Unfold { state ->
        PlanConfiguratorScreen(state = state, onAction = viewModel::onAction)
    }
}

@Composable
private fun PlanConfiguratorScreen(state: ScreenState, onAction: (Action) -> Unit) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LiftAppScaffold(
        topBar = {
            CompactTopAppBar(
                scrollBehavior = topAppBarScrollBehavior,
                title = {
                    CompactTopAppBarDefaults.Title(stringResource(R.string.route_plan_configurator))
                },
                navigationIcon = {
                    CompactTopAppBarDefaults.BackIcon { onAction(Action.PopBackStack) }
                },
            )
        },
        bottomBar = { BottomAppBar.Save(onClick = { onAction(Action.Save(state)) }) },
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        Content(state = state, onAction = onAction, modifier = Modifier.padding(paddingValues))
    }
}

@Composable
private fun Content(
    state: ScreenState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = LocalDimens.current.screen.padding),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(10.dp))

        Text(
            text = state.plan.name ?: stringResource(R.string.training_plan_name_placeholder),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 10.dp),
        )

        DateInput(
            date = state.startDate,
            label = stringResource(R.string.training_plan_start_date),
            selectableDates = DateInputDefaults.SelectableDatesNowAndFuture,
        )

        NumberInput(
            textFieldState = state.cycleCount,
            onPlusClick = { state.cycleCount.updateValueBy(1) },
            onMinusClick = { state.cycleCount.updateValueBy(-1) },
            hint = stringResource(R.string.training_plan_cycle_count),
        )

        InfoCard(text = getInfoText(state))

        // What the block is for and how hard it starts: the two things the tables cannot know.
        // Everything else the generator needs it reads off the exercises themselves.
        Text(
            text = stringResource(R.string.training_plan_intent),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.align(Alignment.Start),
        )

        SegmentedButtonContainer(
            items = TrainingIntent.entries,
            modifier = Modifier.fillMaxWidth(),
        ) { _, intent ->
            SegmentedButton(
                text = intent.prettyName,
                onClick = { onAction(Action.SetIntent(intent)) },
                selected = intent == state.intent,
                showIcon = false,
            )
        }

        NumberInput(
            textFieldState = state.startingRir,
            onPlusClick = { state.startingRir.updateValueBy(1) },
            onMinusClick = { state.startingRir.updateValueBy(-1) },
            hint = stringResource(R.string.training_plan_starting_rir),
        )

        InfoCard(
            text =
                LocalMarkupProcessor.current.toAnnotatedString(
                    stringResource(
                        R.string.training_plan_generation_info,
                        state.startingRir.value.toString(),
                    )
                )
        )

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun getInfoText(state: ScreenState): AnnotatedString {
    val datePattern = stringResource(R.string.date_weekday_day_month)
    val dateFormatter = remember(datePattern) { DateTimeFormatter.ofPattern(datePattern) }

    val startDate = state.startDate.value
    val daysToStart = startDate.toEpochDay() - LocalDate.now().toEpochDay()
    val startsOn =
        when (daysToStart) {
            0L -> stringResource(R.string.generic_today)
            1L -> stringResource(R.string.generic_tomorrow)
            else -> stringResource(R.string.generic_on_date, startDate.format(dateFormatter))
        }

    val endsIn =
        if (state.lengthRemainingDays > 0) {
            stringResource(
                R.string.training_plan_configure_ends_in_weeks_days,
                state.lengthWeeks,
                pluralStringResource(R.plurals.week_count, state.lengthWeeks),
                state.lengthRemainingDays,
                pluralStringResource(R.plurals.day_count, state.lengthRemainingDays),
            )
        } else {
            stringResource(
                R.string.training_plan_configure_ends_in_weeks,
                state.lengthWeeks,
                pluralStringResource(R.plurals.week_count, state.lengthWeeks),
            )
        }

    val endsOn = state.endDate.format(dateFormatter)

    val infoText =
        stringResource(R.string.training_plan_configure_info_text, startsOn, endsIn, endsOn)
    return LocalMarkupProcessor.current.toAnnotatedString(infoText)
}

@MultiDevicePreview
@Composable
private fun PlanConfiguratorPreview() {
    LiftAppTheme {
        CompositionLocalProvider(LocalMarkupProcessor provides rememberDefaultMarkupProcessor()) {
            PlanConfiguratorScreen(
                state =
                    ScreenState(
                        plan =
                            Plan(
                                id = 0,
                                name = "Push Pull Legs",
                                description = "A training plan",
                                items = emptyList(),
                            ),
                        startDate =
                            LocalDateTextFieldState(
                                formatter =
                                    DateTimeFormatter.ofPattern(
                                        stringResource(R.string.date_weekday_day_month_year)
                                    ),
                                initialValue = "Sunday, 13 April 2025",
                            ),
                        cycleCount = IntTextFieldState(initialValue = "6"),
                        intent = TrainingIntent.Both,
                        startingRir = IntTextFieldState(initialValue = "3"),
                        endDate = LocalDate.now().plusDays(42),
                        lengthWeeks = 6,
                        lengthRemainingDays = 0,
                    ),
                onAction = {},
            )
        }
    }
}
