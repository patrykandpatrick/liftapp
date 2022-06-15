package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.lifecycle.ViewModel
import com.patrykandpatryk.liftapp.domain.exercise.GetAllExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    getAllExercises: GetAllExercisesUseCase,
) : ViewModel() {

    val exercises = getAllExercises()
}
