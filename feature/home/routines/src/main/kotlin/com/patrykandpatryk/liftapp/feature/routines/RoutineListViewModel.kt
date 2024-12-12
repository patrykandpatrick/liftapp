package com.patrykandpatryk.liftapp.feature.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class RoutineListViewModel @Inject constructor(routineListDataSource: RoutineListDataSource) :
    ViewModel(), RoutineListState {
    override val routines: StateFlow<ImmutableList<RoutineItem>> =
        routineListDataSource.routineItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = persistentListOf(),
        )
}
