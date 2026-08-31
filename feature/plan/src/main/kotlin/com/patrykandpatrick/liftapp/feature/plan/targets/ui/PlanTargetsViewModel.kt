package com.patrykandpatrick.liftapp.feature.plan.targets.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.target.GetPlanDayTargetsContract
import com.patrykandpatrick.liftapp.domain.target.PlanDayTargets
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.target.UpsertExerciseTargetsContract
import com.patrykandpatrick.liftapp.domain.validation.validNumberHigherThanZero
import com.patrykandpatrick.liftapp.feature.plan.targets.model.Action
import com.patrykandpatrick.liftapp.navigation.data.PlanTargetsRouteData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@HiltViewModel
class PlanTargetsViewModel
@Inject
constructor(
    private val routeData: PlanTargetsRouteData,
    getDayTargets: GetPlanDayTargetsContract,
    textFieldStateManager: TextFieldStateManager,
    private val upsertTargets: UpsertExerciseTargetsContract,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    private val cycleIndex = MutableStateFlow(routeData.cycleIndex)

    /** The exercise the editor is open on, and the source of the target being written. */
    private val editedItem = MutableStateFlow<PlanDayTargets.Item?>(null)

    private val rir = MutableStateFlow<Int?>(null)

    /**
     * Made once and reset as the editor opens, rather than built per exercise. Each `intTextField`
     * without an explicit key claims a fresh generated one and writes to the saved state on every
     * keystroke, so building them per open grew that state without bound — and never read a value
     * back, because the key it was stored under changed with it.
     *
     * The vetoes are floors the steppers would otherwise walk straight through: a negative set
     * count reaches `List(-1)` in [save]. Zero is left reachable so the field can be cleared and
     * retyped, and answered by the validator instead, which is what disables Save.
     */
    private val setCount =
        textFieldStateManager.intTextField(
            savedStateKey = "plan_target_set_count",
            validators = { validNumberHigherThanZero() },
            veto = { it < 0 || it > SetTarget.setRange.endInclusive },
        )

    // Reps are not validated above zero: a run or a held position is prescribed none.
    private val minReps =
        textFieldStateManager.intTextField(
            savedStateKey = "plan_target_min_reps",
            veto = { it < 0 || it > SetTarget.repRange.endInclusive },
        )

    private val maxReps =
        textFieldStateManager.intTextField(
            savedStateKey = "plan_target_max_reps",
            veto = { it < 0 || it > SetTarget.repRange.endInclusive },
        )

    private val restSeconds =
        textFieldStateManager.intTextField(
            savedStateKey = "plan_target_rest_seconds",
            veto = { it < 0 || it > MAX_REST_SECONDS },
        )

    val screenState: StateFlow<Loadable<ScreenState>> =
        cycleIndex
            .flatMapLatest { cycle ->
                combine(
                    getDayTargets.getDayTargets(
                        planID = routeData.planID,
                        cycleIndex = cycle,
                        planItemOrderIndex = routeData.planItemOrderIndex,
                    ),
                    editedItem,
                    rir,
                ) { day, item, rirValue ->
                    ScreenState(day = day, editor = item?.let { editor(it, rirValue) })
                }
            }
            .toLoadableStateFlow(viewModelScope)

    fun onAction(action: Action) {
        when (action) {
            Action.PopBackStack -> viewModelScope.launch { navigationCommander.popBackStack() }
            Action.PreviousCycle -> cycleIndex.value = (cycleIndex.value - 1).coerceAtLeast(0)
            Action.NextCycle -> cycleIndex.value += 1
            is Action.Edit -> openEditor(action.item)
            Action.DismissEditor -> editedItem.value = null
            is Action.SetRir -> rir.value = action.rir
            Action.SaveEdit -> save()
        }
    }

    private fun openEditor(item: PlanDayTargets.Item) {
        val first = item.target.sets.firstOrNull()
        setCount.updateValue(item.target.setCount)
        minReps.updateValue(first?.minReps ?: 0)
        maxReps.updateValue(first?.maxReps ?: 0)
        restSeconds.updateValue(item.target.restTime.inWholeSeconds.toInt())
        rir.value = first?.rir
        editedItem.value = item
    }

    private fun editor(item: PlanDayTargets.Item, rir: Int?): Editor =
        Editor(
            name = item.name,
            exerciseType = item.exerciseType,
            setCount = setCount,
            minReps = minReps,
            maxReps = maxReps,
            restSeconds = restSeconds,
            rir = rir,
        )

    /**
     * Writes the exercise's values onto every one of its sets. Sets beyond the count are dropped
     * and new ones arrive prescribed the same as the rest, which is what the count control means.
     */
    private fun save() {
        val target = editedItem.value?.target ?: return
        val first = target.sets.firstOrNull()
        val set =
            SetTarget(
                minReps = minReps.value,
                maxReps = maxOf(maxReps.value, minReps.value),
                rir = rir.value,
                duration = first?.duration ?: Duration.ZERO,
                distance = first?.distance ?: 0.0,
                calories = first?.calories ?: 0.0,
            )

        viewModelScope.launch {
            upsertTargets.upsertTargets(
                listOf(
                    target.copy(
                        // Save is disabled below one, and the stepper cannot reach a negative; this
                        // is the same floor the workout's set-count buttons hold.
                        sets = List(setCount.value.coerceAtLeast(1)) { set },
                        restTime = restSeconds.value.seconds,
                    )
                )
            )
            editedItem.value = null
        }
    }

    private companion object {
        /** Ten minutes, which is past the longest rest the reference contemplates. */
        const val MAX_REST_SECONDS = 600
    }
}
