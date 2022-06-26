package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.feature.exercise.model.GroupBy
import com.patrykandpatryk.liftapp.feature.exercise.usecase.GetExercisesItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    getExercisesItems: GetExercisesItemsUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow(value = "")
    val query = _query.asStateFlow()

    private val _groupBy = MutableStateFlow(value = GroupBy.Name)
    val groupBy = _groupBy.asStateFlow()

    val exercises = getExercisesItems(query = _query, groupBy = _groupBy)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun setQuery(query: String) {
        _query.value = query
    }

    fun setGroupBy(groupBy: GroupBy) {
        _groupBy.value = groupBy
    }
}
