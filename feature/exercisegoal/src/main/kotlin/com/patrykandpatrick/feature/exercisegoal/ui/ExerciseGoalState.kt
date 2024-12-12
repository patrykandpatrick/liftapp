package com.patrykandpatrick.feature.exercisegoal.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import com.patrykandpatrick.opto.domain.Preference
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.GetExerciseUseCase
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.goal.GetGoalUseCase
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.goal.SaveGoalUseCase
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.validation.isHigherOrEqualTo
import com.patrykandpatryk.liftapp.domain.validation.validNumberHigherThanZero
import com.patrykandpatryk.liftapp.domain.validation.valueInRange
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
class ExerciseGoalState(
    exercise: Flow<Exercise?>,
    private val goal: Flow<Goal>,
    getFormattedDuration: suspend (Duration) -> String,
    private val saveGoal: suspend (Goal) -> Unit,
    private val savedStateHandle: SavedStateHandle,
    private val textFieldStateManager: TextFieldStateManager,
    private val goalInfoVisiblePreference: Preference<Boolean>,
    private val coroutineScope: CoroutineScope,
) {
    private val _goalSaved = MutableStateFlow(false)

    val goalSaved: StateFlow<Boolean> = _goalSaved

    val goalInfoVisible: StateFlow<Boolean?> =
        goalInfoVisiblePreference
            .get()
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), null)

    val exerciseName: StateFlow<String> =
        exercise
            .filterNotNull()
            .map { it.displayName }
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), "")

    val minReps =
        textFieldStateManager.intTextField(
            validators = {
                validNumberHigherThanZero()
                valueInRange(Goal.RepRange)
            }
        )

    val maxReps =
        textFieldStateManager.intTextField(
            validators = {
                validNumberHigherThanZero()
                valueInRange(Goal.RepRange)
                isHigherOrEqualTo(minReps)
            }
        )

    val sets =
        textFieldStateManager.intTextField(
            validators = {
                validNumberHigherThanZero()
                valueInRange(Goal.SetRange)
            }
        )

    val restTime: StateFlow<Duration> =
        savedStateHandle
            .getStateFlow(REST_TIME_KEY, 0L)
            .map { time -> time.milliseconds }
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), Duration.ZERO)

    val formattedRestTime: StateFlow<String> =
        restTime
            .map { restTime ->
                if (restTime.inWholeMilliseconds == 0L) "" else getFormattedDuration(restTime)
            }
            .stateIn(coroutineScope, SharingStarted.WhileSubscribed(), "")

    @AssistedInject
    constructor(
        @Assisted("routineID") routineID: Long,
        @Assisted("exerciseID") exerciseID: Long,
        @Assisted coroutineScope: CoroutineScope,
        getExercise: GetExerciseUseCase,
        getGoal: GetGoalUseCase,
        saveGoal: SaveGoalUseCase,
        formatter: Formatter,
        savedStateHandle: SavedStateHandle,
        textFieldStateManager: TextFieldStateManager,
        preferenceRepository: PreferenceRepository,
    ) : this(
        exercise = getExercise(exerciseID),
        goal = getGoal(routineID, exerciseID),
        saveGoal = { saveGoal(routineID, exerciseID, it) },
        getFormattedDuration = {
            formatter.getFormattedDuration(it, Formatter.DateFormat.MinutesSeconds)
        },
        savedStateHandle = savedStateHandle,
        textFieldStateManager = textFieldStateManager,
        goalInfoVisiblePreference = preferenceRepository.goalInfoVisible,
        coroutineScope = coroutineScope,
    )

    init {
        loadGoalData()
    }

    private fun loadGoalData() {
        coroutineScope.launch {
            val goal = goal.first()
            minReps.updateValue(goal.minReps)
            maxReps.updateValue(goal.maxReps)
            sets.updateValue(goal.sets)
            setRestTime(goal.breakDuration)
        }
    }

    fun toggleGoalInfoVisible() {
        coroutineScope.launch {
            goalInfoVisiblePreference.set(goalInfoVisible.value?.not() != false)
        }
    }

    fun setRestTime(duration: Duration) {
        savedStateHandle[REST_TIME_KEY] = duration.inWholeMilliseconds
    }

    fun save() {
        if (textFieldStateManager.hasErrors()) return
        coroutineScope.launch {
            saveGoal(
                Goal(
                    minReps = minReps.value,
                    maxReps = maxReps.value,
                    sets = sets.value,
                    breakDuration = restTime.value,
                )
            )
            _goalSaved.value = true
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("routineID") routineID: Long,
            @Assisted("exerciseID") exerciseID: Long,
            coroutineScope: CoroutineScope,
        ): ExerciseGoalState
    }

    companion object {
        private const val REST_TIME_KEY = "rest_time"
    }
}
