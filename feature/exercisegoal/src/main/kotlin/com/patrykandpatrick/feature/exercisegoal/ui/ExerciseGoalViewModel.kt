package com.patrykandpatrick.feature.exercisegoal.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.feature.exercisegoal.model.GetExerciseNameUseCase
import com.patrykandpatrick.feature.exercisegoal.model.GetGoalUseCase
import com.patrykandpatrick.feature.exercisegoal.model.SaveGoalUseCase
import com.patrykandpatrick.opto.domain.Preference
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.di.PreferenceQualifier
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.validation.isHigherOrEqualTo
import com.patrykandpatryk.liftapp.domain.validation.validNumberHigherThanZero
import com.patrykandpatryk.liftapp.domain.validation.valueInRange
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
@HiltViewModel
class ExerciseGoalViewModel
@Inject
constructor(
    coroutineScope: CoroutineScope,
    getExerciseNameUseCase: GetExerciseNameUseCase,
    private val getGoalUseCase: GetGoalUseCase,
    private val saveGoalUseCase: SaveGoalUseCase,
    private val formatter: Formatter,
    private val savedStateHandle: SavedStateHandle,
    private val textFieldStateManager: TextFieldStateManager,
    @PreferenceQualifier.GoalInfoVisible private val infoVisiblePreference: Preference<Boolean>,
) : ViewModel(coroutineScope) {
    private val _goalSaved = MutableStateFlow(false)

    private var goalID = ID_NOT_SET

    val goalSaved: StateFlow<Boolean> = _goalSaved

    val goalInfoVisible: StateFlow<Boolean?> =
        infoVisiblePreference.get().stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val exerciseName: StateFlow<Name> =
        getExerciseNameUseCase()
            .filterNotNull()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Name.Empty)

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
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Duration.ZERO)

    val formattedRestTime: StateFlow<String> =
        restTime
            .map { restTime ->
                if (restTime.inWholeMilliseconds == 0L) ""
                else formatter.getFormattedDuration(restTime, Formatter.DateFormat.MinutesSeconds)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    init {
        loadGoalData()
    }

    private fun loadGoalData() {
        viewModelScope.launch {
            val goal = getGoalUseCase().first()
            goalID = goal.id
            minReps.updateValue(goal.minReps)
            maxReps.updateValue(goal.maxReps)
            sets.updateValue(goal.sets)
            setRestTime(goal.breakDuration)
        }
    }

    fun toggleGoalInfoVisible() {
        viewModelScope.launch {
            goalInfoVisible
            infoVisiblePreference.set(goalInfoVisible.value?.not() != false)
        }
    }

    fun setRestTime(duration: Duration) {
        savedStateHandle[REST_TIME_KEY] = duration.inWholeMilliseconds
    }

    fun save() {
        if (textFieldStateManager.hasErrors()) return
        viewModelScope.launch {
            saveGoalUseCase(
                Goal(
                    id = goalID,
                    minReps = minReps.value,
                    maxReps = maxReps.value,
                    sets = sets.value,
                    breakDuration = restTime.value,
                )
            )
            _goalSaved.value = true
        }
    }

    companion object {
        private const val REST_TIME_KEY = "rest_time"
    }
}
