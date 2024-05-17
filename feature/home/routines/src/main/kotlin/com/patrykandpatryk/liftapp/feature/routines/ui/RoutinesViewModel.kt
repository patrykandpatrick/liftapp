package com.patrykandpatryk.liftapp.feature.routines.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.routine.GetRoutinesWithExerciseNamesUseCase
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseNames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RoutinesViewModel @Inject constructor(
    getRoutinesWithExerciseNames: GetRoutinesWithExerciseNamesUseCase,
) : ViewModel() {

    val routines: StateFlow<List<RoutineWithExerciseNames>> =
        getRoutinesWithExerciseNames()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList(),
            )
}
