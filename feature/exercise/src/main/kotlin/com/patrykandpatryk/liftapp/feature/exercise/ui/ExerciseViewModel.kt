package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.exercise.GetAllExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    getAllExercises: GetAllExercisesUseCase,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    val groupedExercises = getAllExercises()
        .map { exercises ->
            exercises
                .groupBy { exercise -> exercise.name[0].toString() }
                .toSortedMap()
        }
        .flowOn(dispatcher)
        .stateIn(viewModelScope, SharingStarted.Eagerly, sortedMapOf())
}
