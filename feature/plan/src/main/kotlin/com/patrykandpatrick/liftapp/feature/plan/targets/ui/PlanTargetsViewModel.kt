package com.patrykandpatrick.liftapp.feature.plan.targets.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.target.ExerciseTarget
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
import kotlinx.coroutines.launch

@HiltViewModel
class PlanTargetsViewModel
@Inject
constructor(
    routeData: PlanTargetsRouteData,
    getDayTargets: GetPlanDayTargetsContract,
    private val textFieldStateManager: TextFieldStateManager,
    private val upsertTargets: UpsertExerciseTargetsContract,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    private val editor = MutableStateFlow<Editor?>(null)

    /** The target the open editor is editing, held apart from it because [Editor] is view state. */
    private var editedTarget: ExerciseTarget? = null

    val screenState: StateFlow<Loadable<ScreenState>> =
        combine(
                getDayTargets.getDayTargets(
                    planID = routeData.planID,
                    cycleIndex = routeData.cycleIndex,
                    planItemOrderIndex = routeData.planItemOrderIndex,
                ),
                editor,
                ::ScreenState,
            )
            .toLoadableStateFlow(viewModelScope)

    fun onAction(action: Action) {
        when (action) {
            Action.PopBackStack -> viewModelScope.launch { navigationCommander.popBackStack() }
            is Action.Edit -> openEditor(action.item)
            Action.DismissEditor -> closeEditor()
            is Action.SetRir -> editor.value = editor.value?.withRir(action.rir)
            Action.SaveEdit -> save()
        }
    }

    private fun openEditor(item: PlanDayTargets.Item) {
        val first = item.target.sets.firstOrNull()
        editedTarget = item.target
        editor.value =
            Editor(
                name = item.name,
                exerciseType = item.exerciseType,
                setCount =
                    textFieldStateManager.intTextField(
                        initialValue = item.target.setCount.toString(),
                        validators = { validNumberHigherThanZero() },
                        veto = { it > SetTarget.setRange.endInclusive },
                    ),
                minReps =
                    textFieldStateManager.intTextField(
                        initialValue = (first?.minReps ?: 0).toString(),
                        veto = { it > SetTarget.repRange.endInclusive },
                    ),
                maxReps =
                    textFieldStateManager.intTextField(
                        initialValue = (first?.maxReps ?: 0).toString(),
                        veto = { it > SetTarget.repRange.endInclusive },
                    ),
                restSeconds =
                    textFieldStateManager.intTextField(
                        initialValue = item.target.restTime.inWholeSeconds.toString(),
                        veto = { it > MAX_REST_SECONDS },
                    ),
                rir = first?.rir,
            )
    }

    private fun closeEditor() {
        editedTarget = null
        editor.value = null
    }

    /**
     * Writes the exercise's values onto every one of its sets. Sets beyond the count are dropped
     * and new ones arrive prescribed the same as the rest, which is what the count control means.
     */
    private fun save() {
        val open = editor.value ?: return
        val target = editedTarget ?: return
        val set =
            SetTarget(
                minReps = open.minReps.value,
                maxReps = maxOf(open.maxReps.value, open.minReps.value),
                rir = open.rir,
                duration = target.sets.firstOrNull()?.duration ?: Duration.ZERO,
                distance = target.sets.firstOrNull()?.distance ?: 0.0,
                calories = target.sets.firstOrNull()?.calories ?: 0.0,
            )

        viewModelScope.launch {
            upsertTargets.upsertTargets(
                listOf(
                    target.copy(
                        sets = List(open.setCount.value) { set },
                        restTime = open.restSeconds.value.seconds,
                    )
                )
            )
            closeEditor()
        }
    }

    private fun Editor.withRir(rir: Int?): Editor =
        Editor(
            name = name,
            exerciseType = exerciseType,
            setCount = setCount,
            minReps = minReps,
            maxReps = maxReps,
            restSeconds = restSeconds,
            rir = rir,
        )

    private companion object {
        /** Ten minutes, which is past the longest rest the reference contemplates. */
        const val MAX_REST_SECONDS = 600
    }
}
