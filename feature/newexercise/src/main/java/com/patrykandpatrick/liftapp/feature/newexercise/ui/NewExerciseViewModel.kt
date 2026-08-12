package com.patrykandpatrick.liftapp.feature.newexercise.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.logging.LogPublisher
import com.patrykandpatrick.liftapp.core.logging.UiLogger
import com.patrykandpatrick.liftapp.core.viewmodel.SavedStateHandleViewModel
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.exercise.Exercise
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRole
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.exercise.GetExerciseUseCase
import com.patrykandpatrick.liftapp.domain.exercise.InsertExercisesUseCase
import com.patrykandpatrick.liftapp.domain.exercise.ProgressionPolicy
import com.patrykandpatrick.liftapp.domain.exercise.UpdateExercisesUseCase
import com.patrykandpatrick.liftapp.domain.exercise.carriesLoad
import com.patrykandpatrick.liftapp.domain.exercise.defaultProgressionPolicy
import com.patrykandpatrick.liftapp.domain.exercise.hasRole
import com.patrykandpatrick.liftapp.domain.exercise.invoke
import com.patrykandpatrick.liftapp.domain.exercise.suggestExerciseRole
import com.patrykandpatrick.liftapp.domain.extension.toggle
import com.patrykandpatrick.liftapp.domain.mapper.Mapper
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.validation.Validatable
import com.patrykandpatrick.liftapp.domain.validation.toInvalid
import com.patrykandpatrick.liftapp.domain.validation.toValid
import com.patrykandpatrick.liftapp.feature.newexercise.model.Action
import com.patrykandpatrick.liftapp.feature.newexercise.model.NewExerciseState
import com.patrykandpatrick.liftapp.navigation.data.NewExerciseRouteData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

private const val STATE_KEY = "screen_state"
private const val ROLE_CHOSEN_KEY = "role_chosen"
private const val PROGRESSION_CHOSEN_KEY = "progression_chosen"

@HiltViewModel
class NewExerciseViewModel
@Inject
constructor(
    private val newExerciseRouteData: NewExerciseRouteData,
    private val logger: UiLogger,
    private val getExercise: GetExerciseUseCase,
    override val savedStateHandle: SavedStateHandle,
    private val insertExercise: InsertExercisesUseCase,
    private val updateExercise: UpdateExercisesUseCase,
    private val exerciseToStateMapper: Mapper<Exercise, NewExerciseState>,
    private val stateToInsertExercise: Mapper<NewExerciseState.Valid, Exercise.Insert>,
    private val stateToUpdateExercise: Mapper<NewExerciseState.Valid, Exercise.Update>,
    private val navigationCommander: NavigationCommander,
) : ViewModel(), SavedStateHandleViewModel, LogPublisher by logger {
    internal val isEdit = newExerciseRouteData.exerciseID != ID_NOT_SET

    init {
        if (isEdit) loadExerciseState(newExerciseRouteData.exerciseID)
    }

    internal var state: NewExerciseState by
        saveable(STATE_KEY) { mutableStateOf(NewExerciseState.Invalid()) }

    /**
     * Until the user picks a role and a progression themselves, both follow the type and the muscle
     * lists, so filling in the rest of the form leaves the common answer already selected. Once
     * they pick, the form stops second-guessing them — unless the type stops admitting a value at
     * all, which resets the flag and lets the coercion in [withSuggestions] win.
     */
    private var roleChosenByUser: Boolean by saveable(ROLE_CHOSEN_KEY) { mutableStateOf(false) }

    private var progressionChosenByUser: Boolean by
        saveable(PROGRESSION_CHOSEN_KEY) { mutableStateOf(false) }

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateName -> updateName(action.name)
            is Action.UpdateExerciseType -> updateExerciseType(action.exerciseType)
            is Action.UpdateRole -> updateRole(action.role)
            is Action.UpdateProgressionPolicy -> updateProgressionPolicy(action.progressionPolicy)
            is Action.MainMuscleListAction -> updateMainMuscles(action.listAction)
            is Action.SecondaryMuscleListAction -> updateSecondaryMuscles(action.listAction)
            is Action.TertiaryMuscleListAction -> updateTertiaryMuscles(action.listAction)
            Action.Save -> save()
            Action.PopBackStack -> popBackStack()
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun updateName(name: String) {
        val validatableName: Validatable<Name> =
            if (name.isBlank()) {
                Name.Raw(name).toInvalid()
            } else {
                Name.Raw(name).toValid()
            }
        state = state.copyState(name = validatableName, displayName = name)
    }

    private fun updateExerciseType(type: ExerciseType) {
        // The new type may leave one of these fields with no possible answer, at which point the
        // editor stops showing it. Forget the user's pick rather than keep it behind a field they
        // can no longer see — it would still be saved. `withSuggestions` then coerces the value.
        if (type.hasRole.not()) roleChosenByUser = false
        if (type.carriesLoad.not()) progressionChosenByUser = false
        state = state.copyState(type = type).withSuggestions()
    }

    private fun updateRole(role: ExerciseRole?) {
        roleChosenByUser = true
        state = state.copyState(role = role)
    }

    private fun updateProgressionPolicy(progressionPolicy: ProgressionPolicy) {
        progressionChosenByUser = true
        state = state.copyState(progressionPolicy = progressionPolicy)
    }

    private fun updateMainMuscles(action: Action.ListAction) {
        val updatedMuscles = state.primaryMuscles.value.updatedBy(action)
        val mainMusclesValidatable =
            if (updatedMuscles.isEmpty()) {
                updatedMuscles.toInvalid()
            } else {
                updatedMuscles.toValid()
            }
        state = state.copyState(mainMuscles = mainMusclesValidatable).withSuggestions()
    }

    private fun updateSecondaryMuscles(action: Action.ListAction) {
        state =
            state
                .copyState(secondaryMuscles = state.secondaryMuscles.updatedBy(action))
                .withSuggestions()
    }

    private fun updateTertiaryMuscles(action: Action.ListAction) {
        state =
            state
                .copyState(tertiaryMuscles = state.tertiaryMuscles.updatedBy(action))
                .withSuggestions()
    }

    private fun List<Muscle>.updatedBy(action: Action.ListAction): List<Muscle> =
        when (action) {
            Action.ListAction.Clear -> emptyList()
            is Action.ListAction.ToggleMuscle -> toggle(action.muscle)
        }

    private fun NewExerciseState.withSuggestions(): NewExerciseState =
        copyState(
            role = if (roleChosenByUser) role else suggestExerciseRole(type, muscleCount),
            progressionPolicy =
                if (progressionChosenByUser) progressionPolicy else defaultProgressionPolicy(type),
        )

    private val NewExerciseState.muscleCount: Int
        get() = primaryMuscles.value.size + secondaryMuscles.size + tertiaryMuscles.size

    private fun save() {
        when (val state = state) {
            is NewExerciseState.Valid -> {
                insertOrUpdateExercise(state)
                popBackStack()
            }

            is NewExerciseState.Invalid -> {
                this.state = state.copy(showErrors = true)
            }
        }
    }

    private fun insertOrUpdateExercise(state: NewExerciseState.Valid) {
        viewModelScope.launch {
            if (newExerciseRouteData.exerciseID != ID_NOT_SET) {
                updateExercise(stateToUpdateExercise(state))
            } else {
                insertExercise(stateToInsertExercise(state))
            }
        }
    }

    private fun loadExerciseState(exerciseId: Long) {
        viewModelScope.launch {
            getExercise(exerciseId)
                .firstOrNull()
                ?.let { exerciseToStateMapper(it) }
                ?.also { existingExerciseState ->
                    // A stored role is the user's answer. A null one never was, so an exercise that
                    // predates the field opens with the suggestion already filled in — visible, and
                    // still theirs to reject before saving.
                    val type = existingExerciseState.type
                    roleChosenByUser = type.hasRole && existingExerciseState.role != null
                    // Not "chosen" where the type admits no choice: a row that took the column's
                    // SQL default without ever passing through this screen — every exercise a user
                    // created before the column existed — is repaired by opening and saving it.
                    progressionChosenByUser = type.carriesLoad
                    state = existingExerciseState.withSuggestions()
                }
        }
    }
}
