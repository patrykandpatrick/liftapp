package com.patrykandpatrick.liftapp.feature.routine.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.serialization.saved
import com.patrykandpatrick.liftapp.core.extension.update
import com.patrykandpatrick.liftapp.domain.extension.moved
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject

/** Holds the exercises of the superset being edited until the changes are saved or discarded. */
@ViewModelScoped
class SupersetSavedState @Inject constructor(private val savedStateHandle: SavedStateHandle) {

    var isInitialized by savedStateHandle.saved { false }

    val exerciseIDs = savedStateHandle.getStateFlow(EXERCISE_IDS, emptyList<Long>())

    fun setExerciseIDs(exerciseIDs: List<Long>) {
        savedStateHandle[EXERCISE_IDS] = exerciseIDs
    }

    fun addExerciseIDs(addedExerciseIDs: List<Long>) {
        updateExerciseIDs { exerciseIDs -> (exerciseIDs + addedExerciseIDs).distinct() }
    }

    fun removeExerciseID(exerciseID: Long) {
        updateExerciseIDs { exerciseIDs -> exerciseIDs - exerciseID }
    }

    fun reorderExerciseIDs(fromIndex: Int, toIndex: Int) {
        updateExerciseIDs { exerciseIDs -> exerciseIDs.moved(fromIndex, toIndex) }
    }

    private fun updateExerciseIDs(transform: (List<Long>) -> List<Long>) {
        savedStateHandle.update<List<Long>>(EXERCISE_IDS) { transform(it.orEmpty()) }
    }

    private companion object {
        const val EXERCISE_IDS = "superset_exercise_ids"
    }
}
