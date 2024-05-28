package com.patrykandpatryk.liftapp.feature.routines

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.StateFlow

@Stable
interface RoutineListState {
    val routines: StateFlow<ImmutableList<RoutineItem>>
}
