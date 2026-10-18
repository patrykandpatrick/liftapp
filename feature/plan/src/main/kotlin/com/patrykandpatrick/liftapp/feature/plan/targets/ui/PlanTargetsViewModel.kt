package com.patrykandpatrick.liftapp.feature.plan.targets.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.core.text.IntTextFieldState
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.target.GetPlanDayTargetsContract
import com.patrykandpatrick.liftapp.domain.target.PlanDayTargets
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.target.UpsertExerciseTargetsContract
import com.patrykandpatrick.liftapp.domain.validation.validNumberHigherThanZero
import com.patrykandpatrick.liftapp.feature.plan.targets.model.Action
import com.patrykandpatrick.liftapp.feature.plan.targets.model.TargetDraft
import com.patrykandpatrick.liftapp.navigation.data.PlanTargetsRouteData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PlanTargetsViewModel
@Inject
constructor(
    private val routeData: PlanTargetsRouteData,
    getDayTargets: GetPlanDayTargetsContract,
    textFieldStateManager: TextFieldStateManager,
    coroutineScope: CoroutineScope,
    private val upsertTargets: UpsertExerciseTargetsContract,
    private val navigationCommander: NavigationCommander,
) : ViewModel(coroutineScope) {

    private val cycleIndex = MutableStateFlow(routeData.cycleIndex)

    /** The exercise the editor is open on, and the source of the target being written. */
    private val editedItem = MutableStateFlow<PlanDayTargets.Item?>(null)

    private val draft = MutableStateFlow<TargetDraft?>(null)

    /**
     * Set while the fields are filled from [draft]. A text field reports an update made in code the
     * same way as a typed one, and filling the fields for every set at once would otherwise level
     * the sets to whatever the first one held.
     */
    private var isFillingFields = false

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
            onValueChange = { count -> count?.let(::resize) },
            veto = { it < 0 || it > SetTarget.setRange.endInclusive },
        )

    // Reps are not validated above zero: a run or a held position is prescribed none.
    private val minReps =
        textFieldStateManager.intTextField(
            savedStateKey = "plan_target_min_reps",
            onValueChange = { reps -> editSets { it.copy(minReps = reps ?: 0) } },
            veto = { it < 0 || it > SetTarget.repRange.endInclusive },
        )

    private val maxReps =
        textFieldStateManager.intTextField(
            savedStateKey = "plan_target_max_reps",
            onValueChange = { reps -> editSets { it.copy(maxReps = reps ?: 0) } },
            veto = { it < 0 || it > SetTarget.repRange.endInclusive },
        )

    // Empty, or zero, leaves the load open.
    private val loadPercent =
        textFieldStateManager.intTextField(
            savedStateKey = "plan_target_load_percent",
            onValueChange = { percent ->
                editSets { it.copy(loadPercent = percent?.takeIf { it > 0 }?.toDouble()) }
            },
            veto = { it < 0 || it > MAX_LOAD_PERCENT },
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
                    draft,
                ) { day, item, draft ->
                    ScreenState(
                        day = day,
                        editor = if (item != null && draft != null) editor(item, draft) else null,
                    )
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
            is Action.SelectSet -> {
                draft.update { it?.select(action.index) }
                fillFields()
            }
            is Action.SetRir -> editSets { it.copy(rir = action.rir) }
            is Action.UseLoadPercent -> loadPercent.updateValue(action.percent)
            Action.SaveEdit -> save()
        }
    }

    private fun openEditor(item: PlanDayTargets.Item) {
        draft.value = TargetDraft.of(item.target.sets)
        restSeconds.updateValue(item.target.restTime.inWholeSeconds.toInt())
        fillFields()
        editedItem.value = item
    }

    private fun editSets(transform: (SetTarget) -> SetTarget) {
        if (isFillingFields) return
        draft.update { it?.edit(transform) }
    }

    /** A count can drop the selected set out of range, which selects them all and so refills. */
    private fun resize(count: Int) {
        if (isFillingFields) return
        val selected = draft.value?.selected
        draft.update { it?.withCount(count) }
        if (draft.value?.selected != selected) fillFields()
    }

    /** A field every set disagrees on is left empty, and typing into it levels them. */
    private fun fillFields() {
        val draft = draft.value ?: return
        isFillingFields = true
        setCount.updateValue(draft.count)
        minReps.fill(draft.valueOf(SetTarget::minReps))
        maxReps.fill(draft.valueOf(SetTarget::maxReps))
        loadPercent.fill(draft.valueOf(SetTarget::loadPercent)?.roundToInt())
        isFillingFields = false
    }

    private fun IntTextFieldState.fill(value: Int?) {
        if (value == null) clear() else updateValue(value)
    }

    private fun editor(item: PlanDayTargets.Item, draft: TargetDraft): Editor =
        Editor(
            name = item.name,
            exerciseType = item.exerciseType,
            draft = draft,
            setCount = setCount,
            minReps = minReps,
            maxReps = maxReps,
            loadPercent = loadPercent,
            restSeconds = restSeconds,
        )

    private fun save() {
        val target = editedItem.value?.target ?: return
        val sets = draft.value?.toSets() ?: return

        viewModelScope.launch {
            upsertTargets.upsertTargets(
                listOf(target.copy(sets = sets, restTime = restSeconds.value.seconds))
            )
            editedItem.value = null
        }
    }

    private companion object {
        /** Ten minutes, which is past the longest rest the reference contemplates. */
        const val MAX_REST_SECONDS = 600

        const val MAX_LOAD_PERCENT = 100
    }
}
