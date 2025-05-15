package com.patrykandpatrick.liftapp.feature.plan.configurator.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.feature.plan.configurator.model.Action
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.text.IntTextFieldState
import com.patrykandpatryk.liftapp.core.text.LocalDateTextFieldState
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.text.updateValueBy
import com.patrykandpatryk.liftapp.core.ui.BottomAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.core.ui.Info
import com.patrykandpatryk.liftapp.core.ui.input.DateInput
import com.patrykandpatryk.liftapp.core.ui.input.DateInputDefaults
import com.patrykandpatryk.liftapp.core.ui.input.NumberInput
import com.patrykandpatryk.liftapp.domain.plan.Plan
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
    Scaffold(
        topBar = {
            CompactTopAppBar(
                title = {
                    CompactTopAppBarDefaults.Title(stringResource(R.string.route_plan_configurator))
                },
                navigationIcon = {
                    CompactTopAppBarDefaults.BackIcon { onAction(Action.PopBackStack) }
                },
            )
        },
        bottomBar = { BottomAppBar.Save(onClick = { onAction(Action.Save(state)) }) },
        modifier = Modifier.imePadding(),
    ) { paddingValues ->
        Content(state = state, modifier = Modifier.padding(paddingValues))
    }
}

@Composable
private fun Content(state: ScreenState, modifier: Modifier = Modifier) {
    val padding = LocalDimens.current.padding
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = padding.contentHorizontal),
        verticalArrangement = Arrangement.spacedBy(padding.itemVertical),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(padding.contentVerticalSmall))

        Text(
            text = state.plan.name,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = padding.contentVerticalSmall),
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

        Info(text = getInfoText(state))
    }
}

@Composable
private fun getInfoText(state: ScreenState): AnnotatedString {
    val datePattern = stringResource(R.string.date_format_long)
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
                                    stringResource(R.string.date_format_edit)
                                ),
                            initialValue = "Sunday, 13 April 2025",
                        ),
                    cycleCount = IntTextFieldState(initialValue = "6"),
                    endDate = LocalDate.now().plusDays(42),
                    lengthWeeks = 6,
                    lengthRemainingDays = 0,
                ),
            onAction = {},
        )
    }
}
