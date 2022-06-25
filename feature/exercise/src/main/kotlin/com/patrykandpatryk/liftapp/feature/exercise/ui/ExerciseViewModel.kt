package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.feature.exercise.usecase.GetExercisesItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    getExercises: GetExercisesItemsUseCase,
) : ViewModel() {

    val query = MutableStateFlow("")

    val exercises = getExercises(query)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}
