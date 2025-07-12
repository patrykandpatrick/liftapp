package com.patrykandpatryk.liftapp.feature.newroutine.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.serialization.saved
import com.patrykandpatryk.liftapp.core.extension.update
import dagger.hilt.android.scopes.ViewModelScoped
import jakarta.inject.Inject

@ViewModelScoped
class NewRoutineSavedState @Inject constructor(private val savedStateHandle: SavedStateHandle) {

    var isInitialized by savedStateHandle.saved { false }

    val exerciseIDs = savedStateHandle.getStateFlow(EXERCISE_IDS, emptyList<Long>())

    fun addExerciseIDs(exerciseIDs: List<Long>) {
        savedStateHandle.update<List<Long>>(EXERCISE_IDS) { (it ?: emptyList()) + exerciseIDs }
    }

    fun removeExerciseIDs(exerciseID: Long) {
        savedStateHandle.update<List<Long>>(EXERCISE_IDS) {
            (it ?: return@update null) - exerciseID
        }
    }

    fun reorderExerciseIDs(from: Int, to: Int) {
        savedStateHandle.update<List<Long>>(EXERCISE_IDS) { exerciseIDs ->
            val indexRange = 0 until (exerciseIDs?.size ?: return)
            if (from in indexRange && to in indexRange) {
                exerciseIDs.toMutableList().apply {
                    val item = removeAt(from)
                    add(to, item)
                }
            } else {
                return
            }
        }
    }

    private companion object {
        const val EXERCISE_IDS = "exercise_ids"
    }
}
