package com.patrykandpatrick.feature.exercisegoal.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.feature.exercisegoal.model.GetExerciseNameAndTypeUseCase
import com.patrykandpatrick.feature.exercisegoal.model.GetGoalUseCase
import com.patrykandpatrick.feature.exercisegoal.model.GoalInput
import com.patrykandpatrick.feature.exercisegoal.model.SaveGoalUseCase
import com.patrykandpatrick.feature.exercisegoal.model.State
import com.patrykandpatrick.opto.domain.Preference
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.di.PreferenceQualifier
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
@HiltViewModel
class ExerciseGoalViewModel
@Inject
constructor(
    coroutineScope: CoroutineScope,
    getExerciseNameAndTypeUseCase: GetExerciseNameAndTypeUseCase,
    getGoalUseCase: GetGoalUseCase,
    private val saveGoalUseCase: SaveGoalUseCase,
    private val textFieldStateManager: TextFieldStateManager,
    @PreferenceQualifier.GoalInfoVisible private val infoVisiblePreference: Preference<Boolean>,
    @PreferenceQualifier.LongDistanceUnit
    private val longDistanceUnitPreference: Preference<LongDistanceUnit>,
) : ViewModel(coroutineScope) {
    private val goalSaved = MutableStateFlow(false)

    val state =
        combine(
                getExerciseNameAndTypeUseCase(),
                getGoalUseCase(),
                infoVisiblePreference.get(),
                longDistanceUnitPreference.get(),
                goalSaved,
            ) { exercise, goal, infoVisible, distanceUnit, goalSaved ->
                exercise?.let { (name, type) ->
                    State(
                        goalID = goal.id,
                        exerciseName = name,
                        input = GoalInput.create(textFieldStateManager, goal, type, distanceUnit),
                        goalInfoVisible = infoVisible,
                        goalSaved = goalSaved,
                    )
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun setGoalInfoVisible(visible: Boolean) {
        viewModelScope.launch { infoVisiblePreference.set(visible) }
    }

    fun save(state: State) {
        if (textFieldStateManager.hasErrors()) return
        viewModelScope.launch {
            val input = state.input
            saveGoalUseCase(
                Goal(
                    id = state.goalID,
                    minReps = input.minReps?.state?.value ?: 0,
                    maxReps = input.maxReps?.state?.value ?: 0,
                    sets = input.sets?.state?.value ?: 0,
                    restTime = input.restTime.state.value.milliseconds,
                    duration = input.duration?.state?.value?.milliseconds ?: 0.milliseconds,
                    distance = input.distance?.state?.value ?: 0.0,
                    distanceUnit = input.distance?.unit ?: LongDistanceUnit.Kilometer,
                    calories = input.calories?.state?.value ?: 0.0,
                )
            )
            goalSaved.value = true
        }
    }
}
