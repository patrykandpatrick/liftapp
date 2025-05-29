package com.patrykandpatrick.liftapp.plan.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.liftapp.plan.model.Action
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.plan.Plan
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import java.time.LocalDate

@Composable
fun PlanScreen(modifier: Modifier = Modifier, padding: PaddingValues = PaddingValues(0.dp)) {
    val viewModel: PlanViewModel = hiltViewModel()
    val state = viewModel.state.collectAsState().value

    PlanScreen(state, viewModel::onAction, modifier.padding(padding))
}

@Composable
private fun PlanScreen(
    state: Loadable<PlanState>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (isEditVisible, setEditVisible) = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CompactTopAppBar(
                title = {
                    CompactTopAppBarDefaults.Title(stringResource(R.string.route_active_plan_full))
                },
                actions = {
                    state.Unfold { state ->
                        if (state is PlanState.ActivePlan) {
                            CompactTopAppBarDefaults.IconButton(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = stringResource(R.string.action_edit),
                            ) {
                                setEditVisible(true)
                            }
                        }
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(0.dp),
    ) { paddingValues ->
        state.Unfold(modifier = Modifier.padding(paddingValues).fillMaxSize()) { planState ->
            when (planState) {
                is PlanState.ActivePlan -> ActivePlanScreen(planState, onAction)
                PlanState.NoActivePlan -> NoActivePlanScreen(onAction)
            }
        }
    }

    if (isEditVisible) {
        EditBottomSheet(onDismissRequest = { setEditVisible(false) }, onAction = onAction)
    }
}

internal val previewActivePlanState: PlanState.ActivePlan
    get() =
        PlanState.ActivePlan(
            plan =
                Plan(
                    id = 1,
                    name = "Full Body Workout",
                    description = "",
                    items =
                        listOf(
                            Plan.Item.Routine(
                                RoutineWithExercises(
                                    id = 1,
                                    name = "Push",
                                    exercises = exercisesPreview,
                                    primaryMuscles = emptyList(),
                                    secondaryMuscles = emptyList(),
                                    tertiaryMuscles = emptyList(),
                                )
                            ),
                            Plan.Item.Rest,
                            Plan.Item.Routine(
                                RoutineWithExercises(
                                    id = 2,
                                    name = "Pull",
                                    exercises = exercisesPreview,
                                    primaryMuscles = emptyList(),
                                    secondaryMuscles = emptyList(),
                                    tertiaryMuscles = emptyList(),
                                )
                            ),
                            Plan.Item.Rest,
                            Plan.Item.Routine(
                                RoutineWithExercises(
                                    id = 3,
                                    name = "Legs",
                                    exercises = exercisesPreview,
                                    primaryMuscles = emptyList(),
                                    secondaryMuscles = emptyList(),
                                    tertiaryMuscles = emptyList(),
                                )
                            ),
                            Plan.Item.Rest,
                        ),
                ),
            cycleNumber = 1,
            cycleCount = 6,
            currentPlanItemIndex = 2,
            cycleDates = PlanState.getAllCycleDates(LocalDate.now().minusDays(7), 6, 6L),
        )

private val exercisesPreview =
    listOf(
        RoutineExerciseItem(0L, "Bench Press", "Chest", ExerciseType.Weight, Goal.default),
        RoutineExerciseItem(1L, "Squat", "Legs", ExerciseType.Weight, Goal.default),
        RoutineExerciseItem(2L, "Deadlift", "Back", ExerciseType.Weight, Goal.default),
    )

@MultiDevicePreview
@Composable
private fun PlanScreenPreview() {
    LiftAppTheme { PlanScreen(state = Loadable.Success(previewActivePlanState), onAction = {}) }
}

@MultiDevicePreview
@Composable
private fun NoActivePlanScreenPreview() {
    LiftAppTheme { PlanScreen(Loadable.Success(PlanState.NoActivePlan), {}) }
}
