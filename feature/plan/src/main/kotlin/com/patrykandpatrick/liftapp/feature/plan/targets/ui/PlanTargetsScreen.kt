package com.patrykandpatrick.liftapp.feature.plan.targets.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.patrykandpatrick.liftapp.core.exercise.rirString
import com.patrykandpatrick.liftapp.core.format.LocalFormatter
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.model.getDisplayName
import com.patrykandpatrick.liftapp.core.text.updateValueBy
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatrick.liftapp.core.ui.InfoCard
import com.patrykandpatrick.liftapp.core.ui.LiftAppModalBottomSheetWithTopAppBar
import com.patrykandpatrick.liftapp.core.ui.SegmentedButton
import com.patrykandpatrick.liftapp.core.ui.SegmentedButtonContainer
import com.patrykandpatrick.liftapp.core.ui.input.NumberInput
import com.patrykandpatrick.liftapp.core.ui.resource.prettyString
import com.patrykandpatrick.liftapp.domain.target.PlanDayTargets
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.feature.plan.targets.model.Action
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppChipRow
import com.patrykandpatrick.liftapp.ui.component.LiftAppFilterChip
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.ArrowBack
import com.patrykandpatrick.liftapp.ui.icons.ArrowForward
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun PlanTargetsScreen(viewModel: PlanTargetsViewModel = hiltViewModel()) {
    val loadableState = viewModel.screenState.collectAsStateWithLifecycle().value

    loadableState.Unfold { state ->
        PlanTargetsScreen(state = state, onAction = viewModel::onAction)
    }
}

@Composable
private fun PlanTargetsScreen(state: ScreenState, onAction: (Action) -> Unit) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LiftAppScaffold(
        topBar = {
            CompactTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Column {
                        CompactTopAppBarDefaults.Title(stringResource(R.string.route_plan_targets))
                        LiftAppText(
                            text =
                                stringResource(
                                    R.string.plan_targets_subtitle,
                                    state.day.routineName.orEmpty(),
                                    stringResource(
                                        R.string.plan_targets_cycle,
                                        state.day.cycleIndex + 1,
                                    ),
                                ),
                            style = typography.labelMedium,
                            color = colorScheme.foregroundVariant,
                        )
                    }
                },
                navigationIcon = {
                    CompactTopAppBarDefaults.BackIcon { onAction(Action.PopBackStack) }
                },
                actions = {
                    // Every cycle is authored, so every cycle has to be reachable — the whole point
                    // of a prescription per week is changing one before its week arrives.
                    LiftAppIconButton(
                        onClick = { onAction(Action.PreviousCycle) },
                        enabled = state.hasPreviousCycle,
                    ) {
                        Icon(
                            imageVector = LiftAppIcons.ArrowBack,
                            contentDescription =
                                stringResource(R.string.plan_targets_previous_cycle),
                        )
                    }

                    LiftAppIconButton(
                        onClick = { onAction(Action.NextCycle) },
                        enabled = state.hasNextCycle,
                    ) {
                        Icon(
                            imageVector = LiftAppIcons.ArrowForward,
                            contentDescription = stringResource(R.string.plan_targets_next_cycle),
                        )
                    }
                },
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { paddingValues ->
        if (state.day.exercises.isEmpty()) {
            EmptyDay(Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(LocalDimens.current.screen.padding),
            ) {
                items(state.day.exercises, key = { it.target.exerciseID }) { item ->
                    ExerciseTargetCard(item = item, onClick = { onAction(Action.Edit(item)) })
                }
            }
        }
    }

    state.editor?.let { editor ->
        EditorSheet(
            editor = editor,
            onAction = onAction,
        )
    }
}

@Composable
private fun EmptyDay(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(LocalDimens.current.screen.padding),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LiftAppText(
            text = stringResource(R.string.plan_targets_empty),
            style = typography.bodyMedium,
            color = colorScheme.foregroundVariant,
        )
    }
}

@Composable
private fun ExerciseTargetCard(item: PlanDayTargets.Item, onClick: () -> Unit) {
    LiftAppCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            LiftAppText(text = item.name.getDisplayName(), style = typography.titleMedium)

            LiftAppText(
                text =
                    stringResource(
                        R.string.plan_targets_sets_and_rest,
                        item.target.setCount,
                        pluralStringResource(R.plurals.set_count, item.target.setCount),
                        LocalFormatter.current.formatDuration(item.target.restTime),
                    ),
                style = typography.labelMedium,
                color = colorScheme.foregroundVariant,
            )

            // The sets, not a summary of them: a target that differs set to set is exactly what a
            // reader would be misled about by one line.
            item.target.sets.forEachIndexed { index, set ->
                LiftAppText(
                    text =
                        stringResource(
                            R.string.plan_targets_set_line,
                            stringResource(R.string.workout_set_input_title, index + 1),
                            set.prettyString(item.exerciseType, item.target.distanceUnit),
                        ),
                    style = typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun EditorSheet(editor: Editor, onAction: (Action) -> Unit) {
    LiftAppModalBottomSheetWithTopAppBar(
        onDismissRequest = { onAction(Action.DismissEditor) },
        title = { Text(text = editor.name.getDisplayName(), style = typography.titleMedium) },
    ) { dismissSheet ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier =
                Modifier.verticalScroll(rememberScrollState())
                    .padding(LocalDimens.current.screen.padding),
        ) {
            NumberInput(
                textFieldState = editor.setCount,
                onPlusClick = { editor.setCount.updateValueBy(1) },
                onMinusClick = { editor.setCount.updateValueBy(-1) },
                hint = stringResource(R.string.plan_targets_set_count),
            )

            NumberInput(
                textFieldState = editor.restSeconds,
                onPlusClick = { editor.restSeconds.updateValueBy(REST_STEP_SECONDS) },
                onMinusClick = { editor.restSeconds.updateValueBy(-REST_STEP_SECONDS) },
                hint = stringResource(R.string.plan_targets_rest_seconds),
            )

            SetSelector(editor = editor, onAction = onAction)

            val varies = AnnotatedString(stringResource(R.string.plan_targets_varies))

            NumberInput(
                textFieldState = editor.minReps,
                onPlusClick = { editor.minReps.updateValueBy(1) },
                onMinusClick = { editor.minReps.updateValueBy(-1) },
                hint = stringResource(R.string.plan_targets_min_reps),
                supportingText = varies.takeIf { editor.draft.varies(SetTarget::minReps) },
            )

            NumberInput(
                textFieldState = editor.maxReps,
                onPlusClick = { editor.maxReps.updateValueBy(1) },
                onMinusClick = { editor.maxReps.updateValueBy(-1) },
                hint = stringResource(R.string.plan_targets_max_reps),
                supportingText = varies.takeIf { editor.draft.varies(SetTarget::maxReps) },
            )

            Text(
                text = stringResource(R.string.exercise_set_input_rir),
                style = typography.labelLarge,
            )

            val rirVaries = editor.draft.varies(SetTarget::rir)
            SegmentedButtonContainer(
                items = rirOptions,
                modifier = Modifier.fillMaxWidth(),
            ) { _, rir ->
                SegmentedButton(
                    text =
                        rir?.let { rirString(it) }
                            ?: stringResource(R.string.plan_targets_rir_none),
                    onClick = { onAction(Action.SetRir(rir)) },
                    selected = !rirVaries && rir == editor.rir,
                    showIcon = false,
                )
            }

            if (rirVaries) {
                LiftAppText(
                    text = varies.text,
                    style = typography.bodySmall,
                    color = colorScheme.foregroundVariant,
                )
            }

            if (editor.hasLoadPercent) {
                LoadPercentInput(
                    editor = editor,
                    supportingText = varies.takeIf { editor.draft.varies(SetTarget::loadPercent) },
                    onAction = onAction,
                )
            }

            InfoCard(text = stringResource(R.string.plan_targets_edit_info))

            LiftAppButton(
                onClick = {
                    onAction(Action.SaveEdit)
                    dismissSheet()
                },
                enabled = editor.isValid,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }
}

@Composable
private fun SetSelector(editor: Editor, onAction: (Action) -> Unit) {
    LiftAppChipRow {
        LiftAppFilterChip(
            selected = editor.draft.selected == null,
            onClick = { onAction(Action.SelectSet(null)) },
            label = { Text(stringResource(R.string.plan_targets_all_sets)) },
        )

        repeat(editor.draft.count) { index ->
            LiftAppFilterChip(
                selected = editor.draft.selected == index,
                onClick = { onAction(Action.SelectSet(index)) },
                label = { Text(stringResource(R.string.workout_set_input_title, index + 1)) },
            )
        }
    }
}

@Composable
private fun LoadPercentInput(
    editor: Editor,
    supportingText: AnnotatedString?,
    onAction: (Action) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        NumberInput(
            textFieldState = editor.loadPercent,
            onPlusClick = { editor.loadPercent.updateValueBy(LOAD_PERCENT_STEP) },
            onMinusClick = { editor.loadPercent.updateValueBy(-LOAD_PERCENT_STEP) },
            hint = stringResource(R.string.plan_targets_load_percent),
            suffix = "%",
            supportingText = supportingText,
        )

        val suggested = editor.suggestedLoadPercent
        val reps = editor.draft.valueOf(SetTarget::minReps)
        if (suggested != null && reps != null && suggested != editor.loadPercent.value) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                LiftAppText(
                    text =
                        editor.rir?.let { rir ->
                            stringResource(
                                R.string.plan_targets_load_percent_suggestion,
                                reps,
                                pluralStringResource(R.plurals.rep_count, reps),
                                rirString(rir),
                                suggested,
                            )
                        }
                            ?: stringResource(
                                R.string.plan_targets_load_percent_suggestion_failure,
                                reps,
                                pluralStringResource(R.plurals.rep_count, reps),
                                suggested,
                            ),
                    style = typography.bodySmall,
                    color = colorScheme.foregroundVariant,
                    modifier = Modifier.weight(1f),
                )

                PlainLiftAppButton(onClick = { onAction(Action.UseLoadPercent(suggested)) }) {
                    Text(stringResource(R.string.plan_targets_use_load_percent, suggested))
                }
            }
        }
    }
}

/** Null first, because "no effort target" is a choice and not the absence of one. */
private val rirOptions: List<Int?> = listOf(null) + ExerciseSet.RIR_RANGE.toList()

private const val REST_STEP_SECONDS = 15

private const val LOAD_PERCENT_STEP = 5
